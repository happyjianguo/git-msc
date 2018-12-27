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

@SuppressWarnings("serial")
@Entity
@Table(name = "sup_his_medicine_analysis")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HisMedicineAnalysis extends BasicEntity {
	/** 医院编码 */
	public String hospitalCode;
	/** 医院名称 */
	public String hospitalName;
	/** 科室编码 **/
	public String departCode;
	/** 科室名称 **/
	public String departName;
	/** 医生编码 **/
	public String doctorCode;
	/** 医生名称 */
	public String doctorName;

	/** 月份 **/
	private String month;
	/** 医院收入汇总 **/
	private BigDecimal sum;
	/** 药品收入汇总 **/
	private BigDecimal drugSum;
	/** 非药品收入汇总 **/
	private BigDecimal otherSum;
	/** ddd值累计 **/
	private BigDecimal dddsum;
	/** daysum **/
	private Integer daysum;
	/** 品规名称 **/
	private String pzm;
	/** 出院单号 **/
	private String insno;
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
	@SequenceGenerator(name = "generator", sequenceName = "sup_his_medicine_analysis_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public BigDecimal getSum() {
		return sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public BigDecimal getDrugSum() {
		return drugSum;
	}

	public void setDrugSum(BigDecimal drugSum) {
		this.drugSum = drugSum;
	}

	public BigDecimal getOtherSum() {
		return otherSum;
	}

	public void setOtherSum(BigDecimal otherSum) {
		this.otherSum = otherSum;
	}

	public BigDecimal getDddsum() {
		return dddsum;
	}

	public void setDddsum(BigDecimal dddsum) {
		this.dddsum = dddsum;
	}

	public Integer getDaysum() {
		return daysum;
	}

	public void setDaysum(Integer daysum) {
		this.daysum = daysum;
	}

	public String getPzm() {
		return pzm;
	}

	public void setPzm(String pzm) {
		this.pzm = pzm;
	}

	public String getInsno() {
		return insno;
	}

	public void setInsno(String insno) {
		this.insno = insno;
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

	public String getHospitalCode() {
		return hospitalCode;
	}

	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	@Column(length = 100)
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

	@Column(length = 100)
	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getDoctorCode() {
		return doctorCode;
	}

	public void setDoctorCode(String doctorCode) {
		this.doctorCode = doctorCode;
	}

	@Column(length = 100)
	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	@Column(length=100)
	public String getUniqueStr() {
		return uniqueStr;
	}

	public void setUniqueStr(String uniqueStr) {
		this.uniqueStr = uniqueStr;
	}
	

}
