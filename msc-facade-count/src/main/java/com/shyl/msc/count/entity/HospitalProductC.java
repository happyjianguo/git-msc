package com.shyl.msc.count.entity;

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
import com.shyl.msc.dm.entity.Product;

/**
 * 医院药品统计
 * 
 * 
 */
@Entity
@Table(name = "t_count_hospitalproduct_c")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HospitalProductC extends BasicEntity {
	private static final long serialVersionUID = -76076139021650607L;
	
	/**
	 * 年月
	 */
	private String month;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 药品
	 */
	private Product product;
	/**
	 * 合同金额
	 */
	private BigDecimal contractSum;
	/**
	 * 合同内采购金额
	 */
	private BigDecimal contractPurchaseSum;	
	/**
	 * 交易次数
	 */
	private Integer purchaseTimes;
	/**
	 * 配送次数
	 */
	private Integer deliveryTimes;
	/**
	 * 断货次数
	 */
	private Integer shortSupplyTimes;
	/**
	 * 断货采购金额
	 */
	private BigDecimal shortSupplySum;
	/**
	 * 配送药品有效期总天数
	 */
	private Integer validityDayCount;
	/**
	 * 采购数量
	 */
	private BigDecimal purchaseNum;
	/**
	 * 采购金额
	 */
	private BigDecimal purchaseSum;
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
	 * 退货数量
	 */
	private BigDecimal returnsNum;
	/**
	 * 退货金额
	 */
	private BigDecimal returnsSum;
	/**
	 * 期初库存金额
	 */
	private BigDecimal beginStockSum;
	/**
	 * 期末库存金额
	 */
	private BigDecimal endStockSum;
	/**
	 * 合同标志
	 */
	private Integer contractFlag;
	/**
	 * 订单标志
	 */
	private Integer orderFlag;
	/**
	 * 库存标志
	 */
	private Integer stockFlag=0;
	
	/** 主键*/	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_count_hospitalproduct_c_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=10)
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	@Column(length=50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column(length=200)
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="productId",nullable=false)
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getContractSum() {
		return contractSum;
	}
	public void setContractSum(BigDecimal contractSum) {
		this.contractSum = contractSum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getContractPurchaseSum() {
		return contractPurchaseSum;
	}
	public void setContractPurchaseSum(BigDecimal contractPurchaseSum) {
		this.contractPurchaseSum = contractPurchaseSum;
	}
	public Integer getPurchaseTimes() {
		return purchaseTimes;
	}
	public void setPurchaseTimes(Integer purchaseTimes) {
		this.purchaseTimes = purchaseTimes;
	}
	public Integer getDeliveryTimes() {
		return deliveryTimes;
	}
	public void setDeliveryTimes(Integer deliveryTimes) {
		this.deliveryTimes = deliveryTimes;
	}
	public Integer getShortSupplyTimes() {
		return shortSupplyTimes;
	}
	public void setShortSupplyTimes(Integer shortSupplyTimes) {
		this.shortSupplyTimes = shortSupplyTimes;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getShortSupplySum() {
		return shortSupplySum;
	}
	public void setShortSupplySum(BigDecimal shortSupplySum) {
		this.shortSupplySum = shortSupplySum;
	}
	public Integer getValidityDayCount() {
		return validityDayCount;
	}
	public void setValidityDayCount(Integer validityDayCount) {
		this.validityDayCount = validityDayCount;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getPurchaseNum() {
		return purchaseNum;
	}
	public void setPurchaseNum(BigDecimal purchaseNum) {
		this.purchaseNum = purchaseNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getPurchaseSum() {
		return purchaseSum;
	}
	public void setPurchaseSum(BigDecimal purchaseSum) {
		this.purchaseSum = purchaseSum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getBeginStockSum() {
		return beginStockSum;
	}
	public void setBeginStockSum(BigDecimal beginStockSum) {
		this.beginStockSum = beginStockSum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getEndStockSum() {
		return endStockSum;
	}
	public void setEndStockSum(BigDecimal endStockSum) {
		this.endStockSum = endStockSum;
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
	public Integer getContractFlag() {
		return contractFlag;
	}
	public void setContractFlag(Integer contractFlag) {
		this.contractFlag = contractFlag;
	}
	public Integer getOrderFlag() {
		return orderFlag;
	}
	public void setOrderFlag(Integer orderFlag) {
		this.orderFlag = orderFlag;
	}
	public Integer getStockFlag() {
		return stockFlag;
	}
	public void setStockFlag(Integer stockFlag) {
		this.stockFlag = stockFlag;
	}


}