package com.shyl.msc.b2b.plan.service;

import java.math.BigDecimal;
import java.util.List;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor;
import com.shyl.sys.entity.User;

public interface IDirectoryVendorService extends IBaseService<DirectoryVendor, Long> {

	void tender(@ProjectCodeFlag String projectCode, List<DirectoryVendor> fastjson, User currentUser);

	void dochoose(@ProjectCodeFlag String projectCode, Long id, Long productId, String status, BigDecimal score) throws Exception ;

}
