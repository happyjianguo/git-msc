package com.shyl.msc.webService.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.shyl.common.cache.lock.IBaseLock;
import com.shyl.common.cache.lock.LockCollection;
import com.shyl.msc.b2b.stock.entity.HisStockDay;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.stock.service.IHisStockDayService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IHospitalStockWebService;
import com.shyl.sys.dto.Message;

/**
 * 医院库存
 * @author a_Q
 *
 */
@WebService(serviceName="hospitalStockWebService",portName="hospitalStockPort", targetNamespace="http://webservice.msc.shyl.com/")
public class HospitalStockWebService extends BaseWebService implements IHospitalStockWebService {

	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IProductService productService;
	@Resource
	private IHisStockDayService hisStockDayService;
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private LockCollection lockCollection;
	/**
	 * 医院库存上传 
	 */
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.hospitalStock_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			String hospitalCode = converData.getString("yybm");//医院编码
			String kcrq = converData.getString("kcrq");//库存日期
			IBaseLock baseLock = lockCollection.getLock(HisStockDay.class, hospitalCode+"-"+kcrq);
			baseLock.lock();
			try{
				message = checkData(converData);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) message.getData();
				Hospital hospital = (Hospital) map.get("hospital");
				//第三步验证签名
				message = checkSign(hospital.getIocode(), data, sign);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				String ptdm = converData.getString("ptdm");	//平台代码
				//第四步新增报文信息
				message = savaDatagrame(ptdm, hospital.getCode(), hospital.getFullName(), data, dataType, DatagramType.hospitalStock_send);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第五步执行主逻辑
				message = sendMethod(converData,Long.valueOf(message.getData().toString()), hospital);
			}finally {
				baseLock.unlock();
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message sendMethod(JSONObject jObject,Long datagramId, Hospital hospital) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String mx = jObject.getString("mx"); //明细
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			String kcrq = jObject.getString("kcrq");//库存日期
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			hisStockDayService.saveHisStock(ptdm, list, kcrq, hospital, datagramId, ptdm);
			message.setSuccess(true);
			message.setMsg("返回成功");
			message.setData("");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message checkData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			if(CommonProperties.IS_TO_SZ_PROJECT.equals("0")){
				if(StringUtils.isEmpty(ptdm)){
					message.setMsg("平台代码不能为空");
					return message;
				}
				List<String> projectList = new ArrayList<>(Arrays.asList(CommonProperties.DB_PROJECTCODE.split(",")));
				if(!projectList.contains(ptdm)){
					message.setMsg("平台代码有误");
					return message;
				}
			}
			String yybm = jObject.getString("yybm");//医院编码
			if(StringUtils.isEmpty(yybm)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			hospital = hospitalService.findByCode(ptdm, yybm);
			if(hospital == null){
				message.setMsg("医院编码有误");
				return message;
			}
			String kcrq = jObject.getString("kcrq");//库存日期
			if(StringUtils.isEmpty(kcrq)){
				message.setMsg("库存日期为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(kcrq, 1)){
				message.setMsg("库存日期格式有误");
				return message;
			}
			String jls = jObject.getString("jls");	//记录数
			if(StringUtils.isEmpty(jls)){
				message.setMsg("记录数不能为空");
				return message;
			}
			try {
				jObject.getIntValue("jls");//记录数
			} catch (Exception e) {
				message.setMsg("记录数格式有误");
				return message;
			}
			String mx = jObject.getString("mx"); //明细
			if(StringUtils.isEmpty(mx)){
				message.setMsg("库存明细不能为空");
				return message;
			}
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = new ArrayList<>();
			try {
				list = JSON.parseArray(mx, JSONObject.class);
				if (list != null && list.size() == 0) {
					message.setMsg("明细数据不能为空");
					return message;
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("库存明细数据格式有误");
				return message;
			}
			for(JSONObject jo:list){
				int sxh = 1;
				String sxhString = jo.getString("sxh");		//顺序号
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = jo.getIntValue("sxh");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				
				String ypbm = jo.getString("ypbm"); //药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码不能为空");
					return message;
				}
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					message.setMsg("第("+sxh+")笔：药品编码有误");
					return message;
				}
				Goods goods = goodsService.getByProductCodeAndHosiptal(ptdm, ypbm, hospital.getCode());
				if(goods == null){
					message.setMsg("第("+sxh+")笔：药品编码为("+ypbm+")的药品不在医院药品目录中");
					return message;
				}
				String qckcsl = jo.getString("qckcsl"); //期初库存数量
				if(!StringUtils.isEmpty(qckcsl)){
					try {
						jo.getBigDecimal("qckcsl");
					} catch (Exception e) {
						e.printStackTrace();
						message.setMsg("第("+sxh+")笔：期初库存数量格式有误");
						return message;
					}
				}
				String qckcje = jo.getString("qckcje"); //期初库存金额
				if(!StringUtils.isEmpty(qckcje)){
					try {
						jo.getDoubleValue("qckcje");
					} catch (Exception e) {
						e.printStackTrace();
						message.setMsg("第("+sxh+")笔：期初库存金额格式有误");
						return message;
					}
				}
				String qmkcsl = jo.getString("qmkcsl"); //期末库存数量
				if(StringUtils.isEmpty(qmkcsl)){
					message.setMsg("第("+sxh+")笔：期末库存数量不能为空");
					return message;
				}
				try {
					jo.getBigDecimal("qmkcsl");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：期末库存数量格式有误");
					return message;
				}
				
				String qmkcje = jo.getString("qmkcje"); //期末库存金额
				if(StringUtils.isEmpty(qmkcje)){
					message.setMsg("第("+sxh+")笔：期末库存金额不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("qmkcje");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：期末库存金额格式有误");
					return message;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("hospital", hospital);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
}
