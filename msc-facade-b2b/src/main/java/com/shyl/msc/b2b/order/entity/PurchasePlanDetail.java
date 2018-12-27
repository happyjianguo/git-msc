package com.shyl.msc.b2b.order.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.msc.enmu.OrderDetailStatus;

/**
 * 采购订单计划明细 
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_order_purchaseplan_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchasePlanDetail extends OrderDetail {
	private static final long serialVersionUID = 3199265152401464871L;
	/**
	 * 状态
	 */
	public enum Status {
		normal("正常"),
		hcancel("医院取消");
		
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}

	/**
	 * 采购订单计划
	 */
	@JsonIgnore
	private PurchasePlan purchasePlan;
	/**
	 * 备注
	 */
	private String notes;	
	/**
	 * gpo编码(Company)
	 */
	private String gpoCode;
	/**
	 * gpo名称(Company)
	 */
	private String gpoName;
	/**
	 * 供应商编码(Company)
	 */
	private String vendorCode;
	/**
	 * 供应商名称(Company)
	 */
	private String vendorName;
	/**
	 * 状态 
	 */
	private Status status = Status.normal;	
	/**
	 * 订单计划明细状态
	 * 
	 */
	private OrderDetailStatus orderDetailStatus;

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_purchaseplan_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="purchasePlanId")
	public PurchasePlan getPurchasePlan() {
		return purchasePlan;
	}
	public void setPurchasePlan(PurchasePlan purchasePlan) {
		this.purchasePlan = purchasePlan;
	}
	@Column(length=100)
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Column(length=20)
	public String getGpoCode() {
		return gpoCode;
	}
	public void setGpoCode(String gpoCode) {
		this.gpoCode = gpoCode;
	}
	@Column(length=200)
	public String getGpoName() {
		return gpoName;
	}
	public void setGpoName(String gpoName) {
		this.gpoName = gpoName;
	}
	@Column(length=20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length=200)
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public OrderDetailStatus getOrderDetailStatus() {
		return orderDetailStatus;
	}
	public void setOrderDetailStatus(OrderDetailStatus orderDetailStatus) {
		this.orderDetailStatus = orderDetailStatus;
	}
	
	

}