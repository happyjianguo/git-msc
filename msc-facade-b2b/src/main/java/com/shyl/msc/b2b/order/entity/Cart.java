package com.shyl.msc.b2b.order.entity;

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
 * 购物车细项 
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_order_cart")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cart extends BasicEntity {
	private static final long serialVersionUID = 3199265152401464871L;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 商品价格id
	 */
	private Long goodsPriceId;
	/**
	 * 数量
	 */
	private Integer num;
	/**
	 * 备注
	 */
	private String notes;

	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_cart_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(nullable=false,length=50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column(nullable=false)
	public Long getGoodsPriceId() {
		return goodsPriceId;
	}
	public void setGoodsPriceId(Long goodsPriceId) {
		this.goodsPriceId = goodsPriceId;
	}
	@Column(nullable=false)
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	@Column(length=100)
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}	

}