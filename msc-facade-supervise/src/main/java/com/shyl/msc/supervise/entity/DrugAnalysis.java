package com.shyl.msc.supervise.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import com.shyl.common.entity.BasicEntity;

@MappedSuperclass 
@SuppressWarnings("serial")
public class DrugAnalysis  extends BasicEntity {
	public enum Type{
		clinic,
		his
	}
	//汇总类型
	private Type type; 
	/**汇总标志  **/
	private String code;
	/** 月份 **/
	private String month;
	/** 药品编号 **/
	private String productCode;
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
	/** 金额 **/
	private BigDecimal sum;
	/** 数量 **/
	private Integer num;
	/** 基本药物类型 **/
	private Integer baseDrugType;
	/** 抗菌药物类型 **/
	private Integer absDrugType;
	/** 特殊药品类型 **/
	private Integer specialDrugType;
	/** 医保药品类型 **/
	private Integer insuranceDrugType;
	/** 辅助药品类型 **/
	private Integer auxiliaryType;
	/** 药品新增 **/
	private Integer ypxz;
	/** ddd值  **/
	private BigDecimal ddd;
	/** ddds值 (药品数量/ddd) **/
	private BigDecimal ddds;
	/** dddc值(药品费用/ddds)  **/
	private BigDecimal dddc;
	

	@Column(length = 50, unique = true, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}
	@Column(length = 10)
	public String getMonth() {
		return month;
	}
	@Column(length = 60)
	public String getProductCode() {
		return productCode;
	}
	public BigDecimal getSum() {
		return sum;
	}
	public Integer getNum() {
		return num;
	}
	@Column(precision = 16, scale = 5)
	public BigDecimal getDdd() {
		return ddd;
	}
	@Column(precision = 16, scale = 5)
	public BigDecimal getDdds() {
		return ddds;
	}
	@Column(precision = 16, scale = 5)
	public BigDecimal getDddc() {
		return dddc;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setMonth(String month) {
		this.month = month;
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
	public void setDdd(BigDecimal ddd) {
		this.ddd = ddd;
	}
	public void setDdds(BigDecimal ddds) {
		this.ddds = ddds;
	}
	public void setDddc(BigDecimal dddc) {
		this.dddc = dddc;
	}
	@Column(length = 100)
	public String getProductName() {
		return productName;
	}
	@Column(length = 30)
	public String getPackDesc() {
		return packDesc;
	}
	@Column(length = 300)
	public String getModel() {
		return model;
	}
	@Column(length = 30)
	public String getDosageFormName() {
		return dosageFormName;
	}
	@Column(length = 300)
	public String getProducerName() {
		return producerName;
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

	public Integer getBaseDrugType() {
		return baseDrugType;
	}

	public Integer getAbsDrugType() {
		return absDrugType;
	}

	public Integer getSpecialDrugType() {
		return specialDrugType;
	}

	public Integer getInsuranceDrugType() {
		return insuranceDrugType;
	}

	public void setBaseDrugType(Integer baseDrugType) {
		this.baseDrugType = baseDrugType;
	}

	public void setAbsDrugType(Integer absDrugType) {
		this.absDrugType = absDrugType;
	}

	public void setSpecialDrugType(Integer specialDrugType) {
		this.specialDrugType = specialDrugType;
	}

	public void setInsuranceDrugType(Integer insuranceDrugType) {
		this.insuranceDrugType = insuranceDrugType;
	}

	public Integer getAuxiliaryType() {
		return auxiliaryType;
	}

	public Integer getYpxz() {
		return ypxz;
	}

	public void setAuxiliaryType(Integer auxiliaryType) {
		this.auxiliaryType = auxiliaryType;
	}

	public void setYpxz(Integer ypxz) {
		this.ypxz = ypxz;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
