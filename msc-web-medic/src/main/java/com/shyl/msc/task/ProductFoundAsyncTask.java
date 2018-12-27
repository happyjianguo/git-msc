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
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.menu.entity.ProductData;
import com.shyl.msc.menu.entity.Supplement;
import com.shyl.msc.menu.service.IProductDataService;
import com.shyl.msc.menu.service.ISupplementService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;

@SuppressWarnings({"rawtypes","unchecked"})
@Component
public class ProductFoundAsyncTask {
	
	@Resource
	private IProductDataService productDataService;
	@Resource
	private ISupplementService supplementService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	//抓取错误次数
	public static int errorcount = 0;
	//抓取状态0，停止 1正在抓取， 2休眠中
	public static int status = 0;
	
	public static int tableid = 0;
	
	public static int sleepTime = 1000;

	@Async
	public void syncProductData(int tableid) {
		int page = Integer.valueOf(WormProperties.get("PRODUCT_"+ tableid + "_PAGE", "1"));
		int pageNo = Integer.valueOf(WormProperties.get("PRODUCT_"+ tableid + "_PAGENO", "9999"));
		while(page <= pageNo) {
			if (ProductFoundAsyncTask.status == 0) {
				break;
			}
			ProductFoundAsyncTask.tableid = tableid;
			System.out.println("开始查询页码"+page+";总页数"+pageNo);
			syncProductData(tableid, page);
			page = Integer.valueOf(WormProperties.get("PRODUCT_"+ tableid + "_PAGE", "1"));
			pageNo = Integer.valueOf(WormProperties.get("PRODUCT_"+ tableid + "_PAGENO", "9999"));
			
		}
		System.out.println("药品数据抓取线程已停止");
		ProductFoundAsyncTask.tableid = 0;
		ProductFoundAsyncTask.status = 0;
		ProductFoundAsyncTask.errorcount = 0;
	}
	

	@Async
	public void syncBaseDrug() {
	}
	
	@Async
	public void syncSpecialDrugDao() {
		
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
					if (tableId != 63) {
						ProductData data = productDataService.getByExtId(Integer.valueOf(id), tableId);
						if (data != null) {
							
							Method method = bean.getClass().getMethod("setId", Long.class);
							method.invoke(bean, data.getId());
							updateList.add(bean);
						} else {
							saveList.add(bean);
						}
					} else {
						Supplement sup = supplementService.getByExtId(Integer.valueOf(id));
						if (sup != null) {
							Method method = bean.getClass().getMethod("setId", Long.class);
							method.invoke(bean, sup.getId());
							updateList.add(bean);
						} else {
							saveList.add(bean);
						}
					}
				}
				//保存本次数据
				AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "PLATFORM_NO");
				
				if(tableId == 63) {
					supplementService.saveBatch("", saveList);
					supplementService.updateBatch("", updateList);
				} else {
					productDataService.saveBatch("", saveList);
					productDataService.updateBatch("", updateList);
				}
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
		if (ProductFoundAsyncTask.status == 0) {
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
				if (tableId == 25) {
					args = new String[]{"批准文号","产品名称","英文名称","商品名","剂型","规格","生产单位","产品类别","药品本位码"};
					columns =  new String[]{"AuthorizeNo","ProductName","EnglishName","TradeName","DosageFormName","Model","ProducerName","DrugType","StandardCode"};
					classz = ProductData.class;
				} else if (tableId == 63) {
					args = new String[]{"药品通用名称","包装规格","规格","批准文号","药品生产企业"};
					columns =  new String[]{"ProductName","PackDesc","Model","AuthorizeNo","ProducerName"};
					classz = Supplement.class;
				} else {
					args = new String[]{"注册证号","原注册证号","分包装批准文号","公司名称（中文）","公司名称（英文）","产品名称（中文）","产品名称（英文）","商品名（中文）","剂型（中文）","规格（中文）",
							"分包装企业名称","包装规格（中文）","药品本位码"};
					columns =  new String[]{"AuthorizeNo","OldAuthorizeNo","SplitAuthorizeNo","ProducerName","ProducerEName",
							"ProductName","EnglishName","TradeName","DosageFormName","Model",
							"SplitProducerName","PackDesc","StandardCode"};
					classz = ProductData.class;
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
								if (columns[i]!=null && columns[i].endsWith("AuthorizeNo")&&value.length()>=9) {
									value = value.substring(value.length()-9, value.length());
								}
								//填充数据
								Method method = classz.getMethod("set"+columns[i], String.class);
								method.invoke(bean, value);
								break;
							}
						}
					}
				}
				if(tableId == 63) {
					//填充数据
					Method method = classz.getMethod("setExtId", Integer.class);
					method.invoke(bean, Integer.valueOf(id));
				}  else {
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
	
	
	
	public static void main(String[] args) {
		ProductFoundAsyncTask task = new ProductFoundAsyncTask();
		//ProductFoundAsyncTask.errorcount = 0;
		ProductFoundAsyncTask.status = 0;
		ProductFoundAsyncTask.tableid = 0;
		ProductFoundAsyncTask.tableid = 0;
		
		NetWormTools.cookieStr = "";
		ProductFoundAsyncTask.status = 1;
		//task.syncProductData(25);
		task.getProductContent(25, "29813");
		//System.out.println(WormProperties.get("PRODUCT_25_PAGENO", "33"));
	}
	
}
