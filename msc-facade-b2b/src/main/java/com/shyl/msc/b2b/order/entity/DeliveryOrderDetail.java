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

/**
 * 配送单明细
 * 
 * */
@Entity
@Table(name = "t_order_deliveryorder_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DeliveryOrderDetail extends OrderDetail {
	private static final long serialVersionUID = 6536458620920376491L;
	/**
	 * 配送单
	 */
	private DeliveryOrder deliveryOrder;
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
	 * 条码
	 */
	private String barcode;
	/**
	 * 质量记录
	 */
	private String qualityRecord;
	/**
	 * 检验报告链接
	 */
	private String inspectionReportUrl;
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
	 * 开票金额
	 */
	private BigDecimal invoiceGoodsSum;
	/**
	 * 是否开票
	 */
	private Integer isInvoice=0;
	/**
	 * 订单明细编号
	 */
	private String purchaseOrderDetailCode;
	/**
	 * 订单计划明细编号
	 */
	private String purchaseOrderPlanDetailCode;
	/**
	 * 采购计划明细编号
	 */
	private String purchasePlanDetailCode;

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_deliveryorder_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="deliveryOrderId")
	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}
	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
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
	@Column(length=50)
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	@Column(length=300)
	public String getQualityRecord() {
		return qualityRecord;
	}
	public void setQualityRecord(String qualityRecord) {
		this.qualityRecord = qualityRecord;
	}
	@Column(length=100)
	public String getInspectionReportUrl() {
		return inspectionReportUrl;
	}
	public void setInspectionReportUrl(String inspectionReportUrl) {
		this.inspectionReportUrl = inspectionReportUrl;
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
	@Column(length=30)
	public String getPurchaseOrderDetailCode() {
		return purchaseOrderDetailCode;
	}
	public void setPurchaseOrderDetailCode(String purchaseOrderDetailCode) {
		this.purchaseOrderDetailCode = purchaseOrderDetailCode;
	}
	@Column(length=30)
	public String getPurchaseOrderPlanDetailCode() {
		return purchaseOrderPlanDetailCode;
	}
	public void setPurchaseOrderPlanDetailCode(String purchaseOrderPlanDetailCode) {
		this.purchaseOrderPlanDetailCode = purchaseOrderPlanDetailCode;
	}
	@Column(length=30)
	public String getPurchasePlanDetailCode() {
		return purchasePlanDetailCode;
	}
	public void setPurchasePlanDetailCode(String purchasePlanDetailCode) {
		this.purchasePlanDetailCode = purchasePlanDetailCode;
	}


}