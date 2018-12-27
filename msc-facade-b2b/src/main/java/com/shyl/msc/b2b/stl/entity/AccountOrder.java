package com.shyl.msc.b2b.stl.entity;

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

/**
 * 订单账务
 * 
 * 
 */
@Entity
@Table(name = "t_stl_account_order")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AccountOrder extends BasicEntity {
	private static final long serialVersionUID = -76076139021650607L;
	
	/**
	 * 年月
	 */
	private String month;
	/**
	 * 供应商id(Company)
	 */
	private String vendorCode;
	/**
	 * 供应商名称(Company)
	 */
	private String vendorName;
	/**
	 * 医疗机构id(Hospital)
	 */
	private String hospitalCode;
	/**
	 * 医疗机构名称(Hospital)
	 */
	private String hospitalName;
	/**
	 * 采购数量
	 */
	private BigDecimal orderNum;
	/**
	 * 采购金额
	 */
	private BigDecimal orderSum;
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
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_stl_account_order_s")
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
	@Column(length=20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length=50)
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
	@Column(length=50)
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getOrderSum() {
		return orderSum;
	}
	public void setOrderSum(BigDecimal orderSum) {
		this.orderSum = orderSum;
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

}