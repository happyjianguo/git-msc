package com.shyl.msc.dm.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.enmu.TradeType;
import com.shyl.sys.entity.User;
/**
 * 产品价格Service接口
 * 
 * @author a_Q
 *
 */
public interface IProductPriceService extends IBaseService<ProductPrice, Long> {

	/**
	 * 根据供应商查产品价格
	 * @param pageable
	 * @param vendorCode
	 * @return
	 */
	public DataGrid<ProductPrice> pageByGPO(@ProjectCodeFlag String projectCode, PageRequest pageable, String vendorCode);

	/**
	 * 有效商品列表
	 * @param pageable
	 * @return
	 */
	public DataGrid<ProductPrice> pageByEnabled(@ProjectCodeFlag String projectCode, PageRequest pageable);

	/**
	 * 根据药品编码查商品价格
	 * @param code
	 * @return
	 */
	public ProductPrice getByCode(@ProjectCodeFlag String projectCode, String code);

	/**
	 * 根据医院id 和 药品id 查商品价格
	 * @param hospitalCode
	 * @param productCode
	 * @return
	 */
	public ProductPrice getByProductAndHospital(@ProjectCodeFlag String projectCode, String hospitalCode, String productCode);

	/**
	 * 根据供应商id 和 药品id 查商品价格
	 * @param productCode
	 * @param vendorCode
	 * @param pageable
	 * @return
	 */
	public DataGrid<ProductPrice> queryByProductAndGpo(@ProjectCodeFlag String projectCode, String productCode, String vendorCode, PageRequest pageable);
	
	/**
	 * 
	 * @param productCode
	 * @param type
	 * @return
	 */
	public ProductPrice getByProduct(@ProjectCodeFlag String projectCode, String productCode,Integer type);
	
	/**
	 * 
	 * @param productCode
	 * @param type
	 * @return
	 */
	public ProductPrice getPatientPrice(@ProjectCodeFlag String projectCode, String productCode,String vendorCode);


	/**
	 * 导入excel新增
	 * @param upExcel
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	public String doExcelH(@ProjectCodeFlag String projectCode, String[][] upExcel, User user) throws Exception;

	/**
	 * 今日需要生效的价格
	 * @param today
	 * @return
	 */
	public List<ProductPrice> effectList(@ProjectCodeFlag String projectCode, String today);

	/**
	 * 生效一笔价格
	 * @param productPrice
	 */
	public void effectPrice(@ProjectCodeFlag String projectCode, ProductPrice productPrice);

	/**
	 * 作废价格
	 * @param id
	 */
	public void deletePrice(@ProjectCodeFlag String projectCode, Long id);

	/**
	 * 某药品所有生效的统一价格
	 * @param productCode
	 * @return
	 */
	public List<ProductPrice> listByProduct(@ProjectCodeFlag String projectCode, String productCode);

	/**
	 * 某药品所有生效的指定医院价格
	 * @param productCode
	 * @param orgCode
	 * @return
	 */
	public List<ProductPrice> listByProductAndHospital(@ProjectCodeFlag String projectCode, String productCode, String orgCode);

	public ProductPrice findByKey(@ProjectCodeFlag String projectCode, String code, String vendorCode, String hospitalCode, TradeType type);

	public void saveProductPrice(List<JSONObject> list, String ptdm);

}
