package com.shyl.msc.b2b.order.entity;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.enmu.OrderType;

/**
 * 单据共用属性
 * 
 * 
 * */
@MappedSuperclass 
public class Order extends BasicEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 单号
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
	 * gpoCode(Company)
	 */
	private String gpoCode;
	/**
	 * gpo名称(Company)
	 */
	private String gpoName;
	/**
	 * 供应商编码(Company)
	 */
	private String vendorCode;
	/**
	 * 供应商名称(Company)
	 */
	private String vendorName;
	/**
	 * 医疗机构编码(Hospital)
	 */
	private String hospitalCode;
	/**
	 * 医疗机构名称(Hospital)
	 */
	private String hospitalName;
	/**
	 * 配送点id(Warehouse)
	 */
	private String warehouseCode;
	/**
	 * 配送点名称(Warehouse)
	 */
	private String warehouseName;
	/**
	 * 数量
	 */
	private BigDecimal num;
	/**
	 * 金额
	 */
	private BigDecimal sum;
	/**
	 * 是否过账（0未过账，1过账）
	 */
	private Integer isPass = 0;
	/**
	 * 是否自动生成（0手工，1自动）
	 */
	private Integer isAuto;
	/**
	 * 订单类型
	 */
	private OrderType orderType;
	/**
	 * 报文id
	 */
	private Long datagramId;
	/**
	 * 是否读取
	 */
	private Integer isRead=0;
	
	@Column(length=30,unique = true, nullable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(length=60)
	public String getInternalCode() {
		return internalCode;
	}
	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	@Column(length=20)
	public String getGpoCode() {
		return gpoCode;
	}
	public void setGpoCode(String gpoCode) {
		this.gpoCode = gpoCode;
	}
	@Column(length=200)
	public String getGpoName() {
		return gpoName;
	}
	public void setGpoName(String gpoName) {
		this.gpoName = gpoName;
	}
	@Column(length=20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length=200)
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
	@Column(length=30)
	public String getWarehouseCode() {
		return warehouseCode;
	}
	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}
	@Column(length=100)
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getNum() {
		return num;
	}
	public void setNum(BigDecimal num) {
		this.num = num;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getSum() {
		return sum;
	}
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	public Integer getIsPass() {
		return isPass;
	}
	public void setIsPass(Integer isPass) {
		this.isPass = isPass;
	}
	public Integer getIsAuto() {
		return isAuto;
	}
	public void setIsAuto(Integer isAuto) {
		this.isAuto = isAuto;
	}
	@Transient
	public OrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	public Long getDatagramId() {
		return datagramId;
	}
	public void setDatagramId(Long datagramId) {
		this.datagramId = datagramId;
	}
	public Integer getIsRead() {
		return isRead;
	}
	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}
}