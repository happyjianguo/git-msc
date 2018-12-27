package com.shyl.msc.supervise.entity;

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
 * 医院基本药物配备使用比例
 * 
 *
 */
@Entity
@Table(name = "t_hospital_baseDrugProvide")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BaseDrugProvide extends BasicEntity{
	private static final long serialVersionUID = -3804747186845138987L;
	/**
	 * 镇区编号
	 */
	private String countyCode;
	/**
	 * 镇区名称
	 */
	private String countyName;
	/**
	 * 年月
	 */
	private String month;
	/**
	 * 医疗机构编码
	 */
	private String hospitalCode;
	/**
	 * 医疗机构名称
	 */
	private String hospitalName;
	/**
	 * 医疗机构级别
	 */
	private String orgLevel;
	/**
	 * 医疗机构类别
	 */
	private Integer orgType;
	/**
	 * 是否县级公立医院改革试点医院
	 * 是1   否0
	 */
	private Integer isReformHospital;
	/**
	 * 基本药物品规数
	 *
	 */
	private Integer baseDrugTotal;
	/**
	 * 全部药物品规数
	 *
	 */
	private Integer drugTotal;
	/**
	 * 基本药物销售金额
	 *
	 */
	private BigDecimal baseDrugTrade;
	/**
	 * 全部药物销售金额
	 *
	 */
	private BigDecimal drugTrade;
	
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_hospital_baseDrugProvide_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column
	public String getCountyCode() {
		return countyCode;
	}
	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}
	@Column
	public String getCountyName() {
		return countyName;
	}
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	@Column
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	@Column
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@Column
	public String getOrgLevel() {
		return orgLevel;
	}
	public void setOrgLevel(String orgLevel) {
		this.orgLevel = orgLevel;
	}
	@Column
	public Integer getOrgType() {
		return orgType;
	}
	public void setOrgType(Integer orgType) {
		this.orgType = orgType;
	}
	@Column
	public Integer getIsReformHospital() {
		return isReformHospital;
	}
	public void setIsReformHospital(Integer isReformHospital) {
		this.isReformHospital = isReformHospital;
	}
	public Integer getBaseDrugTotal() {
		return baseDrugTotal;
	}
	public void setBaseDrugTotal(Integer baseDrugTotal) {
		this.baseDrugTotal = baseDrugTotal;
	}
	public Integer getDrugTotal() {
		return drugTotal;
	}
	public void setDrugTotal(Integer drugTotal) {
		this.drugTotal = drugTotal;
	}
	public BigDecimal getBaseDrugTrade() {
		return baseDrugTrade;
	}
	public void setBaseDrugTrade(BigDecimal baseDrugTrade) {
		this.baseDrugTrade = baseDrugTrade;
	}
	public BigDecimal getDrugTrade() {
		return drugTrade;
	}
	public void setDrugTrade(BigDecimal drugTrade) {
		this.drugTrade = drugTrade;
	}	
}
