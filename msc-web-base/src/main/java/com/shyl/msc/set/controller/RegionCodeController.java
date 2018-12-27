package com.shyl.msc.set.controller;

import java.util.List;

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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;


/**
 * 地区管理
 * @author dell
 *
 */
@Controller
@RequestMapping("/set/regioncode")
public class RegionCodeController extends BaseController{
	@Resource
	private IRegionCodeService regionCodeService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "set/regioncode/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<RegionCode> page(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.ASC,"fullName");
		pageable.setSort(sort);
		DataGrid<RegionCode> page =  regionCodeService.query(user.getProjectCode(), pageable);
		//page.addFooter("name","code");
//		BigDecimal total = new BigDecimal("0");
//		for (RegionCode rc : page.getRows()) {
//			total = total.add(new BigDecimal(rc.getCode())); 
//			
//		}
//		page.addFooter("code");
//		RegionCode footerMap = new RegionCode();
//		footerMap.setCode(total+"");
//		footerMap.setFullName("Total:"); 
//		page.getFooter().add(footerMap);
//		page.getFooter().add(footerMap);
		return page;
	}


	/**
	 * 一级地区
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/lvlone")
	@ResponseBody
	public List<RegionCode> lvlone(@CurrentUser User user){
		List<RegionCode> list = regionCodeService.getLvlone(user.getProjectCode());
		return list;
	}
	
	/**
	 * 二级地区
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/lvltwo")
	@ResponseBody
	public List<RegionCode> lvltwo(Long parentId,@CurrentUser User user){
		List<RegionCode> list = regionCodeService.getChlidList(user.getProjectCode(), parentId);
		return list;
	}
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "set/regioncode/add";
	}
	
	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(RegionCode regionCode,Long combox1,Long combox2, @CurrentUser User user){
		Message message = new Message();
		try{
			if(combox2 != null){
				regionCode.setParentId(combox2);
				regionCode.setTreePath(combox1+","+combox2);
				RegionCode province = regionCodeService.getById(user.getProjectCode(), combox1);
				RegionCode city = regionCodeService.getById(user.getProjectCode(), combox2);
				regionCode.setFullName(province.getName()+"-"+city.getName()+"-"+regionCode.getName());
			}else if(combox1 != null){
				regionCode.setParentId(combox1);;
				regionCode.setTreePath(combox1+"");
				RegionCode province = regionCodeService.getById(user.getProjectCode(), combox1);
				regionCode.setFullName(province.getName()+"-"+regionCode.getName());
			}else{
				regionCode.setFullName(regionCode.getName());
			}
			
			regionCodeService.save(user.getProjectCode(), regionCode);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(String treePath,Model model){
		if(treePath != null){
			String[] arr = treePath.split(",");
			for (int i = 0; i < arr.length; i++) {
				model.addAttribute("combox"+(i+1), arr[i]);
			}
		}
		
		return "set/regioncode/edit";
	}
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(RegionCode regionCode,Long combox1,Long combox2, @CurrentUser User user){
		Message message = new Message();
		try{
			if(combox2 != null){
				regionCode.setParentId(combox2);;
				regionCode.setTreePath(combox1+","+combox2);
				RegionCode province = regionCodeService.getById(user.getProjectCode(), combox1);
				RegionCode city = regionCodeService.getById(user.getProjectCode(), combox2);
				regionCode.setFullName(province.getName()+"-"+city.getName()+"-"+regionCode.getName());
			}else if(combox1 != null){
				regionCode.setParentId(combox1);;
				regionCode.setTreePath(combox1+"");
				RegionCode province = regionCodeService.getById(user.getProjectCode(), combox1);
				regionCode.setFullName(province.getName()+"-"+regionCode.getName());
			}else{
				regionCode.setFullName(regionCode.getName());
			}
			regionCodeService.updateWithInclude(user.getProjectCode(), regionCode, "code","name","fullName","parentId","treePath","isDisabled");
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}

	/**
	 * 删除
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id, @CurrentUser User user){
		Message message = new Message();
		
		try{
			regionCodeService.delete(user.getProjectCode(), id);
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return  message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/mktree", method = RequestMethod.GET)
	public String mktree(@CurrentUser User user){
		regionCodeService.mktree(user.getProjectCode());
		
		return "set/regioncode/edit";
	}
	
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
		
	}
}
