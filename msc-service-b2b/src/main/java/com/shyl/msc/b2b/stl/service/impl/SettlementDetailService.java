package com.shyl.msc.b2b.stl.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.stl.dao.ISettlementDetailDao;
import com.shyl.msc.b2b.stl.entity.SettlementDetail;
import com.shyl.msc.b2b.stl.service.ISettlementDetailService;

/**
 * 发票
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class SettlementDetailService extends BaseService<SettlementDetail, Long> implements ISettlementDetailService {
	private ISettlementDetailDao settlementDetailDao;

	public ISettlementDetailDao getSettlementDetailDao() {
		return settlementDetailDao;
	}

	@Resource
	public void setSettlementDetailDao(ISettlementDetailDao settlementDetailDao) {
		this.settlementDetailDao = settlementDetailDao;
		super.setBaseDao(settlementDetailDao);
	}


	
}
