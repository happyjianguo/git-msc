package com.shyl.msc.b2b.order.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.ICartDao;
import com.shyl.msc.b2b.order.entity.Cart;
import com.shyl.msc.b2b.order.service.ICartService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;
/**
 * 购物车Service实现类
 * 
 * @author a_Q
 *
 */
@Service
public class CartService extends BaseService<Cart, Long> implements ICartService {
	private ICartDao cartDao;

	public ICartDao getCartDao() {
		return cartDao;
	}

	@Resource
	public void setCartDao(ICartDao cartDao) {
		this.cartDao = cartDao;
		super.setBaseDao(cartDao);
	}

	@Resource IPurchaseOrderService purchaseOrderService;
	@Resource IGoodsService goodsService;
	@Resource IGoodsPriceService goodsPriceService;
	@Resource IHospitalService hospitalService;
	@Resource ICompanyService companyService;
	
	@Override
	@Transactional(readOnly=true)
	public Cart findByHopitalAndGoods(String projectCode, String hospitalCode, Long goodsPriceId) {
		return cartDao.findByHopitalAndGoods(hospitalCode, goodsPriceId);
	}

	
	@Override
	@Transactional(readOnly=true)
	public List<Map<String, Object>> queryByHospital(String projectCode, String hospitalCode) {
		return cartDao.queryByHospital(hospitalCode);
	}

	@Override
	@Transactional
	public String doExcelH(String projectCode, String[][] excelarr,User user) throws Exception {
		Hospital hospital = hospitalService.getById(projectCode, user.getOrganization().getOrgId().longValue());
		if(hospital == null){
			return "该帐号不是医院帐号，无权操作";
		}
		for (int i = 0; i < excelarr.length; i++) {
			String[] row = excelarr[i];
			//row ,0药品编码	1供应商code 2数量 
			Integer num = 0;
			try {
				if(row[2].trim().equals(""))
					continue;
				num = Integer.valueOf(row[2]);
			} catch (Exception e) {
				continue;
			}

			if(num<=0){
				continue;
			}
			
			Goods g = goodsService.getByProductAndHospital(projectCode, row[0],hospital.getCode(),0);
			if(g == null){
				throw new MyException("第"+i+"笔数据异常，药品编码"+row[0]+"不存在");
				//return "第"+i+"笔数据异常，药品编码"+row[0]+"不存在";
			}
			
			Company company = companyService.findByCode(projectCode,row[1], "isVendor=1");
			if(company == null){
				throw new MyException("第"+i+"笔数据异常，供应商编码"+row[1]+"不存在");
			}
			
			GoodsPrice gp = goodsPriceService.getByProductCodeAndVender(projectCode, g.getProductCode(), company.getCode(), hospital.getCode());
			
			if(gp == null){
				throw new MyException("第"+i+"笔数据异常，找不到对应的药品价格");
			}
			
			
			
			Cart c = this.findByHopitalAndGoods(projectCode, hospital.getCode(), gp.getId());
			if(c == null){
				c = new Cart();
				c.setHospitalCode(hospital.getCode());
				c.setGoodsPriceId(gp.getId());
				c.setNum(num);
				c.setCreateUser(user.getEmpId());
				cartDao.save(c);
			}else{
				num = num + c.getNum();
				c.setNum(num);
				cartDao.update(c);
			}
		}
		 return "成功导入"+excelarr.length+"笔数据";
	}
	
	
}
