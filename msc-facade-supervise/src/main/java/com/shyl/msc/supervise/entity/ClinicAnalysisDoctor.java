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
@Table(name = "sup_clinic_analysis_doctor")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ClinicAnalysisDoctor extends ClinicAnalysis {

	/** 医院编码 */
	public String hospitalCode;
	/** 医院编码 */
	public String hospitalName;
	/** 科室编码 **/
	public String departCode;
	/** 科室名称 **/
	public String departName;
	/** 医生编码 **/
	public String doctorCode;
	/** 医生名称 */
	public String doctorName;

	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_clinic_analysis_dc_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(length = 50)
	public String getHospitalCode() {
		return hospitalCode;
	}

	@Column(length = 50)
	public String getHospitalName() {
		return hospitalName;
	}

	@Column(length = 50)
	public String getDepartCode() {
		return departCode;
	}

	@Column(length = 100)
	public String getDepartName() {
		return departName;
	}

	@Column(length = 30)
	public String getDoctorCode() {
		return doctorCode;
	}

	@Column(length = 30)
	public String getDoctorName() {
		return doctorName;
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

	public void setDoctorCode(String doctorCode) {
		this.doctorCode = doctorCode;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

}
