package com.shyl.msc.b2b.task;

import com.shyl.common.cache.dao.TaskLockDAO;
import com.shyl.common.entity.PageRequest;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import com.shyl.msc.b2b.stl.entity.TradeInvoiceDetail;
import com.shyl.msc.b2b.stl.service.ITradeInvoiceDetailService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("TradeInvoiceJob")
public class TradeInvoiceJob {

    @Resource
    private ITradeInvoiceDetailService tradeInvoiceDetailService;
    @Resource
    private TaskLockDAO taskLockDAO;

    /**
     * 过账任务程序
     * 每天凌晨1点执行"0 0 1 * * ?"
     * 每30秒执行一次 "0/30 * * * * ?"
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void updateTradeInvoice(){
        if(!taskLockDAO.lock("updateTradeInvoice")){
            return;
        }
        try {
            List<TradeInvoiceDetail> tradeInvoiceDetails = getBySelect(TradeInvoice.Type.vendorToHospital,TradeInvoice.Type.GPOToVendor);
            tradeInvoiceDetails.addAll(getBySelect(TradeInvoice.Type.GPOToVendor,TradeInvoice.Type.producerToGPO));
            tradeInvoiceDetailService.updateBatchByCode("",tradeInvoiceDetails);
        }finally {
            taskLockDAO.unlock("updateTradeInvoice");
        }
    }

    private List<TradeInvoiceDetail> getBySelect(TradeInvoice.Type type,TradeInvoice.Type type1){
        PageRequest pageRequest = new PageRequest();
        pageRequest.getQuery().put("t#tradeInvoice.type_E_EQ", type);
        pageRequest.getQuery().put("t#isExistsParent_I_EQ",0);
        List<TradeInvoiceDetail> tradeInvoiceDetails = tradeInvoiceDetailService.list("",pageRequest);
        if(CollectionUtils.isEmpty(tradeInvoiceDetails)){
            return null;
        }
        //根据商品编码和批号，供应商来查询gpo到供应商的数据
        StringBuilder s = new StringBuilder();
        s.append("'0'");
        List<TradeInvoiceDetail> gpoTradeInvoiceDetails = new ArrayList<>();
        int i = 0;
        for(TradeInvoiceDetail td : tradeInvoiceDetails){
            i++;
            s.append(",'"+td.getProductCode()+td.getBatchCode());
            if(TradeInvoice.Type.vendorToHospital.equals(type)){
                s.append(td.getTradeInvoice().getVendorCode()+"'");
            }else if(TradeInvoice.Type.GPOToVendor.equals(type)){
                s.append(td.getTradeInvoice().getGpoCode()+"'");
            }
            if(i % 300 ==0){
                gpoTradeInvoiceDetails.addAll(tradeInvoiceDetailService.getByCode("",s.toString(),type1));
                s = new StringBuilder();
                s.append("'0'");
            }
        }
        gpoTradeInvoiceDetails.addAll(tradeInvoiceDetailService.getByCode("",s.toString(),type1));
        Map<String,TradeInvoiceDetail> maps = new HashMap<>();
        for(TradeInvoiceDetail td2 : gpoTradeInvoiceDetails){
            String key = td2.getProductCode()+td2.getBatchCode()+td2.getTradeInvoice().getCustomerCode();
            if(null == maps.get(key)){
                maps.put(key,td2);
            }
        }
        List<TradeInvoiceDetail> updateTradeInvoiceDetails = new ArrayList<>();
        for(TradeInvoiceDetail td : tradeInvoiceDetails){
            String key = "";
            if(TradeInvoice.Type.vendorToHospital.equals(type)){
                key = td.getProductCode()+td.getBatchCode()+td.getTradeInvoice().getVendorCode();
            }else if(TradeInvoice.Type.GPOToVendor.equals(type)){
                key = td.getProductCode()+td.getBatchCode()+td.getTradeInvoice().getGpoCode();
            }
            if(maps.get(key) != null){
                td.setIsExistsParent(new Integer(1));
                updateTradeInvoiceDetails.add(td);
            }/*else{
                td.setIsExistsParent(new Integer(0));
            }*/
        }
        return updateTradeInvoiceDetails;
    }
}
