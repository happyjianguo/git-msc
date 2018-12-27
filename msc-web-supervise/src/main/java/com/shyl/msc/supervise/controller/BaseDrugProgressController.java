package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.entity.BaseDrugProgress;
import com.shyl.msc.supervise.service.IBaseDrugProgressService;
import com.shyl.sys.entity.User;

/**
 * 实施基本药物制度进展Controller
 * 
 * @author a_Q
 *
 */

@Controller
@RequestMapping("/supervise/baseDrugProgress")
public class BaseDrugProgressController extends BaseController {

	@Resource
	private IBaseDrugProgressService baseDrugProgressService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Override
	protected void init(WebDataBinder arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 首页
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
		return "supervise/baseDrugProgress/list";
	}
	
	@RequestMapping("page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest page,Integer queryType,@CurrentUser User user){
		queryType = (queryType == null ? 0 : queryType);
		Map<String,Object> query = page.getQuery();
		if(query==null){
			query=new HashMap<String,Object>();
		}
		if(query.get("b#healthStationType_S_EQ")!=null){
			String healthStationType = (String)query.get("b#healthStationType_S_EQ");
			query.put("b#healthStationType_S_EQ", BaseDrugProgress.HealthStationType.valueOf(healthStationType).ordinal());
			
		}
		DataGrid<Map<String, Object>> result =baseDrugProgressService.queryProgressByPage(user.getProjectCode()+"_SUP",page,queryType);
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
			System.out.println(treePath);
			model.addAttribute("treePath", treePath);
		}
		
		return "/supervise/baseDrugProgress/search";
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType,@CurrentUser User user, HttpServletResponse resp){
		queryType = (queryType == null ?0:queryType);
		DataGrid<Map<String, Object>> result =baseDrugProgressService.queryProgressByPage(user.getProjectCode()+"_SUP",page,queryType);
		List<Map<String, Object>> list = new ArrayList<>();
		list.addAll(result.getRows());
		if (result.getTotal()>page.getPageSize()) {
			int pageNo = (int)result.getTotal()/page.getPageSize();
			if (result.getTotal()%page.getPageSize()!=0) {
				pageNo++;
			}
			for (int i=2;i<=pageNo;i++) {
				page.setPage(i);
				result =  baseDrugProgressService.queryProgressByPage(user.getProjectCode()+"_SUP",page,queryType);
				if (result.getRows().size() >0) {
					list.addAll(result.getRows());
				}
			}
		}
		String[] headers = null;
		String[] beanNames = null;
		switch(queryType){
		case 0:
			headers = new String[]{"CITYNAME","HEALTHSTATIONNUM","ISHIGHSIXTY","ISIMPLEMENTEDSTATION","ISGENERALSTATION","ISTHIRDHEALTHSTATION","ISINHEALTHINSURANCE"};
			beanNames = new String[]{"市","村卫生站机构数","基本药物集中采购且采购品规数、金额占比均不低于60%机构数","已实行药品零差率销售的机构数","已实施一般诊疗费收费的机构数","承担30%以上基本公共卫生服务机构数","已纳入城乡居民医保门诊统筹实施范围的机构数"};
			break;
		case 1:
			headers = new String[]{"COUNTYNAME","HEALTHSTATIONNUM","ISHIGHSIXTY","ISIMPLEMENTEDSTATION","ISGENERALSTATION","ISTHIRDHEALTHSTATION","ISINHEALTHINSURANCE"};
			beanNames = new String[]{"镇区","村卫生站机构数","基本药物集中采购且采购品规数、金额占比均不低于60%机构数","已实行药品零差率销售的机构数","已实施一般诊疗费收费的机构数","承担30%以上基本公共卫生服务机构数","已纳入城乡居民医保门诊统筹实施范围的机构数"};
			break;
		case 2:
			headers = new String[]{"COUNTYNAME","MONTH","HEALTHSTATIONNUM","ISHIGHSIXTY","ISIMPLEMENTEDSTATION","ISGENERALSTATION","ISTHIRDHEALTHSTATION","ISINHEALTHINSURANCE"};
			beanNames = new String[]{"镇区","月份","村卫生站机构数","基本药物集中采购且采购品规数、金额占比均不低于60%机构数","已实行药品零差率销售的机构数","已实施一般诊疗费收费的机构数","承担30%以上基本公共卫生服务机构数","已纳入城乡居民医保门诊统筹实施范围的机构数"};
			break;
		default :
			return ;
		}
		ExcelUtil util = new ExcelUtil(beanNames, headers);
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
