package com.shyl.msc.dm.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.ProductRegister;
import com.shyl.sys.entity.User;

public interface IProductRegisterService extends IBaseService<ProductRegister, Long> {

	public void add(@ProjectCodeFlag String projectCode, User user, String rows);

}
