package com.shyl.msc.dm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.msc.dm.entity.Directory;
import com.shyl.msc.dm.service.IDirectoryService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/dm/directory")
public class DirectoryController {

	@Resource
	private IDirectoryService directoryService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@RequestMapping("/list")
	public String list() {
		
		return "dm/directory/list";
	}

	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Directory> page(PageRequest page, @CurrentUser User user) {
		return directoryService.query(user.getProjectCode(), page);
	}
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	public String add(){
		return "dm/directory/add";
	}
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	@ResponseBody
	public Message add(Directory directory, @CurrentUser User user){
		Message msg = new Message();
		try {
			directoryService.save(user.getProjectCode(), directory);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}

	@RequestMapping(value = "/delete",method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Long id, @CurrentUser User user){
		Message msg = new Message();
		try {
			directoryService.delete(user.getProjectCode(), id);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}

	
	@RequestMapping(value="/export")
	@ResponseBody
	public void export(HttpServletResponse resp)  {
		String[] heanders = {"通用名","推荐剂型","剂型","规格","属性","质量层次","最小使用单位","生产厂家","备注"};
		
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
	public Message importExcel(MultipartFile myfile,@CurrentUser User user)  {
		Message message = new Message(); 
		 String filename = myfile.getOriginalFilename();  
		 if (filename == null || "".equals(filename)) {  
			 message.setMsg("文件不能为空");
			 return message;
		 }  
		 try {  
			  if(filename.endsWith(".xls") ||filename.endsWith(".xlsx")) {
				  //读取Excel
				  this.doExcel(myfile, user.getProjectCode());
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
	
	private void doExcel(MultipartFile myfile, String projectCode) throws Exception {
		
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
			List<AttributeItem> quals = attributeItemService.getItemSelect(projectCode,null, "product_qualityLevel");
			Map<String,String> qualsMap = new HashMap<String,String>();
			for(AttributeItem item : quals) {
				qualsMap.put(item.getField2(), item.getField1());
			}
			List<Directory> directorySaveList = new ArrayList<Directory>();
			List<Directory> directoryUpdateList = new ArrayList<Directory>();
			int cellLen = sheet.getRow(0).getPhysicalNumberOfCells();
			String[] upExcel = new String[cellLen];
			try {
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
					if (StringUtils.isEmpty(upExcel[0]) || StringUtils.isEmpty(upExcel[2])) {
						throw new RuntimeException("第"+i+"条数据不能为空");
					}
					String genericName = upExcel[0];
					String rcDosageFormName = upExcel[1];
					String dosageFormName = upExcel[2];
					String model = upExcel[3];
					String qualityLevel = upExcel[4];
					String producerNames = upExcel[7];
					String minUnit = upExcel[6];
					String note = upExcel[5];
					
					Directory directory = directoryService.findDirectory(projectCode, genericName, dosageFormName, model, qualityLevel, producerNames,minUnit,note);
					if (directory == null) {
						directory = new Directory();
						directorySaveList.add(directory);
					} else {
						directoryUpdateList.add(directory);
					}
					directory.setGenericName(genericName);
					directory.setDosageFormName(dosageFormName);
					directory.setModel(model);
					directory.setQualityLevel(qualityLevel);
					if (!StringUtils.isEmpty(qualityLevel)) {
						if (StringUtils.isEmpty(qualsMap.get(upExcel[4]))) {
							throw new RuntimeException("第"+i+"条质量层次有误");
						}
						directory.setQualityLevelCode(qualsMap.get(upExcel[4]));
					}
					directory.setRcDosageFormName(rcDosageFormName);
					directory.setNote(upExcel[5]);
					directory.setMinUnit(upExcel[6]);
					directory.setProducerNames(producerNames);
					directory.setModifyDate(new Date());
					directory.setBatch(upExcel[8]);
				}
				if (directorySaveList.size() > 0) {
					System.out.println("新增条数:"+directorySaveList.size());
					directoryService.saveBatch(projectCode, directorySaveList);
				} 
				if (directoryUpdateList.size() > 0) {
					System.out.println("更新条数:"+directoryUpdateList.size());
					directoryService.updateBatch(projectCode, directoryUpdateList);
					
				}
			} catch(RuntimeException e) {
				throw e;
			} catch(Exception e) {
				throw e;
			} finally {
				workBook.close();
			}
		}
		
	}
	
	
	
}
