package com.shyl.msc.dm.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.DirectoryPrice;
import com.shyl.msc.dm.entity.DirectoryPriceRecord;

public interface IDirectoryPriceService extends IBaseService<DirectoryPrice,Long>{

	void doSave(@ProjectCodeFlag String projectCode, DirectoryPriceRecord directoryPriceRecord);

}
