package com.shyl.msc.b2b.plan.entity;

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
import com.shyl.msc.set.entity.ProjectDetail;

/** 供应商申报药品 */
@Entity
@Table(name = "t_plan_directory_vendor")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DirectoryVendor extends BasicEntity {
	private static final long serialVersionUID = 8999057192563600418L;
	/**
	 * 状态
	 */
	public enum Status {
		undeclare("未申报"),
		declare("申报"),
		unwin("未中标"),
		win("中标");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 供应商编码
	 */
	private String vendorCode;
	/**
	 * 供应商名称
	 */
	private String vendorName;
	/**
	 * 招标项目细项
	 */
	private ProjectDetail projectDetail;
	/**
	 * 药品名称
	 */
	private String productName;
	/**
	 * 规格
	 */
	private String model;
	/**
	 * 厂商编码
	 */
	private String producerCode;
	/**
	 * 厂商名称
	 */
	private String producerName;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 申报时间
	 */
	private Date declareDate;
	/**
	 * 评标时间
	 */
	private Date evaluationDate;
	/**
	 * 定标时间
	 */
	private Date calibrationDate;
	/**
	 *	评标平均分
	 */
	private BigDecimal avgScore = new BigDecimal("0");
	/**
	 *	对应药品
	 */
	private Long productId;
	/**
	 * 状态
	 */
	private Status status;

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_tender_directory_vendor_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length = 20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length = 100)
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="projectDetailId")
	public ProjectDetail getProjectDetail() {
		return projectDetail;
	}
	public void setProjectDetail(ProjectDetail projectDetail) {
		this.projectDetail = projectDetail;
	}
	@Column(length = 100)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Column(length = 300)
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@Column(length = 20)
	public String getProducerCode() {
		return producerCode;
	}
	public void setProducerCode(String producerCode) {
		this.producerCode = producerCode;
	}
	@Column(length = 100)
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public Date getDeclareDate() {
		return declareDate;
	}
	public void setDeclareDate(Date declareDate) {
		this.declareDate = declareDate;
	}
	public Date getEvaluationDate() {
		return evaluationDate;
	}
	public void setEvaluationDate(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}
	public Date getCalibrationDate() {
		return calibrationDate;
	}
	public void setCalibrationDate(Date calibrationDate) {
		this.calibrationDate = calibrationDate;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public BigDecimal getAvgScore() {
		return avgScore;
	}
	public void setAvgScore(BigDecimal avgScore) {
		this.avgScore = avgScore;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
}
