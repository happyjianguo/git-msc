package com.shyl.msc.b2b.task;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shyl.common.cache.dao.TaskLockDAO;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;

@Component("contractTaskJob")
public class ContractTaskJob {
	
	@Resource
	private IContractDetailService contractDetailService;
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
		if(!taskLockDAO.lock("contractTaskJob")){
			return;
		}
		try {

	        System.out.println("开始执行-------------accountTaskJob");
	        long begin = System.currentTimeMillis();
	        List<AttributeItem> attributeItems = attributeItemService.listByAttributeNo("","platform_no");
	        for(AttributeItem attributeItem:attributeItems){
	        	contractDetailService.contractToGoodsPrice(attributeItem.getField1());
	        }
	        long end = System.currentTimeMillis() - begin;
	        System.out.println("完成执行-------------accountTaskJob");
	        System.out.println("耗时：" + end + "毫秒");
		} finally {
			taskLockDAO.unlock("contractTaskJob");
		}
    }
}
