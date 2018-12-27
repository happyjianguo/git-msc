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

/**
 * 配送单
 * 
 * 
 * */
@Entity
@Table(name = "t_order_deliveryorder")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DeliveryOrder extends Order {
	private static final long serialVersionUID = -1202131114162467610L;
	/**
	 * 状态
	 */
	public enum Status {
		unreceive("未收货"),
		receiving("收货中"),
		closed("收货完成");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 送货公司id(Company)
	 */
	private String senderCode;
	/**
	 * 送货公司名称(Company)
	 */
	private String senderName;
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
	 * 创建日期（配送商收到）
	 */
	private Date receiveDate;
	/**
	 * 验货日期
	 */
	private Date checkedDate;
	/**
	 * 状态
	 */
	private Status status;
	/**
	 * 采购订单编号
	 */
	private String purchaseOrderCode;
	/**
	 * 采购订单计划编号
	 */
	private String purchaseOrderPlanCode;
	/**
	 * 采购计划编号
	 */
	private String purchasePlanCode;
	/**
	 * 是1否0已开票
	 */
	private Integer isInvoiced = 0;
	/**
	 * 条码(一般为药企编号+药企配送单号)
	 */
	private String barcode;
	/**
	 * 配送单明细
	 */
	@JsonIgnore
	private Set<DeliveryOrderDetail> deliveryOrderDetails = new HashSet<DeliveryOrderDetail>();

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_deliveryorder_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=20)
	public String getSenderCode() {
		return senderCode;
	}
	public void setSenderCode(String senderCode) {
		this.senderCode = senderCode;
	}
	@Column(length=200)
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
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
	public Date getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	public Date getCheckedDate() {
		return checkedDate;
	}
	public void setCheckedDate(Date checkedDate) {
		this.checkedDate = checkedDate;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	@Column(length=30)
	public String getPurchaseOrderCode() {
		return purchaseOrderCode;
	}
	public void setPurchaseOrderCode(String purchaseOrderCode) {
		this.purchaseOrderCode = purchaseOrderCode;
	}
	@Column(length=30)
	public String getPurchaseOrderPlanCode() {
		return purchaseOrderPlanCode;
	}
	public void setPurchaseOrderPlanCode(String purchaseOrderPlanCode) {
		this.purchaseOrderPlanCode = purchaseOrderPlanCode;
	}
	@Column(length=30)
	public String getPurchasePlanCode() {
		return purchasePlanCode;
	}
	public void setPurchasePlanCode(String purchasePlanCode) {
		this.purchasePlanCode = purchasePlanCode;
	}
	public Integer getIsInvoiced() {
		return isInvoiced;
	}
	public void setIsInvoiced(Integer isInvoiced) {
		this.isInvoiced = isInvoiced;
	}
	@Column(length=50)
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	@Transient
	public Set<DeliveryOrderDetail> getDeliveryOrderDetails() {
		return deliveryOrderDetails;
	}
	public void setDeliveryOrderDetails(
			Set<DeliveryOrderDetail> deliveryOrderDetails) {
		this.deliveryOrderDetails = deliveryOrderDetails;
	}
}