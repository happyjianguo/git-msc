package com.shyl.msc.b2b.stl.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.stl.dao.IPaymentDao;
import com.shyl.msc.b2b.stl.entity.Payment;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.entity.Settlement.Status;
import com.shyl.msc.b2b.stl.service.IPaymentService;
import com.shyl.msc.b2b.stl.service.ISettlementService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.User;

/**
 * 发票
 * 
 * @author a_Q
 *
 */
@Service
public class PaymentService extends BaseService<Payment, Long> implements
		IPaymentService {
	private IPaymentDao paymentDao;

	public IPaymentDao getSettlementDao() {
		return paymentDao;
	}

	@Resource
	public void setPaymentDao(IPaymentDao paymentDao) {
		this.paymentDao = paymentDao;
		super.setBaseDao(paymentDao);
	}

	@Resource
	private ISettlementService settlementService;
	@Resource
	private ISnService snService;

	@Override
	@Transactional
	public String mkpayment(String projectCode, Payment payment, Long settleId, User user) {
		Settlement s = settlementService.getById(projectCode, settleId);

		payment.setCode(snService.getCode(projectCode, OrderType.payment));// 付款单号
		payment.setVendorCode(s.getVendorCode());// 供应商id
		payment.setVendorName(s.getVendorName());// 供应商名称
		payment.setHospitalCode(s.getHospitalCode());// 医疗机构id
		payment.setHospitalName(s.getHospitalName());// 医疗机构名称
		payment.setOrderDate(new Date());
		payment.setSettlementCode(s.getCode());
		

		// 修改结算单状态 金额
		BigDecimal paymentSum = payment.getSum();
		BigDecimal settlementSum = s.getSum();
		BigDecimal paidAmt = s.getPaidAmt();

		paidAmt = paidAmt.add(paymentSum);
		if (paidAmt.compareTo(settlementSum) >= 0) {
			s.setStatus(Status.paid);
		} else {
			s.setStatus(Status.paying);
		}
		s.setPaidAmt(paidAmt);
		settlementService.update(projectCode, s);
		paymentDao.save(payment);

		JSONArray jsonArray = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", payment.getCode());
		jsonArray.add(jo);
		return JSON.toJSONString(jsonArray);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> listByPeriod(String projectCode, String beginDate, String endDate) {
		List<Map<String, Object>> list = paymentDao.listByPeriod(beginDate, endDate);
		List<Map<String, Object>> data1 = getData1(list);
		List<Map<String, Object>> data2 = getData2(data1);
		
		return data2;
	}
	
	private List<Map<String, Object>> getData1(List<Map<String, Object>> list){
		List<Map<String, Object>> settlementList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> settlementMap = new HashMap<String, Map<String, Object>>();	
		for (Map<String, Object> map : list) {
			String hospitalCode = (String) map.get("HOSPITALCODE");
			String hospitalName = (String) map.get("HOSPITALNAME");
			Date accEndDate = (Date) map.get("ACCENDDATE");
			BigDecimal sum = map.get("SUM")==null?new BigDecimal(0):(BigDecimal) map.get("SUM");
			BigDecimal paidAmt = map.get("PAIDAMT")==null?new BigDecimal(0):(BigDecimal) map.get("PAIDAMT");
			BigDecimal paySum = map.get("PAYSUM")==null?new BigDecimal(0):(BigDecimal) map.get("PAYSUM");
			Date payDate = (Date) map.get("PAYDATE");
			String settlementCode = (String) map.get("SETTLEMENTCODE");
			
			Map<String, Object> settlement = (Map<String, Object>) settlementMap
					.get(settlementCode);
			if (settlement == null) {
				settlement = new HashMap<String, Object>();			
				settlement.put("hospitalCode", hospitalCode);
				settlement.put("hospitalName", hospitalName);
				settlement.put("sum", sum);
				//未还款天数
				long unpayDay = (accEndDate.getTime()-new Date().getTime())/(24*60*60*1000); 
				//未还款额度
				BigDecimal unpayAmt = (sum.subtract(paidAmt)).multiply(new BigDecimal(unpayDay));
				settlement.put("unpayAmt", unpayAmt);
				
				settlementMap.put(settlementCode, settlement);
				settlementList.add(settlement);
			}

			//提前回款天数
			long prepayDay = (accEndDate.getTime()-payDate.getTime())/(24*60*60*1000); 
			//提前回款额度
			BigDecimal prepayAmt = paySum.multiply(new BigDecimal(prepayDay));
			BigDecimal prepayAmtT = (BigDecimal)settlement.get("prepayAmt");
			if(prepayAmtT == null){
				prepayAmtT = new BigDecimal(0);
			}
			settlement.put("prepayAmt", prepayAmtT.add(prepayAmt));		
			
			if(prepayDay > 0){
				BigDecimal prepaySumT = (BigDecimal)settlement.get("prepaySum");
				if(prepaySumT == null){
					prepaySumT = new BigDecimal(0);
				}
				settlement.put("prepaySum", prepaySumT.add(paySum));	
			}	
		}
		
		return settlementList;
	}
	
	private List<Map<String, Object>> getData2(List<Map<String, Object>> list){
		List<Map<String, Object>> settlementList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> settlementMap = new HashMap<String, Map<String, Object>>();	
		for (Map<String, Object> map : list) {
			String hospitalCode = (String) map.get("hospitalCode");
			String hospitalName = (String) map.get("hospitalName");
			BigDecimal unpayAmt = map.get("unpayAmt")==null?new BigDecimal(0):(BigDecimal) map.get("unpayAmt");
			BigDecimal prepayAmt = map.get("prepayAmt")==null?new BigDecimal(0):(BigDecimal) map.get("prepayAmt");
			BigDecimal sum = map.get("sum")==null?new BigDecimal(0):(BigDecimal) map.get("sum");
			BigDecimal prepaySum = map.get("prepaySum")==null?new BigDecimal(0):(BigDecimal) map.get("prepaySum");
			Map<String, Object> settlement = (Map<String, Object>) settlementMap.get(hospitalCode);
			if (settlement == null) {
				settlement = new HashMap<String, Object>();			
				settlement.put("id", hospitalCode);
				settlement.put("name", hospitalName);
						
				settlementMap.put(hospitalCode, settlement);
				settlementList.add(settlement);
			}

			BigDecimal unpayAmtT = (BigDecimal)settlement.get("unpayAmt");
			if(unpayAmtT == null){
				unpayAmtT = new BigDecimal(0);
			}
			settlement.put("unpayAmt", unpayAmtT.add(unpayAmt));	
			
			BigDecimal prepayAmtT = (BigDecimal)settlement.get("prepayAmt");
			if(prepayAmtT == null){
				prepayAmtT = new BigDecimal(0);
			}
			settlement.put("prepayAmt", prepayAmtT.add(prepayAmt));		
			
			BigDecimal sumT = (BigDecimal)settlement.get("sum");
			if(sumT == null){
				sumT = new BigDecimal(0);
			}
			settlement.put("sum", sumT.add(sum));	
			
			BigDecimal prepaySumT = (BigDecimal)settlement.get("prepaySum");
			if(prepaySumT == null){
				prepaySumT = new BigDecimal(0);
			}
			settlement.put("prepaySum", prepaySumT.add(prepaySum));	
		}
		
		return settlementList;
	}

	@Override
	@Transactional
	public void savePayment(String projectCode, Payment payment) {
		paymentDao.save(payment);
		String code = payment.getSettlementCode();
		Settlement settlement = settlementService.findByCode(projectCode, code);
		BigDecimal sum = settlement.getPaidAmt() == null?new BigDecimal(0):settlement.getPaidAmt();
		settlement.setPaidAmt(payment.getSum().add(sum));
		if(settlement.getSum().compareTo(settlement.getPaidAmt()) > 0){
			settlement.setStatus(Status.paying);
		}else{
			settlement.setStatus(Status.paid);
		}
		settlementService.update(projectCode, settlement);
	}

	@Override
	public Payment getByInternalCode(String projectCode, String internalCode) {
		return paymentDao.getByInternalCode(internalCode);
	}
}
