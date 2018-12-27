package com.shyl.msc.supervise.controller;
import java.io.IOException;
import java.io.OutputStream;
/*
 * 门诊药品使用分析
 */
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IClinicAnalysisService;
import com.shyl.sys.entity.User;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/supervise/clinicMedicineAnalysis")
public class ClinicMedicineAnalysisController extends BaseController{
	
	@Resource
	private IClinicAnalysisService clinicAnalysisService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IAttributeItemService attributeItemService;

	@Override
	protected void init(WebDataBinder arg0) {	
	}
	
	@RequestMapping(value="" ,method=RequestMethod.GET)
	public String index(ModelMap model, Integer queryType,String jsonStr, Integer clinicType, @CurrentUser User user){

		queryType = (queryType == null ?0:queryType);
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
			obj.put("query['a#clinicType_L_EQ']", clinicType);
			model.addAttribute("isFirst", 1);
		}
		try {
			model.addAttribute("jsonStr", URLEncoder.encode(obj.toString(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		model.addAttribute("queryType", queryType);
		model.addAttribute("clinicType", clinicType);
		return "/supervise/clinicMedicineAnalysis/index";
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String seach(ModelMap model,@CurrentUser User user) {
		model.addAttribute("treePath", this.getTreePath(user)[0]);
		model.addAttribute("orgType", user.getOrganization().getOrgType());
		model.addAttribute("searchType", 1);
		return "/supervise/clinicMedicineAnalysis/search";
	}
	
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(ModelMap model,@CurrentUser User user) {
		return "/supervise/clinicMedicineAnalysis/chart";
	}
	
	@RequestMapping(value="page",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user,ModelMap model)  throws Exception {
		queryType = (queryType == null ?0:queryType);
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
		
		if (queryType>4) {
			query.remove("z#treePath_S_RLK");
		}
		
		return this.getData(user, page, queryType);
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
			System.out.println("======================连接数据源："+user.getProjectCode()+"_SUP");
			data = clinicAnalysisService.groupByColumn(user.getProjectCode()+"_SUP",page, column);
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
		int connectHosCount = 0;
		for (Map<String, Object> res : data) {
			if (res.size() > 0) {
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
			List<Map<String, Object>> list = clinicAnalysisService.groupByColumn(user.getProjectCode()+"_SUP",pageTmp, "A.HOSPITALCODE").getRows();

			for (Map<String, Object> map : list) {
				Map<String, Future> fmap = new HashMap<>();
				System.out.println("连接数据源：sup_"+map.get("HOSPITALCODE")+ ++connectHosCount);
				pageTmp.getQuery().put("A#HOSPITALCODE_S_EQ", map.get("HOSPITALCODE"));
				clinicAnalysisService.getVisitorNum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future0", RpcContext.getContext().getFuture());
				clinicAnalysisService.getRegRecipeNum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future1", RpcContext.getContext().getFuture());
				clinicAnalysisService.getDrugNum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future2", RpcContext.getContext().getFuture());
				clinicAnalysisService.getDrugSum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future3", RpcContext.getContext().getFuture());
				clinicAnalysisService.getSum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future4", RpcContext.getContext().getFuture());

				System.out.println("HOSPITALCODE0:"+map.get("HOSPITALCODE"));
				hospitals.put((String)map.get("HOSPITALCODE"), fmap);
			}
			System.out.println("医院len:"+hospitals.size());

			res.put("hospitals", hospitals);
		}
		long date1 = new Date().getTime();
		System.out.println("执行时间x："+(date1-date0)/1000+"秒" + "连接数据源次数 "+connectHosCount);
		for (Map<String, Object> res : data) {
			Map<String, Map<String, Future>> hospitals = (Map<String, Map<String, Future>>)res.get("hospitals");
			Iterator<String> keys = hospitals.keySet().iterator();
			BigDecimal visitornum = new BigDecimal(0d);
			BigDecimal regrecipenum = new BigDecimal(0d);
			BigDecimal drugnum = new BigDecimal(0d);
			BigDecimal drugsum = new BigDecimal(0d);
			BigDecimal sum = new BigDecimal(0d);
			while (keys.hasNext()) {
				String key = keys.next();
				Map<String, Future> fmap = hospitals.get(key);
				BigDecimal visitornum0 = ((Future<BigDecimal>)fmap.get("Future0")).get();
				BigDecimal regrecipenum0 = ((Future<BigDecimal>)fmap.get("Future1")).get();
				BigDecimal drugnum0 = ((Future<BigDecimal>)fmap.get("Future2")).get();
				BigDecimal drugsum0 = ((Future<BigDecimal>)fmap.get("Future3")).get();
				BigDecimal sum0 = ((Future<BigDecimal>)fmap.get("Future4")).get();
				if (visitornum0 == null) {
					visitornum0 = new BigDecimal(0d);
				}
				if (regrecipenum0 == null) {
					regrecipenum0 = new BigDecimal(0d);
				}
				if (drugnum0 == null) {
					drugnum0 = new BigDecimal(0d);
				}
				if (drugsum0 == null) {
					drugsum0 = new BigDecimal(0d);
				}
				if (sum0 == null) {
					sum0 = new BigDecimal(0d);
				}
				visitornum = visitornum.add(visitornum0);
				regrecipenum = regrecipenum.add(regrecipenum0);
				drugnum = drugnum.add(drugnum0);
				drugsum = drugsum.add(drugsum0);
				sum = sum.add(sum0);
			}
			res.put("VISITORNUM", visitornum);
			res.put("REGRECIPENUM", regrecipenum);
			res.put("DRUGNUM", drugnum);
			res.put("DRUGSUM", drugsum);
			res.put("SUM", sum);
			if (new BigDecimal(0).equals(visitornum)) {
				res.put("AVGDRUGNUM", 0.00d);
				res.put("AVGDRUGSUM", 0.00d);
				res.put("RECIPERATE", 0.00d);
			} else {
				res.put("AVGDRUGNUM", drugnum.divide(visitornum, 2 , BigDecimal.ROUND_HALF_UP));
				res.put("AVGDRUGSUM", drugsum.divide(visitornum, 2 , BigDecimal.ROUND_HALF_UP));
				res.put("RECIPERATE", regrecipenum.divide(visitornum, 4 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
			}
			
			if (new BigDecimal(0).equals(sum)) {
				res.put("DRUGRATIO", 0.00d);
			} else {
				res.put("DRUGRATIO", drugsum.divide(sum, 5 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
			}
			res.remove("hospitals");
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
	

	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, @CurrentUser User user, HttpServletResponse resp){
		queryType = (queryType == null ?0:queryType);
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
		
		if (queryType>4) {
			query.remove("z#treePath_S_RLK");
		}
		
		try{
			page.setPageSize(10000);
			//查询数据
			DataGrid<Map<String, Object>> data = this.getData(user, page, queryType);
			String[] headers = null;
			String[] beanNames = null;
			
			switch(queryType){
				case 1:
					headers = new String[]{"PROVINCENAME","VISITORNUM","DRUGNUM","AVGDRUGNUM","DRUGSUM","AVGDRUGSUM","SUM","DRUGRATIO","REGRECIPENUM","RECIPERATE"};
					beanNames = new String[]{"省名称","总就诊人次","药品品规数","每就诊人均品规数","药品费用","每就诊人次药品费用","总收入","药品收入比(%)","处方人次","处方率(%)"};
					break;
				case 2:
					headers = new String[]{"CITYNAME","VISITORNUM","DRUGNUM","AVGDRUGNUM","DRUGSUM","AVGDRUGSUM","SUM","DRUGRATIO","REGRECIPENUM","RECIPERATE"};
					beanNames = new String[]{"市名称","总就诊人次","药品品规数","每就诊人均品规数","药品费用","每就诊人次药品费用","总收入","药品收入比(%)","处方人次","处方率(%)"};
					break;
				case 3:
					headers = new String[]{"COUNTYNAME","VISITORNUM","DRUGNUM","AVGDRUGNUM","DRUGSUM","AVGDRUGSUM","SUM","DRUGRATIO","REGRECIPENUM","RECIPERATE"};
					beanNames = new String[]{"区名称","总就诊人次","药品品规数","每就诊人均品规数","药品费用","每就诊人次药品费用","总收入","药品收入比(%)","处方人次","处方率(%)"};
					break;
				case 4:
					headers = new String[]{"HOSPITALNAME","VISITORNUM","DRUGNUM","AVGDRUGNUM","DRUGSUM","AVGDRUGSUM","SUM","DRUGRATIO","REGRECIPENUM","RECIPERATE"};
					beanNames = new String[]{"医院名称","总就诊人次","药品品规数","每就诊人均品规数","药品费用","每就诊人次药品费用","总收入","药品收入比(%)","处方人次","处方率(%)"};
					break;
				case 5:
					headers = new String[]{"HOSPITALNAME","DEPARTNAME","VISITORNUM","DRUGNUM","AVGDRUGNUM","DRUGSUM","AVGDRUGSUM","SUM","DRUGRATIO","REGRECIPENUM","RECIPERATE"};
					beanNames = new String[]{"医院名称","科室名称","总就诊人次","药品品规数","每就诊人均品规数","药品费用","每就诊人次药品费用","总收入","药品收入比(%)","处方人次","处方率(%)"};
					break;
				case 6:
					headers = new String[]{"HOSPITALNAME","DEPARTNAME","DOCTORNAME","VISITORNUM","DRUGNUM","AVGDRUGNUM","DRUGSUM","AVGDRUGSUM","SUM","DRUGRATIO","REGRECIPENUM","RECIPERATE"};
					beanNames = new String[]{"医院名称","科室名称","医生名称","总就诊人次","药品品规数","每就诊人均品规数","药品费用","每就诊人次药品费用","总收入","药品收入比(%)","处方人次","处方率(%)"};
					break;
			}
			ExcelUtil util = new ExcelUtil(beanNames, headers);
			Workbook workbook = util.doExportXLS(data.getRows(), "门诊药品使用分析", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=DrugAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		}catch(Exception e){
			
		}
	}
}
