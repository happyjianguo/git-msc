package com.shyl.msc.dm.service.impl;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IGoodsCountryDao;
import com.shyl.msc.dm.entity.GoodsCountry;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsCountryService;
import com.shyl.msc.dm.service.IProductService;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
@Transactional(readOnly = true)
public class GoodsCountryService extends BaseService<GoodsCountry,Long> implements IGoodsCountryService {

    private IGoodsCountryDao goodsCountryDao;

    @Autowired(required = false)
    private RedisTemplate redisTemplate;
    @Resource
    private IProductService productService;

    @Resource
    public void setGoodsCountryDao(IGoodsCountryDao goodsCountryDao){
        this.goodsCountryDao = goodsCountryDao;
        super.setBaseDao(goodsCountryDao);
    }


    @Override
    public DataGrid<Map<String, Object>> selectAll(String projectCode, PageRequest pageable) {
        return goodsCountryDao.selectAll(pageable);
    }

    @Override
    public List<Map<String, Object>> selectAllforExport(String projectCode, PageRequest pageable) {
        List<Map<String, Object>> list = goodsCountryDao.selectAllforExport(pageable);
        if(list.size() > 14000){
            redisTemplate.opsForList().leftPush("goodsCountry",list);
            return null;
        }
        return goodsCountryDao.selectAllforExport(pageable);
    }

    @Override
    @Transactional
    public void importExcel(String projectFlag, String[][] datas) {
        //这里实现阻塞
        while(true){
            if(redisTemplate.opsForList().leftPop("countryDrug23") != null){
                break;
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException("操作失败");
                }
            }
        }
        //首先要获取对照目录
        List<GoodsCountry> goodsCountryList = (List<GoodsCountry>)redisTemplate.opsForList().leftPop("goodsCountryList");
        Map<String,GoodsCountry> goodsCountryMap = new HashMap<>();
        for(GoodsCountry g : goodsCountryList){
            goodsCountryMap.put(g.getProductCode(),g);
        }

        //获取药品目录
        List<Product> productList = new ArrayList<>();
        for(int i=0;i<4;i++){
            productList.addAll((List<Product>)redisTemplate.opsForList().leftPop("productList"+i));
        }
        Map<String,Product> productMap = new HashMap<>();
        for(Product p : productList){
            productMap.put(p.getCode(),p);
        }

        //获取国家药品目录
        List<Map<String,Object>> countryDrugList = new ArrayList<>();
        for(int i=0 ;i<24 ;i++){
            countryDrugList.addAll((List<Map<String,Object>>)redisTemplate.opsForList().leftPop("countryDrug"+i));
        }
        Map<String,Map<String, Object>> countryDrugMap = new HashMap<>();
        for(Map<String,Object> map : countryDrugList){
            countryDrugMap.put((String)map.get("COUNTRYPRODUCTCODE"),map);
        }

        List<GoodsCountry> addList = new ArrayList<>();
        List<GoodsCountry> updateList = new ArrayList<>();

        for(int i=0; i<datas.length; i++){
            String productCode = datas[i][0].trim();
            String countryProductCode = datas[i][1].trim();
            if(!productMap.containsKey(productCode)){
                continue;
            }
            if(!goodsCountryMap.containsKey(countryProductCode)){
                continue;
            }
            //该对照关系已存在   修改该笔数据
            GoodsCountry gc = null;
            if(goodsCountryMap.containsKey(productCode)){
                gc = goodsCountryMap.get("productCode");
                setGoodsCountryInfo(productCode,countryProductCode,gc,countryDrugMap,productMap);
                updateList.add(gc);
            }else{
                gc = new GoodsCountry();
                setGoodsCountryInfo(productCode,countryProductCode,gc,countryDrugMap,productMap);
                addList.add(gc);
            }
        }
        goodsCountryDao.updateBatch(updateList);
        goodsCountryDao.flush();
        goodsCountryDao.clear();
        goodsCountryDao.saveBatch(addList);
    }

    private void setGoodsCountryInfo(String productCode,String countryProductCode,GoodsCountry gc, Map<String,Map<String, Object>> countryDrugMap,Map<String,Product> productMap) {
        //药品信息
        Product p = productMap.get(productCode);
        gc.setProductId(p.getId());
        gc.setProductName(p.getName());
        gc.setProductCode(gc.getProductCode());
        gc.setProductGCode(p.getProductGCode());
        gc.setPackDesc(p.getPackDesc());
        gc.setModel(p.getModel());
        gc.setDosageFormName(p.getDosageFormName());
        gc.setProducerName(p.getProducerName());
        gc.setAuthorizeNo(p.getAuthorizeNo());
        //国家药品信息
        Map<String,Object> map = countryDrugMap.get(countryProductCode);
        gc.setCountryProductCode(map.get("COUNTRYPRODUCTCODE") == null ? null : map.get("COUNTRYPRODUCTCODE").toString());
        gc.setCountryProductName(map.get("COUNTRYPRODUCTNAME") == null ? null : map.get("COUNTRYPRODUCTNAME").toString());
        gc.setCountryProducerCode(map.get("COUNTRYPRODUCERCODE") == null ? null : map.get("COUNTRYPRODUCERCODE").toString());
        gc.setCountryProducerName(map.get("COUNTRYPRODUCERNAME") == null ? null : map.get("COUNTRYPRODUCERNAME").toString());
        gc.setCountryAuthorizeNo(map.get("COUNTRYAUTHORIZENO") == null ? null : map.get("COUNTRYAUTHORIZENO").toString());
        gc.setCountryDosageFormCode(map.get("COUNTRYDOSAGEFORMCODE") == null ? null : map.get("COUNTRYDOSAGEFORMCODE").toString());
        gc.setCountryDosageFormName(map.get("COUNTRYDOSAGEFORMNAME") == null ? null : map.get("COUNTRYDOSAGEFORMNAME").toString());
        gc.setCountryModel(map.get("COUNTRYMODEL") == null ? null : map.get("COUNTRYMODEL").toString());
        gc.setCountryModelCode(map.get("COUNTRYMODELCODE") == null ? null : map.get("COUNTRYMODELCODE").toString());
        gc.setCountryConvertRatio(map.get("COUNTRYCONVERTRATIO") == null ? null : Integer.valueOf(map.get("COUNTRYCONVERTRATIO").toString()));
        gc.setCountryConvertRatioCode(map.get("COUNTRYCONVERTRATIOCODE") == null ? null:map.get("COUNTRYCONVERTRATIOCODE").toString());
        gc.setCountryProductGCode(map.get("COUNTRYPRODUCTGCODE") == null ? null : map.get("COUNTRYPRODUCTGCODE").toString());
    }

    private JSONObject getCountryAndProductListByExcel(String[][] datas) {
        //多线程查询
        Map<Integer,Callable<List<Map<String, Object>>>> map = new HashMap<>();
        List<String> goodsStr = new ArrayList<>();
        List<String> countryStr = new ArrayList<>();
        /*Integer count = 0;
        PageRequest pageRequest = new PageRequest();
        for (int i = 0; i < datas.length; i++) {
            String[] row = datas[i];
            goodsStr.add(row[0]);
            countryStr.add(row[1]);
            if (i % 300 == 0) {
                count++;
                Callable<List<Map<String, Object>>> callable = new Callable<List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> call() throws Exception {
                        pageRequest.getQuery().put("", StringUtils.join(countryStr,","));
                        List<Map<String,Object>> list = goodsCountryDao.getCountryDrugByCode(pageRequest);
                        return list;
                    }
                };
                map.put(count,callable);
                countryStr.clear();
            }
        }
        count++;
        Callable<List<Map<String, Object>>> callable = new Callable<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> call() throws Exception {
                pageRequest.getQuery().put("", StringUtils.join(countryStr,","));
                List<Map<String,Object>> list = goodsCountryDao.getCountryDrugByCode(pageRequest);
                return list;
            }
        };
        map.put(count,callable);*/
        //查询product
        Callable<List<Product>> productCallable = new Callable<List<Product>>() {
            @Override
            public List<Product> call() throws Exception {
                List<Product> list = productService.list(null);
                return list;
            }
        };
        //查询goodscountry对照关系表
        Callable<List<GoodsCountry>> goodsCountryCallable = new Callable<List<GoodsCountry>>() {
            @Override
            public List<GoodsCountry> call() throws Exception {
                List<GoodsCountry> list = goodsCountryDao.getAll(null);
                return list;
            }
        };
        JSONObject jsonObject = new JSONObject();
        try{
            FutureTask<List<Product>> productTask = new FutureTask<List<Product>>(productCallable);
            FutureTask<List<GoodsCountry>> goodsCountryTask = new FutureTask<List<GoodsCountry>>(goodsCountryCallable);

            /*Map<Integer,FutureTask<List<Map<String, Object>>>> futureTaskMap = new HashMap<>();
            for(Integer key : map.keySet()){
                FutureTask<List<Map<String, Object>>> task = new FutureTask<List<Map<String, Object>>>(map.get(key));
                futureTaskMap.put(key,task);
            }
            for(Integer key : futureTaskMap.keySet()){
                threadPoolTaskExecutor.submit(futureTaskMap.get(key));
            }*/
            /*threadPoolTaskExecutor.submit(productTask);
            threadPoolTaskExecutor.submit(goodsCountryTask);*/
            //最终查询到的国家药品目录
            /*List<Map<String, Object>> countryDrugList = new ArrayList<>();
            for(Integer key : futureTaskMap.keySet()){
                countryDrugList.addAll(futureTaskMap.get(key).get());
            }*/
            //所有的药品目录
            List<Product> productList = productTask.get();

            List<GoodsCountry> goodsCountryList = goodsCountryTask.get();

            //将国家药品目录存放到map里面
            Map<String,Map<String, Object>> countryDrugMap = new HashMap<>();
            /*for(Map<String, Object> map1 : countryDrugList){
                String key = (String) map1.get("COUNTRYPRODUCTCODE");
                countryDrugMap.put(key,map1);
            }*/
            Map<String,Product> productMap = new HashMap<>();
            for(Product p : productList){
                String key = p.getCode();
                productMap.put(key,p);
            }
            Map<String,GoodsCountry> goodsCountryMap = new HashMap<>();
            for(GoodsCountry g : goodsCountryList){
                goodsCountryMap.put(g.getProductCode(),g);
            }
            jsonObject.put("countryDrug",countryDrugMap);
            jsonObject.put("product",productMap);
            jsonObject.put("goodsCountry",goodsCountryMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("多线程查询失败");
        }
        return jsonObject;
    }

    @Override
    public void getByAllByAsync(String projectCode) {
        List<GoodsCountry> goodsCountryList = goodsCountryDao.getAll(null);
        if(CollectionUtils.isNotEmpty(goodsCountryList)){
            redisTemplate.opsForList().leftPush("goodsCountryList",goodsCountryList);
        }
    }

    @Override
    public void getByAllDrugByAsync(String projectCode, Integer i) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageSize(10000);
        pageRequest.setPage(i);
        List<Map<String,Object>> list = goodsCountryDao.getCountryDrugByCode(pageRequest);
        if(CollectionUtils.isNotEmpty(list)){
            redisTemplate.opsForList().leftPush("countryDrug"+i,list);
        }
    }
}
