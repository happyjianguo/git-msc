package com.shyl.msc.set.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shyl.common.util.ExcelUtil;
import com.shyl.common.util.SHA1;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IOrganizationService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 中标企业Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/set/company")
public class CompanyController extends BaseController {
	@Resource
	private ICompanyService companyService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private IOrganizationService organizationService;
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
	public String home(Model model, String companyType){
		model.addAttribute("companyType", companyType);
		return "set/company/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Company> page(PageRequest pageable, String companyType, @CurrentUser User user,
								  HttpServletRequest req, HttpServletResponse resp){
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		if (user == null) {
			resp.setHeader("Access-Control-Allow-Origin", "*");
			String data = req.getParameter("data");
			String sign = req.getParameter("sign");
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("","external_system", "ypk10001");

			if (StringUtils.isEmpty(data) || StringUtils.isEmpty(sign) || attributeItem == null
					|| !SHA1.checkSign(attributeItem.getField3(), data, sign)) {
				return new DataGrid<Company>();
			}
			return companyService.pageByType("", pageable, companyType);
		}

		return companyService.pageByType(user.getProjectCode(), pageable, companyType);
	}
	
	/**
	 * 全部查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<Company> list(PageRequest pageable, @CurrentUser User user){
		List<Company> list =  companyService.list(user.getProjectCode(), pageable);

		return list;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "set/company/add";
	}
	
	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Company company,Long combox1,Long combox2,Long combox3,@CurrentUser User user){
		Message message = new Message();
		try{
			Company obj = companyService.findByCode1(user.getProjectCode(), company.getCode());
			if(obj != null){
				throw new Exception("公司代码"+company.getCode()+"已存在");
			}
			if(combox3 != null){
				company.setRegionCode(combox3);
			}else if(combox2 != null){
				company.setRegionCode(combox2);
			}else if(combox1 != null){
				company.setRegionCode(combox1);
			}
			companyService.saveCompany(user.getProjectCode(), company);
			message.setSuccess(true);
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
	public String edit(Company company,Model model, @CurrentUser User user){
		if(company !=null){
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), company.getRegionCode());
			if(regionCode != null){
				String treePath = regionCode.getTreePath();
				if(treePath == null){
					treePath = regionCode.getId()+"";
				}else{
					treePath += ","+regionCode.getId();
				}
				System.out.println("treePath = "+treePath);
				String[] arr = treePath.split(",");
				for (int i = 0; i < arr.length; i++) {
					model.addAttribute("combox"+(i+1), arr[i]);
				}
			}
		}
		
		return "set/company/edit";
	}
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Company company,Long combox1,Long combox2,Long combox3,@CurrentUser User user){
		Message message = new Message();
		try{
			if(combox3 != null){
				company.setRegionCode(combox3);
			}else if(combox2 != null){
				company.setRegionCode(combox2);
			}else if(combox1 != null){
				company.setRegionCode(combox1);
			}
			companyService.updateCompany(user.getProjectCode(), company);
			
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
	public Message del(Long id,@CurrentUser User user){
		Message message = new Message();
		
		try{
			Company c = companyService.getById(user.getProjectCode(), id);
			companyService.deleteCompany(user.getProjectCode(), c);
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return  message;
	}
	/**
	 * GPO列表
	 * @return
	 */
	@RequestMapping(value="/listGPO")
	@ResponseBody
	public List<Map<String, Object>> listGPO(@CurrentUser User user){
		List<Map<String, Object>> list = companyService.listGPO(user.getProjectCode());
		return list;
	}
	/**
	 * 配送企业列表
	 * @return
	 */
	@RequestMapping(value="/listVendor")
	@ResponseBody
	public List<Map<String, Object>> listVendor(@CurrentUser User user){
		List<Map<String, Object>> list = companyService.listVendor(user.getProjectCode());
		return list;
	}

	/**
	 * <p>
	 * @param myfile - 文件上传路径
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public Message upload(MultipartFile myfile, @CurrentUser User user){

		Message message = new Message();
		String filename = myfile.getOriginalFilename();
		if (filename == null || "".equals(filename)) {
			message.setMsg("文件不能为空");
			return message;
		}
		try {
			if(filename.endsWith(".xls") ||filename.endsWith(".xlsx")) {
				//读取Excel
				String msg =  this.doExcelH(myfile,user);

				message.setMsg(msg);
			}else {
				message.setMsg("请用正确模版格式导入");
			}
		}  catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;

	}


	private String doExcelH(MultipartFile file, User user) throws Exception {
		String[][] upExcel = null;
		InputStream input = file.getInputStream();
		XSSFWorkbook workBook = new XSSFWorkbook(input);
		XSSFSheet sheet = workBook.getSheetAt(0);
		if (sheet != null) {
			// i = 0 是标题栏
			for (int i = 1; i <= sheet.getPhysicalNumberOfRows() - 1; i++) {
				XSSFRow row0 = sheet.getRow(0);
				XSSFRow row = sheet.getRow(i);
				if (upExcel == null) {
					upExcel = new String[sheet.getPhysicalNumberOfRows() - 1][row0.getPhysicalNumberOfCells()];
				}
				for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
					XSSFCell cell = row.getCell(j);
					String cellStr = ExcelUtil.getValue(cell);
					upExcel[i - 1][j] = cellStr;
				}
			}
		}
		workBook.close();
		return companyService.doExcelH(user.getProjectCode(), upExcel, user);
	}
}
