package com.shyl.msc.dm.service;


import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.GoodsHospital;

/**
 * 
 * 
 * @author a_Q
 *
 */
public interface IGoodsHospitalService extends IBaseService<GoodsHospital, Long> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	public GoodsHospital getByGoodsId(@ProjectCodeFlag String projectCode, Long id);
	

	/**
	 * 根据药品内部编码
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	public GoodsHospital getByInternalCode(@ProjectCodeFlag String projectCode, String hospitalCode, String internalCode);
	
	
	/**
	 * 根据药品编码
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	public GoodsHospital getByProductCode(@ProjectCodeFlag String projectCode, String hospitalCode, String productCode);


	public List<Map<String, Object>> listByDate(@ProjectCodeFlag String projectCode, String scgxsj);
	
	public DataGrid<Map<String,Object>> queryByPage(@ProjectCodeFlag String projectCode,PageRequest page);
	
	public List<Map<String,Object>> queryByAll(@ProjectCodeFlag String projectCode,PageRequest page);
}
