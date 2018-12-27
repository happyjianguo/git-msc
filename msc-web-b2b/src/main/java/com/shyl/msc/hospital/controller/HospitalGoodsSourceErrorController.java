package com.shyl.msc.hospital.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.GoodsHospitalSource;
import com.shyl.msc.dm.service.IGoodsHospitalSourceService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;


/**
 * 问题数据
 * @author Administrator
 */
@Controller
@RequestMapping("/hospital/goodsSourceError")
public class HospitalGoodsSourceErrorController extends BaseController {

	@Resource
	private IGoodsHospitalSourceService goodsHospitalSourceService;
	@Resource
	private IProductService	productService;
	
	
	protected void init(WebDataBinder binder) {

	}

	@RequestMapping("")
	public String home() {
		return "hospital/goodsSourceError/list";
	}

	/**
	 * 医院就编码分页
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/sourcePage", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> sourcePage(PageRequest page, @CurrentUser User user) {
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
			page.setQuery(query);
		}
		query.put("t#status_L_GE", 3);
		query.put("t#status_L_LE", 4);
		Long hospitalId = -1L;
		if (user != null) {
			hospitalId = user.getOrganization().getOrgId();
		}
		query.put("t#hospitalId_L_EQ", hospitalId);
		return goodsHospitalSourceService.npquery(user.getProjectCode(), page);
	}
	
	/**
	 * 保存并备案，或保存并核对
	 * @param source
	 * @return
	 */
	@RequestMapping(value="/doSave", method=RequestMethod.POST)
	@ResponseBody
	public Message doSave(GoodsHospitalSource source,@CurrentUser User user) {
		Message	msg = new Message();
		try{
			goodsHospitalSourceService.saveMapping(user.getProjectCode(), source);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	

	
	/**
	 * 未添加目录 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/newProductPage")
	@ResponseBody
	public JSONObject newProductPage(String productName, String producerName, 
			String dosageFormName, String productCode, PageRequest pageable, @CurrentUser User user){	
		List<String> productWords = new ArrayList<String>();
		List<String> dosageWords = new ArrayList<String>();
		List<String> producerWords = new ArrayList<String>();
		if (!StringUtils.isEmpty(producerName)) {
			producerWords = getWords(producerName, null);
		}
		if (!StringUtils.isEmpty(dosageFormName)) {
			dosageWords = getWords(dosageFormName, null);
		}
		if (!StringUtils.isEmpty(productName)) {
			List<String> stop = new ArrayList<String>();
			for (String dosa : dosageWords) {
				stop.add(dosa);
			}
			stop.add("注射剂");
			stop.add("胶囊");
			stop.add("分散片");
			stop.add("精品");
			stop.add("药膏");
			//获取产品的分词，排除剂型
			productWords = getWords(productName, stop);
		}
		
		DataGrid<Map<String, Object>> page =  productService
				.npquery(user.getProjectCode(), productWords, dosageWords, producerWords, productCode, pageable);
		
		for(Map<String,Object> map : page.getRows()){
			//如果有传递code说明该code被对应了
			if(map.get("CODE").equals(productCode)){
				map.put("selected", "true");
			}
		}
		JSONObject result = new JSONObject();
		result.put("rows", page.getRows());
		result.put("tooter", page.getFooter());
		result.put("total", page.getTotal());
		result.put("producerWords", producerWords);
		result.put("productWords", productWords);
		result.put("dosageWords", dosageWords);
		
		return result;
	}
	

	/**
	 * 获取分词
	 * @param searchKey
	 * @param topWords
	 * @return
	 * @throws IOException
	 */
	private List<String> getWords(String searchKey, List<String> topWords) {
		try {
			List<String> words = new ArrayList<String>();
	        Reader read = new InputStreamReader(new ByteArrayInputStream(searchKey.getBytes()));  
	        IKSegmentation iks = new IKSegmentation(read,true);//true开启只能分词模式，如果不设置默认为false，也就是细粒度分割
	        
	        Lexeme t;  
	        //判断是否存在分词信息
	        while ((t = iks.next()) != null) {  
	            //关键次
	            String word = t.getLexemeText();
	        	if (topWords != null && topWords.contains(word)) {
	        		continue;
	        	} 
	            //只有大于等于1个长度的词语才做分词
	            if (word.length() > 1) {
	                words.add(word);
	            }
	        }
	        if (words.size() == 0) {
	            words.add(searchKey);
	        }
	        return words;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
