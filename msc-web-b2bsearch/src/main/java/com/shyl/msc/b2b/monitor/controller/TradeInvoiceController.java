package com.shyl.msc.b2b.monitor.controller;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.GridFSDAO;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import com.shyl.msc.b2b.stl.entity.TradeInvoiceDetail;
import com.shyl.msc.b2b.stl.service.ITradeInvoiceDetailService;
import com.shyl.msc.b2b.stl.service.ITradeInvoiceService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.FileManagement;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IFileManagementService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易发票
 *
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/tradeInvoice")
public class TradeInvoiceController extends BaseController{

    @Resource
    private ITradeInvoiceService tradeInvoiceService;
    @Resource
    private ITradeInvoiceDetailService tradeInvoiceDetailService;
    @Resource
    private GridFSDAO gridFSDAO;
    @Resource
    private IFileManagementService fileManagementService;
    @Resource
    private IHospitalService hospitalService;
    /**
     *
     * @return
     */
    @RequestMapping("")
    public String home(){
        return "/b2b/monitor/tradeInvoice/list";
    }

    /**
     * 分页查询
     * @param pageable
     * @return
     */
    @RequestMapping("/page")
    @ResponseBody
    public DataGrid<TradeInvoice> page(PageRequest pageable, @CurrentUser User user){
        return tradeInvoiceService.query(user.getProjectCode(), pageable);
    }


    /**
     * 明细分页查询
     * @param pageable
     * @return
     */
    @RequestMapping("/mxlist")
    @ResponseBody
    public DataGrid<TradeInvoiceDetail> mxlist(PageRequest pageable, @CurrentUser User user){
        String type = (String)pageable.getQuery().get("t#tradeInvoice.type_E_EQ");
        if(StringUtils.isNotEmpty(type)){
            pageable.getQuery().put("t#tradeInvoice.type_E_EQ",TradeInvoice.Type.valueOf(type));
            if(TradeInvoice.Type.valueOf(type).equals(TradeInvoice.Type.vendorToHospital)){
                if(user.getOrganization().getOrgType() == 1){
                    Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
                    pageable.getQuery().put("t#tradeInvoice.hospitalCode_S_EQ", hospital.getCode());
                }
            }
        }
        pageable.setSort(new Sort(Sort.Direction.DESC,"tradeInvoice.orderDate"));
        DataGrid<TradeInvoiceDetail> page = tradeInvoiceDetailService.query(user.getProjectCode(), pageable);
        page.addFooter("batchCode", "goodsNum","goodsSum");
        return page;
    }

    /**
     * 查看附件
     * @param user
     * @param response
     */
    @RequestMapping("/readfile")
    public void exportExcel(Long id,@CurrentUser User user, HttpServletResponse response){
        TradeInvoice tradeInvoice = tradeInvoiceService.getById(user.getProjectCode(),id);
        try{
            if(tradeInvoice != null){
                List<FileManagement> fileList = fileManagementService.findByKeyFlag(user.getProjectCode(),"tradeInvoice"+tradeInvoice.getId());
                FileManagement file = null;
                if(fileList .size() >0){
                    file = fileList.get(0);
                    response.setContentType("application/pdf;charset=UTF-8");
                    response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
                    gridFSDAO.findFileByIdToOutputStream(file.getFileURL(), "tradeInvoice", response);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 统计
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/doCount" , method = RequestMethod.POST)
    @ResponseBody
    public Message doCount(PageRequest pageable,String projectCode, @CurrentUser User user){
        Message message = new Message();
        try{
            List<TradeInvoiceDetail> tradeInvoiceDetails = getBySelect(TradeInvoice.Type.vendorToHospital,TradeInvoice.Type.GPOToVendor);
            if(tradeInvoiceDetails == null ){
                tradeInvoiceDetails = new ArrayList<>();
            }
            tradeInvoiceDetails.addAll(getBySelect(TradeInvoice.Type.GPOToVendor,TradeInvoice.Type.producerToGPO));
            tradeInvoiceDetailService.updateBatchByCode(user.getProjectCode(),tradeInvoiceDetails);
            message.setMsg("成功执行了"+ tradeInvoiceDetails.size()+"条");
        }catch (Exception e){
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg(e.getMessage());
        }
        return message;
    }

    private List<TradeInvoiceDetail> getBySelect(TradeInvoice.Type type,TradeInvoice.Type type1){
        PageRequest pageRequest = new PageRequest();
        pageRequest.getQuery().put("t#tradeInvoice.type_E_EQ", type);
        pageRequest.getQuery().put("t#isExistsParent_I_EQ",0);
        List<TradeInvoiceDetail> tradeInvoiceDetails = tradeInvoiceDetailService.list("",pageRequest);
        if(CollectionUtils.isEmpty(tradeInvoiceDetails)){
            return null;
        }
        //根据商品编码和批号，供应商来查询gpo到供应商的数据
        StringBuilder s = new StringBuilder();
        s.append("'0'");
        List<TradeInvoiceDetail> gpoTradeInvoiceDetails = new ArrayList<>();
        int i = 0;
        for(TradeInvoiceDetail td : tradeInvoiceDetails){
            i++;
            s.append(",'"+td.getProductCode()+td.getBatchCode());
            if(TradeInvoice.Type.vendorToHospital.equals(type)){
                s.append(td.getTradeInvoice().getVendorCode()+"'");
            }else if(TradeInvoice.Type.GPOToVendor.equals(type)){
                s.append(td.getTradeInvoice().getGpoCode()+"'");
            }
            if(i % 300 ==0){
                gpoTradeInvoiceDetails.addAll(tradeInvoiceDetailService.getByCode("",s.toString(),type1));
                s = new StringBuilder();
                s.append("'0'");
            }
        }
        gpoTradeInvoiceDetails.addAll(tradeInvoiceDetailService.getByCode("",s.toString(),type1));
        Map<String,TradeInvoiceDetail> maps = new HashMap<>();
        for(TradeInvoiceDetail td2 : gpoTradeInvoiceDetails){
            String key = td2.getProductCode()+td2.getBatchCode()+td2.getTradeInvoice().getCustomerCode();
            if(null == maps.get(key)){
                maps.put(key,td2);
            }
        }
        List<TradeInvoiceDetail> updateTradeInvoiceDetails = new ArrayList<>();
        for(TradeInvoiceDetail td : tradeInvoiceDetails){
            String key = "";
            if(TradeInvoice.Type.vendorToHospital.equals(type)){
                key = td.getProductCode()+td.getBatchCode()+td.getTradeInvoice().getVendorCode();
            }else if(TradeInvoice.Type.GPOToVendor.equals(type)){
                key = td.getProductCode()+td.getBatchCode()+td.getTradeInvoice().getGpoCode();
            }
            if(maps.get(key) != null){
                td.setIsExistsParent(new Integer(1));
                updateTradeInvoiceDetails.add(td);
            }/*else{
                td.setIsExistsParent(new Integer(0));
            }*/
        }
        return updateTradeInvoiceDetails;
    }

    @Override
    protected void init(WebDataBinder webDataBinder) {

    }
}
