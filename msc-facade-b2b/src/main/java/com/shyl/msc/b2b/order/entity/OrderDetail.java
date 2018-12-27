package com.shyl.msc.b2b.order.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.enmu.OrderType;

/**
 * 单据细项共用属性
 * 
 * 
 * */
@MappedSuperclass 
public class OrderDetail extends BasicEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 编号
	 */
	private String code;
	/**
	 * 内部单号
	 */
	private String internalCode;
	/**
	 * 日期
	 */
	private Date orderDate;
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
	/**
	 * 是否过账（0未过账，1过账）
	 */
	private Integer isPass = 0;
	/**
	 * 合同明细id
	 */
	private String contractDetailCode;
	/**
	 * 订单类型
	 */
	private OrderType orderType;
	
	
	@Column(length=30,unique = true, nullable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(length=30)
	public String getInternalCode() {
		return internalCode;
	}
	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}
	@Column(nullable = false) 
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
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
	public Integer getIsPass() {
		return isPass;
	}
	public void setIsPass(Integer isPass) {
		this.isPass = isPass;
	}
	@Column(length=30)
	public String getContractDetailCode() {
		return contractDetailCode;
	}
	public void setContractDetailCode(String contractDetailCode) {
		this.contractDetailCode = contractDetailCode;
	}
	@Transient
	public OrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

}