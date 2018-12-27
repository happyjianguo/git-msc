package com.shyl.msc.set.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IRegionCodeDao;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.service.IRegionCodeService;

@Service
@Transactional(readOnly=true)
public class RegionCodeService extends BaseService<RegionCode,Long> implements IRegionCodeService{
	
	private IRegionCodeDao regionCodeDao;
	@Resource
	public void setregionCodeDao(IRegionCodeDao regionCodeDao) {
		this.regionCodeDao = regionCodeDao;
		super.setBaseDao(regionCodeDao);
	}
	
	@Override
	public List<RegionCode> getChlidList(String projectCode, Long pid) {
		return regionCodeDao.getChlidList(pid);
	}

	// 获取resourceP新子节点的sort
	@Override
	public Integer getNewSort(String projectCode, Long parentId) {
		List<RegionCode> list = regionCodeDao.getChlidList(parentId);
		if(list == null || list.size() == 0)
			return 0;
		//RegionCode resource = list.get(list.size()-1);
		//Integer maxSort = resource.getSort();
		//maxSort = maxSort == null?0:maxSort+1;
		return 1;
	}

	@Override
	public List<RegionCode> getLvlone(String projectCode) {
		return regionCodeDao.getLvlone();
	}

	@Override
	@Transactional
	public void mktree(String projectCode) {
		PageRequest req = new PageRequest();
		List<RegionCode> list = super.list(projectCode, req);
		for(RegionCode r:list){
			if(r.getParentId()!=null){
				RegionCode p = regionCodeDao.getById(r.getParentId());
				if(p!=null){
					if(p.getTreePath()==null){
						r.setTreePath(p.getId()+"");
					}else{
						r.setTreePath(p.getTreePath()+","+p.getId());
					}
					
					System.out.println(r.getId()+":"+r.getTreePath());
					super.update(projectCode, r);
				}
			}
		}
	}

	@Override
	public RegionCode findByCode(String projectCode, String code) {
		return regionCodeDao.findByCode(code);
	}


}
