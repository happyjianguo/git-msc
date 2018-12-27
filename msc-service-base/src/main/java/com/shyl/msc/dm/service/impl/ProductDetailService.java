package com.shyl.msc.dm.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import com.shyl.common.entity.Sort;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IProductDao;
import com.shyl.msc.dm.dao.IProductDetailDao;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.msc.dm.entity.ProductPriceRecord;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;
import com.shyl.msc.dm.entity.ProductPriceRecord.Type;
import com.shyl.msc.dm.service.IProductDetailService;
import com.shyl.msc.dm.service.IProductPriceRecordHisService;
import com.shyl.msc.dm.service.IProductPriceRecordService;
import com.shyl.msc.set.dao.ICompanyDao;
import com.shyl.msc.set.dao.IHospitalDao;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.entity.User;

@Service
@Transactional(readOnly=true)
public class ProductDetailService extends BaseService<ProductDetail, Long> implements IProductDetailService {

	@Resource
	private IProductDao productDao;
	@Resource
	private ICompanyDao companyDao;
	@Resource
	private IHospitalDao hospitalDao;
	@Resource
	private IProductPriceRecordService productPriceRecordService;
	@Resource
	private IProductPriceRecordHisService productPriceRecordHisService;
	
	private IProductDetailDao productDetailDao;
	
	public IProductDetailDao getProductDetailDao() {
		return productDetailDao;
	}

	@Resource
	public void setProductDetailDao(IProductDetailDao productDetailDao) {
		this.productDetailDao = productDetailDao;
		super.setBaseDao(productDetailDao);
	}

	@Override
	public ProductDetail getByKey(String projectCode, Long productId, String vendorCode, String hospitalCode) {
		return productDetailDao.getByKey(productId, vendorCode, hospitalCode);
	}

	@Override
	public DataGrid<ProductDetail> page(String projectCode, PageRequest pageRequest) {
		return productDetailDao.page(pageRequest);
	}

	@Override
	@Transactional
	public String importExcel(String projectCode, String[][] upExcel, User user) {
		int k=0;
		for (int i = 0; i < upExcel.length; i++) {
			String[] row = upExcel[i];
			String productCode = row[0];
			String vendorCode= row[1];
			String hospitalCode = row[2];
			if(StringUtils.isEmpty(productCode)){
				continue;
			}
			if(StringUtils.isEmpty(vendorCode)){
				continue;
			}
			if(StringUtils.isEmpty(hospitalCode)){
				continue;
			}
			Product product = productDao.getByCode(productCode);
			if(product == null){
				continue;
			}
			BigDecimal price = new BigDecimal(0);
			try {
				if(row[3].trim().equals(""))
					continue;
				price = new BigDecimal(row[3]);
			} catch (Exception e) {
				continue;
			}
			if(price.compareTo(new BigDecimal(0)) <= 0){
				continue;
			}
			Company vendor = companyDao.findByCode(vendorCode, "isVendor=1");
			Hospital hospital = hospitalDao.findByCode(hospitalCode);
			if(vendor == null){
				continue;
			}
			if(hospital == null){
				continue;
			}
			ProductDetail productDetail = productDetailDao.getByKey(product.getId(), vendorCode, hospitalCode);
			if(productDetail == null){
				productDetail = new ProductDetail();
				productDetail.setProduct(product);
				productDetail.setHospitalCode(hospitalCode);
				productDetail.setHospitalName(hospital.getFullName());
				productDetail.setVendorCode(vendorCode);
				productDetail.setVendorName(vendor.getFullName());
				productDetail.setPrice(price);
				productDetailDao.save(productDetail);
			}else{
				productDetail.setPrice(price);
				productDetailDao.update(productDetail);
			}
			
			//记录ProductPriceRecord
			recordPrice(projectCode,productDetail);
			
			k++;
		}
		return "导入"+k+"笔数据";
	}
	
	private void recordPrice(String projectCode,ProductDetail productDetail) {
		Product product = productDetail.getProduct();
		PageRequest pageable = new PageRequest();
		
		pageable.getQuery().put("t#productCode_S_EQ", product.getCode());
		pageable.getQuery().put("t#gpoCode_S_EQ", product.getGpoCode());
		pageable.getQuery().put("t#type_S_EQ",Type.gpo);
		ProductPriceRecord ppr = productPriceRecordService.getByKey(projectCode,pageable);
		//flag=1 新增his表
		int flag = 0;
		//未记录
		if(ppr == null){
			flag = 1;
			ppr = new ProductPriceRecord();
			ppr.setProductCode(product.getCode());
			ppr.setProductName(product.getName());
			ppr.setGpoCode(product.getGpoCode());
			ppr.setGpoName(product.getGpoName());
			ppr.setVendorCode(productDetail.getVendorCode());
			ppr.setVendorName(productDetail.getVendorName());
			ppr.setFinalPrice(productDetail.getPrice());
			ppr.setLastPrice(productDetail.getPrice());
			ppr.setPriceCount(1);
			ppr.setType(Type.gpo);
			productPriceRecordService.save(projectCode,ppr);
		}else if(ppr.getFinalPrice().compareTo(productDetail.getPrice())!=0){
			flag = 1;
			ppr.setVendorCode(productDetail.getVendorCode());
			ppr.setVendorName(productDetail.getVendorName());
			ppr.setLastPrice(ppr.getFinalPrice());
			ppr.setFinalPrice(productDetail.getPrice());
			ppr.setPriceCount(ppr.getPriceCount()+1);
			productPriceRecordService.update(projectCode,ppr);
		}
		if(flag == 1){
			ProductPriceRecordHis pprh = new ProductPriceRecordHis();
			pprh.setProductCode(product.getCode());
			pprh.setProductName(product.getName());
			pprh.setGpoCode(product.getGpoCode());
			pprh.setGpoName(product.getGpoName());
			pprh.setVendorCode(productDetail.getVendorCode());
			pprh.setVendorName(productDetail.getVendorName());
			pprh.setFinalPrice(productDetail.getPrice());
			pprh.setType(com.shyl.msc.dm.entity.ProductPriceRecordHis.Type.gpo);
			productPriceRecordHisService.save(projectCode,pprh);
		}
	}
	

	@Override
	public ProductDetail getByKey2(String projectCode, Long productId, String hospitalCode) {
		return productDetailDao.getByKey2( productId, hospitalCode);
	}

	@Override
	public List<ProductDetail> listByGPO(String projectCode, String gpoCode){
		return productDetailDao.listByGPO(gpoCode);
	}
	
	@Override
	public List<ProductDetail> listByVendor(String projectCode, String vendorCode){
		return productDetailDao.listByVendor(vendorCode);
	}

	@Override
	public List<ProductDetail> listByProductId(Sort sort, Long productId) {
		return productDetailDao.listByProductId(sort,productId);
	}

	@Override
	public ProductDetail getByKey3(String projectCode, String productCode, String vendorCode, String hospitalCode) {
		return productDetailDao.getByKey3(productCode, vendorCode, hospitalCode);
	}
}
