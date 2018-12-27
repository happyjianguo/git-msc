/*
 * 
 * 
 * 
 */
package com.shyl.msc.b2b.order.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.enmu.OrderType;

/**
 * Entity - 序列号
 * 
 * 
 * 
 */
@Entity
@Table(name = "t_order_sn")
public class Sn extends BasicEntity {
	private static final long serialVersionUID = -8107372380308881321L;

	/** 类型 */
	private OrderType type;
	
	/** 日期 */
	private String codeDate;

	/** 末值 */
	private Long lastValue;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_sn_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	@Column(nullable = false, updatable = false)
	public OrderType getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(OrderType type) {
		this.type = type;
	}
	/**
	 * 获取 日期
	 * 
	 * @return 日期
	 */
	@Column(length=10,nullable = false)
	public String getCodeDate() {
		return codeDate;
	}
	/**
	 * 设置日期
	 * 
	 * @param codeDate
	 *            日期
	 */
	public void setCodeDate(String codeDate) {
		this.codeDate = codeDate;
	}

	/**
	 * 获取末值
	 * 
	 * @return 末值
	 */
	@Column(nullable = false)
	public Long getLastValue() {
		return lastValue;
	}

	/**
	 * 设置末值
	 * 
	 * @param lastValue
	 *            末值
	 */
	public void setLastValue(Long lastValue) {
		this.lastValue = lastValue;
	}

}