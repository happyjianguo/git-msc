package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.Goods;
/**
 * 商品DAO接口
 * 
 * @author a_Q
 *
 */
public interface IGoodsDao extends IBaseDao<Goods, Long> {

	/**
	 * 根据vendorCode查goods
	 * @param pageable
	 * @param vendorCode
	 * @return
	 */
	public DataGrid<Goods> pageByGPO(PageRequest pageable, String vendorCode);

	/**
	 * 有效商品分页
	 * @param pageable
	 * @return
	 */
	public DataGrid<Goods> pageByEnabled(PageRequest pageable);
	
	/**
	 * 根据产品Id查商品
	 * @param code
	 * @return
	 */
	public Goods getByProduct(String productCode);

	/**
	 * 根据医院查
	 * @param pageable
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<Goods> pageByHospital(PageRequest pageable, String hospitalCode);

	/**
	 * 根据医院查
	 * @param pageable
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<Map<String, Object>> pagePlaceByHospital(PageRequest pageable, String hospitalCode);
	
	/**
	 * 根据产品id和医院id查询药品目录
	 * @param productCode
	 * @param hospitalCode
	 * @param isDisabled 
	 * @return
	 */
	public Goods getByProductAndHospital(String productCode,String hospitalCode, int isDisabled);

	/**
	 * 
	 * @param productCode
	 * @param lastDate
	 * @return
	 */
	public List<Map<String, Object>> listByHospital(String productCode, String lastDate);

	/**
	 * 
	 * @param productCode
	 * @param hospitalCode
	 * @return
	 */
	public Goods getByProductCodeAndHosiptal(String productCode, String hospitalCode);

	public DataGrid<Map<String, Object>> listForAutoOrder(String hospitalCode, String vendorCode, PageRequest pageable);

	public DataGrid<Map<String, Object>> hospitalListForAutoOrder(String vendorCode, PageRequest pageable);

	public Goods getByCode(String hospitalCode);

	/**
	 * 查询个人goods
	 * @param productId
	 * @param patient
	 * @return
	 */
	public Goods getByProductIdAndType(String productCode);

	/**
	 * 设置价格批次作业 获取list
	 * @param productCode
	 * @param vendorCode
	 * @param hospitalCode
	 * @return
	 */
	public List<Goods> findByPriceKey(String productCode, String vendorCode,  String hospitalCode);

	public List<Map<String, Object>> listByHospital(String hospitalCode);

	public List<Map<String, Object>> listByHospitalWithGPO(String hospitalCode);

	/**
	 * 根据productCode查询
	 * @param productCode
	 * @return
	 */
	public List<Goods> listByProductCode(String productCode);

	public DataGrid<Map<String, Object>> queryHospitalAndProduct(PageRequest pageable);

	public List<Map<String, Object>> listByProductCodeMap(String productCode);

}
