package com.shyl.msc.b2b.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDao;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IReturnsRequestDao;
import com.shyl.msc.b2b.order.dao.IReturnsRequestDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;
import com.shyl.msc.b2b.order.entity.ReturnsRequest.Status;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
import com.shyl.msc.b2b.order.service.IReturnsRequestService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;
/**
 * 退货申请单Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly = true)
public class ReturnsRequestService extends BaseService<ReturnsRequest, Long> implements IReturnsRequestService {
	private IReturnsRequestDao returnsRequestDao;
	
	public IReturnsRequestDao getReturnsRequestDao() {
		return returnsRequestDao;
	}

	@Resource
	public void setReturnsRequestDao(IReturnsRequestDao returnsRequestDao) {
		this.returnsRequestDao = returnsRequestDao;
		super.setBaseDao(returnsRequestDao);	
	}
	
	@Resource
	private IDeliveryOrderDao deliveryOrderDao;
	@Resource
	private IDeliveryOrderDetailDao deliveryOrderDetailDao;
	@Resource
	private IReturnsRequestDetailDao returnsRequestDetailDao;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ISnDao sndao;
	@Resource
	private IProductService productService;
	
	@Override
	@Transactional
	public String mkreturn(String projectCode, String data, User user) {
		Hospital hospital = hospitalService.getById(projectCode, user.getOrganization().getOrgId());
		List<JSONObject> list = JSON.parseArray(data, JSONObject.class);
		Map<String, List<JSONObject>> vendorMap = new HashMap<>();
		for(JSONObject jo:list){
			if(jo == null){
				continue;
			}
			List<JSONObject> list2 = new ArrayList<>();
			String vendorId = jo.getString("vendorCode");
			String gpoId = jo.getString("gpoCode");
			String key = "";
			if(StringUtils.isEmpty(gpoId)){
				key = vendorId;
			}else{
				key = vendorId + "-" + gpoId;
			}
			if(vendorMap.get(key) == null){
				list2 = new ArrayList<>();
				list2.add(jo);
			}else{
				list2 = vendorMap.get(key);
				list2.add(jo);
			}
			vendorMap.put(key, list2);
		}
		JSONArray jsonArray = new JSONArray();
		for(String key : vendorMap.keySet()){
			String [] k = key.split("-"); 
			Company vendor = companyService.findByCode(projectCode,k[0], "isVendor=1");
			Company gpo = null;
			if(k.length > 1 && !StringUtils.isEmpty(k[1])){
				gpo = companyService.findByCode(projectCode,k[1], "isGPO=1");
			}
			ReturnsRequest returnsRequest = new ReturnsRequest();
			returnsRequest.setCode(sndao.getCode(OrderType.returnsRequest));//退货申请单号
			returnsRequest.setInternalCode("");
			returnsRequest.setVendorCode(vendor.getCode());//供应商id
			returnsRequest.setVendorName(vendor.getFullName());//供应商名称
			if(gpo != null){
				returnsRequest.setGpoCode(gpo.getCode());
				returnsRequest.setGpoName(gpo.getFullName());
			}
			
			returnsRequest.setHospitalCode(hospital.getCode());//医疗机构id
			returnsRequest.setHospitalName(hospital.getFullName());//医疗机构名称
			returnsRequest.setReturnsMan(user.getName());
			returnsRequest.setReturnsBeginDate(new Date());
			returnsRequest.setOrderDate(new Date());//退货申请时间
			returnsRequest.setIsPass(0);
			returnsRequest.setIsAuto(0);
			returnsRequest.setStatus(Status.unaudit);
			returnsRequest.setReturnsBeginDate(new Date());
			returnsRequest.setReturnsMan(user.getEmpId());
			
			
			BigDecimal returnsNum = new BigDecimal(0); 
			BigDecimal returnsSum = new BigDecimal(0); 
			//订单明细
			List<JSONObject> details = vendorMap.get(key);
			int i = 1;
			for (JSONObject detail:details) {
				Long detailId = detail.getLong("id");
				BigDecimal num = detail.getBigDecimal("num");
				String reason = detail.getString("reason");
				DeliveryOrderDetail pd = deliveryOrderDetailDao.getById(detailId);
			
				ReturnsRequestDetail returnsRequestDetail = new ReturnsRequestDetail();
				String detail_code = returnsRequest.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
				returnsRequestDetail.setReturnsRequest(returnsRequest);//退货单
				returnsRequestDetail.setOrderDate(returnsRequest.getOrderDate());
				returnsRequestDetail.setCode(detail_code);
				returnsRequestDetail.setInternalCode("");
				returnsRequestDetail.setBatchCode(pd.getBatchCode());//生产批号
				returnsRequestDetail.setBatchDate(pd.getBatchDate());//生产日期
				returnsRequestDetail.setExpiryDate(pd.getExpiryDate());//有效日期
				returnsRequestDetail.setProductCode(pd.getProductCode());
				returnsRequestDetail.setProductName(pd.getProductName());
				returnsRequestDetail.setProducerName(pd.getProducerName());
				returnsRequestDetail.setDosageFormName(pd.getDosageFormName());
				returnsRequestDetail.setModel(pd.getModel());
				returnsRequestDetail.setPackDesc(pd.getPackDesc());
				returnsRequestDetail.setUnit(pd.getUnit());
				returnsRequestDetail.setPrice(pd.getPrice());
				returnsRequestDetail.setGoodsNum(num);//数量
				returnsRequestDetail.setGoodsSum(returnsRequestDetail.getPrice().multiply(num));//金额
				returnsRequestDetail.setReason(reason);//退货原因
				returnsRequestDetail.setDeliveryOrderDetailCode(pd.getCode());
				returnsRequestDetail.setIsPass(0);
				
				
				returnsRequest.getReturnsRequestDetails().add(returnsRequestDetail);
				returnsNum = returnsNum.add(returnsRequestDetail.getGoodsNum());
				returnsSum = returnsSum.add(returnsRequestDetail.getGoodsSum());
				i++;
			}
			returnsRequest.setNum(returnsNum);
			returnsRequest.setSum(returnsSum);
			returnsRequest.setDatagramId(null);
			this.saveReturnsRequest(projectCode, returnsRequest);
			JSONObject jo = new JSONObject();
			jo.put("thsqdbh", returnsRequest.getCode());
			jsonArray.add(jo);
		}
		return JSON.toJSONString(jsonArray);
	}
	
	@Override
	@Transactional
	public void saveReturnsRequest(String projectCode, ReturnsRequest returnsRequest){
		returnsRequestDao.saveJDBC(returnsRequest);
		List<ReturnsRequestDetail> returnsRequestDetails = new ArrayList<>();
		for (ReturnsRequestDetail returnsRequestDetail : returnsRequest.getReturnsRequestDetails()) {
			returnsRequestDetail.setReturnsRequest(returnsRequest);
			returnsRequestDetails.add(returnsRequestDetail);
			//returnsRequestDetailDao.save(returnsRequestDetail);
		}
		returnsRequestDetailDao.saveBatch(returnsRequestDetails);
	}

	@Override
	public List<ReturnsRequest> listByDate(String projectCode, String vendorCode, String startDate, String endDate, boolean isGPO) {
		return returnsRequestDao.listByDate(vendorCode, startDate, endDate, isGPO);
	}

	@Override
	public ReturnsRequest findByCode(String projectCode, String code) {
		return returnsRequestDao.findByCode(code);
	}

	@Override
	@Transactional
	public void udateReturnsRequest(String projectCode, ReturnsRequest request) {
		ReturnsRequest returnsRequest = returnsRequestDao.getById(request.getId());
		returnsRequest.setStatus(request.getStatus());
		returnsRequest.setReply(request.getReply());
		for(ReturnsRequestDetail requestDetail:request.getReturnsRequestDetails()){
			ReturnsRequestDetail returnsRequestDetail = returnsRequestDetailDao.getById(requestDetail.getId());
			returnsRequestDetail.setReplyNum(requestDetail.getReplyNum());
			returnsRequestDetail.setReply(requestDetail.getReply());
			returnsRequestDetailDao.update(returnsRequestDetail);
		}
		returnsRequestDao.update(returnsRequest);
	}

	@Override
	public void saveReturnsRequest(String projectCode, ReturnsRequest returnsRequest, List<JSONObject> list) {
		BigDecimal returnsNum = new BigDecimal(0); 
		BigDecimal returnsSum = new BigDecimal(0); 
		int i = 0;
		for(JSONObject jo:list){
			i++;
			String psdmxbh = jo.getString("psdmxbh");//配送单明细编号
			String scph = jo.getString("scph");//生产批号
			String scrq = jo.getString("scrq");//生产日期
			String yxrq = jo.getString("yxrq");//有效日期
			String thyy = jo.getString("thyy");//退货原因
			String ypbm = jo.getString("ypbm");//药品编码
			BigDecimal thsl = jo.getBigDecimal("thsl");//退货数量
			BigDecimal thdj = jo.getBigDecimal("thdj");//退货单价
			BigDecimal thje = jo.getBigDecimal("thje");//退货金额
			Product product = productService.getByCode(projectCode, ypbm);
			DeliveryOrderDetail pd = deliveryOrderDetailDao.findByCode(psdmxbh);
			if(pd != null){
				String gpoCode = pd.getDeliveryOrder().getGpoCode();
				String gpoName = pd.getDeliveryOrder().getGpoName();
				if (!StringUtils.isEmpty(gpoCode)) {
					returnsRequest.setGpoCode(gpoCode);
					returnsRequest.setGpoName(gpoName);
				}
			}
			
			ReturnsRequestDetail returnsRequestDetail = new ReturnsRequestDetail();
			String detail_code = returnsRequest.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			returnsRequestDetail.setReturnsRequest(returnsRequest);//退货单
			returnsRequestDetail.setOrderDate(returnsRequest.getOrderDate());
			returnsRequestDetail.setCode(detail_code);
			returnsRequestDetail.setInternalCode("");
			returnsRequestDetail.setBatchCode(scph);//生产批号
			returnsRequestDetail.setBatchDate(scrq);//生产日期
			returnsRequestDetail.setExpiryDate(yxrq);//有效日期
			returnsRequestDetail.setProductCode(product.getCode());
			returnsRequestDetail.setProductName(product.getName());
			returnsRequestDetail.setProducerName(product.getProducerName());
			returnsRequestDetail.setDosageFormName(product.getDosageFormName());
			returnsRequestDetail.setModel(product.getModel());
			returnsRequestDetail.setPackDesc(product.getPackDesc());
			returnsRequestDetail.setUnit(product.getUnit());
			returnsRequestDetail.setGoodsNum(thsl);//数量
			if(pd != null){
				returnsRequestDetail.setPrice(pd.getPrice());
				returnsRequestDetail.setGoodsSum(pd.getPrice().multiply(thsl));//金额
				returnsRequestDetail.setDeliveryOrderDetailCode(pd.getCode());
			} else {
				returnsRequestDetail.setPrice(thdj);
				returnsRequestDetail.setGoodsSum(thje);//金额
			}
			returnsRequestDetail.setReason(thyy);//退货原因
			returnsRequestDetail.setIsPass(0);
			returnsRequestDetail.setOrderType(OrderType.returnsRequest);
			returnsRequestDetail.setCreateUser(returnsRequest.getCreateUser());;
			
			returnsRequest.getReturnsRequestDetails().add(returnsRequestDetail);
			returnsNum = returnsNum.add(returnsRequestDetail.getGoodsNum());
			returnsSum = returnsSum.add(returnsRequestDetail.getGoodsSum());
		}
		returnsRequest.setNum(returnsNum);
		returnsRequest.setSum(returnsSum);
		
		this.saveReturnsRequest(projectCode, returnsRequest);
	}

	@Override
	public DataGrid<ReturnsRequest> listByReturnsRequestAndDetail(PageRequest pageable) {
		// TODO Auto-generated method stub
		return 	 returnsRequestDao.listByReturnsRequestAndDetail(pageable);
	}
}
