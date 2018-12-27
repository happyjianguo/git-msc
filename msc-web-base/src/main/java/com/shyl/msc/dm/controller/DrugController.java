package com.shyl.msc.dm.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.Pinyin4jUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.DosageForm;
import com.shyl.msc.dm.entity.Drug;
import com.shyl.msc.dm.service.IDosageFormService;
import com.shyl.msc.dm.service.IDrugService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 药品Controller
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/drug")
public class DrugController extends BaseController {

	@Resource
	private IDrugService drugService;
	@Resource
	private IDosageFormService dosageFormService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "dm/drug/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Drug> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<Drug> page =  drugService.query(user.getProjectCode(), pageable);

		return page;
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<Drug> list(PageRequest pageable, @CurrentUser User user){
		List<Drug> list =  drugService.list(user.getProjectCode(), pageable);
		return list;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "dm/drug/add";
	}
	
	/**
	 * 新增
	 * @param drug
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Drug drug,@CurrentUser User user){
		Message message = new Message();
		try{
			/*PageRequest pageable = new PageRequest();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("t#code_S_EQ", drug.getCode());
			pageable.setQuery(m);
			List<Drug> list = drugService.list(pageable);
			if(list.size()>0){
				throw new Exception("药品目录代码"+drug.getCode()+"已存在");
			}*/
			setData(user.getProjectCode(), drug);
			drug.setCreateUser(user.getEmpId());
			drugService.save(user.getProjectCode(), drug);
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
	public String edit(Long drugTypeId,Long dosageFormId,Model model){
		if(drugTypeId!=null){
			model.addAttribute("combox1", drugTypeId);
		}
		if(dosageFormId!=null){
			model.addAttribute("combox2", dosageFormId);
		}
		return "dm/drug/edit";
	}
	/**
	 * 修改
	 * @param drug
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Drug drug,@CurrentUser User user){
		Message message = new Message();
		try{
			setData(user.getProjectCode(), drug);
			drugService.update(user.getProjectCode(), drug);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
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
			drugService.delete(user.getProjectCode(), id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}

	private void setData(String projectCode, Drug drug){
		DosageForm dosageForm = dosageFormService.getById(projectCode, drug.getDosageFormId());
		drug.setDosageFormName(dosageForm.getName());
		if (StringUtils.isEmpty(drug.getPinyin())) {
			drug.setPinyin(Pinyin4jUtil.getPinYinHeadChar(drug.getGenericName()).toUpperCase());
		}
		if (StringUtils.isEmpty(drug.getCode())) {
			drug.setGenericCode(drugService.getMaxCode(projectCode, drug.getGenericName()));
			drug.setCode(drug.getGenericCode() + "-" + dosageForm.getCode());
		}
	}
}
