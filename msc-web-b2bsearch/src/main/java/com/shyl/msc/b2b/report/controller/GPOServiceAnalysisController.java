package com.shyl.msc.b2b.report.controller;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.sys.entity.User;

/**
 * 供应商服务效率分析
 *
 */
@Controller
@RequestMapping("/b2b/report/gposervice")
public class GPOServiceAnalysisController extends BaseController {

	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderPlanDetailService purchaseOrderPlanDetailService;
	
	@Override
	protected void init(WebDataBinder binder) {
		
	}

	@RequestMapping("/chart2")
	public String home(){
		return "/b2b/report/gposervice/chart2";
	}
	
	/**
	 * 供货情况分析
	 * @return
	 */
	@RequestMapping("/chartB")
	@ResponseBody
	public JSONObject chartB(String year, @CurrentUser User user){
		List<Map<String,Object>> supplys = purchaseOrderPlanDetailService.listByDate(user.getProjectCode(), year+"-01-01", year+"-12-31");
		
		JSONObject chart = new JSONObject();
		Iterator<Map<String, Object>> iterator = supplys.iterator();
		while(iterator.hasNext()){
			Map<String, Object> next = iterator.next();
			if((boolean)next.get("NUM").equals(new BigDecimal(0))){
				iterator.remove();
			}
		}
		chart.put("supplys", supplys);
		return chart;
	}
	
	/**
	 * 供货情况分析 明细
	 * @return
	 */
	@RequestMapping(value = "/chartBDetail", method = RequestMethod.GET)
	public String chartBDetail(String year,String hospitalCode, Model model){
		model.addAttribute("year",year);
		model.addAttribute("hospitalCode",hospitalCode);
		return "/b2b/report/gposervice/chart2detail";
	}
	/**
	 * 供货情况分析 明细
	 * @return
	 */
	@RequestMapping(value = "/chartBDetail", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> chartBDetail(String year,String hospitalCode, PageRequest pageable,@CurrentUser User user){
		System.out.println("year="+year);
		
		DataGrid<Map<String,Object>> page = purchaseOrderPlanDetailService.listByHospitalDate(user.getProjectCode(), hospitalCode,year+"-01-01", year+"-12-31",pageable);

		return page;
	}
	
	/**
	 * 服务商效率排名
	 * @return
	 */
	@RequestMapping(value = "/chartRank", method= RequestMethod.GET)
	public String chartRank() {
		return "b2b/report/gposervice/chartRank";
	}

	@RequestMapping(value = "/chartRank", method= RequestMethod.POST)
	@ResponseBody
	private List<Map<String, Object>> chartRank(String year, String month, @CurrentUser User user) {
		return purchaseOrderPlanService.queryGpoTimelyRankList(user.getProjectCode(), year, month);
	}

	/**
	 * 服务商及时率
	 * @return
	 */
	@RequestMapping(value = "/chartTimely", method= RequestMethod.GET)
	public String chartTimely(ModelMap map, Long vendorId){
		map.addAttribute("vendorId", vendorId);
		return "b2b/report/gposervice/chartTimely";
	}

	@RequestMapping(value = "/chartTimely", method= RequestMethod.POST)
	@ResponseBody
	private JSONArray chartTimely(String year, String month, String vendorCode, @CurrentUser User user) {
		JSONArray array = new JSONArray();
		
		//6小时内
		Long time6H = purchaseOrderPlanService.getDeliveryTimely(user.getProjectCode(), year, month, vendorCode, 0, 6);
		//12小时内
		Long time12H = purchaseOrderPlanService.getDeliveryTimely(user.getProjectCode(), year, month, vendorCode, 6, 12);
		//24小时内
		Long time24H = purchaseOrderPlanService.getDeliveryTimely(user.getProjectCode(), year, month, vendorCode, 12, 24);
		//48小时内
		Long time48H = purchaseOrderPlanService.getDeliveryTimely(user.getProjectCode(), year, month, vendorCode, 24, 48);
		//3天以上
		Long time3D = purchaseOrderPlanService.getDeliveryTimely(user.getProjectCode(), year, month, vendorCode, 48, null);
		JSONObject json = new JSONObject();
		json.put("name", "6小时及以内");
		json.put("value", time6H);
		array.add(json);
		json = new JSONObject();
		json.put("name", "12小时及以内");
		json.put("value", time12H);
		array.add(json);
		json = new JSONObject();
		json.put("name", "24小时及以内");
		json.put("value", time24H);
		array.add(json);
		json = new JSONObject();
		json.put("name", "48小时及以内");
		json.put("value", time48H);
		array.add(json);
		json = new JSONObject();
		json.put("name", "3天以上");
		json.put("value", time3D);
		array.add(json);
		return array;
	}

}
