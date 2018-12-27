package com.shyl.msc.b2b.plan.entity;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.dm.entity.Product;
/**
 * 三方合同明细
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_plan_contract_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ContractDetail extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	/**
	 * 状态
	 */
	public enum Status {
		uneffect("未生效"),
		effect("已生效"),
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
	 * 合同
	 */
	private Contract contract;
	/**
	 * 合同明细编号
	 */
	private String code;
	/**
	 * 产品
	 */
	private Product product;
	/**
	 * 单价
	 */
	private BigDecimal price;
	/**
	 * 合同数量
	 */
	private BigDecimal contractNum;
	/**
	 * 合同金额
	 */
	private BigDecimal contractAmt;
	/**
	 * 购物车数量
	 */
	private BigDecimal cartNum;
	/**
	 * 购物车金额
	 */
	private BigDecimal cartAmt;
	/**
	 * 采购计划数量
	 */
	private BigDecimal purchasePlanNum;
	/**
	 * 采购计划金额
	 */
	private BigDecimal purchasePlanAmt;
	/**
	 * 采购数量
	 */
	private BigDecimal purchaseNum;
	/**
	 * 采购金额
	 */
	private BigDecimal purchaseAmt;
	/**
	 * 配送数量
	 */
	private BigDecimal deliveryNum;
	/**
	 * 配送金额
	 */
	private BigDecimal deliveryAmt;
	/**
	 * 退货数量
	 */
	private BigDecimal returnsNum;
	/**
	 * 退货金额
	 */
	private BigDecimal returnsAmt;
	/**
	 * 订单结案数量
	 */
	private BigDecimal closedNum;
	/**
	 * 订单结案金额
	 */
	private BigDecimal closedAmt;
	/**
	 * 状态
	 */
	private Status status;
	/**
	 * 是否过账（0未过账，1过账）
	 */
	private Integer isPass;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_plan_contract_detail_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="contractId")
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	@Column(length=30)
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
	@Column(precision=16, scale=2)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	@Column(precision=16, scale=0)
	public BigDecimal getContractNum() {
		return contractNum;
	}
	public void setContractNum(BigDecimal contractNum) {
		this.contractNum = contractNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getContractAmt() {
		return contractAmt;
	}
	public void setContractAmt(BigDecimal contractAmt) {
		this.contractAmt = contractAmt;
	}
	@Column(precision=16, scale=0)
	public BigDecimal getCartNum() {
		return cartNum;
	}
	public void setCartNum(BigDecimal cartNum) {
		this.cartNum = cartNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getCartAmt() {
		return cartAmt;
	}
	public void setCartAmt(BigDecimal cartAmt) {
		this.cartAmt = cartAmt;
	}
	@Column(precision=16, scale=0)
	public BigDecimal getPurchasePlanNum() {
		return purchasePlanNum;
	}
	public void setPurchasePlanNum(BigDecimal purchasePlanNum) {
		this.purchasePlanNum = purchasePlanNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getPurchasePlanAmt() {
		return purchasePlanAmt;
	}
	public void setPurchasePlanAmt(BigDecimal purchasePlanAmt) {
		this.purchasePlanAmt = purchasePlanAmt;
	}
	@Column(precision=16, scale=0)
	public BigDecimal getPurchaseNum() {
		return purchaseNum;
	}
	public void setPurchaseNum(BigDecimal purchaseNum) {
		this.purchaseNum = purchaseNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getPurchaseAmt() {
		return purchaseAmt;
	}
	public void setPurchaseAmt(BigDecimal purchaseAmt) {
		this.purchaseAmt = purchaseAmt;
	}
	@Column(precision=16, scale=0)
	public BigDecimal getDeliveryNum() {
		return deliveryNum;
	}
	public void setDeliveryNum(BigDecimal deliveryNum) {
		this.deliveryNum = deliveryNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getDeliveryAmt() {
		return deliveryAmt;
	}
	public void setDeliveryAmt(BigDecimal deliveryAmt) {
		this.deliveryAmt = deliveryAmt;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getReturnsNum() {
		return returnsNum;
	}
	public void setReturnsNum(BigDecimal returnsNum) {
		this.returnsNum = returnsNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getReturnsAmt() {
		return returnsAmt;
	}
	public void setReturnsAmt(BigDecimal returnsAmt) {
		this.returnsAmt = returnsAmt;
	}
	
	@Column(precision=16, scale=0)
	public BigDecimal getClosedNum() {
		return closedNum;
	}
	public void setClosedNum(BigDecimal closedNum) {
		this.closedNum = closedNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getClosedAmt() {
		return closedAmt;
	}
	public void setClosedAmt(BigDecimal closedAmt) {
		this.closedAmt = closedAmt;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Integer getIsPass() {
		return isPass;
	}
	public void setIsPass(Integer isPass) {
		this.isPass = isPass;
	}
	
}
