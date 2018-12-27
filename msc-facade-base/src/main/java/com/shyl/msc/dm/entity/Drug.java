package com.shyl.msc.dm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/** 药品 */
@Entity
@Table(name = "t_dm_drug")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Drug extends BasicEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 编码
	 */
	private String code;
	/**
	 * 通 用 名
	 */
	private String genericName;
	/**
	 * 英文名称
	 */
	private String englishName;
	/**
	 * 拼音简称
	 */
	private String pinyin;
	/**
	 * 剂型id
	 */
	private Long dosageFormId;
	/**
	 * 剂型名称
	 */
	private String dosageFormName;
	/**
	 * 药品性状
	 */
	private String style;
	/**
	 * 处方药(Attribute)
	 */
	private String prescription;
	/**
	 * 备用标识(Attribute)
	 */
	private String backTag;
	/**
	 * 中西药分类(Attribute)
	 */
	private Long drugType;
	/**
	 * 抗菌药物
	 */
	private Long absDrugType;
	/**
	 * 特殊药品分类
	 */
	private Long specialDrugType;
	/**
	 * 新编药物学分类
	 */
	private Long newlyDrugType;
	/**
	 * 药理分类
	 */
	private Long pharmacologyType;
	/**
	 * 质量标准编号
	 */
	private String qsno;
	/**
	 * 质量标准类型(Attribute)
	 */
	private String qualityType;
	/**
	 * 归档文件序号
	 */
	private String archNo;
	/**
	 * 备 注
	 */
	private String notes;	
	/** 
	 * 禁用标识
	 */
	private Integer isDisabled = 0;
	/**
	 * 通用编码
	 */
	private String genericCode;
	/**
	 * 耗材分类
	 */
	private Long suppliesType;
	
	/** 主键*/
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_drug_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=50,unique = true, nullable = false,updatable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(length=100,nullable = false)
	public String getGenericName() {
		return genericName;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	@Column(length=50)
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	@Column(length=50)
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public Long getDosageFormId() {
		return dosageFormId;
	}
	public void setDosageFormId(Long dosageFormId) {
		this.dosageFormId = dosageFormId;
	}
	@Column(length=50)
	public String getDosageFormName() {
		return dosageFormName;
	}
	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}
	@Column(length=20)
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	@Column(length=20)
	public String getPrescription() {
		return prescription;
	}
	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}
	@Column(length=20)
	public String getBackTag() {
		return backTag;
	}
	public void setBackTag(String backTag) {
		this.backTag = backTag;
	}
	public Long getDrugType() {
		return drugType;
	}
	public void setDrugType(Long drugType) {
		this.drugType = drugType;
	}
	public Long getAbsDrugType() {
		return absDrugType;
	}
	public void setAbsDrugType(Long absDrugType) {
		this.absDrugType = absDrugType;
	}
	public Long getSpecialDrugType() {
		return specialDrugType;
	}
	public void setSpecialDrugType(Long specialDrugType) {
		this.specialDrugType = specialDrugType;
	}
	public Long getNewlyDrugType() {
		return newlyDrugType;
	}
	public void setNewlyDrugType(Long newlyDrugType) {
		this.newlyDrugType = newlyDrugType;
	}
	public Long getPharmacologyType() {
		return pharmacologyType;
	}
	public void setPharmacologyType(Long pharmacologyType) {
		this.pharmacologyType = pharmacologyType;
	}
	@Column(length=20)
	public String getQsno() {
		return qsno;
	}
	public void setQsno(String qsno) {
		this.qsno = qsno;
	}
	@Column(length=20)
	public String getQualityType() {
		return qualityType;
	}
	public void setQualityType(String qualityType) {
		this.qualityType = qualityType;
	}
	@Column(length=20)
	public String getArchNo() {
		return archNo;
	}
	public void setArchNo(String archNo) {
		this.archNo = archNo;
	}
	@Column(length=100)
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}

	public String getGenericCode() {
		return genericCode;
	}
	public void setGenericCode(String genericCode) {
		this.genericCode = genericCode;
	}
	public Long getSuppliesType() {
		return suppliesType;
	}
	public void setSuppliesType(Long suppliesType) {
		this.suppliesType = suppliesType;
	}
	
}