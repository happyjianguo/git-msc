package com.shyl.msc.b2b.stl.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.entity.SettlementDetail;
import com.shyl.sys.entity.User;
/**
 * 结算单
 * 
 * @author a_Q
 *
 */
public interface ISettlementService extends IBaseService<Settlement, Long> {

	/**
	 * 生成结算单
	 * @param settlement
	 * @param invoiceids
	 * @param hospitalId
	 * @param user
	 * @return
	 */
	String mkSettlement(@ProjectCodeFlag String projectCode, Settlement settlement, Long[] invoiceids, String hospitalCode, User user);

	public void saveSettlement(@ProjectCodeFlag String projectCode, Settlement settlement);

	public Settlement findByCode(@ProjectCodeFlag String projectCode, String code);

	public JSONArray saveSettlement(@ProjectCodeFlag String projectCode, Settlement settlement, List<JSONObject> arr);

	public Settlement getByInternalCode(@ProjectCodeFlag String projectCode, String code, String internalCode, Boolean isGPO);

	List<SettlementDetail> listBySettlementId(@ProjectCodeFlag String projectCode, Long id);
}
