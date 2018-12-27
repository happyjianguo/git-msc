package com.shyl.msc.enmu;

public enum SubjectStus {

	create("创建");
	
	private String name;
	
	private SubjectStus(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
