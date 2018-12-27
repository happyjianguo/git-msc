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

/**
 * 医院药品信息表（旧药品，通过导入进来）
 * @author lenovo
 *
 */
@Entity
@Table(name = "t_dm_goods_hospital_source")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GoodsHospitalSource  extends BasicEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 医院id
	 */
	private Long hospitalId;
	/**
	 * 医院代码
	 */
	private String hospitalCode;
	/**
	 * 医院药品内部编码（医院）
	 */
	private String internalCode;
	
	/**
	 * 药品名称
	 */
	private String productName;
	/**
	 * 通用名
	 */
	private String genericName;
	
	/**
	 * 规格
	 */
	private String model; 
	
	/**
	 * 包装
	 */
	private String unitName; 
	
	/**
	 * 最小制剂单位
	 */
	private String minunit; 
	/**
	 * 药品厂家
	 */
	private String producerName; 
	
	/**
	 * 剂型名
	 */
	private String dosageFormName;
	/**
	 * 中标价格
	 */
	private BigDecimal biddingPrice;
	/**
	 * 供应商名称
	 */
	private String vendorName;
	/**
	 * 成交价格
	 */
	private BigDecimal finalPrice;
	/**
	 * 基本药物标志
	 */
	private String  baseMark;

	/**
	 * 药品编码
	 */
	private String  productCode;
	/**
	 * 包装转换比（目前没有太多意义）
	 */
	private BigDecimal convertRatio;
	/**
	 * 包装转换比
	 */
	private BigDecimal convertRatio0;
	/**
	 * 用药监管code
	 */
	private String yyjgcode;
	/**
	 * 医保编码
	 */
	private String ybdrugsNO;
	
	/**
	 * 药交ID
	 */
	private String yjCode;
	
	/**
	 * 物价编码
	 */
	private String priceFileNo;
	/**
	 * 本位吗
	 */
	private String standardCode;
	/**
	 * 是否已经比对 0 未比对， 1自动比对 2 已比对 3审核不通过 4，审核通过
	 */
	private Long status;
	/**
	 * 国药准字
	 */
	private String authorizeNo;
	

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_goods_hospital_source_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(length=100)
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@Column(length=100)
	public String getInternalCode() {
		return internalCode;
	}
	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}
	@Column(length=100)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Column(length=100)
	public String getGenericName() {
		return genericName;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	@Column(length=400)
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@Column(length=50)
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	@Column(length=100)
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	@Column(length=100)
	public String getDosageFormName() {
		return dosageFormName;
	}
	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getBiddingPrice() {
		return biddingPrice;
	}
	public void setBiddingPrice(BigDecimal biddingPrice) {
		this.biddingPrice = biddingPrice;
	}
	@Column(length=100)
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}
	
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}

	public String getBaseMark() {
		return baseMark;
	}
	public void setBaseMark(String baseMark) {
		this.baseMark = baseMark;
	}
	public Long getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Long hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public BigDecimal getConvertRatio() {
		return convertRatio==null?new BigDecimal(1):convertRatio;
	}
	public void setConvertRatio(BigDecimal convertRatio) {
		this.convertRatio = convertRatio==null ? new BigDecimal(1):convertRatio;
	}
	public BigDecimal getConvertRatio0() {
		return convertRatio0;
	}
	@Column(length=50)
	public String getYyjgcode() {
		return yyjgcode;
	}
	@Column(length=50)
	public String getYbdrugsNO() {
		return ybdrugsNO;
	}
	@Column(length=50)
	public String getYjCode() {
		return yjCode;
	}
	@Column(length=50)
	public String getStandardCode() {
		return standardCode;
	}
	@Column(length=50)
	public String getPriceFileNo() {
		return priceFileNo;
	}
	public void setConvertRatio0(BigDecimal convertRatio0) {
		this.convertRatio0 = convertRatio0;
	}
	public void setYyjgcode(String yyjgcode) {
		this.yyjgcode = yyjgcode;
	}
	public void setYbdrugsNO(String ybdrugsNO) {
		this.ybdrugsNO = ybdrugsNO;
	}
	public void setYjCode(String yjCode) {
		this.yjCode = yjCode;
	}
	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}
	public void setPriceFileNo(String priceFileNo) {
		this.priceFileNo = priceFileNo;
	}
	@Column(length=100)
	public String getAuthorizeNo() {
		return authorizeNo;
	}
	public void setAuthorizeNo(String authorizeNo) {
		this.authorizeNo = authorizeNo;
	}
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
	@Column(length=10)
	public String getMinunit() {
		return minunit;
	}
	public void setMinunit(String minunit) {
		this.minunit = minunit;
	}
}
