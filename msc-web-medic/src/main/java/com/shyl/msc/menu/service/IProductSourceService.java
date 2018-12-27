package com.shyl.msc.menu.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.DrugType;
import com.shyl.msc.menu.entity.ProductSource;
import com.shyl.sys.entity.AttributeItem;
/**
 * 国家标准药品目录信息
 * 
 * @author a_Q
 *
 */
public interface IProductSourceService extends IBaseService<ProductSource, Long> {

	/**
	 * 根据编码查询source
	 * @param code
	 * @return
	 */
	public ProductSource getByCode(String code);
	
	/**
	 * 根据本位码获取数据
	 * @param standardCode
	 * @return
	 */
	public ProductSource getByStandardCodeOnly(String standardCode);
	
	/**
	 * 根据本位码、通用名、包装获取资源信息
	 * @param standardCode
	 * @param genericName
	 * @param model
	 * @param packDesc
	 * @return
	 */
	public ProductSource getByStandardCodeAndPack(String standardCode, String genericName, String model, String packDesc);
	
	/**
	 * 根据药品名称查询
	 * @param name
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> npquery(List<String> productWords, List<String> dosageWords, 
			List<String> producerWords, String productCode, PageRequest pageable);
	
	/**
	 * 重置编码前修改医院的编码并删除药品类目
	 */
	public void deletByReset();
	
	/**
	 * 获取最大药品编号
	 */
	public String getMaxCode();

	/**
	 * 获取规格数目
	 * @param genericName
	 * @return
	 */
	public String getMaxModelCode(String newcode, String model);

	/**
	 * 获取包装数目
	 * @param genericName
	 * @param model
	 * @return
	 */
	public String getMaxPackCode(String newcode, String model, String packDesc);
	

	/**
	 * 保持药品属性
	 * @param attributeNo
	 * @param name
	 * @return
	 */
	public AttributeItem saveAttribute(String attributeNo, String name);
	/**
	 * 保持药品分类
	 * @param parentId
	 * @param name
	 * @param treePath
	 * @param typ
	 * @return
	 */
	public DrugType saveDrugType(Long parentId, String name, String treePath, String typ);
	
	public ProductSource getByNewcode(String code, String model, String packDesc);
}
