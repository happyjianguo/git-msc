package com.shyl.msc.hospital.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.util.StringUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsHospitalSource;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsHospitalSourceService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;



@Controller
@RequestMapping("/hospital/goodsSourceAudit")
public class HospitalGoodsSourceAuditController extends BaseController {

	@Resource
	private IGoodsHospitalSourceService goodsHospitalSourceService;
	@Resource
	private IGoodsService	goodsService;
	@Resource
	private IProductService	productService;
	
	
	protected void init(WebDataBinder binder) {

	}

	@RequestMapping("")
	public String home() {
		return "hospital/goodsSourceAudit/list";
	}

	/**
	 * 医院就编码分页
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/sourcePage", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> sourcePage(PageRequest page, @CurrentUser User user) {
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
			page.setQuery(query);
		}
		if (query.get("t#status_L_EQ") == null) {
			query.put("t#status_L_EQ", 2);
		}
		Long hospitalId = -1L;
		if (user != null) {
			hospitalId = user.getOrganization().getOrgId();
		}
		query.put("t#hospitalId_L_EQ", hospitalId);
		return goodsHospitalSourceService.npquery(user.getProjectCode(), page);
	}
	
	/**
	 * 审核对照信息
	 * @param id
	 * @param status
	 * @return
	 */
	@RequestMapping(value="/audit", method=RequestMethod.POST)
	@ResponseBody
	public Message audit(String ids, Long status, @CurrentUser User user) {
		Message	msg = new Message();
		try{
			Long orgId = user.getOrganization().getOrgId();
			int i =goodsHospitalSourceService.updateStatusByIds(user.getProjectCode(), ids, status==1L?99L:4L,
					orgId);
			if (i <=0) {
				msg.setSuccess(false);
			} else if (status == 1L) {
				syncToGoods(user.getProjectCode(), ids, orgId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	
	/**
	 * 同步到goods中去
	 * @param ids
	 * @param hospitalId
	 * @param status
	 */
    private void syncToGoods(String projectCode, String idStr, Long hospitalId) {
    	if (StringUtils.isEmpty(idStr)) {
    		return;
    	}
    	String[] ids = idStr.split(",");
    	for(String id : ids) {
    		if (!StringUtils.isEmpty(id) && StringUtil.isNumber(id)) {
    			GoodsHospitalSource source = goodsHospitalSourceService.getById(projectCode, Long.valueOf(id));
    			Product	product = productService.getByCode(projectCode, source.getProductCode());
    			//判断是否已经存在了
    			Goods goods = goodsService.getByProductCodeAndHosiptal(projectCode, source.getProductCode(), source.getHospitalCode());
    			if (goods == null) {
    				goods = new Goods();
        			goods.setStockUpLimit(0);
        			goods.setStockDownLimit(0);
        			goods.setStockNum(new BigDecimal(0));
        			goods.setIsDisabled(0);
        			goods.setProduct(product);
        			goods.setProductCode(product.getCode());
    			}
    			goods.setHospitalCode(source.getHospitalCode());
    			if (goods.getId() == null) {
        			goodsService.save(projectCode, goods);
    			} else {
        			goodsService.update(projectCode, goods);
    			}
    		}
    	}
    }
}
