package com.shyl.msc.b2b.order.entity;


import java.math.BigDecimal;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 退货单明细 */
@Entity
@Table(name = "t_order_returnsorder_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReturnsOrderDetail extends OrderDetail {
	private static final long serialVersionUID = 8866547909888950360L;

	/**
	 * 退货单
	 */
	private ReturnsOrder returnsOrder;
	/**
	 * 生产批号
	 */
	private String batchCode;
	/**
	 * 生产日期
	 */
	private String batchDate;
	/**
	 * 有效日期
	 */
	private String expiryDate;
	/**
	 * 退货原因
	 */
	private String reason;
	/**
	 *  配送单明细编号
	 */
	private String deliveryOrderDetailCode;
	/**
	 * 开票金额
	 */
	private BigDecimal invoiceGoodsSum;
	/**
	 * 是否开票
	 */
	private Integer isInvoice=0;
	/**
	 *  退货申请单明细编号
	 */
	private String returnsRequestDetailCode;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_returnsorder_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="returnsOrderId")
	public ReturnsOrder getReturnsOrder() {
		return returnsOrder;
	}
	public void setReturnsOrder(ReturnsOrder returnsOrder) {
		this.returnsOrder = returnsOrder;
	}
	@Column(length=100)
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	@Column(length=10)
	public String getBatchDate() {
		return batchDate;
	}
	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}
	@Column(length=10)
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	@Column(length=100)
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	@Column(length=30)
	public String getDeliveryOrderDetailCode() {
		return deliveryOrderDetailCode;
	}
	public void setDeliveryOrderDetailCode(String deliveryOrderDetailCode) {
		this.deliveryOrderDetailCode = deliveryOrderDetailCode;
	}
	@Column(length=30)
	public String getReturnsRequestDetailCode() {
		return returnsRequestDetailCode;
	}
	public void setReturnsRequestDetailCode(String returnsRequestDetailCode) {
		this.returnsRequestDetailCode = returnsRequestDetailCode;
	}
	
	@Column(precision=16, scale=2)
	public BigDecimal getInvoiceGoodsSum() {
		return invoiceGoodsSum;
	}
	public void setInvoiceGoodsSum(BigDecimal invoiceGoodsSum) {
		this.invoiceGoodsSum = invoiceGoodsSum;
	}
	public Integer getIsInvoice() {
		return isInvoice;
	}
	public void setIsInvoice(Integer isInvoice) {
		this.isInvoice = isInvoice;
	}
	
}