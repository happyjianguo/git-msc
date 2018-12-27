package com.shyl.msc.b2b.hospital.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.hospital.entity.LackProduct;
/**
 * 短缺药品上报DAO接口
 * 
 *
 */
public interface ILackProductDao extends IBaseDao<LackProduct, Long> {
	/**
	 * 查询唯一一笔数据
	 * @param month
	 * @param hospitalCode
	 * @param productId
	 * @return
	 */
	public LackProduct findUnique(String month, String hospitalCode, Long productId);
	
	/**
	 * 根据年月、医院查询短缺药品
	 * @param startMonth
	 * @param toMonth
	 * @param hospitalCode
	 * @return
	 */
	public List<Map<String, Object>> queryBy(PageRequest page);
	
	/**
	 * 医院查询短缺药品 次数
	 * @return
	 */
	public DataGrid<Map<String,Object>> queryByCount(PageRequest page);
	
	/**
	 * 医院查询短缺药品明细
	 * @return
	 */
	public DataGrid<Map<String,Object>> queryByMx(PageRequest page);
}
