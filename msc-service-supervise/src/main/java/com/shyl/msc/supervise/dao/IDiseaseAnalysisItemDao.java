package com.shyl.msc.supervise.dao;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.DiseaseAnalysisItem;

public interface IDiseaseAnalysisItemDao extends IBaseDao<DiseaseAnalysisItem, Long> {
	
	public int getDrugNum(PageRequest page);
	
	public DataGrid<DiseaseAnalysisItem> groupProductBy(PageRequest page);
}
