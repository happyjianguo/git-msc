package com.shyl.msc.b2b.stl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="t_stl_account_product")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AccountProduct extends BasicEntity
{
  private static final long serialVersionUID = -76076139021650607L;
  private String month;
  private String productCode;
  private BigDecimal orderNum;
  private BigDecimal orderSum;
  private BigDecimal deliveryNum;
  private BigDecimal deliverySum;
  private BigDecimal inOutBoundNum;
  private BigDecimal inOutBoundSum;
  private BigDecimal returnsNum;
  private BigDecimal returnsSum;

  @Id
  @SequenceGenerator(name="generator", sequenceName="t_stl_account_product_s")
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generator")
  public Long getId()
  {
    return this.id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  @Column(length=10)
  public String getMonth() { return this.month; }

  public void setMonth(String month) {
    this.month = month;
  }
  @Column(length=50)
  public String getProductCode() { return this.productCode; }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }
  @Column(precision=16, scale=3)
  public BigDecimal getOrderNum() { return this.orderNum; }

  public void setOrderNum(BigDecimal orderNum) {
    this.orderNum = orderNum;
  }
  @Column(precision=16, scale=2)
  public BigDecimal getOrderSum() { return this.orderSum; }

  public void setOrderSum(BigDecimal orderSum) {
    this.orderSum = orderSum;
  }
  @Column(precision=16, scale=3)
  public BigDecimal getDeliveryNum() { return this.deliveryNum; }

  public void setDeliveryNum(BigDecimal deliveryNum) {
    this.deliveryNum = deliveryNum;
  }
  @Column(precision=16, scale=2)
  public BigDecimal getDeliverySum() { return this.deliverySum; }

  public void setDeliverySum(BigDecimal deliverySum) {
    this.deliverySum = deliverySum;
  }
  @Column(precision=16, scale=3)
  public BigDecimal getInOutBoundNum() { return this.inOutBoundNum; }

  public void setInOutBoundNum(BigDecimal inOutBoundNum) {
    this.inOutBoundNum = inOutBoundNum;
  }
  @Column(precision=16, scale=2)
  public BigDecimal getInOutBoundSum() { return this.inOutBoundSum; }

  public void setInOutBoundSum(BigDecimal inOutBoundSum) {
    this.inOutBoundSum = inOutBoundSum;
  }
  @Column(precision=16, scale=3)
  public BigDecimal getReturnsNum() { return this.returnsNum; }

  public void setReturnsNum(BigDecimal returnsNum) {
    this.returnsNum = returnsNum;
  }
  @Column(precision=16, scale=2)
  public BigDecimal getReturnsSum() { return this.returnsSum; }

  public void setReturnsSum(BigDecimal returnsSum) {
    this.returnsSum = returnsSum;
  }
}