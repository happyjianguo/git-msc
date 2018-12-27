package com.shyl.msc.supervise.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/**
 * 住院诊断
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "sup_clinic_diagnosis")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ClinicDiagnosis extends BasicEntity {
	/** 诊断流水 hospitalCode||internalCode */
	public String code;
	/** 诊断流水号 */
	public String outSno;
	/** 医院编码 */
	public String hospitalCode;
	/** 医院编码*/
	public String hospitalName;
	/** 科室编码 **/
	public String departCode;
	/** 科室名称 **/
	public String departName;
	/** 医生编码 **/
	public String doctorCode;
	/** 医生名称 **/
	public String doctorName;
	/** 病人编号 **/
	public String patientId;
	/** 病人名称 */
	public String patienName;
	/** 诊断日期 **/
	public Date diagDate;
	/** 疾病code**/
	public String diagCode;
	/** 疾病名称**/
	public String diagName;

	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_clinic_diagnosis_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}
	public String getOutSno() {
		return outSno;
	}
	public String getHospitalCode() {
		return hospitalCode;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public String getDepartCode() {
		return departCode;
	}
	public String getDepartName() {
		return departName;
	}
	public String getDoctorCode() {
		return doctorCode;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public String getPatientId() {
		return patientId;
	}
	public String getPatienName() {
		return patienName;
	}
	public Date getDiagDate() {
		return diagDate;
	}
	public String getDiagCode() {
		return diagCode;
	}
	public String getDiagName() {
		return diagName;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setOutSno(String outSno) {
		this.outSno = outSno;
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
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public void setPatienName(String patienName) {
		this.patienName = patienName;
	}
	public void setDiagDate(Date diagDate) {
		this.diagDate = diagDate;
	}
	public void setDiagCode(String diagCode) {
		this.diagCode = diagCode;
	}
	public void setDiagName(String diagName) {
		this.diagName = diagName;
	}

}
