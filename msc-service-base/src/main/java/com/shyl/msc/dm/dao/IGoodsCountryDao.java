package com.shyl.msc.dm.dao;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.GoodsCountry;

import java.util.List;
import java.util.Map;

public interface IGoodsCountryDao extends IBaseDao<GoodsCountry, Long> {

    /**
     *主查询，返回DataGrid
     * @param pageable
     * @return
     */
    public DataGrid<Map<String,Object>> selectAll(PageRequest pageable);

    /**
     * 主查询，返回导出数据
     * @param pageable
     * @return
     */
    public List<Map<String,Object>> selectAllforExport(PageRequest pageable);

    List<Map<String,Object>> getCountryDrugByCode(PageRequest pageable);
}
