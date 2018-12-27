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
import com.shyl.common.entity.BasicEntity;

/**
 * 采购订单结案申请明细
 * 
 * 
 */
@Entity
@Table(name = "t_order_purchase_c_r_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchaseClosedRequestDetail extends BasicEntity {
	private static final long serialVersionUID = -76076139021650607L;
	
	/**
	 * 订单结案申请
	 */
	private PurchaseClosedRequest purchaseClosedRequest;
	/**
	 * 订单明细编号
	 */
	private String purchaseOrderDetailCode;
	/**
	 * 药品编码
	 */
	private String productCode;
	/**
	 * 药品名称
	 */
	private String productName;
	/**
	 * 生产企业
	 */
	private String producerName;
	/**
	 * 剂型名称
	 */
	private String dosageFormName;
	/**
	 * 规格
	 */
	private String model;	
	/**
	 * 包装规格
	 */
	private String packDesc;
	/**
	 * 单位
	 */
	private String unit;
	/**
	 * 单价
	 */
	private BigDecimal price;
	/**
	 * 数量
	 */
	private BigDecimal goodsNum;
	/**
	 * 金额
	 */
	private BigDecimal goodsSum;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_purchase_c_request_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="purchaseClosedRequestId")
	public PurchaseClosedRequest getPurchaseClosedRequest() {
		return purchaseClosedRequest;
	}
	public void setPurchaseClosedRequest(PurchaseClosedRequest purchaseClosedRequest) {
		this.purchaseClosedRequest = purchaseClosedRequest;
	}
	public String getPurchaseOrderDetailCode() {
		return purchaseOrderDetailCode;
	}
	public void setPurchaseOrderDetailCode(String purchaseOrderDetailCode) {
		this.purchaseOrderDetailCode = purchaseOrderDetailCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	@Column(length=100)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Column(length=300)
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	@Column(length = 50)
	public String getDosageFormName() {
		return dosageFormName;
	}

	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}
	@Column(length = 300)
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@Column(length = 50)
	public String getPackDesc() {
		return packDesc;
	}
	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}
	@Column(length=20)
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(BigDecimal goodsNum) {
		this.goodsNum = goodsNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getGoodsSum() {
		return goodsSum;
	}
	public void setGoodsSum(BigDecimal goodsSum) {
		this.goodsSum = goodsSum;
	}
}