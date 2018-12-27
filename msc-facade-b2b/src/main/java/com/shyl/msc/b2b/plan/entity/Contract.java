package com.shyl.msc.b2b.plan.entity;

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
/**
 * 三方合同
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_plan_contract")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contract extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	/**
	 * 状态
	 */
	public enum Status {
		noConfirm("待确认"),
		unsigned("待签订"),
		hospitalSigned("医院签订"),
		rejected("已驳回"),
		signed("已签订"),
		executed("执行完"),
		cancel("已作废"),
		stop("已终止");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 编号
	 */
	private String code;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 供应商编码
	 */
	private String vendorCode;
	/**
	 * 供应商名称
	 */
	private String vendorName;
	/**
	 * gpo编码
	 */
	private String gpoCode;
	/**
	 * gpo名称
	 */
	private String gpoName;
	/**
	 * 合同金额
	 */
	private BigDecimal amt;
	/**
	 * 医院确认时间
	 */
	private Date hospitalConfirmDate;	
	/**
	 * 供应商确认时间
	 */
	private Date vendorConfirmDate;
	/**
	 * gpo确认时间
	 */
	private Date gpoConfirmDate;
	/**
	 * 有效期起
	 */
	private String startValidDate;
	/**
	 * 有效期止
	 */
	private String endValidDate;
	/**
	 * 生效时间
	 */
	private Date effectiveDate;	
	/**
	 * 医院签章文件路径
	 */
	private String hospitalSealPath;
	/**
	 * 供应商签章文件路径
	 */
	private String vendorSealPath;
	/**
	 * gpo签章文件路径
	 */
	private String gpoSealPath;
	/**
	 * 合同文件路径
	 */
	private String filePath;
	/**
	 * 签章页码
	 */
	private Integer pageNum;
	/**
	 * 医院签章x轴坐标
	 */
	private BigDecimal hospitalX;
	/**
	 * 医院签章Y轴坐标
	 */
	private BigDecimal hospitalY;
	/**
	 * GPO签章x轴坐标
	 */
	private BigDecimal gpoX;
	/**
	 * GPO签章Y轴坐标
	 */
	private BigDecimal gpoY;
	/**
	 * 供应商签章x轴坐标
	 */
	private BigDecimal vendorX;
	/**
	 * 供应商签章Y轴坐标
	 */
	private BigDecimal vendorY;
	/**
	 * 父协议
	 */
	private Long parentId;
	/**
	 * 状态
	 */
	private Status status;
	/**
	 * 驳回原因
	 */
	private String rejectedReason;
	/**
	 * 是否过账（0未过账，1过账）
	 */
	private Integer isPass;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_plan_contract_s")
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
	@Column(length=50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column(length=100)
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@Column(length=20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length=100)
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	@Column(length=20)
	public String getGpoCode() {
		return gpoCode;
	}
	public void setGpoCode(String gpoCode) {
		this.gpoCode = gpoCode;
	}
	@Column(length=100)
	public String getGpoName() {
		return gpoName;
	}
	public void setGpoName(String gpoName) {
		this.gpoName = gpoName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getAmt() {
		return amt;
	}
	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}
	public Date getHospitalConfirmDate() {
		return hospitalConfirmDate;
	}	
	public void setHospitalConfirmDate(Date hospitalConfirmDate) {
		this.hospitalConfirmDate = hospitalConfirmDate;
	}
	public Date getVendorConfirmDate() {
		return vendorConfirmDate;
	}
	public void setVendorConfirmDate(Date vendorConfirmDate) {
		this.vendorConfirmDate = vendorConfirmDate;
	}
	public Date getGpoConfirmDate() {
		return gpoConfirmDate;
	}
	public void setGpoConfirmDate(Date gpoConfirmDate) {
		this.gpoConfirmDate = gpoConfirmDate;
	}
	@Column(length=10)
	public String getStartValidDate() {
		return startValidDate;
	}
	public void setStartValidDate(String startValidDate) {
		this.startValidDate = startValidDate;
	}
	@Column(length=10)
	public String getEndValidDate() {
		return endValidDate;
	}
	public void setEndValidDate(String endValidDate) {
		this.endValidDate = endValidDate;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	@Column(length=100)
	public String getHospitalSealPath() {
		return hospitalSealPath;
	}
	public void setHospitalSealPath(String hospitalSealPath) {
		this.hospitalSealPath = hospitalSealPath;
	}
	@Column(length=100)
	public String getVendorSealPath() {
		return vendorSealPath;
	}
	public void setVendorSealPath(String vendorSealPath) {
		this.vendorSealPath = vendorSealPath;
	}
	@Column(length=100)
	public String getGpoSealPath() {
		return gpoSealPath;
	}
	public void setGpoSealPath(String gpoSealPath) {
		this.gpoSealPath = gpoSealPath;
	}
	@Column(length=100)
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getHospitalX() {
		return hospitalX;
	}
	public void setHospitalX(BigDecimal hospitalX) {
		this.hospitalX = hospitalX;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getHospitalY() {
		return hospitalY;
	}
	public void setHospitalY(BigDecimal hospitalY) {
		this.hospitalY = hospitalY;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getGpoX() {
		return gpoX;
	}
	public void setGpoX(BigDecimal gpoX) {
		this.gpoX = gpoX;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getGpoY() {
		return gpoY;
	}
	public void setGpoY(BigDecimal gpoY) {
		this.gpoY = gpoY;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getVendorX() {
		return vendorX;
	}
	public void setVendorX(BigDecimal vendorX) {
		this.vendorX = vendorX;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getVendorY() {
		return vendorY;
	}
	public void setVendorY(BigDecimal vendorY) {
		this.vendorY = vendorY;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	@Column(length=600)
	public String getRejectedReason() {
		return rejectedReason;
	}
	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
	}
	public Integer getIsPass() {
		return isPass;
	}
	public void setIsPass(Integer isPass) {
		this.isPass = isPass;
	}
	
}
