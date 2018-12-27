package com.shyl.msc.set.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IWarehouseDao;
import com.shyl.msc.set.entity.Warehouse;
import com.shyl.msc.set.service.IWarehouseService;
/**
 * 库房Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class WarehouseService extends BaseService<Warehouse, Long> implements IWarehouseService {
	private IWarehouseDao warehouseDao;

	public IWarehouseDao getWarehouseDao() {
		return warehouseDao;
	}

	@Resource
	public void setWarehouseDao(IWarehouseDao warehouseDao) {
		this.warehouseDao = warehouseDao;
		super.setBaseDao(warehouseDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public Warehouse save(String projectCode, Warehouse entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public Warehouse updateWithInclude(String projectCode, Warehouse entity, String... args) {
		return super.updateWithInclude(projectCode, entity,args);
	}
	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}
	
	@Override
	@Cacheable(value = "warehouse")
	public List<Map<String, Object>> lisByHospital(String projectCode, Long hospitalId) {
		return warehouseDao.lisByHospital(hospitalId);
	}
	
	@Override
	@CacheEvict(value = "warehouse")
	public DataGrid<Warehouse> queryByHospital(String projectCode, PageRequest pageablem,Long hospitalId,String searchkey){
		return warehouseDao.queryByHospital(pageablem, hospitalId,searchkey);
	}
	
	@Override
	@Cacheable(value = "warehouse")
	public Warehouse queryByCodeAndPid(String projectCode, String code, Long pid){
		return warehouseDao.queryByCodeAndPid(code, pid);
	}

	@Override
	@Cacheable(value = "warehouse")
	public DataGrid<Warehouse> pageByHospital(String projectCode, PageRequest pageable, Long id) {
		return warehouseDao.pageByHospital(pageable, id);
	}

	@Override
	@Cacheable(value = "warehouse")
	public Warehouse findByCode(String projectCode, String code) {
		return warehouseDao.findByCode(code);
	}

	@Override
	public Warehouse getLast(String projectCode, String hospitalCode) {
		return warehouseDao.getLast(hospitalCode);
	}

	@Override
	@Transactional
	public JSONArray saveWarehouse(String projectCode, JSONObject jObject) {
		JSONArray array = new JSONArray();
		String mx = jObject.getString("mx");//明细
		List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
		List<Warehouse> warehouses = new ArrayList<>();
		for(JSONObject detail:list){
			String psdmc = detail.getString("psdmc");	//配送点名称
			String psddz = detail.getString("psddz");	//配送点地址
			String jd = detail.getString("jd");		//经度
			String wd = detail.getString("wd");		//纬度
			String lxr = detail.getString("lxr");	//联系人
			String lxdh = detail.getString("lxdh");	//联系电话
			int sfjy_i = jObject.getIntValue("sfjy");//是否禁用
			Warehouse warehouse = new Warehouse();
			String code = "";//TODO 生成规则
			array.add(code);
			warehouse.setCode(code);
			warehouse.setName(psdmc);
			warehouse.setAddr(psddz);
			warehouse.setContact(lxr);
			warehouse.setPhone(lxdh);
			warehouse.setLongitude(jd);
			warehouse.setLatitude(wd);
			warehouse.setIsDisabled(sfjy_i);
			warehouses.add(warehouse);
		}
		warehouseDao.saveBatch(warehouses);
		return array;
	}

	@Override
	public List<Map<String, Object>> listByLocation(String projectCode, int pxfs, String minLat, String maxLat, String minLng,
			String maxLng, String ypbm) {
		return warehouseDao.listByLocation(pxfs, minLat, maxLat, minLng, maxLng, ypbm);
	}
}
