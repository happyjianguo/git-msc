package com.shyl.msc.set.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IBlacklistDao;
import com.shyl.msc.set.entity.Blacklist;
/**
 * 企业黑名单DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class BlacklistDao extends BaseDao<Blacklist, Long> implements IBlacklistDao {

}
