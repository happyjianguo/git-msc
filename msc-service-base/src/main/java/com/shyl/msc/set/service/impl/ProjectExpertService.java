package com.shyl.msc.set.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IExpertDao;
import com.shyl.msc.set.dao.IProjectDao;
import com.shyl.msc.set.dao.IProjectExpertDao;
import com.shyl.msc.set.entity.Expert;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.entity.ProjectExpert;
import com.shyl.msc.set.service.IExpertService;
import com.shyl.msc.set.service.IProjectExpertService;

@Service
@Transactional(readOnly=true)
public class ProjectExpertService extends BaseService<ProjectExpert,Long> implements IProjectExpertService{
	@Resource
	private IExpertDao expertDao;
	@Resource
	private IProjectDao projectDao;
	
	private IProjectExpertDao projectExpertDao;

	public IProjectExpertDao getProjectExpertDao() {
		return projectExpertDao;
	}

	@Resource
	public void setProjectExpertDao(IProjectExpertDao projectExpertDao) {
		this.projectExpertDao = projectExpertDao;
		super.setBaseDao(projectExpertDao);
	}

	@Override
	public List<Expert> rdmCourse(String projectCode, Long projectId,Map<String, Object> expertMap) throws Exception {
		List<Expert> list = new ArrayList<>();
		for (String courseCode : expertMap.keySet()){  
			doRdmCourse(list,projectId,courseCode,(Integer) expertMap.get(courseCode));
        }  
		return list ;
	}

	private void doRdmCourse(List<Expert> list, Long projectId,String courseCode, Integer num) throws Exception  {
		PageRequest page = new PageRequest();
		page.getQuery().put("t#courseCode_S_EQ", courseCode);
		List<Expert> expertList = expertDao.getAll(page);
		Integer max = expertList.size();
		if(max < num){
			throw new Exception(courseCode+"人数为"+max+",无法抽取"+num+"人");
		}
		String ranStr = randomFunc(max,num);
		for (int i = 0; i < expertList.size(); i++) {
			String s = ","+i+",";
			if(ranStr.indexOf(s)!=-1){
				list.add(expertList.get(i));
			}
		}
		//Math.random()*max
		
	}

	private String randomFunc(Integer max, Integer num) {
		List<Integer> listMax = new ArrayList<>();
		for (int i = 0; i < max; i++) {
			listMax.add(i);
		}
		return randomFunc1(listMax,num,",");
	}

	private String randomFunc1(List<Integer> listMax, Integer num,String rtnStr) {
		if(num > 0){
			int r = (int) (Math.random()*listMax.size());
			rtnStr += listMax.get(r)+",";
			listMax.remove(r);
			return randomFunc1(listMax,num - 1,rtnStr);
		}
		
		return rtnStr;
	}

	@Override
	@Transactional
	public void saveRdmCourse(String projectCode, Long projectId, Map<String, Object> expertMap) throws Exception {
		Project project = projectDao.getById(projectId);
		if(project == null){
			throw new Exception("项目ID："+projectId+"不存在");
		}
		if(expertMap.size() > 0){
			PageRequest page = new PageRequest();
			page.getQuery().put("t#project.id_S_EQ", projectId);
			List<ProjectExpert> peList = projectExpertDao.getAll(page);
			for (int i = 0; i < peList.size(); i++) {
				ProjectExpert pe = peList.get(i);
				projectExpertDao.delete(pe);
			}
		}
		
		for (String key : expertMap.keySet()){ 
			Long expertId = Long.parseLong((String)expertMap.get(key));
			Expert expert = expertDao.getById(expertId);
			if(expert != null){
				ProjectExpert pe = new ProjectExpert();
				pe.setProject(project);
				pe.setExpert(expert);
				projectExpertDao.save(pe);
			}
        }  
	}
}
