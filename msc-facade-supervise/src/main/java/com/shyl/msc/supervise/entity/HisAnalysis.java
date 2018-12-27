package com.shyl.msc.supervise.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.shyl.common.entity.BasicEntity;

@MappedSuperclass
@SuppressWarnings("serial")
public class HisAnalysis extends BasicEntity {
	/** 月份 **/
	public String month;
	/** 汇总标志 hospitalCode||departCode||yyyymm **/
	public String code;
	/** 基本药物数量 */
	public Integer baseDrugNum;
	/** 抗菌药物数量 */
	public Integer absDrugNum;
	/** 医院收入汇总 **/
	public BigDecimal sum;
	/** 药品收入汇总 **/
	public BigDecimal drugSum;
	/** 非药品收入汇总 **/
	public BigDecimal otherSum;
	/** 基本药物收入汇总 */
	public BigDecimal baseDrugSum;
	/** 总品规数 **/
	public Integer drugNum;
	/** 出院人数 */
	public Integer outNum;
	/** 住院抗菌药物使用人次 **/
	public Integer absNum;
	/** 住院天数人数 */
	public Integer daySum;
	/** ddd值累计 **/
	public BigDecimal dddSum;
	/** ddd强度（抗菌药物sum/住院天数*100） **/
	public BigDecimal dddIntensity;
	/** 特殊药品金额 **/
	private BigDecimal specialDrugSum;
	/** 特殊药品数量 **/
	private Integer specialDrugNum;
	/** 医保药品金额 **/
	private BigDecimal insuranceDrugSum;
	/** 医保药品数量 **/
	private Integer insuranceDrugNum;
	/** 抗菌药物收入汇总 */
	public BigDecimal absDrugSum;
	/** 抗菌药物收入汇总（非限制使用级) */
	public BigDecimal absType1Sum;
	/** 抗菌药物收入汇总（限制使用级) */
	public BigDecimal absType2Sum;
	/** 抗菌药物收入汇总（特殊使用级) */
	public BigDecimal absType3Sum;
	/** 抗菌药物处方数（非限制使用级) */
	public Integer absRecipe1Num;
	/** 抗菌药物处方数（限制使用级) */
	public Integer absRecipe2Num;
	/** 抗菌药物处方数（特殊使用级) */
	public Integer absRecipe3Num;

	/** 功效分类 **/

	public Integer getAbsNum() {
		return absNum;
	}

	public void setAbsNum(Integer absNum) {
		this.absNum = absNum;
	}

	public Integer getAbsRecipe1Num() {
		return absRecipe1Num;
	}

	public void setAbsRecipe1Num(Integer absRecipe1Num) {
		this.absRecipe1Num = absRecipe1Num;
	}

	public Integer getAbsRecipe2Num() {
		return absRecipe2Num;
	}

	public void setAbsRecipe2Num(Integer absRecipe2Num) {
		this.absRecipe2Num = absRecipe2Num;
	}

	public Integer getAbsRecipe3Num() {
		return absRecipe3Num;
	}

	public void setAbsRecipe3Num(Integer absRecipe3Num) {
		this.absRecipe3Num = absRecipe3Num;
	}

	public Integer getOutNum() {
		return outNum;
	}

	public Integer getDaySum() {
		return daySum;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public void setDrugSum(BigDecimal drugSum) {
		this.drugSum = drugSum;
	}

	public void setOtherSum(BigDecimal otherSum) {
		this.otherSum = otherSum;
	}

	public void setBaseDrugSum(BigDecimal baseDrugSum) {
		this.baseDrugSum = baseDrugSum;
	}

	public void setAbsDrugSum(BigDecimal absDrugSum) {
		this.absDrugSum = absDrugSum;
	}

	public void setAbsType1Sum(BigDecimal absType1Sum) {
		this.absType1Sum = absType1Sum;
	}

	public void setAbsType2Sum(BigDecimal absType2Sum) {
		this.absType2Sum = absType2Sum;
	}

	public void setAbsType3Sum(BigDecimal absType3Sum) {
		this.absType3Sum = absType3Sum;
	}

	public void setDrugNum(Integer drugNum) {
		this.drugNum = drugNum;
	}

	public void setOutNum(Integer outNum) {
		this.outNum = outNum;
	}

	public void setDaySum(Integer daySum) {
		this.daySum = daySum;
	}

	public void setDddSum(BigDecimal dddSum) {
		this.dddSum = dddSum;
	}

	public void setDddIntensity(BigDecimal dddIntensity) {
		this.dddIntensity = dddIntensity;
	}

	public BigDecimal getDddSum() {
		return dddSum;
	}

	public BigDecimal getDddIntensity() {
		return dddIntensity;
	}

	public Integer getDrugNum() {
		return drugNum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getAbsType1Sum() {
		return absType1Sum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getAbsType2Sum() {
		return absType2Sum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getAbsType3Sum() {
		return absType3Sum;
	}

	@Column(length = 10)
	public String getMonth() {
		return month;
	}

	@Column(length = 50, unique = true, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getSum() {
		return sum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getDrugSum() {
		return drugSum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getOtherSum() {
		return otherSum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getBaseDrugSum() {
		return baseDrugSum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getAbsDrugSum() {
		return absDrugSum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getSpecialDrugSum() {
		return specialDrugSum;
	}

	public Integer getSpecialDrugNum() {
		return specialDrugNum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getInsuranceDrugSum() {
		return insuranceDrugSum;
	}

	@Column(precision = 16, scale = 5)
	public Integer getInsuranceDrugNum() {
		return insuranceDrugNum;
	}

	public void setSpecialDrugSum(BigDecimal specialDrugSum) {
		this.specialDrugSum = specialDrugSum;
	}

	public void setSpecialDrugNum(Integer specialDrugNum) {
		this.specialDrugNum = specialDrugNum;
	}

	public void setInsuranceDrugSum(BigDecimal insuranceDrugSum) {
		this.insuranceDrugSum = insuranceDrugSum;
	}

	public void setInsuranceDrugNum(Integer insuranceDrugNum) {
		this.insuranceDrugNum = insuranceDrugNum;
	}

	@Column(precision = 16, scale = 5)
	public Integer getBaseDrugNum() {
		return baseDrugNum;
	}

	public void setBaseDrugNum(Integer baseDrugNum) {
		this.baseDrugNum = baseDrugNum;
	}

	public void setAbsDrugNum(Integer absDrugNum) {
		this.absDrugNum = absDrugNum;
	}

	@Column(precision = 16, scale = 5)
	public Integer getAbsDrugNum() {
		return absDrugNum;
	}
}
