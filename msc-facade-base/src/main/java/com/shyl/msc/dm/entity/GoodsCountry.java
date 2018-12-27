package com.shyl.msc.dm.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
import javax.persistence.*;

/**
 * 国家药品映射关系
 *
 */
@Entity
@Table(name = "t_dm_goods_country")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GoodsCountry extends BasicEntity{
    private static final long serialVersionUID = 1L;

    /**
     * 产品
     */
    private Long productId;
    /**
     * 商品编码
     */
    private String productCode;
    /**
     * 药品标准码
     */
    private String productGCode;
    /**
     * 药品名称
     */
    private String productName;
    /**
     * 包装规格
     */
    private String packDesc;
    /**
     * 规格
     */
    private String model;
    /**
     * 剂型名
     */
    private String dosageFormName;
    /**
     * 生产企业名称
     */
    private String producerName;
    /**
     * 批准文号
     */
    private String authorizeNo;



    /**
     * 国家药品编码
     */
    private String countryProductCode;
    /**
     * 国家药品名称
     */
    private String countryProductName;
    /**
     * 国家生产企业编码
     */
    private String countryProducerCode;
    /**
     * 国家生产企业名称
     */
    private String countryProducerName;
    /**
     * 国家批准文号
     */
    private String countryAuthorizeNo;
    /**
     * 大类
     */
    private String parentTypeName;
    /**
     * 大类码
     */
    private String parentTypeCode;
    /**
     * 药理/功效分类
     */
    private String medicTypeName;
    /**
     *药理/功效分类码
     */
    private String medciTypeCode;
    /**
     * 品种名（中文）
     */
    private String varietyName;
    /**
     * 品种名代码
     */
    private String varietyCode;
    /**
     * 酸根盐基
     */
    private String acidSalt;
    /**
     * 酸根盐基编码
     */
    private String acidSaltCode;
    /**
     * 剂型分类名称
     */
    private String countryDosageFormName;
    /**
     * 剂型分类编码
     */
    private String countryDosageFormCode;
    /**
     * 规格
     */
    private String countryModel;
    /**
     * 制剂规格分类码
     */
    private String countryModelCode;
    /**
     * 转换系数
     */
    private Integer countryConvertRatio;
    /**
     * 转换系数编码
     */
    private String countryConvertRatioCode;
    /**
     * 材质
     */
    private String material;
    /**
     * 最小包装单位
     */
    private String minPackageUnit;
    /**
     * 最小制剂单位
     */
    private String minPreparationUnit;
    /**
     *29位码
     */
    private String countryProductGCode;

    @Id
    @SequenceGenerator(name = "generator",sequenceName="t_dm_goods_country_s")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(nullable=true)
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
    @Column(length=50)
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    @Column(length=50)
    public String getProductGCode() {
        return productGCode;
    }

    public void setProductGCode(String productGCode) {
        this.productGCode = productGCode;
    }
    @Column(length=100)
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    @Column(length=50)
    public String getPackDesc() {
        return packDesc;
    }

    public void setPackDesc(String packDesc) {
        this.packDesc = packDesc;
    }
    @Column(length=300)
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    @Column(length=50)
    public String getDosageFormName() {
        return dosageFormName;
    }

    public void setDosageFormName(String dosageFormName) {
        this.dosageFormName = dosageFormName;
    }
    @Column(length=300)
    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }
    @Column(length=100)
    public String getAuthorizeNo() {
        return authorizeNo;
    }

    public void setAuthorizeNo(String authorizeNo) {
        this.authorizeNo = authorizeNo;
    }
    @Column(length=50)
    public String getCountryProductCode() {
        return countryProductCode;
    }

    public void setCountryProductCode(String countryProductCode) {
        this.countryProductCode = countryProductCode;
    }
    @Column(length=100)
    public String getCountryProductName() {
        return countryProductName;
    }

    public void setCountryProductName(String countryProductName) {
        this.countryProductName = countryProductName;
    }
    @Column(length=50)
    public String getCountryProducerCode() {
        return countryProducerCode;
    }

    public void setCountryProducerCode(String countryProducerCode) {
        this.countryProducerCode = countryProducerCode;
    }
    @Column(length=300)
    public String getCountryProducerName() {
        return countryProducerName;
    }

    public void setCountryProducerName(String countryProducerName) {
        this.countryProducerName = countryProducerName;
    }
    @Column(length=100)
    public String getCountryAuthorizeNo() {
        return countryAuthorizeNo;
    }

    public void setCountryAuthorizeNo(String countryAuthorizeNo) {
        this.countryAuthorizeNo = countryAuthorizeNo;
    }
    @Column(length=300)
    public String getParentTypeName() {
        return parentTypeName;
    }

    public void setParentTypeName(String parentTypeName) {
        this.parentTypeName = parentTypeName;
    }
    @Column(length=50)
    public String getParentTypeCode() {
        return parentTypeCode;
    }

    public void setParentTypeCode(String parentTypeCode) {
        this.parentTypeCode = parentTypeCode;
    }
    @Column(length=300)
    public String getMedicTypeName() {
        return medicTypeName;
    }

    public void setMedicTypeName(String medicTypeName) {
        this.medicTypeName = medicTypeName;
    }
    @Column(length=50)
    public String getMedciTypeCode() {
        return medciTypeCode;
    }

    public void setMedciTypeCode(String medciTypeCode) {
        this.medciTypeCode = medciTypeCode;
    }
    @Column(length=300)
    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }
    @Column(length=50)
    public String getVarietyCode() {
        return varietyCode;
    }

    public void setVarietyCode(String varietyCode) {
        this.varietyCode = varietyCode;
    }
    @Column(length=50)
    public String getAcidSalt() {
        return acidSalt;
    }

    public void setAcidSalt(String acidSalt) {
        this.acidSalt = acidSalt;
    }
    @Column(length=50)
    public String getAcidSaltCode() {
        return acidSaltCode;
    }

    public void setAcidSaltCode(String acidSaltCode) {
        this.acidSaltCode = acidSaltCode;
    }
    @Column(length=100)
    public String getCountryDosageFormName() {
        return countryDosageFormName;
    }

    public void setCountryDosageFormName(String countryDosageFormName) {
        this.countryDosageFormName = countryDosageFormName;
    }
    @Column(length=50)
    public String getCountryDosageFormCode() {
        return countryDosageFormCode;
    }

    public void setCountryDosageFormCode(String countryDosageFormCode) {
        this.countryDosageFormCode = countryDosageFormCode;
    }
    @Column(length = 300)
    public String getCountryModel() {
        return countryModel;
    }

    public void setCountryModel(String countryModel) {
        this.countryModel = countryModel;
    }
    @Column(length=50)
    public String getCountryModelCode() {
        return countryModelCode;
    }

    public void setCountryModelCode(String countryModelCode) {
        this.countryModelCode = countryModelCode;
    }

    public Integer getCountryConvertRatio() {
        return countryConvertRatio;
    }

    public void setCountryConvertRatio(Integer countryConvertRatio) {
        this.countryConvertRatio = countryConvertRatio;
    }
    @Column(length=50)
    public String getCountryConvertRatioCode() {
        return countryConvertRatioCode;
    }

    public void setCountryConvertRatioCode(String countryConvertRatioCode) {
        this.countryConvertRatioCode = countryConvertRatioCode;
    }
    @Column(length=50)
    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
    @Column(length=50)
    public String getMinPackageUnit() {
        return minPackageUnit;
    }

    public void setMinPackageUnit(String minPackageUnit) {
        this.minPackageUnit = minPackageUnit;
    }
    @Column(length=50)
    public String getMinPreparationUnit() {
        return minPreparationUnit;
    }

    public void setMinPreparationUnit(String minPreparationUnit) {
        this.minPreparationUnit = minPreparationUnit;
    }
    @Column(length=100)
    public String getCountryProductGCode() {
        return countryProductGCode;
    }

    public void setCountryProductGCode(String countryProductGCode) {
        this.countryProductGCode = countryProductGCode;
    }
}
