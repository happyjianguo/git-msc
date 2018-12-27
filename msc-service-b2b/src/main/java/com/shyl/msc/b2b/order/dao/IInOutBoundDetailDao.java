package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
/**
 * 出入库明细DAO接口
 * 
 * @author a_Q
 *
 */
public interface IInOutBoundDetailDao extends IBaseDao<InOutBoundDetail, Long> {

	/**
	 * 根据配送单明细编号查出入库明细
	 * @param code
	 * @return
	 */
	public InOutBoundDetail getByDeliveryOrderDetailCode(String code);

	/**
	 * 
	 * @param isPass
	 * @return
	 */
	public List<InOutBoundDetail> listByIsPass(int isPass);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<InOutBoundDetail> listByInOutBound(Long id);

	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable);

	/**
	 * 入库金额统计
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> queryByPage(PageRequest pageable);
	/**
	 * 入库金额统计导出
	 * @param pageable
	 * @return
	 */
	public List<Map<String, Object>> queryByAll(PageRequest pageable);
	
	public List<Map<String, Object>> listByProductReport(PageRequest pageable);
	

	/**
	 * 查询GPO及非GPO采购数据
	 * @param year
	 * @param month
	 * @param maxsize
	 * @return
	 */
	public List<Map<String,Object>> listGpoReport(String year, String month, int maxsize);

	/**
	 * 查询GPO及非GPO采购数据，按区域
	 * @param year
	 * @param month
	 * @param treepath
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> listGpoReportByRegion(String year, String month, String treepath, int maxsize);
	/**
	 * 查询gpo
	 * 
	 */
	public List<Map<String, Object>> queryInOutBoundDetail(PageRequest page,String beginMonth, String endMonth);

	public DataGrid<InOutBoundDetail> queryByCode(PageRequest page);
}
