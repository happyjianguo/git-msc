package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.GoodsHospital;

public interface IGoodsHospitalDao extends IBaseDao<GoodsHospital, Long> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	public GoodsHospital getByGoodsId(Long id);
	
	/**
	 * 根据药品内部编码
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	public GoodsHospital getByInternalCode(String hospitalCode, String internalCode);

	
	/**
	 * 根据药品编码
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	public GoodsHospital getByProductCode(String hospitalCode, String productCode);

	public List<Map<String, Object>> listByDate(String scgxsj);
	
	public DataGrid<Map<String,Object>> queryByPage(PageRequest page);
	
	public List<Map<String,Object>> queryByAll(PageRequest page);
}
