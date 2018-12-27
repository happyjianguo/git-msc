package com.shyl.msc.dm.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageParam;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.Product;
/**
 * 产品Service接口
 * 
 * @author a_Q
 *
 */
public interface IProductService extends IBaseService<Product, Long> {

	/**
	 * 
	 * @param code
	 * @return
	 */
	public Product getByCode(@ProjectCodeFlag String projectCode, String code);

	/**
	 * 未添加目录 分页查询
	 * @param name 
	 * @param code
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> npquery(@ProjectCodeFlag String projectCode, String code, String name, PageRequest pageable);

	/**
	 * 
	 * @return
	 */
	public Map<String, Object> count(@ProjectCodeFlag String projectCode);

	/**
	 * 查询GPO药品集中情况
	 * @return
	 */
	public List<Map<String, Object>> getCentralizedPercent(@ProjectCodeFlag String projectCode, int maxsize,String year,String month);
	
	/**
	 * 查询GPO药品所占百分比
	 * @return
	 */
	public Map<String,Object> getGpoPercent(@ProjectCodeFlag String projectCode);

	
	/**
	 * 根据药品名称查询
	 * @return
	 */
	public DataGrid<Map<String, Object>> npquery(@ProjectCodeFlag String projectCode, List<String> productWords, List<String> dosageWords, 
			List<String> producerWords, String productCode, PageRequest pageable);
	
	DataGrid<Product> query(@ProjectCodeFlag String projectCode, PageRequest pageable);
	
	public DataGrid<Product> pageByGPO(@ProjectCodeFlag String projectCode, PageRequest pageable, List<Long> gpoCodes);

	public List<Product> listByGPOS(@ProjectCodeFlag String projectCode, PageRequest pageRequest, List<Long> gpoIds);
	
	public DataGrid<Product> pageInAttribute(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public DataGrid<Product> pageInProductVendor(@ProjectCodeFlag String projectCode, PageRequest pageable, String hospitalCode, Integer isInProductVendor);

	public List<Map<String, Object>> listByGPO(@ProjectCodeFlag String projectCode, Long gpoId, String scgxsj);

	public List<Map<String, Object>> listByDate(@ProjectCodeFlag String projectCode, String scgxsj);
	
	public String getMaxCode(@ProjectCodeFlag String projectCode);
	
	public String getMaxModelCode(@ProjectCodeFlag String projectCode, String newcode, String model);

	public String getMaxPackCode(@ProjectCodeFlag String projectCode, String newcode, String model, String packDesc);
	
	public List<Map<String, Object>> listByGPOAndCode(@ProjectCodeFlag String projectCode, Long gpoId, String code);

	public List<Map<String, Object>> producerComb(@ProjectCodeFlag String projectCode, String productName);

	/**
	 * product和批次的对应关系
	 * @return
	 */
	public List<Map<String, Object>> productPiciMap();
	
	public DataGrid<Product> listByVendorAndDate(@ProjectCodeFlag String projectCode, String vendorCode, String startDate, String endDate, PageParam pageable);

	public DataGrid<Map<String, Object>> queryByGoodsHospital(String projectCode, String scgxsj, PageRequest page);

	void getByAllByAsync(@ProjectCodeFlag String projectCode,Integer i);
	
}
