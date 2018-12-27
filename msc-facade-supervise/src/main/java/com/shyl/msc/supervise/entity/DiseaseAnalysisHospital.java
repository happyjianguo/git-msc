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
import com.shyl.msc.supervise.entity.DrugAnalysis.Type;

@SuppressWarnings("serial")
@Entity
@Table(name = "sup_disease_analysis_hospital")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DiseaseAnalysisHospital extends BasicEntity {

	/**汇总标志 hospitalCode||diseaseCode||yyyymm **/
	public String code;
	/** 月份 **/
	public String month;
	/** 疾病编码 **/
	public String diseaseCode;
	/** 疾病名称 **/
	public String diseaseName;
	/** 医院编码 */
	public String hospitalCode;
	/** 医院编码 */
	public String hospitalName;
	/** 药品总费用 **/
	public BigDecimal sum;
	/**  该疾病诊疗总数 **/
	public Integer treatmentTotal;
	/**  诊疗率（占比） **/
	public BigDecimal treatmentRate;
	/** 品规数 **/
	public Integer isOperation;
	
	public Type type;
	

	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_disease_analysis_h_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	@Column(length = 50, unique = true, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}
	@Column(length = 30)
	public String getMonth() {
		return month;
	}
	@Column(length = 30)
	public String getDiseaseCode() {
		return diseaseCode;
	}
	@Column(length = 80)
	public String getDiseaseName() {
		return diseaseName;
	}

	public Integer getIsOperation() {
		return isOperation;
	}
	@Column(length = 30)
	public String getHospitalCode() {
		return hospitalCode;
	}
	@Column(length = 100)
	public String getHospitalName() {
		return hospitalName;
	}
	public BigDecimal getSum() {
		return sum;
	}
	public Integer getTreatmentTotal() {
		return treatmentTotal;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	public void setTreatmentTotal(Integer treatmentTotal) {
		this.treatmentTotal = treatmentTotal;
	}

	public void setDiseaseCode(String diseaseCode) {
		this.diseaseCode = diseaseCode;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}


	public void setIsOperation(Integer isOperation) {
		this.isOperation = isOperation;
	}
	@Column(length=20,precision=8)
	public BigDecimal getTreatmentRate() {
		return treatmentRate;
	}

	public void setTreatmentRate(BigDecimal treatmentRate) {
		this.treatmentRate = treatmentRate;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
