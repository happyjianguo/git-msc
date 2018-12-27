/*
 * 
 * 
 * 
 */
package com.shyl.msc.b2b.order.dao.impl;

import javax.annotation.Resource;

import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.Sn;
import com.shyl.msc.enmu.OrderType;

/**
 * Dao - 序列号
 * 
 */
@Repository
public class SnDao extends BaseDao<Sn,Long> implements ISnDao,InitializingBean {
	private HiloOptimizer planHiloOptimizer;
	private HiloOptimizer orderPlanHiloOptimizer;
	private HiloOptimizer orderHiloOptimizer;
	private HiloOptimizer deliveryHiloOptimizer;
	private HiloOptimizer returnsHiloOptimizer;
	private HiloOptimizer inoutboundHiloOptimizer;
	private HiloOptimizer invoiceHiloOptimizer;
	private HiloOptimizer settlementHiloOptimizer;
	private HiloOptimizer paymentHiloOptimizer;
	private HiloOptimizer returnsRequestHiloOptimizer;
	private HiloOptimizer orderRequestHiloOptimizer;
	private HiloOptimizer contractHiloOptimizer;
	private HiloOptimizer contractRequestHiloOptimizer;
	private HiloOptimizer tradeInvoiceHiloOptimizer;
	
	private SessionFactory sessionFactory;
	@Resource(name="sessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		planHiloOptimizer = new HiloOptimizer(OrderType.plan, "J");
		orderPlanHiloOptimizer = new HiloOptimizer(OrderType.orderPlan, "X");
		orderHiloOptimizer = new HiloOptimizer(OrderType.order, "D");
		deliveryHiloOptimizer = new HiloOptimizer(OrderType.delivery, "P");
		returnsHiloOptimizer = new HiloOptimizer(OrderType.returns, "T");
		inoutboundHiloOptimizer = new HiloOptimizer(OrderType.inoutbound, "R");
		invoiceHiloOptimizer = new HiloOptimizer(OrderType.invoice, "F");		
		settlementHiloOptimizer = new HiloOptimizer(OrderType.settlement, "K");
		paymentHiloOptimizer = new HiloOptimizer(OrderType.payment, "Z");	
		returnsRequestHiloOptimizer = new HiloOptimizer(OrderType.returnsRequest, "S");
		orderRequestHiloOptimizer = new HiloOptimizer(OrderType.orderClosedRequest, "C");
		contractHiloOptimizer = new HiloOptimizer(OrderType.contract, "H");
		contractRequestHiloOptimizer = new HiloOptimizer(OrderType.contractClosedRequest, "E");
		tradeInvoiceHiloOptimizer = new HiloOptimizer(OrderType.tradeInvoice, "Y");
	}
	public String getCode(OrderType type) {
		Assert.notNull(type);
		
		if (type == OrderType.plan) {
			return planHiloOptimizer.generate();
		} else if (type == OrderType.orderPlan) {
			return orderPlanHiloOptimizer.generate();
		} else if (type == OrderType.order) {
			return orderHiloOptimizer.generate();
		} else if (type == OrderType.delivery) {
			return deliveryHiloOptimizer.generate();
		} else if (type == OrderType.returns) {
			return returnsHiloOptimizer.generate();
		} else if (type == OrderType.inoutbound) {
			return inoutboundHiloOptimizer.generate();
		} else if(type == OrderType.invoice){
			return invoiceHiloOptimizer.generate();
		} else if (type == OrderType.settlement) {
			return settlementHiloOptimizer.generate();
		} else if(type == OrderType.payment){
			return paymentHiloOptimizer.generate();
		} else if(type == OrderType.returnsRequest){
			return returnsRequestHiloOptimizer.generate();
		} else if(type == OrderType.orderClosedRequest){
			return orderRequestHiloOptimizer.generate();
		} else if(type == OrderType.contract){
			return contractHiloOptimizer.generate();
		}else if(type == OrderType.contractClosedRequest){
			return contractRequestHiloOptimizer.generate();
		}else if(type == OrderType.tradeInvoice){
			return tradeInvoiceHiloOptimizer.generate();
		}
		return null;
	}

	private long getLastValue(OrderType type,String today) {
		String jpql = "select sn from Sn sn where sn.type = :type and sn.codeDate = :codeDate";
		Sn sn = (Sn)getSession().createQuery(jpql).setFlushMode(FlushMode.COMMIT).setLockMode("sn", LockMode.PESSIMISTIC_WRITE)
				.setParameter("type", type).setParameter("codeDate", today)
				.uniqueResult();
		if(sn == null){
			sn = new Sn();
			sn.setType(type);
			sn.setCodeDate(today);
			sn.setLastValue(1L);
		}
		long lastValue = sn.getLastValue();
		sn.setLastValue(lastValue + 1);
		getSession().merge(sn);
		getSession().flush();
		return lastValue;
	}
	
	/**
	 * 高低位算法
	 */
	private class HiloOptimizer {

		private OrderType type;
		private String prefix;

		public HiloOptimizer(OrderType type, String prefix) {
			this.type = type;
			this.prefix = prefix;
		}
		public synchronized String generate(){
			String result = "";
			String today = DateUtil.getToday();
			long lastValue = getLastValue(type, today);
			result = prefix + today + changeLastValue(lastValue, 8);
			return result;
		}
		
		private String changeLastValue(long lastValue, int len){
		     String strHao = lastValue+"";
		     while (strHao.length() < len) {
		         strHao = "0" + strHao;
		     }
		     return strHao;
		}
	}

}
