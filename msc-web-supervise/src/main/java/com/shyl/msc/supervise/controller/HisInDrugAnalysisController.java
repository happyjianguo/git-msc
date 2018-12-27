package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.rpc.RpcContext;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IHisAnalysisService;
import com.shyl.sys.entity.User;

import net.sf.json.JSONObject;

/**
 * 住院药品使用分析
 * 
 * @author CHENJIN
 *
 */
@Controller
@RequestMapping("/supervise/hisInDrugAnalysis")
public class HisInDrugAnalysisController extends BaseController {
	@Resource
	private IHisAnalysisService hisAnalysisService;

	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;

	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model, Integer queryType, String jsonStr, @CurrentUser User user) {
		queryType = (queryType == null ? 0 : queryType);
		// 如果是卫计委，过滤掉通行的地址
		if(user.getOrganization().getOrgType() == 3) {
			String[] info = this.getTreePath(user);
			int minType = info[0].split(",").length;
			model.addAttribute("zoneName", info[1]);
			if (queryType<minType) {
				queryType = minType;
			}
			model.addAttribute("treePath", info[0]);
		} else if(user.getOrganization().getOrgType() == 1){
			if(StringUtils.isEmpty(jsonStr)){
				queryType=4;
			}	
		}
		JSONObject obj = new JSONObject();
		if (jsonStr!=null) {
			obj = JSONObject.fromObject(jsonStr);
		}
		if (obj.size() == 0) {
			String month = DateUtil.getDateAfterMonths(new Date(), 1,"yyyy-MM");
			obj.put("query['a#month_S_GE']", month);
			obj.put("query['a#month_S_LE']", month);
			obj.put("queryType", queryType);
			model.addAttribute("isFirst", 1);
		}
		try {
			model.addAttribute("jsonStr", URLEncoder.encode(obj.toString(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		model.addAttribute("queryType", queryType);
		return "/supervise/hisInDrugAnalysis/index";
	}

	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public String chart(ModelMap model, @CurrentUser User user) {
		return "/supervise/hisInDrugAnalysis/chart";
	}
	
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String seach(ModelMap model,@CurrentUser User user) {
		model.addAttribute("treePath", this.getTreePath(user)[0]);
		model.addAttribute("orgType", user.getOrganization().getOrgType());
		model.addAttribute("searchType", 1);
		return "/supervise/hisInDrugAnalysis/search";
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> query(PageRequest page, Integer queryType, @CurrentUser User user) throws Exception{
		queryType = (queryType == null ? 0 : queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		//如果是医院，删选医院数据
		if(user.getOrganization().getOrgType() == 1){
			query.put("z#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		} else {
			String treePath0 = (String) query.get("z#treePath_S_RLK");
			String treePath = getTreePath(user)[0];

			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("z#treePath_S_RLK", treePath);
			}
		}
		if (queryType > 4) {
			query.remove("z#treePath_S_RLK");
		}
		return this.getData(user, page, queryType);
	}

	// 导出
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user,
			HttpServletResponse resp){
		queryType = (queryType == null ? 0 : queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		//如果是医院，删选医院数据
		if(user.getOrganization().getOrgType() == 1){
			query.put("z#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		} else {
			String treePath0 = (String) query.get("z#treePath_S_RLK");
			String treePath = getTreePath(user)[0];

			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("z#treePath_S_RLK", treePath);
			}
		}
		if (queryType > 4) {
			query.remove("z#treePath_S_RLK");
		}
		try {
			page.setPageSize(10000);
			String[] headers = null;
			String[] beanNames = null;
			ExcelUtil util = new ExcelUtil(headers, beanNames);
			DataGrid<Map<String, Object>> data = this.getData(user, page, queryType);
			switch (queryType) {
			case 0:
				beanNames = new String[] {  "SUM","AVGSUM","DRUGSUM","RATEDRUGSUM","OUTNUM","AVGOUTDRUGSUM", "DAYSUM",  "AVGDAYDRUGSUM", "DRUGNUM","AVGDRUGNUM" };
				headers = new String[] {  "住院总费用","每住院人均费用","住院药品总费用 ","药品费用占比（%）","出院人次","每出院人次药品费用", "住院床日数","每住院床日药品费用","住院药品品种数","每住院人均品种数" };
				break;
			
			case 1:
				beanNames = new String[] { "PROVINCENAME", "SUM","AVGSUM","DRUGSUM","RATEDRUGSUM","OUTNUM","AVGOUTDRUGSUM", "DAYSUM",  "AVGDAYDRUGSUM", "DRUGNUM","AVGDRUGNUM" };
				headers = new String[] { "省名称", "住院总费用","每住院人均费用","住院药品总费用 ","药品费用占比（%）","出院人次","每出院人次药品费用", "住院床日数","每住院床日药品费用","住院药品品种数","每住院人均品种数" };
				break;
			case 2:
				beanNames = new String[] { "CITYNAME", "SUM","AVGSUM","DRUGSUM","RATEDRUGSUM","OUTNUM","AVGOUTDRUGSUM", "DAYSUM",  "AVGDAYDRUGSUM", "DRUGNUM","AVGDRUGNUM" };
				headers = new String[] { "市名称", "住院总费用","每住院人均费用","住院药品总费用 ","药品费用占比（%）","出院人次","每出院人次药品费用", "住院床日数","每住院床日药品费用","住院药品品种数","每住院人均品种数" };
				break;
			case 3:
				beanNames = new String[] { "COUNTYNAME", "SUM","AVGSUM","DRUGSUM","RATEDRUGSUM","OUTNUM","AVGOUTDRUGSUM", "DAYSUM",  "AVGDAYDRUGSUM", "DRUGNUM","AVGDRUGNUM" };
				headers = new String[] { "区名称", "住院总费用","每住院人均费用","住院药品总费用 ","药品费用占比（%）","出院人次","每出院人次药品费用", "住院床日数","每住院床日药品费用","住院药品品种数","每住院人均品种数" };
				break;
			case 4:
				beanNames = new String[] { "HOSPITALNAME", "SUM","AVGSUM","DRUGSUM","RATEDRUGSUM","OUTNUM","AVGOUTDRUGSUM", "DAYSUM",  "AVGDAYDRUGSUM", "DRUGNUM","AVGDRUGNUM" };
				headers = new String[] { "医院名称", "住院总费用","每住院人均费用","住院药品总费用 ","药品费用占比（%）","出院人次","每出院人次药品费用", "住院床日数","每住院床日药品费用","住院药品品种数","每住院人均品种数" };
				break;
			case 5:
				beanNames = new String[] { "HOSPITALNAME","DEPARTNAME", "SUM","AVGSUM","DRUGSUM","RATEDRUGSUM","OUTNUM","AVGOUTDRUGSUM", "DAYSUM",  "AVGDAYDRUGSUM", "DRUGNUM","AVGDRUGNUM" };
				headers = new String[] { "医院名称","科室名称", "住院总费用","每住院人均费用","住院药品总费用 ","药品费用占比（%）","出院人次","每出院人次药品费用", "住院床日数","每住院床日药品费用","住院药品品种数","每住院人均品种数" };
				break;
			case 6:
				beanNames = new String[] { "HOSPITALNAME","DEPARTNAME","DOCTORNAME", "SUM","AVGSUM","DRUGSUM","RATEDRUGSUM","OUTNUM","AVGOUTDRUGSUM", "DAYSUM",  "AVGDAYDRUGSUM", "DRUGNUM","AVGDRUGNUM" };
				headers = new String[] { "医院名称","科室名称","医生名称", "住院总费用","每住院人均费用","住院药品总费用 ","药品费用占比（%）","出院人次","每出院人次药品费用", "住院床日数","每住院床日药品费用","住院药品品种数","每住院人均品种数" };
				break;
			default:
				return;
			}
			Workbook workbook = util.doExportXLS(data.getRows(), "住院药品使用分析", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=HisInDrugAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);
			out.flush();
			workbook.close();
			out.close();
		} catch (Exception e) {
		}
			
	}
	
	private String[] getTreePath(User user) {
		RegulationOrg regulationOrg=null;
		RegionCode regionCode=null;
		if (user.getOrganization().getOrgType() == 3) {
			regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),user.getOrganization().getOrgCode());
			regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
		}else if(user.getOrganization().getOrgType() == 1){
			Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
		}
		String treePath = regionCode.getTreePath();
		if (treePath == null) {
			treePath += regionCode.getId().toString();
		} else {
			treePath += "," + regionCode.getId();
		}
		return new String[]{treePath, regionCode.getName()};
	}
	
	public DataGrid<Map<String, Object>> getData(User user,PageRequest page, Integer type) throws Exception {
		String column = "";
		switch (type) {
			case 1:
				column = "Z.PROVINCECODE";
				break;
			case 2:
				column = "Z.CITYCODE";
				break;
			case 3:
				column = "Z.COUNTYCODE";
				break;
			case 4:
				column = "A.HOSPITALCODE";
				break;
			case 5:
				column = "A.DEPARTCODE";
				break;
			case 6:
				column = "A.DOCTORCODE";
				break;
		}
		long date0 = new Date().getTime();
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();
		list.add(map);
		DataGrid<Map<String, Object>> data = new DataGrid<>(list, page, 1);
		if (!"".equals(column)) {
			data = hisAnalysisService.groupByColumn(user.getProjectCode()+"_SUP",page, column);
		}
		long date1 = new Date().getTime();
		System.out.println("执行时间0："+(date1-date0)/1000+"秒,datalength:"+data.getRows().size());
		//分析查询
		analysis(user, data.getRows(), page, column, type);
		date1 = new Date().getTime();
		System.out.println("执行时间1："+(date1-date0)/1000+"秒");
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public void analysis(User user, List<Map<String, Object>> data, PageRequest page, String column, Integer type)  throws Exception {
		page.setPageSize(1000);
		long date0 = new Date().getTime();
		for (Map<String, Object> res : data) {
			if(res.size()>0){
				String[] keys = column.split("\\.");
				page.getQuery().put(keys[0]+"#"+keys[1]+"_S_EQ", res.get(keys[1]));
			}
			Map<String, Map<String, Future>> hospitals = new HashMap<>();
			PageRequest pageTmp = new PageRequest();
			BeanUtils.copyProperties(page, pageTmp);
			pageTmp.setQuery(new HashMap<String, Object>());
			pageTmp.getQuery().putAll(page.getQuery());
			pageTmp.setPage(1);
			pageTmp.setPageSize(1000);
			List<Map<String, Object>> list = hisAnalysisService.groupByColumn(user.getProjectCode()+"_SUP",pageTmp, "A.HOSPITALCODE").getRows();
			for (Map<String, Object> map : list) {
				Map<String, Future> fmap = new HashMap<>();
				pageTmp.getQuery().put("A#HOSPITALCODE_S_EQ", map.get("HOSPITALCODE"));
				hisAnalysisService.getDrugNum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future0", RpcContext.getContext().getFuture());
				hisAnalysisService.getSum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future1", RpcContext.getContext().getFuture());
				hisAnalysisService.getDaySum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future2", RpcContext.getContext().getFuture());
				hisAnalysisService.getDrugSum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future3", RpcContext.getContext().getFuture());
				hisAnalysisService.getOutNum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future4", RpcContext.getContext().getFuture());
				
				System.out.println("HOSPITALCODE0:"+map.get("HOSPITALCODE"));
				hospitals.put((String)map.get("HOSPITALCODE"), fmap);
			}
			res.put("hospitals", hospitals);
		}
		long date1 = new Date().getTime();
		System.out.println("执行时间x："+(date1-date0)/1000+"秒");
		for (Map<String, Object> res : data) {
			Map<String, Map<String, Future>> hospitals = (Map<String, Map<String, Future>>)res.get("hospitals");
			Iterator<String> keys = hospitals.keySet().iterator();
			BigDecimal absNum = new BigDecimal(0d);
			BigDecimal drugNum = new BigDecimal(0d);
			BigDecimal sum = new BigDecimal(0d);
			BigDecimal daySum = new BigDecimal(0d);
			BigDecimal dddSum = new BigDecimal(0d);
			BigDecimal drugSum = new BigDecimal(0d);
			BigDecimal outNum = new BigDecimal(0d);
			while (keys.hasNext()) {
				String key = keys.next();
				Map<String, Future> fmap = hospitals.get(key);
				BigDecimal drugNum0 = ((Future<BigDecimal>)fmap.get("Future0")).get();
				BigDecimal sum0 = ((Future<BigDecimal>)fmap.get("Future1")).get();
				BigDecimal daySum0 = ((Future<BigDecimal>)fmap.get("Future2")).get();
				BigDecimal drugSum0 = ((Future<BigDecimal>)fmap.get("Future3")).get();
				BigDecimal outNum0 = ((Future<BigDecimal>)fmap.get("Future4")).get();
				if(drugNum0==null){
					drugNum0=new BigDecimal(0d);
				}
				if(sum0==null){
					sum0=new BigDecimal(0d);
				}
				if(daySum0==null){
					daySum0=new BigDecimal(0d);
				}
				if(drugSum0==null){
					drugSum0=new BigDecimal(0d);
				}
				if(outNum0==null){
					outNum0=new BigDecimal(0d);
				}
				drugNum=drugNum.add(drugNum0);
				sum=sum.add(sum0);
				daySum=daySum.add(daySum0);
				drugSum=drugSum.add(drugSum0);
				outNum=outNum.add(outNum0);
			}
			res.put("DRUGNUM", drugNum);
			res.put("SUM", sum);
			res.put("DAYSUM", daySum);
			res.put("DDDSUM", dddSum);
			res.put("DRUGSUM", drugSum);
			res.put("OUTNUM", outNum);
			if (new BigDecimal(0).equals(outNum)) {
				res.put("AVGSUM", 0.00d);
				res.put("AVGOUTDRUGSUM", 0.00d);
				res.put("AVGDRUGNUM", 0.00d);
			}else{
				res.put("AVGSUM", sum.divide(outNum, 2 , BigDecimal.ROUND_HALF_UP));
				res.put("AVGOUTDRUGSUM", drugSum.divide(outNum, 2 , BigDecimal.ROUND_HALF_UP));
				res.put("AVGDRUGNUM", drugNum.divide(outNum, 2 , BigDecimal.ROUND_HALF_UP));
			}
			if(new BigDecimal(0).equals(daySum)){
				res.put("AVGDAYDRUGSUM", 0.00d);
			}else{
				res.put("AVGDAYDRUGSUM", drugSum.divide(daySum, 2 , BigDecimal.ROUND_HALF_UP));
			}
			if(new BigDecimal(0).equals(sum)){
				res.put("RATEDRUGSUM", 0.00d);
			}else{
				res.put("RATEDRUGSUM", drugSum.divide(sum, 4 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
			}
			res.remove("hospitals");
		}
	}

}
