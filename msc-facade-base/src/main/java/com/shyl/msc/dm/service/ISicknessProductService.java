package com.shyl.msc.dm.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.SicknessProduct;

public interface ISicknessProductService extends IBaseService<SicknessProduct, Long> {


	public List<Product> listProductBySicknessCode(@ProjectCodeFlag String projectCode, String sicknessCode);

	public SicknessProduct findByProductCode(@ProjectCodeFlag String projectCode, String productCode);

	public DataGrid<Product> pageBySicknessCode(@ProjectCodeFlag String projectCode, PageRequest pageable, String sicknessCode);

	public DataGrid<Map<String, Object>> pageByProductWithSelected(@ProjectCodeFlag String projectCode, PageRequest pageable, String sicknessCode);

	public SicknessProduct findByKey(@ProjectCodeFlag String projectCode, String sicknessCode, String productCode);

}
