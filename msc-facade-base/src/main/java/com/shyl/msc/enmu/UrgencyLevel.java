package com.shyl.msc.enmu;

public enum UrgencyLevel {
	urgent("紧急"),noturgent("不紧急");
	
	private String name;
	private UrgencyLevel(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
