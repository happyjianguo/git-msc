package com.shyl.msc.set.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IOrganizationService;

@Controller
@RequestMapping("/set/regulationorg")
public class RegulationOrgController {
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private  IOrganizationService organizationService;
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "set/regulationorg/list";
	}
	/**
	 * 全部查询
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<RegulationOrg> list(PageRequest pageable, @CurrentUser User user){
		List<RegulationOrg> list =  regulationOrgService.list(user.getProjectCode(), pageable);
		return list;
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<RegulationOrg> homePage(PageRequest pageable, @CurrentUser User user){
		DataGrid<RegulationOrg> page = (DataGrid<RegulationOrg>)regulationOrgService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "set/regulationorg/add";
	}
	/**
	 * 新增
	 * @param regulationOrg
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(RegulationOrg regulationOrg,Long combox1,Long combox2,Long combox3,@CurrentUser User user){
		Message message = new Message();		
		try{
			RegulationOrg regulationOrgold =  regulationOrgService.findByCode(user.getProjectCode(), regulationOrg.getCode());
			if(regulationOrgold != null){
				throw new Exception("编码"+regulationOrg.getCode()+"已存在");
			}
			
			if(combox3 != null){
				regulationOrg.setRegionCode(combox3);
			}else if(combox2 != null){
				regulationOrg.setRegionCode(combox2);
			}else if(combox1 != null){
				regulationOrg.setRegionCode(combox1);
			}
			regulationOrg = regulationOrgService.save(user.getProjectCode(), regulationOrg);	
			
			//新增organization
			Organization org = new Organization();
			org.setOrgCode(regulationOrg.getCode());
			org.setOrgType(3);
			org.setOrgName(regulationOrg.getFullName());
			org.setOrgId(regulationOrg.getId());
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
	public String edit(RegulationOrg regulationOrg,Model model, @CurrentUser User user){
		if(regulationOrg !=null){
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
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
		return "set/regulationorg/edit";
	}
	/**
	 * 修改
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(RegulationOrg regulationOrg,Long combox1,Long combox2,Long combox3, @CurrentUser User user){
		Message message = new Message();	
		try{
			if(combox3 != null){
				regulationOrg.setRegionCode(combox3);
			}else if(combox2 != null){
				regulationOrg.setRegionCode(combox2);
			}else if(combox1 != null){
				regulationOrg.setRegionCode(combox1);
			}
			regulationOrg = regulationOrgService.update(user.getProjectCode(), regulationOrg);
			
			//修改organization
			Organization org = organizationService.getByOrgId(user.getProjectCode(), regulationOrg.getId(),3);
			if(org != null){
				org.setOrgCode(regulationOrg.getCode());
				org.setOrgName(regulationOrg.getFullName());
				organizationService.update(user.getProjectCode(), org);
			}else{
				org = new Organization();
				org.setOrgCode(regulationOrg.getCode());
				org.setOrgType(3);
				org.setOrgName(regulationOrg.getFullName());
				org.setOrgId(regulationOrg.getId());
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
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id, @CurrentUser User user){
		Message message = new Message();
		
		try{
			regulationOrgService.delete(user.getProjectCode(), id);
			Organization org = organizationService.getByOrgId(user.getProjectCode(), id,3);
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
