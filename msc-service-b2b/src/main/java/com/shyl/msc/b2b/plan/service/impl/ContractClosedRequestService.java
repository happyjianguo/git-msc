package com.shyl.msc.b2b.plan.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.service.IGoodsPriceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.plan.dao.IContractClosedRequestDao;
import com.shyl.msc.b2b.plan.dao.IContractDao;
import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest.ClosedObject;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractClosedRequestService;

@Service
@Transactional(readOnly=true)
public class ContractClosedRequestService extends BaseService<ContractClosedRequest, Long> implements IContractClosedRequestService {

	@Resource
	private IContractDao contractDao;
	@Resource
	private IContractDetailDao contractDetailDao;
	@Resource
	private IGoodsPriceService goodsPriceService;

	private IContractClosedRequestDao contractClosedRequestDao;

	public IContractClosedRequestDao getContractClosedRequestDao() {
		return contractClosedRequestDao;
	}

	@Resource
	public void setContractClosedRequestDao(IContractClosedRequestDao contractClosedRequestDao) {
		this.contractClosedRequestDao = contractClosedRequestDao;
		super.setBaseDao(contractClosedRequestDao);
	}

	@Override
	public ContractClosedRequest findByContract(String projectCode, String code) {
		return contractClosedRequestDao.findByContract(code);
	}

	@Override
	public DataGrid<ContractClosedRequest> queryByOrg(String projectCode, PageRequest pageable, String hospitalCode, String vendorCode, String gpoCode) {
		return contractClosedRequestDao.queryByOrg( pageable,  hospitalCode,  vendorCode, gpoCode);
	}

	@Override
	public List<Map<String, Object>> listByDate(String projectCode, String code, String cxkssj, String cxjssj, boolean isGPO) {
		return contractClosedRequestDao.listByDate(code, cxkssj, cxjssj, isGPO);
	}

	@Override
	public ContractClosedRequest findByCode(String projectCode, String code) {
		return contractClosedRequestDao.findByCode(code);
	}

	@Override
	@Transactional
	public void saveRequest(String projectCode, ContractClosedRequest contractClosedRequest) {
		contractClosedRequestDao.update(contractClosedRequest);
		if(contractClosedRequest.getStatus().equals(ContractClosedRequest.Status.agree)){
			if(contractClosedRequest.getClosedObject().equals(ClosedObject.contractDetail)) {
				ContractDetail cd =  contractDetailDao.findByCode(contractClosedRequest.getContractDetailCode());
				cd.setStatus(com.shyl.msc.b2b.plan.entity.ContractDetail.Status.stop);
				contractDetailDao.update(cd);
				updateGoodsPrice(projectCode, cd);
			} else {
				Contract contract = contractDao.findByCode(contractClosedRequest.getContractCode());
				contract.setStatus(Contract.Status.stop);
				contractDao.update(contract);
				List<ContractDetail> details = contractDetailDao.listByContractId(contract.getId());
				for (ContractDetail cd : details) {
					updateGoodsPrice(projectCode, cd);
				}
			}

		}
	}

	/**
	 * 判断是否价格需要被禁用
	 * @param projectCode
	 * @param cd
	 */
	private void updateGoodsPrice(String projectCode, ContractDetail cd) {
		List<ContractDetail> cds = contractDetailDao.listBySigned(cd.getProduct().getId(),
				cd.getContract().getHospitalCode(), cd.getContract().getVendorCode());
		if (cds.size() == 0) {
			GoodsPrice gp = goodsPriceService.findByKey(projectCode, cd.getProduct().getCode(),
					cd.getContract().getVendorCode(), cd.getContract().getHospitalCode(), null, null,1);
			if (gp!=null) {
				gp.setModifyDate(new Date());
				gp.setIsDisabled(1);
				goodsPriceService.update(projectCode, gp);
			}
		}
	}

	@Override
	public List<Map<String, Object>> listByCode(String projectCode, String code) {
		return contractClosedRequestDao.listByCode(code);
	}

	@Override
	public ContractClosedRequest findByContractDetail(String projectCode, String code) {
		return contractClosedRequestDao.findByContractDetail(code);
	}
	

}
