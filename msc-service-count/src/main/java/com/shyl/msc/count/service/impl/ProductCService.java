package com.shyl.msc.count.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.count.dao.IProductCDao;
import com.shyl.msc.count.entity.ProductC;
import com.shyl.msc.count.service.IProductCService;
import com.shyl.msc.dm.entity.Product;

@Service
@Transactional(readOnly = true)
public class ProductCService extends BaseService<ProductC, Long> implements IProductCService {
	private Map<String, Map<String, ProductC>> data = new HashMap<String, Map<String, ProductC>>();

	private IProductCDao productCDao;

	public IProductCDao getProductCDao() {
		return productCDao;
	}

	@Resource
	public void setProductCDao(IProductCDao productCDao) {
		this.productCDao = productCDao;
		super.setBaseDao(productCDao);
	}

	@Override
	public ProductC getProductC(String projectCode, String month, Product product) {
		Map<String, ProductC> productCs = data.get(projectCode);
		if (productCs == null) {
			productCs = new HashMap<String, ProductC>();
			data.put(projectCode, productCs);
		}
		ProductC productC = productCs.get(month + product.getId());
		if (productC == null) {
			productC = productCDao.getByKey(month, product.getId());
			if (productC == null) {
				productC = new ProductC();
				productC.setMonth(month);
				productC.setProduct(product);
				productC.setContractSum(new BigDecimal(0));
				productC.setContractPurchaseSum(new BigDecimal(0));
				productC.setPurchaseTimes(0);
				productC.setDeliveryTimes(0);
				productC.setShortSupplyTimes(0);
				productC.setShortSupplySum(new BigDecimal(0));
				productC.setValidityDayCount(0);
				productC.setPurchaseNum(new BigDecimal(0));
				productC.setPurchaseSum(new BigDecimal(0));
				productC.setDeliveryNum(new BigDecimal(0));
				productC.setDeliverySum(new BigDecimal(0));
				productC.setInOutBoundNum(new BigDecimal(0));
				productC.setInOutBoundSum(new BigDecimal(0));
				productC.setReturnsNum(new BigDecimal(0));
				productC.setReturnsSum(new BigDecimal(0));

				productCDao.save(productC);
			}
			productCs.put(month + product.getId(), productC);
		}
		return productC;
	}

	@Override
	public void updateBatch(String projectCode) {
		List<ProductC> list = new ArrayList<ProductC>();
		Map<String, ProductC> map = data.get(projectCode);
		if (map != null) {
			for (String key : map.keySet()) {
				list.add(map.get(key));
			}
		}
		productCDao.updateBatch(list);
		data.put(projectCode, null);
	}

	@Override
	public Map<String, Object> countByMonth(String month) {
		return productCDao.countByMonth(month);
	}

	@Override
	public List<Map<String, Object>> reportCTrade(String projectcode,String year) {
		return productCDao.reportCTrade(year);
	}

	@Override
	public DataGrid<Map<String, Object>> reportCGoodsTrade(String projectCode,String dateS, String dateE, PageRequest pageable) {
		return productCDao.reportCGoodsTrade(dateS, dateE, pageable);
	}
}
