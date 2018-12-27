package com.shyl.msc.set.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.Warehouse;
/**
 * 库房Service接口
 * 
 * @author a_Q
 *
 */
public interface IWarehouseService extends IBaseService<Warehouse, Long> {

	/**
	 * 根据医院查库房
	 * @param hospitalId
	 * @return
	 */
	public List<Map<String, Object>> lisByHospital(@ProjectCodeFlag String projectCode, Long hospitalId);
	
	/**
	 * 根据医院查库房
	 * @param hospitalId
	 * @param searchkey
	 * @return
	 */
	public DataGrid<Warehouse> queryByHospital(@ProjectCodeFlag String projectCode, PageRequest pageablem,Long hospitalId,String searchkey);

	/**
	 * 根据代码和医院id查询
	 * @param code
	 * @param pid
	 * @return
	 */
	public Warehouse queryByCodeAndPid(@ProjectCodeFlag String projectCode, String code, Long pid);

	/**
	 * 
	 * @param pageable
	 * @param id
	 * @return
	 */
	public DataGrid<Warehouse> pageByHospital(@ProjectCodeFlag String projectCode, PageRequest pageable, Long id);

	/**
	 * 
	 * @param code
	 * @return
	 */
	public Warehouse findByCode(@ProjectCodeFlag String projectCode, String code);

	public Warehouse getLast(@ProjectCodeFlag String projectCode, String code);

	/**
	 * 配送点上传保存
	 * @param jObject
	 * @return
	 */
	public JSONArray saveWarehouse(@ProjectCodeFlag String projectCode, JSONObject jObject);

	public List<Map<String, Object>> listByLocation(@ProjectCodeFlag String projectCode, int pxfs, String minLat, String maxLat, String minLng,
			String maxLng, String ypbm);
}
