package com.shyl.msc.b2b.stl.entity;

import java.math.BigDecimal;

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

import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.dm.entity.Product;

/**
 * 发票明细
 * 
 * 
 * */
@Entity
@Table(name = "t_stl_invoice_detail")
public class InvoiceDetail extends BasicEntity {
	private static final long serialVersionUID = 6536458620920376491L;

	/**
	 * 发票
	 */
	private Invoice invoice;
	/**
	 * 编号
	 */
	private String code;
	/**
	 * 药品
	 */
	private Product product;
	/**
	 * 药品编码
	 */
	private String productCode;
	/**
	 * 药品名称
	 */
	private String productName;
	/**
	 * 生产企业
	 */
	private String producerName;
	/**
	 * 剂型名称
	 */
	private String dosageFormName;
	/**
	 * 规格
	 */
	private String model;	
	/**
	 * 包装规格
	 */
	private String packDesc;
	/**
	 * 单位
	 */
	private String unit;
	/**
	 * 单价
	 */
	private BigDecimal price;
	/**
	 * 数量
	 */
	private BigDecimal goodsNum;
	/**
	 * 金额
	 */
	private BigDecimal goodsSum;
	/**
	 * 不含税单价
	 */
	private BigDecimal noTaxPrice;
	/**
	 * 税率
	 */
	private BigDecimal taxRate;
	/**
	 * 不含税金额
	 */
	private BigDecimal noTaxSum;
	/**
	 * 批号
	 */
	private String batchCode;
	/**
	 * 配送或退货单明细编号
	 */
	private String deliveryOrReturnsDetailCode;
	/**
	 * 配送或退货单编号
	 */
	private String deliveryOrReturnsCode;
	
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_stl_invoice_detail_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="invoiceId")
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	@Column(length=30,unique = true, nullable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="productId")
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	@Column(length=50)
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	@Column(length=100)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Column(length=200)
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	@Column(length = 50)
	public String getDosageFormName() {
		return dosageFormName;
	}

	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}
	@Column(length = 300)
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@Column(length = 50)
	public String getPackDesc() {
		return packDesc;
	}
	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}
	@Column(length=20)
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(BigDecimal goodsNum) {
		this.goodsNum = goodsNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getGoodsSum() {
		return goodsSum;
	}
	public void setGoodsSum(BigDecimal goodsSum) {
		this.goodsSum = goodsSum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getNoTaxPrice() {
		return noTaxPrice;
	}
	public void setNoTaxPrice(BigDecimal noTaxPrice) {
		this.noTaxPrice = noTaxPrice;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getNoTaxSum() {
		return noTaxSum;
	}
	public void setNoTaxSum(BigDecimal noTaxSum) {
		this.noTaxSum = noTaxSum;
	}
	@Column(length=30)
	public String getDeliveryOrReturnsDetailCode() {
		return deliveryOrReturnsDetailCode;
	}
	public void setDeliveryOrReturnsDetailCode(String deliveryOrReturnsDetailCode) {
		this.deliveryOrReturnsDetailCode = deliveryOrReturnsDetailCode;
	}
	@Column(length=30)
	public String getDeliveryOrReturnsCode() {
		return deliveryOrReturnsCode;
	}
	public void setDeliveryOrReturnsCode(String deliveryOrReturnsCode) {
		this.deliveryOrReturnsCode = deliveryOrReturnsCode;
	}
	@Column(length=100)
	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
}