package com.shyl.msc.dm.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.GoodsPrice;

public interface IGoodsPriceService extends IBaseService<GoodsPrice, Long> {

	public List<GoodsPrice> listByGoods(@ProjectCodeFlag String projectCode, Long goodsId);

	public GoodsPrice getByProductCodeAndVender(@ProjectCodeFlag String projectCode, String productCode, String vendorCode, String hospitalCode) ;

	/**
	 * 根据key查询
	 * @param productCode
	 * @param vendorCode
	 * @param hospitalCode
	 * @param isDisabled
	 * @param isDisabledByH
	 * @param isFormContract
	 * @return
	 */
	public GoodsPrice findByKey(@ProjectCodeFlag String projectCode, String productCode, String vendorCode,  String hospitalCode,Integer isDisabled,Integer isDisabledByH,Integer isFormContract);

	/**
	 * 查询医院价格目录
	 * @param hospitalCode
	 * @param scgxsj
	 * @return
	 */
	public List<GoodsPrice> listByHospital(@ProjectCodeFlag String projectCode, String hospitalCode, String scgxsj);

	/**
	 * 医院可选供应商列表
	 * @param hospitalCode
	 * @return 
	 */
	public List<Map<String, Object>> getVendorList(@ProjectCodeFlag String projectCode, String hospitalCode);

	/**
	 * isFormContract = 1的列表
	 * @return
	 */
	public List<GoodsPrice> listByIsFormContract(@ProjectCodeFlag String projectCode);

	public List<GoodsPrice> listByProductCode(@ProjectCodeFlag String projectCode, String productCode);
}
