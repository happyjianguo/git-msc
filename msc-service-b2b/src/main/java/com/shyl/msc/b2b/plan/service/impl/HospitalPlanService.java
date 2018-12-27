package com.shyl.msc.b2b.plan.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.plan.dao.IHospitalPlanDao;
import com.shyl.msc.b2b.plan.dao.IHospitalPlanDetailDao;
import com.shyl.msc.b2b.plan.entity.HospitalPlan;
import com.shyl.msc.b2b.plan.entity.HospitalPlanDetail;
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.sys.entity.User;

@Service
@Transactional(readOnly=true)
public class HospitalPlanService extends BaseService<HospitalPlan, Long> implements IHospitalPlanService {
	@Resource
	private IProjectDetailService projectDetailService;
	
	@Resource
	private IHospitalPlanDetailDao hospitalPlanDetailDao;
	
	private IHospitalPlanDao hospitalPlanDao;

	@Resource
	public void setHospitalPlanDao(IHospitalPlanDao hospitalPlanDao) {
		this.hospitalPlanDao = hospitalPlanDao;
		super.setBaseDao(hospitalPlanDao);
	}

	@Override
	public DataGrid<Map<String, Object>> nquery(String projectCode, PageRequest pageable, Integer status, String hospitalCode) {
		return hospitalPlanDao.nquery(pageable, status, hospitalCode);
	}

	@Override
	public HospitalPlan getByDetailId(String projectCode, Long projectDetailId, String hospitalCode) {
		return hospitalPlanDao.getByDetailId(projectDetailId, hospitalCode);
	}

	@Override
	public Long getCountByDetailId(String projectCode, Long projectDetailId) {
		return hospitalPlanDao.getCountByDetailId(projectDetailId);
	}

	@Override
	public Long getCountByProjectId(String projectCode, Long projectId) {
		return hospitalPlanDao.getCountByProjectId(projectId);
	}

	@Override
	public List<HospitalPlan> listByProjectDetailId(String projectCode, Long projectDetailId) {
		return hospitalPlanDao.listByProjectDetailId(projectDetailId);
	}

	@Override
	public DataGrid<Map<String, Object>> tradeByProduct(String projectCode, PageRequest pageable, String startDate, String endDate) {
		return hospitalPlanDao.tradeByProduct(pageable, startDate, endDate);
	}

	@Override
	public DataGrid<Map<String, Object>> tradeDetailByProduct(String projectCode, PageRequest pageable, String startDate, String endDate) {
		return hospitalPlanDao.tradeDetailByProduct(pageable, startDate, endDate);
	}

	@Override
	public JSONArray getToGPO(String projectCode, long xmmxdh_l) {
		JSONArray jo_arr_r = new JSONArray();
		List<HospitalPlan> hospitalPlans = hospitalPlanDao.listByProjectDetailId(xmmxdh_l);
		for(HospitalPlan hospitalPlan : hospitalPlans){
			JSONObject jo_r = new JSONObject();
			jo_r.put("xmmxdh", String.valueOf(hospitalPlan.getProjectDetailId()));
			jo_r.put("yybldh", String.valueOf(hospitalPlan.getId()));
			jo_r.put("yybm", hospitalPlan.getHospitalCode());
			jo_r.put("cgzl", hospitalPlan.getNum());
			jo_r.put("cgzq", hospitalPlan.getCycle());
			jo_r.put("ksny", hospitalPlan.getStartMonth());
			jo_r.put("jsny", hospitalPlan.getEndMonth());
			List<HospitalPlanDetail> hospitalPlanDetails = hospitalPlanDetailDao.getByHospitalPlanId(hospitalPlan.getId());
			JSONArray jod_arr_r = new JSONArray();
			for(HospitalPlanDetail hospitalPlanDetail : hospitalPlanDetails){
				JSONObject jod_r = new JSONObject();
				jod_r.put("ny", hospitalPlanDetail.getMonth());
				jod_r.put("cgl", hospitalPlanDetail.getNum());
				jod_arr_r.add(jod_r);
			}
			jo_r.put("mx", jod_arr_r);
			jo_arr_r.add(jo_r);
		}
		return jo_arr_r;
	}

	@Override
	@Transactional
	public void setup(String projectCode, String jsonStr, User user) {
		String hospitalCode = user.getOrganization().getOrgCode();
		String hospitalName = user.getOrganization().getOrgName();
		
		JSONArray arr = JSONArray.parseArray(jsonStr);
		for(int i=0;i<arr.size();i++) {
			JSONObject obj = arr.getJSONObject(i);
			Long projectDetailId = obj.getLong("projectDetailId");
			HospitalPlan plan = hospitalPlanDao.getByDetailId(projectDetailId, hospitalCode);
			ProjectDetail pd = projectDetailService.getById(projectCode,projectDetailId);
			if (plan == null) {
				plan = new HospitalPlan();
				plan.setProjectDetailId(projectDetailId);
				plan.setHospitalCode(hospitalCode);
				plan.setHospitalName(hospitalName);
				//更新projectDetail的量
				pd.setNum(pd.getNum()+Integer.parseInt(obj.getBigDecimal("num")+""));
				//projectDetailService.update(user.getProjectCode(),pd);
			}else{
				//更新projectDetail的量
				pd.setNum(pd.getNum()-Integer.parseInt(plan.getNum()+"")+Integer.parseInt(obj.getBigDecimal("num")+""));
				//projectDetailService.update(user.getProjectCode(),pd);
			}
			String startMonth = obj.getString("startMonth");
			String endMonth =  obj.getString("endMonth");
			plan.setCycle(obj.getInteger("cycle"));
			plan.setNum(obj.getBigDecimal("num"));
			if (plan.getNum() == null||plan.getNum().compareTo(new BigDecimal(0))==0) {
				throw new RuntimeException("药品数量不能为空且大于0");
			}
			
			plan.setStartMonth(startMonth);
			plan.setEndMonth(endMonth);

			Integer month0 = Integer.valueOf(endMonth.replaceAll("-", ""));
			Integer month1 = Integer.valueOf(startMonth.replaceAll("-", ""));
			
			Integer a = (month0/100) - (month1/100);
			Integer b = (month0%100) - (month1%100);
			if (a != 0) {
				a = a*12;
			}
			Integer cycle = b + a + 1;
			plan.setCycle(cycle);
			if (plan.getId() == null) {
				plan = hospitalPlanDao.save(plan);
				//更新projectDetail的报量医院数
				PageRequest pageable = new PageRequest();
				pageable.getQuery().put("t#projectDetailId_L_EQ", projectDetailId);
				List<HospitalPlan> hplist = hospitalPlanDao.getAll(pageable);
				pd.setHospitalNum(hplist.size());
			} else {
				hospitalPlanDao.update(plan);
				//删除之前的明细
				hospitalPlanDetailDao.deleteByHospitalPlanId(plan.getId());
			}
			//更新projectDetail
			projectDetailService.update(projectCode,pd);
			
			//处理明细
			if(obj.containsKey("details")) {
				JSONArray details = obj.getJSONArray("details");
				for(int j=0;j<details.size();j++) {
					JSONObject dobj = details.getJSONObject(j);
					HospitalPlanDetail planDetail = new HospitalPlanDetail();
					planDetail.setHospitalPlan(plan);
					planDetail.setMonth(dobj.getString("month"));
					planDetail.setNum(dobj.getBigDecimal("num"));
					hospitalPlanDetailDao.save(planDetail);
				}
			} else {
				Integer num = plan.getNum().intValue();
				int detailNum = num/cycle;
				for(int j=0;j<cycle;j++) {
					HospitalPlanDetail planDetail = new HospitalPlanDetail();
					if (j+1==cycle) {
						detailNum = num;
					} else { 
						num = num-detailNum;
					}
					
					planDetail.setHospitalPlan(plan);
					
					int year = month1/100;
					int month = j+(month1%100);
					if (month > 12) {
						month = month - 12;
						year = year+1;
					}
					String monthStr = String.valueOf(month);
					if (month < 10) {
						monthStr = "0" + monthStr;
					}
					
					planDetail.setMonth(year + "-" + monthStr);
					planDetail.setNum(new BigDecimal(detailNum));
					hospitalPlanDetailDao.save(planDetail);
				}
			}
			
			
		}
	}

	@Override
	public DataGrid<Map<String, Object>> reportForProductPlan(String projectCode, String startDate, String endDate,PageRequest pageable) {
		return hospitalPlanDao.reportForProductPlan( projectCode,  startDate,  endDate,  pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> reportDetailForProductPlan(String projectCode, String startDate,
			String endDate, PageRequest pageable) {
		return hospitalPlanDao.reportDetailForProductPlan( projectCode,  startDate,  endDate,  pageable);
	}

}
