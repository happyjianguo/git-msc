package com.shyl.msc.enmu;

public enum OrderDetailStatus {
	create("订单建立"),
	agree("订单审核通过"),
	disagree("订单审核不通过"),
	sent("药品已发出"),
	receive("药品已到配送点"),
	fetch("取药完成"),
	returns("已退货");
	private String name;
	private OrderDetailStatus(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
