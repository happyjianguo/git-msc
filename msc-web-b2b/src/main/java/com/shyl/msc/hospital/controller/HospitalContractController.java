package com.shyl.msc.hospital.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shyl.common.util.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.ca.CommonCA;
import com.shyl.common.cache.dao.VerifyCodeDAO;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.GridFSDAO;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.common.ContractPDF;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest.ClosedObject;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractClosedRequestService;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IMsgService;
import com.shyl.sys.service.IOrganizationService;

@Controller
@RequestMapping("/hospital/contract")
public class HospitalContractController extends BaseController {

	@Resource
	private IContractService contractService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private GridFSDAO gridFSDAO;
	@Resource
	private IContractClosedRequestService contractClosedRequestService;
	@Resource
	private ISnService snService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private ContractPDF contractPDF;
	@Resource
	private VerifyCodeDAO verifyCodeDAO;
	@Resource
	private CuratorFramework curatorFramework;
	
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user, String code) {
		model.addAttribute("code", code);
		model.addAttribute("oid", user.getClientCert());
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "ISCAUSED");
		model.addAttribute("ISCAUSED", attributeItem.getField3());
		return "hospital/contract/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Contract> page(PageRequest page, @CurrentUser User user) {
		Map<String, Object> query = page.getQuery();
		String status = (String)query.get("t#status_L_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_L_EQ", Contract.Status.valueOf(status).ordinal());
		} else {
			query.put("t#status_L_NE", Contract.Status.noConfirm.ordinal());
		}
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		Sort sort = new Sort(Direction.DESC,"code");
		page.setSort(sort);
//		return contractService.query(user.getProjectCode(), page);
		return contractService.listByContractAndDetail(user.getProjectCode(), page);
	}
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<ContractDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.ASC,"product.code");
		pageable.setSort(sort);
		DataGrid<ContractDetail> page = contractDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		AttributeItem aiB = attributeItemService.queryByAttrAndItemNo("contract", "B");
		if(aiB.getField3().equals("2")){
			AttributeItem aiE = attributeItemService.queryByAttrAndItemNo("contract", "E");
			if (aiE!= null && aiE.getField3()!=null) {
				model.addAttribute("dateStr",aiE.getField3());
			}
		}
		model.addAttribute("type",aiB.getField3());
		return "hospital/contract/add";
	}
	
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success() {
		return "hospital/contract/success";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(@CurrentUser User user, String deadline,String imgBase64) {
		Message msg = new Message();
		AttributeItem aiA = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "A");
		if(!aiA.getField3().equals("1")){
			AttributeItem aiD = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "D");
			msg.setSuccess(false);
			msg.setMsg("已过合同签订日期("+aiD.getField3()+")");
			return msg;
		}
		try{
			if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
				List<Contract> contracts = contractService.listByHospital(user.getProjectCode(), user.getOrganization().getOrgCode(), Contract.Status.noConfirm);
				JSONArray jArray = new JSONArray();
				for(Contract contract:contracts){
					contract.setStatus(Contract.Status.unsigned);
					AttributeItem aiB = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "B");
					if(aiB.getField3().equals("0")){
						AttributeItem aiC = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "C");
						contract.setStartValidDate(DateUtil.getToday10());
						contract.setEndValidDate(aiC.getField3());
					} else if(aiB.getField3().equals("2")){						
						contract.setStartValidDate(DateUtil.getToday10());
						contract.setEndValidDate(deadline);
					}else{
						contract.setStartValidDate(DateUtil.getToday10());
						contract.setEndValidDate(DateUtil.getToday10(DateUtil.getAddNumDate(Calendar.MONTH, Integer.valueOf(deadline))));
					}
			    	List<ContractDetail> cd = contractDetailService.findByPID(user.getProjectCode(), contract.getId());
			    	if (cd.size() == 0) {
			    		contractService.delete(user.getProjectCode(), contract);
			    		continue;
			    	}
			    	
			    	//累计合同总金额
			    	BigDecimal contractAmt = new BigDecimal("0");
			    	for (int i = 0; i < cd.size(); i++) {
			    		ContractDetail contractDetail = cd.get(i);
						String detailCode = contract.getCode()+"-"+ NumberUtil.addZeroForNum((i+1)+"", 4);
						//重置code
						contractDetail.setCode(detailCode);
			    		contractAmt = contractAmt.add(contractDetail.getContractAmt());
					}
					//批量更新药品编码的code
					contractDetailService.updateBatch(user.getProjectCode(), cd);
			    	contract.setAmt(contractAmt);
					//contract.setHospitalConfirmDate(new Date());
					//contract.setGpoConfirmDate(new Date());
					//contract.setEffectiveDate(new Date());
					//生成pdf合同
					//String pdfpath = makePdf(contract, user);
					//ContractPDF cpdf = new ContractPDF();
					String pdfpath = contractPDF.makePdf(contract, cd, user);
					//contract.setHospitalSealPath(pdfpath);
					contract.setFilePath(pdfpath);
					contractService.update(user.getProjectCode(), contract);
					JSONObject jObject = new JSONObject();
					jObject.put("code", contract.getCode());
					jArray.add(jObject);
					//pdf传输至sgl服务器
					//String wj = gridFSDAO.findFileByIdToString(pdfpath, "contract");
					//System.out.println("wj ===="+wj);
					System.out.println("contract.getCode() ===="+contract.getCode());
					//System.out.println("CommonProperties.SGL_PDFSEND ===="+CommonProperties.SGL_PDFSEND_HTTP);
					
				}
				JSONObject r_jo = new JSONObject();
				r_jo.put("arr", jArray);
				
				msg.setSuccess(true);
				msg.setMsg("新增成功");
				msg.setData(r_jo);
			}else{
				msg.setSuccess(false);
				msg.setMsg("不是医院账号");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/okwzt", method = RequestMethod.POST)
	@ResponseBody
	public Message okwzt(Long id, String UserCert, String UserSignedData, 
			@CurrentUser User user, HttpServletRequest request) {
		Message msg = new Message();
		try{
			Contract c = contractService.getById(user.getProjectCode(), id);
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "ISCAUSED");
			if(attributeItem.getField3().equals("1") && c != null){			
				Object object = verifyCodeDAO.get("pdfSignRandom/"+request.getSession().getId());
				String Random = "";
				if (object != null) {
					Random = (String)object;
				}
				//Random = "1024";
				System.out.println("Random"+Random);
				System.out.println("UserCert"+UserCert);
				System.out.println("UserSignedData"+UserSignedData);
				
				if(StringUtils.isEmpty(UserSignedData)) {
					msg.setMsg("证书信息为空");
					msg.setSuccess(false);
					return msg;
				}
				if(!checkCA(Random,UserCert,UserSignedData,user,request.getServerName())){
					msg.setMsg("证书与当前账号不一致");
					msg.setSuccess(false);
					return msg;
				}
				AttributeItem attributeItem1 = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "CANAME_SIGN");
				String url = CommonCA.addSignWeb(attributeItem1.getField3(), gridFSDAO.findFileByIdToString(c.getFilePath(), "contract"), c.getCode(), request.getServerName());
				if(!url.equals("")){
					msg.setData(url);
					msg.setSuccess(true);
				}else{
					msg.setSuccess(false);
					msg.setMsg("");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg(e.getMessage());
			msg.setSuccess(false);
		}
		return msg;
	}

	@RequestMapping(value = "/checkContract", method = RequestMethod.POST)
	@ResponseBody
	public Message checkContract(@CurrentUser User user,final Long id){
		Message msg = new Message();
		try{
			final Contract c = contractService.getById(user.getProjectCode(), id);
			if(Status.hospitalSigned.equals(c.getStatus())){
				msg.setSuccess(true);
				return msg;
			}
			Stat stat = curatorFramework.checkExists().forPath("/CONTRACT/"+id);
			if(stat == null){
				curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/CONTRACT/"+id);
			}
			curatorFramework.getData().usingWatcher(
					new CuratorWatcher() {
						@Override
						public void process(WatchedEvent event) {
							synchronized (c){
								c.notifyAll();
							}
						}
					}
			).forPath("/CONTRACT/"+id);
			/**
			 * 监听数据节点的变化情况
			 */
			/*final NodeCache nodeCache = new NodeCache(curatorFramework, "/CONTRACT/"+id, false);
			nodeCache.start(true);
			nodeCache.getListenable().addListener(
					new NodeCacheListener() {
						@Override
						public void nodeChanged() throws Exception {
							synchronized (c){
								c.notifyAll();
							}
						}
					}
			);*/
			System.out.println("合同编号为："+c.getCode()+"的线程在等待");
			synchronized (c){
				c.wait();
			}
			System.out.println("合同编号为："+c.getCode()+"的线程被唤醒");
			//清理该节点
			//curatorFramework.delete().forPath("/CONTRACT/"+id);
		}catch (Exception e){
			e.printStackTrace();
			msg.setMsg("签订失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/ok", method = RequestMethod.POST)
	@ResponseBody
	public Message ok(Long id,String UserCert,String UserSignedData,@CurrentUser User user,HttpServletRequest request) {
		Message msg = new Message();
		try{
			Contract c = contractService.getById(user.getProjectCode(), id);
			c.setHospitalConfirmDate(new Date());
			c.setGpoConfirmDate(new Date());
			c.setStatus(Status.hospitalSigned);
			c.setEffectiveDate(new Date());
			c.setHospitalSealPath(c.getFilePath());
			contractService.update(user.getProjectCode(), c);
			msg.setMsg("签订成功");
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("签订失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	private boolean checkCA(String random, String userCert, String userSignedData, User user,String userIP) {
		try{
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "CANAME_SIGN");
			Map<String, String> resultMap = CommonCA.checkSign(attributeItem.getField3(),userCert, random, userSignedData,userIP);
			//校验sign结果
			if ("true".equals(resultMap.get("code"))) {
				String oid = resultMap.get("oid");
				if (user.getClientCert() == null) {
					return false;
				}
				//判断用户维护的唯一标准oid从后面匹配是否和cert中维护的一致
				//oid格式可能为SF000000000000，也可能为1@xxxSF0000000000
				if (user!=null&&!StringUtils.isBlank(oid) && !StringUtils.isBlank(oid) 
						&& !StringUtils.isBlank(user.getClientCert()) && oid.endsWith(user.getClientCert())) {
					return true;
				} else {
					return false;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
		return false;
	}


	/**
	 * 查看附件
	 * @param user
	 * @param response
	 */
	@RequestMapping("/readfile")
	public void exportExcel(Long id,@CurrentUser User user, HttpServletResponse response){
//		response.setContentType("application/pdf");
//		response.setHeader("Content-Disposition", "attachment; filename=aaa.pdfss");
//		response.setHeader("Expires","0");

		Contract c = contractService.getById(user.getProjectCode(), id);
		response.setContentType("application/pdf;charset=UTF-8");
//		response.setHeader("Content-Disposition", "attachment; filename=pdfFilled.pdf");
//		response.setHeader("Expires","0");
		
		gridFSDAO.readFileByIdToOutputStream(c.getFilePath(), "contract", response);
	}
	

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user) {
		Message msg = new Message();
		try{
			Contract c= contractService.getById(user.getProjectCode(), id);
			c.setStatus(Status.cancel);
			contractService.update(user.getProjectCode(), c);
			msg.setMsg("作废成功");
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("作废失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping("/close")
	public String close(ModelMap model, @CurrentUser User user){
		model.addAttribute("user", user);
		return "hospital/contract/para";
	}
	
	@RequestMapping(value = "/close", method = RequestMethod.POST)
	@ResponseBody
	public Message close(Long id,String reason, @CurrentUser User user) {
		System.out.println("id="+id);
		System.out.println("reason="+reason);
		Message msg = new Message();
		try{
			if(user.getOrganization().getOrgType() !=null && user.getOrganization().getOrgId() != null){
				Integer type = user.getOrganization().getOrgType();
				if(type == 1){
					Contract c = contractService.getById(user.getProjectCode(), id);
					System.out.println("c.getcode"+c.getCode());
					ContractClosedRequest ccr = contractClosedRequestService.findByContract(user.getProjectCode(), c.getCode());
					if(ccr != null){
						msg.setSuccess(false);
						msg.setMsg("该合同已经提交申请结案");
						return msg;
					}
					
					ccr = new ContractClosedRequest();
					ccr.setCode(snService.getCode(user.getProjectCode(), OrderType.contractClosedRequest));
					ccr.setContractCode(c.getCode());
					ccr.setClosedMan(user.getName());
					ccr.setClosedObject(ClosedObject.contract);
					ccr.setClosedRequestDate(new Date());
					ccr.setStatus(com.shyl.msc.b2b.plan.entity.ContractClosedRequest.Status.unaudit);
					ccr.setReason(reason);
					contractClosedRequestService.save(user.getProjectCode(),  ccr);
					
					msg.setMsg("申请结案成功");
				}else{
					msg.setMsg("您不是医院身份登录");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("终止失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
