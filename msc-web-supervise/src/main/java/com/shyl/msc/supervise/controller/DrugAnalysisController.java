package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IDrugAnalysisService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/drugAnalysis")
public class DrugAnalysisController extends BaseController {

	@Resource
	private IDrugAnalysisService drugAnalysisService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private IProductService productService;
	@Override
	protected void init(WebDataBinder arg0) {
	}

	/**
	 * queryType 0全省，1全市，2全区 3 医院，4 科室，5医生
	 * @param model
	 * @param queryType
	 * @param jsonStr
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index(ModelMap model, Integer queryType, String jsonStr, @CurrentUser User user) {
		queryType = (queryType == null ?0:queryType);
		// 如果是卫计委，过滤掉通行的地址
				if (user.getOrganization().getOrgType() == 3) {
					RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
							user.getOrganization().getOrgCode());
					if (regulationOrg.getRegionCode() != null) {
						RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
						String treePath = regionCode.getTreePath();
						if (treePath == null) {
							treePath += regionCode.getId().toString();
						} else {
							treePath += "," + regionCode.getId();
						}
						model.addAttribute("treePath", treePath);
					}
					RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
					int minType = 0;
					if (regionCode.getParentId() != null) {
						minType = regionCode.getTreePath().split(",").length + 1;
					}
					model.addAttribute("zoneName", regionCode.getName());

					if (queryType < minType) {
						queryType = minType;
					}
				} else if (user.getOrganization().getOrgType() == 1) {
					if (StringUtils.isEmpty(jsonStr)) {
						jsonStr = "{queryType:" + 4 + "}";
						queryType = 4;
					}
					model.addAttribute("orgType",1);
				} else {
					return null;
				}
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		model.addAttribute("queryType", queryType);
		return "/supervise/drugAnalysis/index";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String seach(ModelMap model,@CurrentUser User user) {
		if (user.getOrganization().getOrgType() != null) {
			RegulationOrg regulationOrg = null;
			RegionCode regionCode = null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			} else if (user.getOrganization().getOrgType() == 1) {
				Hospital hospital = hospitalService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			model.addAttribute("treePath", treePath);
		}
		model.addAttribute("searchType", 1);
		return "/supervise/drugAnalysis/search";
	}
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(ModelMap model,@CurrentUser User user) {
		return "/supervise/drugAnalysis/chart";
	}

	@RequestMapping(value="/page", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> query(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user) {
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "sum")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "num")));
		}
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		Calendar c = Calendar.getInstance();  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);  
		Date day =  c.getTime();  
		String start=  new SimpleDateFormat("yyyy-MM").format(day);  
		String begindata = "";
		String enddata="";
		if(query.get("month_S_GE")==null||query.get("month_S_GE")==""){
			query.put("month_S_GE", start);
			query.put("month_S_LE", start);
			begindata=start;
		}else{
			begindata=(String)query.get("month_S_GE");
			enddata=(String)query.get("month_S_LE");
		}
		if (user.getOrganization().getOrgType() != null) {
			RegulationOrg regulationOrg = null;
			RegionCode regionCode = null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			} else if (user.getOrganization().getOrgType() == 1) {
				Hospital hospital = hospitalService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
				query.put("c#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("c#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("c#treePath_S_RLK", treePath);
			}
		}
		if (queryType>4) {
			query.remove("c#treePath_S_RLK");
		}
		DataGrid<Map<String, Object>> queryDrugAnalysisByPage =drugAnalysisService.queryDrugAnalysisByPage(user.getProjectCode()+"_SUP",page, queryType);
		for (Map<String, Object> map : queryDrugAnalysisByPage.getRows()) {
			map.put("begindata", begindata);
			map.put("enddata", enddata);
			String productCode = (map.get("PRODUCTCODE")==null?"":map.get("PRODUCTCODE").toString());
			Product product = productService.getByCode(null, productCode);
			if(product!=null){
				map.put("ISGPOPURCHASE", product.getIsGPOPurchase());
			}	
		}
		return queryDrugAnalysisByPage;
	}

	@RequestMapping(value="/export", method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user, HttpServletResponse resp) {
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "sum")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "num")));
		}
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		Calendar c = Calendar.getInstance();  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);  
		Date day =  c.getTime();  
		String start=  new SimpleDateFormat("yyyy-MM").format(day);  
		if(query.get("month_S_GE")==null||query.get("month_S_GE")==""){
			query.put("month_S_GE", start);
			query.put("month_S_LE", start);
		}
		if (user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
					user.getOrganization().getOrgCode());
			if (regulationOrg.getRegionCode() != null) {
				RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
				String treePath = regionCode.getTreePath();
				if (treePath == null) {
					treePath += regionCode.getId().toString();
				} else {
					treePath += "," + regionCode.getId();
				}

				String treePath0 = (String) query.get("c#treePath_S_RLK");
				if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
					query.put("c#treePath_S_RLK", treePath);
				}
			}
		} else if ((user.getOrganization().getOrgType() == 1)) {
			Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			if (hospital.getRegionCode() != null) {
				RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
				String treePath = regionCode.getTreePath();
				if (treePath == null) {
					treePath += regionCode.getId().toString();
				} else {
					treePath += "," + regionCode.getId();
				}
				query.put("c#hospitalCode_S_EQ", hospital.getCode());
				String treePath0 = (String) query.get("c#treePath_S_RLK");
				if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
					query.put("c#treePath_S_RLK", treePath);
				}

			}
		}
		if (queryType>4) {
			query.remove("c#treePath_S_RLK");
		}
		Map<String, Map<Object,Object>> ma = new HashMap<>();
		Map<Object,Object> ISGPOPURCHASE = new HashMap<>();
		ISGPOPURCHASE.put(new Integer(0), "否");
		ISGPOPURCHASE.put(new Integer(1), "是");
		ma.put("ISGPOPURCHASE", ISGPOPURCHASE);
		String[] headers = null;
		
		String[] beanNames = null;
		switch (queryType) {
			case 0:
				headers = new String[]{"PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","NUM","DDD","DDDS","DDDC","ISGPOPURCHASE"};
				beanNames =new String[]{"药品名称","规格","包装","剂型","生产厂家","金额","数量","DDD","DDDS","DDDC","是否GPO药品"};
				break;
			case 1:
				headers = new String[]{"PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","NUM","DDD","DDDS","DDDC","ISGPOPURCHASE"};
				beanNames =new String[]{"省级","药品名称","规格","包装","剂型","生产厂家","金额","数量","DDD","DDDS","DDDC","是否GPO药品"};
				break;
			case 2:
				headers = new String[]{"CITYNAME","PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","NUM","DDD","DDDS","DDDC","ISGPOPURCHASE"};
				beanNames =new String[]{"市级","药品名称","规格","包装","剂型","生产厂家","金额","数量","DDD","DDDS","DDDC","是否GPO药品"};
				break;
			case 3:
				headers = new String[]{"COUNTYNAME","PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","NUM","DDD","DDDS","DDDC","ISGPOPURCHASE"};
				beanNames =new String[]{"区/县","药品名称","规格","包装","剂型","生产厂家","金额","数量","DDD","DDDS","DDDC","是否GPO药品"};
				break;
			case 4:
				headers = new String[]{"HOSPITALNAME","PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","NUM","DDD","DDDS","DDDC","ISGPOPURCHASE"};
				beanNames =new String[]{"医院","药品名称","规格","包装","剂型","生产厂家","金额","数量","DDD","DDDS","DDDC","是否GPO药品"};
				break;
			case 5:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","NUM","DDD","DDDS","DDDC","ISGPOPURCHASE"};
				beanNames =new String[]{"医院","科室","药品名称","规格","包装","剂型","生产厂家","金额","数量","DDD","DDDS","DDDC","是否GPO药品"};
				break;
			case 6:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","DOCTORNAME","PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","NUM","DDD","DDDS","DDDC","ISGPOPURCHASE"};
				beanNames =new String[]{"医院","科室","医生","药品名称","规格","包装","剂型","生产厂家","金额","数量","DDD","DDDS","DDDC","是否GPO药品"};
				break;
			default:
				return;
		}

		ExcelUtil util = new ExcelUtil(beanNames, headers,null,ma);
		List<Map<String,Object>> list = drugAnalysisService.queryDrugAnalysisByAll(user.getProjectCode()+"_SUP",page, queryType);
		for (Map<String, Object> map : list) {
			String productCode = (map.get("PRODUCTCODE")==null?"":map.get("PRODUCTCODE").toString());
			Product product = productService.getByCode(null, productCode);
			if(product!=null){
				map.put("ISGPOPURCHASE", product.getIsGPOPurchase());
			}	
		}
		try {
			Workbook workbook = util.doExportXLS(list, "单品种统计", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=DrugAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		} catch (IOException e) {
		}
	}

}
