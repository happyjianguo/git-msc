package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.SicknessProduct;

public interface ISicknessProductDao extends IBaseDao<SicknessProduct, Long> {

	public List<Product> listProductBySicknessCode(String sicknessCode);

	public SicknessProduct findByProductCode(String productCode);

	public DataGrid<Product> pageBySicknessCode(PageRequest pageable, String sicknessCode);

	public DataGrid<Map<String, Object>> pageByProductWithSelected(PageRequest pageable, String sicknessCode);

	public SicknessProduct findByKey(String sicknessCode, String productCode);

}
