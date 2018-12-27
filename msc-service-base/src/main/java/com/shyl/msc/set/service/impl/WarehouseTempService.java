package com.shyl.msc.set.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IWarehouseTempDao;
import com.shyl.msc.set.entity.WarehouseTemp;
import com.shyl.msc.set.service.IWarehouseTempService;

@Service
@Transactional(readOnly = true)
public class WarehouseTempService extends BaseService<WarehouseTemp, Long> implements IWarehouseTempService {

	private IWarehouseTempDao warehouseTempDao;

	public IWarehouseTempDao getWarehouseTempDao() {
		return warehouseTempDao;
	}

	@Resource
	public void setWarehouseTempDao(IWarehouseTempDao warehouseTempDao) {
		this.warehouseTempDao = warehouseTempDao;
		super.setBaseDao(warehouseTempDao);
	}

	@Override
	@Transactional
	public JSONArray saveWarehouse(String projectCode, JSONObject jObject) {
		JSONArray array = new JSONArray();
		String mx = jObject.getString("mx");//明细
		List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
		List<WarehouseTemp> warehouseTemps = new ArrayList<>();
		for(JSONObject detail:list){
			String psdybm = detail.getString("psdybm");	//配送点原编码
			String psdmc = detail.getString("psdmc");	//配送点名称
			String psddz = detail.getString("psddz");	//配送点地址
			String jd = detail.getString("jd");		//经度
			String wd = detail.getString("wd");		//纬度
			String lxr = detail.getString("lxr");	//联系人
			String lxdh = detail.getString("lxdh");	//联系电话
			int sfjy_i = jObject.getIntValue("sfjy");//是否禁用
			WarehouseTemp warehouseTemp = new WarehouseTemp();
			warehouseTemp.setExternalCode(psdybm);
			warehouseTemp.setName(psdmc);
			warehouseTemp.setAddr(psddz);
			warehouseTemp.setContact(lxr);
			warehouseTemp.setPhone(lxdh);
			warehouseTemp.setLongitude(jd);
			warehouseTemp.setLatitude(wd);
			warehouseTemp.setIsDisabled(sfjy_i);
			warehouseTemp.setStatus(WarehouseTemp.Status.unaudit);
			warehouseTemps.add(warehouseTemp);
		}
		warehouseTempDao.saveBatch(warehouseTemps);
		return array;
	}
	
}
