package com.shyl.msc.menu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.dm.entity.DosageForm;
import com.shyl.msc.dm.entity.Drug;
import com.shyl.msc.set.entity.Company;

/** 药品 */
@Entity
@Table(name = "t_menu_product_source")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProductSource extends BasicEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 编码
	 */
	private String code;
	/**
	 * 药品名
	 */
	private String name;
	/**
	 * 英文名称
	 */
	private String englishName;

	/**
	 * 公司信息
	 */
	private Company company;
	
	/**
	 * 药品目录
	 */
	private Drug drug;
	
	/**
	 * 药品目录
	 */
	private DosageForm dosageForm;
	
	/**
	 * 规格
	 */
	private String model;
	/**
	 * 剂量
	 */
	private BigDecimal dose;
	/**
	 * 剂量单位
	 */
	private String doseUnit;
	/**
	 * 单位转换比
	 */
	private BigDecimal convertRatio;

	/**
	 * 是否紧急配送药
	 */
	private Integer isUrgent;
	
	/**
	 * 通用产品编码
	 */
	private String productGCode;

	/**
	 * 批准文号
	 */
	private String authorizeNo;
	
	/**
	 * 国药准字
	 */
	private String nationalAuthorizeNo;

	/**
	 * 保证规格
	 */
	private String packDesc;
	/**
	 * 商品名
	 */
	private String tradeName;
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
	 * 医保编号
	 */
	private String ybdrugsNO;
	/**
	 * 物价编号
	 */
	private String priceFileNo;
	/**
	 * 基本药物分类
	 */
	private String baseDrugTypeName;
	/**
	 * 注册证号
	 */
	private String importFileNo;
	/**
	 * 抗菌药物分类
	 */
	private String absDrugTypeName;
	/**
	 * 抗菌药物ddd值
	 */
	private String ddd;
	/**
	 * 医保药品分类
	 */
	private String ybDoseType;
	/**
	 *医保类别
	 */
	private String insuranceDrugTypeName;
	/**
	 * 本位码
	 */
	private String standardCode;
	/**
	 * 剂型分类
	 */
	private Long drugType;
	/**
	 * 特殊
	 */
	private Long insuranceDrugType;
	/**
	 * 基础药物
	 */
	private Long baseDrugType;
	
	/**
	 * 国家药品信息网ID
	 */
	private Long extId;
	
	/**
	 * 规格序号
	 */
	private String modelCode;
	/**
	 * 包装序号
	 */
	private String packCode;
	
	/**
	 * 拼音
	 */
	private String pinyin;
	
	/**
	 * 包装材质
	 */
	private String packageMaterial;

	/**
	 * 国家药品代码
	 */
	private String nationalCode;
	
	/**
	 * 定价类型
	 */
	private String priceType;
	
	/**
	 * 批准文号开始时间
	 */
	private Date authorizeBeginDate;
	
	/**
	 * 批准文号结束时间
	 */
	private Date authorizeOutDate;
	
	/**
	 * 注册证号开始时间
	 */
	private Date importBeginDate;
	
	/**
	 * 注册证号结束时间
	 */
	private Date importOutDate;
	
	/**
	 * 产品来源
	 */
	private String productSource;
	/**
	 * 原料来源
	 */
	private String materialSource;
	/**
	 * 是否GPM认证
	 */
	private Integer gMPFlag;
	
	/**
	 * 质量层次
	 */
	private String qualityLevel;
	
	/**
	 * 专利类型
	 */
	private String patentType;
	
	/**
	 * 专利有效期开始
	 */
	private Date patentBeginDate;
	
	/**
	 * 专利有效期结束
	 */
	private Date patentOutDate;
	
	/**
	 * 优质优价中成药
	 */
	private Integer hqgpcmmFlag;
	
	/**
	 * 中医院集诊必备中成药
	 */
	private Integer emergencyFlag;
	
	/**
	 * 中药保护品种
	 */
	private Integer protectFlag;
	
	/**
	 * 中药保护品种有效期开始
	 */
	private Date protectBeginDate;
	
	/**
	 *中药保护品种有效期结束
	 */
	private Date protectOutDate;
	
	/**
	 *新药
	 */
	private Integer newDrugFlag;
	
	/**
	 * 新药有效期开始
	 */
	private Date newDrugBeginDate;
	
	/**
	 * 新药有效期结束
	 */
	private Date newDrugOutDate;
	
	/**
	 *委托加工
	 */
	private Integer consignFlag;
	
	/**
	 *委托加工有效期结束
	 */
	private Date consignBeginDate;
	
	/**
	 *委托加工有效期结束
	 */
	private Date consignOutDate;
	
	/**
	 *委托加工企业编码
	 */
	private String consigncoCode;
	
	/**
	 *委托加工企业名称
	 */
	private String consigncoName;
	
	/**
	 * 一级总代理编码
	 */
	private String level1AgentCode;
	
	/**
	 * 一级总代理名称
	 */
	private String level1AgentName;
	
	/**
	 * 归档文件序列号
	 */
	private String archFileNo;
	
	/**
	 * 归档文件序列号
	 */
	private Integer isDisabled;
	
	/**
	 * 备注
	 */
	private String notes;



	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "t_menu_product_source_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="producerId")
	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="drugId")
	public Drug getDrug() {
		return drug;
	}
	
	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="dosageFormId")
	public DosageForm getDosageForm() {
		return dosageForm;
	}
	
	public void setDosageForm(DosageForm dosageForm) {
		this.dosageForm = dosageForm;
	}
	
	@Column(length = 1000, unique = true, nullable = false)
	public String getCode() {
		return code;
	}

	@Column(length = 100)
	public String getName() {
		return name;
	}

	@Column(length = 300)
	public String getEnglishName() {
		return englishName;
	}

	@Column(length = 2000)
	public String getModel() {
		return model;
	}


	@Column(length = 50)
	public String getAuthorizeNo() {
		return authorizeNo;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public void setModel(String model) {
		this.model = model;
	}


	public void setAuthorizeNo(String authorizeNo) {
		this.authorizeNo = authorizeNo;
	}

	public Long getExtId() {
		return extId;
	}

	public void setExtId(Long extId) {
		this.extId = extId;
	}

	@Column(length = 50)
	public String getPackDesc() {
		return packDesc;
	}

	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	public BigDecimal getConvertRatio() {
		return convertRatio;
	}

	public void setConvertRatio(BigDecimal convertRatio) {
		this.convertRatio = convertRatio;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getMinunit() {
		return minunit;
	}

	public void setMinunit(String minunit) {
		this.minunit = minunit;
	}

	@Column(length = 100)
	public String getYbdrugsNO() {
		return ybdrugsNO;
	}


	public void setYbdrugsNO(String ybdrugsNO) {
		this.ybdrugsNO = ybdrugsNO;
	}

	@Column(length = 50)
	public String getPriceFileNo() {
		return priceFileNo;
	}

	@Column(length = 50)
	public String getBaseDrugTypeName() {
		return baseDrugTypeName;
	}

	@Column(length = 50)
	public String getImportFileNo() {
		return importFileNo;
	}

	@Column(length = 50)
	public String getAbsDrugTypeName() {
		return absDrugTypeName;
	}

	@Column(length = 50)
	public String getDdd() {
		return ddd;
	}

	public String getYbDoseType() {
		return ybDoseType;
	}
	public void setPriceFileNo(String priceFileNo) {
		this.priceFileNo = priceFileNo;
	}

	public void setBaseDrugTypeName(String baseDrugTypeName) {
		this.baseDrugTypeName = baseDrugTypeName;
	}

	public void setImportFileNo(String importFileNo) {
		this.importFileNo = importFileNo;
	}

	public void setAbsDrugTypeName(String absDrugTypeName) {
		this.absDrugTypeName = absDrugTypeName;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public void setYbDoseType(String ybDoseType) {
		this.ybDoseType = ybDoseType;
	}

	@Column(length = 200)
	public String getStandardCode() {
		return standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	public Long getDrugType() {
		return drugType;
	}

	public void setDrugType(Long drugType) {
		this.drugType = drugType;
	}
	public Long getBaseDrugType() {
		return baseDrugType;
	}

	public void setBaseDrugType(Long baseDrugType) {
		this.baseDrugType = baseDrugType;
	}

	public String getInsuranceDrugTypeName() {
		return insuranceDrugTypeName;
	}

	public void setInsuranceDrugTypeName(String insuranceDrugTypeName) {
		this.insuranceDrugTypeName = insuranceDrugTypeName;
	}

	public Long getInsuranceDrugType() {
		return insuranceDrugType;
	}

	public void setInsuranceDrugType(Long insuranceDrugType) {
		this.insuranceDrugType = insuranceDrugType;
	}

	@Column(length = 20)
	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	@Column(length = 20)
	public String getPackCode() {
		return packCode;
	}

	public void setPackCode(String packCode) {
		this.packCode = packCode;
	}

	@Column(length = 10)
	public String getDoseUnit() {
		return doseUnit;
	}

	public void setDoseUnit(String doseUnit) {
		this.doseUnit = doseUnit;
	}

	@Column(length = 50)
	public String getProductGCode() {
		return productGCode;
	}

	public void setProductGCode(String productGCode) {
		this.productGCode = productGCode;
	}

	@Column(length = 50)
	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public Integer getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Integer isUrgent) {
		this.isUrgent = isUrgent;
	}

	@Column(length = 20)
	public String getPackageMaterial() {
		return packageMaterial;
	}

	@Column(length = 20)
	public String getNationalCode() {
		return nationalCode;
	}

	@Column(length = 20)
	public String getPriceType() {
		return priceType;
	}

	public Date getAuthorizeBeginDate() {
		return authorizeBeginDate;
	}

	public Date getAuthorizeOutDate() {
		return authorizeOutDate;
	}

	public Date getImportBeginDate() {
		return importBeginDate;
	}

	public Date getImportOutDate() {
		return importOutDate;
	}

	@Column(length = 20)
	public String getProductSource() {
		return productSource;
	}

	@Column(length = 20)
	public String getMaterialSource() {
		return materialSource;
	}

	public Integer getgMPFlag() {
		return gMPFlag;
	}

	@Column(length = 20)
	public String getQualityLevel() {
		return qualityLevel;
	}

	@Column(length = 20)
	public String getPatentType() {
		return patentType;
	}

	public Date getPatentBeginDate() {
		return patentBeginDate;
	}

	public Date getPatentOutDate() {
		return patentOutDate;
	}

	public Integer getHqgpcmmFlag() {
		return hqgpcmmFlag;
	}

	public Integer getEmergencyFlag() {
		return emergencyFlag;
	}

	public Integer getProtectFlag() {
		return protectFlag;
	}

	public Date getProtectBeginDate() {
		return protectBeginDate;
	}

	public Date getProtectOutDate() {
		return protectOutDate;
	}

	public Integer getNewDrugFlag() {
		return newDrugFlag;
	}

	public Date getNewDrugBeginDate() {
		return newDrugBeginDate;
	}

	public Date getNewDrugOutDate() {
		return newDrugOutDate;
	}

	public Integer getConsignFlag() {
		return consignFlag;
	}

	public Date getConsignBeginDate() {
		return consignBeginDate;
	}

	public Date getConsignOutDate() {
		return consignOutDate;
	}

	@Column(length = 20)
	public String getConsigncoCode() {
		return consigncoCode;
	}

	@Column(length = 50)
	public String getConsigncoName() {
		return consigncoName;
	}

	@Column(length = 20)
	public String getLevel1AgentCode() {
		return level1AgentCode;
	}

	@Column(length = 50)
	public String getLevel1AgentName() {
		return level1AgentName;
	}

	@Column(length = 20)
	public String getArchFileNo() {
		return archFileNo;
	}

	public Integer getIsDisabled() {
		return isDisabled;
	}

	@Column(length = 500)
	public String getNotes() {
		return notes;
	}

	public void setPackageMaterial(String packageMaterial) {
		this.packageMaterial = packageMaterial;
	}

	public void setNationalCode(String nationalCode) {
		this.nationalCode = nationalCode;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public void setAuthorizeBeginDate(Date authorizeBeginDate) {
		this.authorizeBeginDate = authorizeBeginDate;
	}

	public void setAuthorizeOutDate(Date authorizeOutDate) {
		this.authorizeOutDate = authorizeOutDate;
	}

	public void setImportBeginDate(Date importBeginDate) {
		this.importBeginDate = importBeginDate;
	}

	public void setImportOutDate(Date importOutDate) {
		this.importOutDate = importOutDate;
	}

	public void setProductSource(String productSource) {
		this.productSource = productSource;
	}

	public void setMaterialSource(String materialSource) {
		this.materialSource = materialSource;
	}

	public void setgMPFlag(Integer gMPFlag) {
		this.gMPFlag = gMPFlag;
	}

	public void setQualityLevel(String qualityLevel) {
		this.qualityLevel = qualityLevel;
	}

	public void setPatentType(String patentType) {
		this.patentType = patentType;
	}

	public void setPatentBeginDate(Date patentBeginDate) {
		this.patentBeginDate = patentBeginDate;
	}

	public void setPatentOutDate(Date patentOutDate) {
		this.patentOutDate = patentOutDate;
	}

	public void setHqgpcmmFlag(Integer hqgpcmmFlag) {
		this.hqgpcmmFlag = hqgpcmmFlag;
	}

	public void setEmergencyFlag(Integer emergencyFlag) {
		this.emergencyFlag = emergencyFlag;
	}

	public void setProtectFlag(Integer protectFlag) {
		this.protectFlag = protectFlag;
	}

	public void setProtectBeginDate(Date protectBeginDate) {
		this.protectBeginDate = protectBeginDate;
	}

	public void setProtectOutDate(Date protectOutDate) {
		this.protectOutDate = protectOutDate;
	}

	public void setNewDrugFlag(Integer newDrugFlag) {
		this.newDrugFlag = newDrugFlag;
	}

	public void setNewDrugBeginDate(Date newDrugBeginDate) {
		this.newDrugBeginDate = newDrugBeginDate;
	}

	public void setNewDrugOutDate(Date newDrugOutDate) {
		this.newDrugOutDate = newDrugOutDate;
	}

	public void setConsignFlag(Integer consignFlag) {
		this.consignFlag = consignFlag;
	}

	public void setConsignBeginDate(Date consignBeginDate) {
		this.consignBeginDate = consignBeginDate;
	}

	public void setConsignOutDate(Date consignOutDate) {
		this.consignOutDate = consignOutDate;
	}

	public void setConsigncoCode(String consigncoCode) {
		this.consigncoCode = consigncoCode;
	}

	public void setConsigncoName(String consigncoName) {
		this.consigncoName = consigncoName;
	}

	public void setLevel1AgentCode(String level1AgentCode) {
		this.level1AgentCode = level1AgentCode;
	}

	public void setLevel1AgentName(String level1AgentName) {
		this.level1AgentName = level1AgentName;
	}

	public void setArchFileNo(String archFileNo) {
		this.archFileNo = archFileNo;
	}

	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Column(length = 100)
	public String getNationalAuthorizeNo() {
		return nationalAuthorizeNo;
	}

	public void setNationalAuthorizeNo(String nationalAuthorizeNo) {
		this.nationalAuthorizeNo = nationalAuthorizeNo;
	}


}