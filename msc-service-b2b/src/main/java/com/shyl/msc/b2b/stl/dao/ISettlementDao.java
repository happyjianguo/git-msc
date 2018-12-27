package com.shyl.msc.b2b.stl.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.entity.SettlementDetail;
/**
 * 结算单
 * 
 * @author a_Q
 *
 */
public interface ISettlementDao extends IBaseDao<Settlement, Long> {

	public Settlement findByCode(String code);

	public Settlement getByInternalCode(String code, String internalCode, Boolean isGPO);

	public List<SettlementDetail> listBySettlementId(Long id);

	
}
