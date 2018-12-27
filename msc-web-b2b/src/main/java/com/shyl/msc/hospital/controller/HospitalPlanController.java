package com.shyl.msc.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.annotation.Fastjson;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.util.GridFSDAO;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.plan.entity.HospitalPlan;
import com.shyl.msc.b2b.plan.entity.HospitalPlanDetail;
import com.shyl.msc.b2b.plan.service.IHospitalPlanDetailService;
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.msc.dm.entity.Directory;
import com.shyl.msc.enmu.ProjectStus;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.msc.set.service.IProjectService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/hospital/plan")
public class HospitalPlanController extends BaseController {

	@Resource
	private IProjectService	projectService;
	@Resource
	private IProjectDetailService projectDetailService;
	@Resource
	private IHospitalPlanService hospitalPlanService;
	@Resource
	private IHospitalPlanDetailService hospitalPlanDetailService;
	@Resource
	private GridFSDAO gridFSDAO;
	@Override
	protected void init(WebDataBinder arg0) {
	}


	@RequestMapping("")
	public String home(ModelMap model) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		model.addAttribute("today", format.format(new Date()));
		return "hospital/plan/projectList";
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Project> page(PageRequest page, @CurrentUser User user) {
		Map<String, Object> query = page.getQuery();
		String s = (String)query.get("t#projectStus_E_EQ");
		if(StringUtils.isNotBlank(s)) query.put("t#projectStus_E_EQ", ProjectStus.valueOf(s));
		String type = (String)query.get("t#type_S_EQ");
		if(!StringUtils.isEmpty(type)){
			query.put("t#type_S_EQ", Project.ProjectType.valueOf(type));
		}
		return projectService.query(user.getProjectCode(), page);
	}
	
	@RequestMapping("/planList")
	public String planList(ModelMap model, String projectCode,@CurrentUser User user) {
		Project project = projectService.getByCode(user.getProjectCode(), projectCode);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		String now = format.format(new Date());
		int today = Integer.valueOf(format.format(new Date()));
		int start = Integer.valueOf(format.format(project.getStartDate()));
		int end = Integer.valueOf(format.format(project.getEndDate()));
		if (today>= start && today<=end) {
			model.addAttribute("isStart", 1);
		}
		
		model.addAttribute("today", now);
		model.addAttribute("projectCode", projectCode);
		
		//model.addAttribute("ISCAUSED", 1);
		//model.addAttribute("ISCAUSED", CommonProperties.ISCAUSED);
		
		return "hospital/plan/planList";
	}
	
	@RequestMapping(value = "/planPage", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject planPage(Long projectId,PageRequest page, @CurrentUser User user, Integer status) {
		if(projectId == null){
			return new JSONObject();
		}
		page.getQuery().put("b#id_L_EQ", projectId);
		String hospitalCode = user.getOrganization().getOrgCode();
		if (page.getSort() == null) {
			Sort sort = new Sort(new Order(Direction.ASC,"c.genericName"),new Order(Direction.ASC,"c.dosageFormName"),
					new Order(Direction.ASC,"c.model"),new Order(Direction.ASC,"c.qualityLevelCode"));
			page.setSort(sort);
		}
		
		DataGrid<Map<String,Object>> data = hospitalPlanService.nquery(user.getProjectCode(), page, status, hospitalCode);
		
		JSONObject ret = new JSONObject();
		String startMonthDef = "";
		String endMonthDef = "";
		if (data.getRows().size()>0) {
			Map<String,Object> project = data.getRows().get(0);
			startMonthDef = (String)project.get("STARTMONTHDEF");
			endMonthDef = (String)project.get("ENDMONTHDEF");
		}
		
		List<Map<String,Object>> arr = data.getRows();
		
		for (int i=0;i<arr.size();i++) {
			Map<String,Object> map = arr.get(i);
			HospitalPlan plan = hospitalPlanService.getByDetailId(user.getProjectCode(), Long.valueOf(map.get("ID").toString()), hospitalCode);
			if (plan!= null) {
				map.put("HOSPITALPLANID", plan.getId());
				map.put("NUM", plan.getNum());
				map.put("STARTMONTH", plan.getStartMonth());
				map.put("ENDMONTH", plan.getEndMonth());
			} else {
				map.put("STARTMONTH", startMonthDef);
				map.put("ENDMONTH", endMonthDef);
			}
		}
		ret.put("total", data.getTotal());
		ret.put("rows", arr);
		
		return ret;
	}
	
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public String details(Long hospitalPlanId) {
		return "hospital/plan/detailList";
	}
	
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	@ResponseBody
	public List<HospitalPlanDetail> planPage(Long hospitalPlanId,@CurrentUser User user) {
		return hospitalPlanDetailService.getByHospitalPlanId(user.getProjectCode(), hospitalPlanId);
	}


	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long hospitalPlanId,@CurrentUser User user) {
		Message msg = new Message();
		try {
			HospitalPlan hp = hospitalPlanService.getById(user.getProjectCode(),hospitalPlanId);
			Long projectDetailId = hp.getProjectDetailId();
			ProjectDetail pd = projectDetailService.getById(user.getProjectCode(),projectDetailId);
			hospitalPlanDetailService.deleteByHospitalPlanId(user.getProjectCode(), hospitalPlanId);
			hospitalPlanService.delete(user.getProjectCode(), hospitalPlanId);
			
			//更新projectDetail的报量医院数
			PageRequest pageable = new PageRequest();
			pageable.getQuery().put("t#projectDetailId_L_EQ", projectDetailId);
			List<HospitalPlan> hplist = hospitalPlanService.list(user.getProjectCode(),pageable);
			pd.setHospitalNum(hplist.size());
			//更新projectDetail
			projectDetailService.update(user.getProjectCode(),pd);
			
			
			msg.setSuccess(true);
			msg.setMsg("清除成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("清除失败");
			msg.setSuccess(false);
		}
		return msg;
	}

	
	@RequestMapping(value = "/setup", method = RequestMethod.POST)
	@ResponseBody
	public Message setup(@Fastjson String jsonStr, @CurrentUser User user) {
		System.out.println("jsonStr="+jsonStr);
		Message msg = new Message();
		try{
			hospitalPlanService.setup(user.getProjectCode(),jsonStr,user);
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		    msg.setMsg("报量失败");
			msg.setSuccess(false);
		}
		return msg;
	}

	@RequestMapping(value = "/export")
	public void export(HttpServletResponse resp, HttpServletRequest request, @CurrentUser User user, String projectCode, String contractName) {
		String hospitalCode = user.getOrganization().getOrgCode();
		Sort sort = new Sort(new Order(Direction.ASC,"genericName"),new Order(Direction.ASC,"dosageFormName"),
				new Order(Direction.ASC,"model"),new Order(Direction.ASC,"qualityLevelCode"));
		
		PageRequest page = new PageRequest();
		page.setSort(sort);
		Map<String, Object> query =  new HashMap<String, Object>();
		query.put("project.code_S_EQ", projectCode);
		page.setQuery(query);
		page.setPageSize(200);
		//信息
		DataGrid<ProjectDetail> data = projectDetailService.query(user.getProjectCode(), page);
		String[] heanders = {"项目编号","目录明细编号","通用名","推荐剂型","剂型","规格","质量层次","计划采购量（必填）","最小使用单位","计划开始时间（必填，格式：2016-01）","计划结束时间（必填，格式：2016-06）","报名企业","备注"};
		
		OutputStream out = null;
		Workbook wb = new HSSFWorkbook();
	 	try {
			 Sheet sh = wb.createSheet("sheet1");
			 Row row = sh.createRow(0);
			 for(int i = 0;i<heanders.length;i++) {
				 Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(heanders[i]);
			 }


			 String filename = contractName;
            //获得请求头中的User-Agent
            String agent = request.getHeader("User-Agent").toUpperCase();
            //根据不同浏览器进行不同的编码
            String filenameEncoder = "";
            if (agent.contains("GECKO")||agent.contains("MSIE")) {
                // IE浏览器
				filename = filename.replace("+", " ");
				filenameEncoder = URLEncoder.encode(filename, "utf-8");
            } else if (agent.contains("Firefox")) {
                // 火狐浏览器
                BASE64Encoder base64Encoder = new BASE64Encoder();
                filenameEncoder = "=?utf-8?B?"
                        + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
            } else {
                // 其它浏览器
                filenameEncoder = URLEncoder.encode(filename, "utf-8");
            }
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment;filename="+filenameEncoder+".xls");
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

	@RequestMapping(value = "/imp")
	@ResponseBody
	public Message imp(MultipartFile myfile, @CurrentUser User user)  {
		Message message = new Message(); 
		 String filename = myfile.getOriginalFilename();  
		 if (filename == null || "".equals(filename)) {  
			 message.setMsg("文件不能为空");
			 return message;
		 }  
		 try {  
			  if(filename.endsWith(".xls") ||filename.endsWith(".xlsx")) {
				  //读取Excel
				  this.importExcel(myfile, user);
				  message.setMsg("导入成功");
			  }else {
				  message.setMsg("请用正确格式导入");
			  }
		 } catch(RuntimeException e) {
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
	
	private void importExcel(MultipartFile file, User user) throws IOException {

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
				String[] upExcel = null;
				String hospitalCode = user.getOrganization().getOrgCode();
				String hospitalName = user.getOrganization().getOrgName();
				
				// i = 0 是标题栏
				for (int i = 1, l = sheet.getPhysicalNumberOfRows() ; i < l; i++) {
					Row row = sheet.getRow(i);
					upExcel = new String[row0.getPhysicalNumberOfCells()];
					for (int j = 0, cl = row0.getPhysicalNumberOfCells(); j < cl; j++) {
						Cell cell = row.getCell(j);
						String cellStr = ExcelUtil.getValue(cell);
						if (j == 0 && StringUtils.isEmpty(cellStr)) {
							break;
						}
						upExcel[j] = cellStr;
					}
					if (StringUtils.isEmpty(upExcel[1])) {
						throw new RuntimeException("第"+i+"行药品明细编号不能为空");
					}
					if (StringUtils.isEmpty(upExcel[7]) || !StringUtils.isNumeric(upExcel[7])) {
						throw new RuntimeException("第"+i+"行计划数量不能为空或者格式不正确");
					}
					
					if (StringUtils.isEmpty(upExcel[9])) {
						throw new RuntimeException("第"+i+"行计划开始日期不能为空");
					}
					if (StringUtils.isEmpty(upExcel[10])) {
						throw new RuntimeException("第"+i+"行计划结束日期不能为空");
					}
					this.setup(user.getProjectCode(), Long.valueOf(upExcel[1]), upExcel[7], upExcel[9], upExcel[10], hospitalCode, hospitalName);
				}
			}
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {
			throw e;
		} finally {
			workBook.close();
		}
	}
	private void setup(String projectCode, Long projectDetailId, String cgsl, String startMonth, 
			String endMonth, String hospitalCode, String  hospitalName) {
		HospitalPlan plan = hospitalPlanService.getByDetailId(projectCode, projectDetailId, hospitalCode);
		ProjectDetail pd = projectDetailService.getById(projectCode,projectDetailId);
		if (plan == null) {
			plan = new HospitalPlan();
			plan.setProjectDetailId(projectDetailId);
			plan.setHospitalCode(hospitalCode);
			plan.setHospitalName(hospitalName);
			//更新projectDetail的量
			pd.setNum(pd.getNum()+Integer.parseInt(cgsl));
		}else{
			//更新projectDetail的量
			pd.setNum(pd.getNum()-Integer.parseInt(plan.getNum()+"")+Integer.parseInt(cgsl));
		}
		plan.setNum(new BigDecimal(cgsl));
		if (plan.getNum() == null || plan.getNum().compareTo(new BigDecimal(0))<=0) {
			throw new RuntimeException("药品数量不能为空且大于0");
		}
		plan.setStartMonth(startMonth);
		plan.setEndMonth(endMonth);

		Integer month0 = Integer.valueOf(endMonth.replaceAll("-", ""));
		Integer month1 = Integer.valueOf(startMonth.replaceAll("-", ""));
		
		Integer a = (month0/100) - (month1/100);
		Integer b = (month0%100) - (month1%100);
		if (a != 0) {
			a = a*12;
		}
		Integer cycle = b + a + 1;
		plan.setCycle(cycle);
		if (plan.getId() == null) {
			plan = hospitalPlanService.save(projectCode, plan);
			//更新projectDetail的报量医院数
			PageRequest pageable = new PageRequest();
			pageable.getQuery().put("t#projectDetailId_L_EQ", projectDetailId);
			List<HospitalPlan> hplist = hospitalPlanService.list(projectCode,pageable);
			pd.setHospitalNum(hplist.size());
		} else {
			hospitalPlanService.update(projectCode, plan);
			//删除之前的明细
			hospitalPlanDetailService.deleteByHospitalPlanId(projectCode, plan.getId());
		}
		//更新projectDetail
		projectDetailService.update(projectCode,pd);
		
		Integer num = plan.getNum().intValue();
		int detailNum = num/cycle;
		for(int j=0;j<cycle;j++) {
			HospitalPlanDetail planDetail = new HospitalPlanDetail();
			if (j+1==cycle) {
				detailNum = num;
			} else { 
				num = num-detailNum;
			}
			
			planDetail.setHospitalPlan(plan);
			
			int year = month1/100;
			int month = j+(month1%100);
			if (month > 12) {
				month = month - 12;
				year = year+1;
			}
			String monthStr = String.valueOf(month);
			if (month < 10) {
				monthStr = "0" + monthStr;
			}
			
			planDetail.setMonth(year + "-" + monthStr);
			planDetail.setNum(new BigDecimal(detailNum));
			hospitalPlanDetailService.save(projectCode, planDetail);
		}
	}
	
	

	
}
