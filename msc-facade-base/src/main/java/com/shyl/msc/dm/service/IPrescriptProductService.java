package com.shyl.msc.dm.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.PrescriptProduct;

public interface IPrescriptProductService extends IBaseService<PrescriptProduct, Long> {


	public List<PrescriptProduct> listProductByModifyDate(@ProjectCodeFlag String projectCode, String modifyDate);

	public PrescriptProduct findByProductId(@ProjectCodeFlag String projectCode, Long productId);
	
	public DataGrid<Map<String, Object>> pageByProductWithSelected(@ProjectCodeFlag String projectCode, PageRequest pageable);

}
