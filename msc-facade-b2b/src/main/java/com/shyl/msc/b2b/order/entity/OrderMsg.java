package com.shyl.msc.b2b.order.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.enmu.OrderDetailStatus;

/**
 * 订单状态
 * 
 * 
 */
@Entity
@Table(name = "t_order_orderMsg")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderMsg extends BasicEntity {
	private static final long serialVersionUID = -76076139021650607L;

	/**
	 * 采购计划明细编号
	 */
	private String purchasePlanDetailCode;
	/**
	 * 索引编号
	 */
	private String indexCode;
	/**
	 * 时间
	 */
	private Date statusDate;	
	/**
	 * 状态
	 * 
	 */
	private OrderDetailStatus orderDetailStatus;
	

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_orderMsg_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=30)
	public String getPurchasePlanDetailCode() {
		return purchasePlanDetailCode;
	}
	public void setPurchasePlanDetailCode(String purchasePlanDetailCode) {
		this.purchasePlanDetailCode = purchasePlanDetailCode;
	}
	@Column(length=30)
	public String getIndexCode() {
		return indexCode;
	}
	public void setIndexCode(String indexCode) {
		this.indexCode = indexCode;
	}
	public Date getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}
	public OrderDetailStatus getOrderDetailStatus() {
		return orderDetailStatus;
	}
	public void setOrderDetailStatus(OrderDetailStatus orderDetailStatus) {
		this.orderDetailStatus = orderDetailStatus;
	}
	

}