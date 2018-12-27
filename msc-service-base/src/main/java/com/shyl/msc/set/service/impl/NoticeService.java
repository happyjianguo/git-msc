package com.shyl.msc.set.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageParam;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.INoticeDao;
import com.shyl.msc.set.entity.Notice;
import com.shyl.msc.set.service.INoticeService;
/**
 * 公告Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class NoticeService extends BaseService<Notice, Long> implements INoticeService {
	private INoticeDao noticeDao;

	public INoticeDao getNoticeDao() {
		return noticeDao;
	}

	@Resource
	public void setNoticeDao(INoticeDao noticeDao) {
		this.noticeDao = noticeDao;
		super.setBaseDao(noticeDao);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "notice", allEntries = true)
	public Notice save(String projectCode, Notice entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "notice", allEntries = true)
	public Notice updateWithInclude(String projectCode, Notice entity, String... args) {
		return super.updateWithInclude(projectCode, entity,args);
	}
	@Override
	@Transactional
	@CacheEvict(value = "notice", allEntries = true)
	public Notice update(String projectCode, Notice entity) {
		return super.update(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "notice", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}
	
	@Override
	@Cacheable(value = "notice")
	public Notice getById(String projectCode, Long id) {
		return noticeDao.getById(id);
	}
	
	@Override
	@Cacheable(value = "notice")
	public List<Notice> list(String projectCode, PageParam pageable) {
		return noticeDao.getAll(pageable);
	}
	
	@Override
	@Cacheable(value = "notice")
	public DataGrid<Notice> query(String projectCode, PageParam pageable) {
		return noticeDao.query(pageable);
	}
}
