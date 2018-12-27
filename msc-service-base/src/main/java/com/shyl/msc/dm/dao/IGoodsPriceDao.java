package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.GoodsPrice;

public interface IGoodsPriceDao extends IBaseDao<GoodsPrice, Long> {

	public List<GoodsPrice> listByGoods(Long goodsId);

	public GoodsPrice getByProductCodeAndVender(String productCode, String vendorCode, String hospitalCode);

	/**
	 * 查询prdocutid 对应的所有，不考虑医院
	 * @param productCode
	 * @param vendorCode
	 * @return
	 */
	public List<GoodsPrice> findByProduct(String productCode, String vendorCode);


	/**
	 * 查询医院价格数据
	 * @param hospitalCode
	 * @param scgxsj
	 * @return
	 */
	public List<GoodsPrice> listByHospital(String hospitalCode, String scgxsj);

	public GoodsPrice findByKey(String productCode, String vendorCode, String hospitalCode, Integer isDisabled,Integer isDisabledByH, Integer isFormContract);

	public List<Map<String, Object>> getVendorList(String hospitalCode);

	public List<GoodsPrice> listByIsFormContract();

	public List<GoodsPrice> listByProductCode(String productCode);

}
