package com.shyl.msc.client.service;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.PageRequest;

public interface IProductCloudService {
	/**
	 * 查询创业云数据
	 * @param page
	 * @return
	 */
	public JSONObject queryCloudByPage(PageRequest page);
	
}
