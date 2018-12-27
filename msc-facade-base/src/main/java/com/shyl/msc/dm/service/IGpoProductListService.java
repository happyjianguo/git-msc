package com.shyl.msc.dm.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.GpoProductList;
import org.springframework.web.multipart.MultipartFile;

public interface IGpoProductListService extends IBaseService<GpoProductList, Long> {
	
	GpoProductList findByCode(@ProjectCodeFlag String projectCode, Long productId, String venderCode);
	
	GpoProductList getPrice(@ProjectCodeFlag String projectCode, Long productId);

	void doExcel(@ProjectCodeFlag String projectCode,String[][] upExcel);

}
