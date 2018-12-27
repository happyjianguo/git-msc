package com.shyl.msc.supervise.entity;

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
 * 实施基本药物制度进展JAVABEAN
 * 
 *
 */
@Entity
@Table(name = "t_hospital_baseDrugProgress")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BaseDrugProgress extends BasicEntity{
	private static final long serialVersionUID = 6806744180560897035L;
	
	public enum HealthStationType {
		healthStation,//村卫生站
		healthServiceCentre,//非政府办乡镇卫生院、社区卫生服务中心
		outpatientDepartment //非政府办门诊部、诊所（医务室）
	}
	/**
	 * 市编码
	 */
	private String cityCode;
	/**
	 * 市名称
	 */
	private String cityName;
	/**
	 * 镇区编码
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
	 * 是否村卫生站0     非政府办乡镇卫生院、社区卫生服务中心1     非政府办门诊部、诊所（医务室）2
	 */
	private HealthStationType healthStationType;
	/**
	 * 基本药物集中采购且采购品规数、金额占比均不低于60%   是(1) 否(0)
	 */
	private Integer isHighSixty;
	/**
	 * 是否已实行药品零差率销售    是(1) 否(0)
	 */
	private Integer isImplementedStation;  
	/**
	 * 是否已实施一般诊疗费收费的卫生站   是(1) 否(0)
	 */
	private Integer isGeneralStation;
	/**
	 * 是否承担30%以上基本公共卫生服务的卫生站  是(1) 否(0)
	 */
	private Integer isThirdHealthStation;
	/**
	 * 已纳入城乡居民医保门诊统筹实施范围的卫生站  是(1) 否(0)
	 */
	private Integer isInHealthInsurance;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_hospital_baseDrugProgress_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	@Column
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
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
	public HealthStationType getHealthStationType() {
		return healthStationType;
	}
	public void setHealthStationType(HealthStationType healthStationType) {
		this.healthStationType = healthStationType;
	}
	@Column
	public Integer getIsHighSixty() {
		return isHighSixty;
	}
	public void setIsHighSixty(Integer isHighSixty) {
		this.isHighSixty = isHighSixty;
	}
	@Column
	public Integer getIsImplementedStation() {
		return isImplementedStation;
	}
	public void setIsImplementedStation(Integer isImplementedStation) {
		this.isImplementedStation = isImplementedStation;
	}
	@Column
	public Integer getIsGeneralStation() {
		return isGeneralStation;
	}
	public void setIsGeneralStation(Integer isGeneralStation) {
		this.isGeneralStation = isGeneralStation;
	}
	@Column
	public Integer getIsThirdHealthStation() {
		return isThirdHealthStation;
	}
	public void setIsThirdHealthStation(Integer isThirdHealthStation) {
		this.isThirdHealthStation = isThirdHealthStation;
	}
	@Column
	public Integer getIsInHealthInsurance() {
		return isInHealthInsurance;
	}
	public void setIsInHealthInsurance(Integer isInHealthInsurance) {
		this.isInHealthInsurance = isInHealthInsurance;
	}
	
}
