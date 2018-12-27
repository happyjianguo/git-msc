package com.shyl.msc.hospital.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.ICartContractService;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 下单
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/hospital/placeOrderContract")
public class HospitalPlaceOrderContractController extends BaseController {

	
	@Resource
	private ICartContractService cartContractService;
	@Resource
	private IContractDetailService contractDetailService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	@RequestMapping(value="/list")
	public String list(){
		return "/hospital/placeOrderContract/list";
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<ContractDetail> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<ContractDetail> page = null;
		if(user.getOrganization().getOrgType() == 1){
			String hospitalCode = user.getOrganization().getOrgCode();
			Map<String, Object> query = pageable.getQuery();
			query.put("t#contract.hospitalCode_S_EQ", hospitalCode);
			query.put("t#contract.status_S_EQ", Status.signed);
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			query.put("t#contract.endValidDate_S_GT", fmt.format(new Date()));
			query.put("t#contractNum-t.purchasePlanNum-t.cartNum_BD_GT", new BigDecimal("0"));
			Sort sort = new Sort(Direction.ASC,"product.code");
			pageable.setSort(sort);
			page = contractDetailService.query(user.getProjectCode(), pageable);
			
		}
		return page;
	}
	
	@RequestMapping(value="/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Integer goodsNum,Long contractDId, @CurrentUser User user){
		System.out.println("-----goodsNum----"+goodsNum);
		System.out.println("-----contractDId----"+contractDId);
		System.out.println("-----user----"+user.getEmpId());
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgId() != null){
				Integer type = user.getOrganization().getOrgType();
				if(type == 1){
					cartContractService.mkCartContract(user.getProjectCode(), goodsNum,contractDId,user);
					
					message.setSuccess(true);
				}else{
					message.setSuccess(false);
					message.setMsg("不是医疗机构账号");
				}
			}else{
				message.setSuccess(false);
				message.setMsg("不是医疗机构账号");
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	/**
	 * 导出合同药品目录
	 * @param user
	 * @param resp
	 */
	@RequestMapping("/exportExcel")
	public void exportExcel(PageRequest pageable,@CurrentUser User user, HttpServletResponse resp){
		try {
			if(user.getOrganization().getOrgType() == 1){
				String hospitalCode = user.getOrganization().getOrgCode();
				String name = user.getOrganization().getOrgName()+"下单模板";
				
				Map<String, Object> query = pageable.getQuery();
				//query.put("t#contract.hospitalCode_S_EQ", user.getOrganization().getOrgCode());
				query.put("t#contract.status_S_EQ", Status.signed);
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				query.put("t#contract.endValidDate_S_GT", fmt.format(new Date()));
				
				List<Map<String, Object>> dataList = contractDetailService.listByHospitalSigned(user.getProjectCode(), hospitalCode);
				System.out.println("dataList======"+dataList.size());
				String heanders [] = {"药品编码","合同明细","数量","可购余量","截止日期","价格","单位","药品名称","通用名",
						"剂型","规格","包装规格","生产企业","供应商名称"};
				String beannames [] = {"PRODUCTCODE","CONTRACTCODE","CARTNUM","LASTNUM","ENDVALIDDATE","PRICE","UNITNAME","PRODUCTNAME",
						"GENERICNAME","DOSAGEFORMNAME","MODEL","PACKDESC","PRODUCERNAME","VENDORNAME"};
				Map<String, Boolean> lineMap = new HashMap<>();
				lineMap.put("PRODUCTCODE", true);
				lineMap.put("CONTRACTCODE", true);
				lineMap.put("CARTNUM", true);
				ExcelUtil excelUtil = new ExcelUtil(heanders, beannames, lineMap);
				Workbook workbook = excelUtil.doExportXLS(dataList, name, false, true);
				
				resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				resp.setHeader("Content-Disposition", "attachment; filename=template.xls");
				OutputStream out = resp.getOutputStream();
				workbook.write(out);  
		 		out.flush();
		 		workbook.close();
		 		out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
