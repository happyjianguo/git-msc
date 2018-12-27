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
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.service.IInOutBoundDetailService;
import com.shyl.msc.b2b.order.service.IInOutBoundService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IHisAnalysisService;
import com.shyl.msc.supervise.service.IHospitalZoneService;
import com.shyl.sys.entity.User;

/**
 * 医院药品排名
 * 
 * @author CHENJIN
 *
 */
@Controller
@RequestMapping("/supervise/drugRanking")
public class DrugRankingController extends BaseController {
	@Resource
	private IHisAnalysisService hisAnalysisService;

	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private IProductService productService;
	@Resource
	private IInOutBoundDetailService inOutBoundDetailService;
	@Resource
	private IHospitalZoneService hospitalZoneService;
	
	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(ModelMap model, Integer queryType, String jsonStr, @CurrentUser User user) {
		queryType = (queryType == null ? 0 : queryType);
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
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("queryType", queryType);
		return "/supervise/drugRanking/index";
	}

	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public String chart(ModelMap model, @CurrentUser User user) {
		// return "/supervise/baseDrugTotal/chart";
		return "/supervise/drugRanking/chart";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String seach(ModelMap model, @CurrentUser User user) {
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
		// return "/supervise/baseDrugTotal/search";
		return "/supervise/drugRanking/search";
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> query(PageRequest page, Integer queryType, Integer orderType,
			@CurrentUser User user) {
		queryType = (queryType == null ? 0 : queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType == 0) {
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
		String beginMonth = null;
		String endMonth = null;
		Hospital hospital = null;
		if(query.get("h#month_S_GE")==null||query.get("h#month_S_GE")==""){
			query.put("h#month_S_GE", start);
			query.put("h#month_S_LE", start);
			beginMonth=start;
			endMonth=start;
		}else{
			beginMonth=(String)query.get("h#month_S_GE");
			endMonth=(String)query.get("h#month_S_LE");
		}
		if (user.getOrganization().getOrgType() != null) {
			RegulationOrg regulationOrg = null;
			RegionCode regionCode = null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			} else if (user.getOrganization().getOrgType() == 1) {
				 hospital = hospitalService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
				query.put("h#hospitalCode_S_EQ", hospital.getCode());
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
		if (queryType > 4) {
			query.remove("c#treePath_S_RLK");
			query.remove("c#provinceCode_S_EQ");
			query.remove("c#cityCode_S_EQ");
			query.remove("c#countyCode_S_EQ");
			
		}
		DataGrid<Map<String, Object>> queryHisAnalysisByPage = hisAnalysisService.queryDrugRankingByPage(user.getProjectCode()+"_SUP",page,
				queryType);
		for (Map<String, Object> map : queryHisAnalysisByPage.getRows()) {
			map.put("begindata", beginMonth);
			map.put("enddata", endMonth);
			BigDecimal purcahseNum=new BigDecimal(0d);
			BigDecimal purcahseSum=new BigDecimal(0d);
			BigDecimal gpoPurcahseNum=new BigDecimal(0d);
			BigDecimal gpoPurcahseSum=new BigDecimal(0d);
			BigDecimal notGpoPurcahseNum=new BigDecimal(0d);
			BigDecimal notGpoPurcahseSum=new BigDecimal(0d);
			PageRequest newPage = new PageRequest();
			Map<String, Object> newQuery = newPage.getQuery();
			if(queryType==1){
				newQuery.put("z#provinceCode_S_EQ", (String)map.get("PROVINCECODE"));
			}else if(queryType==2){
				newQuery.put("z#cityCode_S_EQ", (String)map.get("CITYCODE"));
			}else if(queryType==3){
				newQuery.put("z#countyCode_S_EQ", (String)map.get("COUNTYCODE"));
			}else if(queryType==4){
				newQuery.put("z#hospitalCode_S_EQ", (String)map.get("HOSPITALCODE"));
			}
			List<Map<String, Object>> his= hospitalZoneService.getHospital(user.getProjectCode()+"_SUP",newPage);
			List<String> codes = new ArrayList<>();
			for (Map<String, Object> bean : his) {
				codes.add((String)bean.get("HOSPITALCODE"));
			}
			String newCode = "";
			for (String str : codes) {
				if (StringUtils.isNotEmpty(newCode)) {
					newCode+=",";
				}
				newCode+="'"+str+"'";
			}
			PageRequest newPage1 = new PageRequest();
			Map<String, Object> newQuery1 = newPage1.getQuery();
			newQuery1.put("b#hospitalCode_S_IN", newCode);
			newQuery1.put("d#productCode_S_EQ", (String)map.get("PRODUCTCODE"));
			List<Map<String, Object>> queryInOutBoundDetail = inOutBoundDetailService.queryInOutBoundDetail(user.getProjectCode(),newPage1,beginMonth,endMonth);
			for (Map<String, Object> newMap : queryInOutBoundDetail) {
				purcahseNum = (BigDecimal)newMap.get("PURCAHSENUM");
				purcahseSum = (BigDecimal)newMap.get("PURCAHSESUM");
				gpoPurcahseNum=(BigDecimal)newMap.get("GPOPURCAHSENUM");
				gpoPurcahseSum = (BigDecimal)newMap.get("GPOPURCAHSESUM");
				notGpoPurcahseNum = (BigDecimal)newMap.get("NOTGPOPURCAHSENUM");
				notGpoPurcahseSum=(BigDecimal)newMap.get("NOTGPOPURCAHSESUM");
			}
			map.put("PURCAHSENUM", purcahseNum);
			map.put("PURCAHSESUM", purcahseSum);
			map.put("GPOPURCAHSENUM", gpoPurcahseNum);
			map.put("GPOPURCAHSESUM", gpoPurcahseSum);
			map.put("NOTGPOPURCAHSENUM", notGpoPurcahseNum);
			map.put("NOTGPOPURCAHSESUM", notGpoPurcahseSum);
		}
		return queryHisAnalysisByPage;
	}

	// 导出
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user,
			HttpServletResponse resp) {
		queryType = (queryType == null ? 0 : queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType == 0) {
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
		String beginMonth = null;
		String endMonth = null;
		if(query.get("h#month_S_GE")==null||query.get("h#month_S_GE")==""){
			query.put("h#month_S_GE", start);
			query.put("h#month_S_LE", start);
			beginMonth=start;
			endMonth=start;
		}else{
			beginMonth=(String)query.get("h#month_S_GE");
			endMonth=(String)query.get("h#month_S_LE");
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
				query.put("h#hospitalCode_S_EQ", hospital.getCode());
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
		if (queryType > 4) {
			query.remove("c#treePath_S_RLK");
			query.remove("c#provinceCode_S_EQ");
			query.remove("c#cityCode_S_EQ");
			query.remove("c#countyCode_S_EQ");
		}

		String[] headers = null;

		String[] beanNames = null;
		Map<String, Map<Object,Object>> ma = new HashMap<>();
		Map<Object,Object> absDrugType = new HashMap<>();
		Map<Object,Object> auxiliaryType = new HashMap<>();
		Map<Object,Object> ISGPOPURCHASE = new HashMap<>();
		absDrugType.put(new BigDecimal(0), "否");
		absDrugType.put(new BigDecimal(6), "是");
		absDrugType.put(new BigDecimal(7), "是");
		absDrugType.put(new BigDecimal(8), "是");
		auxiliaryType.put(new BigDecimal(0), "否");
		auxiliaryType.put(new BigDecimal(1), "是");
		ISGPOPURCHASE.put(new Integer(0), "否");
		ISGPOPURCHASE.put(new Integer(1), "是");
		ma.put("ABSDRUGTYPE", absDrugType);
		ma.put("AUXILIARYTYPE", auxiliaryType);
		ma.put("ISGPOPURCHASE", ISGPOPURCHASE);
		switch (queryType) {
		case 0:
			beanNames = new String[] { "PRODUCTCODE", "PRODUCTNAME", "DOSAGEFORMNAME", "PACKDESC", "PRODUCERNAME",
					"AUTHORIZENO", "ABSDRUGTYPE", "AUXILIARYTYPE", "SUM", "NUM","PURCAHSENUM","PURCAHSESUM","GPOPURCAHSENUM","GPOPURCAHSESUM","NOTGPOPURCAHSENUM","NOTGPOPURCAHSESUM","ISGPOPURCHASE" };
			// beanNames headers
			headers = new String[] { "药品编码", "药品名称", "剂型", "规格", "生产企业 ", "国药准字(批准文号)", "抗菌药物标志", "辅助用药标志", "金额",
					"数量","总采购数量","总采购金额","GPO采购数量","GPO采购金额","非GPO采购数量","非GPO采购金额","是否GPO药品" };
			break;
		case 1:
			beanNames = new String[] { "PRODUCTNAME", "PRODUCTCODE", "PRODUCTNAME", "DOSAGEFORMNAME", "PACKDESC", "PRODUCERNAME",
					"AUTHORIZENO", "ABSDRUGTYPE", "AUXILIARYTYPE", "SUM", "NUM" ,"PURCAHSENUM","PURCAHSESUM","GPOPURCAHSENUM","GPOPURCAHSESUM","NOTGPOPURCAHSENUM","NOTGPOPURCAHSESUM","ISGPOPURCHASE"};
			headers = new String[] { "省级", "药品编码", "药品名称", "剂型", "规格", "生产企业 ", "国药准字(批准文号)", "抗菌药物标志", "辅助用药标志", "金额",
					"数量","总采购数量","总采购金额","GPO采购数量","GPO采购金额","非GPO采购数量","非GPO采购金额","是否GPO药品" };
			break;
		case 2:
			beanNames = new String[] { "CITYNAME", "PRODUCTCODE", "PRODUCTNAME", "DOSAGEFORMNAME", "PACKDESC", "PRODUCERNAME",
					"AUTHORIZENO", "ABSDRUGTYPE", "AUXILIARYTYPE", "SUM", "NUM","PURCAHSENUM","PURCAHSESUM","GPOPURCAHSENUM","GPOPURCAHSESUM","NOTGPOPURCAHSENUM","NOTGPOPURCAHSESUM","ISGPOPURCHASE" };
			headers = new String[] { "市级", "药品编码", "药品名称", "剂型", "规格", "生产企业 ", "国药准字(批准文号)", "抗菌药物标志", "辅助用药标志", "金额",
					"数量","总采购数量","总采购金额","GPO采购数量","GPO采购金额","非GPO采购数量","非GPO采购金额","是否GPO药品" };
			break;
		case 3:
			beanNames = new String[] { "COUNTYNAME", "PRODUCTCODE", "PRODUCTNAME", "DOSAGEFORMNAME", "PACKDESC", "PRODUCERNAME",
					"AUTHORIZENO", "ABSDRUGTYPE", "AUXILIARYTYPE", "SUM", "NUM","PURCAHSENUM","PURCAHSESUM","GPOPURCAHSENUM","GPOPURCAHSESUM","NOTGPOPURCAHSENUM","NOTGPOPURCAHSESUM","ISGPOPURCHASE" };
			headers = new String[] { "区/县", "药品编码", "药品名称", "剂型", "规格", "生产企业 ", "国药准字(批准文号)", "抗菌药物标志", "辅助用药标志", "金额",
					"数量","总采购数量","总采购金额","GPO采购数量","GPO采购金额","非GPO采购数量","非GPO采购金额","是否GPO药品" };
			break;
		case 4:
			beanNames = new String[] { "HOSPITALNAME", "PRODUCTCODE", "PRODUCTNAME", "DOSAGEFORMNAME", "PACKDESC", "PRODUCERNAME",
					"AUTHORIZENO", "ABSDRUGTYPE", "AUXILIARYTYPE", "SUM", "NUM","PURCAHSENUM","PURCAHSESUM","GPOPURCAHSENUM","GPOPURCAHSESUM","NOTGPOPURCAHSENUM","NOTGPOPURCAHSESUM","ISGPOPURCHASE" };
			headers = new String[] { "医院", "药品编码", "药品名称", "剂型", "规格", "生产企业 ", "国药准字(批准文号)", "抗菌药物标志", "辅助用药标志", "金额",
					"数量","总采购数量","总采购金额","GPO采购数量","GPO采购金额","非GPO采购数量","非GPO采购金额","是否GPO药品" };
			break;
		case 5:
			beanNames = new String[] { "HOSPITALNAME","DEPARTNAME", "PRODUCTCODE", "PRODUCTNAME", "DOSAGEFORMNAME", "PACKDESC", "PRODUCERNAME",
					"AUTHORIZENO", "ABSDRUGTYPE", "AUXILIARYTYPE", "SUM", "NUM","ISGPOPURCHASE" };
			headers = new String[] { "医院","科室", "药品编码", "药品名称", "剂型", "规格", "生产企业 ", "国药准字(批准文号)", "抗菌药物标志", "辅助用药标志", "金额",
					"数量","是否GPO药品" };
			break;
		case 6:
			beanNames = new String[] { "HOSPITALNAME","DEPARTNAME","DOCTORNAME","PRODUCTCODE", "PRODUCTNAME", "DOSAGEFORMNAME", "PACKDESC", "PRODUCERNAME",
					"AUTHORIZENO", "ABSDRUGTYPE", "AUXILIARYTYPE", "SUM", "NUM","ISGPOPURCHASE" };
			headers = new String[] { "医院","科室","医生", "药品编码", "药品名称", "剂型", "规格", "生产企业 ", "国药准字(批准文号)", "抗菌药物标志", "辅助用药标志", "金额",
					"数量","是否GPO药品" };
			break;
		default:
			return;
		}

		ExcelUtil util = new ExcelUtil(headers, beanNames, null, ma);
		/*DataGrid<Map<String, Object>> data = hisAnalysisService.queryDrugRankingByPage(page, queryType);
		List<Map<String, Object>> list = data.getRows();*/
		List<Map<String, Object>> list = hisAnalysisService.queryDrugRankingByPageAll(user.getProjectCode()+"_SUP",page, queryType);
		for (Map<String, Object> map : list) {
			map.put("begindata", beginMonth);
			map.put("enddata", endMonth);
			BigDecimal purcahseNum=new BigDecimal(0d);
			BigDecimal purcahseSum=new BigDecimal(0d);
			BigDecimal gpoPurcahseNum=new BigDecimal(0d);
			BigDecimal gpoPurcahseSum=new BigDecimal(0d);
			BigDecimal notGpoPurcahseNum=new BigDecimal(0d);
			BigDecimal notGpoPurcahseSum=new BigDecimal(0d);
			PageRequest newPage = new PageRequest();
			Map<String, Object> newQuery = newPage.getQuery();
			if(queryType==1){
				newQuery.put("z#provinceCode_S_EQ", (String)map.get("PROVINCECODE"));
			}else if(queryType==2){
				newQuery.put("z#cityCode_S_EQ", (String)map.get("CITYCODE"));
			}else if(queryType==3){
				newQuery.put("z#countyCode_S_EQ", (String)map.get("COUNTYCODE"));
			}else if(queryType==4){
				newQuery.put("z#hospitalCode_S_EQ", (String)map.get("HOSPITALCODE"));
			}
			List<Map<String, Object>> his= hospitalZoneService.getHospital(user.getProjectCode()+"_SUP",newPage);
			List<String> codes = new ArrayList<>();
			for (Map<String, Object> bean : his) {
				codes.add((String)bean.get("HOSPITALCODE"));
			}
			String newCode = "";
			for (String str : codes) {
				if (StringUtils.isNotEmpty(newCode)) {
					newCode+=",";
				}
				newCode+="'"+str+"'";
			}
			PageRequest newPage1 = new PageRequest();
			Map<String, Object> newQuery1 = newPage1.getQuery();
			newQuery1.put("b#hospitalCode_S_IN", newCode);
			newQuery1.put("d#productCode_S_EQ", (String)map.get("PRODUCTCODE"));
			List<Map<String, Object>> queryInOutBoundDetail = inOutBoundDetailService.queryInOutBoundDetail(user.getProjectCode(),newPage1,beginMonth,endMonth);
			for (Map<String, Object> newMap : queryInOutBoundDetail) {
				purcahseNum = (BigDecimal)newMap.get("PURCAHSENUM");
				purcahseSum = (BigDecimal)newMap.get("PURCAHSESUM");
				gpoPurcahseNum=(BigDecimal)newMap.get("GPOPURCAHSENUM");
				gpoPurcahseSum = (BigDecimal)newMap.get("GPOPURCAHSESUM");
				notGpoPurcahseNum = (BigDecimal)newMap.get("NOTGPOPURCAHSENUM");
				notGpoPurcahseSum=(BigDecimal)newMap.get("NOTGPOPURCAHSESUM");
			}
			map.put("PURCAHSENUM", purcahseNum);
			map.put("PURCAHSESUM", purcahseSum);
			map.put("GPOPURCAHSENUM", gpoPurcahseNum);
			map.put("GPOPURCAHSESUM", gpoPurcahseSum);
			map.put("NOTGPOPURCAHSENUM", notGpoPurcahseNum);
			map.put("NOTGPOPURCAHSESUM", notGpoPurcahseSum);
		}
		try {
			Workbook workbook = util.doExportXLS(list, "医院药品排名", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=drugRanking.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);
			out.flush();
			workbook.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
