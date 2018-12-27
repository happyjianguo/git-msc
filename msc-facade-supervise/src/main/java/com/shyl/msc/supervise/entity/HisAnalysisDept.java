package com.shyl.msc.supervise.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@Table(name = "sup_his_analysis_dept")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HisAnalysisDept extends HisAnalysis {

	/** 医院编码 */
	public String hospitalCode;
	/** 医院编码 */
	public String hospitalName;
	/** 科室编码 **/
	public String departCode;
	/** 科室名称 **/
	public String departName;


	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_his_analysis_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(length = 30)
	public String getHospitalCode() {
		return hospitalCode;
	}

	@Column(length = 100)
	public String getHospitalName() {
		return hospitalName;
	}

	@Column(length = 50)
	public String getDepartCode() {
		return departCode;
	}

	@Column(length = 50)
	public String getDepartName() {
		return departName;
	}

	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public void setDepartCode(String departCode) {
		this.departCode = departCode;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

}
