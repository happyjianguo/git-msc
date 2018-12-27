package com.shyl.msc.b2b.stl.service;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stl.entity.AccountProduct;

import java.util.List;
import java.util.Map;

public abstract interface IAccountProductService extends IBaseService<AccountProduct, Long>
{
  void passAccount(@ProjectCodeFlag String paramString);

  void passAccountBetter (@ProjectCodeFlag String projectCode);

  public abstract DataGrid<Map<String, Object>> reportHospitalSB(@ProjectCodeFlag String paramString1, String paramString2, String paramString3, String paramString4, PageRequest paramPageRequest);

  public abstract List<Map<String, Object>> reportTrade(@ProjectCodeFlag String paramString1, String paramString2);

  public abstract DataGrid<Map<String, Object>> reportGoodsTrade(@ProjectCodeFlag String paramString1, String paramString2, String paramString3, PageRequest paramPageRequest);
}