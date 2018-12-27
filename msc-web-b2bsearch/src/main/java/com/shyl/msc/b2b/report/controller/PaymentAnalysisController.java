package com.shyl.msc.b2b.report.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.stl.service.IPaymentService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;

/**
 * 回款额度、比例、周期分析
 *
 */
@Controller
@RequestMapping("/b2b/report/payment")
public class PaymentAnalysisController extends BaseController {

	@Resource
	private IPaymentService	paymentService;
	@Resource
	private IHospitalService hospitalService;
	
	@RequestMapping("/chart1")
	public String chart1(){
		return "b2b/report/payment/chart1";
	}
	
	@RequestMapping("/chart2")
	public String chart2(){
		return "b2b/report/payment/chart2";
	}
	
	@RequestMapping("/chart3")
	public String chart3(){
		return "b2b/report/payment/chart3";
	}

	@Override
	protected void init(WebDataBinder binder) {
		
	}

	/**
	 * 回款额度、回款比例、回款周期分析
	 * @return
	 */
	@RequestMapping("/chart")
	@ResponseBody
	public JSONObject chart(String org, String year, String month,@CurrentUser User user){
		List<Map<String,Object>> pays = paymentService.listByPeriod(user.getProjectCode(), year+"-"+month+"-01", year+"-"+month+"-31");
		
		JSONObject chart = new JSONObject();
		//区县
		if(org.equals("1")){
			pays = getDate(user.getProjectCode(), pays);		
		}
		chart.put("pays", pays);
		return chart;
	}
	
	private List<Map<String, Object>> getDate(String projectCode, List<Map<String, Object>> list){
		List<Map<String, Object>> hospitalList = hospitalService.listAll(projectCode);
		Map<String, String> hospitalMap = new HashMap<String, String>();
		for(Map<String, Object> map:hospitalList){
			System.out.println("id"+map.get("CODE"));
			System.out.println(map.get("REGIONCODENAME"));
			hospitalMap.put(map.get("CODE").toString(), map.get("REGIONCODENAME").toString());
		}
		List<Map<String, Object>> settlementList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> settlementMap = new HashMap<String, Map<String, Object>>();	
		for (Map<String, Object> map : list) {
			String hospitalCode = (String) map.get("id");
			BigDecimal unpayAmt = (BigDecimal) map.get("unpayAmt");
			BigDecimal prepayAmt = (BigDecimal) map.get("prepayAmt");			
			BigDecimal sum = (BigDecimal) map.get("sum");
			BigDecimal prepaySum = (BigDecimal) map.get("prepaySum");	
            
			String regionCodeName = (String)hospitalMap.get(hospitalCode);
			System.out.println(regionCodeName+":"+hospitalCode+":"+unpayAmt+":"+prepayAmt+":"+sum+":"+prepaySum);
			Map<String, Object> settlement = (Map<String, Object>) settlementMap
					.get(regionCodeName);
			if (settlement == null) {
				settlement = new HashMap<String, Object>();			
				settlement.put("name", regionCodeName);
						
				settlementMap.put(regionCodeName, settlement);
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
}
