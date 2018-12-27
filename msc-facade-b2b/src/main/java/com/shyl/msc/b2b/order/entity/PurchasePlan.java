package com.shyl.msc.b2b.order.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.msc.enmu.CreateMethod;
import com.shyl.msc.enmu.UrgencyLevel;

/**
 * 采购计划
 * 
 * 
 */
@Entity
@Table(name = "t_order_purchaseplan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchasePlan extends Order {
	private static final long serialVersionUID = -76076139021650607L;

	/**
	 * 产生方式
	 */
	private CreateMethod createMethod;	
	/**
	 * 要求供货日期
	 */
	private Date requireDate;
	/**
	 * 紧急程度(0是紧急，1是不紧急)
	 */
	private UrgencyLevel urgencyLevel;
	/**
	 * 是否多次配送(0是单次配送，1是多次配送)
	 */
	private Integer isManyDelivery;
	/**
	 * 计划类型(0是集中采购,1是处方外配 )
	 */
	private Integer planType=0;

	/**
	 * 采购订单明细
	 */
	@JsonIgnore
	private Set<PurchasePlanDetail> purchasePlanDetails = new HashSet<PurchasePlanDetail>();

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_purchaseplan_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CreateMethod getCreateMethod() {
		return createMethod;
	}
	public void setCreateMethod(CreateMethod createMethod) {
		this.createMethod = createMethod;
	}
	public Date getRequireDate() {
		return requireDate;
	}
	public void setRequireDate(Date requireDate) {
		this.requireDate = requireDate;
	}
	public UrgencyLevel getUrgencyLevel() {
		return urgencyLevel;
	}
	public void setUrgencyLevel(UrgencyLevel urgencyLevel) {
		this.urgencyLevel = urgencyLevel;
	}
	public Integer getIsManyDelivery() {
		return isManyDelivery;
	}
	public void setIsManyDelivery(Integer isManyDelivery) {
		this.isManyDelivery = isManyDelivery;
	}
	@Transient
	public Set<PurchasePlanDetail> getPurchasePlanDetails() {
		return purchasePlanDetails;
	}
	public void setPurchasePlanDetails(Set<PurchasePlanDetail> purchasePlanDetails) {
		this.purchasePlanDetails = purchasePlanDetails;
	}
	public Integer getPlanType() {
		return planType;
	}
	public void setPlanType(Integer planType) {
		this.planType = planType;
	}
}