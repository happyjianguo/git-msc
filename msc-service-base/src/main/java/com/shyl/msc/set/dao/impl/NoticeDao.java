package com.shyl.msc.set.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.INoticeDao;
import com.shyl.msc.set.entity.Notice;
/**
 * 公告DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class NoticeDao extends BaseDao<Notice, Long> implements INoticeDao {

}
