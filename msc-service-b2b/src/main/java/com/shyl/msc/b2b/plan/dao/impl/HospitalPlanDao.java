package com.shyl.msc.b2b.plan.dao.impl;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.plan.dao.IHospitalPlanDao;
import com.shyl.msc.b2b.plan.entity.HospitalPlan;

@Repository
public class HospitalPlanDao extends BaseDao<HospitalPlan, Long> implements IHospitalPlanDao {


	@Override
	public DataGrid<Map<String, Object>> nquery(PageRequest pageable, Integer status, String hospitalCode) {
		String sql ="select c.genericName,c.rcdosageFormName,c.dosageFormName,c.model,c.qualityLevel,c.minUnit,c.producernames,b.startDate as startDate,"
				+ "b.endDate as endDate,a.id,b.code as projectCode, b.name as projectName,b.startMonthDef,b.endMonthDef,c.note  "
				+ "from t_set_project_detail a,t_set_project b,t_dm_directory c where a.directoryId=c.id and a.projectId=b.id";
		if (status == null) {
		} else if (status == 1) {
			sql+=" and not exists(select 1 FROM t_plan_hospitalplan d where a.id = d.projectDetailId and d.hospitalCode='"+
					hospitalCode+"') ";
		} else if (status == 2) {
			sql+=" and exists(select 1 FROM t_plan_hospitalplan d where a.id = d.projectDetailId and d.hospitalCode='"+
					hospitalCode+"') ";
		}
		
		
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public HospitalPlan getByDetailId(Long detailId, String hospitalCode) {
		return super.getByHql("from HospitalPlan where hospitalCode=? and projectDetailId=?", hospitalCode, detailId);
	}

	@Override
	public Long getCountByDetailId(Long detailId) {
		return super.count("select count(1) from HospitalPlan where projectDetailId=?", detailId);
	}


	@Override
	public Long getCountByProjectId(Long projectId) {
		
		return super.count("select count(1) from HospitalPlan a,ProjectDetail b where a.projectDetailId=b.id and b.project.id=?", projectId);
	}

	@Override
	public List<HospitalPlan> listByProjectDetailId(Long projectDetailId) {
		String hql = "from HospitalPlan hp where hp.projectDetailId=?";
		return super.listByHql(hql, null, projectDetailId);
	}

	@Override
	public DataGrid<Map<String, Object>> tradeByProduct(PageRequest pageable, String startDate, String endDate) {
		String sql = "select p.CODE, p.NAME,pd.ID as PDID, dr.GENERICNAME, dr.DOSAGEFORMNAME, dr.MODEL, dr.QUALITYLEVEL, sum(hp.NUM) as num"
				+ " from t_plan_hospitalplan hp "
				+ " left join t_set_project_detail pd on hp.PROJECTDETAILID=pd.ID "
				+ " left join t_dm_directory dr on dr.id=pd.directoryId  "
				+ " left join t_set_project p on pd.PROJECTID=p.ID where 1=1 ";
		if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)){
			sql += " and ((hp.startMonth<='"+startDate+"' and  hp.endMonth>='"+startDate+"') or "
					+ " (hp.startMonth<='"+endDate+"' and hp.endMonth>='"+endDate+"') or"
					+ " (hp.startMonth>='"+startDate+"' and hp.endMonth<='"+endDate+"')) ";
		}else{
			if(!StringUtils.isEmpty(startDate)){
				sql += " and hp.endMonth>='"+startDate+"'";
			}
			if(!StringUtils.isEmpty(endDate)){
				sql += " and hp.startMonth<='"+endDate+"'";
			}
		}
		sql += " group by p.CODE, p.NAME,pd.ID, dr.GENERICNAME, dr.DOSAGEFORMNAME, dr.MODEL, dr.QUALITYLEVEL";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> tradeDetailByProduct(PageRequest pageable, String startDate, String endDate) {
		String sql = "select p.CODE, p.NAME, dr.GENERICNAME, dr.DOSAGEFORMNAME, dr.MODEL, dr.QUALITYLEVEL, hp.HOSPITALNAME, hp.NUM"
				+ " from t_plan_hospitalplan hp "
				+ " left join t_set_project_detail pd on hp.PROJECTDETAILID=pd.ID "
				+ " left join t_dm_directory dr on dr.id=pd.directoryId  "
				+ " left join t_set_project p on pd.PROJECTID=p.ID where 1=1 ";
		if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)){
			sql += " and ((hp.startMonth<='"+startDate+"' and  hp.endMonth>='"+startDate+"') or "
					+ " (hp.startMonth<='"+endDate+"' and hp.endMonth>='"+endDate+"') or"
					+ " (hp.startMonth>='"+startDate+"' and hp.endMonth<='"+endDate+"')) ";
		}else{
			if(!StringUtils.isEmpty(startDate)){
				sql += " and hp.endMonth>='"+startDate+"'";
			}
			if(!StringUtils.isEmpty(endDate)){
				sql += " and hp.startMonth<='"+endDate+"'";
			}
		}
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> reportForProductPlan(String projectCode, String startDate, String endDate,PageRequest pageable) {
		String sql = "select rc.name as REGIONCODE,hp.hospitalName,count(hp.id) as count"
				+ " from t_plan_hospitalplan hp "
				+ " left join t_set_project_detail pd on hp.PROJECTDETAILID=pd.ID   "
				+ " left join t_set_project p on pd.PROJECTID=p.ID "
				+ " left join t_dm_directory dr on dr.id=pd.directoryId   "
				+ " left join t_set_hospital h on hp.hospitalName=h.fullname "
				+ " left join t_set_regioncode rc on h.regionCode = rc.id  "
				+ " where 1=1 ";
		
		if(!StringUtils.isEmpty(startDate)){
			sql += " and to_char(p.startDate,'yyyy-mm-dd') >='"+startDate+"'";
		}
		if(!StringUtils.isEmpty(endDate)){
			sql += " and to_char(p.startDate,'yyyy-mm-dd') <='"+endDate+"'";
		}
		
		sql += " group by rc.name,hp.hospitalName ";
		
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> reportDetailForProductPlan(String projectCode, String startDate,
			String endDate, PageRequest pageable) {
		String sql = "select hp.hospitalName,p.code,p.name,to_char(p.startDate,'yyyy-mm-dd') as startDate,to_char(p.endDate,'yyyy-mm-dd') as endDate,dr.genericName,dr.dosageFormName,dr.model,dr.qualityLevel,dr.producerNames,hp.num "
				+ " from t_plan_hospitalplan hp "
				+ " left join t_set_project_detail pd on hp.PROJECTDETAILID=pd.ID   "
				+ " left join t_set_project p on pd.PROJECTID=p.ID "
				+ " left join t_dm_directory dr on dr.id=pd.directoryId   "
				+ " left join t_set_hospital h on hp.hospitalName=h.fullname "
				+ " left join t_set_regioncode rc on h.regionCode = rc.id  "
				+ " where 1=1 ";
		
		if(!StringUtils.isEmpty(startDate)){
			sql += " and to_char(p.startDate,'yyyy-mm-dd') >='"+startDate+"'";
		}
		if(!StringUtils.isEmpty(endDate)){
			sql += " and to_char(p.startDate,'yyyy-mm-dd') <='"+endDate+"'";
		}
		
		return super.findBySql(sql, pageable, Map.class);
	}


}
