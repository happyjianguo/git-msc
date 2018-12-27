package com.shyl.msc.dm.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.framework.util.StringUtil;
import com.shyl.msc.dm.dao.IGoodsDao;
import com.shyl.msc.dm.dao.IGoodsHospitalSourceDao;
import com.shyl.msc.dm.dao.IProductDao;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsHospitalSource;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsHospitalSourceService;
import com.shyl.msc.set.dao.IHospitalDao;
import com.shyl.msc.set.entity.Hospital;

/**
 * 
 * 
 * @author a_Q
 *
 */
@Service
public class GoodsHospitalSourceService extends
		BaseService<GoodsHospitalSource, Long> implements
		IGoodsHospitalSourceService {

	private IGoodsHospitalSourceDao goodsHospitalSourceDao;
	@Resource
	private IHospitalDao hospitalDao;
	@Resource
	private IProductDao productDao;
	@Resource
	private IGoodsDao	goodsDao;

	public IGoodsHospitalSourceDao getGoodsHospitalOldDao() {
		return goodsHospitalSourceDao;
	}

	@Resource
	public void setGoodsHospitalSourceDao(
			IGoodsHospitalSourceDao goodsHospitalSourceDao) {
		this.goodsHospitalSourceDao = goodsHospitalSourceDao;
		super.setBaseDao(goodsHospitalSourceDao);
	}

	@Override
	@Transactional
	public void saveGoodsHospitalSource(String projectCode, String[][] olds) throws Exception {
		Map<String, Hospital> map = new HashMap<String, Hospital>();
		
		/**
		 * 1医院名称	2医院内部药品编码	3用药监管平台编码	4医保编码	
		 * 5批准文号	6药交ID	7药品本位码	8物价ID	9药品名称	
		 * 10通用名	11药品规格	12包装单位	13单位转换比	
		 * 14药品厂家	15药品剂型	16零售价	17供应企业	18供应价	19基本药物标识
		 */

		for (int i = 0; i < olds.length; i++) {
			String[] oldArr = olds[i];
			if (oldArr == null || oldArr[0] == null) {
				continue;
			}
			String hospitalName = oldArr[0].trim();
			String internalCode = oldArr[1].trim();
			String yyjgcode = oldArr[2];
			String ybdrugsNO = oldArr[3];
			String authorizeNo = oldArr[4];
			String yjcode = oldArr[5];
			String standardCode = oldArr[6];
			String wjcode = oldArr[7];
			String productName = oldArr[8];
			String genericName = oldArr[9];
			String model = oldArr[10];
			String minunit = oldArr[11];
			String unitName = oldArr[12];
			String convertRatio = oldArr[13];
			String producerName = oldArr[14];
			String dosageFormName = oldArr[15];
			String finalPrice = oldArr[16];
			String vendorName = oldArr[17];
			String biddingPrice = oldArr[18];

			if (!StringUtils.isEmpty(model)) {
				model = model.replaceAll("国际单位", "IU")
						.replaceAll("单位", "IU").replaceAll("（", "(")
						.replaceAll("）", ")").replaceAll("）", ")")
						.replaceAll("：", ":");
			}
			     
			// 医院
			if (map.get(hospitalName) == null) {
				Hospital hospital = hospitalDao.findByName(hospitalName);
				if (hospital == null) {
					throw new MyException("医院名称：" + hospitalName + "不存在");
				}
				map.put(hospital.getFullName(), hospital);
			}
			//过滤同编码，医院，规格，厂家的数据，表明数据相同，但是供应商不同
			GoodsHospitalSource old = goodsHospitalSourceDao.getByCode(internalCode, productName,
					producerName, hospitalName, model);
			if (old == null) {
				old = new GoodsHospitalSource();
			} else {
				System.out.println("内部编码："+internalCode);
				continue;
			}
			//拆分规格
			String[] models = splitModel(model);
			if (StringUtils.isEmpty(convertRatio)) {
				convertRatio = splitConvertRatio(model);
			}
			//查询本位吗对应数量的资源
			Product source = null;
			if (StringUtils.isNotEmpty(standardCode) && source == null) {
				source = productDao.getByStandardCode(standardCode, models, convertRatio);
			}
			if (StringUtils.isNotEmpty(authorizeNo) && source == null) {
				source = productDao.getByAuthorizeNo(authorizeNo, models, convertRatio);
			}
			if (StringUtils.isNotEmpty(wjcode) && source == null) {
				source = productDao.getByPriceFileNo(wjcode, models, convertRatio);
			}
			if (StringUtils.isNotEmpty(genericName) && source == null) {
				source = productDao.getByGenericName(genericName, models, producerName, convertRatio);
			}
			//如果是未对照才自动对照。
			if (old.getStatus() == null  || old.getStatus() < 2 
					|| (old.getConvertRatio() != null && old.getConvertRatio().doubleValue() == 0.00)) {
				
				if (source != null) {
					//转换比
					BigDecimal convertRatio0 = new BigDecimal(source.getConvertRatio());
					
					if (convertRatio0 != null && !convertRatio0.equals(0)) {
						//如果没有包装数量的数据将包装数据默认设置成标准目录的数据
						if (convertRatio != null && ("1".equals(convertRatio) || "0".equals(convertRatio))) {
							convertRatio = convertRatio0.toString();
						}
						BigDecimal dec = convertRatio0
							.divide(new BigDecimal(convertRatio),3,BigDecimal.ROUND_HALF_UP);
						old.setConvertRatio(dec);
						old.setProductCode(source.getCode());
						old.setStatus(1L);
					} else {
						old.setConvertRatio(new BigDecimal(1));
						old.setProductCode(source.getCode());
						old.setStatus(1L);
					}
				} else {
					old.setStatus(0L);
				}
			}
			if (!StringUtils.isEmpty(convertRatio)) {
				old.setConvertRatio0(new BigDecimal(convertRatio));
			}
			Hospital hospital = map.get(hospitalName);
			old.setHospitalId(hospital.getId());
			old.setHospitalName(hospitalName);
			old.setHospitalCode(hospital.getCode());
			old.setInternalCode(internalCode);
			old.setAuthorizeNo(authorizeNo);
			old.setProductName(productName);
			old.setGenericName(genericName);
			if (StringUtils.isEmpty(genericName)) {
				old.setGenericName(productName);
			}
			if (StringUtils.isEmpty(old.getProductName())) {
				old.setProductName(old.getGenericName());
			}
			old.setModel(model);
			old.setUnitName(unitName);
			old.setProducerName(producerName);
			old.setDosageFormName(dosageFormName);
			if (!StringUtils.isEmpty(biddingPrice) && StringUtil.isNumber(biddingPrice)) {
				old.setBiddingPrice(new BigDecimal(biddingPrice));
			}
			if (!StringUtils.isEmpty(finalPrice)) {
				if (!StringUtils.isNumeric(finalPrice)) {
					old.setFinalPrice(new BigDecimal(finalPrice));
				}
				old.setFinalPrice(new BigDecimal(finalPrice));
			}
			old.setMinunit(minunit);
			old.setVendorName(vendorName);
			old.setYyjgcode(yyjgcode);
			old.setYbdrugsNO(ybdrugsNO);
			old.setYjCode(yjcode);
			old.setStandardCode(standardCode);
			old.setPriceFileNo(wjcode);
			if (old.getId() == null) {
				this.save(projectCode, old);
			} else {
				this.update(projectCode, old);
			}
			this.goodsDao.flush();
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public DataGrid<Map<String, Object>> npquery(String projectCode, PageRequest page) {
		return goodsHospitalSourceDao.npquery(page);
	}
	
	@Override
	@Transactional(readOnly = true)
	public DataGrid<GoodsHospitalSource> npquery(String projectCode, List<String> productWords,
			List<String> dosageWords, List<String> producerWords,
			PageRequest pageable) {
		return goodsHospitalSourceDao.npquery(productWords, dosageWords, producerWords, pageable);
	}
	

	@Override
	@Transactional
	public void saveMapping(String projectCode, GoodsHospitalSource source) {
			// 医院旧数据信息
			GoodsHospitalSource source0 = goodsHospitalSourceDao.getById(source.getId());

			source0.setMinunit(source.getMinunit());
			source0.setModel(source.getModel());
			source0.setProducerName(source.getProducerName());
			source0.setConvertRatio0(source.getConvertRatio0());
			source0.setUnitName(source.getUnitName());
			source0.setYjCode(source.getYjCode());
			if (source.getStatus()!= null && source.getStatus() == 99L) {
				source0.setProductCode(source.getProductCode());
				source0.setConvertRatio(source.getConvertRatio());
				source0.setStatus(99L);
			} else {
				source0.setProductCode(source.getProductCode());
				source0.setConvertRatio(source.getConvertRatio());
				source0.setStatus(5L);
			}
			source0.setAuthorizeNo(source.getAuthorizeNo());
			source0.setStandardCode(source.getStandardCode());
			goodsHospitalSourceDao.update(source0);

	}
	
	/**
	 * 拆解规格
	 * @param model
	 * @return
	 */
	private String[] splitModel(String model) {
		model = model.toLowerCase();
		if (model.indexOf("*") > 0) {
			model = model.split("\\*")[0];
		}
		char[] chars = model.toCharArray();
		String dose = "";
		String unit = "";
		for (char c : chars) {
			if (c == '/' || c == '*' || c == '×' || c == ':'
					 || c == '(') {
				break;
			}
			if (c >= '0' && c <='9' || c =='.') {
				dose += c;
			} else {
				unit += c;
			}
		}
		String newDose = null;
		String newUnit = null;
		try {
			if ("微克".equals(unit)) {
				unit = "μg";
			} else if ("毫克".equals(unit)) {
				unit = "mg";
			} else if ("克".equals(unit)) {
				unit = "g";
			} else if ("毫升".equals(unit)) {
				unit = "ml";
			} else if ("ug".equals(unit)) {
				unit = "μg";
			}
			
			if ("mg".equals(unit)) {
				Double d = Double.valueOf(dose);
				if (d < 1) {
					newDose = new BigDecimal(d).multiply(new BigDecimal(1000)).toString();
					newUnit = "μg";
				} else {
					newDose = new BigDecimal(d)
						.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).toString();
					newUnit = "g";
				}
			} else if ("g".equals(unit)) {
				Double d = Double.valueOf(dose);
				if (d < 1) {
					newDose = new BigDecimal(d).multiply(new BigDecimal(1000)).toString();
					newUnit = "mg";
				} else {
					newDose = new BigDecimal(d)
						.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).toString();
					newUnit = "kg";
				}
			} else if ("μg".equals(unit)) {
				Double d = Double.valueOf(dose);
				if (d > 1) {
					newDose = new BigDecimal(d)
						.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).toString();
					newUnit = "mg";
				}
			}
		} catch (Exception e) {
		}
		if (model.startsWith("(") && model.endsWith(")")) {
			model = model.substring(1, model.length() -2);
		}
		List<String> list = new ArrayList<String>();
		list.add(model);
		if (newDose != null && newDose != null ) {
			list.add(Double.valueOf(newDose)+ newUnit);
		}
		if (!StringUtils.isEmpty(dose) && !StringUtils.isEmpty(unit) ) {
			list.add(dose + unit);
		}
		return list.toArray(new String[1]);
	}
	
	/**
	 * 拆分不出来
	 * @param model
	 * @return
	 */
	private String splitConvertRatio(String model) {
		if (model.startsWith("(") && model.endsWith("")) {
			return "1";
		}
		if (model.indexOf("揿") > 0 || model.indexOf("喷") > 0 ) {
			return "1";
		}
		if (model.indexOf("*")>0) {
			String[] m = model.split("\\*");
			if (m.length !=2) {
				return "1";
			}
			model =m[1];
		} else if (model.indexOf("×") >0) {
			String[] m = model.split("\\*");
			if (m.length !=2) {
				return "1";
			}
			model = m[1];
		} else {
			return "1";
		}
		char[] chars = model.toCharArray();
		String convertRatio = "";
		for (char c : chars) {
			if (c >= '0' && c <='9' || c =='.') {
				convertRatio += c;
			} else {
				break;
			}
		}
		if ("".equals(convertRatio)) {
			return "1";
		}
		return convertRatio;
	}
	@Override
	@Transactional
    public int syncToGoods(String projectCode, int maxsize, Long hospitalId, Long status) {
		//产品资源
		List<GoodsHospitalSource> list = goodsHospitalSourceDao.queryNotinGoods(maxsize, hospitalId, status);
		if (list.size() == 0) {
			return 0;
		}
		for (GoodsHospitalSource goodsSource : list) {
			Product	product = productDao.getByCode(goodsSource.getProductCode());
			
			Goods goods = new Goods();
			goods.setStockUpLimit(0);
			goods.setStockDownLimit(0);
			goods.setStockNum(new BigDecimal(0));
			goods.setStockSum(new BigDecimal(0));
			goods.setIsDisabled(0);
			goods.setHospitalCode(goodsSource.getHospitalCode());
			goods.setProductCode(product.getCode());
			goodsDao.save(goods);
			
		}
		return 1;
    }

	@Override
	@Transactional
	public int updateStatusByIds(String projectCode, String ids, Long status, Long hospitalId) {
		return goodsHospitalSourceDao.updateStatusByIds(ids, status, hospitalId);
	}

	@Override
	@Transactional
	public void saveMapping(String projectCode, Long id, String productCode,
			Double convertRatio) {
			// 医院旧数据信息
			GoodsHospitalSource source = goodsHospitalSourceDao.getById(id);
			source.setProductCode(productCode);
			source.setConvertRatio(new BigDecimal(convertRatio));
			source.setStatus(2L);
			goodsHospitalSourceDao.update(source);
			//国家药品目录
			Product product = productDao.getByCode(productCode);
			if (product == null) {
				return;
			}
			if (product.getAuthorizeNo() == null && product.getImportFileNo() == null) {
				product.setAuthorizeNo(source.getAuthorizeNo());
			}
			if (product.getStandardCode() == null) {
				product.setStandardCode(source.getStandardCode());
			}
			productDao.update(product);
	}
}
