package com.shyl.msc.b2b.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shyl.common.cache.dao.TaskLockDAO;
import com.shyl.msc.b2b.stl.service.IAccountOrderService;
import com.shyl.msc.b2b.stl.service.IAccountProductService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;

@Component("accountTaskJob")
public class AccountTaskJob {
	
	@Resource
	private IAccountOrderService accountOrderService;
	@Resource
	private IAccountProductService accountProductService;
	@Resource
	private TaskLockDAO taskLockDAO;
	@Resource
	private IAttributeItemService attributeItemService;
	/**
	 * 过账任务程序   
	 * 每天凌晨1点执行"0 0 1 * * ?"
	 * 每30秒执行一次 "0/30 * * * * ?"
	 */
	@Scheduled(cron = "0 0 2 * * ?")
    public void accountTaskJob() {
		if(!taskLockDAO.lock("accountTaskJob")){
			return;
		}
        System.out.println("开始执行-------------accountTaskJob");

		/*MonitorTask mt = new MonitorTask();
		mt.setMethodName("accountTaskJob");*/

		try {
	        long begin = System.currentTimeMillis();
	        List<AttributeItem> attributeItems = attributeItemService.listByAttributeNo("","platform_no");
	        for(AttributeItem attributeItem:attributeItems){
				accountOrderService.passAccount(attributeItem.getField1());//过订单账务
				accountProductService.passAccount(attributeItem.getField1());//过产品账务
	        }
	        long end = System.currentTimeMillis() - begin;
	        //System.out.println("完成执行-------------accountTaskJob");
	        System.out.println("耗时：" + end + "毫秒");
		}finally {
		taskLockDAO.unlock("accountTaskJob");
		}
    }
}
