package com.shyl.msc.menu.dao;


import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.menu.entity.ProductSource;
/**
 * 药品国家标准目录
 * 
 * @author a_Q
 *
 */
public interface IProductSourceDao extends IBaseDao<ProductSource, Long> {

	/**
	 * 通过编码获取标准目录
	 * @param code
	 * @return
	 */
	public ProductSource getByCode(String code);
	
	/**
	 * 通过本位吗获取数据
	 * @param standardCode
	 * @return
	 */
	public ProductSource getByStandardCodeOnly(String standardCode);
	
	/**
	 * 根据本位码、通用名、包装获取资源信息
	 * @param standardCode
	 * @param genericName
	 * @param packDesc
	 * @return
	 */
	public ProductSource getByStandardCodeAndPack(String standardCode, String genericName, String model, String packDesc);
	
	/**
	 * 通过新编码和规格获取到商品信息
	 * @param code
	 * @param model
	 * @return
	 */
	public ProductSource getByNewcode(String code, String model, String packDesc);
	
	/**
	 * 通过编码获取标准目录
	 * @param code
	 * @return
	 */
	public Long getStandardCodeCount(String standardCode);

	
	/**
	 * 根据药品名称查询
	 * @param name
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> npquery(List<String> productWords, List<String> dosageWords, 
			List<String> producerWords, String productCode, PageRequest pageable);
	
	/**
	 * 查询国家目录数据（标准目录不存在）
	 * @param maxsize
	 * @return
	 */
	public List<ProductSource> queryNotinProduct(int maxsize);
	
	/**
	 * 根据医保编号匹配
	 * @param yjCode
	 * @param models
	 * @param convertRatio
	 * @return
	 */
	public ProductSource getByYbdrugsNO(String yjCode, String[] models, String convertRatio);
	
	/**
	 * 通过药交ID匹配
	 * @param yjCode
	 * @param models
	 * @param convertRatio
	 * @return
	 */
	public ProductSource getByYjcode(String yjCode);

	/**
	 * 根据物价id匹配
	 * @param priceFileNo
	 * @param models
	 * @param convertRatio
	 * @return
	 */
	public ProductSource getByPriceFileNo(String priceFileNo, String[] models, String convertRatio);
	
	/**
	 * 根据本位码获取目录信息
	 * @param standardCode
	 * @param model
	 * @param convertRatio
	 * @return
	 */
	public ProductSource getByStandardCode(String standardCode, String[] model, String convertRatio);

	/**
	 * 根据批准文号获取目录信息
	 * @param licenseNumber
	 * @param model
	 * @param convertRatio
	 * @return
	 */
	public ProductSource getByAuthorizeNo(String licenseNumber, String[] model, String convertRatio);
	
	/**
	 * 根据通用名获取目录信息
	 * @param genericName
	 * @return
	 */
	public ProductSource getByGenericName(String genericName, String[] model, 
			String producerName, String convertRatio);
	
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
	 * 获取最大编号
	 */
	public String getMaxCode();
	
	/**
	 * 重置编码前修改医院的编码并删除药品类目
	 */
	public void deletByReset();
	
	/**
	 * 情况code信息
	 */
	public void clearCode();
}
