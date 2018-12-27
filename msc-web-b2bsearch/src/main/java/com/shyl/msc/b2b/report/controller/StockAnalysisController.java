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
import com.shyl.msc.b2b.stock.service.IHisStockDayService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;

/**
 * 库存周转率分析
 *
 */
@Controller
@RequestMapping("/b2b/report/stock")
public class StockAnalysisController extends BaseController {

	@Resource
	private IHisStockDayService	hisStockDayService;
	@Resource
	private IHospitalService hospitalService;
	
	@RequestMapping("")
	public String home(){
		return "b2b/report/stock/chart";
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		
	}

	/**
	 * 库存周转率分析
	 * @return
	 */
	@RequestMapping("/chart")
	@ResponseBody
	public JSONObject chart(String org, String year, String month,@CurrentUser User user){
		List<Map<String,Object>> turnoverRatios = hisStockDayService.getHospitalTurnoverRatio(user.getProjectCode(), year+"-"+month+"-01", year+"-"+month+"-31");
		
		JSONObject chart = new JSONObject();
		//区县
		if(org.equals("1")){
			turnoverRatios = getData(user.getProjectCode(), turnoverRatios);		
		}
		chart.put("turnoverRatios", turnoverRatios);
		return chart;
	}
	
	private List<Map<String, Object>> getData(String projectCode, List<Map<String, Object>> list){
		List<Map<String, Object>> hospitalList = hospitalService.listAll(projectCode);
		Map<String, String> hospitalMap = new HashMap<String, String>();
		for(Map<String, Object> map:hospitalList){
			hospitalMap.put(map.get("CODE").toString(), map.get("REGIONCODENAME").toString());
		}
		List<Map<String, Object>> settlementList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> settlementMap = new HashMap<String, Map<String, Object>>();	
		for (Map<String, Object> map : list) {
			String hospitalCode = map.get("HOSPITALCODE").toString();
			BigDecimal beginAmt = map.get("BEGINAMT")==null?new BigDecimal(0):(BigDecimal) map.get("BEGINAMT");
			BigDecimal endAmt = map.get("ENDAMT")==null?new BigDecimal(0):(BigDecimal) map.get("ENDAMT");
			BigDecimal totalSum = map.get("TOTALSUM")==null?new BigDecimal(0):(BigDecimal) map.get("TOTALSUM");
            
			String regionCodeName = hospitalMap.get(hospitalCode).toString();
			Map<String, Object> settlement = (Map<String, Object>) settlementMap
					.get(regionCodeName);
			if (settlement == null) {
				settlement = new HashMap<String, Object>();			
				settlement.put("NAME", regionCodeName);
						
				settlementMap.put(regionCodeName, settlement);
				settlementList.add(settlement);
			}

			BigDecimal beginAmtT = (BigDecimal)settlement.get("BEGINAMT");
			if(beginAmtT == null){
				beginAmtT = new BigDecimal(0);
			}
			settlement.put("BEGINAMT", beginAmtT.add(beginAmt));	
			
			BigDecimal endAmtT = (BigDecimal)settlement.get("ENDAMT");
			if(endAmtT == null){
				endAmtT = new BigDecimal(0);
			}
			settlement.put("ENDAMT", endAmtT.add(endAmt));		
			
			BigDecimal totalSumT = (BigDecimal)settlement.get("TOTALSUM");
			if(totalSumT == null){
				totalSumT = new BigDecimal(0);
			}
			settlement.put("TOTALSUM", totalSumT.add(totalSum));	
		}
		
		return settlementList;
	}
}
