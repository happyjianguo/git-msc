package com.shyl.msc.base.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.util.GridFSDAO;
import com.shyl.msc.dm.entity.GoodsHospitalSource;
import com.shyl.msc.dm.service.IDrugService;
import com.shyl.msc.dm.service.IGoodsHospitalSourceService;
import com.shyl.sys.entity.FileManagement;
import com.shyl.sys.entity.Msg;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IFileManagementService;
import com.shyl.sys.service.IMsgService;

@Component
public class ProductSourceAsyncTask {

	@Resource
	private IGoodsHospitalSourceService goodsHospitalSourceService;
	@Resource
	private IDrugService drugService;
	@Resource
	private GridFSDAO gridFSDAO;
	@Resource
	private IFileManagementService fileManagementService;
	@Resource
	private IMsgService iMsgService;

	@Async
	public void mkFileByNeedCreate(User user)  {
		//生成未对照数据
		saveFile(0L,user);
		//生成自动数据
		saveFile(1L,user);
		//生成备案数据
		saveFile(5L,user);
		//生成备案数据
		saveFile(6L,user);
	}
	
	private void saveFile(Long status,User user) {
		PageRequest page = new PageRequest();
		Sort sort = new Sort(new Order(Direction.ASC,"genericname"),
				 new Order(Direction.ASC,"dosageformname"),
				 new Order(Direction.ASC,"model"),
				 new Order(Direction.ASC,"convertratio0"),
				 new Order(Direction.ASC,"producername"),
					new Order(Direction.DESC,"productCode"));
		page.setSort(sort);
		page.setPageSize(500);

		Map<String, Object> query = new HashMap<String, Object>();
		query.put("t#status_L_EQ", status);
		page.setQuery(query);
		
		DataGrid<GoodsHospitalSource> data = goodsHospitalSourceService.query(user.getProjectCode(), page);
		//获取文件基本目录
		String basePath = getClass().getResource("/").getPath();
		basePath = basePath.substring(0, basePath.indexOf("/WEB-INF"))+"/download/";
		//处理目录，没有的生成
		File file = new File(basePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		String keyFlg = "";
		String msg = "";
		if (status == 0) {
			keyFlg = "unCompare";
			msg = "未对照数据excel已生成";
		} else if(status == 1) {
			keyFlg = "autoCompare";
			msg = "自动对照数据excel已生成";
		} else if(status == 5) {
			keyFlg = "isBak5";
			msg = "医院备案数据excel已生成";
		} else if(status == 6) {
			keyFlg = "isBak6";
			msg = "药师备案数据excel已生成";
		}
		String filename = keyFlg + ".xls";
		//文件服务中的key值
		if ("\\".equals(File.separator)) {
			file = new File((basePath+filename).replaceAll("/", "\\\\"));
		} else {
			file = new File(basePath+filename);
		}
		FileOutputStream out  = null;
		Workbook wb = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			wb = new HSSFWorkbook();

			/*
			通用名1	药品名称2
			商品名3	药品规格4	药品单位5	包装规格6	药品剂型7	生产企业8	药品性质（中药.中药；西药.西药）9	单位换算比10	最小制剂单位11	
			基本药物类型12	 药品本位码13	注册证号14	批准文号15	
			抗菌药DDD值16	抗菌药物级别17	剂量18	
			剂量单位19	医保剂型归类20	深圳医保编码21	医保类别22	
			药理分类-第1级23	药理分类-第2级24	药理分类-第3级25	药理分类-第4级26	特殊药品分类27*/

			 String[] title = {"通用名","药品名称","商品名","药品规格","药品单位","包装规格","药品剂型","生产企业","药品性质（中药.中药；西药.西药）","单位换算比","最小制剂单位",
					 "基本药物类型","药品本位码","注册证号","批准文号","抗菌药DDD值","抗菌药物级别","剂量","剂量单位","医保剂型归类","深圳医保编码","医保类别",
					 "药理分类-第1级","药理分类-第2级","药理分类-第3级","药理分类-第4级","特殊药品分类"};

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
					 String genericName = source.getGenericName();
					 Map<String, String> map = null;
					 if (!StringUtils.isEmpty(source.getGenericName())) {
						 map = drugService.queryDrugInfoByName(user.getProjectCode(), source.getGenericName(), source.getProductCode());
					 } else if(!StringUtils.isEmpty(source.getProductName())) {
						 map = drugService.queryDrugInfoByName(user.getProjectCode(), source.getProductName(), source.getProductCode());
					 }
					 
					 //处理剂型数
					 if (map != null && !map.isEmpty()) {
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
				 	cell.setCellValue(genericName);
				 	cell = row.createCell(1, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(source.getProductName());
				 	cell = row.createCell(3, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(model);
				 	cell = row.createCell(4, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(source.getUnitName());
				 	cell = row.createCell(5, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(packDesc);
				 	cell = row.createCell(6, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(source.getDosageFormName());
				 	cell = row.createCell(7, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(source.getProducerName());
				 	cell = row.createCell(8, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("YPXZ"));
				 	if( source.getConvertRatio0() != null) {
					 	cell = row.createCell(9, Cell.CELL_TYPE_STRING);
					 	cell.setCellValue(source.getConvertRatio0().toString());
				 	};
				 	cell = row.createCell(10, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(minUnit);
				 	cell = row.createCell(11, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("BASEDRUGTYPENAME"));
				 	cell = row.createCell(12, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(source.getStandardCode());
				 	cell = row.createCell(14, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(source.getAuthorizeNo());
				 	cell = row.createCell(15, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("DDD"));
				 	cell = row.createCell(16, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("ABSDRUGTYPENAME"));
				 	/*
					通用名1	药品名称2
					商品名3	药品规格4	药品单位5	包装规格6	药品剂型7	生产企业8	药品性质（中药.中药；西药.西药）9	单位换算比10	最小制剂单位11	
					基本药物类型12	 药品本位码13	注册证号14	批准文号15	
					抗菌药DDD值16	抗菌药物级别17	剂量18	
					剂量单位19	医保剂型归类20	深圳医保编码21	医保类别22	
					药理分类-第1级23	药理分类-第2级24	药理分类-第3级25	药理分类-第4级26	特殊药品分类27*/
				 	if (!StringUtils.isEmpty(source.getProductCode())) {
					 	cell = row.createCell(17, Cell.CELL_TYPE_STRING);
					 	cell.setCellValue(map.get("DOSE"));
					 	cell = row.createCell(18, Cell.CELL_TYPE_STRING);
					 	cell.setCellValue(map.get("DOSEUNIT"));
				 	}
				 	cell = row.createCell(22, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("DRUGTYPE0"));
				 	cell = row.createCell(23, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("DRUGTYPE1"));
				 	cell = row.createCell(24, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("DRUGTYPE2"));
				 	cell = row.createCell(25, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("DRUGTYPE3"));
				 	cell = row.createCell(26, Cell.CELL_TYPE_STRING);
				 	cell.setCellValue(map.get("SPECIALDRUGTYPENAME"));
				 
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
			 	Sheet sh = wb.createSheet("sheet1");
			 	Row row = sh.createRow(0);
			 	for (int i=0;i<title.length;i++) {
			 		Cell cell = row.createCell(i);
			 		cell.setCellValue(title[i]);
			 	}
		 	}
			wb.write(out);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
				}
			}
		}
		try {

			//保存文件
			String fileid = gridFSDAO.saveFile(file, filename, "GoodsHospitalSource");
			//获取医院未对照或已经对照数据的方法
			List<FileManagement> fmList = fileManagementService.findByKeyFlag(user.getProjectCode(), "GoodsHospitalSource_"+keyFlg);
			FileManagement fm= null;
			if (fmList.size() > 0) {
				//取最后一个
				fm = fmList.get(0);
			} else {
				fm = new FileManagement();
			}
			fm.setFileName(fileid);
			fm.setFileURL(fileid);
			fm.setKeyFlag("GoodsHospitalSource_"+keyFlg);
			//判断新增还是修改
			if (fm.getId() == null) {
				fileManagementService.save(user.getProjectCode(), fm);
			} else {
				fileManagementService.update(user.getProjectCode(), fm);
			}
			//保存系统消息
			Msg message = new Msg();
			message.setOrganizationId(user.getOrganization().getId());
			message.setOrgName(user.getOrganization().getOrgName());
			message.setTitle(msg);
			iMsgService.sendBySYSToOrg(user.getProjectCode(), message, user.getOrganization().getOrgId());
			System.out.println("任务执行结束");
		} catch (Exception e) {
			System.out.println("任务执行结束");
			e.printStackTrace();
		}
		
	}
}
