package com.shyl.msc.hospital.controller;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
import com.shyl.msc.supervise.entity.BaseDrugProvide;
import com.shyl.msc.supervise.entity.HospitalZone;
import com.shyl.msc.supervise.service.IBaseDrugProvideService;
import com.shyl.msc.supervise.service.IHospitalZoneService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 实施基本药物制度进展Controller
 * 
 * @author a_Q
 *
 */

@Controller
@RequestMapping("/hospital/baseDrugProvide")
public class HospitalBaseDrugProvideController extends BaseController {

	@Resource
	private IBaseDrugProvideService baseDrugProvideService;
	@Resource
	private IHospitalZoneService hospitalZoneService ;
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user){
		return "hospital/baseDrugProvide/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<BaseDrugProvide> page(PageRequest page, @CurrentUser User user){
		page.setSort(new Sort(new Order(Direction.DESC, "month")));
		return baseDrugProvideService.query(user.getProjectCode(),page);
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "hospital/baseDrugProvide/add";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(BaseDrugProvide baseDrugProvide, @CurrentUser User user) {
		Message msg = new Message();
		try{
			BaseDrugProvide obj = baseDrugProvideService.findUnique(user.getProjectCode(), baseDrugProvide.getMonth(),user.getOrganization().getOrgCode());
			if(obj != null){
				throw new Exception("该月份已上报");
			}
			if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
				baseDrugProvide.setHospitalCode(user.getOrganization().getOrgCode());
				baseDrugProvide.setHospitalName(user.getOrganization().getOrgName());	
				baseDrugProvide.setOrgType(user.getOrganization().getOrgType());
				HospitalZone his = hospitalZoneService.getByCode(user.getProjectCode(),baseDrugProvide.getHospitalCode());
				if(his!=null){
					baseDrugProvide.setCountyCode(his.getCountyCode());
					baseDrugProvide.setCountyName(his.getCountyName());
					baseDrugProvide.setOrgLevel(his.getOrgLevel());
				}	
			}	
			baseDrugProvide.setCreateUser(user.getEmpId());
			baseDrugProvideService.save(user.getProjectCode(), baseDrugProvide);
			msg.setMsg("添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("添加失败,"+e.getMessage());
			msg.setSuccess(false);
		}
		return msg;
	}
	
	//删除
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user) {
		Message msg = new Message();
		try{			
			baseDrugProvideService.delete(user.getProjectCode(), id);
			msg.setMsg("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("删除失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	//导入
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public Message upload(MultipartFile myfile, @CurrentUser User user) {
		Message msg = new Message();
		try {
			if (user.getOrganization() != null && user.getOrganization().getOrgType() == 1) {
				String filename = myfile.getOriginalFilename();
				if (filename == null || "".equals(filename)) {
					throw new Exception("文件不能为空");
				}
				if (!filename.endsWith(".xls") && !filename.endsWith(".xlsx")) {				
					throw new Exception("请用正确模版格式导入");
				}
				//将Excel转换成可以保存的List
				List<BaseDrugProvide> baseDrugProvides = tranExcelToList(myfile, user);
				
				baseDrugProvideService.saveBatch(user.getProjectCode(), baseDrugProvides);
				msg.setMsg("上传成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
			msg.setMsg(e.getMessage());
		}
		return msg;
	}
	
	//将Excel转换成可以保存的List
	private List<BaseDrugProvide> tranExcelToList(MultipartFile file, User user) throws Exception {
		String[][] upExcel = null;
		InputStream input = file.getInputStream();
		HSSFWorkbook workBook = new HSSFWorkbook(input);
		HSSFSheet sheet = workBook.getSheetAt(0);
		if (sheet != null) {
			// i = 0 是标题栏
			for (int i = 1; i <= sheet.getPhysicalNumberOfRows() - 1; i++) {
				HSSFRow row0 = sheet.getRow(0);
				HSSFRow row = sheet.getRow(i);
				if (upExcel == null) {
					upExcel = new String[sheet.getPhysicalNumberOfRows() - 1][row0.getPhysicalNumberOfCells()];
				}
				for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
					HSSFCell cell = row.getCell(j);
					String cellStr = ExcelUtil.getValue(cell);
					upExcel[i - 1][j] = cellStr;
				}
			}
		}
		workBook.close();
		return tranArrayToList(upExcel, user);
	}
	
	private List<BaseDrugProvide> tranArrayToList(String[][] upExcel, User user) throws Exception{
		List<BaseDrugProvide> baseDrugProvides = new ArrayList<BaseDrugProvide>();
		Map<String,Object> map = new HashMap<String, Object>();
		for (int i = 0; i < upExcel.length; i++) {
			String[] row = upExcel[i];
			if(StringUtils.isEmpty(row[0])){
				continue;
			}else{
				if(!row[0].matches("^[1-9][0-9]{3}\\-((0[1-9])|(1[012]))$")){
					throw new Exception("第"+(i+1)+"笔数据异常，年月格式有误");
				}
				//剔除重复数据
				Object obj1 = map.get(row[0] + "#"+user.getOrganization().getOrgCode());
				if(obj1 != null){
					continue;
				}
				BaseDrugProvide obj2 = baseDrugProvideService.findUnique(user.getProjectCode(), row[0],user.getOrganization().getOrgCode());
				if(obj2 != null){
					continue;
				}
				BaseDrugProvide provide = new BaseDrugProvide();
				provide.setMonth(row[0]);
				provide.setBaseDrugTotal(Integer.parseInt(row[1]));
				provide.setDrugTotal(Integer.parseInt(row[2]));
				provide.setBaseDrugTrade(new BigDecimal(row[3]));
				provide.setDrugTrade(new BigDecimal(row[4]));
				if("是".equals(row[5])||"1".equals(row[5])){
					provide.setIsReformHospital(new Integer(1));
				}else{
					provide.setIsReformHospital(new Integer(0));
				}
				
				if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
					provide.setHospitalCode(user.getOrganization().getOrgCode());
					provide.setHospitalName(user.getOrganization().getOrgName());	
					provide.setOrgType(user.getOrganization().getOrgType());
					HospitalZone his = hospitalZoneService.getByCode(user.getProjectCode(),provide.getHospitalCode());
					if(his!=null){
						provide.setCountyCode(his.getCountyCode());
						provide.setCountyName(his.getCountyName());
						provide.setOrgLevel(his.getOrgLevel());
					}	
				}
				baseDrugProvides.add(provide);
				map.put(row[0] + "#"+user.getOrganization().getOrgCode(), "");
			}
			
		}	
		return baseDrugProvides;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Model model){
		return "hospital/baseDrugProvide/edit";
	}
	
	/**
	 * 修改
	 * @param baseDrugProvide
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(BaseDrugProvide baseDrugProvide,@CurrentUser User user){
		Message message = new Message();
		try{
			//保存到标准目录		
			baseDrugProvideService.update(user.getProjectCode(), baseDrugProvide);
		
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	@Override
	protected void init(WebDataBinder arg0) {
		// TODO Auto-generated method stub
		
	}

}
