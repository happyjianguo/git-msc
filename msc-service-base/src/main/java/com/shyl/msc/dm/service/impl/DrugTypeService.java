package com.shyl.msc.dm.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IDrugTypeDao;
import com.shyl.msc.dm.entity.DrugType;
import com.shyl.msc.dm.service.IDrugTypeService;
/**
 * 药品分类Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class DrugTypeService extends BaseService<DrugType, Long> implements IDrugTypeService {
	private IDrugTypeDao drugTypeDao;

	public IDrugTypeDao getDrugTypeDao() {
		return drugTypeDao;
	}

	@Resource
	public void setDrugTypeDao(IDrugTypeDao drugTypeDao) {
		this.drugTypeDao = drugTypeDao;
		super.setBaseDao(drugTypeDao);
	}
	
	@Override
	public List<DrugType> listByParentId(String projectCode, Long parentId) {
		return drugTypeDao.listByParentId(parentId);
	}

	@Override
	public DrugType findByCode(String projectCode, String code) {
		return drugTypeDao.findByCode(code);
	}
	
	@Override
	@Transactional
	public void mktree(String projectCode) {
		PageRequest req = new PageRequest();
		List<DrugType> list = super.list(projectCode, req);
		for(DrugType r:list){
			if(r.getParentId()!=null){
				DrugType p = drugTypeDao.getById(r.getParentId());
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
	
}
