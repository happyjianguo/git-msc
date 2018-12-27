package com.shyl.msc.dm.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageParam;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.Product;
/**
 * 产品DAO接口
 * 
 * @author a_Q
 *
 */
public interface IProductDao extends IBaseDao<Product, Long> {

	/**
	 * 
	 * @param code
	 * @return
	 */
	public Product getByCode(String code);

	public DataGrid<Map<String, Object>> npquery(String code, String name, PageRequest pageable);

	/**
	 * 
	 * @return
	 */
	public Map<String, Object> count();

	
	/**
	 * 查询GPO药品集中情况
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getCentralizedPercent(int maxsize,String year,String month);
	
	/**
	 * 查询GPO药品所占百分比
	 * @return
	 */
	public BigDecimal getGpoPercent();
	
	/**
	 * 根据药品名称查询
	 * @param name
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> npquery(List<String> productWords, List<String> dosageWords, 
			List<String> producerWords, String productCode, PageRequest pageable);

	public DataGrid<Product> pageByGPO(PageRequest pageable, List<Long> gpoIds);

	public List<Product> listByGPOS(PageRequest pageRequest, List<Long> gpoIds);

	DataGrid<Product> pageInAttribute(PageRequest pageable);

	public DataGrid<Product> pageInProductVendor(PageRequest pageable, String hospitalCode, Integer isInProductVendor);

	public List<Map<String, Object>> listByGPO(Long gpoId, String scgxsj);

	public List<Map<String, Object>> listByDate(String scgxsj);

	public String getMaxCode();

	public String getMaxModelCode(String newcode, String model);

	public String getMaxPackCode(String newcode, String model, String packDesc);
	
	public Product getByPriceFileNo(String priceFileNo, String[] models, String convertRatio);
	
	public Product getByStandardCode(String standardCode, String[] models, String convertRatio);
	
	public Product getByAuthorizeNo(String licenseNumber, String[] models, String convertRatio);
	
	public Product getByGenericName(String genericName, String[] models, String producerName, String convertRatio);

	public List<Map<String, Object>> listByGPOAndCode(Long gpoId, String code);

	public List<Map<String, Object>> producerComb(String projectCode, String productName);

	public List<Map<String, Object>> productPiciMap();
	
	public DataGrid<Product> listByVendorAndDate(String vendorCode, String startDate, String endDate, PageParam pageable);

	public DataGrid<Map<String, Object>> queryByGoodsHospital(String scgxsj, PageRequest page);

}
