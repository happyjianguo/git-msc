package com.shyl.msc.supervise.entity;

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
 * 药品映射关系 
 * 
 */
@Entity
@Table(name = "sup_medicine_hospital")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MedicineHospital extends BasicEntity {
	private static final long serialVersionUID = 4401916733979249605L;
	
	/**
	 * 医院药品内部编码（医院）
	 */
	private String internalCode;
	/**
	 * 产品
	 */
	private Long productId;
	/**
	 * 药品编码
	 */
	private String productCode;
	/**
	 * 药品名称
	 */
	private String productName;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 通用名
	 */
	private String genericName;
	/**
	 * 剂型名
	 */
	private String dosageFormName;
	/**
	 * 规格
	 */
	private String model;
	/**
	 * 单位名称
	 */
	private String unitName;
	/**
	 * 包装规格
	 */
	private String packDesc;
	/**
	 * 生产企业名称
	 */
	private String producerName;
	/**
	 * 转换比
	 */
	private BigDecimal convertRatio;
	/** 
	 * 是否辅助药品 
	 **/
	private Integer auxiliaryType;
	/** 
	 * 药品信息
	 * **/
	private Medicine medicine;

	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="sup_medicine_hospital_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(nullable=false)
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	@Column(length=100)
	public String getGenericName() {
		return genericName;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	@Column(length=50)
	public String getDosageFormName() {
		return dosageFormName;
	}
	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}
	@Column(length=300)
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
	@Column(length=50)
	public String getPackDesc() {
		return packDesc;
	}
	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}
	@Column(length=300)
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	public String getInternalCode() {
		return internalCode;
	}
	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getConvertRatio() {
		return convertRatio;
	}
	public void setConvertRatio(BigDecimal convertRatio) {
		this.convertRatio = convertRatio;
	}
	public Integer getAuxiliaryType() {
		return auxiliaryType;
	}
	public void setAuxiliaryType(Integer auxiliaryType) {
		this.auxiliaryType = auxiliaryType;
	}
	@Transient
	public Medicine getMedicine() {
		return medicine;
	}
	public void setMedicine(Medicine medicine) {
		this.medicine = medicine;
	}
	
}