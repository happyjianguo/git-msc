package com.shyl.msc.count.task;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shyl.common.cache.dao.TaskLockDAO;
import com.shyl.msc.count.service.IHospitalProductCService;
import com.shyl.msc.count.service.IOrderCService;
import com.shyl.msc.count.service.IOrderDetailCService;
import com.shyl.msc.count.service.IVendorProductCService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;

@Component("countTaskJob")
public class CountTaskJob {
	
	@Resource
	private IOrderCService orderCService;
	@Resource
	private IOrderDetailCService orderDetailCService;
	@Resource
	private IHospitalProductCService hospitalProductCService;
	@Resource
	private IVendorProductCService vendorProductCService;
	@Resource
	private TaskLockDAO taskLockDAO;
	@Resource
	private IAttributeItemService attributeItemService;
	
	/**
	 * 过账任务程序   
	 * 每天凌晨1点执行"0 0 1 * * ?"
	 * 每30秒执行一次 "0/30 * * * * ?"
	 */
//	@Scheduled(cron = "0 0 2 * * ?")
		@Scheduled(cron = "0/10 * * * * ?")
    public void countTaskJob() {
		if(!taskLockDAO.lock("countTaskJob3")){
			return;
		}
        System.out.println("开始执行-------------countTaskJob");
		try {
	        long begin = System.currentTimeMillis();
	        List<AttributeItem> attributeItems = attributeItemService.listByAttributeNo("","platform_no");
	        for(AttributeItem attributeItem:attributeItems){
	        	orderCService.pass(attributeItem.getField1());
	            orderDetailCService.pass(attributeItem.getField1());
	            
	            hospitalProductCService.pass(attributeItem.getField1());
	            vendorProductCService.pass(attributeItem.getField1());
	        }
	        long end = System.currentTimeMillis() - begin;
	        System.out.println("完成执行-------------countTaskJob");
	        System.out.println("耗时：" + end + "毫秒");
		
		}catch (Exception e){
			taskLockDAO.unlock("countTaskJob3");
			e.printStackTrace();
			System.out.println("msg========"+e.getMessage());
		}finally {
			taskLockDAO.unlock("countTaskJob3");
		}
    }
}
