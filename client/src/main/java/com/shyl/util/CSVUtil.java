package com.shyl.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;

public class CSVUtil {

	public static List<Map<String, Object>> csvToList(String filePath) throws IOException{
		CsvReader reader = new CsvReader(filePath, ',', Charset.forName("GBK"));
		int i=0;
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Object [] head = null;
		while (reader.readRecord()) {
			Object[] line = reader.getValues();
			i++;
			if(i==1){
				head = line;
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			for(int j=0; j<line.length; j++){
				map.put(head[j]+"", line[j]);
			}
			list.add(map);
		}
		return list;
	}
	

	public static List<Map<String, Object>> csvToList(InputStream in) throws IOException{
		CsvReader reader = new CsvReader(in, ',', Charset.forName("GBK"));
		int i=0;
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Object [] head = null;
		while (reader.readRecord()) {
			Object[] line = reader.getValues();
			i++;
			if(i==1){
				head = line;
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			for(int j=0; j<line.length; j++){
				map.put(head[j]+"", line[j]);
			}
			list.add(map);
		}
		return list;
	}
}
