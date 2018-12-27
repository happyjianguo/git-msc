package com.shyl.msc.b2b.hospital.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.hospital.entity.PurchaseRecord;
/**
 * 采购记录上报DAO接口
 * 
 *
 */
public interface IPurchaseRecordDao extends IBaseDao<PurchaseRecord, Long> {

	/**
	 * 根据年月、医院查询采购记录
	 * @param startMonth
	 * @param toMonth
	 * @param hospitalCode
	 * @return
	 */
	public List<Map<String, Object>> query(String startMonth, String toMonth, String hospitalCode);
	
	public DataGrid<Map<String, Object>> queryByPage(PageRequest page);
}
