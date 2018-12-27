package com.shyl.msc.b2b.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.shyl.common.util.GridFSDAO;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;

@Component("contractPDF")
public class ContractPDF  {

	@Resource
	private GridFSDAO gridFSDAO;
	
	@Resource
	private IContractDetailService contractDetailService;
	
	@Resource
	private IHospitalService hospitalService;
	
	@Resource
	private IAttributeItemService attributeItemService;
	
	public String makePdf(Contract c, List<ContractDetail> cd, User user) throws Exception {
		Hospital h = hospitalService.findByCode(user.getProjectCode(), c.getHospitalCode());
		if(h == null){
			throw new Exception("合同中医院编码不存在");
		}
		File f = new File("tempPDF.pdf");
		Document document = new Document();  
		PdfWriter pdfwrite = PdfWriter.getInstance(document, new FileOutputStream(f));  
    	document.open();  		
    	
    	document.add(new Paragraph("合同编号："+c.getCode(),setChineseFont()));  
    	document.add(new Paragraph(" "));  
    	Paragraph p = new Paragraph("深圳市公立医院药品集中采购电子交易合同",setChineseFont(20,Font.BOLD));
    	p.setAlignment(1);
    	document.add(p);  
    	p = new Paragraph("（试行）",setChineseFont(16,Font.NORMAL));
    	p.setAlignment(1);
    	document.add(p);  
    	document.add(new Paragraph(" "));  
    	
    	//创建一个2列的表格
    	PdfPTable table = new PdfPTable(2);
    	table.setLockedWidth(true);
    	table.setTotalWidth(528);
    	String postAddr = h.getPostAddr() == null?"":h.getPostAddr();
        table.addCell(myCell4("交收地址："+postAddr));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String today = fmt.format(new Date());
        table.addCell(myCell4("生效时间："+today+" 有效期："+c.getStartValidDate()+" 至 "+c.getEndValidDate()));
        table.completeRow();
        
        table.addCell(myCell4("甲方(医疗机构)："+c.getHospitalName()));
        //table.addCell(myCell("确认时间: "+today));
        table.addCell(myCell(" "));
        table.completeRow();
        
        table.addCell(myCell4("乙方(药品集团采购组织)："+c.getGpoName()));
        //table.addCell(myCell("确认时间: "));
        table.addCell(myCell(" "));
        table.completeRow();
        
        table.addCell(myCell4("丙方(配送企业)："+c.getVendorName()));
        //table.addCell(myCell("确认时间: "));
        table.addCell(myCell(" "));
        table.completeRow();
        document.add(table);
        
        p = new Paragraph("        根据《中华人民共和国药品管理法》、《中华人民共和国合同法》、《深圳市公立医院药品集团采购规定》（试行）及其他有关法律、法规的规定，为确保药品网上交易的顺利进行，明确交易各方的权利和义务，特订立本合同。",setChineseFont());
    	document.add(p);  
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第一条 交易方式",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        甲方、乙方、丙方通过药品交易平台进行药品交易；甲方对丙方线下(即平台外)支付药品款。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第二条 合同标的",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        交易药品的名称、剂型、包装规格、交易价格、生产企业等见本合同附件之采购药品明细表。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第三条 质量标准",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        1、	乙方、丙方向甲方供应的药品必须符合国家的质量标准和有关要求。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、	乙方、丙方供应的全部药品应按国家标准保护措施进行包装，以防止药品在配送过程中损坏或变质，确保药品安全无损运抵交货地点。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        3、	甲方可要求乙方、丙方提供其合法的有效证件及所供药品的生产批件或进口药品注册证（复印件）、质量标准等相关文件。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        4、	甲方可要求乙方、丙方提供与所供药品同批号的药检报告书或进口药品质量检验报告书。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第四条 订货与交货",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        1、	本合同签订后，乙方应按采购量组织采购，保证按本合同约定及时、足量供应药品。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、	甲方通过交易平台发出订单，丙方收到订单后应于4个小时内在交易平台响应。根据一般药品、急救药品的轻重缓急程度，积极安排配送。急救药品4小时内送达，节假日照常配送；一般药品24小时内送到，最长不超过48小时。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        3、	丙方配送药品的品种、剂型、数量等必须严格按照甲方发送的订单执行。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        4、乙方对丙方的配送供货行为负责，并承担连带责任。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第五条 药品验收",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        1、	甲方在接收药品时，应对药品进行验货确认，对不符合合同要求的，甲方有权拒绝接收。丙方应及时更换被拒绝的药品，不得影响甲方的临床用药。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、	各方对药品质量存在争议时，应送甲方所在地药检部门检验。如送检药品存在质量问题，检验费用由乙方和丙方承担，甲方有权据此单方终止该品规药品电子交易合同的履行；如送检药品无质量问题，合同继续履行，检验费用由甲方承担。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        3、	丙方配送的药品如在临床使用过程中出现不良反应，甲方应及时通报乙方和丙方。同时甲方有权单方终止该品规药品电子交易合同的继续履行，退回剩余药品，由此造成的所有损失由乙方和丙方承担。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        4、	为保证药品质量，避免造成药品的浪费，甲方对已购进的药品应妥善储存和管理。如因药品质量问题造成的一切损失由乙方和丙方承担全部责任；如因甲方库存条件不符合药品正常储存，造成的药品质量问题，由甲方承担全部责任。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第六条 交易价款结算",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        1、	交易价格：按本合同附件之合同产品项约定内容中载明的成交价执行，该价格包含成本、运输、包装、伴随服务、税费及其他一切附加费用。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、	本合同履行期间，如遇政策性调价，尚未履行交付的药品，按交易平台更新后的价格执行。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        3、	甲方接收丙方配送的药品后，丙方应随货同行将合法发票送达甲方。甲方应在收到合法发票后按规定通过交易平台进行发票交付确认。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        4、	结算时间：药品送达公立医院指定的交收地点后，公立医院应当按照有关规定验收，并在五个工作日内通过供应平台确认收货；在药品配送企业提交发票后五个工作日内确认发票，并在确认发票之日起三十日内向药品配送企业支付全部药品交易款。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第七条 甲方的违约责任",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        甲方如出现下列行为，应按照《中华人民共和国合同法》的规定承担违约责任:",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        1、甲方违反本合同约定，无正当理由拒绝接收丙方配送的药品；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、甲方无正当理由未按照本合同约定的期限和采购药品明细表完成采购；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        3、甲方未按照本合同约定期限支付交易价款；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        4、对于本合同规定的应向乙方采购的药品，甲方不通过交易平台而进行线下自行采购。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        5、其他违反本合同约定的行为。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第八条 乙方和丙方的违约责任",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        乙方、丙方如出现下列行为，应按照《中华人民共和国合同法》的规定承担违约责任，且乙、丙双方互相为对方承担连带责任:",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        1、违反本合同约定，不及时、不足量或拒绝供货；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、所供药品不符合本合同约定的质量标准；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        3、丙方无正当理由拖延供货，导致甲方用药短缺或断货的，乙方可按照相关规定另行选择其他配送企业。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        4、其他违反本合同约定的行为。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第九条 不可抗力",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        合同当事人因不可抗力而导致合同实施延误或不能履行合同义务，不承担误期赔偿或终止合同的责任。（“不可抗力”系指合同各方无法控制、不可预见的事件，但不包括合同某一方的违约或疏忽。这些事件包括但不限于：战争、严重火灾、洪水、台风、地震及其他双方商定的事件）。在不可抗力事件发生后，合同一方应尽快以书面形式将不可抗力的情况和原因通知其他方。除另行要求外，合同当事人应尽实际可能继续履行合同义务，以及寻求采取合理的方案履行不受不可抗力影响的其他事项。不可抗力事件影响消除后，合同当事人可通过协商在合理的时间内迖成进一步履行合同的协议。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第十条 合同的解除",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        有下列情形之一的，本合同解除；如存在违约情形的，守约方可要求违约方承担相应的违约责任：",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        1、	各方经协商一致并报深圳市卫生行政部门许可；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、	因不可抗力、政府政策调整致使合同履行不能实现合同目的或继续履行本合同已无必要；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        3、	任何一方明确表示或者以自己的行为表明不履行本合同主要义务；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        4、	任何一方迟延履行本合同主要义务，经他方催告后在合理期限内仍未履行；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        5、	任何一方有其他严重违约行为致使不能实现本合同目的；",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        6、	法律规定的其他情形",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第十一条 争议的解决",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        因履行本合同发生争议，由争议各方协商解决，协商不成的，按本条第项约定的方式解决：",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        1、	提交深圳仲裁委员会仲裁。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、	依法向有管辖权的人民法院起诉。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	p = new Paragraph("第十二条 其他",setChineseFont(12,Font.BOLD));
    	document.add(p); 
    	p = new Paragraph("        1、	各方通过交易平台确认的订单为本合同的组成部分。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        2、	本合同未尽事项，各方可在不违背法律、法规、交易规则且不与本合同内容相冲突的前提下，进行其他约定或另行签订补充协议。经各方签字盖章的补充协议与本合同具有同等法律效力。",setChineseFont());
    	document.add(p); 
    	p = new Paragraph("        3、	本合同于集团采购组织“交易平台”签署，自甲方通过交易平台创建发送合同，且乙方、丙方均通过交易平台确认同意之日起成立并生效。",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	document.newPage();
    	p = new Paragraph("        附件: 合同产品项约定内容 ",setChineseFont());
    	document.add(p); 
    	document.add(new Paragraph(" "));  
    	
    	//创建一个四列的表格
    	table = new PdfPTable(15);
    	table.setLockedWidth(true);
    	table.setTotalWidth(550); 
        table.addCell(myCell3("序号"));
        table.addCell(myCell3("产品编码"));
        table.addCell(myCell3("产品名称"));
        table.addCell(myCell3("商品名"));
        table.addCell(myCell3("剂型"));
        table.addCell(myCell3("规格"));
        table.addCell(myCell3("包装规格"));
        table.addCell(myCell3("规格属性"));
        table.addCell(myCell3("包装材质"));
        table.addCell(myCell3("生产企业"));
        table.addCell(myCell3("成交价"));
        table.addCell(myCell3("采购数量"));
        table.addCell(myCell3("合计金额"));
        table.addCell(myCell3("采购期限"));
        table.addCell(myCell3("商品备注"));
        table.completeRow();
        DecimalFormat myformat = new DecimalFormat(); 
        myformat.applyPattern("##,##0.00"); 
        
    	for(int i=0;i<cd.size();i++){
    		ContractDetail d = (ContractDetail)cd.get(i);
    		Product pt = d.getProduct();
    		table.addCell(new PdfPCell(new Phrase(i+1+"",setChineseFont())));
            table.addCell(myCell3(pt.getCode()));
            table.addCell(myCell3(pt.getName()));
            table.addCell(myCell3(pt.getGenericName()));
            table.addCell(myCell3(pt.getDosageFormName()));
            table.addCell(myCell3(pt.getModel()));
            table.addCell(myCell3(pt.getPackDesc()));
            table.addCell(myCell3(pt.getModelCode()));
            if(pt.getPackageMaterial() != null){
            	AttributeItem attributeItem = attributeItemService.getById(user.getProjectCode(), Long.parseLong(pt.getPackageMaterial()));
            	if(attributeItem!=null)
            		table.addCell(myCell3(attributeItem.getField2()));
            }else{
            	 table.addCell(myCell3(""));
            }
            table.addCell(myCell3(pt.getProducerName()));
            table.addCell(myCell3(myformat.format(d.getPrice())));
            table.addCell(myCell3(d.getContractNum()+""));
            table.addCell(myCell3(myformat.format(d.getContractAmt())));
            table.addCell(myCell3(c.getEndValidDate()+""));
            table.addCell(myCell3(""));
            table.completeRow();
    	}
        //将表格添加到新的文档
        document.add(table);
        
        document.newPage();
        //创建一个四列的表格
      	table = new PdfPTable(4);
      	table.setLockedWidth(true);
      	table.setTotalWidth(528);
      	table.addCell(myCell2("操作人"));
      	table.addCell(myCell2("操作类型"));
      	table.addCell(myCell2("备注"));
      	table.addCell(myCell2("签章"));
      	table.completeRow();
      	table.addCell(myCell1(user.getOrganization().getOrgName()));
      	table.addCell(myCell1("买方签名确认"));
      	table.addCell(myCell1(" "));
      	//医院签章
      	table.addCell(myCell1(" "));
      	
      	table.completeRow();
      	
      	table.addCell(myCell1(c.getGpoName()));
  		table.addCell(myCell1("签名同意合同"));
  		table.addCell(myCell1(" "));
  		table.addCell(myCell1(" "));
  		table.completeRow();
  		
  		table.addCell(myCell1(c.getVendorName()));
  		table.addCell(myCell1("签名同意合同"));
  		table.addCell(myCell1(" "));
  		table.addCell(myCell1(" "));
  		table.completeRow();
        //将表格添加到新的文档
        document.add(table);
        document.add(new Paragraph(" "));  
        p = new Paragraph("合同签订时间："+today ,setChineseFont());
    	p.setAlignment(2);
    	document.add(p);  
    	
//        PdfContentByte under = pdfwrite.getDirectContentUnder();
//
//        //添加水印
//                under.beginText();  
//                under.setColorFill(BaseColor.LIGHT_GRAY);  
//                under.setFontAndSize(null, 100);
//                under.setTextMatrix(70, 0);  
//                under.showText("hello hello hello hello ");   
////                int rise = 200;
////                for (int k = 0; k <waterMarkName.length(); k++) {  
////	                under.setTextRise(rise);  
////	                char c = waterMarkName.charAt(k);  
////	                under.showText(c + " ");  
////	                rise += 100;  
////                }  
//                         
//                under.endText();
        
        System.out.println("total="+ pdfwrite.getPageSize());
        
        c.setPageNum(pdfwrite.getPageNumber());
        c.setGpoX(new BigDecimal("472"));
        c.setGpoY(new BigDecimal("600"));
        c.setHospitalX(new BigDecimal("472"));
        c.setHospitalY(new BigDecimal("700"));
        c.setVendorX(new BigDecimal("472"));
        c.setVendorY(new BigDecimal("500"));
    	document.close();  
        System.out.println("生成HelloPdf成功！");
        //String fileId ="";
        String fileId = gridFSDAO.saveFile(f, "contract_"+c.getCode()+".pdf", "contract");
        f.delete();
        return fileId;
	}
	
	private PdfPCell myCell1(String s) {
		PdfPCell c = new PdfPCell(new Phrase(s,setChineseFont()));
		
		c.setMinimumHeight(100);
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		c.setVerticalAlignment(Element.ALIGN_MIDDLE);
		
		return c;
	}
	private PdfPCell myCell2(String s) {
		PdfPCell c = new PdfPCell(new Phrase(s,setChineseFont()));
		c.setNoWrap(true);
		c.setMinimumHeight(20);
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		c.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return c;
	}

	private PdfPCell myCell3(String s) {
		PdfPCell c = new PdfPCell(new Phrase(s,setChineseFont(8,Font.NORMAL)));
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		c.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return c;
	}

	private PdfPCell myCell(String s) {
		PdfPCell c = new PdfPCell(new Phrase(s,setChineseFont(10,Font.NORMAL)));
		c.setBorder(0);
		c.setNoWrap(true);
		c.setMinimumHeight(30);
		c.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return c;
	}

	private PdfPCell myCell4(String s) {
		PdfPCell c = new PdfPCell(new Phrase(s,setChineseFont(12,Font.BOLD)));
		c.setBorder(0);
		c.setNoWrap(true);
		c.setMinimumHeight(30);
		c.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return c;
	}

	// 产生PDF字体
    public Font setChineseFont(float size, int bold) {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, size, bold);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;
    }
    
    public Font setChineseFont() {
    	return setChineseFont(12,Font.NORMAL);
    }
	

}
