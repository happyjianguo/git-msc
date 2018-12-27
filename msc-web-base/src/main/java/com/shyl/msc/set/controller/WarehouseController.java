package com.shyl.msc.set.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.NumberUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.Warehouse;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IOrganizationService;
/**
 * 库房Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/set/warehouse")
public class WarehouseController extends BaseController {

	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IOrganizationService organizationService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "set/warehouse/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Warehouse> homePage(PageRequest pageable, @CurrentUser User user){
		DataGrid<Warehouse> page = new DataGrid<Warehouse>();
		if(user.getOrganization().getOrgId() != null){
			Map<String, Object> query = pageable.getQuery();
			String status = (String)query.get("t#type_S_EQ");
			if(!StringUtils.isEmpty(status)){
				query.put("t#type_S_EQ", Warehouse.Type.valueOf(status));
			}
			Integer type = user.getOrganization().getOrgType();
			Long id = Long.valueOf(user.getOrganization().getOrgId());
			if(type == 1){
				page = warehouseService.pageByHospital(user.getProjectCode(), pageable, id);
			}else if(type == 3 || type  == 5 || type == 9){
				page = warehouseService.query(user.getProjectCode(), pageable);
			}
		}
		return page;
	}
	
	/**
	 * 进入新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(@CurrentUser User user, Model model){
		Integer type = user.getOrganization().getOrgType();
		Long id = Long.valueOf(user.getOrganization().getOrgId());
		if(type == 1){
			Hospital hospital = hospitalService.getById(user.getProjectCode(), id);
			model.addAttribute("hospital", hospital);
			model.addAttribute("code", getWarehouseCode(user.getProjectCode(), hospital.getCode()));
		}
		model.addAttribute("orgType", type);
		return "set/warehouse/add";
	}
	
	private String getWarehouseCode(String projectCode, String hospitalCode) {
		Warehouse warehouse = warehouseService.getLast(projectCode, hospitalCode);
		if(warehouse == null){
			return hospitalCode+"01";
		}
		String code = warehouse.getCode();
		String[] num = code.split(hospitalCode);
		if(num.length >1 ){
			String serNumS = num[1];
			Integer serNum = null;
			try {
				serNum = Integer.valueOf(serNumS);
			} catch (Exception e) {
				e.printStackTrace();
				serNum = null;
			}
			if(serNum != null && serNumS.length() == 2){
				serNum++;
				return hospitalCode+NumberUtil.addZeroForNum(serNum.toString(), 2);
			}
		}
		return hospitalCode+"01";
	}

	/**
	 * 新增
	 * @param warehouse
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Warehouse warehouse,Long hospitalId, @CurrentUser User user){
		Message message = new Message();		
		try{
			if(user.getOrganization().getOrgId() != null){
				Integer type = user.getOrganization().getOrgType();
				if(type == 1 || type == 5 || type == 9){
					Warehouse obj = warehouseService.queryByCodeAndPid(user.getProjectCode(), warehouse.getCode(),  hospitalId);
					if(obj == null){
						Hospital hospital = hospitalService.getById(user.getProjectCode(), hospitalId);
						warehouse.setHospital(hospital);
					    warehouse.setModifyDate(new Date());
						warehouseService.save(user.getProjectCode(), warehouse);
						
						//新增organization
						Organization org = new Organization();
						org.setOrgCode(warehouse.getCode());
						org.setOrgType(7);
						org.setOrgName(warehouse.getName());
						org.setOrgId(warehouse.getId());
						organizationService.save(user.getProjectCode(), org);
						message.setMsg("编码"+warehouse.getCode()+"添加成功");
					}else{
						message.setSuccess(false);
						message.setMsg("编码"+warehouse.getCode()+"已经添加，不能重复添加");
					}
				}else{
					message.setSuccess(false);
					message.setMsg("该账号不能新增！");
				}
			}else{
				message.setSuccess(false);
				message.setMsg("该账号不能新增！");
			}
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
	public String edit(){
		return "set/warehouse/edit";
	}
	
	/**
	 * 修改
	 * @param warehouse
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Warehouse warehouse, @CurrentUser User user){
		Message message = new Message();
		
		try{
			warehouse.setModifyDate(new Date());
			warehouseService.updateWithInclude(user.getProjectCode(), warehouse,"name","addr","contact","phone","type","isReceive","isDisabled","modifyDate","modifyUser");
			
			//修改organization
			Organization org = organizationService.getByOrgId(user.getProjectCode(), warehouse.getId(),7);
			if(org != null){
				org.setOrgCode(warehouse.getCode());
				org.setOrgType(7);
				org.setOrgName(warehouse.getName());
				org.setOrgId(warehouse.getId());
				organizationService.update(user.getProjectCode(), org);
			}else{
				org = new Organization();
				org.setOrgCode(warehouse.getCode());
				org.setOrgType(7);
				org.setOrgName(warehouse.getName());
				org.setOrgId(warehouse.getId());
				organizationService.save(user.getProjectCode(), org);
			}
			message.setMsg("编码"+warehouse.getCode()+"修改成功");
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
			Warehouse warehouse = warehouseService.getById(user.getProjectCode(), id);
			warehouseService.delete(user.getProjectCode(), id);
			message.setMsg("编码"+warehouse.getCode()+"删除成功");
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return message;
	}
	
	/**
	 * 根据医院查库房
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value="/listByHospital")
	@ResponseBody
	public List<Map<String, Object>> listByHospital(Long hospitalId,@CurrentUser User user){
		List<Map<String, Object>> list = warehouseService.lisByHospital(user.getProjectCode(), hospitalId);
		return list;
	}
	
	/**
	 * 分页查询 药房
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/queryByHospital")
	@ResponseBody
	public DataGrid<Warehouse> queryByHospital(PageRequest pageablem,Long id,String searchkey,@CurrentUser User user){
		if(id == null){
			return null;
		}
		DataGrid<Warehouse> page = warehouseService.queryByHospital(user.getProjectCode(), pageablem,id,searchkey);
		return page;
	}

	/**
	 * 查询药房类型
	 * @return
	 */
	@RequestMapping(value = "/getTypeSelect", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String,Object>> getTypeSelect(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0;i<Warehouse.Type.values().length;i++){
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("id", Warehouse.Type.values()[i]);
			m.put("name", Warehouse.Type.values()[i].getName());
			list.add(m);
		}
		
		return list;
		
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page2")
	@ResponseBody
	public DataGrid<Warehouse> homePage2(PageRequest pageable, @CurrentUser User user){
		DataGrid<Warehouse> page = (DataGrid<Warehouse>)warehouseService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	/**
	 * 进入新增画面
	 * @return
	 */
	@RequestMapping(value = "/add2", method = RequestMethod.GET)
	public String add2(String hospitalCode, Model model,@CurrentUser User user){
		model.addAttribute("code", getWarehouseCode(user.getProjectCode(), hospitalCode));
		return "set/hospital/subadd";
	}
	
	/**
	 * 新增
	 * @param warehouse
	 * @return
	 */
	@RequestMapping(value = "/add2", method = RequestMethod.POST)
	@ResponseBody
	public Message add2(Warehouse warehouse,@CurrentUser User user){
		Message message = new Message();		
		try{
			Warehouse obj = warehouseService.findByCode(user.getProjectCode(), warehouse.getCode());
			if(obj == null){
				warehouse.setModifyDate(new Date());
				warehouseService.save(user.getProjectCode(), warehouse);
				
				//新增organization
				Organization org = new Organization();
				org.setOrgCode(warehouse.getCode());
				org.setOrgType(7);
				org.setOrgName(warehouse.getName());
				org.setOrgId(warehouse.getId());
				organizationService.save(user.getProjectCode(), org);
				message.setMsg("编码"+warehouse.getCode()+"添加成功");
			}else{
				message.setSuccess(false);
				message.setMsg("编码"+warehouse.getCode()+"已经添加，不能重复添加");
			}
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
	@RequestMapping(value = "/edit2", method = RequestMethod.GET)
	public String edit2(){
		return "set/hospital/subedit";
	}
	
	/**
	 * 修改
	 * @param warehouse
	 * @return
	 */
	@RequestMapping(value = "/edit2", method = RequestMethod.POST)
	@ResponseBody
	public Message edit2(Warehouse warehouse, @CurrentUser User user){
		Message message = new Message();
		
		try{
			warehouse.setModifyDate(new Date());
			warehouseService.updateWithInclude(user.getProjectCode(), warehouse,"name","addr","type","isReceive","isDisabled");
			
			//修改organization
			Organization org = organizationService.getByOrgId(user.getProjectCode(), warehouse.getId(),7);
			if(org != null){
				org.setOrgCode(warehouse.getCode());
				org.setOrgType(7);
				org.setOrgName(warehouse.getName());
				org.setOrgId(warehouse.getId());
				organizationService.update(user.getProjectCode(), org);
			}else{
				org = new Organization();
				org.setOrgCode(warehouse.getCode());
				org.setOrgType(7);
				org.setOrgName(warehouse.getName());
				org.setOrgId(warehouse.getId());
				organizationService.save(user.getProjectCode(), org);
			}
			message.setMsg("编码"+warehouse.getCode()+"修改成功");
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return message;
	}

}
