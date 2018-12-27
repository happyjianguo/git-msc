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
 * 门诊处方
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "sup_clinic_reg")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ClinicReg extends BasicEntity {
	/** 门诊类别 */
	private enum ClinicType {
		/** 普通门诊 **/
		ordinary,
		/** 紧急门诊 **/
		urgent;
	}

	/** 处方编号hospitalCode||outSno */
	public String code;
	/** 门诊流水号 */
	public String outSno;
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
	/** 医生名称 **/
	public String doctorName;
	/** 病人编号 **/
	public String patientId;
	/** 病人名称 */
	public String patienName;
	/** 处方日期 **/
	public Date cdate;
	/** 门诊类别 **/
	public ClinicType ClinicType;
	/** 总费用 **/
	public BigDecimal sum;
	/** 药品费用 **/
	public BigDecimal drugSum;
	/** 医疗费用 **/
	public BigDecimal otherSum;
	/** 年龄 **/
	private String age;
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
	@SequenceGenerator(name = "generator", sequenceName = "sup_clinic_reg_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 50, unique = true, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}

	@Column(length = 50)
	public String getOutSno() {
		return outSno;
	}

	@Column(length = 30)
	public String getHospitalCode() {
		return hospitalCode;
	}

	@Column(length = 30)
	public String getPatientId() {
		return patientId;
	}

	@Column(length = 20)
	public String getPatienName() {
		return patienName;
	}

	public Date getCdate() {
		return cdate;
	}

	@Column(length = 60)
	public String getDepartCode() {
		return departCode;
	}

	@Column(length = 60)
	public String getDepartName() {
		return departName;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getSum() {
		return sum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getDrugSum() {
		return drugSum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getOtherSum() {
		return otherSum;
	}


	public ClinicType getClinicType() {
		return ClinicType;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public String getDoctorCode() {
		return doctorCode;
	}

	public String getDoctorName() {
		return doctorName;
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

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public void setPatienName(String patienName) {
		this.patienName = patienName;
	}

	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}

	public void setDepartCode(String departCode) {
		this.departCode = departCode;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public void setDrugSum(BigDecimal drugSum) {
		this.drugSum = drugSum;
	}
	public void setOtherSum(BigDecimal otherSum) {
		this.otherSum = otherSum;
	}
	public void setClinicType(ClinicType clinicType) {
		ClinicType = clinicType;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public void setDoctorCode(String doctorCode) {
		this.doctorCode = doctorCode;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getAge() {
		return age;
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

	public void setAge(String age) {
		this.age = age;
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


}
