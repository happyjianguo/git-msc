package com.shyl.msc.hospital.controller;

import java.io.InputStream;
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
import com.shyl.msc.supervise.entity.BaseDrugProgress;
import com.shyl.msc.supervise.entity.BaseDrugProgress.HealthStationType;
import com.shyl.msc.supervise.entity.BaseDrugProvide;
import com.shyl.msc.supervise.entity.HospitalZone;
import com.shyl.msc.supervise.service.IBaseDrugProgressService;
import com.shyl.msc.supervise.service.IHospitalZoneService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 实施基本药物制度进展上传Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/hospital/baseDrugProgress")
public class HospitalBaseDrugProgressController extends BaseController{
	
	@Resource
	private IBaseDrugProgressService baseDrugProgressService;
	@Resource
	private IHospitalZoneService hospitalZoneService ;
	/**
	 * 首页
	 * @return
	 */
	@RequestMapping("")
	public String home(@CurrentUser User user){
		return "hospital/baseDrugProgress/list";
	}
	
	/**
	 * page
	 * @return
	 */
	@RequestMapping("page")
	@ResponseBody
	public DataGrid<BaseDrugProgress> page(PageRequest page, @CurrentUser User user){
		page.setSort(new Sort(new Order(Direction.DESC, "month")));
		DataGrid<BaseDrugProgress> result =  baseDrugProgressService.query(user.getProjectCode(),page);
		return result;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "hospital/baseDrugProgress/add";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(BaseDrugProgress baseDrugProgress, @CurrentUser User user) {
		Message msg = new Message();
		try{
			Integer healthStationType =BaseDrugProgress.HealthStationType.valueOf(baseDrugProgress.getHealthStationType().toString()).ordinal() ;
			//医院选择了一个机构类型之后就不能再选择其它的类型
			BaseDrugProgress obj1 = baseDrugProgressService.findByType(user.getProjectCode(),healthStationType);
			if(obj1==null){
				throw new Exception("机构类型只能选择一个");
			}
			//判断医院是否重复上报
			BaseDrugProgress obj2 = baseDrugProgressService.findUnique(user.getProjectCode(), baseDrugProgress.getMonth(),user.getOrganization().getOrgCode());
			if(obj2 != null){
				throw new Exception("该月份已上报");
			}
			if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
				baseDrugProgress.setHospitalCode(user.getOrganization().getOrgCode());
				baseDrugProgress.setHospitalName(user.getOrganization().getOrgName());
				HospitalZone his = hospitalZoneService.getByCode(user.getProjectCode(),baseDrugProgress.getHospitalCode());
				if(his!=null){
					baseDrugProgress.setCityCode(his.getCityCode());
					baseDrugProgress.setCityName(his.getCityName());
					baseDrugProgress.setCountyCode(his.getCountyCode());
					baseDrugProgress.setCountyName(his.getCountyName());
				}
			}
			baseDrugProgress.setCreateUser(user.getEmpId());
			baseDrugProgressService.save(user.getProjectCode(),baseDrugProgress);
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
			baseDrugProgressService.delete(user.getProjectCode(), id);
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
				List<BaseDrugProgress> baseDrugProgresss = tranExcelToList(myfile, user);
				
				baseDrugProgressService.saveBatch(user.getProjectCode(), baseDrugProgresss);
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
	private List<BaseDrugProgress> tranExcelToList(MultipartFile file, User user) throws Exception {
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
	
	private List<BaseDrugProgress> tranArrayToList(String[][] upExcel, User user) throws Exception{
		List<BaseDrugProgress> baseDrugProgresss = new ArrayList<BaseDrugProgress>();
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
				HealthStationType healthStationType = null;
				if("村卫生站".equals(row[1])){
					healthStationType=BaseDrugProgress.HealthStationType.healthStation;
				}else if("非政府办乡镇卫生院、社区卫生服务中心".equals(row[1])){
					healthStationType=BaseDrugProgress.HealthStationType.healthServiceCentre;
				}else{
					healthStationType=BaseDrugProgress.HealthStationType.outpatientDepartment;
				}
				//医院选择了一个机构类型之后就不能再选择其它的类型
				BaseDrugProgress obj2 = baseDrugProgressService.findByType(user.getProjectCode(),BaseDrugProgress.HealthStationType.valueOf(healthStationType.toString()).ordinal());
				if(obj2==null){
					continue;
				}
				//判断医院是否重复上报
				BaseDrugProgress obj3 = baseDrugProgressService.findUnique(user.getProjectCode(), row[0],user.getOrganization().getOrgCode());
				if(obj3 != null){
					continue;
				}
				BaseDrugProgress progress = new BaseDrugProgress();
				
				progress.setMonth(row[0]);
				progress.setHealthStationType(healthStationType);
				if("是".equals(row[2])||"1".equals(row[2])){
					progress.setIsHighSixty(new Integer(1));
				}else{
					progress.setIsHighSixty(new Integer(0));
				}
				if("是".equals(row[3])||"1".equals(row[3])){
					progress.setIsImplementedStation(new Integer(1));
				}else{
					progress.setIsImplementedStation(new Integer(0));
				}
				if("是".equals(row[4])||"1".equals(row[4])){
					progress.setIsGeneralStation(new Integer(1));
				}else{
					progress.setIsGeneralStation(new Integer(0));
				}
				if("是".equals(row[5])||"1".equals(row[5])){
					progress.setIsThirdHealthStation(new Integer(1));
				}else{
					progress.setIsThirdHealthStation(new Integer(0));
				}
				if("是".equals(row[6])||"1".equals(row[6])){
					progress.setIsInHealthInsurance(new Integer(1));
				}else{
					progress.setIsInHealthInsurance(new Integer(0));
				}
				if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
					progress.setHospitalCode(user.getOrganization().getOrgCode());
					progress.setHospitalName(user.getOrganization().getOrgName());
					HospitalZone his = hospitalZoneService.getByCode(user.getProjectCode(),progress.getHospitalCode());
					if(his!=null){
						progress.setCityCode(his.getCityCode());
						progress.setCityName(his.getCityName());
						progress.setCountyCode(his.getCountyCode());
						progress.setCountyName(his.getCountyName());
					}
				}
				baseDrugProgresss.add(progress);
				map.put(row[0] + "#"+user.getOrganization().getOrgCode(), "");
			}
			
		}	
		return baseDrugProgresss;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Model model){
		return "hospital/baseDrugProgress/edit";
	}
	
	/**
	 * 修改
	 * @param baseDrugProvide
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(BaseDrugProgress baseDrugProgress,@CurrentUser User user){
		Message message = new Message();
		try{
			//保存到标准目录		
			baseDrugProgressService.update(user.getProjectCode(), baseDrugProgress);
		
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	@Override
	protected void init(WebDataBinder arg0) {	
	}

}
