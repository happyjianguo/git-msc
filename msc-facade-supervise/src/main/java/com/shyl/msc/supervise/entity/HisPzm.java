package com.shyl.msc.supervise.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.shyl.common.entity.BasicEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "sup_his_pzm")
public class HisPzm extends BasicEntity {
	
	private String inSno;
	
	private String pzm;
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
	/** 出院日期 **/
	public Date cdate;

	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_his_pzm_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPzm() {
		return pzm;
	}
	
	
	public String getInSno() {
		return inSno;
	}

	public void setInSno(String inSno) {
		this.inSno = inSno;
	}

	public void setPzm(String pzm) {
		this.pzm = pzm;
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

	public String getHospitalCode() {
		return hospitalCode;
	}

	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getDepartCode() {
		return departCode;
	}

	public void setDepartCode(String departCode) {
		this.departCode = departCode;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getDoctorCode() {
		return doctorCode;
	}

	public void setDoctorCode(String doctorCode) {
		this.doctorCode = doctorCode;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatienName() {
		return patienName;
	}

	public void setPatienName(String patienName) {
		this.patienName = patienName;
	}

	public Date getCdate() {
		return cdate;
	}

	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}
	
}
