package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.entity.BaseDrugProvide;
import com.shyl.msc.supervise.service.IBaseDrugProvideService;
import com.shyl.sys.entity.User;

/**
 * 医院基本药物配备使用比例Controller
 * 
 * @author a_Q
 *
 */

@Controller
@RequestMapping("/supervise/baseDrugProvide")
public class BaseDrugProvideController extends BaseController {

	@Resource
	private IBaseDrugProvideService baseDrugProvideService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Override
	protected void init(WebDataBinder arg0) {
	}
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model,Integer queryType,String jsonStr,@CurrentUser User user){
		queryType = (queryType == null ? 0 : queryType);
		if (user.getOrganization().getOrgType() == 3) {
			if (!StringUtils.isEmpty(jsonStr)) {
				try {
					model.addAttribute("jsonStr", URLEncoder.encode(jsonStr, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
				}
			}
		}
		model.addAttribute("queryType", queryType);
		return "supervise/baseDrugProvide/list";
	}
	
	@RequestMapping("page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest page,Integer queryType,@CurrentUser User user){
		queryType = (queryType == null ? 0 : queryType);
		DataGrid<Map<String, Object>> result =baseDrugProvideService.queryProvideByPage(user.getProjectCode()+"_SUP",page,queryType);
		return result;
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String search(ModelMap model,@CurrentUser User user) {
		if (user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),user.getOrganization().getOrgCode());
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			model.addAttribute("treePath", treePath);
		}
		return "/supervise/baseDrugProvide/search";
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType,@CurrentUser User user, HttpServletResponse resp){
		queryType = (queryType == null ?0:queryType);
		DataGrid<Map<String, Object>> result =baseDrugProvideService.queryProvideByPage(user.getProjectCode()+"_SUP",page,queryType);
		List<Map<String, Object>> list = new ArrayList<>();
		list.addAll(result.getRows());
		if (result.getTotal()>page.getPageSize()) {
			int pageNo = (int)result.getTotal()/page.getPageSize();
			if (result.getTotal()%page.getPageSize()!=0) {
				pageNo++;
			}
			for (int i=2;i<=pageNo;i++) {
				page.setPage(i);
				result =  baseDrugProvideService.queryProvideByPage(user.getProjectCode()+"_SUP",page,queryType);
				if (result.getRows().size() >0) {
					list.addAll(result.getRows());
				}
			}
		}
		String[] headers = null;
		String[] beanNames = null;
		
		switch(queryType){
		case 0:
			headers = new String[]{"COUNTYNAME","BASEDRUGTOTAL","DRUGTOTAL","DRUGRATIO","BASEDRUGTRADE","DRUGTRADE","DRUGTRADERATIO"};
			beanNames = new String[]{"镇(区)","基本药物品规数","全部药物品规数","基本药物品规数占比","基本药物销售金额","全部药物销售金额","基本药物销售金额占比"};
			break;
		case 1:
			headers = new String[]{"HOSPITALNAME","ORGLEVEL","ORGTYPE","BASEDRUGTOTAL","DRUGTOTAL","DRUGRATIO","BASEDRUGTRADE","DRUGTRADE","DRUGTRADERATIO","ISREFORMHOSPITAL"};
			beanNames = new String[]{"医疗机构名称","医疗机构级别","医疗机构类型","基本药物品规数","全部药物品规数","基本药物品规数占比","基本药物销售金额","全部药物销售金额","基本药物销售金额占比","是否县级公立医院改革试点医院"};
			break;
		case 2:
			headers = new String[]{"HOSPITALNAME","MONTH","ORGLEVEL","ORGTYPE","BASEDRUGTOTAL","DRUGTOTAL","DRUGRATIO","BASEDRUGTRADE","DRUGTRADE","DRUGTRADERATIO","ISREFORMHOSPITAL"};
			beanNames = new String[]{"医疗机构名称","月份","医疗机构级别","医疗机构类型","基本药物品规数","全部药物品规数","基本药物品规数占比","基本药物销售金额","全部药物销售金额","基本药物销售金额占比","是否县级公立医院改革试点医院"};
			break;
		default :
			return ;
		}
		ExcelUtil util = new ExcelUtil(beanNames, headers);
		/*Map<String, Map<Object,Object>> ma = new HashMap<>();
		if(queryType>0){
			Map<Object,Object> ISREFORMHOSPITAL = new HashMap<>();
			ISREFORMHOSPITAL.put(new Integer(0), "否");
			ISREFORMHOSPITAL.put(new Integer(1), "是");
			ma.put("ISREFORMHOSPITAL", ISREFORMHOSPITAL);
			util = new ExcelUtil(beanNames, headers,null,ma);
		}*/
		
		DecimalFormat df=new DecimalFormat("######0.0"); 
		for (Map<String, Object> li : list) {
			if(li.get("BASEDRUGTOTAL")!=null&&li.get("DRUGTOTAL")!=null){
				double baseDrugTotal = Double.parseDouble(li.get("BASEDRUGTOTAL").toString());
				double drugTotal = Double.parseDouble(li.get("DRUGTOTAL").toString());
				if(baseDrugTotal!=0&&drugTotal!=0){
					li.put("DRUGRATIO", df.format(baseDrugTotal*100/baseDrugTotal)+"%");
				}else{
					li.put("DRUGRATIO", "%");
				}
			}else{
				li.put("DRUGRATIO", "%");
			}
			if(li.get("BASEDRUGTRADE")!=null&&li.get("DRUGTRADE")!=null){
				double baseDrugTrade = Double.parseDouble(li.get("BASEDRUGTRADE").toString());
				double drugTrade = Double.parseDouble(li.get("DRUGTRADE").toString());
				if(baseDrugTrade!=0&&drugTrade!=0){
					li.put("DRUGTRADERATIO", df.format(baseDrugTrade*100/drugTrade)+"%");
				}else{
					li.put("DRUGTRADERATIO", "%");
				}
			}else{
				li.put("DRUGTRADERATIO", "%");
			}
			if(li.get("ISREFORMHOSPITAL")!=null){
				Integer isReformHospital = Integer.parseInt(li.get("ISREFORMHOSPITAL").toString());
				if(isReformHospital==1){
					li.put("ISREFORMHOSPITAL", "是");
				}else{
					li.put("ISREFORMHOSPITAL", "否");
				}
			}else{
				li.put("ISREFORMHOSPITAL", "否");
			}
			
		}
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
