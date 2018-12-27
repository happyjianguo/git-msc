package com.shyl.msc.task;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.shyl.common.WormProperties;
import com.shyl.common.framework.util.StringUtil;
import com.shyl.common.util.NetWormTools;
import com.shyl.msc.menu.entity.MedicalDevices;
import com.shyl.msc.menu.service.IMedicalDevicesService;
@SuppressWarnings({"rawtypes","unchecked"})
@Component
public class MedicDevicesFoundAsyncTask {
	
	@Resource
	private IMedicalDevicesService medicalDevicesService;
	
	//抓取错误次数
	public static int errorcount = 0;
	//抓取状态0，停止 1正在抓取， 2休眠中
	public static int status = 0;
	
	public static int tableid = 0;
	
	public static int sleepTime = 1000;

	@Async
	public void syncMedicDevices(int tableid) {
		int page = Integer.valueOf(WormProperties.get("PRODUCT_"+ tableid + "_PAGE", "1"));
		int pageNo = Integer.valueOf(WormProperties.get("PRODUCT_"+ tableid + "_PAGENO", "9999"));
		while(page <= pageNo) {
			if (MedicDevicesFoundAsyncTask.status == 0) {
				break;
			}
			MedicDevicesFoundAsyncTask.tableid = tableid;
			System.out.println("开始查询页码"+page+";总页数"+pageNo);
			syncProductData(tableid, page);
			page = Integer.valueOf(WormProperties.get("PRODUCT_"+ tableid + "_PAGE", "1"));
			pageNo = Integer.valueOf(WormProperties.get("PRODUCT_"+ tableid + "_PAGENO", "9999"));
			
		}
		System.out.println("药品数据抓取线程已停止");
		MedicDevicesFoundAsyncTask.tableid = 0;
		MedicDevicesFoundAsyncTask.status = 0;
		MedicDevicesFoundAsyncTask.errorcount = 0;
	}
	


	private void syncProductData(int tableId, int curstart) {
    	String path="http://125.35.6.7/datasearch/face3/search.jsp?tableId="+tableId+"&tableName=TABLE"+tableId+"&curstart="+curstart;
    	try {
			String html = NetWormTools.doPost(path);
			String row = null;
			if (html.indexOf("共")>0 && html.indexOf("页")>0) {
				String newHtml = html.substring(html.lastIndexOf("devPage")-1000,html.length());
				String lastPage = newHtml.substring(newHtml.indexOf("共")+1, newHtml.lastIndexOf("页"));
				if (StringUtil.isNumber(lastPage)) {
					WormProperties.put("PRODUCT_"+tableId+"_PAGENO", lastPage);
				}
				List saveList = new ArrayList();
				List updateList = new ArrayList();
				while(html.indexOf("<p")>=0 && html.indexOf("</p>")>=0 ) {
					int nextRow = html.indexOf("</p>")+4;
					row = html.substring(html.indexOf("<p"), nextRow);
					html = html.substring(nextRow, html.length());

					row = row.substring(row.indexOf("&Id=")+4, row.length());
					
					String id = row.substring(0, row.indexOf("'"));
					//获取药品或者规格详细信息
					Object bean = getProductContent(tableId, id);
					if (bean == null) {
						return;
					}
					//国产器械26   进口器械27
					if (tableId == 26||tableId==27) {
						MedicalDevices data = medicalDevicesService.getByExtId(Integer.valueOf(id), tableId);
						if (data != null) {
							Method method = bean.getClass().getMethod("setId", Long.class);
							method.invoke(bean, data.getId());
							updateList.add(bean);
						} else {
							saveList.add(bean);
						}
						
					} 
				}
				medicalDevicesService.saveBatch("", saveList);
				medicalDevicesService.updateBatch("", updateList);
    			curstart++;
				errorcount=0;
				WormProperties.put("PRODUCT_" + tableId + "_PAGE", String.valueOf(curstart));
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		errorcount++;
		try {
			if (errorcount>5) {
				NetWormTools.cookieStr = "";
				Thread.sleep(sleepTime);
			} else if (errorcount>10) {
		    	status = 2;
				System.out.println("连续错误15次，进入休眠30分钟");
				errorcount=0;
				//休眠30分钟
				Thread.sleep(1800000);
		    	status = 1;
			} else {
				Thread.sleep(sleepTime);
				syncProductData(tableId, curstart);
			}
		} catch (Exception e) {
		}
	}
	
	private Object getProductContent(int tableId, String id) {
		if (MedicDevicesFoundAsyncTask.status == 0) {
			return null;
		}
    	String path = "http://125.35.6.7/datasearch/face3/content.jsp?tableId="+tableId+"&tableName=TABLE"+tableId+"&Id="+id;
		try {
			String html = NetWormTools.doPost(path);
			if (html.indexOf("<table")>0&&html.indexOf("</table>")>0) {
				html = html.substring(html.indexOf("<table"),html.indexOf("</table>")+8);

				String row = null;
				String key = null;
				String value = null;
				String[] args = null;
				String[] columns = null;

				Class classz = null;
				if (tableId == 26) {
					args = new String[]{"注册证编号","注册人名称","注册人住所","生产地址","代理人名称","代理人住所","产品名称","型号、规格","结构及组成","适用范围","备注",
							"批准日期","有效期至","产品标准","变更日期","主要组成成分（体外诊断试剂）","预期用途（体外诊断试剂）","产品储存条件及有效期（体外诊断试剂）"};
					columns =  new String[]{"RegistCode","RegistName","RegistAddress","ProducerAddress","AgentName","AgentAddress","ProductName",
							"Model","Structure","Range","Notes","AuthorizeDate","ValidExpiry","ProductStandard","ChageDate","MainContent",
							"ExpectUse","StorageCondition"};
					classz = MedicalDevices.class;
				}else if(tableId==27){
					args = new String[]{"产品名称","注册证编号","注册人名称","注册人住所","生产地址","代理人名称","代理人住所","型号、规格","结构及组成","适用范围","生产国或地区（英文）","备注",
							"批准日期","有效期至","生产厂商名称（中文）","产品名称（中文）","产品标准","生产国或地区（中文）","售后服务机构","变更日期","主要组成成分（体外诊断试剂）","预期用途（体外诊断试剂）","产品储存条件及有效期（体外诊断试剂）"};
					columns =  new String[]{"EnglishName","RegistCode","RegistName","RegistAddress","ProducerAddress","AgentName","AgentAddress",
							"Model","Structure","Range","AreaEName","Notes","AuthorizeDate","ValidExpiry","ProducerName","ProductName","ProductStandard",
							"AreaName","AfterSaleOrg","ChageDate","MainContent","ExpectUse","StorageCondition"};
					classz = MedicalDevices.class;
				}
				Object bean = classz.newInstance();
				while(html.indexOf("<tr")>=0 && html.indexOf("</tr>")>=0 ) {
					int nextRow = html.indexOf("</tr>")+5;
					row = html.substring(html.indexOf("<tr"), nextRow);
					html = html.substring(nextRow, html.length());
					if (row.split("</td>").length !=3) {
						continue;
					}
					int keyEnd = row.indexOf("</td>")+5;
					key = row.substring(row.indexOf("<td"), keyEnd);
					row = row.substring(keyEnd, row.length());
					keyEnd = row.indexOf("</td>")+5;
					value = row.substring(row.indexOf("<td"), keyEnd);
					if (key.length() != 0) {
						key = NetWormTools.stripHtml(key);
					}
					if (key.length() != 0){
						value = NetWormTools.stripHtml(value);
						for(int i=0;i<args.length;i++) {
							if (args[i].equals(key)) {
								System.out.print(columns[i]+":"+value+";");
								//填充数据
								Method method = classz.getMethod("set"+columns[i], String.class);
								method.invoke(bean, value);
								break;
							}
						}
					}
				}
				if(tableId == 26||tableId==27) {
					//填充数据
					Method method = classz.getMethod("setExtId", Integer.class);
					method.invoke(bean, Integer.valueOf(id));
					method = classz.getMethod("setType", Integer.class);
					method.invoke(bean, tableId);
				} 
				errorcount = 0;
				Thread.sleep(sleepTime);
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorcount++;
	    }
		try {
			if (errorcount>5) {
				NetWormTools.cookieStr = "";
				Thread.sleep(sleepTime);
			} else if (errorcount>10) {
					System.out.println("连续错误20次，进入休眠30分钟");
					errorcount=0;
					//休眠30分钟
					Thread.sleep(1800000);
			} else {
				Thread.sleep(sleepTime);
			}
		} catch (Exception e) {
		}
		return getProductContent(tableId, id);
	}
}
