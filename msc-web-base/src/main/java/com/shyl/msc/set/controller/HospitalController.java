package com.shyl.msc.set.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IOrganizationService;
/**
 * 医疗机构Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/set/hospital")
public class HospitalController extends BaseController {

	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private  IOrganizationService organizationService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	@RequestMapping(value="/getHospital")
	@ResponseBody
	public List<Map<String, Object>> getHospital(@CurrentUser User user){
		return hospitalService.listAll(user.getProjectCode());
	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "set/hospital/list";
	}
	@RequestMapping("view")
	public String view(){
		return "set/hospital/view";
	}
	/**
	 * 全部查询
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<Hospital> list(PageRequest pageable, @CurrentUser User user){
		List<Hospital> list =  hospitalService.list(user.getProjectCode(), pageable);
		return list;
	}
	/**
	 * 全部查询
	 * @return
	 */
	@RequestMapping("/listByCounty")
	@ResponseBody
	public List<Hospital> listByCounty(PageRequest pageable,Long province, Long city, Long county, @CurrentUser User user){
		List<Hospital> list =  hospitalService.list(user.getProjectCode(), pageable, province, city, county);
		return list;
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Hospital> homePage(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"code"));
		pageable.setSort(sort);
		DataGrid<Hospital> page = (DataGrid<Hospital>)hospitalService.query(user.getProjectCode(), pageable);
		for (Hospital h : page.getRows()) {
			RegionCode rc = regionCodeService.getById(user.getProjectCode(), h.getRegionCode());
			if(rc != null){
				System.out.println(rc.getFullName());
				//暂借 五笔简称wbcode 存 地区中文名
				h.setWbcode(rc.getFullName());
			}
		}
		return page;
	}
	
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model){
		Double d = Math.random()*100000000;
		BigDecimal b = new BigDecimal(d);
		b = b.divide(new BigDecimal(1), 0,4);
		String a = new BigDecimal("100000000").add(b).toString().substring(1);
		model.addAttribute("iocode", a);
		return "set/hospital/add";
	}
	/**
	 * 新增
	 * @param hospital
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Hospital hospital,Long combox1,Long combox2,Long combox3, @CurrentUser User user){
		Message message = new Message();		
		try{
			if(combox3 != null){
				hospital.setRegionCode(combox3);
			}else if(combox2 != null){
				hospital.setRegionCode(combox2);
			}else if(combox1 != null){
				hospital.setRegionCode(combox1);
			}
			hospital = hospitalService.save(user.getProjectCode(), hospital);	
			
			//新增organization
			Organization org = new Organization();
			org.setOrgCode(hospital.getCode());
			org.setOrgType(1);
			org.setOrgName(hospital.getFullName());
			org.setOrgId(hospital.getId());
			organizationService.save(user.getProjectCode(), org);
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Hospital hospital, Model model, @CurrentUser User user){
		if(hospital !=null){
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
			if(regionCode != null){
				String treePath = regionCode.getTreePath();
				if(treePath == null){
					treePath = regionCode.getId()+"";
				}else{
					treePath += ","+regionCode.getId();
				}
				System.out.println("treePath = "+treePath);
				String[] arr = treePath.split(",");
				for (int i = 0; i < arr.length; i++) {
					model.addAttribute("combox"+(i+1), arr[i]);
				}
			}
		}
		return "set/hospital/edit";
	}
	/**
	 * 修改
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Hospital hospital,Long combox1,Long combox2,Long combox3,@CurrentUser User user){
		Message message = new Message();	
		try{
			if(combox3 != null){
				hospital.setRegionCode(combox3);
			}else if(combox2 != null){
				hospital.setRegionCode(combox2);
			}else if(combox1 != null){
				hospital.setRegionCode(combox1);
			}
			hospital = hospitalService.checkAndUpdate(user.getProjectCode(), hospital);		
			
			//修改organization
			Organization org = organizationService.getByOrgId(user.getProjectCode(), hospital.getId(),1);
			if(org != null){
				org.setOrgCode(hospital.getCode());
				org.setOrgName(hospital.getFullName());
				organizationService.update(user.getProjectCode(), org);
			}else{
				org = new Organization();
				org.setOrgCode(hospital.getCode());
				org.setOrgType(1);
				org.setOrgName(hospital.getFullName());
				org.setOrgId(hospital.getId());
				organizationService.save(user.getProjectCode(), org);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return message;
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user){
		Message message = new Message();
		
		try{
			hospitalService.delete(user.getProjectCode(), id);
			Organization org = organizationService.getByOrgId(user.getProjectCode(), id,1);
			if(org != null)
				organizationService.delete(user.getProjectCode(), org);
			message.setMsg("删除成功");
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return message;
	}

}
