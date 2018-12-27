package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IReturnsRequestDetailDao;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
/**
 * 退货申请单明细DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class ReturnsRequestDetailDao extends BaseDao<ReturnsRequestDetail, Long> implements IReturnsRequestDetailDao{

	@Override
	public List<ReturnsRequestDetail> listById(Long id) {
		String hql = "from ReturnsRequestDetail t where t.returnsRequest.id = ? ";
		return super.listByHql(hql, null, id);
	}

	@Override
	public List<ReturnsRequestDetail> listByPid(Long id) {
		String hql = "from ReturnsRequestDetail t where t.returnsRequest.id = ? ";
		return super.listByHql(hql, null, id);
	}

	@Override
	public ReturnsRequestDetail findByCode(String code) {
		String hql = "from ReturnsRequestDetail t where t.code= ? ";
		return super.getByHql(hql, code);
	}

}
