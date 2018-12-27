package com.shyl.msc.webService.impl;

import java.math.BigDecimal;
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

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IReturnsOrderDetailService;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.msc.b2b.order.service.IReturnsRequestDetailService;
import com.shyl.msc.b2b.order.service.IReturnsRequestService;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.msc.webService.IReturnsOrderWebService;
import com.shyl.sys.dto.Message;

/**
 * 退货单实现类
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="returnsOrderWebService",portName="returnsOrderPort", targetNamespace="http://webservice.msc.shyl.com/")
public class ReturnsOrderWebService extends BaseWebService implements IReturnsOrderWebService {
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IReturnsOrderService returnsOrderService;
	@Resource
	private IReturnsRequestService returnsRequestService;
	@Resource
	private IReturnsRequestDetailService returnsRequestDetailService;
	@Resource
	private IReturnsOrderDetailService returnsOrderDetailService;
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService ;
	@Resource
	private IProductService productService;
	@Resource
	private ISnService snService;
	@Resource
	private IAttributeItemService attributeItemService;

	/**
	 * GPO上传退货单
	 */
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.returnsOrder_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Company company = (Company) map.get("company");
			Company gpo = (Company) map.get("gpo");
			Company vendor = (Company) map.get("vendor");
			Hospital hospital = (Hospital) map.get("hospital");
			String ptdm = converData.getString("ptdm");	//平台代码
			
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.returnsOrder_send);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			
			//第五步执行主逻辑
			message = sendMethod(converData, gpo, vendor, hospital, Long.valueOf(message.getData().toString()));
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	
	private Message sendMethod(JSONObject jObject, Company gpo, Company vendor, Hospital hospital, Long datagramId) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String yqthdbh = jObject.getString("yqthdbh");//药企退货单编号
			String thsj = jObject.getString("thsj");//退货时间
			String thfqr = jObject.getString("thfqr");//退货发起人
			String thfqsj = jObject.getString("thfqsj");//退货发起时间
			String thsqdbh = jObject.getString("thsqdbh");//退货申请单编号
			String mx = jObject.getString("mx");//明细
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			ReturnsOrder returnsOrder = new ReturnsOrder();
			returnsOrder.setCode(snService.getCode(ptdm, OrderType.returns));//退货单号
			returnsOrder.setInternalCode(yqthdbh);
			if(gpo != null){
				returnsOrder.setGpoCode(gpo.getCode());
				returnsOrder.setGpoName(gpo.getFullName());
			}
			returnsOrder.setVendorCode(vendor.getCode());//供应商id
			returnsOrder.setVendorName(vendor.getFullName());//供应商名称
			returnsOrder.setHospitalCode(hospital.getCode());//医疗机构id
			returnsOrder.setHospitalName(hospital.getFullName());//医疗机构名称
			returnsOrder.setReturnsRequestCode(thsqdbh);//退货申请单编号
			returnsOrder.setReturnsMan(thfqr);
			returnsOrder.setReturnsBeginDate(DateUtil.strToDate(thfqsj));
			returnsOrder.setOrderDate(DateUtil.strToDate(thsj));//退货时间
			returnsOrder.setIsAuto(1);
			returnsOrder.setIsPass(0);
			returnsOrder.setDatagramId(datagramId);
			returnsOrder.setProjectCode(ptdm);
			List<JSONObject> arr = JSON.parseArray(mx, JSONObject.class);
			JSONArray res_arr = returnsOrderService.saveReturnsOrder(ptdm, returnsOrder, arr);
			
			JSONObject data_rtn = new JSONObject();//返回
			data_rtn.put("thdbh", returnsOrder.getCode());
			data_rtn.put("yqthdbh", yqthdbh);
			
			data_rtn.put("mx", res_arr);
			message.setSuccess(true);
			message.setMsg("返回成功");
			message.setData(data_rtn);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}

	private Message checkData(JSONObject jObject){
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		Boolean isGPO = true;
		Company company = new Company();
		Company gpo = new Company();
		Company vendor = new Company();
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
			String jglx = jObject.getString("jglx");	//机构类型
			if(StringUtils.isEmpty(jglx)){
				message.setMsg("机构类型不能为空");
				return message;
			}
			try {
				int jglx_i = jObject.getIntValue("jglx");
				if(jglx_i != 1 && jglx_i != 2){
					message.setMsg("机构类型应为1或2");
					return message;
				}
				if(jglx_i == 2){
					isGPO = false;
				} else {
					isGPO = true;
				}
			} catch (Exception e) {
				message.setMsg("机构类型格式有误");
				return message;
			}
			String jgbm = jObject.getString("jgbm");	//机构编码
			if(StringUtils.isEmpty(jgbm)){
				message.setMsg("机构编码不能为空");
				return message;
			}
			if(isGPO){
				company = companyService.findByCode(ptdm, jgbm,"isGPO=1");
			}else{
				company = companyService.findByCode(ptdm, jgbm,"isVendor=1");
			}
			if(company == null){
				message.setMsg("机构编码有误");
				return message;
			}
			
			String gpobm = jObject.getString("gpobm");//GPO编码
			if(!gpobm.equals("")){
				gpo = companyService.findByCode(ptdm, gpobm, "isGPO=1");//GPO
				if(gpo == null){
					message.setMsg("GPO编码有误");
					return message;
				}
			}
			
			String gysbm = jObject.getString("gysbm");//供应商编码
			if(StringUtils.isEmpty(gysbm)){
				message.setMsg("供应商编码不能为空");
				return message;
			}
			vendor = companyService.findByCode(ptdm, gysbm, "isVendor=1");//供应商
			if(vendor == null){
				message.setMsg("供应商编码有误");
				return message;
			}
			String thfqr = jObject.getString("thfqr");//退货发起人
			if(StringUtils.isEmpty(thfqr)){
				message.setMsg("退货发起人不能为空");
				return message;
			}
			String thfqsj = jObject.getString("thfqsj");//退货发起时间
			if(StringUtils.isEmpty(thfqsj)){
				message.setMsg("退货发起时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(thfqsj, 2)){
				message.setMsg("退货发起时间格式有误");
				return message;
			}
			String thsj = jObject.getString("thsj");//退货时间
			if(StringUtils.isEmpty(thsj)){
				message.setMsg("退货时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(thsj, 2)){
				message.setMsg("退货时间格式有误");
				return message;
			}
			String yqthdbh = jObject.getString("yqthdbh");//药企退货单编号
			if(StringUtils.isEmpty(yqthdbh)){
				message.setMsg("药企退货单编号不能为空");
				return message;
			}
			ReturnsOrder returnsOrder = null;
			if(isGPO){
				returnsOrder = returnsOrderService.getByInternalCode(ptdm, gpo.getCode(), yqthdbh, isGPO);
			}else{
				returnsOrder = returnsOrderService.getByInternalCode(ptdm, vendor.getCode(), yqthdbh, isGPO);
			}
			if(returnsOrder != null){
				message.setMsgcode(Message.MsgCode.err01.toString());
				message.setMsg("药企退货单编号已存在");
				List<ReturnsOrderDetail> returnsOrderDetails = returnsOrderDetailService.listByReturnId(ptdm, returnsOrder.getId());
				JSONArray res_arr = new JSONArray();
				int k = 0;
				for(ReturnsOrderDetail detail:returnsOrderDetails){
					k++;
					JSONObject jo = new JSONObject();
					jo.put("sxh", k);
					jo.put("thdmxbh", detail.getCode());
					res_arr.add(jo);
				}
				JSONObject r_jo = new JSONObject();
				r_jo.put("thdbh", returnsOrder.getCode());
				r_jo.put("mx", res_arr);
				message.setData(r_jo);
				return message;
			}
			String thsqdbh = jObject.getString("thsqdbh");//退货申请单编号
			if(!StringUtils.isEmpty(thsqdbh)){
				ReturnsRequest returnsRequest = returnsRequestService.findByCode(ptdm, thsqdbh);
				if(returnsRequest == null){
					message.setMsg("退货申请单编号有误");
					return message;
				}
				returnsOrder = returnsOrderService.findByRequestCode(ptdm, thsqdbh);
				if(returnsOrder != null){
					message.setMsg("退货申请单编号已存在");
					return message;
				}
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
			String mx = jObject.getString("mx");
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
				message.setMsg("明细数据格式有误");
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
					sxh = jObject.getIntValue("sxh");	//顺序号
				} catch (Exception e) {
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				String ypbm = jo.getString("ypbm");//药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码不能为空");
					return message;
				}
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					message.setMsg("第("+sxh+")笔：药品编码有误");
					return message;
				}
				String scph = jo.getString("scph");//生产批号
				if(StringUtils.isEmpty(scph)){
					message.setMsg("第("+sxh+")笔：生产批号不能为空");
					return message;
				}
				String scrq = jo.getString("scrq");//生产日期
				if(StringUtils.isEmpty(scrq)){
					message.setMsg("第("+sxh+")笔：生产日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(scrq, 1)){
					message.setMsg("第("+sxh+")笔：生产日期格式错误");
					return message;
				}
				String yxrq = jo.getString("yxrq");//有效日期
				if(StringUtils.isEmpty(yxrq)){
					message.setMsg("第("+sxh+")笔：有效日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(yxrq, 1)){
					message.setMsg("第("+sxh+")笔：有效日期格式错误");
					return message;
				}
				String thsl = jo.getString("thsl");//退货数量
				if(StringUtils.isEmpty(thsl)){
					message.setMsg("第("+sxh+")笔：退货数量不能为空");
					return message;
				}
				BigDecimal thsl_int = new BigDecimal(0);
				try {
					thsl_int = jo.getBigDecimal("thsl");//退货数量
					if(thsl_int.compareTo(new BigDecimal(0)) <= 0){
						message.setMsg("第("+sxh+")笔：退货数量不能小于等于零");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：退货数量格式有误");
					return message;
				}
				String thdj = jo.getString("thdj");//退货单价
				if(StringUtils.isEmpty(thdj)){
					message.setMsg("第("+sxh+")笔：退货单价不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("thdj");//退货单价
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：退货单价格式有误");
					return message;
				}
				String thyy = jo.getString("thyy");//退货原因
				if(StringUtils.isEmpty(thyy)){
					message.setMsg("第("+sxh+")笔：退货原因不能为空");
					return message;
				}
				String psdmxbh = jo.getString("psdmxbh");//订单明细编号
				if(StringUtils.isEmpty(psdmxbh)){
					message.setMsg("第("+sxh+")笔：配送单明细编号不能为空");
					return message;
				}
				DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailService.findByCode(ptdm, psdmxbh);
				if(deliveryOrderDetail == null){
					message.setMsg("第("+sxh+")笔：配送单明细编号("+psdmxbh+")有误");
					return message;
				}
				if(!deliveryOrderDetail.getProductCode().equals(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码("+ypbm+")和配送单明细药品编码 不匹配");
					return message;
				}
				BigDecimal totalReturnNum = thsl_int.add(deliveryOrderDetail.getReturnsGoodsNum()==null?new BigDecimal(0):deliveryOrderDetail.getReturnsGoodsNum());
				if(totalReturnNum.compareTo(deliveryOrderDetail.getGoodsNum()) > 0){
					message.setMsg("第("+sxh+")笔：退货数量总和不能大于配送数量");
					return message;
				}
				String thsqdmxbh = jo.getString("thsqdmxbh"); //退货申请单明细编号
				if(!StringUtils.isEmpty(thsqdmxbh)){
					ReturnsRequestDetail returnsRequestDetail = returnsRequestDetailService.findByCode(ptdm, thsqdmxbh);
					if(returnsRequestDetail == null){
						message.setMsg("第("+sxh+")笔：退货申请单明细编号("+thsqdmxbh+")有误");
						return message;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("hospital", hospital);
		map.put("company", company);
		map.put("gpo", gpo);
		map.put("vendor", vendor);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
}
