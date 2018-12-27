package com.shyl.msc.dm.entity;

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

/** 产品 */
@Entity
@Table(name = "t_dm_product")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Product extends BasicEntity {
	private static final long serialVersionUID = 2530287410912170038L;

	/**
	 * 编码
	 */
	private String code;
	/**
	 * 类别(Attribute)
	 */
	private String type;
	/**
	 * 遴选药品目录id
	 */
	private Long directoryId;
	/**
	 * 药品目录id
	 */
	private Long drugId;
	/**
	 * 产品名称
	 */
	private String name;
	/**
	 * 商品名
	 */
	private String tradeName;
	/**
	 * 拼音简称
	 */
	private String pinyin;
	/**
	 * 本位码
	 */
	private String standardCode;
	/**
	 * 国家药品代码
	 */
	private String nationalCode;
	/**
	 * 产品通用编码
	 */
	private String productGCode;

	/**
	 * 生产企业(company).
	 */
	private Long producerId;
	/**
	 * 生产企业名称
	 */
	private String producerName;
	/**
	 * 规格
	 */
	private String model;
	/**
	 * 剂量
	 */
	private BigDecimal dose;
	/**
	 * 剂量单位(Attribute)
	 */
	private String doseUnit;
	/**
	 * 最大ddd数(按最小计量单位来算，一天不能超过多少片)
	 */

	private BigDecimal doseDdd;

	/**
	 * 包装材质(Attribute)
	 */
	private String packageMaterial;
	/**
	 * 单位转换比
	 */
	private Integer convertRatio;
	/**
	 * 单位(Attribute)
	 */
	private String unit;
	/**
	 * 单位名称
	 */
	private String unitName;
	/**
	 * 最小制剂单位(Attribute)
	 */
	private String minunit;
	/**
	 * 定价类型(Attribute)
	 */
	private String priceType;
	/**
	 * 零售价
	 */
	private BigDecimal finalPrice;
	/**
	 * 物价批文号
	 */
	private String priceFileNo;
	/**
	 * 基本药物类型
	 */
	private Long baseDrugType;
	/**
	 * 基本药物类型名称
	 */
	private String baseDrugTypeName;
	/**
	 * 医保药品类型
	 */
	private Long insuranceDrugType;
	/**
	 * 医保药品类型名称
	 */
	private String insuranceDrugTypeName;
	/**
	 * 国家医保序号
	 */
	private String ybdrugsNO;
	/**
	 * 产品来源(Attribute)
	 */
	private String productSource;
	/**
	 * 原料来源(Attribute)
	 */
	private String materialSource;
	/**
	 * 批准文号
	 */
	private String authorizeNo;
	/**
	 * 国药准字
	 */
	private String nationalAuthorizeNo;
	/**
	 * 批准文号有效起始日期
	 */
	private Date authorizeBeginDate;
	/**
	 * 批准文号有效截止日期
	 */
	private Date authorizeOutDate;
	/**
	 * 进口药品注册证号
	 */
	private String importFileNo;
	/**
	 * 进口药品注册证有效起始日期
	 */
	private Date importBeginDate;
	/**
	 * 进口药品注册证有效截止日期
	 */
	private Date importOutDate;
	/**
	 * 质量层次(Attribute)
	 */
	private String qualityLevel;
	/**
	 * 是否有GMP证书 flags第2位
	 */
	private Integer gMPFlag;
	/**
	 * 优质优价中成药 flags第3位
	 */
	private Integer hqgpcmmFlag;
	/**
	 * 中医院集诊必备中成药 flags第4位
	 */
	private Integer emergencyFlag;
	/**
	 * 中药保护品种 flags第5位
	 */
	private Integer protectFlag;
	/**
	 * 新药 flags第6位
	 */
	private Integer newDrugFlag;
	/**
	 * 委托加工 flags第7位
	 */
	private Integer consignFlag;
	/**
	 * 专利类型(Attribute)
	 */
	private String patentType;
	/**
	 * 专利有效起始日期
	 */
	private Date patentBeginDate;
	/**
	 * 专利有效截止日期
	 */
	private Date patentOutDate;
	/**
	 * 中药保护品种有效起始日
	 */
	private Date protectBeginDate;
	/**
	 * 中药保护品种有效期
	 */
	private Date protectOutDate;
	/**
	 * 新药有效起始日
	 */
	private Date newDrugBeginDate;
	/**
	 * 新药有效结束期
	 */
	private Date newDrugOutDate;
	/**
	 * 委托加工有效起始日期
	 */
	private Date consignBeginDate;
	/**
	 * 委托加工有效截止日期
	 */
	private Date consignOutDate;
	/**
	 * 委托加工企业名称(company)
	 */
	private String consigncoName;
	/**
	 * 委托加工企业编码(company)
	 */
	private String consigncoCode;
	/**
	 * 一级总代理名称(company)
	 */
	private String level1AgentName;
	/**
	 * 一级总代理编码(company)
	 */
	private String level1AgentCode;
	/**
	 * 归档文件序列号
	 */
	private String archFileNo;
	/**
	 * 包装规格
	 */
	private String packDesc;
	/**
	 * 限定日剂量（DDD）
	 */
	private String ddd;
	/**
	 * 备 注
	 */
	private String notes;
	/**
	 * 维护SFDA基础数据库对应的链接（URL）
	 */
	private String url;
	/**
	 * 是否GPO采购
	 */
	private Integer isGPOPurchase;
	/**
	 * gpoid(Company)
	 */
	private Long gpoId;
	/**
	 * gpo编码(Company)
	 */
	private String gpoCode;
	/**
	 * gpo名称(Company)
	 */
	private String gpoName;
	/**
	 * 规格编码
	 */
	private String modelCode;
	/**
	 * 包装编码
	 */
	private String packCode;
	/**
	 * 是否紧急配送药
	 */
	private Integer isUrgent;
	/**
	 * 是否禁用(1是，0否)
	 */
	private Integer isDisabled;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 通用名
	 */
	private String genericName;
	/**
	 * 英文名称
	 */
	private String englishName;
	/**
	 * 拼音简称
	 */
	private String pinyin2;
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
	 * 备注
	 */
	private String notes2;
	/**
	 * 是否社康药品
	 */
	private Integer isHealth;
	/**
	 * 是否国家谈判药品  (1是，0否)
	 */
	private Integer isNationalNegotiations;

	/** 主键 */

	@Id
	@SequenceGenerator(name = "generator", sequenceName = "t_dm_product_s")
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

	public void setCode(String code) {
		this.code = code;
	}

	@Column(length = 20)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(Long directoryId) {
		this.directoryId = directoryId;
	}

	public Long getDrugId() {
		return drugId;
	}

	public void setDrugId(Long drugId) {
		this.drugId = drugId;
	}

	@Column(length = 20)
	public String getNationalCode() {
		return nationalCode;
	}

	public void setNationalCode(String nationalCode) {
		this.nationalCode = nationalCode;
	}

	public Long getProducerId() {
		return producerId;
	}

	public void setProducerId(Long producerId) {
		this.producerId = producerId;
	}

	@Column(length = 200)
	public String getProducerName() {
		return producerName;
	}

	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}

	@Column(length = 300)
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	@Column(length = 20)
	public String getDoseUnit() {
		return doseUnit;
	}

	public void setDoseUnit(String doseUnit) {
		this.doseUnit = doseUnit;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getDoseDdd() {
		return doseDdd;
	}

	public void setDoseDdd(BigDecimal doseDdd) {
		this.doseDdd = doseDdd;
	}

	@Column(length = 100, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 100)
	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	@Column(length = 50)
	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	@Column(length = 200)
	public String getStandardCode() {
		return standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	@Column(length = 20)
	public String getPackageMaterial() {
		return packageMaterial;
	}

	public void setPackageMaterial(String packageMaterial) {
		this.packageMaterial = packageMaterial;
	}

	public Integer getConvertRatio() {
		return convertRatio;
	}

	public void setConvertRatio(Integer convertRatio) {
		this.convertRatio = convertRatio;
	}

	@Column(length = 20)
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Column(length = 20)
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@Column(length = 20)
	public String getMinunit() {
		return minunit;
	}

	public void setMinunit(String minunit) {
		this.minunit = minunit;
	}

	@Column(length = 20)
	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	@Column(precision = 16, scale = 2)
	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

	@Column(length = 20)
	public String getPriceFileNo() {
		return priceFileNo;
	}

	public void setPriceFileNo(String priceFileNo) {
		this.priceFileNo = priceFileNo;
	}

	public Long getBaseDrugType() {
		return baseDrugType;
	}

	public void setBaseDrugType(Long baseDrugType) {
		this.baseDrugType = baseDrugType;
	}

	@Column(length = 50)
	public String getBaseDrugTypeName() {
		return baseDrugTypeName;
	}

	public void setBaseDrugTypeName(String baseDrugTypeName) {
		this.baseDrugTypeName = baseDrugTypeName;
	}

	public Long getInsuranceDrugType() {
		return insuranceDrugType;
	}

	public void setInsuranceDrugType(Long insuranceDrugType) {
		this.insuranceDrugType = insuranceDrugType;
	}

	@Column(length = 50)
	public String getInsuranceDrugTypeName() {
		return insuranceDrugTypeName;
	}

	public void setInsuranceDrugTypeName(String insuranceDrugTypeName) {
		this.insuranceDrugTypeName = insuranceDrugTypeName;
	}

	@Column(length = 20)
	public String getYbdrugsNO() {
		return ybdrugsNO;
	}

	public void setYbdrugsNO(String ybdrugsNO) {
		this.ybdrugsNO = ybdrugsNO;
	}

	@Column(length = 20)
	public String getProductSource() {
		return productSource;
	}

	public void setProductSource(String productSource) {
		this.productSource = productSource;
	}

	@Column(length = 20)
	public String getMaterialSource() {
		return materialSource;
	}

	public void setMaterialSource(String materialSource) {
		this.materialSource = materialSource;
	}

	@Column(length = 100)
	public String getAuthorizeNo() {
		return authorizeNo;
	}

	public void setAuthorizeNo(String authorizeNo) {
		this.authorizeNo = authorizeNo;
	}

	public Date getAuthorizeBeginDate() {
		return authorizeBeginDate;
	}

	@Column(length = 100)
	public String getNationalAuthorizeNo() {
		return nationalAuthorizeNo;
	}

	public void setNationalAuthorizeNo(String nationalAuthorizeNo) {
		this.nationalAuthorizeNo = nationalAuthorizeNo;
	}

	public void setAuthorizeBeginDate(Date authorizeBeginDate) {
		this.authorizeBeginDate = authorizeBeginDate;
	}

	public Date getAuthorizeOutDate() {
		return authorizeOutDate;
	}

	public void setAuthorizeOutDate(Date authorizeOutDate) {
		this.authorizeOutDate = authorizeOutDate;
	}

	@Column(length = 30)
	public String getImportFileNo() {
		return importFileNo;
	}

	public void setImportFileNo(String importFileNo) {
		this.importFileNo = importFileNo;
	}

	public Date getImportBeginDate() {
		return importBeginDate;
	}

	public void setImportBeginDate(Date importBeginDate) {
		this.importBeginDate = importBeginDate;
	}

	public Date getImportOutDate() {
		return importOutDate;
	}

	public void setImportOutDate(Date importOutDate) {
		this.importOutDate = importOutDate;
	}

	@Column(length = 20)
	public String getQualityLevel() {
		return qualityLevel;
	}

	public void setQualityLevel(String qualityLevel) {
		this.qualityLevel = qualityLevel;
	}

	public Integer getgMPFlag() {
		return gMPFlag;
	}

	public void setgMPFlag(Integer gMPFlag) {
		this.gMPFlag = gMPFlag;
	}

	public Integer getHqgpcmmFlag() {
		return hqgpcmmFlag;
	}

	public void setHqgpcmmFlag(Integer hqgpcmmFlag) {
		this.hqgpcmmFlag = hqgpcmmFlag;
	}

	public Integer getEmergencyFlag() {
		return emergencyFlag;
	}

	public void setEmergencyFlag(Integer emergencyFlag) {
		this.emergencyFlag = emergencyFlag;
	}

	public Integer getProtectFlag() {
		return protectFlag;
	}

	public void setProtectFlag(Integer protectFlag) {
		this.protectFlag = protectFlag;
	}

	public Integer getNewDrugFlag() {
		return newDrugFlag;
	}

	public void setNewDrugFlag(Integer newDrugFlag) {
		this.newDrugFlag = newDrugFlag;
	}

	public Integer getConsignFlag() {
		return consignFlag;
	}

	public void setConsignFlag(Integer consignFlag) {
		this.consignFlag = consignFlag;
	}

	@Column(length = 20)
	public String getPatentType() {
		return patentType;
	}

	public void setPatentType(String patentType) {
		this.patentType = patentType;
	}

	public Date getPatentBeginDate() {
		return patentBeginDate;
	}

	public void setPatentBeginDate(Date patentBeginDate) {
		this.patentBeginDate = patentBeginDate;
	}

	public Date getPatentOutDate() {
		return patentOutDate;
	}

	public void setPatentOutDate(Date patentOutDate) {
		this.patentOutDate = patentOutDate;
	}

	public Date getProtectBeginDate() {
		return protectBeginDate;
	}

	public void setProtectBeginDate(Date protectBeginDate) {
		this.protectBeginDate = protectBeginDate;
	}

	public Date getProtectOutDate() {
		return protectOutDate;
	}

	public void setProtectOutDate(Date protectOutDate) {
		this.protectOutDate = protectOutDate;
	}

	public Date getNewDrugBeginDate() {
		return newDrugBeginDate;
	}

	public void setNewDrugBeginDate(Date newDrugBeginDate) {
		this.newDrugBeginDate = newDrugBeginDate;
	}

	public Date getNewDrugOutDate() {
		return newDrugOutDate;
	}

	public void setNewDrugOutDate(Date newDrugOutDate) {
		this.newDrugOutDate = newDrugOutDate;
	}

	public Date getConsignBeginDate() {
		return consignBeginDate;
	}

	public void setConsignBeginDate(Date consignBeginDate) {
		this.consignBeginDate = consignBeginDate;
	}

	public Date getConsignOutDate() {
		return consignOutDate;
	}

	public void setConsignOutDate(Date consignOutDate) {
		this.consignOutDate = consignOutDate;
	}

	@Column(length = 50)
	public String getConsigncoName() {
		return consigncoName;
	}

	public void setConsigncoName(String consigncoName) {
		this.consigncoName = consigncoName;
	}

	@Column(length = 20)
	public String getConsigncoCode() {
		return consigncoCode;
	}

	public void setConsigncoCode(String consigncoCode) {
		this.consigncoCode = consigncoCode;
	}

	@Column(length = 50)
	public String getLevel1AgentName() {
		return level1AgentName;
	}

	public void setLevel1AgentName(String level1AgentName) {
		this.level1AgentName = level1AgentName;
	}

	@Column(length = 20)
	public String getLevel1AgentCode() {
		return level1AgentCode;
	}

	public void setLevel1AgentCode(String level1AgentCode) {
		this.level1AgentCode = level1AgentCode;
	}

	@Column(length = 20)
	public String getArchFileNo() {
		return archFileNo;
	}

	public void setArchFileNo(String archFileNo) {
		this.archFileNo = archFileNo;
	}

	@Column(length = 50)
	public String getPackDesc() {
		return packDesc;
	}

	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}

	@Column(length = 20)
	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	@Column(length = 100)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(length = 50)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getIsGPOPurchase() {
		return isGPOPurchase;
	}

	public void setIsGPOPurchase(Integer isGPOPurchase) {
		this.isGPOPurchase = isGPOPurchase;
	}

	public Long getGpoId() {
		return gpoId;
	}

	public void setGpoId(Long gpoId) {
		this.gpoId = gpoId;
	}

	@Column(length = 20)
	public String getGpoCode() {
		return gpoCode;
	}

	public void setGpoCode(String gpoCode) {
		this.gpoCode = gpoCode;
	}

	@Column(length = 200)
	public String getGpoName() {
		return gpoName;
	}

	public void setGpoName(String gpoName) {
		this.gpoName = gpoName;
	}

	public Integer getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}

	@Column(length = 100)
	public String getGenericName() {
		return genericName;
	}

	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}

	@Column(length = 50)
	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	@Column(length = 50)
	public String getPinyin2() {
		return pinyin2;
	}

	public void setPinyin2(String pinyin2) {
		this.pinyin2 = pinyin2;
	}

	public Long getDosageFormId() {
		return dosageFormId;
	}

	public void setDosageFormId(Long dosageFormId) {
		this.dosageFormId = dosageFormId;
	}

	@Column(length = 50)
	public String getDosageFormName() {
		return dosageFormName;
	}

	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}

	@Column(length = 20)
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Column(length = 20)
	public String getPrescription() {
		return prescription;
	}

	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}

	@Column(length = 20)
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

	@Column(length = 20)
	public String getQsno() {
		return qsno;
	}

	public void setQsno(String qsno) {
		this.qsno = qsno;
	}

	@Column(length = 20)
	public String getQualityType() {
		return qualityType;
	}

	public void setQualityType(String qualityType) {
		this.qualityType = qualityType;
	}

	@Column(length = 20)
	public String getArchNo() {
		return archNo;
	}

	public void setArchNo(String archNo) {
		this.archNo = archNo;
	}

	@Column(length = 100)
	public String getNotes2() {
		return notes2;
	}

	public void setNotes2(String notes2) {
		this.notes2 = notes2;
	}

	@Column(length = 100)
	public String getProductGCode() {
		return productGCode;
	}

	public void setProductGCode(String productGCode) {
		this.productGCode = productGCode;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getPackCode() {
		return packCode;
	}

	public void setPackCode(String packCode) {
		this.packCode = packCode;
	}

	public Integer getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Integer isUrgent) {
		this.isUrgent = isUrgent;
	}

	public Integer getIsHealth() {
		return isHealth;
	}

	public void setIsHealth(Integer isHealth) {
		this.isHealth = isHealth;
	}

	public Integer getIsNationalNegotiations() {
		return isNationalNegotiations;
	}

	public void setIsNationalNegotiations(Integer isNationalNegotiations) {
		this.isNationalNegotiations = isNationalNegotiations;
	}

}