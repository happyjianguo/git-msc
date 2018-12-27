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
@Table(name = "sup_disease_analysis_item")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DiseaseAnalysisItem extends BasicEntity {

	/**汇总标志 hospitalCode||diseaseCode||yyyymm **/
	private String diseaseAnalysisCode;
	/** 药品数据**/
	private String productCode;
	/** 药品名称 **/
	private String productName;
	/** 包装 **/
	private String packDesc;
	/** 规格 **/
	private String model;
	/** 剂型名称 **/
	private String dosageFormName;
	/** 生产厂家 **/
	private String producerName;
	/** 金额 **/
	private BigDecimal sum;
	/** 数量 **/
	private Integer num;
	/** 数量 **/
	private BigDecimal ddd;
	/** 数量 **/
	private BigDecimal dddc;
	/** 数量 **/
	private BigDecimal ddds;
	

	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_disease_analysis_i_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	@Column(length = 60)
	public String getDiseaseAnalysisCode() {
		return diseaseAnalysisCode;
	}

	@Column(length = 30)
	public String getProductCode() {
		return productCode;
	}

	@Column(length = 100)
	public String getProductName() {
		return productName;
	}

	@Column(length = 300)
	public String getPackDesc() {
		return packDesc;
	}

	@Column(length = 300)
	public String getModel() {
		return model;
	}

	@Column(length = 30)
	public String getDosageFormName() {
		return dosageFormName;
	}

	@Column(length = 30)
	public String getProducerName() {
		return producerName;
	}


	@Column(length = 60)
	public BigDecimal getSum() {
		return sum;
	}


	public Integer getNum() {
		return num;
	}


	public BigDecimal getDdd() {
		return ddd;
	}


	public BigDecimal getDddc() {
		return dddc;
	}


	public BigDecimal getDdds() {
		return ddds;
	}


	public void setDiseaseAnalysisCode(String diseaseAnalysisCode) {
		this.diseaseAnalysisCode = diseaseAnalysisCode;
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}


	public void setModel(String model) {
		this.model = model;
	}


	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}


	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}


	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}


	public void setNum(Integer num) {
		this.num = num;
	}


	public void setDdd(BigDecimal ddd) {
		this.ddd = ddd;
	}


	public void setDddc(BigDecimal dddc) {
		this.dddc = dddc;
	}


	public void setDdds(BigDecimal ddds) {
		this.ddds = ddds;
	}
}
