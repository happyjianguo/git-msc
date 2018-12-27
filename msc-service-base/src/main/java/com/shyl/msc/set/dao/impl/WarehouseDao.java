package com.shyl.msc.set.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IWarehouseDao;
import com.shyl.msc.set.entity.Warehouse;
/**
 * 库房DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class WarehouseDao extends BaseDao<Warehouse, Long> implements IWarehouseDao {

	@Override
	public List<Map<String, Object>> lisByHospital(Long hospitalId) {
		String sql = "select wh.id, wh.code, wh.name from t_set_warehouse wh where wh.hospitalId=? and wh.isReceive=1 and wh.isDisabled=0";
		return super.listBySql(sql, null, Map.class, hospitalId);
	}

	@Override
	public DataGrid<Warehouse> queryByHospital(PageRequest pageablem,
			Long hospitalId, String searchkey) {
		String hql=" from Warehouse where hospital.id =?  ";
		
		if(!StringUtils.isEmpty(searchkey)){
			hql+=" and name like '%"+searchkey+"%'";
		}
		return (DataGrid<Warehouse>) super.query(hql, pageablem,hospitalId);
	}
	
	@Override
	public Warehouse queryByCodeAndPid(String code ,Long pid) {
		String hql = " from Warehouse where code = ? and hospital.id = ? ";
		return super.getByHql(hql, code, pid);
	}

	@Override
	public DataGrid<Warehouse> pageByHospital(PageRequest pageable, Long id) {
		String hql = "from Warehouse t where t.hospital.id = ?";
		return super.query(hql, pageable, id);
	}

	@Override
	public Warehouse findByCode(String code) {
		String hql = " from Warehouse where code = ?";
		return super.getByHql(hql, code);
	}

	@Override
	public Warehouse getLast(String hospitalCode) {
		String hql = "from Warehouse w where w.hospital.code=?";
		Sort sort = new Sort(new Order(Direction.DESC,"w.code"));
		List<Warehouse> warehouses = super.limitList(hql, 1, sort, hospitalCode);
		if(warehouses.size()==0){
			return null;
		}else{
			return warehouses.get(0);
		}
	}

	@Override
	public List<Map<String, Object>> listByLocation(int pxfs, String minLat, String maxLat, String minLng,
			String maxLng, String ypbm) {
		String sql = "select w.code, w.name, w.addr, w.longitude, w.latitude, ws.price, ws.num"
				+ " from t_set_warehouse w left join p_stock_warehousestock ws on w.code=ws.vendorCode"
				+ " where w.longitude>? and w.longitude<? and w.latitude>? and w.latitude<?";
		if(!StringUtils.isEmpty(ypbm)){
			sql += " and ws.productCode='"+ypbm+"'";
		}
		return super.listBySql(sql, null, Map.class, minLat, maxLat, minLng, maxLng);
	}
}