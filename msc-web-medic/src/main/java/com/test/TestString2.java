package com.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestString2 {
	
	public static void main(String[] args) throws IOException {
		InputStreamReader isr = new InputStreamReader(new FileInputStream("G:\\国产药品目录.txt"), "gbk");
		BufferedReader read = new BufferedReader(isr);
		String data="";
		String s="";
		while((data=read.readLine())!=null){
		s+=data;
		}
		System.out.println(s);
	}
	
}
