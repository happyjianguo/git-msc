package com.shyl.msc.supervise.controller;
/*
 * 大金额处方分析
 */
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
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.entity.Quota;
import com.shyl.msc.supervise.service.IClinicAnalysisService;
import com.shyl.msc.supervise.service.IQuotaService;
import com.shyl.sys.entity.User;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/supervise/largeSumAnalysis")
public class LargeSumAnalysisController extends BaseController{
	@Resource
	private IClinicAnalysisService clinicAnalysisService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	public IQuotaService quotaService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IAttributeItemService attributeItemService;

	@Override
	protected void init(WebDataBinder arg0) {
	}
	
	@RequestMapping(value="", method = RequestMethod.GET)
	public String index(ModelMap model, Integer queryType,String jsonStr, @CurrentUser User user){
		queryType = (queryType == null ?0:queryType);
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
		Quota qu = quotaService.getByCode(user.getProjectCode()+"_SUP","largeSum");
		BigDecimal min = qu.getMin();
		model.addAttribute("queryType", queryType);
		model.addAttribute("minSum", min);
		System.out.println("queryType"+queryType);
		return "/supervise/largeSumAnalysis/index";
	}
	
	@RequestMapping(value="page",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, Integer queryType,Integer orderType, @CurrentUser User user,ModelMap model)throws Exception{
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		/*if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "LARGESUMRECIPE")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "LARGESUMRECIPE")));
		}*/
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
	
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(ModelMap model,@CurrentUser User user) {
		return "/supervise/largeSumAnalysis/chart";
	}
	
	@RequestMapping(value="/clinicRecipe", method=RequestMethod.GET)
	public String index(ModelMap model, String jsonStr, @CurrentUser User user) {
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/largeSumAnalysis/clinicRecipe";
	}
	
	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public String detail(ModelMap model, String hospitalCode, String outSno, @CurrentUser User user) {
		model.addAttribute("outSno",outSno);
		model.addAttribute("hospitalCode",hospitalCode);
		return "/supervise/largeSumAnalysis/detail";
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user, HttpServletResponse resp)throws Exception{
		queryType = (queryType == null ?0:queryType);
		Integer orgType = user.getOrganization().getOrgType();
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		/*if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "LARGESUMRECIPE")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "LARGESUMRECIPE")));
		}*/
		//如果是医院，删选医院数据
		if(user.getOrganization().getOrgType() == 1){
			query.put("z#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		} else {
			String treePath0 = (String) query.get("z#treePath_S_RLK");
			String treePath = getTreePath(user)[0];

			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("z#treePath_S_RLK", treePath);
			}
		}if (queryType>4) {
			query.remove("treePath_S_RLK");
		}
		String[] headers = null;
		String[] beanNames = null;
		try{
			page.setPageSize(10000);
			//查询数据
			DataGrid<Map<String, Object>> data = this.getData(user, page, queryType);
			switch(queryType){
				case 0:
					headers = new String[]{"ZONENAME","LARGESUMRECIPE"};
					beanNames = new String[]{"区域","大金额处方数"};
					break;
				case 1:
					headers = new String[]{"PROVINCENAME","LARGESUMRECIPE"};
					beanNames = new String[]{"省名称","大金额处方数"};
					break;
				case 2:
					headers = new String[]{"CITYNAME","LARGESUMRECIPE"};
					beanNames = new String[]{"市名称","大金额处方数"};
					break;
				case 3:
					headers = new String[]{"COUNTYNAME","LARGESUMRECIPE"};
					beanNames = new String[]{"区名称","大金额处方数"};
					break;
				case 4:
					headers = new String[]{"HOSPITALNAME","LARGESUMRECIPE"};
					beanNames = new String[]{"医院名称","大金额处方数"};
					break;
				case 5:
					headers = new String[]{"HOSPITALNAME","DEPARTNAME","LARGESUMRECIPE"};
					beanNames = new String[]{"医院名称","科室名称","大金额处方数"};
					break;
				case 6:
					headers = new String[]{"HOSPITALNAME","DEPARTNAME","DOCTORNAME","LARGESUMRECIPE"};
					beanNames = new String[]{"医院名称","科室名称","医生名称","大金额处方数"};
					break;
				default :
					headers = new String[]{"HOSPITALCODE","LARGESUMRECIPE"};
					beanNames = new String[]{"医院编码","大金额处方数"};
					break;
			}
			ExcelUtil util = new ExcelUtil(beanNames, headers);
			
			String zoneName ="";
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "ZONENAME");
			if(attributeItem != null){
				  zoneName = attributeItem.getField3();
			}
			for (Map<String,Object> map : data.getRows()) {
				map.put("ZONENAME", zoneName);
			}
			Workbook workbook = util.doExportXLS(data.getRows(), "大金额处方分析", false, true);
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
		long date0 = new Date().getTime();
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
			pageTmp.setPageNumber(1);

			List<Map<String, Object>> list = clinicAnalysisService.groupByColumn(user.getProjectCode()+"_SUP",pageTmp, "A.HOSPITALCODE").getRows();

			for (Map<String, Object> map : list) {
				Map<String, Future> fmap = new HashMap<>();
				System.out.println("连接数据源：sup_"+map.get("HOSPITALCODE"));
				pageTmp.getQuery().put("A#HOSPITALCODE_S_EQ", map.get("HOSPITALCODE"));
				clinicAnalysisService.getLargeRecipeNum("sup_"+map.get("HOSPITALCODE"), pageTmp);
				fmap.put("Future0", RpcContext.getContext().getFuture());
				
				System.out.println("HOSPITALCODE0:"+map.get("HOSPITALCODE"));
				hospitals.put((String)map.get("HOSPITALCODE"), fmap);
			}
			System.out.println("医院len:"+hospitals.size());

			res.put("hospitals", hospitals);
		}
		for (Map<String, Object> res : data) {
			Map<String, Map<String, Future>> hospitals = (Map<String, Map<String, Future>>)res.get("hospitals");
			Iterator<String> keys = hospitals.keySet().iterator();
			BigDecimal largeSumRecipe = new BigDecimal(0d);
			while (keys.hasNext()) {
				String key = keys.next();
				Map<String, Future> fmap = hospitals.get(key);
				BigDecimal largeSumRecipe0 = ((Future<BigDecimal>)fmap.get("Future0")).get();
				if (largeSumRecipe0 == null) {
					largeSumRecipe0 = new BigDecimal(0d);
				}
				
			largeSumRecipe = largeSumRecipe.add(largeSumRecipe0);
				
			}
			res.put("LARGESUMRECIPE", largeSumRecipe);
			res.remove("hospitals");
		}
	}
}
