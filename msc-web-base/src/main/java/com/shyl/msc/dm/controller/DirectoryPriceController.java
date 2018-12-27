package com.shyl.msc.dm.controller;

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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
import com.shyl.msc.dm.entity.Directory;
import com.shyl.msc.dm.entity.DirectoryPrice;
import com.shyl.msc.dm.entity.DirectoryPriceRecord;
import com.shyl.msc.dm.service.IDirectoryPriceRecordService;
import com.shyl.msc.dm.service.IDirectoryPriceService;
import com.shyl.msc.dm.service.IDirectoryService;

@Controller
@RequestMapping("/dm/directoryPrice")
public class DirectoryPriceController extends BaseController{

	@Resource
	private IDirectoryPriceService directoryPriceService;
	@Resource
	private IDirectoryPriceRecordService directoryPriceRecordService;
	@Resource
	private IDirectoryService directoryService;


	/**
	 * 主页
	* @return
	*/
	@RequestMapping("")
	public String home(){
		return "dm/directoryPrice/list";
	}

	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<DirectoryPrice> page(PageRequest pageable){
		DataGrid<DirectoryPrice> page = directoryPriceService.query("",pageable);
		return page;
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<DirectoryPriceRecord> mxpage(PageRequest pageable){
		DataGrid<DirectoryPriceRecord> page = directoryPriceRecordService.query("",pageable);
		return page;
	}

	/**
	 * 全部查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<DirectoryPrice> list(PageRequest pageable){
		List<DirectoryPrice> list = directoryPriceService.list("",pageable);
		return list;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "dm/directoryPrice/add";
	}

	/**
	 * 新增
	 * @param directoryPrice
	 * @return
	*/
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(DirectoryPriceRecord directoryPriceRecord,@CurrentUser User user){
		Message message = new Message();
		try{
			directoryPriceService.doSave(user.getProjectCode(),directoryPriceRecord);
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
	public String edit(Long id,Model model,@CurrentUser User user){
		DirectoryPrice dp =  directoryPriceService.getById(user.getProjectCode(),id);
		model.addAttribute("directoryPrice",dp);
		return "dm/directoryPrice/edit";
	}
	
	/**
	 * 修改
	 * @param directoryPrice
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(DirectoryPrice directoryPrice){
		Message message = new Message();
		try{
			directoryPriceService.update("",directoryPrice);
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
	public Message del(Long id){
		Message message = new Message();
		try{
			directoryPriceRecordService.delete("",id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	@Override
	protected void init(WebDataBinder arg0) {}
	
}
