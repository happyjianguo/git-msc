package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.PrescriptProduct;

public interface IPrescriptProductDao extends IBaseDao<PrescriptProduct, Long> {

	public List<PrescriptProduct> listProductByModifyDate(String modifyDate) ;

	public PrescriptProduct findByProductId(Long productId);
	
	public DataGrid<Map<String, Object>> pageByProductWithSelected(PageRequest pageable);

}
