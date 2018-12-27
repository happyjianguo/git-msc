package com.shyl.msc.b2b.report.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IInOutBoundDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.sys.entity.User;

/**
 * 订单GPO集中度查询
 * @author lenovo
 *
 */
@Controller
@RequestMapping("/b2b/report/orderCentralized")
public class OrderCentralizedController extends BaseController {

	@Resource
	private IPurchaseOrderService	purchaseOrder;

	@Resource
	private IInOutBoundDetailService inOutBoundDetailServic;

	@Override
	protected void init(WebDataBinder binder) {

	}

	@RequestMapping(value = "/hospitalChartSum", method = RequestMethod.GET)
	public String hisChartSum() {
		return "b2b/report/orderCentralized/hospitalChartSum";
	}

	@RequestMapping(value = "/hospitalChartSum", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> hisChartSum(String year, String month, @CurrentUser User user) {
		return purchaseOrder.getGpoSumByHospital(user.getProjectCode(), year, month, 0);
	}

	@RequestMapping(value = "/hospitalChartPercent", method = RequestMethod.GET)
	public String hisChartPercent() {
		return "b2b/report/orderCentralized/hospitalChartPercent";
	}

	@RequestMapping(value = "/hospitalChartPercent", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> hisChartPercent(String year, String month, @CurrentUser User user) {
		return purchaseOrder.getGpoCentralizedByHospital(user.getProjectCode(), year, month, 0);
	}


	@RequestMapping(value = "/hospitalChartPercent1", method = RequestMethod.GET)
	public String hisChartPercent1() {
		return "b2b/report/orderCentralized/hospitalChartPercent1";
	}

	@RequestMapping(value = "/hospitalChartPercent1", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> hisChartPercent1(String year, String month, @CurrentUser User user) {
		return inOutBoundDetailServic.listGpoReport(user.getProjectCode(), year, month, 0);
	}


	@RequestMapping(value = "/regionChartSum", method = RequestMethod.GET)
	public String regionChartSum() {
		return "b2b/report/orderCentralized/regionChartSum";
	}

	@RequestMapping(value = "/regionChartSum", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> regionChartSum(String year, String month, String treepath, @CurrentUser User user) {
		return purchaseOrder.getGpoSumByRegion(user.getProjectCode(), year, month, treepath, 0);
	}

	@RequestMapping(value = "/regionChartPercent", method = RequestMethod.GET)
	public String regionChartPercent() {
		return "b2b/report/orderCentralized/regionChartPercent";
	}

	@RequestMapping(value = "/regionChartPercent", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> regionChartPercent(String year, String month, String treepath, @CurrentUser User user) {
		return purchaseOrder.getGpoCentralizedByRegion(user.getProjectCode(), year, month, treepath, 0);
	}

	@RequestMapping(value = "/regionChartPercent1", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> regionChartPercent1(String year, String month, String treepath, @CurrentUser User user) {
		return inOutBoundDetailServic.listGpoReportByRegion(user.getProjectCode(), year, month, treepath, 0);
	}
}
