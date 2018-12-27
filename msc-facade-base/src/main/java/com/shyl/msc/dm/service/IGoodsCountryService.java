package com.shyl.msc.dm.service;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.GoodsCountry;
import com.shyl.sys.dto.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IGoodsCountryService extends IBaseService<GoodsCountry,Long> {

    public DataGrid<Map<String,Object>> selectAll(@ProjectCodeFlag String projectCode, PageRequest pageable);

    public List<Map<String,Object>> selectAllforExport(@ProjectCodeFlag String projectCode,PageRequest pageable);

    void importExcel(@ProjectCodeFlag String projectFlag, String[][] datas);

    void getByAllByAsync(@ProjectCodeFlag String projectCode);

    void getByAllDrugByAsync(@ProjectCodeFlag String projectCode,Integer i);
}
