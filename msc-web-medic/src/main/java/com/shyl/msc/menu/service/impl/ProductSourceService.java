package com.shyl.msc.menu.service.impl;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IDosageFormDao;
import com.shyl.msc.dm.dao.IDrugDao;
import com.shyl.msc.dm.dao.IDrugTypeDao;
import com.shyl.msc.dm.dao.IGoodsHospitalSourceDao;
import com.shyl.msc.dm.dao.IProductDao;
import com.shyl.msc.dm.entity.DrugType;
import com.shyl.msc.menu.dao.IProductSourceDao;
import com.shyl.msc.menu.entity.ProductSource;
import com.shyl.msc.menu.service.IProductSourceService;
import com.shyl.sys.dao.IAttributeDao;
import com.shyl.sys.dao.IAttributeItemDao;
import com.shyl.msc.set.dao.ICompanyDao;
import com.shyl.sys.entity.Attribute;
import com.shyl.sys.entity.AttributeItem;
/**
 * 产品Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional
public class ProductSourceService extends BaseService<ProductSource, Long> implements IProductSourceService {
	
	private IProductSourceDao productSourceDao;
	@Resource
	private IGoodsHospitalSourceDao goodsHospitalSourceDao;
	@Resource
	private IProductDao productDao;
	@Resource
	private ICompanyDao	companyDao;
	@Resource
	private IDrugDao    drugDao;
	@Resource
	private IDosageFormDao	dosageFormDao;
	@Resource
	private IDrugTypeDao	drugTypeDao;
	@Resource
	private IAttributeDao	attributeDao;
	@Resource
	private IAttributeItemDao	attributeItemDao;

	public IProductSourceDao getProductSourceDao() {
		return productSourceDao;
	}

	@Resource
	public void setProductSourceDao(IProductSourceDao productSourceDao) {
		this.productSourceDao = productSourceDao;
		super.setBaseDao(productSourceDao);
	}
	
	@Override
	@Transactional
	public ProductSource getByCode(String code) {
		return this.productSourceDao.getByCode(code);
	}
	
	@Override
	@Transactional
	public ProductSource getByStandardCodeOnly(String standardCode) {
		return this.productSourceDao.getByStandardCodeOnly(standardCode);
	}

	/**
	 * 根据本位码、通用名、包装获取资源信息
	 * @param standardCode
	 * @param genericName
	 * @param model
	 * @param packDesc
	 * @return
	 */
	public ProductSource getByStandardCodeAndPack(String standardCode, 
			String genericName, String model, String packDesc) {
		return this.productSourceDao.getByStandardCodeAndPack(standardCode, genericName, model, packDesc);
	}
	

	@Override
	public DataGrid<Map<String, Object>> npquery(List<String> productWords, List<String> dosageWords, 
			List<String> producerWords, String productCode, PageRequest pageable) {
		return productSourceDao.npquery(productWords, dosageWords, producerWords, productCode, pageable);
	}
	
	@Override
	public DrugType saveDrugType(Long parentId, String name, String treePath, String typ) {
		//药品分类
		DrugType type = drugTypeDao.findByTree(name, treePath);
		if (type == null) {
			type = new DrugType();
			type.setCode(drugTypeDao.getMaxCode(typ));
			type.setName(name);
			type.setTreePath(treePath);
			type.setParentId(parentId);
			type = drugTypeDao.save(type);
			this.drugTypeDao.flush();
		}
		return type;
	}
	@Override
	public AttributeItem saveAttribute(String attributeNo, String name) {
		if (StringUtils.isEmpty(name)) {
			name = "其他";
		}
		AttributeItem item = attributeItemDao.queryByAttrAndField2(attributeNo, name);
		if (item == null) {
			Attribute attribute = attributeDao.queryByAttributeNo(null, attributeNo);
			item = new AttributeItem();
			item.setField1(attributeItemDao.getMaxCode(attributeNo));
			item.setField2(name);
			item.setAttribute(attribute);
			attributeItemDao.flush();
			item = attributeItemDao.save(item);
		}
		return item;
	}
	
	@Override
	public void deletByReset() {
		productSourceDao.deletByReset();
	}

	@Override
	public String getMaxModelCode(String newcode, String model) {
		return productSourceDao.getMaxModelCode(newcode, model);
	}

	@Override
	public String getMaxPackCode(String newcode, String model, String packDesc) {
		return productSourceDao.getMaxPackCode(newcode, model, packDesc);
	}

	@Override
	public ProductSource getByNewcode(String code, String model, String packDesc) {
		return productSourceDao.getByNewcode(code, model, packDesc);
	}
	
	public String getMaxCode() {
		return productSourceDao.getMaxCode();
	}
}
