package com.shyl.msc.dm.controller;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.Entity;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.GoodsCountry;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsCountryService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

/**
 * @ function: 国家药品对照
 */
@Controller
@RequestMapping("/dm/goodsCountry")
public class GoodsCountryController extends BaseController {

    @Resource
    private IGoodsCountryService goodsCountryService;
    @Autowired(required = false)
    private RedisTemplate redisTemplate;
    @Autowired(required = false)
    private TaskExecutor taskExecutor;
    @Resource
    private IProductService productService;

    private String filterRules;

    @Override
    protected void init(WebDataBinder webDataBinder) {

    }

    @RequestMapping("")
    public String toHome() {
        return "dm/goodsCountry/list";
    }

    /**
     * 主查询功能
     *
     * @param pageable
     * @param user
     * @return
     */
    @RequestMapping("/page")
    @ResponseBody
    public DataGrid<Map<String, Object>> page(PageRequest pageable, @CurrentUser User user) {
        this.filterRules = pageable.getFilterRules();
        Map<String, Object> query = pageable.getQuery();
        if (query == null) {
            query = new HashMap<String, Object>();
        }

        if (user.getOrganization().getOrgType() == 1) {
            query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
        }
        return goodsCountryService.selectAll(user.getProjectCode(), pageable);
    }

    /**
     * 导出
     *
     * @param resp
     * @param user
     */
    @RequestMapping("/export")
    public void exportGoodsCountry(HttpServletResponse resp, @CurrentUser User user, PageRequest pageable) {

        if (StringUtils.isNotEmpty(this.filterRules)) {
            pageable.setFilterRules(this.filterRules);
        }
        if (user.getOrganization().getOrgType() == 1) {
            pageable.getQuery().put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
        }
        List<Map<String, Object>> datas = goodsCountryService.selectAllforExport(user.getProjectCode(), pageable);
        if (datas == null) {
            datas = (List<Map<String, Object>>) redisTemplate.opsForList().leftPop("goodsCountry");
        }
        //List<Map<String,Object>> datas = goodsCountryService.selectAllforExport(user.getProjectCode(),pageable);

        System.out.println("**********查询结果********" + datas.size());

        String heanders[] = {"医院编码", "医院名称", "医院药品内部编码", "医院药品名称", "医院规格", "医院药品包装规格", "医院剂型", "医院生产厂家", "药品编码", "药品名称", "药品规格", "药品包装规格", "药品剂型", "药品生产厂家", "药品批准文号", "国家药品编码", "国家药品名称", "国家药品规格", "国家包装规格", "国家剂型名称", "国家生产厂家", "国家批准文号", "是否国家药品对照", "是否GPO药品"};
        //String beannames [] = {"HOSPITALCODE","HOSPITALNAME","INTERNALCODE","PRODUCTNAME","MODEL","PACKDESC","DOSAGEFORMNAME","PRODUCERNAME","SPRODUCTCODE","SPRODUCTNAME","SMODEL","SPACKDESC","SDOSAGEFORMNAME","SPRODUCERNAME","SAUTHORIZENO","COUNTRYPRODUCTCODE","COUNTRYPRODUCTNAME","COUNTRYMODEL","CPACKDESC","","","SNATIONALCODE","SISGPOPURCHASE"};
        OutputStream out = null;
        Workbook wb = new HSSFWorkbook();

        //ExcelUtil excelUtil = new ExcelUtil(heanders,beannames);
        try {
            Sheet sh = wb.createSheet("sheet1");
            Row row = sh.createRow(0);
            for (int i = 0; i < heanders.length; i++) {
                Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
                cell.setCellValue(heanders[i]);
            }
            for (int i = 0; i < datas.size(); i++) {
                Map<String, Object> map = datas.get(i);

                row = sh.createRow(i + 1);
                Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("HOSPITALCODE"));

                cell = row.createCell(1, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("HOSPITALNAME"));

                cell = row.createCell(2, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("INTERNALCODE"));

                cell = row.createCell(3, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("PRODUCTNAME"));

                cell = row.createCell(4, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("MODEL"));

                cell = row.createCell(5, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("PACKDESC"));

                cell = row.createCell(6, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("DOSAGEFORMNAME"));

                cell = row.createCell(7, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("PRODUCERNAME"));

                cell = row.createCell(8, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("SPRODUCTCODE"));

                cell = row.createCell(9, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("SPRODUCTNAME"));

                cell = row.createCell(10, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("SMODEL"));

                cell = row.createCell(11, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("SPACKDESC"));

                cell = row.createCell(12, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("SDOSAGEFORMNAME"));

                cell = row.createCell(13, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("SPRODUCERNAME"));

                cell = row.createCell(14, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("SAUTHORIZENO"));

                cell = row.createCell(15, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("COUNTRYPRODUCTCODE"));

                cell = row.createCell(16, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("COUNTRYPRODUCTNAME"));

                cell = row.createCell(17, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("COUNTRYMODEL"));//医院名称

                cell = row.createCell(18, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("CPACKDESC"));//医院名称

                cell = row.createCell(19, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("COUNTRYDOSAGEFORMNAME"));//医院名称

                cell = row.createCell(20, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("COUNTRYPRODUCERNAME"));//医院名称

                cell = row.createCell(21, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("COUNTRYAUTHORIZENO"));//医院名称

                cell = row.createCell(22, Cell.CELL_TYPE_STRING);
                cell.setCellValue((String) map.get("SNATIONALCODE"));//医院名称

                cell = row.createCell(23, Cell.CELL_TYPE_STRING);
                cell.setCellValue(map.get("SISGPOPURCHASE") != null ? map.get("SISGPOPURCHASE").toString() : "");//医院名称
            }
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=goodsCountry.xls");
            out = resp.getOutputStream();
            wb.write(out);
            out.flush();
            wb.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Message upload(MultipartFile myfile, @CurrentUser final User user) {

        Message message = new Message();
        String filename = myfile.getOriginalFilename();
        if (filename == null || "".equals(filename)) {
            message.setMsg("文件不能为空");
            message.setSuccess(false);
            return message;
        }
        if (!(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            message.setSuccess(false);
            message.setMsg("请用正确模版格式导入");
            return message;
        }
        try {
            String[][] excel = this.tranExcelToArray(myfile, user);
            //用异步去查询GoodsCountry
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    goodsCountryService.getByAllByAsync(user.getProjectCode());
                }
            });
            //用异步去查询Product
            List<Integer> integers = new ArrayList<>();
            for(int i=0 ;i<4 ;i++){
                integers.add(i);
            }
            for(final Integer i : integers){
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        productService.getByAllByAsync(user.getProjectCode(),i);
                    }
                });
            }
            //用异步去查询国家药品目录
            integers = new ArrayList<>();
            for(int i=0 ;i<24 ;i++){
                integers.add(i);
            }
            for(final Integer i : integers){
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        goodsCountryService.getByAllDrugByAsync(user.getProjectCode(),i);
                    }
                });
            }
            goodsCountryService.importExcel(user.getProjectCode(), excel);
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg(e.getMessage());
        }
        return message;
    }

    private String[][] tranExcelToArray(MultipartFile file, User user) throws Exception {
        String[][] upExcel = null;
        InputStream input = file.getInputStream();
        Workbook workBook = null;
        if (file.getOriginalFilename().endsWith(".xls")) {
            workBook = new HSSFWorkbook(input);
        } else {
            workBook = new XSSFWorkbook(input);
        }

        Sheet sheet = workBook.getSheetAt(0);
        if (sheet != null) {
            // i = 0 是标题栏
            for (int i = 1; i <= sheet.getPhysicalNumberOfRows() - 1; i++) {
                Row row0 = sheet.getRow(0);
                Row row = sheet.getRow(i);
                if (upExcel == null) {
                    upExcel = new String[sheet.getPhysicalNumberOfRows() - 1][row0.getPhysicalNumberOfCells()];
                }
                for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
                    Cell cell = row.getCell(j);
                    String cellStr = ExcelUtil.getValue(cell);
                    upExcel[i - 1][j] = cellStr;
                }
            }
        }
        workBook.close();
        return upExcel;
    }

    @RequestMapping("/exportTemplate")
    public void exportTemplate(HttpServletResponse resp, PageRequest pageRequest) {
        try {
            String name = "国家药品对照导入模板";
            List<Map<String, Object>> datas = new ArrayList<>();
            String heanders[] = {"药品编码", "国家药品编码"};
            String beannames[] = {"productCode", "countryProductCode"};
            Map<String, Boolean> lineMap = new HashMap<>();
            ExcelUtil excelUtil = new ExcelUtil(heanders, beannames, lineMap);
            Workbook workbook = excelUtil.doExportXLS(datas, name, false, true);

            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=goodsCountryTemplate.xls");
            OutputStream out = resp.getOutputStream();
            workbook.write(out);
            out.flush();
            workbook.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
