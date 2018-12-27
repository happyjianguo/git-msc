package com.shyl.msc.menu.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.menu.entity.MedicalDevices;
import com.shyl.msc.menu.service.IMedicalDevicesService;
import com.shyl.msc.task.MedicDevicesFoundAsyncTask;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 国产，进口药品数据
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/menu/medicalDevices")
public class MedicalDevicesController extends BaseController {

	@Resource
	private IMedicalDevicesService medicalDevicesService;
	@Resource
	private MedicDevicesFoundAsyncTask medicalDevicesFoundAsyncTask;
	
	@Override
	protected void init(WebDataBinder arg0) {
		
	}

	@RequestMapping("")
	public String home() {
		return "/menu/medicalDevices/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<MedicalDevices> list(PageRequest pageable, @CurrentUser User user) {
		Sort sort = new Sort(Direction.ASC,"id");
		pageable.setSort(sort);
		return medicalDevicesService.query(user.getProjectCode(), pageable);
	}
	
	@RequestMapping("/snyc")
	@ResponseBody
	public Message snyc(int tableid) {
		Message msg = new Message();
		try {
			if( MedicDevicesFoundAsyncTask.status != 0) {
				if (MedicDevicesFoundAsyncTask.tableid == 25) {
					throw new MyException("正在抓取国产药品数据，抓取完毕再操作");
				} else if (MedicDevicesFoundAsyncTask.tableid == 36) {
					throw new MyException("正在抓取进口药品数据，抓取完毕再操作");
				} else if (MedicDevicesFoundAsyncTask.tableid == 26) {
					throw new MyException("正在抓取国产器械数据，抓取完毕再操作");
				}else if (MedicDevicesFoundAsyncTask.tableid == 27) {
					throw new MyException("正在抓取进口机械数据，抓取完毕再操作");
				}else if (MedicDevicesFoundAsyncTask.tableid == 63) {
					throw new MyException("正在抓取药品规格数据，抓取完毕再操作");
				}else if (MedicDevicesFoundAsyncTask.tableid == 74) {
					throw new MyException("正在抓取基本药品数据，抓取完毕再操作");
				}else {
					throw new MyException("正在抓取其他药品数据，抓取完毕再操作");
				}
			}

			MedicDevicesFoundAsyncTask.status = 1;
			medicalDevicesFoundAsyncTask.syncMedicDevices(tableid);
			msg.setMsg("同步信息线程开启");
		} catch (MyException e) {
			msg.setMsg(e.getMessage());
		}
		return msg;
	}
	
	@RequestMapping("/stop")
	@ResponseBody
	public Message stop() {
		Message msg = new Message();
		MedicDevicesFoundAsyncTask.status = 0;
		msg.setMsg("同步信息线程已停止");
		return msg;
	}
}
