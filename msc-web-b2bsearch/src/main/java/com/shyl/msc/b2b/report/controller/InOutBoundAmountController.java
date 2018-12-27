package com.shyl.msc.b2b.report.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IInOutBoundDetailService;
import com.shyl.sys.entity.User;

/**
 * 入库金额统计
 */
@Controller
@RequestMapping("/b2b/report/inOutBoundAmount")
public class InOutBoundAmountController extends BaseController{

	@Resource
	private IInOutBoundDetailService inOutBoundDetailService;
	
	/**
	 * 首页
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "b2b/report/inOutBoundAmount/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */ 
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest page, @CurrentUser User user){
		page.setSort(new Sort(new Order(Direction.DESC, "goodssum")));
		DataGrid<Map<String, Object>> result = inOutBoundDetailService.queryByPage(user.getProjectCode(),page);
		result.addFooter("HOSPITALNAME", "GOODSSUM","GPOGOODSSUM","OTHERGOODSSUM");
		return result;
	}
	
	@RequestMapping("/export")
	public void export(PageRequest page,@CurrentUser User user,HttpServletResponse resp) {
		String heanders [] = {"医疗机构名称","总入库金额","GPO入库金额","非GPO入库金额"};
		String beannames [] = {"HOSPITALNAME","GOODSSUM","GPOGOODSSUM","OTHERGOODSSUM"};
		List<Map<String, Object>> list = inOutBoundDetailService.queryByAll(user.getProjectCode(),page);
		try{
			ExcelUtil excelUtil = new ExcelUtil(heanders, beannames);
			DecimalFormat df=new DecimalFormat("######0.00");
			BigDecimal GOODSSUM = new BigDecimal(0d); //总入库金额
	 		BigDecimal GPOGOODSSUM = new BigDecimal(0d);//GPO入库金额
	 		BigDecimal OTHERGOODSSUM = new BigDecimal(0d); //非GPO入库金额
	 		for (Map<String, Object> l : list) {
	 			GOODSSUM=GOODSSUM.add((BigDecimal)l.get("GOODSSUM"));
	 			GPOGOODSSUM=GPOGOODSSUM.add((BigDecimal)l.get("GPOGOODSSUM"));
	 			OTHERGOODSSUM=OTHERGOODSSUM.add((BigDecimal)l.get("OTHERGOODSSUM"));
			}
	 		Map<String, Object> map = new HashMap<String, Object>();
	 		map.put("HOSPITALNAME", "合计");
	 		map.put("GOODSSUM", df.format(GOODSSUM.doubleValue()));
	 		map.put("GPOGOODSSUM",df.format(GPOGOODSSUM.doubleValue()));
	 		map.put("OTHERGOODSSUM", df.format(OTHERGOODSSUM.doubleValue()));
	 		list.add(map);
			Workbook workbook = excelUtil.doExportXLS(list, "入库金额统计", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=report.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void init(WebDataBinder arg0) {
	}

}
