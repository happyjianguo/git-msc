package com.shyl.msc.dm.entity;

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

/**
 * gpo药品价格表
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_dm_gpoProduct")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GpoProductList extends BasicEntity {
	private static final long serialVersionUID = -1236398719015974891L;

	/**
	 *  遴选目录
	 */
	private Product product;
	/**
	 *  供应商编码
	 */
	private String vendorCode;
	/**
	 *  供应商名称
	 */
	private String vendorName;
	/**
	 * 价格
	 */
	private BigDecimal price;
	
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_gpoProduct_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="productId")
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
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
	@Column(precision=16, scale=2)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
