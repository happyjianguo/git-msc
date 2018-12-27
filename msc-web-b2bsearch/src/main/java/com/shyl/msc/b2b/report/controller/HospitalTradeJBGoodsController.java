package com.shyl.msc.b2b.report.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 4、医疗机构采购汇总（含图）
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/hospitaltradejbgoods")
public class HospitalTradeJBGoodsController extends BaseController {

	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IHospitalService hospitalService;
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap map){
		System.out.println("inin");
		map.addAttribute("dateS", DateUtil.getYear()+"-01-01");
		map.addAttribute("dateE", DateUtil.getToday10());
		return "b2b/report/hospitaltradejbgoods/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> page(String name,PageRequest pageable,@CurrentUser User user){
		if(user.getOrganization().getOrgType() == 1){
			Map<String,Object> query = pageable.getQuery();
			if (query == null) {
				query = new HashMap<String, Object>();
			}
			Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			query.put("o1#hospitalCode_S_EQ", hospital.getCode());
		}
		DataGrid<Map<String, Object>> page =  purchaseOrderDetailService.report4(user.getProjectCode(), name,pageable);
		page.addFooter("HOSPITALNAME", "SUM", "SBSUM", "JBSUM", "FJBSUM");
		return page;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/export")
	public void export(String name,PageRequest pageable,@CurrentUser User user ,HttpServletResponse resp){
		DataGrid<Map<String, Object>> page =  purchaseOrderDetailService.report4(user.getProjectCode(), name,pageable);
		List<Map<String, Object>> list = new ArrayList<>();
		list.addAll(page.getRows());
		if (page.getTotal()>pageable.getPageSize()) {
			int pageNo = (int)page.getTotal()/pageable.getPageSize();
			if (page.getTotal()%pageable.getPageSize()!=0) {
				pageNo++;
			}
			for (int i=2;i<=pageNo;i++) {
				pageable.setPage(i);
				page =  purchaseOrderDetailService.report4(user.getProjectCode(), name, pageable);
				if (page.getRows().size() >0) {
					list.addAll(page.getRows());
				}
			}
		}
		String[] beannames = {"HOSPITALNAME","SUM","SBSUM","JBSUM","FJBSUM"};
		String[] heanders = {"医疗机构","采购总金额（元）","社保药物采购金额（元）","基本药物采购金额（元）","非基本药物采购金额（元）"};
		
		OutputStream out = null;
		Workbook wb = null;
	 	try {
	 		ExcelUtil util = new ExcelUtil(heanders, beannames);
	 		DecimalFormat df=new DecimalFormat("######0.00");
	 		BigDecimal sumGather = new BigDecimal(0d); //总金额汇总
	 		BigDecimal sbsumGather = new BigDecimal(0d);//社保金额汇总
	 		BigDecimal jbsumGather = new BigDecimal(0d); //基本药物金额汇总
	 		BigDecimal fjbsumGather = new BigDecimal(0d); //非基本药物金额汇总
	 		for (Map<String, Object> l : list) {
	 			sumGather=sumGather.add((BigDecimal)l.get("SUM"));
	 			sbsumGather=sbsumGather.add((BigDecimal)l.get("SBSUM"));
	 			jbsumGather=jbsumGather.add((BigDecimal)l.get("JBSUM"));
	 			fjbsumGather=fjbsumGather.add((BigDecimal)l.get("FJBSUM"));
			}
	 		Map<String, Object> map = new HashMap<String, Object>();
	 		map.put("HOSPITALNAME", "合计");
	 		
	 		map.put("SUM", df.format(sumGather.doubleValue()));
	 		map.put("SBSUM",df.format(sbsumGather.doubleValue()));
	 		map.put("JBSUM", df.format(jbsumGather.doubleValue()));
	 		map.put("FJBSUM", df.format(fjbsumGather.doubleValue()));
	 		list.add(map);
			 wb = util.doExportXLS(list, "医疗结构采购金额", false, true);
			 
			 resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			 resp.setHeader("Content-Disposition", "attachment; filename=hospitaltradejbgoods.xls");
			 out = resp.getOutputStream();
			 wb.write(out);
			 out.flush();
	 	} catch (IOException e) {
	 		e.printStackTrace();
	 	}  finally {
			if (wb!=null) {
		 		try {
					wb.close();
				} catch (IOException e) {
				}
			}
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@RequestMapping(value="/chart", method = RequestMethod.GET)
	public String chart(String name,String dateS, String dateE,Model model){
		model.addAttribute("name", name);
		model.addAttribute("dateS", dateS);
		model.addAttribute("dateE", dateE);
		return "b2b/report/hospitaltradejbgoods/chart";
	}

	@RequestMapping(value="/chart2", method = RequestMethod.GET)
	public String chart2(String rows, Model model){
		System.out.println("------rows-----"+rows);
		model.addAttribute("rows", rows);
		return "b2b/report/hospitaltradejbgoods/chart2";
	}
	
	@RequestMapping(value="/chart", method = RequestMethod.POST)
	@ResponseBody
	public Message Chart(String name,String dateS, String dateE,@CurrentUser User user){
		System.out.println("------name-----"+name);
		Message message = new Message();
		try {
			List<Map<String, Object>> list = purchaseOrderDetailService.chart4(user.getProjectCode(), name, dateS, dateE);
			
			message.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return message;
	}
}
