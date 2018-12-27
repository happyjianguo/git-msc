package com.shyl.msc.supervise.dao.impl;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IHisRegItemDao;
import com.shyl.msc.supervise.entity.HisRegItem;

import java.util.Map;

@Repository
public class HisRegItemDao extends BaseDao<HisRegItem, Long> implements IHisRegItemDao {
    @Override
    public DataGrid<Map<String, Object>> queryByPage(PageRequest page) {
        String sql = "select r.productCode,r.productName,r.dosageFormName,r.model,r.packDesc,r.producerName,r.sum,"
                + "m.isGPOPurchase from sup_his_reg_item r left join sup_medicine m on r.productCode=m.code";
        return this.findBySql(sql, page, Map.class);
    }
}
