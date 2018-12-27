package com.shyl.msc.b2b.plan.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.plan.dao.IContractDao;
import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.Msg;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.service.IMsgService;
import com.shyl.sys.service.IOrganizationService;

@Service
@Transactional(readOnly=true)
public class ContractDetailService extends BaseService<ContractDetail, Long> implements IContractDetailService {

	private IContractDetailDao contractDetailDao;

	public IContractDetailDao getContractDetailDao() {
		return contractDetailDao;
	}

	@Resource
	public void setContractDetailDao(IContractDetailDao contractDetailDao) {
		this.contractDetailDao = contractDetailDao;
		super.setBaseDao(contractDetailDao);
	}
	@Resource
	private IGoodsPriceService goodsPriceService;
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IContractDao contractDao;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IOrganizationService organizationService;
	@Resource
	private IMsgService msgService;
	
	@Override
	public List<ContractDetail> listByContractId(String projectCode, Long contractId) {
		return contractDetailDao.listByContractId(contractId);
	}

	@Override
	public List<ContractDetail> findByPID(String projectCode, Long id) {
		return contractDetailDao.findByPID(id);
	}

	@Override
	public List<ContractDetail> listByHospitalCode(String projectCode, Sort sort, String hospitalCode) {
		return contractDetailDao.listByHospitalCode(sort, hospitalCode);
	}

	@Override
	public ContractDetail getByKey(String projectCode, Long productId, String hospitalCode, String gpoCode, String vendorCode) {
		return contractDetailDao.getByKey(productId, hospitalCode, gpoCode, vendorCode);
	}
	
	@Override
	public DataGrid<ContractDetail> queryByH(String projectCode, String hospitalCode, PageRequest pageable) {
		return contractDetailDao.queryByH(hospitalCode, pageable);
	}

	@Override
	public List<Map<String, Object>> listByHospitalSigned(String projectCode, String hospitalCode) {
		return contractDetailDao.listByHospitalSigned(hospitalCode);
	}

	@Override
	public List<ContractDetail> findOlder(String projectCode, String hospitalCode, String vendorCode, Long productId) {
		return contractDetailDao.findOlder(hospitalCode, vendorCode, productId);
	}
	
	@Override
	@Transactional
	public void contractToGoodsPrice(String projectCode) {
		//1、将goodsprice.isFormContract=1的数据作废
		List<GoodsPrice> gplist = goodsPriceService.listByIsFormContract(projectCode);
		for (GoodsPrice goodsPrice : gplist) {
			goodsPrice.setIsDisabled(1);
			goodsPriceService.update(projectCode, goodsPrice);
		}
		//2、查询符合要求的  contractDetail
		List<ContractDetail> cdlist = contractDetailDao.listBySigned();
		System.out.println("cdlist ===="+cdlist.size());
		//3、将contractDetail转为 goodsprice
		for (ContractDetail cd : cdlist) {
			Contract c = cd.getContract();
			Product product = cd.getProduct();
			GoodsPrice gp = goodsPriceService.findByKey(projectCode, product.getCode(), c.getVendorCode(), c.getHospitalCode(), null, null,1);
			
			if(gp == null){
				Goods g = goodsService.getByProductCodeAndHosiptal(projectCode, product.getCode(), c.getHospitalCode());
				if(g == null){
					g = new Goods();
					g.setProduct(product);
					g.setProductCode(product.getCode());
					g.setHospitalCode(c.getHospitalCode());
					g = goodsService.save(projectCode, g);
				}
				gp = new GoodsPrice();
				gp.setGoodsId(g.getId());
				gp.setProductCode(product.getCode());
				gp.setHospitalCode(c.getHospitalCode());
				gp.setVendorCode(c.getVendorCode());
				gp.setVendorName(c.getVendorName());
				gp.setIsFormContract(1);
				gp.setFinalPrice(cd.getPrice());
				gp.setBiddingPrice(cd.getPrice());
				gp.setBeginDate(c.getStartValidDate());
				gp.setOutDate(c.getEndValidDate());
				gp.setIsDisabled(0);
				gp.setIsDisabledByH(0);
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				gp.setEffectDate(fmt.format(c.getEffectiveDate()));
				goodsPriceService.save(projectCode, gp);
				System.out.println("gp setProductCode ===="+product.getCode());
			}else{
				gp.setIsDisabled(0);
				gp.setIsDisabledByH(0);
				gp.setFinalPrice(cd.getPrice());
				gp.setBiddingPrice(cd.getPrice());
				gp.setBeginDate(c.getStartValidDate());
				gp.setOutDate(c.getEndValidDate());
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				gp.setEffectDate(fmt.format(c.getEffectiveDate()));
				goodsPriceService.update(projectCode, gp);
			}
		}
	}

	@Override
	public void contractToGoodsPrice(String projectCode, Contract contract) {
		List<ContractDetail> cdlist = contractDetailDao.findByPID(contract.getId());
		System.out.println("cdlist ===="+cdlist.size());
		//3、将contractDetail转为 goodsprice
		for (ContractDetail cd : cdlist) {
			Contract c = cd.getContract();
			Product product = cd.getProduct();
			GoodsPrice gp = goodsPriceService.findByKey(projectCode, product.getCode(), c.getVendorCode(), c.getHospitalCode(), null, null,1);
			
			if(gp == null){
				Goods g = goodsService.getByProductCodeAndHosiptal(projectCode, product.getCode(), c.getHospitalCode());
				if(g == null){
					g = new Goods();
					g.setProduct(product);
					g.setProductCode(product.getCode());
					g.setHospitalCode(c.getHospitalCode());
					g.setModifyDate(new Date());
					g.setIsDisabled(0);
					g = goodsService.save(projectCode, g);
				}
				gp = new GoodsPrice();
				gp.setGoodsId(g.getId());
				gp.setProductCode(product.getCode());
				gp.setHospitalCode(c.getHospitalCode());
				gp.setVendorCode(c.getVendorCode());
				gp.setVendorName(c.getVendorName());
				gp.setIsFormContract(1);
				gp.setFinalPrice(cd.getPrice());
				gp.setBiddingPrice(cd.getPrice());
				gp.setBeginDate(c.getStartValidDate());
				gp.setOutDate(c.getEndValidDate());
				if(c.getEffectiveDate() != null){
					SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
					gp.setEffectDate(fmt.format(c.getEffectiveDate()));
				}
				gp.setIsDisabled(0);
				gp.setModifyDate(new Date());
				goodsPriceService.save(projectCode, gp);
				System.out.println("gp setProductCode ===="+product.getCode());
			}else{
				gp.setIsDisabled(0);
				gp.setIsDisabledByH(0);
				gp.setFinalPrice(cd.getPrice());
				gp.setBiddingPrice(cd.getPrice());
				gp.setBeginDate(c.getStartValidDate());
				gp.setOutDate(c.getEndValidDate());
				if(c.getEffectiveDate() != null){
					SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
					gp.setEffectDate(fmt.format(c.getEffectiveDate()));
				}
				
				goodsPriceService.update(projectCode, gp);
			}
		}
	}

	@Override
	@Transactional
	public void gpoCheck(String projectCode, Long id) {
		Contract c= contractDao.getById(id);
		c.setVendorConfirmDate(new Date());
		c.setStatus(Status.signed);
		c = contractDao.update(c);
	
		List<ContractDetail> cdlist = contractDetailDao.findByPID(c.getId());
		for (ContractDetail contractDetail : cdlist) {
			contractDetail.setStatus(com.shyl.msc.b2b.plan.entity.ContractDetail.Status.effect);
			contractDetailDao.update(contractDetail);
		}
		sendMsg(projectCode, c);
		this.contractToGoodsPrice(projectCode, c);
	}
	@Override
	@Transactional
	public void gpoCheck(String projectCode, Contract c) {
		c = contractDao.update(c);
		
		List<ContractDetail> cdlist = contractDetailDao.findByPID(c.getId());
		for (ContractDetail contractDetail : cdlist) {
			contractDetail.setStatus(com.shyl.msc.b2b.plan.entity.ContractDetail.Status.effect);
			contractDetailDao.update(contractDetail);
		}
		sendMsg(projectCode, c);
		this.contractToGoodsPrice(projectCode, c);
	}
	
	private void sendMsg(String projectCode, Contract c) {
		Msg msg = new Msg();
		msg.setIds("00");
		msg.setCaty("0");
		if(c.getStatus().equals(Status.rejected)){
			msg.setTitle("合同被供应商拒签。-- "+c.getCode());
		}else if(c.getStatus().equals(Status.signed)){
			msg.setTitle("供应商签订完成。-- "+c.getCode());
		}
		
		msg.setAttach("/hospital/contract.htmlx?code="+c.getCode());
		Hospital hospital = hospitalService.findByCode(projectCode, c.getHospitalCode());
		Company company = companyService.findByCode(projectCode,c.getVendorCode(), "isVendor=1");
		//查询医院的orgId
		Organization oh = organizationService.getByOrgId(projectCode, hospital.getId(), 1);
		//查询供应商的orgId
		Organization og = organizationService.getByOrgId(projectCode, company.getId(), 2);
		if(og != null){
			msg.setOrganizationId(og.getId());
			msg.setOrgName(og.getOrgName());
		}
		Long ohId = Long.parseLong("-1");
		if(oh != null){
			ohId = oh.getId();
		}
		try {
			msgService.sendBySYSToOrg(projectCode, msg, ohId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public DataGrid<Map<String, Object>> tradeByProduct(String projectCode, PageRequest pageable, String startDate, String endDate) {
		return contractDetailDao.tradeByProduct(pageable, startDate, endDate);
	}

	@Override
	public DataGrid<Map<String, Object>> tradeDetailByProduct(String projectCode, PageRequest pageable, String startDate, String endDate) {
		return contractDetailDao.tradeDetailByProduct(pageable, startDate, endDate);
	}

	@Override
	public DataGrid<ContractDetail> pageByHospitalCode(String projectCode, PageRequest pageRequest, String hospitalCode) {
		return contractDetailDao.pageByHospitalCode(pageRequest, hospitalCode);
	}

	@Override
	public List<ContractDetail> listBySigned(String projectCode, String hospitalCode) {
		return contractDetailDao.listBySigned(hospitalCode);
	}

	@Override
	public ContractDetail findByCode(String projectCode, String code) {
		return contractDetailDao.findByCode(code);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(String projectCode, PageRequest pageable) {
		return contractDetailDao.pageByProductReport(pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(String projectCode, PageRequest pageable) {
		return contractDetailDao.pageByProductDetailReport(pageable);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(String projectCode, PageRequest pageable) {
		return contractDetailDao.listByProductReport(pageable);
	}
	@Override
	public List<ContractDetail> listByExecution(String projectCode, PageRequest pageable) {
		return contractDetailDao.listByExecution(pageable);
	}

	@Override
//	@Cacheable("contractDetail")
	public DataGrid<ContractDetail> pageByExecution(String projectCode, PageRequest pageable) {
		return contractDetailDao.pageByExecution(pageable);
	}

	@Override
	public List<ContractDetail> listUnClosedByHospital(String projectCode, String hospitalCode) {
		return contractDetailDao.listUnClosedByHospital(hospitalCode);
	}
	
	@Override
	public List<ContractDetail> listByIsPass(String projectCode, int isPass){
		return contractDetailDao.listByIsPass(isPass);
	}

	@Override
	public String checkGPOContractNum(String projectCode, String hospitalCode, Map<String, JSONObject> slmap) {
		Map<String, List<ContractDetail>> contractDetailmap = getDetailMap(hospitalCode);
		
		StringBuffer strBuf = new StringBuffer();
		for(String key: slmap.keySet()) {
			JSONObject jo = slmap.get(key);
			String ypbm = jo.getString("ypbm");
			String gysbm = jo.getString("gysbm");
			String ypmc = jo.getString("ypmc");
			BigDecimal cgsl = jo.getBigDecimal("cgsl");
			
			List<ContractDetail> cdlist = contractDetailmap.get(hospitalCode+"_"+gysbm+"_"+ypbm);
			BigDecimal planNum = new BigDecimal(0);
			boolean f = false;
			if(cdlist != null){
				for (int i = 0; i < cdlist.size(); i++) {
					ContractDetail cd = cdlist.get(i);
					BigDecimal l = cd.getContractNum().subtract(cd.getPurchasePlanNum());
					if(l.compareTo(cgsl)>=0){//足够
						f = true;
					}else{//不足
						cgsl = cgsl.subtract(l);
						planNum = planNum.add(l);
					}
				}
			}
			
			GoodsPrice goodsPrice = goodsPriceService.findByKey(projectCode, ypbm, gysbm, hospitalCode, 0, 0, 0);
			if(goodsPrice != null){
				f = true;
			}
			if(!f) {
				String msg = "药品"+ypmc+"("+ypbm+")合同余量不足";
				if (planNum.intValue()>0d) {
					msg+="，可购量为"+planNum.intValue() + ";";
				} else {
					msg+= ";";
				}
				strBuf.append(msg);
			}
		}
		return strBuf.toString();
	}

	
	private Map<String, List<ContractDetail>> getDetailMap(String hospitalCode) {
		List<ContractDetail> contractDetails = this.listBySigned(null, hospitalCode);
		Map<String, List<ContractDetail>> map = new HashMap<>();
		for(ContractDetail contractDetail:contractDetails){
			String key = contractDetail.getContract().getHospitalCode()
					+"_"+contractDetail.getContract().getVendorCode()
					+"_"+contractDetail.getProduct().getCode();

			List<ContractDetail> cds =  map.get(key);
			if(map.get(key) == null){
				cds = new ArrayList<>();
			}
			cds.add(contractDetail);
			map.put(key, cds);
		}
		return map;
	}

	@Override
//	@Cacheable("contractServiceDetail")
	public DataGrid<ContractDetail> mxlist(String projectCode, PageRequest page) {
		return contractDetailDao.query(page);
	}
}
