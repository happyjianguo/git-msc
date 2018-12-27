package com.shyl.msc.gpo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor.Status;
import com.shyl.msc.b2b.plan.entity.HospitalPlan;
import com.shyl.msc.b2b.plan.service.IDirectoryVendorService;
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.msc.dm.entity.Directory;
import com.shyl.msc.dm.service.IDirectoryService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.service.IAttributeService;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.msc.set.service.IProjectService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/gpo/projectDetail")
public class ProjectDetailController extends BaseController {

	@Resource
	private IProjectService	projectService;
	@Resource
	private IProjectDetailService	projectDetailService;
	@Resource
	private IProductService	productService;
	@Resource
	private IAttributeService attributeService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IDirectoryService directoryService;
	@Resource
	private IDirectoryVendorService directoryVendorService;
	@Resource
	private IHospitalPlanService hospitalPlanService;
	
	
	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping("")
	public String home(ModelMap model, Long projectId) {
		model.addAttribute("projectId", projectId);
		return "gpo/projectDetail/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<ProjectDetail> page(Long projectId,PageRequest page, @CurrentUser User user) {
		if(projectId == null) return new DataGrid<>();
		page.getQuery().put("t#project.id_L_EQ", projectId);
		if (page.getSort() == null) {
			Sort sort = new Sort(new Order(Direction.ASC,"directory.genericName"),new Order(Direction.ASC,"directory.dosageFormName"),
					new Order(Direction.ASC,"directory.model"),new Order(Direction.ASC,"directory.qualityLevelCode"));
			page.setSort(sort);
		}
		return projectDetailService.query(user.getProjectCode(), page);
	}
	
	@RequestMapping("/pageByProject")
	@ResponseBody
	public DataGrid<Directory> pageByProject(PageRequest pageable, Long id,@CurrentUser User user){
		pageable.getQuery().put("t#project.id_L_EQ", id);
		DataGrid<ProjectDetail> page = projectDetailService.query(user.getProjectCode(), pageable);
		List<Directory> list = new ArrayList<>();
		for (ProjectDetail projectDetail : page.getRows()) {
			list.add(projectDetail.getDirectory());
		}
		return new DataGrid<>(list, pageable, page.getTotal());
	}

	@RequestMapping(value="/directory",method=RequestMethod.GET)
	public String directory() {
		return "gpo/projectDetail/directory";
	}

	@RequestMapping(value="/directory",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Directory> directory(Long projectId, PageRequest page,@CurrentUser User user) {
		DataGrid<Directory> data = directoryService.query(user.getProjectCode(), page);
		for (Directory directory : data.getRows()) {
			ProjectDetail detail = projectDetailService.getByDirectoryId(user.getProjectCode(), projectId, directory.getId());
			//暂用projectCode
			directory.setProjectCode(detail == null?"0":"1");
		}
		return data;
	}
	@RequestMapping(value="/choose",method=RequestMethod.GET)
	public String choose() {
		return "gpo/projectDetail/choose";
	}
	
	@RequestMapping(value="/choose",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<DirectoryVendor> choose(Long projectDetailId, PageRequest page,@CurrentUser User user) {
		System.out.println("projectDetailId="+projectDetailId);
		page.getQuery().put("t#projectDetail.id_L_EQ", projectDetailId);
		page.getQuery().put("t#status_L_NE", Status.undeclare);
		DataGrid<DirectoryVendor> data = directoryVendorService.query(user.getProjectCode(), page);
		return data;
	}
	@RequestMapping(value="/hospital",method=RequestMethod.GET)
	public String hospital() {
		return "gpo/projectDetail/hospital";
	}
	
	@RequestMapping(value="/hospital",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<HospitalPlan> hospital(Long projectDetailId, PageRequest page,@CurrentUser User user) {
		page.getQuery().put("t#projectDetailId_L_EQ", projectDetailId);
		DataGrid<HospitalPlan> data = hospitalPlanService.query(user.getProjectCode(), page);
		return data;
	}
	@RequestMapping(value="/vendor",method=RequestMethod.GET)
	public String vendor() {
		return "gpo/projectDetail/vendor";
	}
	
	@RequestMapping(value="/vendor",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<DirectoryVendor> vendor(Long projectDetailId, PageRequest page,@CurrentUser User user) {
		page.getQuery().put("t#projectDetail.id_L_EQ", projectDetailId);
		page.getQuery().put("t#status_L_NE", DirectoryVendor.Status.undeclare);
		DataGrid<DirectoryVendor> data = directoryVendorService.query(user.getProjectCode(), page);
		return data;
	}
	/**
	 * 定标
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/dochoose", method = RequestMethod.POST)
	@ResponseBody
	public Message dochoose(Long id ,Long productId,String status, @CurrentUser User user){
		Message message = new Message();
		System.out.println("id = "+id);
		System.out.println("status = "+status);
		try{
			directoryVendorService.dochoose(user.getProjectCode(),id,productId,status,new BigDecimal("0"));
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Long projectId, Long directoryId, @CurrentUser User user) {
		Message msg = new Message();
		try{
			Directory directory = directoryService.getById(user.getProjectCode(), directoryId);
			Project project = projectService.getById(user.getProjectCode(), projectId);
			ProjectDetail detail = new ProjectDetail();
			detail.setProject(project);
			detail.setDirectory(directory);
			
			projectDetailService.save(user.getProjectCode(), detail);
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long projectId, Long directoryId,@CurrentUser User user) {
		Message msg = new Message();
		try{
			ProjectDetail detail = projectDetailService.getByDirectoryId(user.getProjectCode(), projectId, directoryId);
			projectDetailService.delete(user.getProjectCode(), detail);
			msg.setMsg("删除成功");
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("删除失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value="/importExcel")
	@ResponseBody
	public Message importExcel(Long projectId, MultipartFile myfile,@CurrentUser User user)  {
		Message message = new Message(); 
		 String filename = myfile.getOriginalFilename();  
		 if (filename == null || "".equals(filename)) {  
			 message.setMsg("文件不能为空");
			 return message;
		 }  
		 System.out.println("projectId:"+projectId);
		 try {  
			  if(filename.endsWith(".xls") ||filename.endsWith(".xlsx")) {
				  //读取Excel
				  this.doExcel(user.getProjectCode(), projectId, myfile);
				  message.setMsg("导入成功");
			  }else {
				  message.setMsg("请用正确格式导入");
			  }
		 }  catch (RuntimeException e) {  
			 	e.printStackTrace();
				message.setSuccess(false);
				message.setMsg(e.getMessage());
		 }  catch (Exception e) {  
			 	e.printStackTrace();
				message.setSuccess(false);
				message.setMsg("导入失败");
		 }  
		 return message;
	}
	
	/**
	 * 一次读取多少条
	 * @param file
	 * @param readLine
	 * @param handle 0 导入标准 1导入地级市目录
	 * @return
	 * @throws Exception
	 */
	private void doExcel(String projectCode, Long projectId, MultipartFile myfile) throws Exception {

		Project project = projectService.getById(projectCode, projectId);
		
		InputStream input = myfile.getInputStream();
		Workbook workBook = null;
		//文件名称
		String filename = myfile.getOriginalFilename();
		if(filename.endsWith(".xlsx")) {
			workBook = new XSSFWorkbook(input);
		} else {
			workBook = new HSSFWorkbook(input);
		}
		Sheet sheet = workBook.getSheetAt(0);
		if (sheet != null) {
			//质量层次信息
			List<AttributeItem> items = attributeItemService.getItemSelect(projectCode,null, "product_qualityLevel");
			Map<String,String> map = new HashMap<String,String>();
			for(AttributeItem item : items) {
				map.put(item.getField2(), item.getField1());
			}
			
			int cellLen = sheet.getRow(0).getPhysicalNumberOfCells();
			String[] upExcel = new String[cellLen];

			try {
				List<ProjectDetail> details = new ArrayList<ProjectDetail>();
				// i = 0 是标题栏
				for (int i = 1 ; i < sheet.getPhysicalNumberOfRows(); i++) {
					Row row = sheet.getRow(i);
					upExcel = new String[cellLen];
					for (int j = 0; j < cellLen; j++) {
						Cell cell = row.getCell(j);
						String cellStr = ExcelUtil.getValue(cell);
						if (j == 0 && StringUtils.isEmpty(cellStr)) {
							break;
						}
						upExcel[j] = cellStr;
					}
					if (StringUtils.isEmpty(upExcel[0])&&StringUtils.isEmpty(upExcel[1])) {
						break;
					}
					Directory directory = directoryService.getById(projectCode, Long.valueOf(upExcel[0]));
					if (directory == null ) {
						throw new RuntimeException("遴选目录未维护");
					}
					ProjectDetail detail = new ProjectDetail();
					detail.setProject(project);
					detail.setDirectory(directory);
					details.add(detail);
				}
				projectDetailService.saveBatch(projectCode, details);
			} catch(RuntimeException e) {
				throw e;
			} catch(Exception e) {
				throw e;
			} finally {
				workBook.close();
			}
		}
		
	}
	
	
	@RequestMapping(value="/export")
	@ResponseBody
	public void export(HttpServletResponse resp, @CurrentUser User user, PageRequest page )  {
		String[] heanders = {"遴选目录编码","通用名","剂型","规格","质量层次","最小使用单位"};
		String[] beannames = {"id","genericName","dosageFormName","model","qualityLevel","minUnit"};
		
		OutputStream out = null;
		Workbook wb = null;
	 	try {
			 ExcelUtil util = new ExcelUtil(heanders, beannames);
			/* PageRequest page = new PageRequest();*/
			 Sort sort = new Sort(new Order(Direction.ASC,"expertGroupCode"),new Order(Direction.ASC,"genericName"),
					 new Order(Direction.ASC,"dosageFormName"),new Order(Direction.ASC,"model"),
					 new Order(Direction.ASC,"qualityLevelCode"));
			 page.setSort(sort);
			 List<Directory> list = directoryService.list(user.getProjectCode(), page);
			 wb = util.doExportXLS(list, "遴选目录", true, true);
			 
			 resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			 resp.setHeader("Content-Disposition", "attachment; filename=directory.xls");
			 out = resp.getOutputStream();
			 wb.write(out);
			 out.flush();
	 	} catch (IOException e) {
	 		e.printStackTrace();
	 	}  finally {
			if (wb!=null) {
		 		try {
					wb.close();
				} catch (IOException e) {
				}
			}
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
