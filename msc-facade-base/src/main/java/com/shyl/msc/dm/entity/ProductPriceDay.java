package com.shyl.msc.dm.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.enmu.TradeType;

/**
 * 产品每日价格 
 * 
 */
@Entity
@Table(name = "t_dm_product_price_day")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProductPriceDay extends BasicEntity {
	private static final long serialVersionUID = 4401916733979249605L;
	/**
	 *  价格日期
	 */
	private String priceDate;
	/**
	 * 产品编码
	 */
	private String productCode;
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * 供应商编码
	 */
	private String vendorCode;
	/**
	 * 供应商名称
	 */
	private String vendorName;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 中标价
	 */
	private BigDecimal biddingPrice;
	/**
	 * 成交价
	 */
	private BigDecimal finalPrice;
	/**
	 * 起始日期
	 */
	private String beginDate;
	/**
	 * 截止日期
	 */
	private String outDate;
	/**
	 * 是否禁用(1是，0否)
	 */
	private Integer isDisabled;
	/**
	 * 交易类型
	 */
	private TradeType tradeType;
	/**
	 * 价格版次
	 */
	private Long productPriceId;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_product_price_day_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=10)
	public String getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(String priceDate) {
		this.priceDate = priceDate;
	}
	@Column(length=50)
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
	@Column(length=20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length=100)
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	@Column(length=50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column(length=100)
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getBiddingPrice() {
		return biddingPrice;
	}
	public void setBiddingPrice(BigDecimal biddingPrice) {
		this.biddingPrice = biddingPrice;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}
	@Column(length=10)
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	@Column(length=10)
	public String getOutDate() {
		return outDate;
	}
	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}
	public TradeType getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}
	public Long getProductPriceId() {
		return productPriceId;
	}
	public void setProductPriceId(Long productPriceId) {
		this.productPriceId = productPriceId;
	}

	
}