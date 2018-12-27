package com.shyl.msc.dm.service.impl;

import javax.annotation.Resource;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.util.ExcelUtil;
import com.shyl.msc.dm.entity.*;
import com.shyl.msc.dm.service.*;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IGpoProductListDao;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly=true)
public class GpoProductListService extends BaseService<GpoProductList, Long> implements IGpoProductListService {

	@Resource
	private IProductService productService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IProductDetailService productDetailService;
	@Resource
	private IProductPriceRecordService productPriceRecordService;
	@Resource
	private IProductPriceRecordHisService productPriceRecordHisService;


	private IGpoProductListDao gpoProductListDao;
	
	public IGpoProductListDao getGpoProductListDao() {
		return gpoProductListDao;
	}

	@Resource
	public void setGpoProductListDao(IGpoProductListDao gpoProductListDao) {
		this.gpoProductListDao = gpoProductListDao;
		super.setBaseDao(gpoProductListDao);
	}

	public GpoProductList findByCode(String projectCode, Long productId, String venderCode) {
		return gpoProductListDao.findByCode(productId, venderCode);
	}

	@Override
	public GpoProductList getPrice(String projectCode, Long productId) {
		return gpoProductListDao.getPrice(productId);
	}

	@Override
	@Transactional
	public void doExcel(String projectCode, String[][] upExcel) {
		List<GpoProductList> saveList = new ArrayList<GpoProductList>();
		List<GpoProductList> updateList = new ArrayList<GpoProductList>();
		for(int i=0;i<upExcel.length;i++){
			String[] row = upExcel[i];
			if (StringUtils.isEmpty(row[0]) || StringUtils.isEmpty(row[1])) {
				throw new RuntimeException("第"+i+"条数据不能为空");
			}
			Product product = productService.getByCode(projectCode, row[0]);
			if (product == null) {
				throw new RuntimeException("第"+i+"条数据药品不存在");
			}
			Company company = companyService.findByCode1(projectCode, row[1]);
			if (company == null) {
				throw new RuntimeException("第"+i+"条数据供应商不存在");
			}
			GpoProductList list = gpoProductListDao.findByCode(product.getId(), company.getCode());
			BigDecimal lastPrice = new BigDecimal(0d);
			if (list == null) {
				list = new GpoProductList();
				list.setProduct(product);
				list.setVendorCode(company.getCode());
				list.setVendorName(company.getFullName());
				list.setPrice(BigDecimal.valueOf(Double.valueOf(row[2])));
				saveList.add(list);
			} else {
				lastPrice = list.getPrice();
				list.setPrice(BigDecimal.valueOf(Double.valueOf(row[2])));
				updateList.add(list);
			}
			//记录ProductPriceRecord
			recordPrice(projectCode,list,lastPrice);

		}
		if (saveList.size() > 0) {
			gpoProductListDao.saveBatch(saveList);
			updateProductDetail(projectCode,saveList);
		}
		if (updateList.size() > 0) {
			gpoProductListDao.updateBatch(updateList);
			updateProductDetail(projectCode,updateList);
		}
	}

	private void recordPrice(String projectCode,GpoProductList gpoProductList,BigDecimal lastPrice){
		List<ProductPriceRecord> productPriceRecords = new ArrayList<>();
		Product product = gpoProductList.getProduct();

		PageRequest pageable = new PageRequest();
		pageable.getQuery().put("t#productCode_S_EQ", product.getCode());
		pageable.getQuery().put("t#gpoCode_S_EQ", product.getGpoCode());
		pageable.getQuery().put("t#type_S_EQ", ProductPriceRecord.Type.gpo);
		ProductPriceRecord ppr = productPriceRecordService.getByKey(projectCode,pageable);

		//flag=1 新增his表
		int flag = 0;
		if(ppr == null){
			flag = 1;
			ppr = new ProductPriceRecord();
			ppr.setProductCode(product.getCode());
			ppr.setProductName(product.getName());
			ppr.setGpoCode(product.getGpoCode());
			ppr.setGpoName(product.getGpoName());
			ppr.setVendorCode(gpoProductList.getVendorCode());
			ppr.setVendorName(gpoProductList.getVendorName());
			ppr.setFinalPrice(gpoProductList.getPrice());
			ppr.setLastPrice(lastPrice);
			ppr.setPriceCount(1);
			ppr.setType(ProductPriceRecord.Type.gpo);
			ppr.setChangeDate(new Date());
			productPriceRecords.add(ppr);
			//productPriceRecordService.save(projectCode,ppr);
		}else if(ppr.getFinalPrice().compareTo(gpoProductList.getPrice())!=0){
			ppr.setVendorCode(gpoProductList.getVendorCode());
			ppr.setVendorName(gpoProductList.getVendorName());
			ppr.setLastPrice(lastPrice);
			ppr.setFinalPrice(gpoProductList.getPrice());
			ppr.setPriceCount(ppr.getPriceCount()+1);
			ppr.setChangeDate(new Date());
			productPriceRecords.add(ppr);
			//productPriceRecordService.save(projectCode,ppr);
		}
		productPriceRecordService.saveBatch(projectCode,productPriceRecords);
		List<ProductPriceRecordHis> productPriceRecordHiss = new ArrayList<>();
		if(flag == 1){
			ProductPriceRecordHis pprh = new ProductPriceRecordHis();
			pprh.setProductCode(product.getCode());
			pprh.setProductName(product.getName());
			pprh.setGpoCode(product.getGpoCode());
			pprh.setGpoName(product.getGpoName());
			pprh.setVendorCode(gpoProductList.getVendorCode());
			pprh.setVendorName(gpoProductList.getVendorName());
			pprh.setFinalPrice(gpoProductList.getPrice());
			pprh.setType(com.shyl.msc.dm.entity.ProductPriceRecordHis.Type.gpo);
			productPriceRecordHiss.add(pprh);
			//productPriceRecordHisService.save(projectCode,pprh);
		}
		productPriceRecordHisService.saveBatch(projectCode,productPriceRecordHiss);

	}

	private void updateProductDetail(String projectCode,List<GpoProductList> list){
		for (GpoProductList gpoproduct : list) {
			Long productId = gpoproduct.getProduct().getId();
			PageRequest page = new PageRequest();
			Map<String,Object> query = new HashMap<String,Object>();
			query.put("productId_S_EQ", productId);
			page.setQuery(query);
			List<ProductDetail> proList = productDetailService.list(projectCode,page);
			for (ProductDetail productDetail : proList) {
				productDetail.setPrice(gpoproduct.getPrice());
			}
			if(!proList.isEmpty()){
				productDetailService.updateBatch(projectCode,proList);
			}
		}

	}
}
