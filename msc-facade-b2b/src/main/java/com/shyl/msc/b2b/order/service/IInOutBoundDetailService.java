package com.shyl.msc.b2b.order.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
/**
 * 出入库明细Service接口
 * 
 * @author a_Q
 *
 */
public interface IInOutBoundDetailService extends IBaseService<InOutBoundDetail, Long> {

	public List<InOutBoundDetail> listByInOutBound(@ProjectCodeFlag String projectCode, Long id);

	public DataGrid<Map<String, Object>> pageByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(@ProjectCodeFlag String projectCode, PageRequest pageable);
	/**
	 * 入库金额统计
	 */
	public DataGrid<Map<String, Object>> queryByPage(@ProjectCodeFlag String projectCode, PageRequest pageable);
	
	/**
	 * 入库金额统计导出
	 */
	public List<Map<String, Object>> queryByAll(@ProjectCodeFlag String projectCode, PageRequest pageable);
	
	public List<Map<String, Object>> listByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);
	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<InOutBoundDetail> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);
	
	/**
	 * 查询GPO及非GPO采购数据，按医院
	 * @param year
	 * @param month
	 * @param maxsize
	 * @return
	 */
	public List<Map<String,Object>> listGpoReport(@ProjectCodeFlag String projectCode,String year, String month, int maxsize);
	/**
	 * 查询GPO及非GPO采购数据，按区域
	 * @param year
	 * @param month
	 * @param treepath
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> listGpoReportByRegion(@ProjectCodeFlag String projectCode, String year, String month, String treepath, int maxsize);
	/**
	 * 查询gpo采购
	 * @param beginMonth
	 * @param endMonth
	 * @return
	 */
	public List<Map<String, Object>> queryInOutBoundDetail(@ProjectCodeFlag String projectCode,PageRequest page,String beginMonth,String endMonth);

	public DataGrid<InOutBoundDetail> queryByCode(@ProjectCodeFlag String projectCode,PageRequest page);
}
