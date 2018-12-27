package com.shyl.msc.set.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.Warehouse;
/**
 * 库房DAO接口
 * 
 * @author a_Q
 *
 */
public interface IWarehouseDao extends IBaseDao<Warehouse, Long> {

	/**
	 * 根据医院查库房
	 * @param hospitalId
	 * @return
	 */
	public List<Map<String, Object>> lisByHospital(Long hospitalId);
	
	/**
	 * 根据医院查库房
	 * @param hospitalId
	 * @param searchkey
	 * @return
	 */
	public DataGrid<Warehouse> queryByHospital(PageRequest pageablem,Long hospitalId,String searchkey);

	/**
	 * 根据代码和医院id查询
	 * @param code
	 * @param pid
	 * @return
	 */
	public Warehouse queryByCodeAndPid(String code, Long pid);

	/**
	 * 根据医院查库房
	 * @param pageable
	 * @param id
	 * @return
	 */
	public DataGrid<Warehouse> pageByHospital(PageRequest pageable, Long id);

	public Warehouse findByCode(String code);

	public Warehouse getLast(String hospitalCode);

	public List<Map<String, Object>> listByLocation(int pxfs, String minLat, String maxLat, String minLng,
			String maxLng, String ypbm);
	
}
