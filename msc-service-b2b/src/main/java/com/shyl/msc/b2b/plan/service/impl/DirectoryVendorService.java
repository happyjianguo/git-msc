package com.shyl.msc.b2b.plan.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.shyl.common.framework.annotation.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.plan.dao.IDirectoryVendorDao;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor.Status;
import com.shyl.msc.b2b.plan.service.IDirectoryVendorService;
import com.shyl.msc.dm.entity.GpoProductList;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IDirectoryService;
import com.shyl.msc.dm.service.IGpoProductListService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.msc.set.service.INoticeService;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IUserService;

@Service
@Transactional(readOnly=true)
public class DirectoryVendorService extends BaseService<DirectoryVendor, Long> implements IDirectoryVendorService {
	@Resource
	private IDirectoryService directoryService;
	@Resource
	private IProjectDetailService projectDetailService;
	@Resource
	private IUserService userService;
	@Resource
	private IProductService productService;
	@Resource
	private IGpoProductListService gpoProductListService;
	
	
	private IDirectoryVendorDao directoryVendorDao;

	public IDirectoryVendorDao getDirectoryVendorDao() {
		return directoryVendorDao;
	}

	@Resource
	public void setDirectoryVendorDao(IDirectoryVendorDao directoryVendorDao) {
		this.directoryVendorDao = directoryVendorDao;
		super.setBaseDao(directoryVendorDao);
	}

	@Override
	@Transactional
	public void tender(String projectCode, List<DirectoryVendor> fastjson,@CurrentUser User currentUser) {
		for (DirectoryVendor directoryVendor : fastjson) {
			if(directoryVendor.getProjectDetail() != null){
				System.out.println("directoryVendor.getProjectDetail().getId()="+directoryVendor.getProjectDetail().getId());
				System.out.println("projectDetailService"+projectDetailService);
				System.out.println("userService"+userService);
				User u = userService.getById(currentUser.getProjectCode(),currentUser.getId());
				System.out.println("getName"+u.getName());
				ProjectDetail pd = projectDetailService.getById(currentUser.getProjectCode(),directoryVendor.getProjectDetail().getId());
				PageRequest pageable = new PageRequest();
				pageable.getQuery().put("t#vendorCode_S_EQ", currentUser.getOrganization().getOrgCode());
				pageable.getQuery().put("t#projectDetail.id_L_EQ", pd.getId());
				DirectoryVendor dv = directoryVendorDao.getByKey(pageable);
				if(dv == null){
					directoryVendor.setVendorCode(currentUser.getOrganization().getOrgCode());
					directoryVendor.setVendorName(currentUser.getOrganization().getOrgName());
					directoryVendor.setProductName(pd.getDirectory().getGenericName());
					directoryVendor.setModel(pd.getDirectory().getModel());
					directoryVendor.setDeclareDate(new Date());
					directoryVendor.setStatus(Status.declare);
					directoryVendorDao.save(directoryVendor);
				}else{
					dv.setPrice(directoryVendor.getPrice());
					dv.setProducerCode(directoryVendor.getProducerCode());
					dv.setProducerName(directoryVendor.getProducerName());
					directoryVendorDao.update(dv);
				}
				
				//System.out.println("getProjectDetailId="+directoryVendor.getProjectDetail().getId());
				//更新项目明细的 投标供应商数量
				pageable = new PageRequest();
				pageable.getQuery().put("t#projectDetail.id_L_EQ", pd.getId());
				List<DirectoryVendor> l = directoryVendorDao.getAll(pageable);
				pd.setVendorNum(l.size());
				projectDetailService.update(currentUser.getProjectCode(),pd);
			}

			
		}
		
	}

	@Override
	@Transactional
	public void dochoose(String projectCode, Long id, Long productId, String status,BigDecimal avgScore) throws Exception {
		DirectoryVendor dvr = directoryVendorDao.getById(id);
		if(dvr==null){
			throw new Exception("申报资料不存在");
		}
		
		dvr.setCalibrationDate(new Date());
		
		if(status.equals("win")){
			if(productId == null){
				throw new Exception("对应药品不存在");
			}
			Product  p =productService.getById(projectCode, productId);
			p.setDirectoryId(dvr.getProjectDetail().getDirectory().getId());
			productService.update(projectCode,p);
			
			dvr.setProductId(productId);
			dvr.setStatus(Status.win);
			
			//更新 供应商药品价格
			GpoProductList gpl = gpoProductListService.findByCode(projectCode, productId, dvr.getVendorCode());
			if(gpl != null){
				gpl.setPrice(dvr.getPrice());
				gpoProductListService.update(projectCode,gpl);
			}else{
				gpl = new GpoProductList();
				gpl.setProduct(p);
				gpl.setVendorCode(dvr.getVendorCode());
				gpl.setVendorName(dvr.getVendorName());
				gpl.setPrice(dvr.getPrice());
				gpoProductListService.save(projectCode,gpl);
			}
			
		}else if(status.equals("unwin")){
			dvr.setStatus(Status.unwin);
		}else{
			throw new Exception("评标结果错误");
		}
		dvr.setAvgScore(avgScore);
		directoryVendorDao.update(dvr);
		
		
		
	}
	
	 
}
