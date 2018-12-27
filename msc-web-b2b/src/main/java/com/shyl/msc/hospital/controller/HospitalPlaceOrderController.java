package com.shyl.msc.hospital.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.Cart;
import com.shyl.msc.b2b.order.service.ICartService;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 下单
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/hospital/placeOrder")
public class HospitalPlaceOrderController extends BaseController {

	@Resource
	private IGoodsService goodsService;
	@Resource
	private IGoodsPriceService goodsPriceService;
	
	@Resource
	private ICartService cartService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	@RequestMapping(value="/list")
	public String list(){
		return "/hospital/placeOrder/list";
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<Map<String, Object>> page = new DataGrid<Map<String, Object>>();
		if(user.getOrganization().getOrgType() == 1){
			String hospitalCode = user.getOrganization().getOrgCode();
			page = goodsService.pagePlaceByHospital(user.getProjectCode(), pageable,hospitalCode);
			for (Map<String, Object> m : page.getRows()) {
				Cart c  =cartService.findByHopitalAndGoods(user.getProjectCode(), hospitalCode,Long.parseLong(m.get("GOODSPRICEID")+""));
				if(c != null){
					m.put("GOODSNUM", c.getNum());
				}
			}
		}
		return page;
	}
	
	@RequestMapping(value="/vendorList")
	@ResponseBody
	public List<Map<String, Object>> vendorList(@CurrentUser User user){
		List<Map<String, Object>> list = null;
		if(user.getOrganization().getOrgType() == 1){
			list = goodsPriceService.getVendorList(user.getProjectCode(), user.getOrganization().getOrgCode());
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("ID", "");
			m.put("NAME", "-请选择-");
			list.add(0, m);
		}
		
		return list;
	}
	
	@RequestMapping(value="/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Integer goodsNum, Long goodsId,Long goodsPriceId, @CurrentUser User user){
		System.out.println("-----goodsNum----"+goodsNum);
		System.out.println("-----goodsPriceId----"+goodsPriceId);
		System.out.println("-----user----"+user.getEmpId());
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgId() != null){
				Integer type = user.getOrganization().getOrgType();
				if(type == 1){
					String hospitalCode = user.getOrganization().getOrgCode();
					Cart cart = cartService.findByHopitalAndGoods(user.getProjectCode(), hospitalCode,goodsPriceId);
					if(cart == null){
						cart = new Cart();
						cart.setHospitalCode(hospitalCode);
						cart.setNum(goodsNum);
						cart.setGoodsPriceId(goodsPriceId);
						cart.setCreateUser(user.getEmpId());
						cartService.save(user.getProjectCode(), cart);
					}else {
						//goodsNum = cart.getNum() + goodsNum;
						cart.setNum(goodsNum);
						cart.setModifyUser(user.getEmpId());
						cartService.update(user.getProjectCode(), cart);
					}
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
	 * 导出医院药品目录
	 * @param user
	 * @param resp
	 */
	@RequestMapping("/exportExcel")
	public void exportExcel(@CurrentUser User user, HttpServletResponse resp){
		try {
			if(user.getOrganization().getOrgType() == 1){
				String hospitalCode = user.getOrganization().getOrgCode();
				String name = user.getOrganization().getOrgName()+"下单模板";
				List<Map<String, Object>> dataList = goodsService.listByHospitalWithGPO(user.getProjectCode(), hospitalCode);
				String heanders [] = {"药品编码","供应商编码","数量","价格","单位","药品名称","通用名",
						"剂型","规格","包装规格","生产企业","供应商名称"};
				String beannames [] = {"PRODUCTCODE","VENDORCODE","NUM","FINALPRICE","UNITNAME","PRODUCTNAME",
						"GENERICNAME","DOSAGEFORMNAME","MODEL","PACKDESC","PRODUCERNAME","VENDORNAME"};
				Map<String, Boolean> lineMap = new HashMap<>();
				lineMap.put("PRODUCTCODE", true);
				lineMap.put("VENDORCODE", true);
				lineMap.put("NUM", true);
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
