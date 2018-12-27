package com.shyl.msc.dm.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.util.GridFSDAO;
import com.shyl.common.util.SHA1;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.base.thread.ProductSourceAsyncTask;
import com.shyl.msc.dm.entity.GoodsHospitalSource;
import com.shyl.msc.dm.service.IDrugService;
import com.shyl.msc.dm.service.IGoodsHospitalSourceService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.FileManagement;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IFileManagementService;



@Controller
@RequestMapping("/dm/goodsSource")
public class GoodsSourceController extends BaseController {

	@Resource
	private IGoodsHospitalSourceService goodsHospitalSourceService;
	@Resource
	private IProductService	productService;
	@Resource
	private IDrugService	drugService;
	@Resource
	private GridFSDAO gridFSDAO;
	@Resource
	private IFileManagementService fileManagementService;
	@Autowired
	private ProductSourceAsyncTask productSourceAsyncTask;
	@Resource
	private IAttributeItemService attributeItemService;
	
	protected void init(WebDataBinder binder) {

	}

	@RequestMapping("/productSource")
	public String productSource() {
		return "/dm/goodsSource/productSource";
	}

	@RequestMapping("/getSign")
	@ResponseBody
	public String getSign(@CurrentUser User user) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";  
	    Random random = new Random();  
	    StringBuffer sb = new StringBuffer();  
	    for (int i = 0; i < 20; i++) {  
	        int number = random.nextInt(base.length());  
	        sb.append(base.charAt(number));  
	    }   
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"external_system", "ypk10001");
		return "{\"data\":\""+sb.toString()+"\",\"sign\":\""+SHA1.getMessageDigest(attributeItem.getField3() + sb.toString(), "SHA-1")+"\"}";
	}


	
	/**
	 * 未添加目录 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/productPage")
	@ResponseBody
	public JSONObject productPage(String productName, String producerName, 
			String dosageFormName, String productCode, PageRequest pageable, @CurrentUser User user){	
		List<String> productWords = new ArrayList<String>();
		List<String> dosageWords = new ArrayList<String>();
		List<String> producerWords = new ArrayList<String>();
		if (!StringUtils.isEmpty(producerName)) {
			producerWords = getWords(producerName, null);
		}
		if (!StringUtils.isEmpty(dosageFormName)) {
			dosageWords = getWords(dosageFormName, null);
		}
		if (!StringUtils.isEmpty(productName)) {
			List<String> stop = new ArrayList<String>();
			for (String dosa : dosageWords) {
				stop.add(dosa);
			}
			stop.add("注射剂");
			stop.add("胶囊");
			stop.add("分散片");
			stop.add("精品");
			stop.add("药膏");
			//获取产品的分词，排除剂型
			productWords = getWords(productName, stop);
		}
		
		DataGrid<Map<String, Object>> page =  productService
				.npquery(user.getProjectCode(), productWords, dosageWords, producerWords, productCode, pageable);
		
		for(Map<String,Object> map : page.getRows()){
			//如果有传递code说明该code被对应了
			if(map.get("CODE").equals(productCode)){
				map.put("selected", "true");
			}
		}
		JSONObject result = new JSONObject();
		result.put("rows", page.getRows());
		result.put("tooter", page.getFooter());
		result.put("total", page.getTotal());
		result.put("producerWords", producerWords);
		result.put("productWords", productWords);
		result.put("dosageWords", dosageWords);
		
		return result;
	}
	
	/**
	 * 添加映射关系
	 * @param model
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/mapping",method=RequestMethod.GET)
	public String mapping(ModelMap model) {
		return "/dm/goodsSource/mapping";
	}
	
	/**
	 * 添加映射关系
	 * @param model
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/mapping",method=RequestMethod.POST)
	@ResponseBody
	public Message mapping(Long id, String productCode, Double convertRatio,@CurrentUser User user) {
		Message message = new Message();
		try{
			goodsHospitalSourceService.saveMapping(user.getProjectCode(), id, productCode, convertRatio);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return message;
	}

	/**
	 * 医院就编码分页
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/sourcePage",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<GoodsHospitalSource> sourcePage(PageRequest page, @CurrentUser User user) {
		Sort sort = new Sort(new Order(Direction.DESC, "productName"),
				new Order(Direction.DESC, "dosageFormName"), new Order(Direction.DESC, "id"));
		page.setSort(sort);
		return goodsHospitalSourceService.query(user.getProjectCode(), page);
	}

	/**
	 * 删除数据
	 * @param model
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/delete")
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user) {
		Message message = new Message();
		try{
			goodsHospitalSourceService.delete(user.getProjectCode(), id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return message;
	}
	


	/**
	 * 添加映射关系
	 * @param model
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/deleteSource")
	@ResponseBody
	public Message deleteOld(Long id, @CurrentUser User user) {
		Message message = new Message();
		try{
			goodsHospitalSourceService.delete(user.getProjectCode(), id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return message;
	}
	

	@RequestMapping(value="/exportExcel")
	public void exportExcel(PageRequest page, HttpServletResponse resp, 
			@CurrentUser User user, Integer status)  {
		Sort sort = new Sort(new Order(Direction.ASC,"hospitalName"),
				new Order(Direction.DESC,"productCode"));
		page.setSort(sort);
	 	page.setPageSize(500);
	 	Map<String, Object> query = page.getQuery();
	 	if (query == null) {
	 		query = new HashMap<String, Object>();
	 		page.setQuery(query);
	 	}
	 	if (user.getOrganization().getOrgType() != null && user.getOrganization().getOrgType() == 1) {
	 		query.put("t#hospitalId_L_EQ", user.getOrganization().getOrgId());
	 	}
	 	
	 	DataGrid<Map<String,Object>> data = goodsHospitalSourceService.npquery(user.getProjectCode(), page);
	
	 	Workbook wb = new XSSFWorkbook();
	 	String[] title = {"医院名称","医院内部药品编码","用药监管平台编码","医保编码","批准文号","药交ID",
				 "药品本位码","物价ID","通用名","药品名称","药品规格","医院转换比","最小制剂单位","包装单位","药品厂家","药品剂型","零售价","供应企业","供应价","基本药物标识",
				 "监管平台编码","监管平台通用名","监管平台药品名称","监管平台药品规格","监管平台包装规格","监管平台厂家","监管转换比"};
		 
	 	if (data.getTotal() > 0) {
			 List<Map<String,Object>> list = data.getRows();
			 String hospitalName = (String)list.get(0).get("HOSPITALNAME");
			 Sheet sh = wb.createSheet(hospitalName);
			 Row row = sh.createRow(0);
			 for (int i=0;i<title.length;i++) {
				 Cell cell = row.createCell(i);
				 cell.setCellValue(title[i]);
			 }
			 Long total = data.getTotal();
			 Long pageNum = total/200;
			 if (total%200 != 0) {
				 pageNum++;
			 }
			 int rowIndex = 1;
			 Map<String,Object> source = null;
			 for(int rownum = 1; rownum <= list.size() ; rownum++) {
				 source = list.get(rownum-1);
				 if (!hospitalName.equals(source.get("HOSPITALNAME"))) {
					 hospitalName = (String)source.get("HOSPITALNAME");
					 rowIndex=1;
					 sh = wb.createSheet(hospitalName);
					 row = sh.createRow(0);
					 for (int i=0;i<title.length;i++) {
						 Cell cell = row.createCell(i);
						 cell.setCellValue(title[i]);
					 }
				 }
				 row = sh.createRow(rowIndex);
				 Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("HOSPITALNAME"));
				 cell = row.createCell(1, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("INTERNALCODE"));
				 cell = row.createCell(2, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("YYJGCODE"));
				 cell = row.createCell(3, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("YBDRUGSNO"));
				 cell = row.createCell(4, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("AUTHORIZENO"));
				 cell = row.createCell(5, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("STANDARDCODE"));
				 cell = row.createCell(6, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRICEFILENO"));
				 cell = row.createCell(7, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("GENERICNAME"));
				 cell = row.createCell(8, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCTNAME"));
				 cell = row.createCell(9, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("MODEL"));
				 cell = row.createCell(10, Cell.CELL_TYPE_NUMERIC);
				 cell.setCellValue((String)source.get("CONVERTRATIO0"));
				 cell = row.createCell(11, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("DOSEUNIT"));
				 cell = row.createCell(12, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("UNITNAME"));
				 cell = row.createCell(13, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCERNAME"));
				 cell = row.createCell(14, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("DOSAGEFORMNAME"));
				 cell = row.createCell(15, Cell.CELL_TYPE_NUMERIC);
				 if (source.get("FinalPrice") != null) {
					 cell.setCellValue(((BigDecimal)source.get("FINALPRICE")).doubleValue());
				 } else {
					 cell.setCellValue("");
				 }
				 cell = row.createCell(16, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("VENDORNAME"));
				 cell = row.createCell(17, Cell.CELL_TYPE_NUMERIC);
				 if (source.get("BIDDINGPRICE") != null) {
					 cell.setCellValue(((BigDecimal)source.get("BIDDINGPRICE")).doubleValue());
				 } else {
					 cell.setCellValue("");
				 }
				 cell = row.createCell(18, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("BaseMark"));
				 //获取资源
				 cell = row.createCell(19, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCTCODE"));
				 cell = row.createCell(20, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("GENERICNAME0"));
				 cell = row.createCell(21, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCTNAME"));
				 cell = row.createCell(22, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("MODEL0"));
				 cell = row.createCell(23, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PACKDESC0"));
				 cell = row.createCell(24, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCERNAME0"));
				 cell = row.createCell(25, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("CONVERTRATIO1"));
				 if (page.getPage()  != pageNum
						 && rownum == list.size()) {
					 page.setPage(page.getPage()+1);
					 data = goodsHospitalSourceService.npquery(user.getProjectCode(), page);
					 list = data.getRows();
					 rownum=0;
				 }
				 rowIndex++;
			 }
		 } else {
			 Sheet sh = wb.createSheet("sheet1");
			 Row row = sh.createRow(0);
			 for (int i=0;i<title.length;i++) {
				 Cell cell = row.createCell(i);
				 cell.setCellValue(title[i]);
			 }
		 }
		 resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=product.xlsx");
		 OutputStream out;
		 try {
			 out = resp.getOutputStream();
			 wb.write(out);
			 out.flush();
			 wb.close();
			 out.close();
		 } catch (IOException e) {
			e.printStackTrace();
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
				  this.doExcel(user.getProjectCode(), myfile, 500);
				  message.setMsg("导入成功");
			  }else {
				  message.setMsg("请用正确格式导入");
			  }
		 }  catch (Exception e) {  
			 	e.printStackTrace();
				message.setSuccess(false);
				message.setMsg(e.getMessage());
		 }  
		 return message;
	}

	/**
	 * 一次读取多少条
	 * @param file
	 * @param readLine
	 * @return
	 * @throws Exception
	 */
	private void doExcel(String projectCode, MultipartFile file, Integer readLine) throws Exception {
		String[][] upExcel = null;
		InputStream input = file.getInputStream();
		Workbook workBook = null;
		//文件名称
		String filename = file.getOriginalFilename();
		if(filename.endsWith(".xlsx")) {
			workBook = new XSSFWorkbook(input);
		} else {
			workBook = new HSSFWorkbook(input);
		}
		try {
			Sheet sheet = workBook.getSheetAt(0);
			if (sheet != null) {
				Row row0 = sheet.getRow(0);
				int rowLen = sheet.getPhysicalNumberOfRows() - 1;
				if (readLine == 0) {
					readLine = rowLen;
				}
				int a = 0;
				int b = 0;
				int i = 0;
				// i = 0 是标题栏
					for (i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
						Row row = sheet.getRow(i);
						if (row == null) {
							System.out.println("执行到第"+i+"行，完毕");
							break;
						}
						//每次只读
						if (i%readLine == 1 || upExcel == null) {
							if (rowLen - i + 1 < readLine) {
								readLine = rowLen - i + 1;
							}
							upExcel = new String[readLine][row0.getPhysicalNumberOfCells()];
							a=0;
						}
						for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
							Cell cell = row.getCell(j);
							String cellStr = ExcelUtil.getValue(cell);
							if (j == 0 && StringUtils.isEmpty(cellStr)) {
								break;
							}
							upExcel[a][j] = cellStr;
						}
						//每次读取的最后一次或者是最后一条数据，保存信息
						if (i%readLine == 0 || i >= rowLen) {
							System.out.println("保存信息完成");
							goodsHospitalSourceService.saveGoodsHospitalSource(projectCode, upExcel);
						}
						a++;
					}
					System.out.println(rowLen+":"+i);
					
				
			}
		} catch(Exception e) {
			throw e;
		} finally {
			workBook.close();
		}
	}
	
	
	
	@RequestMapping(value="/exportProduct")
	public void exportProduct(PageRequest page, HttpServletResponse resp, Integer status,@CurrentUser User user)  {
		 Sort sort = new Sort(new Order(Direction.ASC,"genericname"),
				 new Order(Direction.ASC,"dosageformname"),
				 new Order(Direction.ASC,"model"),
				 new Order(Direction.ASC,"convertratio0"),
				 new Order(Direction.ASC,"producername"),
				 new Order(Direction.DESC,"productCode"));
		 page.setSort(sort);
		 page.setPageSize(200);
		 DataGrid<GoodsHospitalSource> data = goodsHospitalSourceService.query(user.getProjectCode(), page);
		 
		 Workbook wb = new XSSFWorkbook();

		/**序号0	药交ID1	物价产品ID2	产品编码3	
		*通用名4	药品名称5
		*商品名6	药品规格7	药品单位8	包装规格9	药品剂型10	生产企业11	入围属性12	
		*针剂标志13	药品性质（中药.中药；西药.西药）14	单位换算比15	最小制剂单位16	
		*基本药物类型17	在用标志18	目录ID19	目录名称20	目录类型21	合同类型22	
		*药品本位码23	注册证号24	批准文号25	抗菌药物标识26	
		*抗菌药DDD值27	抗菌药物级别28	剂量29	
		*剂量单位30	医保剂型归类31	深圳医保编码32	医保类别33	
		**药理分类-第1级34	药理分类-第2级35	药理分类-第3级36	药理分类-第4级37	特殊药品分类38
		**/

		 String[] title = {"序号","药交ID","物价产品ID","产品编码","通用名","药品名称","商品名","药品规格","药品单位","包装规格","药品剂型","生产企业","入围属性","针剂标志","药品性质（中药.中药；西药.西药）","单位换算比","最小制剂单位","基本药物类型","在用标志","目录ID","目录名称","目录类型","合同类型","药品本位码","注册证号","批准文号","抗菌药物标识","抗菌药DDD值","抗菌药物级别","剂量","剂量单位","医保剂型归类","深圳医保编码","医保类别","药理分类-第1级","药理分类-第2级","药理分类-第3级","药理分类-第4级","特殊药品分类"};

		 if (data.getTotal() > 0) {
			 List<GoodsHospitalSource> list = data.getRows();
			 Sheet sh = wb.createSheet("sheet1");
			 Row row = sh.createRow(0);
			 for (int i=0;i<title.length;i++) {
				 Cell cell = row.createCell(i);
				 cell.setCellValue(title[i]);
			 }
			 Long total = data.getTotal();
			 Long pageNum = total/200;
			 if (total%200 != 0) {
				 pageNum++;
			 }
			 int rowIndex = 1;
			 GoodsHospitalSource source = null;
			 for(int rownum = 1; rownum <= list.size() ; rownum++) {
				 source = list.get(rownum-1);
				 row = sh.createRow(rowIndex);
				 String model = source.getModel();
				 String packDesc = "";
				 String minUnit = "";
				 String doseType = "";
				 String genericName = source.getGenericName();
				 Map<String, String> map = null;
				 if (!StringUtils.isEmpty(source.getGenericName())) {
					 map = drugService.queryDrugInfoByName(user.getProjectCode(), source.getGenericName(), source.getProductCode());
				 } else if(!StringUtils.isEmpty(source.getProductName())) {
					 map = drugService.queryDrugInfoByName(user.getProjectCode(), source.getProductName(), source.getProductCode());
				 }
				 
				 //处理剂型数
				 if (map != null && !map.isEmpty()) {
					 if (!StringUtils.isEmpty(source.getDosageFormName()) 
								 && source.getDosageFormName().indexOf(source.getDosageFormName()) >= 0) {
						 doseType = "针剂";
					 }
					 if (!StringUtils.isEmpty(model)) {
						//去掉括号
						model = model.replaceAll("（.{0,}）", "").replaceAll("\\(.{0,}\\)", "")
								.replaceAll("微克", "μg").replaceAll("毫克", "mg")
								.replaceAll("克", "g").replaceAll("毫升", "ml")
								.replaceAll("ug", "μg");
						if (model.indexOf("*")>0) {
							String[] models = model.split("\\*");
							if (models.length ==2) {
								packDesc = models[1];
								model = models[0];
							}
						} else if (model.indexOf("×") >0) {
							String[] models = model.split("×");
							if (models.length ==2) {
								packDesc = models[1];
								model = models[0];
							}
						} else if (model.indexOf(":") > 0) {
							String[] models = model.split(":");
							if (models.length ==2) {
								packDesc = models[1];
								model = models[0];
							}
						} else if (model.indexOf("：") >0) {
							String[] models = model.split("：");
							if (models.length ==2) {
								packDesc = models[1];
								model = models[0];
							}
						}
						//处理包装规格
						//拆分包装规格
						int i = packDesc.indexOf("/");
						if (i>0) {
							if (StringUtils.isEmpty(minUnit)) {
								minUnit = packDesc.substring(i-1, i);
							}
							packDesc = source.getConvertRatio0() + minUnit + "/"+source.getUnitName();
						}
					 }
					
			 	} else {
				 	map = new HashMap<String, String>();
			 	}
			 	Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(row.getRowNum());
			 	cell = row.createCell(1, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(source.getYjCode());
			 	cell = row.createCell(2, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(source.getPriceFileNo());
			 	cell = row.createCell(4, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(genericName);
			 	cell = row.createCell(5, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(source.getProductName());
			 	cell = row.createCell(7, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(model);
			 	cell = row.createCell(8, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(source.getUnitName());
			 	cell = row.createCell(9, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(packDesc);
			 	cell = row.createCell(10, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(source.getDosageFormName());
			 	cell = row.createCell(11, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(source.getProducerName());
			 	cell = row.createCell(13, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(doseType);
			 	cell = row.createCell(14, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("YPXZ"));
			 	if( source.getConvertRatio0() != null) {
				 	cell = row.createCell(15, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(source.getConvertRatio0().toString());
			 	};
			 	cell = row.createCell(16, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(minUnit);
			 	cell = row.createCell(17, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("BASEDRUGTYPENAME"));
			 	cell = row.createCell(23, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(source.getStandardCode());
			 	cell = row.createCell(25, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(source.getAuthorizeNo());
			 	cell = row.createCell(27, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("DDD"));
			 	cell = row.createCell(28, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("ABSDRUGTYPENAME"));
			 	if (!StringUtils.isEmpty(source.getProductCode())) {
				 	cell = row.createCell(29, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("DOSE"));
				 	cell = row.createCell(30, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("DOSEUNIT"));
			 	}
			 	cell = row.createCell(34, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("DRUGTYPE0"));
			 	cell = row.createCell(35, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("DRUGTYPE1"));
			 	cell = row.createCell(36, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("DRUGTYPE2"));
			 	cell = row.createCell(37, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("DRUGTYPE3"));
			 	cell = row.createCell(38, Cell.CELL_TYPE_STRING);
			 	cell.setCellValue(map.get("SPECIALDRUGTYPENAME"));
			 	if (!StringUtils.isEmpty(source.getProductCode())) {
				 	cell = row.createCell(39, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("PRODUCTNAME"));
				 	cell = row.createCell(40, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("GENERICNAME"));
			 	}
			 
				if (page.getPage()  != pageNum
						 && rownum == list.size()) {
				 	page.setPage(page.getPage()+1);
				 	data = goodsHospitalSourceService.query(user.getProjectCode(), page);
				 	list = data.getRows();
				 	rownum=0;
			 	}
			 	rowIndex++;
			 }
		 } else {
		 	Sheet sh = wb.createSheet("");
		 	Row row = sh.createRow(0);
		 	for (int i=0;i<title.length;i++) {
		 		Cell cell = row.createCell(i);
		 		cell.setCellValue(title[i]);
		 	}
	 	}
	 	resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		resp.setHeader("Content-Disposition", "attachment; filename=product.xlsx");
		OutputStream out;
	 	try {
	 		out = resp.getOutputStream();
	 		wb.write(out);  
	 		out.flush();
	 		wb.close();
	 		out.close();
	 	} catch (IOException e) {
	 		e.printStackTrace();
	 	}  
	}

	@RequestMapping(value="/setError")
	@ResponseBody
	public Message setError(Long id, Long status, @CurrentUser User user)  {
		Message message = new Message();
		try{
			//修改状态
			GoodsHospitalSource source = goodsHospitalSourceService.getById(user.getProjectCode(), id);
			source.setStatus(status);
			if (status == 0 || status == 5 ) {
				source.setProductCode(null);
				source.setConvertRatio(null);
			}
			goodsHospitalSourceService.update(user.getProjectCode(), source);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return message;
	}

	@RequestMapping(value="/downFile")
	public void downFile(String key, HttpServletResponse resp, @CurrentUser User user) {
		//获取医院未对照或已经对照数据的方法
		List<FileManagement> fmList = fileManagementService.findByKeyFlag(user.getProjectCode(), "GoodsHospitalSource_"+key);
		FileManagement fm= null;
		if (fmList.size() > 0) {
			//取最后一个
			fm = fmList.get(0);
		}
		if (fm != null) {
			gridFSDAO.findFileByIdToOutputStream(fm.getFileURL(), "GoodsHospitalSource", resp);
		}
	}

	@RequestMapping(value="/mkFileByNeedCreate")
	@ResponseBody
	public Message mkFileByNeedCreate(@CurrentUser User user) {
		//开启线程
		productSourceAsyncTask.mkFileByNeedCreate(user);
		Message message = new Message();
		message.setSuccess(true);
		message.setMsg("已启动文件生成线程，请关注我的工作台的系统消息");
		return message;
	}
	

	
	/**
	 * 获取分词
	 * @param searchKey
	 * @param topWords
	 * @return
	 * @throws IOException
	 */
	private List<String> getWords(String searchKey, List<String> topWords) {
		try {
			List<String> words = new ArrayList<String>();
	        Reader read = new InputStreamReader(new ByteArrayInputStream(searchKey.getBytes()));  
	        IKSegmentation iks = new IKSegmentation(read,true);//true开启只能分词模式，如果不设置默认为false，也就是细粒度分割  
	        
	        Lexeme t;  
	        //判断是否存在分词信息
	        while ((t = iks.next()) != null) {  
	            //关键次
	            String word = t.getLexemeText();
	        	if (topWords != null && topWords.contains(word)) {
	        		continue;
	        	} 
	            //只有大于等于1个长度的词语才做分词
	            if (word.length() > 1) {
	                words.add(word);
	            }
	        }
	        if (words.size() == 0) {
	            words.add(searchKey);
	        }
	        return words;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
