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
 * 医院区域表
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "sup_hospital_zone")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HospitalZone extends BasicEntity {
	/** 省编码 */
	public String provinceCode;
	/** 省名称 */
	public String provinceName;
	/** 市级编码 */
	public String cityCode;
	/** 市级编码 */
	public String cityName;
	/** 县区编码 */
	public String countyCode;
	/** 县区编码 */
	public String countyName;
	/** 模糊匹配 */
	public String treePath;
	/** 医院编码 */
	public String hospitalCode;
	/** 医院编码 */
	public String hospitalName;
	/** 医院等级 **/
	private String orgLevel;
	
	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_hospital_zone_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}


	public String getProvinceCode() {
		return provinceCode;
	}


	public String getProvinceName() {
		return provinceName;
	}


	public String getCityCode() {
		return cityCode;
	}


	public String getCityName() {
		return cityName;
	}


	public String getCountyCode() {
		return countyCode;
	}


	public String getCountyName() {
		return countyName;
	}


	public String getTreePath() {
		return treePath;
	}


	public String getHospitalCode() {
		return hospitalCode;
	}


	public String getHospitalName() {
		return hospitalName;
	}


	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}


	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}


	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}


	public void setCityName(String cityName) {
		this.cityName = cityName;
	}


	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}


	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}


	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}


	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}


	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	@Column(length=30)
	public String getOrgLevel() {
		return orgLevel;
	}


	public void setOrgLevel(String orgLevel) {
		this.orgLevel = orgLevel;
	}
}
