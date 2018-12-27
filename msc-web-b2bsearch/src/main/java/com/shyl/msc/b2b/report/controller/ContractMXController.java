package com.shyl.msc.b2b.report.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.entity.ContractDetail.Status;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.sys.entity.User;

/**
 * 合同明细执行情况
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/contractMX")
public class ContractMXController extends BaseController {

	@Resource
	private IContractDetailService contractDetailService;

	private String filterRules ;
	
	@Override
	protected void init(WebDataBinder binder) {
		
	}
	
	@RequestMapping("")
	public String list(ModelMap model,String code){
		model.addAttribute("code", code);
		return "b2b/report/contractMX/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> list(PageRequest pageable,@CurrentUser User currentUser){
		this.filterRules = pageable.getFilterRules();
		int orgType = currentUser.getOrganization().getOrgType();
		String orgCode = currentUser.getOrganization().getOrgCode();
		Map<String, Object> query = pageable.getQuery();
		String status = (String)query.get("t#status_L_EQ");
		System.out.println("status"+status);
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_L_EQ", Status.valueOf(status));
		}
		if(orgType == 1){//医院
			query.put("t#contract.hospitalCode_S_EQ", orgCode);
		}else if(orgType == 2){//供应商
			query.put("t#contract.vendorCode_S_EQ", orgCode);
		}
		query.put("t#contractNum_FT_NE", new BigDecimal(0));
		List<Map<String, Object>> rltList = new ArrayList<>();
		if(!StringUtils.isEmpty(pageable.getSort())){
			pageable.setSort("t.purchasePlanNum/t.contractNum");
		}
		Sort sort = new Sort(new Order(Direction.ASC,"t.contract.hospitalName"),new Order(Direction.ASC,"t.contract.code"),new Order(Direction.ASC,"t.product.code"),new Order(Direction.ASC,"t.contract.effectiveDate"));
		pageable.setSort(sort);
		DataGrid<ContractDetail> cdlist = contractDetailService.pageByExecution(currentUser.getProjectCode(), pageable);
		for (ContractDetail cd : cdlist.getRows()) {
			Contract c = cd.getContract();
			Product p = cd.getProduct();
			Map<String, Object> rltMap = new HashMap<>();
			rltMap.put("hospitalName", c.getHospitalName());
			rltMap.put("productCode", p.getCode());
			rltMap.put("productName", p.getName());
			rltMap.put("dosageFormName", p.getDosageFormName());
			rltMap.put("model", p.getModel());
			rltMap.put("packDesc", p.getPackDesc());
			rltMap.put("unitName", p.getUnitName());
			rltMap.put("vendorName", c.getVendorName());
			rltMap.put("producerName", p.getProducerName());
			rltMap.put("price", cd.getPrice());
			rltMap.put("contractNum", cd.getContractNum());
			BigDecimal leftNum = cd.getContractNum().subtract(cd.getPurchasePlanNum());
			rltMap.put("leftNum", leftNum);
			if (cd.getContractNum().compareTo(new BigDecimal(0.00)) == 0) {
				rltMap.put("donePer", "100%");
			} else {
				BigDecimal donePer = (new BigDecimal("1")).subtract(leftNum.divide(cd.getContractNum(),2,BigDecimal.ROUND_HALF_UP))
						.multiply(new BigDecimal("100")); 
				rltMap.put("donePer", donePer+"%");
			}
			rltMap.put("status", cd.getStatus());
			rltMap.put("purchasePlanNum", cd.getPurchasePlanNum());
			rltMap.put("purchaseNum", cd.getPurchaseNum());
			rltMap.put("deliveryNum", cd.getDeliveryNum());
			rltMap.put("returnsNum", cd.getReturnsNum());
			rltMap.put("closedNum", cd.getClosedNum());
			rltMap.put("contractCode", c.getCode());
			rltMap.put("contractMXCode", cd.getCode());
			rltMap.put("startValidDate", c.getStartValidDate());
			rltMap.put("endValidDate", c.getEndValidDate());
			rltMap.put("effectiveDate", c.getEffectiveDate());
			
			rltList.add(rltMap);
		}
		
		DataGrid<Map<String, Object>> page = new DataGrid<>(rltList, pageable, cdlist.getTotal());
		return page;
	}

	@RequestMapping("/export")
	public void export(PageRequest pageable,@CurrentUser User currentUser,HttpServletResponse resp) {
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(this.filterRules)){
			pageable.setFilterRules(this.filterRules);
		}
		int orgType = currentUser.getOrganization().getOrgType();
		String orgCode = currentUser.getOrganization().getOrgCode();
		Map<String, Object> query = pageable.getQuery();
		String status = (String)query.get("t#status_L_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_L_EQ", Status.valueOf(status));
		}
		if(orgType == 1){//医院
			query.put("t#contract.hospitalCode_S_EQ", orgCode);
		}else if(orgType == 2){//供应商
			query.put("t#contract.vendorCode_S_EQ", orgCode);
		}
		Sort sort = new Sort(new Order(Direction.ASC,"t.contract.hospitalName"),new Order(Direction.ASC,"t.contract.code"),new Order(Direction.ASC,"t.product.code"),new Order(Direction.ASC,"t.contract.effectiveDate"));
		pageable.setSort(sort);
		List<ContractDetail> cdlist = contractDetailService.listByExecution(currentUser.getProjectCode(), pageable);
		
//		String[] heanders = {"药品名称","通用名","剂型","规格","包装规格","生产企业","质量层次","合同剩余量","单价","单位","供应商名称","GPO名称","药品编码","供应商编码","有效期","合同量","采购量"};
		String[] heanders = {"药品名称","通用名","剂型","规格","包装规格","生产企业","质量层次","合同剩余量","单价","单位","供应商名称","GPO名称","药品编码","供应商编码","有效期","合同量","采购确认量","医院名称","单价","执行率","采购计划量","配送数量","退货数量","结案数量","合同编码","合同明细编号","合同有效期起","合同有效期止","合同生效时间"};

		OutputStream out = null;
		Workbook wb = new HSSFWorkbook();
	 	try {
			 Sheet sh = wb.createSheet("sheet1");
			 Row row = sh.createRow(0);
			 for(int i = 0;i<heanders.length;i++) {
				 Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(heanders[i]);
			 }
			 for(int i = 0;i<cdlist.size();i++) {
				 ContractDetail cd = cdlist.get(i);
				 Contract c = cd.getContract();
				 Product p = cd.getProduct();
				 
				 row = sh.createRow(i+1);
				 Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getName());
				 cell = row.createCell(1, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getGenericName());
				 cell = row.createCell(2, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getDosageFormName());
				 cell = row.createCell(3, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getModel());
				 cell = row.createCell(4, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getPackDesc());
				 cell = row.createCell(5, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getProducerName());

				 cell = row.createCell(6, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getQualityLevel());
				 
				 cell = row.createCell(7, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(cd.getContractNum().subtract(cd.getPurchasePlanNum()).compareTo(BigDecimal.ZERO)==-1?0:cd.getContractNum().subtract(cd.getPurchasePlanNum()).doubleValue());

				 cell = row.createCell(8, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(cd.getPrice().doubleValue());
				 
				 cell = row.createCell(9, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getUnitName());
				 
				 cell = row.createCell(10, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(c.getVendorName());
				 
				 cell = row.createCell(11, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getGpoName());
				 
				 cell = row.createCell(12, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(p.getCode());
				 cell = row.createCell(13, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(c.getVendorCode());
				 
				 cell = row.createCell(14, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(c.getEndValidDate());

				 cell = row.createCell(15, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(cd.getContractNum().doubleValue());

				 cell = row.createCell(16, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(cd.getPurchaseNum().doubleValue());

				 cell = row.createCell(17, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(c.getHospitalName());//医院名称

				 cell = row.createCell(18, Cell.CELL_TYPE_STRING);
				 BigDecimal price = cd.getPrice();
				 if(price == null){
				     cell.setCellValue("0");
                 }else{
                     cell.setCellValue(cd.getPrice().toString());//单价
                 }

				 cell = row.createCell(19, Cell.CELL_TYPE_STRING);//执行率
                 if (cd.getContractNum().compareTo(new BigDecimal(0.00)) == 0) {
                     cell.setCellValue("100%");
                 } else {
                     BigDecimal donePer = (new BigDecimal("1")).subtract((cd.getContractNum().subtract(cd.getPurchasePlanNum())).divide(cd.getContractNum(),2,BigDecimal.ROUND_HALF_UP))
                             .multiply(new BigDecimal("100"));
                     cell.setCellValue(donePer+"%");
                 }

				 cell = row.createCell(20, Cell.CELL_TYPE_STRING);
                 BigDecimal purchase = cd.getPurchasePlanNum();
                 if(purchase == null){
                     cell.setCellValue("0");
                 }else{
                     cell.setCellValue(cd.getPurchasePlanNum().toString());//采购计划量
                 }

				 cell = row.createCell(21, Cell.CELL_TYPE_STRING);
				 BigDecimal delivery = cd.getDeliveryNum();
				 if(delivery == null){
				     cell.setCellValue("0");
                 }else{
                     cell.setCellValue(cd.getDeliveryNum().toString());//配送数量
                 }

				 cell = row.createCell(22, Cell.CELL_TYPE_STRING);//退货数量
                 BigDecimal returns = cd.getReturnsNum();
                 if(returns == null){
                     cell.setCellValue("0");
                 }else{
                     cell.setCellValue(returns.toString());
                 }

				 cell = row.createCell(23, Cell.CELL_TYPE_STRING);
				 BigDecimal bigDecimal = cd.getClosedNum();
				 if(bigDecimal == null){
                     cell.setCellValue("0");
                 }else{
                     cell.setCellValue(bigDecimal.toString());//结案数量
                 }

				 cell = row.createCell(24, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(c.getCode());//合同编号

				 cell = row.createCell(25, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(cd.getCode());//合同明细编号

				 cell = row.createCell(26, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(c.getStartValidDate());//合同有效期起

				 cell = row.createCell(27, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(c.getEndValidDate());//合同有效期止

				 cell = row.createCell(28, Cell.CELL_TYPE_STRING);
				 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				 cell.setCellValue(sdf.format(c.getEffectiveDate()));//合同生效时间
			 }

			 resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			 resp.setHeader("Content-Disposition", "attachment; filename=product.xls");
			 out = resp.getOutputStream();
			 wb.write(out);  
			 out.flush();
			 wb.close();
			 out.close();
	 	} catch (IOException e) {
	 		e.printStackTrace();
	 	}  finally {
			if (wb!=null) {
		 		try {
					wb.close();
				} catch (IOException e) {
				}
			}
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
