package com.shyl.msc.b2b.order.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
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
 * 采购订单计划
 * 
 * 
 */
@Entity
@Table(name = "t_order_purchaseorderplan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchaseOrderPlan extends Order {
	
	private static final long serialVersionUID = -76076139021650607L;

	/**
	 * 状态
	 */
	public enum Status {
		uneffect("未生效"),
		cancel("已取消"),
		effect("已生效"),
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
	 * 产生方式
	 */
	private CreateMethod createMethod;
	/**
	 * 要求供货日期
	 */
	private Date requireDate;
	/**
	 * 首次送货时间
	 */
	private Date firstDeliveryDate;
	/**
	 * 末次送货时间
	 */
	private Date lastDeliveryDate;
	/**
	 * 实际配送数量
	 */
	private BigDecimal deliveryNum;
	/**
	 * 实际配送金额
	 */
	private BigDecimal deliverySum;
	/**
	 * 实际入库数量
	 */
	private BigDecimal inOutBoundNum;
	/**
	 * 实际入库金额
	 */
	private BigDecimal inOutBoundSum;
	/**
	 * 拒收数量
	 */
	private BigDecimal rejectNum;
	/**
	 * 拒收金额
	 */
	private BigDecimal rejectSum;
	/**
	 * 退货数量
	 */
	private BigDecimal returnsNum;
	/**
	 * 退货金额
	 */
	private BigDecimal returnsSum;
	/**
	 * 紧急程度(0是紧急，1是不紧急)
	 */
	private UrgencyLevel urgencyLevel;
	/**
	 * 状态
	 */
	private Status status;
	/**
	 * 审核时间
	 */
	private Date auditDate;
	/**
	 * 是否多次配送(0是单次配送，1是多次配送)
	 */
	private Integer isManyDelivery;
	/**
	 * 是否过账（0未过账，1过账）
	 */
	private Integer isPassAudit=0;
	/**
	 * 采购计划单编号
	 */
	private String purchasePlanCode;
	/**
	 * 采购订单明细
	 */
	@JsonIgnore
	private Set<PurchaseOrderPlanDetail> purchaseOrderPlanDetails = new HashSet<PurchaseOrderPlanDetail>();

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_purchaseorderplan_s")
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
	public Date getFirstDeliveryDate() {
		return firstDeliveryDate;
	}
	public void setFirstDeliveryDate(Date firstDeliveryDate) {
		this.firstDeliveryDate = firstDeliveryDate;
	}
	public Date getLastDeliveryDate() {
		return lastDeliveryDate;
	}
	public void setLastDeliveryDate(Date lastDeliveryDate) {
		this.lastDeliveryDate = lastDeliveryDate;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getDeliveryNum() {
		return deliveryNum;
	}
	public void setDeliveryNum(BigDecimal deliveryNum) {
		this.deliveryNum = deliveryNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getDeliverySum() {
		return deliverySum;
	}
	public void setDeliverySum(BigDecimal deliverySum) {
		this.deliverySum = deliverySum;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getInOutBoundNum() {
		return inOutBoundNum;
	}
	public void setInOutBoundNum(BigDecimal inOutBoundNum) {
		this.inOutBoundNum = inOutBoundNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getInOutBoundSum() {
		return inOutBoundSum;
	}
	public void setInOutBoundSum(BigDecimal inOutBoundSum) {
		this.inOutBoundSum = inOutBoundSum;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getRejectNum() {
		return rejectNum;
	}
	public void setRejectNum(BigDecimal rejectNum) {
		this.rejectNum = rejectNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getRejectSum() {
		return rejectSum;
	}
	public void setRejectSum(BigDecimal rejectSum) {
		this.rejectSum = rejectSum;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getReturnsNum() {
		return returnsNum;
	}
	public void setReturnsNum(BigDecimal returnsNum) {
		this.returnsNum = returnsNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getReturnsSum() {
		return returnsSum;
	}
	public void setReturnsSum(BigDecimal returnsSum) {
		this.returnsSum = returnsSum;
	}
	public UrgencyLevel getUrgencyLevel() {
		return urgencyLevel;
	}
	public void setUrgencyLevel(UrgencyLevel urgencyLevel) {
		this.urgencyLevel = urgencyLevel;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}
	public Integer getIsManyDelivery() {
		return isManyDelivery;
	}
	public void setIsManyDelivery(Integer isManyDelivery) {
		this.isManyDelivery = isManyDelivery;
	}
	public Integer getIsPassAudit() {
		return isPassAudit;
	}
	public void setIsPassAudit(Integer isPassAudit) {
		this.isPassAudit = isPassAudit;
	}
	@Transient
	public Set<PurchaseOrderPlanDetail> getPurchaseOrderPlanDetails() {
		return purchaseOrderPlanDetails;
	}
	public void setPurchaseOrderPlanDetails(
			Set<PurchaseOrderPlanDetail> purchaseOrderPlanDetails) {
		this.purchaseOrderPlanDetails = purchaseOrderPlanDetails;
	}
	@Column(length=30)
	public String getPurchasePlanCode() {
		return purchasePlanCode;
	}
	public void setPurchasePlanCode(String purchasePlanCode) {
		this.purchasePlanCode = purchasePlanCode;
	}
	

}