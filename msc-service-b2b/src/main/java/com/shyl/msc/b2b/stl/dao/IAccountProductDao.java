package com.shyl.msc.b2b.stl.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stl.entity.AccountProduct;
/**
 * 产品账务
 * 
 * @author a_Q
 *
 */
public interface IAccountProductDao extends IBaseDao<AccountProduct, Long> {


	public AccountProduct getByCode(String month, String code);

	List<AccountProduct> getbyMonthAndCode(String key);

	public DataGrid<Map<String, Object>> reportHospitalSB(String name, String dataS, String dataE,
			PageRequest pageable);
	public List<Map<String, Object>> reportTrade(String year);

	public DataGrid<Map<String, Object>> reportGoodsTrade(String dateS, String dateE, PageRequest pageable);

}
