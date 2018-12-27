package com.shyl.msc.dm.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/**
 * 交易商品价格
 * 
 */
@Entity
@Table(name = "t_dm_goods_price")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GoodsPrice extends BasicEntity {
	private static final long serialVersionUID = 4401916733979249605L;
	/**
	 * 商品id
	 */
	private Long goodsId;
	/**
	 * 药品编码
	 */
	private String productCode;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 供应商编码
	 */
	private String vendorCode;
	/**
	 * 中标价
	 */
	private BigDecimal biddingPrice;
	/**
	 * 成交价
	 */
	private BigDecimal finalPrice;
	/**
	 * 生效日期
	 */
	private String effectDate;
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
	private Integer isDisabled = 0;
	/**
	 * 医院禁用供应商(1是，0否)
	 */
	private Integer isDisabledByH = 0;
	/**
	 * 是否选中
	 */
	private Boolean selected;
	/**
	 * 供应商名称
	 */
	private String vendorName;
	/**
	 * 价格显示
	 */
	private String priceShow;

	/**
	 * 价格类型(1指定医院价格，0统一价格)
	 */
	private Integer priceType = 0;
	
	/**
	 * 是否来自合同
	 */
	private Integer isFormContract = 0;

	
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_goods_price_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	@Column(length=100)
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	@Column(length=50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
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
	@Column(length=20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public Integer getIsDisabledByH() {
		return isDisabledByH;
	}
	public void setIsDisabledByH(Integer isDisabledByH) {
		this.isDisabledByH = isDisabledByH;
	}
	@Transient
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	@Transient
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	@Transient
	public String getPriceShow() {
		return priceShow;
	}
	public void setPriceShow(String priceShow) {
		this.priceShow = priceShow;
	}
	public Integer getPriceType() {
		return priceType;
	}
	public void setPriceType(Integer priceType) {
		this.priceType = priceType;
	}
	public String getEffectDate() {
		return effectDate;
	}
	public void setEffectDate(String effectDate) {
		this.effectDate = effectDate;
	}
	public Integer getIsFormContract() {
		return isFormContract;
	}
	public void setIsFormContract(Integer isFormContract) {
		this.isFormContract = isFormContract;
	}

	
}