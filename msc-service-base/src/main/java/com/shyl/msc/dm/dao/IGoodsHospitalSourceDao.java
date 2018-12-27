package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.GoodsHospitalSource;

public interface IGoodsHospitalSourceDao extends IBaseDao<GoodsHospitalSource, Long> {
	
	/**
	 * 根据医院编码获取code
	 * @param internalCode
	 * @param producerName
	 * @param hospitalName
	 * @param model
	 * @return
	 */
	public GoodsHospitalSource getByCode(String internalCode, String productName, String producerName, 
			String hospitalName, String model);
	
	/**
	 * 分页查询
	 * @param page
	 * @return
	 */
	public DataGrid<Map<String, Object>> npquery(PageRequest page);
	
	/**
	 * 分页查询
	 * @param words
	 * @param pageable
	 * @return
	 */
	public DataGrid<GoodsHospitalSource> npquery(List<String> productWords, List<String> dosageWords, List<String> producerWords, PageRequest pageable);
	
	  
    /**
     * 新编码改为旧编码
     * @param newCode
     * @param oldCode
     * @return
     */
    public int updateByProductCode(String newCode, String oldCode);
    
    /**
     * 查询不在医院药品目录的数据
     * @param maxsize
     * @param hospitalId
     * @param status
     * @return
     */
    public List<GoodsHospitalSource> queryNotinGoods(int maxsize, Long hospitalId, Long status);
    
    /**
     * 根据ID批量更新
     * @param ids
     * @param status
     * @return
     */
    public int updateStatusByIds(String ids, Long status, Long hospitalId);
}
