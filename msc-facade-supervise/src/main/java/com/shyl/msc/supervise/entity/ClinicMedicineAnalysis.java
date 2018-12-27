package com.shyl.msc.supervise.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
@SuppressWarnings("serial")
@Entity
@Table(name = "sup_clinic_medicine_analysis")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ClinicMedicineAnalysis extends BasicEntity{
	
	public enum ClinicType{
		/** 普通门诊 **/
		ordinary,
		/** 紧急门诊 **/
		urgent; 
	}
	/** 医院编码 */
	private String hospitalCode;
	/** 医院编码 */
	private String hospitalName;
	/** 科室编码 **/
	private String departCode;
	/** 科室名称 **/
	private String departName;
	/** 医生编码 **/
	private String doctorCode;
	/** 医生名称 */
	private String doctorName;
	/** 月份 **/
	private String month;
	/** 医院收入汇总 **/
	private BigDecimal sum;
	/** 药品收入汇总 **/
	private BigDecimal drugSum;
	/** 品规名称 **/
	private String pzm;
	/** 处方单号 **/
	private String rpSno;
	/** 单号**/
	private String outsno;
	/** 门诊类别 **/
	private ClinicType clinicType;
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
	/***唯一字符串*/
	private String uniqueStr;
	
	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "clinic_medicine_analysis_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	@Column(precision = 16, scale = 5)
	public BigDecimal getSum() {
		return sum;
	}
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	@Column(precision = 16, scale = 5)
	public BigDecimal getDrugSum() {
		return drugSum;
	}
	public void setDrugSum(BigDecimal drugSum) {
		this.drugSum = drugSum;
	}
	public String getPzm() {
		return pzm;
	}
	public void setPzm(String pzm) {
		this.pzm = pzm;
	}
	public String getRpSno() {
		return rpSno;
	}
	public void setRpSno(String rpSno) {
		this.rpSno = rpSno;
	}
	public String getOutsno() {
		return outsno;
	}
	public void setOutsno(String outsno) {
		this.outsno = outsno;
	}
	public ClinicType getClinicType() {
		return clinicType;
	}
	public void setClinicType(ClinicType clinicType) {
		this.clinicType = clinicType;
	}
	public Integer getBaseDrugType() {
		return baseDrugType;
	}
	public void setBaseDrugType(Integer baseDrugType) {
		this.baseDrugType = baseDrugType;
	}
	public Integer getAbsDrugType() {
		return absDrugType;
	}
	public void setAbsDrugType(Integer absDrugType) {
		this.absDrugType = absDrugType;
	}
	public Integer getSpecialDrugType() {
		return specialDrugType;
	}
	public void setSpecialDrugType(Integer specialDrugType) {
		this.specialDrugType = specialDrugType;
	}
	public Integer getInsuranceDrugType() {
		return insuranceDrugType;
	}
	public void setInsuranceDrugType(Integer insuranceDrugType) {
		this.insuranceDrugType = insuranceDrugType;
	}
	public Integer getAuxiliaryType() {
		return auxiliaryType;
	}
	public void setAuxiliaryType(Integer auxiliaryType) {
		this.auxiliaryType = auxiliaryType;
	}
	public Integer getYpxz() {
		return ypxz;
	}
	public void setYpxz(Integer ypxz) {
		this.ypxz = ypxz;
	}
	@Column(length=100)
	public String getUniqueStr() {
		return uniqueStr;
	}

	public void setUniqueStr(String uniqueStr) {
		this.uniqueStr = uniqueStr;
	}
	
	
}
