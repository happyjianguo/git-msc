package com.shyl.msc.supervise.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "sup_clinic_recipe_item")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ClinicRecipeItem extends BasicEntity {
	/**
	 * 医院药品内部编码（医院）
	 */
	private String internalCode;
	/** 处方明细流水号(医院编号||处方明细流水号) **/
	public String code;
	/** 医院编码 */
	public String hospitalCode;

	/** 支付流水号 **/
	public String paySno;
	/** 门诊流水号 **/
	public String outSno;
	/** 处方流水号 **/
	public String rpSno;
	/** 支付日期 **/
	public Date payDate;
	/** 药品编号 **/
	public String productCode;
	/** 药品名称 **/
	private String productName;
	/** 包装 **/
	private String packDesc;
	/** 规格 **/
	private String model;
	/** 剂型名称 **/
	private String dosageFormName;
	/** 生产厂家 **/
	private String producerName;
	/** ddd值 **/
	private BigDecimal ddd;
	/** 金额 **/
	public BigDecimal sum;
	/** 数量 **/
	public Integer num;
	/** 频率编码 **/
	public String frequencyCode;
	/** 频率名称 **/
	public String frequencyName;
	/** 通途编码 **/
	public String drugUsegeCode;
	/** 是否注射剂 **/
	public Long isInjection;
	/** 是否静脉注射 **/
	public Long isIntraInjection;
	/** 一次剂量 **/
	public String dosaOne;
	/** 一次剂量单位 **/
	public String dosaOneUnit;
	/** 天数 **/
	public Integer days;
	/**
	 * 中西药分类(Attribute)
	 */
	private Long drugType;
	/** 基本药物类型 **/
	private String baseDrugType;
	/** 抗菌药物类型 **/
	private String absDrugType;
	/** 特殊药品类型 **/
	private String specialDrugType;
	/** 医保药品类型 **/
	private String insuranceDrugType;
	/** 辅助药品类型 **/
	private String auxiliaryType;
	/** 药品新增 **/
	private String ypxz;

	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_clinic_recipe_item_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	@Column(length = 50, unique = true, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}

	@Column(length = 50)
	public String getPaySno() {
		return paySno;
	}

	@Column(length = 50)
	public String getOutSno() {
		return outSno;
	}

	public Date getPayDate() {
		return payDate;
	}

	@Column(length = 30)
	public String getProductCode() {
		return productCode;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getSum() {
		return sum;
	}

	public Integer getNum() {
		return num;
	}

	@Column(length = 30)
	public String getFrequencyCode() {
		return frequencyCode;
	}

	@Column(length = 60)
	public String getFrequencyName() {
		return frequencyName;
	}

	@Column(length = 30)
	public String getDrugUsegeCode() {
		return drugUsegeCode;
	}

	public Long getIsInjection() {
		return isInjection;
	}

	public Long getIsIntraInjection() {
		return isIntraInjection;
	}

	@Column(length = 30)
	public String getDosaOne() {
		return dosaOne;
	}

	@Column(length = 30)
	public String getDosaOneUnit() {
		return dosaOneUnit;
	}

	public Integer getDays() {
		return days;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setPaySno(String paySno) {
		this.paySno = paySno;
	}

	public void setOutSno(String outSno) {
		this.outSno = outSno;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public void setFrequencyCode(String frequencyCode) {
		this.frequencyCode = frequencyCode;
	}

	public void setFrequencyName(String frequencyName) {
		this.frequencyName = frequencyName;
	}

	public void setDrugUsegeCode(String drugUsegeCode) {
		this.drugUsegeCode = drugUsegeCode;
	}

	public void setIsInjection(Long isInjection) {
		this.isInjection = isInjection;
	}

	public void setIsIntraInjection(Long isIntraInjection) {
		this.isIntraInjection = isIntraInjection;
	}

	public void setDosaOne(String dosaOne) {
		this.dosaOne = dosaOne;
	}

	public void setDosaOneUnit(String dosaOneUnit) {
		this.dosaOneUnit = dosaOneUnit;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getProductName() {
		return productName;
	}

	public String getPackDesc() {
		return packDesc;
	}

	public String getModel() {
		return model;
	}

	public String getDosageFormName() {
		return dosageFormName;
	}

	public String getProducerName() {
		return producerName;
	}

	public BigDecimal getDdd() {
		return ddd;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}

	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}

	public void setDdd(BigDecimal ddd) {
		this.ddd = ddd;
	}

	public String getHospitalCode() {
		return hospitalCode;
	}

	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}

	@Column(length = 60)
	public String getRpSno() {
		return rpSno;
	}

	public void setRpSno(String rpSno) {
		this.rpSno = rpSno;
	}


	public String getInternalCode() {
		return internalCode;
	}

	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}

	public void setBaseDrugType(String baseDrugType) {
		this.baseDrugType = baseDrugType;
	}

	public void setAbsDrugType(String absDrugType) {
		this.absDrugType = absDrugType;
	}

	public void setSpecialDrugType(String specialDrugType) {
		this.specialDrugType = specialDrugType;
	}

	public void setInsuranceDrugType(String insuranceDrugType) {
		this.insuranceDrugType = insuranceDrugType;
	}

	public void setAuxiliaryType(String auxiliaryType) {
		this.auxiliaryType = auxiliaryType;
	}

	public void setYpxz(String ypxz) {
		this.ypxz = ypxz;
	}

	public String getBaseDrugType() {
		return baseDrugType;
	}

	public String getAbsDrugType() {
		return absDrugType;
	}

	public String getSpecialDrugType() {
		return specialDrugType;
	}

	public String getInsuranceDrugType() {
		return insuranceDrugType;
	}

	public String getAuxiliaryType() {
		return auxiliaryType;
	}

	public String getYpxz() {
		return ypxz;
	}

	public Long getDrugType() {
		return drugType;
	}

	public void setDrugType(Long drugType) {
		this.drugType = drugType;
	}

}
