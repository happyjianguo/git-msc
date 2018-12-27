package com.shyl.msc.dm.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
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
import com.shyl.msc.dm.entity.DrugType;
import com.shyl.msc.dm.service.IDrugTypeService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 药品分类Controller
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/drugType")
public class DrugTypeController extends BaseController {

	@Resource
	private IDrugTypeService drugTypeService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "dm/drugType/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<DrugType> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<DrugType> page =  drugTypeService.query(user.getProjectCode(), pageable);

		return page;
	}
	
	/**
	 * 全部查询
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<DrugType> list(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		List<DrugType> list =  drugTypeService.list(user.getProjectCode(), pageable);
		return list;
	}
	

	/**
	 * 根据传参查询
	 * @return
	 */
	@RequestMapping("/listByParams")
	@ResponseBody
	public List<DrugType> listByParams(String attributeNo,String field1,@CurrentUser User user){
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),attributeNo, field1);
		if(attributeItem == null || attributeItem.getField3()==null){
			return null;
		}
		DrugType drugType = drugTypeService.findByCode(user.getProjectCode(), attributeItem.getField3());
		if (drugType== null) {
			return new ArrayList<DrugType>();
		}
		List<DrugType> list =  drugTypeService.listByParentId(user.getProjectCode(), drugType.getId());
		return list;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "dm/drugType/add";
	}
	
	/**
	 * 新增
	 * @param drugType
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(DrugType drugType,@CurrentUser User user) {
		Message message = new Message();
		try{
			DrugType obj = drugTypeService.findByCode(user.getProjectCode(), drugType.getCode());
			if(obj != null){
				throw new Exception("药品分类代码"+drugType.getCode()+"已存在");
			}
			drugTypeService.save(user.getProjectCode(), drugType);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(){
		return "dm/drugType/edit";
	}
	/**
	 * 修改
	 * @param drugType
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(DrugType drugType, @CurrentUser User user){
		Message message = new Message();
		try{
			drugTypeService.update(user.getProjectCode(), drugType);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 删除
	 * @param Long
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id, @CurrentUser User user){
		Message message = new Message();
		
		try{
			drugTypeService.delete(user.getProjectCode(), id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}

	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/mktree", method = RequestMethod.GET)
	public String mktree(@CurrentUser User user){
		drugTypeService.mktree(user.getProjectCode());
		
		return "dm/regioncode/edit";
	}
}
