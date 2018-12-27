package com.shyl.msc.menu.controller;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.util.StringUtil;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.util.Pinyin4jUtil;
import com.shyl.common.util.SHA1;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.DosageForm;
import com.shyl.msc.dm.entity.Drug;
import com.shyl.msc.dm.entity.DrugType;
import com.shyl.msc.dm.service.IDosageFormService;
import com.shyl.msc.dm.service.IDrugService;
import com.shyl.msc.dm.service.IDrugTypeService;
import com.shyl.msc.dm.service.IGoodsHospitalSourceService;
import com.shyl.msc.menu.entity.ProductSource;
import com.shyl.msc.menu.service.IProductSourceService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 药品详情
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/menu/productSource")
public class ProductSourceController extends BaseController {

	@Resource
	private IProductSourceService	productSourceService;
	@Resource
	private IGoodsHospitalSourceService	goodsHospitalSourceService;
	@Resource
	private IDrugTypeService	drugTypeService;
	@Resource
	private IDosageFormService	dosageFormService;
	@Resource
	private IDrugService	drugService;
	@Resource
	private ICompanyService	companyService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	protected void init(WebDataBinder arg) {
		
	}

	@RequestMapping("")
	public String home() {
		return "/menu/productSource/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ProductSource> page(PageRequest pageable, @CurrentUser User user, HttpServletRequest req, HttpServletResponse resp) {
		if (user == null) {
			resp.setHeader("Access-Control-Allow-Origin", "*");
			String data = req.getParameter("data");
			String sign = req.getParameter("sign");
			//TODO
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("","external_system", "ypk10001");
			
			if (StringUtils.isEmpty(data) || StringUtils.isEmpty(sign) || attributeItem == null
					|| !SHA1.checkSign(attributeItem.getField3(), data, sign)) {
				return new DataGrid<ProductSource>();
			}
		}
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		return productSourceService.query("", pageable);
	}

	@RequestMapping(value="/add",method = RequestMethod.GET)
	public String add() {
		return "/menu/productSource/add";
	}

	@RequestMapping(value="/edit",method = RequestMethod.GET)
	public String edit() {
		return "/menu/productSource/edit";
	}
	
	@RequestMapping(value="/save",method = RequestMethod.POST)
	@ResponseBody
	public Message save(ProductSource source, @CurrentUser User user) {
		Message msg = new Message();
		try {
			setData(user.getProjectCode(), source);
			if (source.getId() == null) {
				this.productSourceService.save(user.getProjectCode(), source);
			} else {
				this.productSourceService.update(user.getProjectCode(), source);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("保存失败");
			msg.setSuccess(false);
		}
		return msg;
	}


	@RequestMapping(value="/delete",method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user) {
		Message msg = new Message();
		try {
			this.productSourceService.delete(user.getProjectCode(), id);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("保存失败");
			msg.setSuccess(false);
		}
		return msg;
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
		 }  catch (RuntimeException e) {  
			 	e.printStackTrace();
				message.setSuccess(false);
				message.setMsg(e.getMessage());
		 }  catch (Exception e) {  
			 	e.printStackTrace();
				message.setSuccess(false);
				message.setMsg(e.getMessage());
		 }  
		 return message;
	}

	/**
	 * 一次读取多少条
	 * @param projectCode
	 * @param myfile
	 * @param readLine
	 * @throws Exception
	 */
	private void doExcel(String projectCode, MultipartFile myfile, Integer readLine) throws Exception {
		
		InputStream input = myfile.getInputStream();
		Workbook workBook = null;
		//文件名称
		String filename = myfile.getOriginalFilename();
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
				int cellLen = row0.getPhysicalNumberOfCells();
				String[] upExcel = new String[cellLen];
				if (readLine == 0) {
					readLine = rowLen;
				}

				//获取基本数据,放置到map中
				DrugType kjywType = drugTypeService.findByCode(projectCode, "kjywjb");
				DrugType tsType = drugTypeService.findByCode(projectCode, "tsypfl");
				DrugType ybType = drugTypeService.findByCode(projectCode, "ybyp");
				DrugType jylx = drugTypeService.findByCode(projectCode, "jylx");
				DrugType ybypylfl = drugTypeService.findByCode(projectCode, "ybypylfl");
				Map<String, Long> baseMap = new HashMap<String, Long>();
				baseMap.put("kjywType", kjywType.getId());
				baseMap.put("tsType", tsType.getId());
				baseMap.put("ybType", ybType.getId());
				baseMap.put("jylx", jylx.getId());
				baseMap.put("ybypylfl", ybypylfl.getId());
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
					baseMap.put("rowIndex", (long)i);
					//每次读取的最后一次或者是最后一条数据，保存信息
					this.saveProduct(projectCode, upExcel, baseMap);
				}
			}
		} catch(Exception e) {
			throw e;
		} finally {
			workBook.close();
		}
		
	}
	
	/**
	 * 重置编码
	 * @return
	 */
	@RequestMapping(value="/resetCode")
	@ResponseBody
	public Message resetCode(@CurrentUser User user) {
		Message message = new Message();
		try{
			productSourceService.deletByReset();
			int result = 1;
			while(result != 0) {
				result = this.resetCode(user.getProjectCode(), result);
			}
		}catch(RuntimeException e){
			e.printStackTrace();
			message.setMsg(e.getMessage());
			message.setSuccess(false);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return message;
	}

	private void setData(String projectCode, ProductSource product){
		System.out.println(product.getCompany().getId());
		Company	company = companyService.getById(projectCode, product.getCompany().getId());
		if(product.getUnit()!=null&&!product.getUnit().equals("")){
			AttributeItem attributeItem = attributeItemService.getById(projectCode, new Long(product.getUnit()));
			if(attributeItem != null){
				product.setUnitName(attributeItem.getField2());
			}			
		}
		if(product.getBaseDrugType()!=null&&!product.getBaseDrugType().equals("")){
			DrugType drugType = drugTypeService.getById(projectCode, new Long(product.getBaseDrugType()));
			if(drugType != null){
				product.setBaseDrugTypeName(drugType.getName());
			}			
		}
		if(product.getInsuranceDrugType()!=null&&!product.getInsuranceDrugType().equals("")){
			DrugType drugType = drugTypeService.getById(projectCode, new Long(product.getInsuranceDrugType()));
			if(drugType != null){
				product.setInsuranceDrugTypeName(drugType.getName());
			}			
		}
		product.setModifyDate(new Date());
		
		if (StringUtils.isEmpty(product.getPinyin())) {
			product.setPinyin(Pinyin4jUtil.getPinYinHeadChar(product.getName()));
		}
		if (StringUtils.isEmpty(product.getCode())) {
			product.setCode(productSourceService.getMaxCode());
		}
		
		if (StringUtils.isEmpty(product.getProductGCode())) {
			Drug drug = drugService.getById(projectCode, product.getDrug().getId());
			DosageForm form = dosageFormService.getById(projectCode, drug.getDosageFormId());
			
			//中西药分类
			Long drugType = drug.getDrugType();
			//抗菌药物等级
			Long absDrugType = drug.getAbsDrugType();
			//药理分类
			Long pharmacologyType = drug.getPharmacologyType();
			//特殊类别
			Long specialDrugType = drug.getSpecialDrugType();
			//默认西药
			String ypxzCode = "X";
			//药品类型编码
			String pharmacologyTypeCode = "00000000";
			String absDrugTypeCode = "0";
			String specialDrugTypeCode = "0";
			String genericCode = drug.getGenericCode();
			
			if (drugType != null) {
				AttributeItem item = attributeItemService.getById(projectCode, drugType);
				ypxzCode = item.getField3();
			}
			if (absDrugType != null) {
				DrugType type = drugTypeService.getById(projectCode, absDrugType);
				absDrugTypeCode = type.getCode();
			}
			if (pharmacologyType != null) {
				DrugType type = drugTypeService.getById(projectCode, pharmacologyType);
				pharmacologyTypeCode = type.getCode();
			}
			if (specialDrugType != null) {
				DrugType type = drugTypeService.getById(projectCode, specialDrugType);
				specialDrugTypeCode = type.getCode();
			}
			
			if (genericCode == null) {
				genericCode = "00000";
			}
			
			//新编码
			String newcode = ypxzCode + genericCode +"-"+ form.getCode()+"-"+
					company.getCode()+"-"+pharmacologyTypeCode;
			//规格序号
			String modelCode = productSourceService.getMaxModelCode(newcode, product.getModel());
			//包装序号
			String packCode = productSourceService.getMaxPackCode(newcode, product.getModel(), product.getPackDesc());
			//拼接包装规格
			newcode = newcode + "-" + modelCode + packCode;
			newcode += "-" + specialDrugTypeCode + absDrugTypeCode;
			byte[] bytes = newcode.getBytes();
			int id = 1;
			for(byte c : bytes) {
				id = (int)c ^ id;
			}
			//加上身份码
			newcode = newcode + "-" + (id%10);
			product.setProductGCode(newcode);
			product.setModelCode(modelCode);
			product.setPackCode(packCode);
		}else {
			String code = "-"+company.getCode()+"-";
			String producttGCode =  product.getProductGCode();
			
			String frontCode = producttGCode.substring(0, 13);
			String centerCode = producttGCode.substring(20,36);
			String newCode = frontCode+code+centerCode;
			byte[] bytes = newCode.getBytes();
			int id = 1;
			for(byte c : bytes) {
				id = (int)c ^ id;
			}
			producttGCode = newCode+"-"+ (id%10);
			product.setProductGCode(producttGCode);
		}
	}
	 
	private ProductSource saveProduct(String projectCode, String[] row, Map<String, Long> map) {

		/*
		通用名1	药品名称2
		商品名3	药品规格4	药品单位5	包装规格6	药品剂型7	生产企业8	药品性质（中药.中药；西药.西药）9	单位换算比10	最小制剂单位11	
		基本药物类型12	 药品本位码13	注册证号14	批准文号15	
		抗菌药DDD值16	抗菌药物级别17	剂量18	
		剂量单位19	医保剂型归类20	深圳医保编码21	医保类别22	
		药理分类-第1级23	药理分类-第2级24	药理分类-第3级25	药理分类-第4级26	特殊药品分类27*/

		
		Long rowIndex= map.get("rowIndex");

		String genericName = row[0];
		String productName = row[1];
		String tradeName = row[2];
		String model = row[3];
		String unitName = row[4];
		String packDesc = row[5];
		String dosageFormName = row[6];
		String producerName = row[7];
		String ypxz = row[8];
		String num = row[9];
		String minUnit = row[10];
		String baseDrugTypeName = row[11];
		String standardCode = row[12];
		String regeditNo = row[13];
		String authorizeNo = row[14];
		String ddd = row[15];
		String absDrugTypeName = row[16];
		String dose = row[17];
		String doseUnit = row[18];
		String ybDoseType=row[19];
		String ybdrugsNO=row[20];
		String insuranceDrugTypeName=row[21];
		String drugType0=row[22];
		String drugType1=row[23];
		String drugType2=row[24];
		String drugType3=row[25];
		String specialDrugTypeName=row[26];
		String notes=row[27];
		String nationalAuthorizeNo=row[28];
		if (StringUtils.isEmpty(genericName)) {
			throw new RuntimeException("第"+rowIndex+"行通用名不允许为空");
		}
		if (StringUtils.isEmpty(dosageFormName)) {
			throw new RuntimeException("第"+rowIndex+"行剂型不允许为空");
		}
		if (StringUtils.isEmpty(producerName)) {
			throw new RuntimeException("第"+rowIndex+"行公司名称不允许为空");
		}
		if (StringUtils.isEmpty(standardCode)&&StringUtils.isEmpty(regeditNo)
				&&StringUtils.isEmpty(authorizeNo)&&StringUtils.isEmpty(nationalAuthorizeNo)) {
			throw new RuntimeException("第"+rowIndex+"行不允许本位吗、注册证号、批准文号、国药准字都为空");
		}
		if (StringUtils.isEmpty(drugType0) || StringUtils.isEmpty(drugType1) 
				|| StringUtils.isEmpty(drugType2) ||StringUtils.isEmpty(drugType3)) {
			throw new RuntimeException("第"+rowIndex+"行药理分类不允许为空");
		}
		/** 格式处理 **/
		if (!StringUtils.isEmpty(model)) {
			model = model.replaceAll("单位", "IU").replaceAll("（", "(")
					.replaceAll("）", ")").replaceAll("）", ")")
					.replaceAll("：", ":");
		}
		if (!StringUtils.isEmpty(authorizeNo)) {
			authorizeNo = authorizeNo.replaceAll("国药准字", "")
					.replaceAll("粤药制字", "");
			
		}
		if (!StringUtils.isEmpty(doseUnit)) {
			doseUnit = doseUnit.replaceAll("单位", "IU");
		}

		String unit = "";
		//中西药分类
		Long drugType = null;
		//基本药物
		Long baseDrugType = null;
		//抗菌药物等级
		Long absDrugType = null;
		//医保类别
		Long insuranceDrugType = null;
		//特殊类别
		Long specialDrugType = null;
		//药理分类
		Long pharmacologyType = null;
		//默认西药
		String ypxzCode = "X";
		//药品类型编码
		String drugTypeCode = "";
		String absDrugTypeCode = "0";
		String specialDrugTypeCode = "0";

		//本位码存在情况下，设置后倒入
		if (!StringUtils.isEmpty(standardCode)) {
			ProductSource product = productSourceService.getByStandardCodeAndPack(standardCode, genericName, model, packDesc);
			if (product == null) {
				product = productSourceService.getByStandardCodeOnly(standardCode);
				if (product != null) {
					this.merge(projectCode, product, model, num, minUnit, unitName);
					return product;
				}
			} else {
				System.out.println("序号"+rowIndex+";序号："+row[0]+"；重复id:"+product.getCode()+";"+genericName+";"+packDesc);
				return product;
			}
			
		}
			
		//保存包装单位
		if (!StringUtils.isEmpty(minUnit)) {
			productSourceService.saveAttribute("packType", minUnit);
		}
		//保存包装单位
		if (!StringUtils.isEmpty(unitName)) {
			AttributeItem item = productSourceService.saveAttribute("packType", unitName);
			unit = String.valueOf(item.getId());
		}
		//保存包装单位
		if (!StringUtils.isEmpty(ypxz)) {
			AttributeItem item = productSourceService.saveAttribute("drug_drugType", ypxz);
			drugType = item.getId();
			ypxzCode = item.getField3();
		}
		if (!StringUtils.isEmpty(baseDrugTypeName)) {
			DrugType type = productSourceService.saveDrugType(map.get("jylx"), baseDrugTypeName,
					Long.toString(map.get("jylx")), "J");
			baseDrugType = type.getId();
		}
		if (!StringUtils.isEmpty(absDrugTypeName)) {
			DrugType type = productSourceService.saveDrugType(map.get("kjywType"), absDrugTypeName,
					Long.toString(map.get("kjywType")), "K");
			absDrugType = type.getId();
			absDrugTypeCode = type.getCode();
		}
		if (!StringUtils.isEmpty(insuranceDrugTypeName)) {
			DrugType type = productSourceService.saveDrugType(map.get("ybType"), insuranceDrugTypeName,
					Long.toString(map.get("ybType")), "Y");
			insuranceDrugType = type.getId();
		}
		if (!StringUtils.isEmpty(specialDrugTypeName)) {
			DrugType type = productSourceService.saveDrugType(map.get("tsType"), specialDrugTypeName,
					Long.toString(map.get("tsType")), "T");
			specialDrugType = type.getId();
			specialDrugTypeCode = type.getCode();
		}
		String dosageFormParent = null;
		if (dosageFormName.indexOf("-") > 0) {
			String[] s = dosageFormName.split("-");
			dosageFormName = s[1];
			dosageFormParent = s[0];
		}
		//处理剂型
		DosageForm form = null;
		try {
			form = dosageFormService.getByName(projectCode,dosageFormName, dosageFormParent);
			if (form == null) {
				throw new RuntimeException("第"+rowIndex+"行"+dosageFormName+"剂型未维护");
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("第"+rowIndex+"行"+dosageFormName+"剂型存在错误");
		}
		//处理药品目录
		//AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("", "publicUser", "dataSource");
		//String dataSource = attributeItem ==null ?"" :attributeItem.getField3();
		Drug drug = drugService.getByName(projectCode, genericName, dosageFormName);
		if (drug == null) {
			drug = new Drug();
			//不为空开始处理级别数据
			//1级
			if (!StringUtils.isEmpty(drugType0)) {
				String treePath = map.get("ybypylfl").toString();
				DrugType type = productSourceService.saveDrugType(map.get("ybypylfl"), drugType0, treePath, "");
				pharmacologyType = type.getId();
				drugTypeCode = type.getCode();
				//2级
				if (!StringUtils.isEmpty(drugType1)) {
					treePath = treePath + "," + Long.toString(pharmacologyType);
					type = productSourceService.saveDrugType(pharmacologyType, drugType1, treePath, type.getCode());
					pharmacologyType = type.getId();
					drugTypeCode = type.getCode();
					//3级
					if (!StringUtils.isEmpty(drugType2)) {
						treePath = treePath + ","+Long.toString(pharmacologyType);
						type = productSourceService.saveDrugType(pharmacologyType, drugType2, treePath, type.getCode());
						pharmacologyType = type.getId();
						drugTypeCode = type.getCode();
						
						//4级
						if (!StringUtils.isEmpty(drugType3)) {
							treePath = treePath + ","+Long.toString(pharmacologyType);
							type = productSourceService.saveDrugType(pharmacologyType, drugType3, treePath, type.getCode());
							pharmacologyType = type.getId();
							drugTypeCode = type.getCode();
						}
					}
				}
			} else {
				//没设置药理分区，去其他通用名下面取
				Drug drug0 = drugService.getByNameOnly(projectCode, genericName);
				if (drug0 != null && drug0.getPharmacologyType() != null) {
					DrugType type = drugTypeService.getById(projectCode, drug0.getPharmacologyType());
					drugTypeCode = type.getCode();
					pharmacologyType = type.getId();
				} else {
					drugTypeCode = "00000";
				}
			}
			drug.setDrugType(drugType);
			drug.setPharmacologyType(pharmacologyType);
			drug.setAbsDrugType(absDrugType);
			drug.setSpecialDrugType(specialDrugType);
			//以下是剂型信息
			drug.setGenericName(genericName);

			drug.setGenericCode(drugService.getMaxCode(projectCode, genericName));

			drug.setCode(drug.getGenericCode() + "-" + form.getCode());
			drug.setDosageFormId(form.getId());
			drug.setDosageFormName(dosageFormName);
			drug = drugService.save(projectCode, drug);
		} else {
			if (drug.getPharmacologyType() != null) {
				DrugType type = drugTypeService.getById(projectCode, drug.getPharmacologyType());
				drugTypeCode = type.getCode();
			} else {
				//没设置药理分区，去其他通用名下面取
				Drug drug0 = drugService.getByNameOnly(projectCode, genericName);
				if (drug0 != null && drug0.getPharmacologyType() != null) {
					DrugType type = drugTypeService.getById(projectCode, drug.getPharmacologyType());
					drugTypeCode = type.getCode();
					drug.setDrugType(type.getId());
					drug = drugService.update(projectCode, drug);
				} else {
					drugTypeCode = "00000";
				}
			}
		}
		
		//处理生产厂家
		Company company = companyService.findByName(projectCode, producerName);
		if (company == null) {
			company = new Company();
			company.setCode(companyService.getMaxCode(projectCode));
			company.setIsProducer(1);
			company.setIsDisabled(0);
			company.setFullName(producerName);
			company = companyService.save(projectCode, company);
		}
		
		//新编码
		String newcode = ypxzCode + drug.getGenericCode()+"-"+form.getCode()+"-"+
				company.getCode() + "-" + drugTypeCode;
		//资源
		ProductSource product = productSourceService.getByNewcode(newcode, model, packDesc);
		if (product == null) {
			product = new ProductSource();
		} else {
			System.out.println("序号"+rowIndex+";序号："+row[0]+"；修改id:"+product.getCode());
			return product;
		}
		//判断是否存在转换比
		if (!StringUtils.isEmpty(num) && StringUtil.isNumber(num)) {
			product.setConvertRatio(new BigDecimal(num));
		}

		if (!StringUtils.isEmpty(model)) {
			product.setModel(model);
		}
		if (!StringUtils.isEmpty(unitName)) {
			product.setUnitName(unitName);
		}
		if (!StringUtils.isEmpty(minUnit)) {
			product.setMinunit(minUnit);
		}
		product.setCompany(company);
		product.setDrug(drug);
		
		product.setName(productName);
		product.setTradeName(tradeName);
		product.setPackDesc(packDesc);
		
		product.setYbDoseType(ybDoseType);
		product.setBaseDrugType(baseDrugType);
		product.setInsuranceDrugType(insuranceDrugType);
		product.setBaseDrugTypeName(baseDrugTypeName);
		product.setAbsDrugTypeName(absDrugTypeName);
		product.setInsuranceDrugTypeName(insuranceDrugTypeName);
		product.setImportFileNo(regeditNo);
		product.setAuthorizeNo(authorizeNo);
		product.setNationalAuthorizeNo(nationalAuthorizeNo);
		product.setDosageForm(form);
		product.setDdd(ddd);
		if (!StringUtils.isEmpty(dose) && StringUtil.isNumber(dose)) {
			product.setDose(new BigDecimal(dose));
		}
		product.setDoseUnit(doseUnit);
		product.setUnit(unit);
		product.setYbdrugsNO(ybdrugsNO);
		product.setStandardCode(standardCode);
		product.setIsDisabled(0);
		product.setDrugType(drug.getDrugType());
		//设置拼音
		product.setPinyin(Pinyin4jUtil.getPinYinHeadChar(product.getName()));
		product.setModifyDate(new Date());
		product.setNotes(notes);
		
		synchronized(this) {
			//规格序号
			String modelCode = productSourceService.getMaxModelCode(newcode, model);
			//包装序号
			String packCode = productSourceService.getMaxPackCode(newcode, model, packDesc);
			//拼接包装规格
			newcode = newcode + "-" + modelCode + packCode;
			newcode += "-" + specialDrugTypeCode + absDrugTypeCode;
			byte[] bytes = newcode.getBytes();
			int id = 1;
			for(byte c : bytes) {
				id = (int)c ^ id;
			}
			//加上身份码
			newcode = newcode + "-" + (id%10);
			System.out.println(productSourceService.getMaxCode());
			product.setCode(productSourceService.getMaxCode());
			product.setProductGCode(newcode);
			product.setModelCode(modelCode);
			product.setPackCode(packCode);
			product = this.productSourceService.save(projectCode, product);
		}
		return product;
	}

	/**
	 * 合并
	 * @param projectCode
	 * @param source
	 * @param model
	 * @param convertRatio
	 * @param minUnit
	 * @param unitName
	 */
	private void merge(String projectCode, ProductSource source, String model, String convertRatio,
				String minUnit, String unitName) {

		ProductSource product = new ProductSource();
		BeanUtils.copyProperties(source, product);
		product.setId(null);
		
		Drug drug = product.getDrug();
		DrugType drugType = drugTypeService.getById(projectCode, drug.getPharmacologyType());
		DosageForm form = dosageFormService.getById(projectCode, drug.getDosageFormId());
		Company company = product.getCompany();
		
		product.setModel(model);
		//默认西药
		String ypxzCode = product.getProductGCode().substring(0,1);
		
		Long specialDrugType = drug.getSpecialDrugType();
		Long absDrugType = drug.getAbsDrugType();
		
		String absDrugTypeCode = "0";
		String specialDrugTypeCode = "0";
		String drugTypeCode = "00000000";
		
		if(specialDrugType != null) {
			specialDrugTypeCode = drugTypeService.getById(projectCode, specialDrugType).getCode();
		}
		if(absDrugType != null) {
			absDrugTypeCode = drugTypeService.getById(projectCode, absDrugType).getCode();
		}
			
		if (form == null) {
			System.out.println(product.getCode()+":"+drug.getDosageFormId());
			System.out.println(product.getCode()+":"+drug.getDosageFormName());
			throw new RuntimeException("剂型"+drug.getDosageFormName()+"不存在");
		}
		
		drugTypeCode = drugType.getCode();
		
		
		//新编码
		String newcode = ypxzCode + drug.getGenericCode()+"-"+form.getCode()+"-"+
				company.getCode() + "-" + drugTypeCode;
		
		product.setConvertRatio(new BigDecimal(convertRatio));
		product.setMinunit(minUnit);
		product.setUnitName(unitName);
		product.setPackDesc(convertRatio + minUnit + "/" + product.getUnitName());
		
		//规格序号
		String modelCode = productSourceService.getMaxModelCode(newcode, product.getModel());
		//包装序号
		String packCode = productSourceService.getMaxPackCode(newcode, product.getModel(), product.getPackDesc());
		
		//拼接包装规格
		newcode = newcode + "-" + modelCode + packCode +
				"-" + specialDrugTypeCode + absDrugTypeCode;
		byte[] bytes = newcode.getBytes();
		int i = 1;
		for(byte c : bytes) {
			i = (int)c ^ i;
		}
		//加上身份码
		newcode = newcode + "-" + (i%10);
		product.setProductGCode(newcode);
		product.setCode(productSourceService.getMaxCode());
		product.setModelCode(modelCode);
		product.setPackCode(packCode);
		product.setPinyin(Pinyin4jUtil.getPinYinHeadChar(product.getName()));
		
		
		productSourceService.save(projectCode, product);
		
	}
	
	/**
	 * 重置code
	 * @param page
	 * @return
	 */
	private int resetCode(String projectCode, int page) {
		PageRequest pageable = new PageRequest();
		pageable.setPage(page);
		pageable.setPageSize(200);
		//产品资源
		DataGrid<ProductSource> data = productSourceService.query(projectCode, pageable);

		for (ProductSource product : data.getRows()) {
			Drug drug = product.getDrug();
			DrugType drugType = drugTypeService.getById(projectCode, drug.getPharmacologyType());
			DosageForm form = dosageFormService.getById(projectCode, drug.getDosageFormId());
			Company company = product.getCompany();
			
			String model = product.getModel();
			String packDesc = product.getPackDesc();

			Long specialDrugType = drug.getSpecialDrugType();
			Long absDrugType = drug.getAbsDrugType();

			//默认西药
			String ypxzCode = attributeItemService.getById(projectCode, drug.getDrugType()).getField3();
			String absDrugTypeCode = "0";
			String specialDrugTypeCode = "0";

			if(specialDrugType != null) {
				specialDrugTypeCode = drugTypeService.getById(projectCode, specialDrugType).getCode();
			}
			if(absDrugType != null) {
				absDrugTypeCode = drugTypeService.getById(projectCode, absDrugType).getCode();
			}
			
			//新编码
			String newcode = ypxzCode + drug.getGenericCode()+"-"+form.getCode()+"-"+
					company.getCode()+"-"+ drugType.getCode();
			//规格序号
			String modelCode = productSourceService.getMaxModelCode(newcode, model);
			//包装序号
			String packCode = productSourceService.getMaxPackCode(newcode, model, packDesc);
			//拼接包装规格
			newcode = newcode + "-" + modelCode + packCode;
			newcode += "-" + specialDrugTypeCode + absDrugTypeCode;
			byte[] bytes = newcode.getBytes();
			int id = 1;
			for(byte c : bytes) {
				id = (int)c ^ id;
			}
			//加上身份码
			newcode = newcode + "-" + (id%10);
			product.setProductGCode(newcode);
			product.setModelCode(modelCode);
			product.setPackCode(packCode);
			productSourceService.update(projectCode, product);
		}
		Long total = data.getTotal();
		int pageCount = total.intValue()/200;
		if (total%200 > 0) {
			pageCount++;
		}
		
		if (page == pageCount) {
			return 0;
		}
		return page+1;
	}
}
