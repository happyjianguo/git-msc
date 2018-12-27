package com.shyl.msc.b2b.plan.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.plan.dao.IContractDao;
import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.dm.entity.ProductVendor;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductDetailService;
import com.shyl.msc.dm.service.IProductPriceService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.dm.service.IProductVendorService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.enmu.TradeType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.Msg;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IMsgService;
import com.shyl.sys.service.IOrganizationService;

@Service
@Transactional(readOnly=true)
public class ContractService extends BaseService<Contract, Long> implements IContractService {
	public Logger log = LoggerFactory.getLogger(IContractService.class);

	private IContractDao contractDao;

	public IContractDao getContractDao() {
		return contractDao;
	}

	@Resource
	public void setContractDao(IContractDao contractDao) {
		this.contractDao = contractDao;
		super.setBaseDao(contractDao);
	}
	
	@Resource
	private IProductVendorService productVendorService;
	@Resource
	private IContractDetailDao contractDetailDao;
	@Resource
	private IProductPriceService productPriceService;
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IGoodsPriceService goodsPriceService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IOrganizationService organizationService;
	@Resource
	private IMsgService msgService;
	@Resource
	private ISnService snService;
	@Resource
	private IProductDetailService productDetailService;
	@Resource
	private IProductService productService;

	@Override
	public List<Contract> listByGPO(String projectCode, String gpoCode, boolean isGPO, Status status, String cxkssj, String cxjssj) {
		return contractDao.listByGPO(gpoCode, isGPO, status, cxkssj, cxjssj);
	}

	@Override
	public Contract findByCode(String projectCode, String code) {
		return contractDao.findByCode(code);
	}

	private void createPriceD(String projectCode, Contract contract, ContractDetail contractDetail) {
		String hospitalCode = contract.getHospitalCode();
		String vendorCode = contract.getVendorCode();
		String productCode = contractDetail.getProduct().getCode();
		
		//1、新增productVendor
		ProductVendor productVendor = productVendorService.findByKey(projectCode, productCode, vendorCode);
		if(productVendor != null){//有 isDisabled = 1的资料
			productVendor.setIsDisabled(0);
			productVendorService.update(projectCode, productVendor);
		}else{
			productVendor = new ProductVendor();
			productVendor.setProductCode(productCode);
			productVendor.setVendorCode(vendorCode);
			productVendor.setIsDisabled(0);
			productVendor.setModifyDate(new Date());
			productVendorService.save(projectCode, productVendor);
		}
		//2、新增productPrice
		//查询唯一一笔生效的数据,将他过期
		ProductPrice oldpp = productPriceService.findByKey(projectCode, productCode,vendorCode,hospitalCode,TradeType.hospital);
		if(oldpp != null){
			oldpp.setIsEffected(2);
			productPriceService.update(projectCode, oldpp);
		}
		ProductPrice productPrice = new ProductPrice();
		productPrice.setTradeType(TradeType.hospital);
		productPrice.setIsDisabled(0);
		productPrice.setVendorCode(vendorCode);
		productPrice.setVendorName(contract.getVendorName());
		productPrice.setHospitalCode(hospitalCode);
		productPrice.setHospitalName(contract.getHospitalName());
		productPrice.setProductCode(productCode);
		productPrice.setProductName(contractDetail.getProduct().getName());
		productPrice.setModifyDate(new Date());
		productPrice.setBiddingPrice(contractDetail.getPrice());
		productPrice.setFinalPrice(contractDetail.getPrice());
		productPrice.setBeginDate(contract.getStartValidDate());
		productPrice.setOutDate(contract.getEndValidDate());
		productPrice.setEffectType(1);//价格生效
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");   
		productPrice.setEffectDate(sdf.format(contract.getEffectiveDate()));
		
		productPrice = productPriceService.save(projectCode, productPrice);
		
		//3、新增 goods
		Goods goods = goodsService.getByProductAndHospital(projectCode, productCode, hospitalCode,1);
		if(goods == null){
			goods = new Goods();
			goods.setProduct(contractDetail.getProduct());
			goods.setProductCode(productCode);
			goods.setHospitalCode(hospitalCode);						
			goods.setIsDisabled(0);
			goods.setModifyDate(new Date());
			goods = goodsService.save(projectCode, goods);
		}else{
			goods.setIsDisabled(0);
			goods.setModifyDate(new Date());
			goods = goodsService.update(projectCode, goods);
		}
		
		//4、新增goodsPrice
		GoodsPrice goodsPrice = goodsPriceService.findByKey(projectCode, productCode,vendorCode, hospitalCode, null, null,1);
		if(goodsPrice == null){
			goodsPrice = new GoodsPrice();
			goodsPrice.setGoodsId(goods.getId());
			goodsPrice.setProductCode(productCode);
			goodsPrice.setHospitalCode(hospitalCode);
			goodsPrice.setVendorCode(vendorCode);
			goodsPrice.setBiddingPrice(productPrice.getBiddingPrice());
			goodsPrice.setFinalPrice(productPrice.getFinalPrice());
			goodsPrice.setEffectDate(productPrice.getEffectDate());
			goodsPrice.setBeginDate(productPrice.getBeginDate());
			goodsPrice.setOutDate(productPrice.getOutDate());
			goodsPrice.setIsDisabled(0);
			goodsPrice.setIsDisabledByH(0);
			goodsPrice.setPriceType(1);
			goods.setModifyDate(new Date());
			goodsPriceService.save(projectCode, goodsPrice);
		}else{
			goodsPrice.setBiddingPrice(productPrice.getBiddingPrice());
			goodsPrice.setFinalPrice(productPrice.getFinalPrice());
			goodsPrice.setEffectDate(productPrice.getEffectDate());
			goodsPrice.setBeginDate(productPrice.getBeginDate());
			goodsPrice.setOutDate(productPrice.getOutDate());
			goodsPrice.setIsDisabledByH(0);
			goodsPrice.setIsDisabled(0);
			goodsPrice.setPriceType(1);
			goods.setModifyDate(new Date());
			goodsPriceService.update(projectCode, goodsPrice);
		}
		
	}

	@Override
	@Transactional
	public void hospitalSigned(String projectCode, Long id,User user) {
		try {
			Contract c = this.getById(projectCode, id);
			c.setStatus(Status.hospitalSigned);
			//生成pdf合同
			c.setHospitalSealPath(makePdf(c,user));
			this.update(projectCode, c);
			//发送消息
			sendMsg(user.getProjectCode(), c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String makePdf(Contract c, User user) throws Exception {
		File f = new File("tempPDF.pdf");
		Document document = new Document();  
    	PdfWriter.getInstance(document, new FileOutputStream(f));  
    	document.open();  		
    	
    	//document.add(new Paragraph(" "));  
    	document.add(new Paragraph("合同编号: "+c.getCode(),setChineseFont()));  
    	Paragraph p = new Paragraph("医院报量单",setChineseFont(20));
    	p.setAlignment(1);
    	document.add(p);  
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("生效时间:"+c.getEffectiveDate()+" 有效期："+c.getStartValidDate()+" 至 "+c.getEndValidDate(),setChineseFont());
    	p.setAlignment(2);
    	document.add(p);  
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("甲方(医疗机构)："+c.getHospitalName()+"          确认时间: "+c.getEndValidDate(),setChineseFont());
    	document.add(p);  
    	p = new Paragraph("乙方(集团采购组织）：                  确认时间: ",setChineseFont());
    	document.add(p);  
    	p = new Paragraph("丙方(配送企业）：             确认时间: ",setChineseFont());
    	document.add(p);  
    	document.add(new Paragraph(" "));  
    	
    	p = new Paragraph("    鉴于：1、甲方为深圳市公立医院，乙方为依据《深圳市推行公立医院药品集团采购改革试点实施方案》（深卫计发〔2016〕63号）、《深圳市公立医院药品集团采购目录管理办法（试行）》、《深圳市公立医院药品集团采购组织管理办法（试行）》与《深圳市公立医院药品集团采购规定（试行）》（深卫计规[2016]4号）等有关文件（以下简称“有关文件”）遴选确定的药品集团采购组织，丙方为经公开遴选确定的药品配送企业。",setChineseFont());
    	document.add(p);  
    	p = new Paragraph("    2、甲乙双方已签订编号为   的《药品委托采购供应协议》，乙丙双方已签订编号为   的《委托配送协议》；",setChineseFont());
    	document.add(p);  
    	p = new Paragraph("    3、乙方已根据有关文件的要求建设并负责“全药网”药品供应平台（以下简称“药品供应平台”，其网址为： http://b2b.quanyaowang.com）的运营。",setChineseFont());
    	document.add(p);  
    	p = new Paragraph("    根据《中华人民共和国药品管理法》、《中华人民共和国合同法》、有关文件及其他有关法律、法规的规定，为确保深圳市公立医院药品集团采购的顺利进行，明确交易各方的权利和义务，特订立本合同。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第一条 交易方式",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    甲方、乙方、丙方通过药品供应平台进行药品交易。其中，甲方通过药品供应平台确认各品规药品本年度预期采购量并根据用药需要在药品供应平台下达订单，乙方通过与药品生产经营企业集中谈判、议价及采购所需药品，丙方受托负责相关药品的仓储及配送工作。各方根据合同约定分别进行款项结算及支付。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第二条 合同标的",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    交易药品的名称、剂型、包装规格、交易价格、生产企业等见本合同附件之采购药品明细表。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第三条 质量标准",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、	乙方、丙方向甲方供应的药品必须符合国家的质量标准和有关要求，丙方仓储药品的条件标准由乙丙双方另行约定。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、	乙方、丙方供应的全部药品应按国家标准保护措施进行包装，以防止药品在配送过程中损坏或变质，确保药品安全无损运抵本合同首部载明的交货地点。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    3、	甲方可要求乙方、丙方提供其合法的有效证件及所供药品的生产批件或进口药品注册证（复印件）、质量标准等相关文件。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    4、	甲方可要求乙方、丙方提供与所供药品同批号的药检报告书或进口药品质量检验报告书。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第四条 订货与交货",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、	本合同签订后，乙方应按甲方要求及时组织采购，丙方应保有合理库存，以保证按本合同约定及时、足量供应药品。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、	甲方通过药品供应平台发出订单，丙方收到订单后应于4个小时内在药品供应平台响应。根据一般药品、急救药品的轻重缓急程度，积极安排配送。急救药品4小时内送达，节假日照常配送；一般药品24小时内送到，最长不得超过48小时。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    3、	丙方配送药品的品种、剂型、数量等必须严格按照甲方发送的订单执行。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第五条 药品验收",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、	甲方在接收药品时，应根据具体品类规定的标准（含质量标准及配送环境等）及时对药品进行验货并在五个工作日内通过药品供应平台进行确认。对于不符合合同、订单质量要求的药品，甲方有权拒绝接收。丙方应及时更换被拒收的药品，不得影响甲方的临床用药。若同笔订单中仅有部分药品存在质量问题的，对于订单中质量符合要求的药品，甲方应予接收。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、	验收时各方对药品质量存在争议的，应将争议药品送甲方所在地药检部门检验。如送检药品经检验存在质量问题，检验费用由乙方和丙方承担，甲方有权依据检验结果单方终止该品规药品电子交易合同的履行，并建议乙方对配送企业及配送药品种类进行调整；如送检药品无质量问题，合同继续履行，检验费用由甲方承担。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    3、	丙方配送的药品如在临床使用过程中出现不良反应时，甲方应及时通报乙方和丙方。同时甲方有权单方终止该品规药品电子交易合同的继续履行，并依照本条第2款约定将该药品送药检部门进行检验。若经检验确属药品自身质量问题的，甲方有权退回剩余药品，由此造成的所有损失由乙方和丙方承担，但该药品质量问题系由甲方原因导致的除外。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    4、	为保证药品质量，避免造成药品的浪费，甲方对已购进的药品应妥善储存和管理。如因甲方库存条件不符合药品正常储存条件要求或因甲方自身其他原因，导致丙方交付的药品在有效期内产生质量问题的，由此产生的责任由甲方承担。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第六条 交易价款结算",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、	交易价格：按药品供应平台公布的交易价格执行，该价格已包含成本、运输、包装、伴随服务、税费及其他一切附加费用。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、	本合同履行期间，如因采购价格发生变化（包括但不限于政策性调价等），对于尚未交付的药品，按药品供应平台更新后的价格执行。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    3、	甲方接收丙方配送的药品后，丙方应于次月5日前将发票送达甲方。甲方应在收到发票后五个工作日内，通过药品供应平台进行发票交付确认。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    4、	结算时间：甲方应在确认发票之日起30日内向丙方支付全部药品交易款，具体收款账户及方式由丙方另行书面指定。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第七条 甲方的违约责任",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    甲方如出现下列行为，应按照《中华人民共和国合同法》的规定承担违约责任:",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、甲方违反本合同约定，无正当理由拒绝接收丙方配送的药品；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、甲方无正当理由未按照本合同约定的期限和采购药品明细表完成采购；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    3、甲方未按照本合同约定期限支付交易价款；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    4、对于本合同及有关文件规定的应通过药品供应平台采购的药品，甲方在药品供应平台线下自行采购；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    5、甲方其他违反本合同约定的行为。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第八条 乙方和丙方的违约责任",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    乙方、丙方如出现下列行为，应按照《中华人民共和国合同法》的规定承担违约责任，且乙丙双方互为连带责任:",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、违反本合同约定，不及时、不足量或拒绝供货；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、所供药品不符合本合同约定的质量标准；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    3、丙方无正当理由拒绝配送或拖延配送药品，导致甲方用药短缺或断货的，乙方可按照前述有关文件规定及《委托配送协议》约定取消丙方配送企业资格并另行确定其他配送企业。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    4、乙方、丙方其他违反本合同约定的行为。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第九条 不可抗力",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    合同当事人因不可抗力而导致合同实施延误或不能履行合同义务，不承担误期赔偿或终止合同的责任。（“不可抗力”系指合同各方人力无法控制、不可预见的事件，但不包括合同某一方的违约或疏忽。这些事件包括但不限于：战争，火灾、洪水、台风、地震等严重自然灾害或事故及其他双方商定的事件）。在不可抗力事件发生后，合同一方应尽快以书面形式将不可抗力的情况和原因通知其他方。除另行要求外，合同当事人应尽实际可能继续履行合同义务，以及寻求采取合理的方案履行不受不可抗力影响的其他事项。不可抗力事件影响消除后，合同当事人可通过协商在合理的时间内迖成进一步履行合同的协议。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第十条 合同的解除",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    有下列情形之一的，本合同解除：",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、	各方经协商一致并报深圳市卫生行政部门许可；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、	因不可抗力、政府政策调整致使合同履行不能实现合同目的或继续履行本合同已无必要；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    3、	任何一方明确表示或者以自己的行为表明不履行本合同主要义务；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    4、	任何一方迟延履行本合同主要义务，经他方催告后在合理期限内仍未履行；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    5、	任何一方有其他严重违约行为致使不能实现本合同目的；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    6、	法律规定的其他情形。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第十一条 争议的解决",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、	本合同的订立、解释、履行、解除等均适用中华人民共和国法律。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、	因履行本合同发生争议，由争议各方协商解决，协商不成的，任何一方均有权依法向本合同签订地有管辖权的人民法院起诉。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    第十二条 其他",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    1、	各方通过药品供应平台确认的订单为本合同的组成部分。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    2、	本合同未尽事项，各方可经协商后另行签订补充协议。经各方签字盖章的补充协议与本合同具有同等法律效力。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    3、	本合同自甲方通过药品供应平台创建发送合同，且乙方、丙方均通过药品供应平台确认同意合同之日起成立。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    4、与本合同有关的一切通知均应以书面方式送达其他各方。任何一方联系方式发生变更的，应及时通知其他各方，怠于通知的，应承担由此产生的责任。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    5、本合同签订地为深圳市南山区。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    附件: 合同产品项约定内容 ",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("    ",setChineseFont());
    	document.add(p); 
    	
    	List<ContractDetail> cd = contractDetailDao.findByPID(c.getId());
    	
    	//创建一个四列的表格
    	PdfPTable table = new PdfPTable(15);
        
        table.addCell(new PdfPCell(new Phrase("序号",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("产品 code",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("产品名称",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("商品名",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("剂型",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("规格",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("包装规格",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("规格属性",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("包装材质",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("生产企业",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("成交价",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("采购数量",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("合计金额",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("采购期限",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("商品备注",setChineseFont())));
        
        table.completeRow();
    	for(int i=0;i<cd.size();i++){
    		ContractDetail d = (ContractDetail)cd.get(i);
    		Product pt = d.getProduct();
    		table.addCell(new PdfPCell(new Phrase(i+1+"",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getCode(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getName(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getGenericName(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getDosageFormName(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getModel(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getPackDesc(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getModelCode(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getPackageMaterial(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(pt.getProducerName(),setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(d.getPrice()+"",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(d.getContractNum()+"",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(d.getContractAmt()+"",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(c.getEndValidDate()+"",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase("",setChineseFont())));
            table.completeRow();
    	}
        //将表格添加到新的文档
        document.add(table);
        
      //创建一个四列的表格
    	table = new PdfPTable(5);
        
        table.addCell(new PdfPCell(new Phrase("操作人",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("操作时间",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("操作类型",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("备注",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("签章",setChineseFont())));
        table.completeRow();
        table.addCell(new PdfPCell(new Phrase(user.getName(),setChineseFont())));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        table.addCell(new PdfPCell(new Phrase(format.format(new Date()),setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("医院签订",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("",setChineseFont())));
        table.addCell(new PdfPCell(new Phrase("",setChineseFont())));
        table.completeRow();
    	for(int i=0;i<2;i++){
    		table.addCell(new PdfPCell(new Phrase(" ",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(" ",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(" ",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(" ",setChineseFont())));
            table.addCell(new PdfPCell(new Phrase(" ",setChineseFont())));
            table.completeRow();
    	}
        //将表格添加到新的文档
        document.add(table);
    	
    	
    	document.close();  
        System.out.println("生成HelloPdf成功！");
        String fileId ="";
        //String fileId = gridFSDAO.saveFile(f, "contract_"+c.getCode()+".pdf", "contract");
        return fileId;
	}
	
	// 产生PDF字体
    public Font setChineseFont(float size) {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, size, Font.NORMAL);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;
    }
    
    public Font setChineseFont() {
    	return setChineseFont(12);
    }
	
	private void sendMsg(String projectCode, Contract c) {
		Msg msg = new Msg();
		msg.setIds("00");
		msg.setCaty("0");
		msg.setTitle("有新合同，请签订。-- "+c.getCode());
		
		msg.setAttach("/vendor/contract.htmlx?code="+c.getCode());
		Hospital hospital = hospitalService.findByCode(projectCode, c.getHospitalCode());
		Company company = companyService.findByCode(projectCode,c.getVendorCode(), "isVendor=1");
		//查询医院的orgId
		Organization oh = organizationService.getByOrgId(projectCode, hospital.getId(), 1);
		if(oh != null){
			msg.setOrganizationId(oh.getId());
			msg.setOrgName(oh.getOrgName());
		}
		//查询供应商的orgId
		Organization og = organizationService.getByOrgId(projectCode, company.getId(), 2);
		
		Long ogId = Long.parseLong("-1");
		if(og != null){
			ogId = og.getId();
		}
		try {
			msgService.sendBySYSToOrg(projectCode, msg, ogId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Contract getByKey(String projectCode, String hospitalCode, String gpoCode, String vendorCode) {
		return contractDao.getByKey(hospitalCode, gpoCode, vendorCode);
	}

	@Override
	public List<Contract> listByHospital(String projectCode, String hospitalCode, Status status) {
		return contractDao.listByHospital(hospitalCode, status);
	}

	@Override
	@Transactional
	public String importExcel(String projectCode, String[][] upExcel, User user) throws MyException{
		String hospitalCode = user.getOrganization().getOrgCode();

		List<ContractDetail> saveDetails = new ArrayList<>();
		List<ContractDetail> updateDetails = new ArrayList<>();
		Map<Long,Integer> indexMap = new HashMap<>();
		Map<String,Company> companyMap = new HashMap<>();
		Map<String, Contract> contractMap = new HashMap<>();
		for (int i = 0; i < upExcel.length; i++) {
			String[] row = upExcel[i];
			String productCode = row[12];
			String vendorCode = row[13];
			BigDecimal num = new BigDecimal(0);
			try {
				if(row[7].trim().equals(""))
					continue;
				num = new BigDecimal(row[7]);
			} catch (Exception e) {
				continue;
			}
			if(num.compareTo(new BigDecimal(0)) <= 0){
				continue;
			}
			
			Company vendor = companyMap.get(vendorCode);
			if (vendor == null) {
				vendor = companyService.findByCode(projectCode,vendorCode, "isVendor=1");
			}
			if (vendor == null){
				throw new MyException("第"+i+"笔数据异常，供应商编码有误");
			}
			ProductDetail productDetail = productDetailService.getByKey3(projectCode, productCode,vendorCode,hospitalCode);
			if(productDetail == null || productDetail.getPrice() == null
					|| productDetail.getPrice().doubleValue() == 0.00){
				throw new MyException("药品（"+productCode+"）未设置价格");
			}
			Product product = productDetail.getProduct();
			if (product.getIsGPOPurchase() == null || product.getIsGPOPurchase()!=1) {
				throw new MyException("药品（"+productDetail.getProduct().getCode()+"）已经不是GPO药品");
			}
			String gpoCode = product.getGpoCode();
			ContractDetail contractDetail = contractDetailDao.getByKey(product.getId(), hospitalCode, gpoCode, vendorCode);
			if(contractDetail == null) {
				Contract contract = contractMap.get(hospitalCode +"_"+gpoCode+"_"+vendorCode);
				if (contract == null) {
					contract = contractDao.getByKey(hospitalCode, gpoCode, vendorCode);
					if(contract == null){
						Hospital hospital = hospitalService.findByCode(projectCode, hospitalCode);

						contract = new Contract();
						contract.setCode(snService.getCode(projectCode, OrderType.contract));
						contract.setHospitalCode(hospitalCode);
						contract.setHospitalName(hospital.getFullName());
						contract.setVendorCode(vendorCode);
						contract.setVendorName(vendor.getFullName());
						contract.setGpoCode(gpoCode);
						contract.setGpoName(product.getGpoName());
						contract.setStatus(Contract.Status.noConfirm);
						contract.setModifyDate(new Date());
						contract = contractDao.save(contract);
						contractMap.put(hospitalCode +"_"+gpoCode+"_"+vendorCode,contract);
					}
				}
				contractDetail = new ContractDetail();
				Integer index = indexMap.get(contract.getId());
				if (index == null) {
					index = contractDetailDao.countByPID(contract.getId()).intValue();
				}
				indexMap.put(contract.getId(),++index);
				String detail_code = contract.getCode()+"-"+NumberUtil.addZeroForNum(index+"", 4);
				contractDetail.setCode(detail_code);
				contractDetail.setContract(contract);
				contractDetail.setProduct(product);
				contractDetail.setPrice(productDetail.getPrice());
				contractDetail.setContractNum(num);
				contractDetail.setContractAmt(contractDetail.getContractNum().multiply(contractDetail.getPrice()));
				contractDetail.setCartNum(new BigDecimal(0));
				contractDetail.setCartAmt(new BigDecimal(0));
				contractDetail.setPurchasePlanNum(new BigDecimal(0));
				contractDetail.setPurchasePlanAmt(new BigDecimal(0));
				contractDetail.setPurchaseNum(new BigDecimal(0));
				contractDetail.setPurchaseAmt(new BigDecimal(0));
				contractDetail.setDeliveryNum(new BigDecimal(0));
				contractDetail.setDeliveryAmt(new BigDecimal(0));
				contractDetail.setReturnsNum(new BigDecimal(0));
				contractDetail.setReturnsAmt(new BigDecimal(0));
				contractDetail.setModifyDate(new Date());
				contractDetail.setStatus(com.shyl.msc.b2b.plan.entity.ContractDetail.Status.uneffect);
				contractDetailDao.save(contractDetail);
			} else {
				contractDetail.setContractNum(num);
				contractDetail.setContractAmt(productDetail.getPrice().multiply(num));
				contractDetailDao.update(contractDetail);
			}
		}
		return null;
	}

	@Override
	public JSONArray getToGPO(String projectCode, Company gpo, boolean isGPO) {
		return null;
	}

	@Override
	@Transactional
	public void saveContractBatch(String projectCode, String hospitalCode, String data) {
		List<JSONObject> list = JSON.parseArray(data, JSONObject.class);
		for(JSONObject jo:list){
			if(jo == null){
				continue;
			}
			BigDecimal number = jo.getBigDecimal("number");
			Long productId = jo.getLong("productId");
			String gpoCode = jo.getString("gpoCode");
			String vendorCode = jo.getString("vendorCode");
			Product product = productService.getById(projectCode, productId);
			this.saveContract(projectCode, hospitalCode, product, gpoCode, vendorCode, number);
		}
	}

	@Override
	@Transactional
	public void saveContract(String projectCode, String hospitalCode, Product product, String gpoCode, String vendorCode, BigDecimal num) throws MyException {
		ContractDetail contractDetail = contractDetailDao.getByKey(product.getId(), hospitalCode, gpoCode, vendorCode);
		ProductDetail productDetail = productDetailService.getByKey(projectCode, product.getId(),vendorCode,hospitalCode);
		if(productDetail == null || productDetail.getPrice() == null
				|| productDetail.getPrice().doubleValue() == 0.00){
			throw new MyException("药品（"+product.getCode()+"）未设置价格");
		}
		if(contractDetail == null){
			Contract contract = this.getByKey(projectCode, hospitalCode, gpoCode, vendorCode);
			if(contract == null){
				Hospital hospital = hospitalService.findByCode(projectCode, hospitalCode);
				Company gpo = companyService.findByCode(projectCode,gpoCode, "isGPO=1");
				if (gpo == null) {
					throw new MyException("药品（"+product.getCode()+"）已经不是GPO药品");
				}
				Company vendor = companyService.findByCode(projectCode,vendorCode, "isVendor=1");
				contract = new Contract();
				contract.setCode(snService.getCode(projectCode, OrderType.contract));
				contract.setHospitalCode(hospitalCode);
				contract.setHospitalName(hospital.getFullName());
				contract.setVendorCode(vendorCode);
				contract.setVendorName(vendor.getFullName());
				contract.setGpoCode(gpoCode);
				contract.setGpoName(gpo.getFullName());
				contract.setStatus(Contract.Status.noConfirm);
				contract.setModifyDate(new Date());
				contract = contractDao.save(contract);
			}
			contractDetail = new ContractDetail();
			List<ContractDetail> dlilst = contractDetailDao.findByPID(contract.getId());
			int codenum = dlilst.size()+1;
			String detail_code = contract.getCode()+"-"+NumberUtil.addZeroForNum(codenum+"", 4);
			contractDetail.setCode(detail_code);
			contractDetail.setContract(contract);
			contractDetail.setProduct(product);
			contractDetail.setPrice(productDetail.getPrice());
			contractDetail.setContractNum(num);
			contractDetail.setContractAmt(contractDetail.getContractNum().multiply(contractDetail.getPrice()));
			contractDetail.setCartNum(new BigDecimal(0));
			contractDetail.setCartAmt(new BigDecimal(0));
			contractDetail.setPurchasePlanNum(new BigDecimal(0));
			contractDetail.setPurchasePlanAmt(new BigDecimal(0));
			contractDetail.setPurchaseNum(new BigDecimal(0));
			contractDetail.setPurchaseAmt(new BigDecimal(0));
			contractDetail.setDeliveryNum(new BigDecimal(0));
			contractDetail.setDeliveryAmt(new BigDecimal(0));
			contractDetail.setReturnsNum(new BigDecimal(0));
			contractDetail.setReturnsAmt(new BigDecimal(0));
			contractDetail.setModifyDate(new Date());
			contractDetail.setStatus(com.shyl.msc.b2b.plan.entity.ContractDetail.Status.uneffect);
			contractDetailDao.save(contractDetail);
		}else{
			//改成修改，而不是累加
			contractDetail.setContractNum(num);
			contractDetail.setContractAmt(productDetail.getPrice().multiply(num));
			contractDetailDao.update(contractDetail);
		}
	}

	@Override
	public DataGrid<Map<String, Object>> reportForHospitalContract(String projectCode, String startDate, String endDate,
			PageRequest pageable) {
		return contractDao.reportForHospitalContract( projectCode,  startDate,  endDate,pageable);
	}

	@Override
	public List<Map<String, Object>> reportForHospitalContract1(String projectCode, String hospitalCodes,
			String startDate, String endDate, PageRequest pageable) {
		return contractDao.reportForHospitalContract1( projectCode, hospitalCodes, startDate,  endDate,pageable);
	}

	@Override
	public List<Map<String, Object>> reportForHospitalContract2(String projectCode, String hospitalCodes,
			String startDate, String endDate, PageRequest pageable) {
		return contractDao.reportForHospitalContract2( projectCode, hospitalCodes, startDate,  endDate,pageable);
	}

	@Override
	public List<Contract> listByIsPass(String projectCode, int isPass){
		return contractDao.listByIsPass(isPass);
	}

	@Override
	public DataGrid<Contract> listByContractAndDetail(String projectCode, PageRequest page) {
		return contractDao.listByContractAndDetail(page);
	}

	@Override
//	@Cacheable(value = "contractService")
	public DataGrid<Contract> pageContract(String projectCode, PageRequest page) {
		return contractDao.query(page);
	}
}
