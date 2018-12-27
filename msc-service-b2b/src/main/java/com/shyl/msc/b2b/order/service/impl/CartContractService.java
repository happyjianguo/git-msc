package com.shyl.msc.b2b.order.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.ICartContractDao;
import com.shyl.msc.b2b.order.entity.CartContract;
import com.shyl.msc.b2b.order.service.ICartContractService;
import com.shyl.msc.b2b.plan.dao.IContractDao;
import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;
/**
 * 购物车Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class CartContractService extends BaseService<CartContract, Long> implements ICartContractService {
	private ICartContractDao cartContractDao;

	public ICartContractDao getCartContractDao() {
		return cartContractDao;
	}

	@Resource
	public void setCartContractDao(ICartContractDao cartContractDao) {
		this.cartContractDao = cartContractDao;
		super.setBaseDao(cartContractDao);
	}
	
	@Resource
	private IContractDao contractDao;
	
	@Resource
	private IContractDetailDao contractDetailDao;
	@Resource 
	IHospitalService hospitalService;
	@Override
	public CartContract findByHopitalAndGoods(String projectCode, String hospitalCode, Long contractDId) {
		return cartContractDao.findByHopitalAndGoods( hospitalCode, contractDId);
	}

	@Override
	@Transactional
	public void mkCartContract(String projectCode, Integer goodsNum, Long contractDId, User user) throws Exception   {
		ContractDetail cd =	contractDetailDao.getById(contractDId);
		Contract c = cd.getContract();
		String hospitalCode = c.getHospitalCode();

		//增修购物车
		CartContract cc = this.findByHopitalAndGoods(projectCode, hospitalCode,contractDId);
		if(cc == null){
			cc = new CartContract();
			cc.setHospitalCode(hospitalCode);
			cc.setNum(goodsNum);
			cc.setContractDetailId(contractDId);
			this.save(projectCode, cc);
		}else {
			//goodsNum = cc.getNum() + goodsNum;
			cc.setNum(goodsNum);
			this.update(projectCode, cc);
		}
		//更新合同购物车量
		if(cd != null){
			cd.setCartNum(new BigDecimal(goodsNum));
			contractDetailDao.update(cd);
		}
		
	}

	@Override
	public List<CartContract> listByHospital(String projectCode, String hospitalCode) {
		return cartContractDao.listByHospital(hospitalCode) ;
	}

	@Override
	public List<Map<String, Object>> vendorList(String projectCode, String hospitalCode) {
		return cartContractDao.vendorList(hospitalCode);
	}

	@Override
	@Transactional
	public void delete(String projectCode, Long[] cartids) {
		for (Long id : cartids) {
			CartContract cc = cartContractDao.getById(id);
			
			//更新合同购物车量
			ContractDetail cd = contractDetailDao.getById(cc.getContractDetailId());
			if(cd != null){
				BigDecimal b = cd.getCartNum() == null?new BigDecimal("0"):cd.getCartNum();
				cd.setCartNum(b.subtract(new BigDecimal(cc.getNum())));
				contractDetailDao.update(cd);
			}
			cartContractDao.delete(cc);
		}
	}

	@Override
	@Transactional
	public String doExcelH(String projectCode, String[][] excelarr, User user) throws Exception {
		System.out.println("开始处理"+excelarr.length);
		Hospital hospital = hospitalService.getById(projectCode, user.getOrganization().getOrgId().longValue());
		if(hospital == null){
			return "该帐号不是医院帐号，无权操作";
		}
		for (int i = 0; i < excelarr.length; i++) {
			String[] row = excelarr[i];
			//row ,0药品code	1合同明细id 2数量 
			Integer num = 0;
			try {
				if(row[2].trim().equals(""))
					continue;
				num = Integer.valueOf(row[2]);
			} catch (Exception e) {
				continue;
			}
			System.out.println("num="+num);
			if(num<=0){
				continue;
			}
			
			ContractDetail cd = contractDetailDao.findByCode(row[1]);
			if(cd == null){
				throw new MyException("第"+i+"笔数据异常，合同明细"+row[1]+"不存在");
			}
			
			System.out.println("增修");
			//增修购物车
			CartContract cc = this.findByHopitalAndGoods(projectCode, hospital.getCode(),cd.getId());
			if(cc == null){
				cc = new CartContract();
				cc.setHospitalCode(hospital.getCode());
				cc.setNum(num);
				cc.setContractDetailId(cd.getId());
				this.save(projectCode, cc);
			}else {
				//num = cc.getNum() + num;
				cc.setNum(num);
				this.update(projectCode, cc);
			}
			//更细合同购物车量
			if(cd != null){
				//BigDecimal cartnum = cd.getCartNum()==null?new BigDecimal("0"):cd.getCartNum();
				cd.setCartNum(new BigDecimal(num));
				contractDetailDao.update(cd);
			}
		}
		
		 return "成功导入"+excelarr.length+"笔数据";
	}

	@Override
	public DataGrid<Map<String, Object>> queryByHospital(String projectCode, String hospitalCode, PageRequest pageable) {
		return cartContractDao.queryByHospital(hospitalCode,pageable);
	}

	@Override
	public Map<String, Object> getTotal(String projectCode, String orgCode) {
		return cartContractDao.getTotal(orgCode);
	}

}
