package com.shyl.msc.dm.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.Goods;
/**
 * 商品Service接口
 * 
 * @author a_Q
 *
 */
public interface IGoodsService extends IBaseService<Goods, Long> {

	/**
	 * 根据供应商查goods
	 * @param pageable
	 * @param vendorCode
	 * @return
	 */
	public DataGrid<Goods> pageByGPO(@ProjectCodeFlag String projectCode, PageRequest pageable, String vendorCode);

	/**
	 * 有效商品列表
	 * @param pageable
	 * @return
	 */
	public DataGrid<Goods> pageByEnabled(@ProjectCodeFlag String projectCode, PageRequest pageable);

	/**
	 * 
	 * @param pageable 
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<Goods> pageByHospital(@ProjectCodeFlag String projectCode, PageRequest pageable, String hospitalCode);
	
	
	/**
	 * 根据产品id和医院id查询药品目录
	 * @param productCode
	 * @param hospitalCode
	 * @param isDisabled 
	 * @return
	 */
	public Goods getByProductAndHospital(@ProjectCodeFlag String projectCode, String productCode,String hospitalCode, int isDisabled);

	/**
	 * 根据医院查GOODS列表
	 * @param hospitalCode
	 * @param lastDate
	 * @return
	 */
	public List<Map<String, Object>> listByHospital(@ProjectCodeFlag String projectCode, String hospitalCode, String lastDate);

	/**
	 * 
	 * @param productCode
	 * @param hospitalCode
	 * @return
	 */
	public Goods getByProductCodeAndHosiptal(@ProjectCodeFlag String projectCode, String productCode, String hospitalCode);


	/**
	 * 查询供应商可以补货的 goods
	 * @param hospitalCode
	 * @param vendorCode 
	 * @param pageable 
	 * @return
	 */
	public DataGrid<Map<String, Object>> listForAutoOrder(@ProjectCodeFlag String projectCode, String hospitalCode, String vendorCode, PageRequest pageable);

	/**
	 * 查询需要补货的医院
	 * @param vendorCode 
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> hospitalListForAutoOrder(@ProjectCodeFlag String projectCode, String vendorCode, PageRequest pageable);

	/**
	 * 
	 * @param productCode
	 * @param patient
	 * @return
	 */
	public Goods getByCode(@ProjectCodeFlag String projectCode, String productCode);

	public List<Map<String, Object>> listByHospital(@ProjectCodeFlag String projectCode, String hospitalCode);

	public List<Map<String, Object>> listByHospitalWithGPO(@ProjectCodeFlag String projectCode, String hospitalCode);

	/**
	 * 下单page
	 * @param pageable
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<Map<String, Object>> pagePlaceByHospital(@ProjectCodeFlag String projectCode, PageRequest pageable, String hospitalCode);

	/**
	 * 新增医院药品目录，并默认选择所有供应商
	 * @param productId
	 * @param user 
	 */
	public void addGoodsAndGoodsprice(@ProjectCodeFlag String projectCode, Long productId, String hospitalCode);

	/**
	 * 连表查询 医院和 药品信息
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> queryHospitalAndProduct(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public List<Map<String, Object>> listByProductCode(@ProjectCodeFlag String projectCode, String ypbm);
}
