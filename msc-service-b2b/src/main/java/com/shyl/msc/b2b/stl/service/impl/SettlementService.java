package com.shyl.msc.b2b.stl.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.stl.dao.IInvoiceDao;
import com.shyl.msc.b2b.stl.dao.ISettlementDao;
import com.shyl.msc.b2b.stl.dao.ISettlementDetailDao;
import com.shyl.msc.b2b.stl.entity.Invoice;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.entity.Settlement.Status;
import com.shyl.msc.b2b.stl.entity.SettlementDetail;
import com.shyl.msc.b2b.stl.service.ISettlementService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;
/**
 * 发票
 * 
 * @author a_Q
 *
 */
@Service
public class SettlementService extends BaseService<Settlement, Long> implements ISettlementService {
	
	private ISettlementDao settlementDao;

	public ISettlementDao getSettlementDao() {
		return settlementDao;
	}

	@Resource
	public void setSettlementDao(ISettlementDao settlementDao) {
		this.settlementDao = settlementDao;
		super.setBaseDao(settlementDao);
	}
	
	@Resource
	private IInvoiceDao invoiceDao;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private ISettlementDetailDao settlementDetailDao;
	@Resource
	private ISnDao sndao;

	@Override
	@Transactional
	public String mkSettlement(String projectCode, Settlement settlement, Long[] invoiceids, String hospitalCode, User user) {
		Hospital h = hospitalService.findByCode(settlement.getProjectCode(), hospitalCode);
		Company vendor = companyService.getById(projectCode, user.getOrganization().getOrgId());
		
		Settlement s = new Settlement();
		s.setCode(sndao.getCode(OrderType.settlement));//结算单号
		s.setInternalCode("");
		s.setVendorCode(vendor.getCode());//供应商id
		s.setVendorName(vendor.getFullName());//供应商名称
		s.setHospitalCode(h.getCode());//医疗机构id
		s.setHospitalName(h.getFullName());//医疗机构名称
		s.setIsPass(0);
		s.setOrderDate(new Date());
		s.setAccBeginDate(new Date());
		if(settlement.getAccEndDate() != null){
			s.setAccEndDate(settlement.getAccEndDate());
		}else{
			s.setAccEndDate(getDateAfter(new Date(),30));
		}
		s.setStatus(Status.unpay);
		s.setPaidAmt(new BigDecimal("0"));
		
		
		BigDecimal sum = new BigDecimal(0); 
		//结算明细
		for (int i = 0; i < invoiceids.length; i++) {
			Invoice in = invoiceDao.getById(invoiceids[i]);
			SettlementDetail sd = new SettlementDetail();
			sd.setSettlement(s);//结算单
			
			String code_detail = s.getCode() + "-" + NumberUtil.addZeroForNum((i+1) + "", 4);
			sd.setCode(code_detail);
			sd.setInvoiceCode(in.getCode());
			if(in.getIsRed() == 1){
				sd.setSum(in.getSum().multiply(new BigDecimal("-1")));
			}else{
				sd.setSum(in.getSum());
			}
			System.out.println("in sum = "+sd.getSum());
			
			s.getSettlementDetails().add(sd);

			
			sum = sum.add(sd.getSum());
			//修改发票状态为 已结算
			in.setStatus(com.shyl.msc.b2b.stl.entity.Invoice.Status.settle);
			invoiceDao.update(in);
		}
		s.setSum(sum);
		this.saveSettle(s);
		JSONArray jsonArray = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", s.getCode());
		jsonArray.add(jo);
		return JSON.toJSONString(jsonArray);
	}
	
	@Transactional
	private void saveSettle(Settlement s) {
		settlementDao.save(s);
		
		for (SettlementDetail sd : s.getSettlementDetails()) {
			settlementDetailDao.save(sd);
		}
		
	}

	private Date getDateAfter(Date d, int day) {  
        Calendar now = Calendar.getInstance();  
        now.setTime(d);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);  
        return now.getTime();  
    }

	@Override
	@Transactional
	public void saveSettlement(String projectCode, Settlement settlement) {
		settlementDao.saveJDBC(settlement);
		List<SettlementDetail> settlementDetails = new ArrayList<>();
		for(SettlementDetail detail : settlement.getSettlementDetails()){
			detail.setSettlement(settlement);
			settlementDetails.add(detail);
			//settlementDetailDao.save(detail);
		}
		settlementDetailDao.saveBatch(settlementDetails);
	}

	@Override
	@Transactional(readOnly = true)
	public Settlement findByCode(String projectCode, String code) {
		return settlementDao.findByCode(code);
	}

	@Override
	public JSONArray saveSettlement(String projectCode, Settlement settlement, List<JSONObject> arr) {
		JSONArray res_arr = new JSONArray();
		int i = 0;
		for(JSONObject jo:arr){
			i++;
			String code_detail = settlement.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			SettlementDetail detail = new SettlementDetail();
			String fpbh = jo.getString("fpbh");		//发票编号
			BigDecimal jsje = jo.getBigDecimal("jsje");		//结算金额
			String sxh = jo.getString("sxh");
			Invoice invoice = invoiceDao.findByCode(fpbh);
			detail.setCode(code_detail);
			detail.setInvoiceCode(invoice.getCode());
			detail.setSum(jsje);
			settlement.getSettlementDetails().add(detail);
			
			JSONObject res_arr_jo = new JSONObject();
			res_arr_jo.put("sxh", sxh);
			res_arr_jo.put("jsdmxbh", code_detail);
			res_arr.add(res_arr_jo);
		}
		this.saveSettlement(projectCode, settlement);
		return res_arr;
	}

	@Override
	public Settlement getByInternalCode(String projectCode, String code, String internalCode, Boolean isGPO) {
		return settlementDao.getByInternalCode(code, internalCode, isGPO);
	}

	@Override
	public List<SettlementDetail> listBySettlementId(String projectCode, Long id) {
		return settlementDao.listBySettlementId(id);
	}
	
}
