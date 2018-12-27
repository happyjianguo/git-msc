package com.shyl.msc.enmu;

/**
 * 招标状态
 */
public enum ProjectStus {
	create("项目建立"),
	report("报量中"),
	edit("报量结束"),
	tender("投标中"),
	eval("评标中"),
	done("招标完成");
	
	private String name;
	private ProjectStus(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
