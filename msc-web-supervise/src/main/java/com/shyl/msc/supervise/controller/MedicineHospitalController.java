package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IMedicineHospitalService;
import com.shyl.msc.supervise.service.IMedicineService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/medicineHospital")
public class MedicineHospitalController extends BaseController{
	
	@Resource
	private IMedicineHospitalService medicineHospitalService;
	@Resource
	private IMedicineService medicineService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;

	private String filterRules ;

	@Override
	protected void init(WebDataBinder arg0) {
		
	}
	
	@RequestMapping("")
	public String index(ModelMap model, @CurrentUser User user, String jsonStr) {
		model.addAttribute("orgType",user.getOrganization().getOrgType());
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/medicineHospital/index";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, @CurrentUser User user) {
		this.filterRules = page.getFilterRules();
		Map<String, Object> query = page.getQuery();
		if(null==query){
			page.setQuery(new HashMap<String,Object>());
		}
		page.setSort(new Sort(new Order(Direction.ASC, "t.hospitalname,t.productcode")));  
		for (Entry<String, Object> entry : query.entrySet()) {
			String key = entry.getKey();
			if (!StringUtils.isEmpty(key) && "hospitalCode_S_EQ".equals(key)) {
				query.remove("hospitalCode_S_EQ");
				query.put("t#" + key, entry.getValue());
				break;
			}
		}
		if (user.getOrganization().getOrgType() == 1) {
			if(StringUtils.isEmpty((String) query.get("t#hospitalCode_S_EQ"))){
				query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
			}
		} else if (user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			String treePath = regionCode.getTreePath();
			if(treePath == null){
				treePath += regionCode.getId().toString();
			}else{
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String)query.get("c#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath)<0) {
				query.put("c#treePath_S_RLK", treePath0);
			}
		}
		DataGrid<Map<String,Object>> result = medicineHospitalService.queryByPage(user.getProjectCode()+"_SUP",page);
		return result;
	}

	
	@RequestMapping(value = "/setAuxiliary", method = RequestMethod.POST)
	@ResponseBody
	public Message setAuxiliary(@RequestParam(value = "array[]")Long[] array,Integer isAuxiliary, @CurrentUser User user) {
		Message message = new Message();
		try {
			for (Long id : array) {
				medicineHospitalService.updateAuxiliaryType(user.getProjectCode()+"_SUP",id, isAuxiliary);	
			}	
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("设置失败");
		}
		return message;
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page,@CurrentUser User user, HttpServletResponse resp){
		if(StringUtils.isNotEmpty(this.filterRules)){
			page.setFilterRules(this.filterRules);
		}
		Map<String, Object> query = page.getQuery();
		if(null==query){
			page.setQuery(new HashMap<String,Object>());
		}
		page.setSort(new Sort(new Order(Direction.ASC, "t.hospitalname,t.productcode")));  
		for (Entry<String, Object> entry : query.entrySet()) {
			String key = entry.getKey();
			if (!StringUtils.isEmpty(key) && "hospitalCode_S_EQ".equals(key)) {
				query.remove("hospitalCode_S_EQ");
				query.put("t#" + key, entry.getValue());
				break;
			}
		}
		if (user.getOrganization().getOrgType() == 1) {
			if(StringUtils.isEmpty((String) query.get("t#hospitalCode_S_EQ"))){
				query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
			}
		} else if (user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			String treePath = regionCode.getTreePath();
			if(treePath == null){
				treePath += regionCode.getId().toString();
			}else{
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String)query.get("c#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath)<0) {
				query.put("c#treePath_S_RLK", treePath0);
			}
		}
		String[] headers = new String[]{"HOSPITALNAME","PRODUCTCODE","INTERNALCODE","PRODUCTNAME","MODEL","PACKDESC","DOSAGEFORMNAME","PRODUCERNAME","ISCOMPARE","AUTHORIZENO","IMPORTFILENO","DDD","AUXILIARYTYPE","ISGPOPURCHASE","GPONAME","BASEDRUGTYPE","ABSDRUGTYPE","ISURGENT","ISDISABLED"};
		String[] beanNames = new String[]{"医院名称","药品编码","医院内部编码","药品名称","规格","包装","剂型","生产厂家","是否对照","国药准字","注册证号","DDD","是否辅助用药","是否GPO药品","GPO名称","是否基药","是否抗菌药物","是否急救药品","是否禁用"};
		Map<String, Map<Object,Object>> ma = new HashMap<>();
		Map<Object,Object> ISGPOPURCHASE = new HashMap<>();
		ISGPOPURCHASE.put(new BigDecimal(0), "否");
		ISGPOPURCHASE.put(new BigDecimal(1), "是");
		ma.put("ISGPOPURCHASE", ISGPOPURCHASE);
		Map<Object,Object> ISCOMPARE = new HashMap<>();
		ISCOMPARE.put(new BigDecimal(0), "否");
		ISCOMPARE.put(new BigDecimal(1), "是");
		ma.put("ISCOMPARE", ISCOMPARE);
		Map<Object,Object> AUXILIARYTYPE = new HashMap<>();
		AUXILIARYTYPE.put(new BigDecimal(0), "否");
		AUXILIARYTYPE.put(new BigDecimal(1), "是");
		ma.put("AUXILIARYTYPE", AUXILIARYTYPE);
		Map<Object,Object> BASEDRUGTYPE = new HashMap<>();
		BASEDRUGTYPE.put(new BigDecimal(0), "否");
		BASEDRUGTYPE.put(new BigDecimal(1), "是");
		ma.put("BASEDRUGTYPE", BASEDRUGTYPE);
		Map<Object,Object> ISURGENT = new HashMap<>();
		ISURGENT.put(new BigDecimal(0), "否");
		ISURGENT.put(new BigDecimal(1), "是");
		ma.put("ISURGENT", ISURGENT);
		Map<Object,Object> ABSDRUGTYPE = new HashMap<>();
		ABSDRUGTYPE.put(new BigDecimal(0), "否");
		ABSDRUGTYPE.put(new BigDecimal(1), "是");
		ma.put("ABSDRUGTYPE", ABSDRUGTYPE);
		Map<Object,Object> ISDISABLED = new HashMap<>();
		ISDISABLED.put(new BigDecimal(0), "否");
		ISDISABLED.put(new BigDecimal(1), "是");
		ma.put("ISDISABLED", ISDISABLED);
 		List<Map<String,Object>> list = medicineHospitalService.queryByAll(user.getProjectCode()+"_SUP",page);
		ExcelUtil util = new ExcelUtil(beanNames, headers,null,ma);
		try{
			Workbook workbook = util.doExportXLS(list, "医院基本药物配备使用比例", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=BaseDrugProvide.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		}catch(IOException e){
			
		}
	}
}
