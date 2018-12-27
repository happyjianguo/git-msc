package com.shyl.msc.supervise.controller;
/*
 * 联合用药分析
 */
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
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
import com.shyl.msc.dm.entity.DrugType;
import com.shyl.msc.dm.service.IDrugTypeService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IClinicAnalysisService;
import com.shyl.msc.supervise.service.IHisAnalysisService;
import com.shyl.sys.entity.User;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/supervise/combDrugAnalysis")
public class CombDrugAnalysisController extends BaseController{
	
	@Resource
	private IClinicAnalysisService clinicAnalysisService;
	@Resource
	private IHisAnalysisService	hisAnalysisService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private IDrugTypeService drugTypeService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Override
	protected void init(WebDataBinder arg0) {	
	}
	
	@RequestMapping(value="" ,method=RequestMethod.GET)
	public String index(ModelMap model, Integer queryType,String jsonStr,@CurrentUser User user){
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
			obj.put("query['month_S_GE']", month);
			obj.put("query['month_S_LE']", month);
			obj.put("queryType", queryType);
			model.addAttribute("isFirst", 1);
		}
		try {
			model.addAttribute("jsonStr", URLEncoder.encode(obj.toString(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		model.addAttribute("queryType", queryType);
		return "/supervise/combDrugAnalysis/index";
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String seach(ModelMap model,@CurrentUser User user) {
		model.addAttribute("treePath", this.getTreePath(user)[0]);
		model.addAttribute("orgType", user.getOrganization().getOrgType());
		model.addAttribute("searchType", 1);
		return "/supervise/combDrugAnalysis/search";
	}
	
	@RequestMapping(value="/clinicRecipe", method=RequestMethod.GET)
	public String index(ModelMap model, String jsonStr, @CurrentUser User user) {
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/combDrugAnalysis/clinicRecipe";
	}
	
	@RequestMapping(value="page",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, Integer queryType,Integer orderType, @CurrentUser User user,ModelMap model)  throws Exception{
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		//如果是医院，删选医院数据
		if(user.getOrganization().getOrgType() == 1){
			query.put("hospitalCode_S_EQ", user.getOrganization().getOrgCode());
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
	
	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public String detail(ModelMap model, String hospitalCode, String outSno, @CurrentUser User user) {
		model.addAttribute("outSno",outSno);
		model.addAttribute("hospitalCode",hospitalCode);
		return "/supervise/combDrugAnalysis/detail";
	}
	
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(ModelMap model,@CurrentUser User user) {
		return "/supervise/combDrugAnalysis/chart";
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType,@CurrentUser User user, HttpServletResponse resp) throws Exception{
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		//如果是医院，删选医院数据
		if(user.getOrganization().getOrgType() == 1){
			query.put("hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		} else {
			String treePath0 = (String) query.get("treePath_S_RLK");
			String treePath = getTreePath(user)[0];

			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("z#treePath_S_RLK", treePath);
			}
		}
		
		if (queryType>4) {
			query.remove("treePath_S_RLK");
		}
		try{
			page.setPageSize(10000);
			//查询数据
			DataGrid<Map<String, Object>> data = this.getData(user, page, queryType);
			String[] headers = null;
			String[] beanNames = null;
			
			switch(queryType){
				case 0:
					headers = new String[]{"ZONENAME","RECIPENUM","RECIPERATIO","COMBINEDNUM1","COMBINEDRATIO1","COMBINEDNUM2","COMBINEDRATIO2","COMBINEDNUM3","COMBINEDRATIO3","COMBINEDNUM4","COMBINEDRATIO4","COMBINEDNUM5","COMBINEDRATIO5","COMBINEDNUM6","COMBINEDRATIO6"};
					beanNames = new String[]{"区域","总处方数","处方占比(%)","一联药物处方数","一联药物处方数占比(%)","二联药物处方数","二联药物处方数占比(%)","三联药物处方数","三联药物处方数占比(%)","四联药物处方数","四联药物处方数占比(%)","五联药物处方数","五联药物处方数占比(%)","六联药物处方数","六联药物处方数占比(%)"};
					break;
				case 1:
					headers = new String[]{"PROVINCENAME","RECIPENUM","RECIPERATIO","COMBINEDNUM1","COMBINEDRATIO1","COMBINEDNUM2","COMBINEDRATIO2","COMBINEDNUM3","COMBINEDRATIO3","COMBINEDNUM4","COMBINEDRATIO4","COMBINEDNUM5","COMBINEDRATIO5","COMBINEDNUM6","COMBINEDRATIO6"};
					beanNames = new String[]{"省名称","总处方数","处方占比(%)","一联药物处方数","一联药物处方数占比(%)","二联药物处方数","二联药物处方数占比(%)","三联药物处方数","三联药物处方数占比(%)","四联药物处方数","四联药物处方数占比(%)","五联药物处方数","五联药物处方数占比(%)","六联药物处方数","六联药物处方数占比(%)"};
					break;
				case 2:
					headers = new String[]{"CITYNAME","RECIPENUM","RECIPERATIO","COMBINEDNUM1","COMBINEDRATIO1","COMBINEDNUM2","COMBINEDRATIO2","COMBINEDNUM3","COMBINEDRATIO3","COMBINEDNUM4","COMBINEDRATIO4","COMBINEDNUM5","COMBINEDRATIO5","COMBINEDNUM6","COMBINEDRATIO6"};
					beanNames = new String[]{"市名称","总处方数","处方占比(%)","一联药物处方数","一联药物处方数占比(%)","二联药物处方数","二联药物处方数占比(%)","三联药物处方数","三联药物处方数占比(%)","四联药物处方数","四联药物处方数占比(%)","五联药物处方数","五联药物处方数占比(%)","六联药物处方数","六联药物处方数占比(%)"};
					break;
				case 3:
					headers = new String[]{"COUNTYNAME","RECIPENUM","RECIPERATIO","COMBINEDNUM1","COMBINEDRATIO1","COMBINEDNUM2","COMBINEDRATIO2","COMBINEDNUM3","COMBINEDRATIO3","COMBINEDNUM4","COMBINEDRATIO4","COMBINEDNUM5","COMBINEDRATIO5","COMBINEDNUM6","COMBINEDRATIO6"};
					beanNames = new String[]{"区名称","总处方数","处方占比(%)","一联药物处方数","一联药物处方数占比(%)","二联药物处方数","二联药物处方数占比(%)","三联药物处方数","三联药物处方数占比(%)","四联药物处方数","四联药物处方数占比(%)","五联药物处方数","五联药物处方数占比(%)","六联药物处方数","六联药物处方数占比(%)"};
					break;
				case 4:
					headers = new String[]{"HOSPITALNAME","RECIPENUM","RECIPERATIO","COMBINEDNUM1","COMBINEDRATIO1","COMBINEDNUM2","COMBINEDRATIO2","COMBINEDNUM3","COMBINEDRATIO3","COMBINEDNUM4","COMBINEDRATIO4","COMBINEDNUM5","COMBINEDRATIO5","COMBINEDNUM6","COMBINEDRATIO6"};
					beanNames = new String[]{"医院名称","总处方数","处方占比(%)","一联药物处方数","一联药物处方数占比(%)","二联药物处方数","二联药物处方数占比(%)","三联药物处方数","三联药物处方数占比(%)","四联药物处方数","四联药物处方数占比(%)","五联药物处方数","五联药物处方数占比(%)","六联药物处方数","六联药物处方数占比(%)"};
					break;
				case 5:
					headers = new String[]{"HOSPITALNAME","DEPARTNAME","RECIPENUM","RECIPERATIO","COMBINEDNUM1","COMBINEDRATIO1","COMBINEDNUM2","COMBINEDRATIO2","COMBINEDNUM3","COMBINEDRATIO3","COMBINEDNUM4","COMBINEDRATIO4","COMBINEDNUM5","COMBINEDRATIO5","COMBINEDNUM6","COMBINEDRATIO6"};
					beanNames = new String[]{"医院名称","科室名称","总处方数","处方占比(%)","一联药物处方数","一联药物处方数占比(%)","二联药物处方数","二联药物处方数占比(%)","三联药物处方数","三联药物处方数占比(%)","四联药物处方数","四联药物处方数占比(%)","五联药物处方数","五联药物处方数占比(%)","六联药物处方数","六联药物处方数占比(%)"};
					break;
				case 6:
					headers = new String[]{"HOSPITALNAME","DEPARTNAME","DOCTORNAME","RECIPENUM","RECIPERATIO","COMBINEDNUM1","COMBINEDRATIO1","COMBINEDNUM2","COMBINEDRATIO2","COMBINEDNUM3","COMBINEDRATIO3","COMBINEDNUM4","COMBINEDRATIO4","COMBINEDNUM5","COMBINEDRATIO5","COMBINEDNUM6","COMBINEDRATIO6"};
					beanNames = new String[]{"医院名称","科室名称","医生名称","总处方数","处方占比(%)","一联药物处方数","一联药物处方数占比(%)","二联药物处方数","二联药物处方数占比(%)","三联药物处方数","三联药物处方数占比(%)","四联药物处方数","四联药物处方数占比(%)","五联药物处方数","五联药物处方数占比(%)","六联药物处方数","六联药物处方数占比(%)"};
					break;
				default :
					return;
			}
			ExcelUtil util = new ExcelUtil(beanNames, headers);
		
			Workbook workbook = util.doExportXLS(data.getRows(), "联合用药分析", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=DrugAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		}catch(IOException e){
			
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
			System.out.println("======================连接数据源："+user.getProjectCode()+"_SUP");
			data = clinicAnalysisService.groupByColumn(user.getProjectCode()+"_SUP",page, column);
		}
		//分析查询
		analysis(user, data.getRows(), page, column, type);
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public void analysis(User user, List<Map<String, Object>> data, PageRequest page, String column, Integer type)  throws Exception {
		page.setPageSize(1000);
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

			List<Map<String, Object>> list = clinicAnalysisService.groupByColumn(user.getProjectCode()+"_SUP",pageTmp, "A.HOSPITALCODE").getRows();

			for (Map<String, Object> map : list) {
				Map<String, Future> fmap = new HashMap<>();
				System.out.println("连接数据源：sup_"+map.get("HOSPITALCODE"));
				pageTmp.getQuery().put("A#HOSPITALCODE_S_EQ", map.get("HOSPITALCODE"));

				clinicAnalysisService.getRecipeNum(user.getProjectCode()+"_SUP", pageTmp);
				fmap.put("Future0", RpcContext.getContext().getFuture());
				//门诊分库处方
				clinicAnalysisService.getCombDurgNum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future1", RpcContext.getContext().getFuture());
				System.out.println("HOSPITALCODE0:"+map.get("HOSPITALCODE"));

				
				System.out.println("HOSPITALCODE0:"+map.get("HOSPITALCODE"));
				hospitals.put((String)map.get("HOSPITALCODE"), fmap);
			}

			res.put("hospitals", hospitals);
		}
		for (Map<String, Object> res : data) {
			Map<String, Map<String, Future>> hospitals = (Map<String, Map<String, Future>>)res.get("hospitals");
			Iterator<String> keys = hospitals.keySet().iterator();
			BigDecimal clinicRecipeNum = new BigDecimal(0d);
			BigDecimal clinicCombinedNum1 = new BigDecimal(0d);
			BigDecimal clinicCombinedNum2 = new BigDecimal(0d);
			BigDecimal clinicCombinedNum3 = new BigDecimal(0d);
			BigDecimal clinicCombinedNum4 = new BigDecimal(0d);
			BigDecimal clinicCombinedNum5 = new BigDecimal(0d);
			BigDecimal clinicCombinedNum6 = new BigDecimal(0d);
			BigDecimal allClinicRecipeNum = new BigDecimal(0d);
			BigDecimal allClinicRecipeNuma =new BigDecimal(0d);
			while (keys.hasNext()) {
				String key = keys.next();
				Map<String, Future> fmap = hospitals.get(key);
				allClinicRecipeNuma =((Future<BigDecimal>)fmap.get("Future0")).get();
				List<Map<String, Object>>	clinicData = ((Future<List<Map<String, Object>>>)fmap.get("Future1")).get();
				BigDecimal clinicRecipeNuma=new BigDecimal(0d);
				BigDecimal clinicCombinedNum1a=new BigDecimal(0d);
				BigDecimal clinicCombinedNum2a=new BigDecimal(0d);
				BigDecimal clinicCombinedNum3a=new BigDecimal(0d);
				BigDecimal clinicCombinedNum4a=new BigDecimal(0d);
				BigDecimal clinicCombinedNum5a=new BigDecimal(0d);
				BigDecimal clinicCombinedNum6a=new BigDecimal(0d);
				if(clinicData!=null){
					if(clinicData.size()>0){
						clinicRecipeNuma=(BigDecimal)clinicData.get(0).get("RECIPENUM");
						clinicCombinedNum1a=(BigDecimal)clinicData.get(0).get("COMBINEDNUM1");
						clinicCombinedNum2a=(BigDecimal)clinicData.get(0).get("COMBINEDNUM2");
						clinicCombinedNum3a=(BigDecimal)clinicData.get(0).get("COMBINEDNUM3");
						clinicCombinedNum4a=(BigDecimal)clinicData.get(0).get("COMBINEDNUM4");
						clinicCombinedNum5a=(BigDecimal)clinicData.get(0).get("COMBINEDNUM5");
						clinicCombinedNum6a=(BigDecimal)clinicData.get(0).get("COMBINEDNUM6");
					}
				}
				if(clinicRecipeNuma!=null&&clinicCombinedNum1a!=null&&clinicCombinedNum2a!=null&&clinicCombinedNum3a!=null&&clinicCombinedNum4a!=null&&clinicCombinedNum5a!=null&&clinicCombinedNum6a!=null){
					clinicRecipeNum = clinicRecipeNum.add(clinicRecipeNuma);
					clinicCombinedNum1 = clinicCombinedNum1.add(clinicCombinedNum1a);
					clinicCombinedNum2 = clinicCombinedNum1.add(clinicCombinedNum2a);
					clinicCombinedNum3 = clinicCombinedNum1.add(clinicCombinedNum3a);
					clinicCombinedNum4 = clinicCombinedNum1.add(clinicCombinedNum4a);
					clinicCombinedNum5 = clinicCombinedNum1.add(clinicCombinedNum5a);
					clinicCombinedNum6 = clinicCombinedNum1.add(clinicCombinedNum6a);
				}
				if(allClinicRecipeNuma!=null){
					allClinicRecipeNum=allClinicRecipeNum.add(allClinicRecipeNuma);
				}
				
			}
			res.put("RECIPENUM", clinicRecipeNum);
			res.put("COMBINEDNUM1", clinicCombinedNum1);
			res.put("COMBINEDNUM2", clinicCombinedNum2);
			res.put("COMBINEDNUM3", clinicCombinedNum3);
			res.put("COMBINEDNUM4", clinicCombinedNum4);
			res.put("COMBINEDNUM5", clinicCombinedNum5);
			res.put("COMBINEDNUM6", clinicCombinedNum6);
			if(new BigDecimal(0).equals(allClinicRecipeNum)){
				res.put("RECIPERATIO", 0.00d);
				res.put("COMBINEDRATIO1", 0.00d);
				res.put("COMBINEDRATIO2", 0.00d);
				res.put("COMBINEDRATIO3", 0.00d);
				res.put("COMBINEDRATIO4", 0.00d);
				res.put("COMBINEDRATIO5", 0.00d);
				res.put("COMBINEDRATIO6", 0.00d);
			}else{
				res.put("RECIPERATIO", clinicRecipeNum.divide(allClinicRecipeNum, 5 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
				res.put("COMBINEDRATIO1", clinicCombinedNum1.divide(allClinicRecipeNum, 5 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
				res.put("COMBINEDRATIO2", clinicCombinedNum2.divide(allClinicRecipeNum, 5 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
				res.put("COMBINEDRATIO3", clinicCombinedNum3.divide(allClinicRecipeNum, 5 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
				res.put("COMBINEDRATIO4", clinicCombinedNum4.divide(allClinicRecipeNum, 5 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
				res.put("COMBINEDRATIO5", clinicCombinedNum5.divide(allClinicRecipeNum, 5 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
				res.put("COMBINEDRATIO6", clinicCombinedNum6.divide(allClinicRecipeNum, 5 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100d)));
			}
			res.remove("hospitals");
		}
	}
}
	
