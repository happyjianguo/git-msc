package com.shyl.msc.dm.dao;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.enmu.TradeType;
/**
 * 产品价格DAO接口
 * 
 * @author a_Q
 *
 */
public interface IProductPriceDao extends IBaseDao<ProductPrice, Long> {

	/**
	 * 根据vendorCode查productPrice
	 * @param pageable
	 * @param vendorCode
	 * @return
	 */
	public DataGrid<ProductPrice> pageByGPO(PageRequest pageable, String vendorCode);

	/**
	 * 有效产品价格分页
	 * @param pageable
	 * @return
	 */
	public DataGrid<ProductPrice> pageByEnabled(PageRequest pageable);

	/**
	 * 根据药品编码查产品价格
	 * @param code
	 * @return
	 */
	public ProductPrice getByCode(String code);
	
	/**
	 * 根据产品Id查产品价格
	 * @param i 
	 * @param code
	 * @return
	 */
	public ProductPrice getByProduct(String productCode, TradeType tradeType);

	public ProductPrice getByProductAndHospital(String hospitalCode, String productCode);

	public DataGrid<ProductPrice> queryByProductAndGpo(String productCode, String vendorCode, PageRequest pageable);

	public ProductPrice getPatientPrice(String productCode, String vendorCode);

	public List<ProductPrice> effectList(String today);

	public ProductPrice findByKey(String productCode, String vendorCode, String hospitalCode, TradeType tradeType);

	/**
	 * 查询productCode的所有生效的统一价格
	 * @param productCode
	 * @return
	 */
	public List<ProductPrice> listByProduct(String productCode);

	/**
	 * 查询productCode的所有生效的指定价格
	 * @param productCode
	 * @param hospitalCode
	 * @return
	 */
	public List<ProductPrice> listByProductAndHospital(String productCode, String hospitalCode);

	/**
	 * 所有生效的  productCode+vendorCode
	 * @param productCode
	 * @param vendorCode
	 * @return
	 */
	public List<ProductPrice> effectList(String productCode, String vendorCode);

}
