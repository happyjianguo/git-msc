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
@Table(name = "sup_his_analysis_hospital")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HisAnalysisHospital extends HisAnalysis {
	/** 医院编码 */
	public String hospitalCode;
	/** 医院编码 */
	public String hospitalName;
	
	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_his_analysis_h_s")
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


	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

}
