package com.shyl.msc.dm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.Pinyin4jUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.DosageForm;
import com.shyl.msc.dm.entity.Drug;
import com.shyl.msc.dm.entity.DrugType;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IDosageFormService;
import com.shyl.msc.dm.service.IDrugService;
import com.shyl.msc.dm.service.IDrugTypeService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 产品Controller
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/product")
public class ProductController extends BaseController {

	@Resource
	private IProductService productService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IDrugTypeService drugTypeService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IDrugService drugService;
	@Resource
	private IDosageFormService	dosageFormService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "dm/product/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Product> page(PageRequest pageable, @CurrentUser User user){
		System.out.println("查询药品");
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		
		DataGrid<Product> page =  productService.query(user.getProjectCode(), pageable);

		return page;
	}
	
	@RequestMapping("/pageTodirectory")
	@ResponseBody
	public DataGrid<Product> pageTodirectory(PageRequest pageable,@CurrentUser User user){
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		List<AttributeItem> attributeItems = attributeItemService.getItemSelect(user.getProjectCode(),null, "contract_gpo");
		List<Long> gpoCodes = new ArrayList<>();
		for(AttributeItem attributeItem:attributeItems){
			Company company = companyService.findByCode(user.getProjectCode(),attributeItem.getField1(), "isGPO=1");
			gpoCodes.add(company.getId());
		}
		DataGrid<Product> page =  productService.pageByGPO(user.getProjectCode(), pageable, gpoCodes);
		return page;
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<Product> list(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		List<Product> list =  productService.list(user.getProjectCode(), pageable);
		return list;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "dm/product/add";
	}
	
	/**
	 * 新增
	 * @param product
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Product product,@CurrentUser User user){
		Message message = new Message();
		try{
			/*PageRequest pageable = new PageRequest();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("t#code_S_EQ", product.getCode());
			pageable.setQuery(m);
			List<Product> list = productService.list(pageable);
			if(list.size()>0){
				throw new Exception("药品代码"+product.getCode()+"已存在");
			}*/
			setData(user.getProjectCode(), product);
			//保存到国家目录
			productService.save(user.getProjectCode(), product);
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
	public String edit(Long drugId,Long producerId,Model model){
		if(drugId!=null){
			model.addAttribute("combox1", drugId);
		}
		if(producerId!=null){
			model.addAttribute("combox2", producerId);
		}
		return "dm/product/edit";
	}
	/**
	 * 修改
	 * @param product
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Product product,@CurrentUser User user){
		Message message = new Message();
		try{
			setData(user.getProjectCode(), product);
			//productService.update(product);
			//保存到标准目录		
			productService.update(user.getProjectCode(), product);
		
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id, @CurrentUser User user){
		Message message = new Message();
		try{
			productService.delete(user.getProjectCode(), id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}

	private void setData(String projectCode, Product product){
		Company	producer = companyService.getById(projectCode, product.getProducerId());
		/*if(product.getProducerId()!=null){
			product.setProducerName(producer.getFullName());
		}*/
		if(product.getUnit()!=null&&!product.getUnit().equals("")){
			AttributeItem attributeItem = attributeItemService.getById(projectCode, new Long(product.getUnit()));
			if(attributeItem != null){
				product.setUnitName(attributeItem.getField2());
			}			
		}
		if(product.getBaseDrugType()!=null&&!product.getBaseDrugType().equals("")){
			DrugType drugType = drugTypeService.getById(projectCode, new Long(product.getBaseDrugType()));
			if(drugType != null){
				product.setBaseDrugTypeName(drugType.getName());
			}			
		}
		if(product.getInsuranceDrugType()!=null&&!product.getInsuranceDrugType().equals("")){
			DrugType drugType = drugTypeService.getById(projectCode, new Long(product.getInsuranceDrugType()));
			if(drugType != null){
				product.setInsuranceDrugTypeName(drugType.getName());
			}			
		}
		product.setModifyDate(new Date());
		
		Drug drug = drugService.getById(projectCode, product.getDrugId());
		product.setGenericName(drug.getGenericName());
		product.setEnglishName(drug.getEnglishName());
		product.setPinyin2(drug.getPinyin());
		product.setDosageFormId(drug.getDosageFormId());
		product.setDosageFormName(drug.getDosageFormName());
		product.setStyle(drug.getStyle());
		product.setPrescription(drug.getPrescription());
		product.setBackTag(drug.getBackTag());
		product.setDrugType(drug.getDrugType());
		product.setAbsDrugType(drug.getAbsDrugType());
		product.setSpecialDrugType(drug.getSpecialDrugType());
		product.setNewlyDrugType(drug.getNewlyDrugType());
		product.setPharmacologyType(drug.getPharmacologyType());
		product.setQsno(drug.getQsno());
		product.setQualityType(drug.getQualityType());
		product.setArchNo(drug.getArchNo());
		product.setNotes2(drug.getNotes());
		if (StringUtils.isEmpty(product.getPinyin())) {
			product.setPinyin(Pinyin4jUtil.getPinYinHeadChar(product.getName()));
		}
		if (product.getIsGPOPurchase() != null && product.getIsGPOPurchase() == 1
				&& product.getGpoId() != null) {
			Company	company = companyService.getById(projectCode, product.getGpoId());
			product.setGpoCode(company.getCode());
			product.setGpoName(company.getFullName());
		} else {
			product.setGpoName(null);
			product.setGpoCode(null);
			product.setGpoId(null);
			product.setIsGPOPurchase(0);
		}
		if (StringUtils.isEmpty(product.getCode())) {
			product.setCode(productService.getMaxCode(projectCode));
		}
		
		if (StringUtils.isEmpty(product.getProductGCode())) {
			DosageForm form = dosageFormService.getById(projectCode, drug.getDosageFormId());
			//中西药分类
			Long drugType = drug.getDrugType();
			//抗菌药物等级
			Long absDrugType = drug.getAbsDrugType();
			//药理分类
			Long pharmacologyType = drug.getPharmacologyType();
			//特殊类别
			Long specialDrugType = drug.getSpecialDrugType();
			//默认西药
			String ypxzCode = "X";
			//药品类型编码
			String pharmacologyTypeCode = "00000000";
			String absDrugTypeCode = "0";
			String specialDrugTypeCode = "0";
			String genericCode = drug.getGenericCode();
			
			if (drugType != null) {
				AttributeItem item = attributeItemService.getById(projectCode, drugType);
				ypxzCode = item.getField3();
			}
			if (absDrugType != null) {
				DrugType type = drugTypeService.getById(projectCode, absDrugType);
				absDrugTypeCode = type.getCode();
			}
			if (pharmacologyType != null) {
				DrugType type = drugTypeService.getById(projectCode, pharmacologyType);
				pharmacologyTypeCode = type.getCode();
			}
			if (specialDrugType != null) {
				DrugType type = drugTypeService.getById(projectCode, specialDrugType);
				specialDrugTypeCode = type.getCode();
			}
			
			if (genericCode == null) {
				genericCode = "00000";
			}
			
			//新编码
			String newcode = ypxzCode + genericCode +"-"+ form.getCode()+"-"+
					producer.getCode()+"-"+pharmacologyTypeCode;
			//规格序号
			String modelCode = productService.getMaxModelCode(projectCode, newcode, product.getModel());
			//包装序号
			String packCode = productService.getMaxPackCode(projectCode, newcode, product.getModel(), product.getPackDesc());
			//拼接包装规格
			newcode = newcode + "-" + modelCode + packCode;
			newcode += "-" + specialDrugTypeCode + absDrugTypeCode;
			byte[] bytes = newcode.getBytes();
			int id = 1;
			for(byte c : bytes) {
				id = (int)c ^ id;
			}
			//加上身份码
			newcode = newcode + "-" + (id%10);
			product.setProductGCode(newcode);
			product.setModelCode(modelCode);
			product.setPackCode(packCode);
		} else {
			String code1 = producer.getCode();
			String code2 = product.getProductGCode().substring(15, 19);
			if(!code1.equals(code2)){
				String code = "-"+producer.getCode()+"-";
				String producttGCode =  product.getProductGCode();
				
				String frontCode = producttGCode.substring(0, 13);
				String centerCode = producttGCode.substring(20,36);
				String newCode = frontCode+code+centerCode;
				byte[] bytes = newCode.getBytes();
				int id = 1;
				for(byte c : bytes) {
					id = (int)c ^ id;
				}
				producttGCode = newCode+"-"+ (id%10);
				product.setProductGCode(producttGCode);
			}
		}
			
	}

	/**
	 * ajax获取药品库数据进行同步保存
	 * @param jsonStr
	 * @return
	 */
	@RequestMapping(value = "/saveProduct", method = RequestMethod.POST)
	@ResponseBody
	public Message saveProduct(String jsonStr,@CurrentUser User user) {
		Message msg = new Message();
		if (!StringUtils.isEmpty(jsonStr)) {
			JSONArray array = JSONArray.parseArray(jsonStr);
			for(int i = 0; i < array.size(); i++) {
				JSONObject o = array.getJSONObject(i);
				JSONObject d = o.getJSONObject("drug");
				JSONObject c = o.getJSONObject("company");
				JSONObject f = o.getJSONObject("dosageForm");
				Product product = productService.getByCode(user.getProjectCode(), o.getString("code"));
				DosageForm form = null;
				if(f!= null){
					form = dosageFormService.getByCode(user.getProjectCode(), f.getString("code"));
					if(form == null) {
						form = JSONArray.toJavaObject(f, DosageForm.class);
						form = dosageFormService.save(user.getProjectCode(), form);
					}
				}
				Drug drug = drugService.getByCode(user.getProjectCode(), d.getString("code"));
				if (drug == null) {
					drug = JSONArray.toJavaObject(d, Drug.class);
					if(f != null){
						drug.setDosageFormId(form.getId());
					}
					drug = drugService.save(user.getProjectCode(), drug);
				}
				
				Company company = companyService.findByCode1(user.getProjectCode(), c.getString("code"));
				if (company == null) {
					company = JSONArray.toJavaObject(c, Company.class);
					company = companyService.save(user.getProjectCode(), company);
				}
				
				if (product == null) {
					product =  JSONArray.toJavaObject(o, Product.class);
					product.setDrugId(drug.getId());
					product.setGenericName(drug.getGenericName());
					product.setDosageFormId(drug.getDosageFormId());
					product.setDosageFormName(drug.getDosageFormName());
					product.setDrugType(drug.getDrugType());
					product.setAbsDrugType(drug.getAbsDrugType());
					product.setSpecialDrugType(drug.getSpecialDrugType());
					product.setNewlyDrugType(drug.getNewlyDrugType());
					product.setPharmacologyType(drug.getPharmacologyType());
					product.setProducerId(company.getId());
					product.setProducerName(company.getFullName());
					productService.save(user.getProjectCode(), product);
				}else{
					product.setDrugId(drug.getId());
					product.setGenericName(drug.getGenericName());
					product.setDosageFormId(drug.getDosageFormId());
					product.setDosageFormName(drug.getDosageFormName());
					product.setDrugType(drug.getDrugType());
					product.setAbsDrugType(drug.getAbsDrugType());
					product.setSpecialDrugType(drug.getSpecialDrugType());
					product.setNewlyDrugType(drug.getNewlyDrugType());
					product.setPharmacologyType(drug.getPharmacologyType());
					product.setProducerId(company.getId());
					product.setProducerName(company.getFullName());
					//本位码
					product.setStandardCode(o.getString("standardCode"));
					productService.update(user.getProjectCode() , product);
				}
			}
		} else {
			msg.setMsg("请选择药品数据");
			msg.setSuccess(false);
		}
		return msg;
	}
}
