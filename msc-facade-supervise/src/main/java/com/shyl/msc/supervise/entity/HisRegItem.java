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

/**
 * 出院药品流水
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "sup_his_reg_item")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HisRegItem extends BasicEntity {
	/**
	 * 医院药品内部编码（医院）
	 */
	private String internalCode;
	/** 处方明细流水号(医院编号||处方明细流水号) **/
	public String code;
	/* 医院编码 */
	public String hospitalCode;
	/** 支付流水号 **/
	public String paySno;
	/** 门诊流水号 **/
	public String inSno;
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
	/** 中西药分类(Attribute) **/
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
	@SequenceGenerator(name = "generator", sequenceName = "sup_his_reg_item_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	@Column(length = 50, unique = true, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}

	public String getBaseDrugType() {
		return baseDrugType;
	}

	public void setBaseDrugType(String baseDrugType) {
		this.baseDrugType = baseDrugType;
	}

	public String getAbsDrugType() {
		return absDrugType;
	}

	public void setAbsDrugType(String absDrugType) {
		this.absDrugType = absDrugType;
	}

	public String getSpecialDrugType() {
		return specialDrugType;
	}

	public void setSpecialDrugType(String specialDrugType) {
		this.specialDrugType = specialDrugType;
	}

	public String getInsuranceDrugType() {
		return insuranceDrugType;
	}

	public void setInsuranceDrugType(String insuranceDrugType) {
		this.insuranceDrugType = insuranceDrugType;
	}

	public String getAuxiliaryType() {
		return auxiliaryType;
	}

	public void setAuxiliaryType(String auxiliaryType) {
		this.auxiliaryType = auxiliaryType;
	}

	public String getYpxz() {
		return ypxz;
	}

	public void setYpxz(String ypxz) {
		this.ypxz = ypxz;
	}

	public String getPaySno() {
		return paySno;
	}

	public String getInSno() {
		return inSno;
	}

	public Date getPayDate() {
		return payDate;
	}

	public String getProductCode() {
		return productCode;
	}

	public BigDecimal getSum() {
		return sum;
	}

	public Integer getNum() {
		return num;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setPaySno(String paySno) {
		this.paySno = paySno;
	}

	public void setInSno(String inSno) {
		this.inSno = inSno;
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

	public String getInternalCode() {
		return internalCode;
	}

	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}

	public Long getDrugType() {
		return drugType;
	}

	public void setDrugType(Long drugType) {
		this.drugType = drugType;
	}

}
