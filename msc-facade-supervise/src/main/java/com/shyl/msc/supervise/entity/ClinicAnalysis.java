package com.shyl.msc.supervise.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.shyl.common.entity.BasicEntity;

@MappedSuperclass
@SuppressWarnings("serial")
public class ClinicAnalysis extends BasicEntity {

	public enum ClinicType {
		/** 普通门诊 **/
		ordinary,
		/** 紧急门诊 **/
		urgent;
	}

	/** 汇总标志 hospitalCode||departCode||yyyymm **/
	public String code;
	/** 基本药物数量 */
	public Integer baseDrugNum;
	/** 抗菌药物数量 */
	public Integer absDrugNum;
	/** ddd值累计 **/
	public BigDecimal dddSum;
	/** 总品规数 **/
	public Integer drugNum;
	/** 月份 **/
	public String month;
	/** 医院收入汇总 **/
	public BigDecimal sum;
	/** 药品收入汇总 **/
	public BigDecimal drugSum;
	/** 非药品收入汇总 **/
	public BigDecimal otherSum;
	/** 基本药物收入汇总 */
	public BigDecimal baseDrugSum;
	/** 抗菌药物收入汇总 */
	public BigDecimal absDrugSum;
	/** 处方数 **/
	public Integer recipeNum;
	/** 就诊处方人次 **/
	public Integer regRecipeNum;
	/** 基本药物处方数 */
	public Integer baseRecipeNum;
	/** 抗菌药物处方数 */
	public Integer absRecipeNum;
	/** 访问人数 */
	public Integer visitorNum;
	/** 含注射剂处方数 **/
	public Integer injectionNum;
	/** 含注射剂处方数 **/
	public Integer intraInjectionNum;
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
	/** 特殊药品金额 **/
	private BigDecimal specialDrugSum;
	/** 特殊药品数量 **/
	private BigDecimal specialDrugNum;
	/** 医保药品金额 **/
	private BigDecimal insuranceDrugSum;
	/** 医保药品数量 **/
	private Integer insuranceDrugNum;
	/** 门诊类别 **/
	public ClinicType clinicType;

	public Integer getIntraInjectionNum() {
		return intraInjectionNum;
	}

	public void setIntraInjectionNum(Integer intraInjectionNum) {
		this.intraInjectionNum = intraInjectionNum;
	}

	public Integer getInjectionNum() {
		return injectionNum;
	}

	public void setInjectionNum(Integer injectionNum) {
		this.injectionNum = injectionNum;
	}

	public Integer getAbsRecipeNum() {
		return absRecipeNum;
	}

	public void setAbsRecipeNum(Integer absRecipeNum) {
		this.absRecipeNum = absRecipeNum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getDrugSum() {
		return drugSum;
	}

	public void setDrugSum(BigDecimal drugSum) {
		this.drugSum = drugSum;
	}

	public Integer getRecipeNum() {
		return recipeNum;
	}

	public Integer getBaseRecipeNum() {
		return baseRecipeNum;
	}

	public void setRecipeNum(Integer recipeNum) {
		this.recipeNum = recipeNum;
	}

	public void setBaseRecipeNum(Integer baseRecipeNum) {
		this.baseRecipeNum = baseRecipeNum;
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

	public void setAbsRecipe1Num(Integer absRecipe1Num) {
		this.absRecipe1Num = absRecipe1Num;
	}

	public void setAbsRecipe2Num(Integer absRecipe2Num) {
		this.absRecipe2Num = absRecipe2Num;
	}

	public void setAbsRecipe3Num(Integer absRecipe3Num) {
		this.absRecipe3Num = absRecipe3Num;
	}

	public BigDecimal getAbsType1Sum() {
		return absType1Sum;
	}

	public BigDecimal getAbsType2Sum() {
		return absType2Sum;
	}

	public BigDecimal getAbsType3Sum() {
		return absType3Sum;
	}

	public Integer getAbsRecipe1Num() {
		return absRecipe1Num;
	}

	public Integer getAbsRecipe2Num() {
		return absRecipe2Num;
	}

	public Integer getAbsRecipe3Num() {
		return absRecipe3Num;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getSum() {
		return sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	@Column(length = 50, unique = true, nullable = false, updatable = false)
	public String getCode() {
		return code;
	}

	@Column(length = 10)
	public String getMonth() {
		return month;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getSpecialDrugSum() {
		return specialDrugSum;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getSpecialDrugNum() {
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

	@Column(precision = 16, scale = 5)
	public Integer getBaseDrugNum() {
		return baseDrugNum;
	}

	@Column(precision = 16, scale = 5)
	public Integer getAbsDrugNum() {
		return absDrugNum;
	}

	public void setAbsDrugNum(Integer absDrugNum) {
		this.absDrugNum = absDrugNum;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setBaseDrugNum(Integer baseDrugNum) {
		this.baseDrugNum = baseDrugNum;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	@Column(precision = 16, scale = 5)
	public BigDecimal getDddSum() {
		return dddSum;
	}

	public void setDddSum(BigDecimal dddSum) {
		this.dddSum = dddSum;
	}

	public Integer getDrugNum() {
		return drugNum;
	}

	public void setDrugNum(Integer drugNum) {
		this.drugNum = drugNum;
	}

	public void setSpecialDrugSum(BigDecimal specialDrugSum) {
		this.specialDrugSum = specialDrugSum;
	}

	public void setSpecialDrugNum(BigDecimal specialDrugNum) {
		this.specialDrugNum = specialDrugNum;
	}

	public void setInsuranceDrugSum(BigDecimal insuranceDrugSum) {
		this.insuranceDrugSum = insuranceDrugSum;
	}

	public void setInsuranceDrugNum(Integer insuranceDrugNum) {
		this.insuranceDrugNum = insuranceDrugNum;
	}

	public ClinicType getClinicType() {
		return clinicType;
	}

	public void setClinicType(ClinicType clinicType) {
		this.clinicType = clinicType;
	}

	@Column(precision = 16, scale = 5)
	public Integer getVisitorNum() {
		return visitorNum;
	}

	public void setVisitorNum(Integer visitorNum) {
		this.visitorNum = visitorNum;
	}

	public Integer getRegRecipeNum() {
		return regRecipeNum;
	}

	public void setRegRecipeNum(Integer regRecipeNum) {
		this.regRecipeNum = regRecipeNum;
	}

}
