package com.shyl.msc.dm.service;


import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.GoodsHospitalSource;

/**
 * 
 * 
 * @author a_Q
 *
 */
public interface IGoodsHospitalSourceService extends IBaseService<GoodsHospitalSource, Long> {
	
	/**
	 * 保存旧数据编码
	 * @param olds
	 * @throws Exception
	 */
	public void saveGoodsHospitalSource(@ProjectCodeFlag String projectCode, String[][] olds) throws Exception;
	
	/**
	 * 分页查询
	 * @param page
	 * @return
	 */
	public DataGrid<Map<String, Object>> npquery(@ProjectCodeFlag String projectCode, PageRequest page);
	
	/**
	 * 分页查询
	 * @param words
	 * @param pageable
	 * @return
	 */
	public DataGrid<GoodsHospitalSource> npquery(@ProjectCodeFlag String projectCode, List<String> productWords, List<String> dosageWords, List<String> producerWords, PageRequest pageable);

	/**
	 * 保持映射关系
	 * @param source
	 */
	public void saveMapping(@ProjectCodeFlag String projectCode, GoodsHospitalSource source);


	/**
	 * 同步国家目录到标准目录中去
	 * @param maxsize
	 * @return
	 */
	public int syncToGoods(@ProjectCodeFlag String projectCode, int maxsize, Long hospitalId, Long status);
	
	/**
	 * 批量更新ID值
	 * @param ids
	 * @param status
	 * @param hospitalId
	 * @return
	 */
	public int updateStatusByIds(@ProjectCodeFlag String projectCode, String ids, Long status, Long hospitalId);
	

	public void saveMapping(@ProjectCodeFlag String projectCode, Long id, String productCode, Double convertRatio);
}
