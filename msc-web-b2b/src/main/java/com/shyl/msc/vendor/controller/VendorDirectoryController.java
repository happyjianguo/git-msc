package com.shyl.msc.vendor.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.annotation.Fastjson;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor;
import com.shyl.msc.b2b.plan.service.IDirectoryVendorService;
import com.shyl.msc.dm.entity.Directory;
import com.shyl.msc.dm.service.IDirectoryService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.ProjectStus;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.msc.set.service.IProjectService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/vendor/directory")
public class VendorDirectoryController {

	@Resource
	private IDirectoryService directoryService;
	@Resource
	private IDirectoryVendorService directoryVendorService;
	@Resource
	private IProjectDetailService projectDetailService;
	@Resource
	private IProjectService projectService;
	@Resource
	private IProductService productService;
	
	@RequestMapping("")
	public String list() {
		return "vendor/directory/list";
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Project> page(PageRequest page, @CurrentUser User user) {
		Map<String, Object> query = page.getQuery();
		String type = (String)query.get("t#type_S_EQ");
		if(!StringUtils.isEmpty(type)){
			query.put("t#type_S_EQ", Project.ProjectType.valueOf(type));
		}
		return projectService.query(user.getProjectCode(), page);
	}
	
	@RequestMapping(value = "/mxpage", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<ProjectDetail> mxpage(Long projectId,PageRequest pageable, @CurrentUser User user) {
		if(projectId == null)
			return new DataGrid<>();
		pageable.getQuery().put("t#project.id_L_EQ", projectId);
		DataGrid<ProjectDetail> page = projectDetailService.query(user.getProjectCode(), pageable);
		for (ProjectDetail projectDetail : page.getRows()) {
			PageRequest p = new PageRequest();
			p.getQuery().put("t#vendorCode_S_EQ", user.getOrganization().getOrgCode());
			p.getQuery().put("t#projectDetail.id_L_EQ", projectDetail.getId());
			DirectoryVendor dv = directoryVendorService.getByKey(user.getProjectCode(),p);
			if(dv != null){
				projectDetail.setSegmentStr(dv.getProducerCode()+"_"+dv.getProducerName());
				projectDetail.setSegmentBD(dv.getPrice());
				projectDetail.setSegmentLong(dv.getId());
			}
		}
		return page;
	}
	
	@RequestMapping(value = "/producerComb")
	@ResponseBody
	public List<Map<String, Object>> projectComb(String productName,PageRequest page, @CurrentUser User user) {
		System.out.println("productName="+productName);
		List<Map<String, Object>> list = productService.producerComb(user.getProjectCode(),productName);
		System.out.println("list="+list.size());
		if(list.size() == 0){
			list = productService.producerComb(user.getProjectCode(),"");
		}
		return list;
	}
	
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	public String add(){
		return "vendor/directory/add";
	}
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	@ResponseBody
	public Message add(DirectoryVendor directoryVendor, @CurrentUser User user){
		Message message = new Message();
		try {
			directoryVendor.setDeclareDate(new Date());
			directoryVendor.setStatus(DirectoryVendor.Status.undeclare);
			directoryVendorService.save(user.getProjectCode(), directoryVendor);
			message.setSuccess(true);
			message.setMsg("新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public String edit(Long id, Model model, @CurrentUser User user){
		DirectoryVendor directoryVendor = directoryVendorService.getById(user.getProjectCode(), id);
		//model.addAttribute("genericName", directoryVendor.getSubjectDetail().getDirectory().getGenericName());
		return "vendor/directory/edit";
	}
	
	@RequestMapping(value = "/edit",method = RequestMethod.POST)
	@ResponseBody
	public Message edit(DirectoryVendor directoryVendor, @CurrentUser User user){
		Message message = new Message();
		try {
			directoryVendorService.updateWithInclude(user.getProjectCode(), directoryVendor,
					"productName","model","producerCode","producerName","price");
			message.setSuccess(true);
			message.setMsg("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	@RequestMapping(value = "/delete",method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Long id, @CurrentUser User user){
		Message msg = new Message();
		try {
			directoryVendorService.delete(user.getProjectCode(), id);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/tender",method = RequestMethod.POST)
	@ResponseBody
	public Message tender(@Fastjson List<DirectoryVendor> fastjson, @CurrentUser User currentUser){
		Message msg = new Message();
		try {
			directoryVendorService.tender(currentUser.getProjectCode(),fastjson,currentUser);
//			DirectoryVendor directoryVendor = directoryVendorService.getById(user.getProjectCode(), id);
//			directoryVendor.setStatus(DirectoryVendor.Status.declare);
//			directoryVendorService.update(user.getProjectCode(), directoryVendor);
			//申报数量+1
			//SubjectDetail sd = directoryVendor.getSubjectDetail();
			//Integer num = sd.getNum() == null?0:sd.getNum();
			//sd.setNum(num+1);
			//subjectDetailService.update(user.getProjectCode(), sd);
			
			msg.setSuccess(true);
			msg.setMsg("申报成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value="/export")
	@ResponseBody
	public void export(HttpServletResponse resp)  {
		String[] heanders = {"通用名","剂型","规格","质量层次","最小使用单位"};
		
		OutputStream out = null;
		Workbook wb = new HSSFWorkbook();
	 	try {
			 Sheet sh = wb.createSheet("sheet1");
			 Row row = sh.createRow(0);
			 for(int i = 0;i<heanders.length;i++) {
				 Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(heanders[i]);
			 }

			 resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			 resp.setHeader("Content-Disposition", "attachment; filename=product.xls");
			 out = resp.getOutputStream();
			 wb.write(out);  
			 out.flush();
			 wb.close();
			 out.close();
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
	
	@RequestMapping(value="/importExcel")
	@ResponseBody
	public Message importExcel(MultipartFile myfile, @CurrentUser User user)  {
		Message message = new Message(); 
		 String filename = myfile.getOriginalFilename();  
		 if (filename == null || "".equals(filename)) {  
			 message.setMsg("文件不能为空");
			 return message;
		 }  
		 try {  
			  if(filename.endsWith(".xls") ||filename.endsWith(".xlsx")) {
				  //读取Excel
				  this.doExcel(user.getProjectCode(), myfile);
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
	 * �?次读取多少条
	 * @param file
	 * @param readLine
	 * @param handle 0 导入标准 1导入地级市目�?
	 * @return
	 * @throws Exception
	 */
	private void doExcel(String projectCode, MultipartFile myfile) throws Exception {
		
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
			List<Directory> directoryList = new ArrayList<Directory>();
			int cellLen = sheet.getRow(0).getPhysicalNumberOfCells();
			String[] upExcel = new String[cellLen];
			try {
				// i = 0 是标题栏
				for (int i = 1 ; i < sheet.getPhysicalNumberOfRows(); i++) {
					Row row = sheet.getRow(i);
					for (int j = 0; j < cellLen; j++) {
						Cell cell = row.getCell(j);
						String cellStr = getValue(cell);
						if (j == 0 && StringUtils.isEmpty(cellStr)) {
							break;
						}
						upExcel[j] = cellStr;
					}
					if (StringUtils.isEmpty(upExcel[0]) || StringUtils.isEmpty(upExcel[1])) {
						throw new RuntimeException("�?"+i+"条数据不能为�?");
					}
				}
				directoryService.saveBatch(projectCode, directoryList);
			} catch(RuntimeException e) {
				throw e;
			} catch(Exception e) {
				throw e;
			} finally {
				workBook.close();
			}
		}
		
	}
	
	/**
	 * 解决excel类型问题，获得数�?
	 * @param cell
	 * @return
	 */
	private static String getValue(Cell cell) {
		String value = "";
		if (null == cell) {
			return value;
		}
		switch (cell.getCellType()) {
		// 数�?�型
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				// 如果是date类型�? ，获取该cell的date�?
				Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				value = format.format(date);
				;
			} else {// 纯数�?
				BigDecimal big = new BigDecimal(cell.getNumericCellValue());
				value = big.toString();
				// 解决1234.0 去掉后面�?.0
				if (null != value && !"".equals(value.trim())) {
					String[] item = value.split("[.]");
					if (1 < item.length && "0".equals(item[1])) {
						value = item[0];
					}
				}
			}
			break;
		// 字符串类�?
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue().toString();
			break;
		// 公式类型
		case Cell.CELL_TYPE_FORMULA:
			// 读公式计算�??
			value = String.valueOf(cell.getNumericCellValue());
			if (value.equals("NaN")) {// 如果获取的数据�?�为非法�?,则转换为获取字符�?
				value = cell.getStringCellValue().toString();
			}
			break;
		// 布尔类型
		case Cell.CELL_TYPE_BOOLEAN:
			value = " " + cell.getBooleanCellValue();
			break;
		// 空�??
		case Cell.CELL_TYPE_BLANK:
			value = "";
			break;
		// 故障
		case Cell.CELL_TYPE_ERROR:
			value = "";
			break;
		default:
			value = cell.getStringCellValue().toString();
		}
		if ("null".endsWith(value.trim())) {
			value = "";
		}
		return value;
	}
	
}
