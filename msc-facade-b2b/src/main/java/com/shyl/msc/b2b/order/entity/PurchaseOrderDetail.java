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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 采购订单明细 
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_order_purchaseorder_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchaseOrderDetail extends OrderDetail {
	private static final long serialVersionUID = 3199265152401464871L;
	
	/**
	 * 状态
	 */
	public enum Status {
		normal("正常"),
		stop("已终止");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 采购订单
	 */
	@JsonIgnore
	private PurchaseOrder purchaseOrder;
	/**
	 * 实际配送数量
	 */
	private BigDecimal deliveryGoodsNum;
	/**
	 * 实际配送金额
	 */
	private BigDecimal deliveryGoodsSum;
	/**
	 * 实际入库数量
	 */
	private BigDecimal inOutBoundGoodsNum;
	/**
	 * 实际入库金额
	 */
	private BigDecimal inOutBoundGoodsSum;
	/**
	 * 拒收数量
	 */
	private BigDecimal rejectGoodsNum;
	/**
	 * 拒收金额
	 */
	private BigDecimal rejectGoodsSum;
	/**
	 * 退货数量
	 */
	private BigDecimal returnsGoodsNum;
	/**
	 * 退货金额
	 */
	private BigDecimal returnsGoodsSum;
	/**
	 * 备注
	 */
	private String notes;
	/**
	 * 订单计划明细编号
	 */
	private String purchaseOrderPlanDetailCode;
	/**
	 * 状态
	 */
	private Status status;

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_purchaseorder_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="purchaseOrderId")
	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getDeliveryGoodsNum() {
		return deliveryGoodsNum;
	}
	public void setDeliveryGoodsNum(BigDecimal deliveryGoodsNum) {
		this.deliveryGoodsNum = deliveryGoodsNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getDeliveryGoodsSum() {
		return deliveryGoodsSum;
	}
	public void setDeliveryGoodsSum(BigDecimal deliveryGoodsSum) {
		this.deliveryGoodsSum = deliveryGoodsSum;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getInOutBoundGoodsNum() {
		return inOutBoundGoodsNum;
	}
	public void setInOutBoundGoodsNum(BigDecimal inOutBoundGoodsNum) {
		this.inOutBoundGoodsNum = inOutBoundGoodsNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getInOutBoundGoodsSum() {
		return inOutBoundGoodsSum;
	}
	public void setInOutBoundGoodsSum(BigDecimal inOutBoundGoodsSum) {
		this.inOutBoundGoodsSum = inOutBoundGoodsSum;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getRejectGoodsNum() {
		return rejectGoodsNum;
	}
	public void setRejectGoodsNum(BigDecimal rejectGoodsNum) {
		this.rejectGoodsNum = rejectGoodsNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getRejectGoodsSum() {
		return rejectGoodsSum;
	}
	public void setRejectGoodsSum(BigDecimal rejectGoodsSum) {
		this.rejectGoodsSum = rejectGoodsSum;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getReturnsGoodsNum() {
		return returnsGoodsNum;
	}
	public void setReturnsGoodsNum(BigDecimal returnsGoodsNum) {
		this.returnsGoodsNum = returnsGoodsNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getReturnsGoodsSum() {
		return returnsGoodsSum;
	}
	public void setReturnsGoodsSum(BigDecimal returnsGoodsSum) {
		this.returnsGoodsSum = returnsGoodsSum;
	}
	@Column(length=100)
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Column(length=30)
	public String getPurchaseOrderPlanDetailCode() {
		return purchaseOrderPlanDetailCode;
	}
	public void setPurchaseOrderPlanDetailCode(String purchaseOrderPlanDetailCode) {
		this.purchaseOrderPlanDetailCode = purchaseOrderPlanDetailCode;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

}