package com.shyl.msc.base.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shyl.common.cache.dao.TaskLockDAO;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.dm.service.IProductPriceDayService;
import com.shyl.msc.dm.service.IProductPriceService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;

@Component("priceTaskJob")
public class PriceTaskJob {
	
	@Resource
	private IProductPriceDayService productPriceDayService;
	@Resource
	private IProductPriceService productPriceService;
	@Resource
	private TaskLockDAO taskLockDAO;
	@Resource
	private IAttributeItemService attributeItemService;
	
	/**
	 * 设置价格任务程序   
	 * 每天凌晨2点执行"0 0 2 * * ?"
	 * 每30秒执行一次 "0/30 * * * * ?"
	 */
	@Scheduled(cron = "0 0 2 * * ?")  
    public void priceTaskJob() {
		if(!taskLockDAO.lock("priceTaskJob")){
			return;
		}
		try {

			System.out.println("开始执行-------------priceTaskJob");
	        long begin = System.currentTimeMillis();
	        //1、查询今天可生效的价格
	        String today = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
	        List<AttributeItem> attributeItems = attributeItemService.listByAttributeNo("","platform_no");
	        for(AttributeItem attributeItem:attributeItems){
	        	List<ProductPrice> pplist = productPriceService.effectList(attributeItem.getField1(), today);//TODO
	            //2、循环生效价格
	            for (ProductPrice productPrice : pplist) {
	            	productPriceService.effectPrice(attributeItem.getField1(), productPrice);//TODO
	    		}
	        }
	        
	        long end = System.currentTimeMillis() - begin;
	        System.out.println("完成执行-------------priceTaskJob");
	        System.out.println("耗时：" + end + "毫秒");
		} finally {
			taskLockDAO.unlock("priceTaskJob");
		}
    }
	
}
