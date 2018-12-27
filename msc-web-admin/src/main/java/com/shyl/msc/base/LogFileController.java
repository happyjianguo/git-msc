package com.shyl.msc.base;

import com.shyl.common.util.ZipUtils;
import com.shyl.common.web.controller.BaseController;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/base/logFile")
public class LogFileController extends BaseController{

    private final Logger logger = LoggerFactory.getLogger(LogFileController.class);

    public enum PathType{
        tomcat,
        base,
        b2b,
        supervise
    }

    private final static String TOMCATLOGPATH = "/app/tomcat/logs";

    private final static String BASEDUBBOLOGPATH = "/app/msc/1-service-base/logs";

    private final static String B2BEDUBBOLOGPATH = "/app/msc/2-service-b2b/logs";

    private final static String SUPERVISEDUBBOLOGPATH = "/app/msc/9-service-supervise/logs";

    /**
     * 主页
     * @return
     */
    @RequestMapping("")
    public String home(){
        return "base/logFile";
    }

    @RequestMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> list(String type){
        List<Map<String, Object>> fileList = new ArrayList<>();
        try{

            String filePath = TOMCATLOGPATH;
            if(StringUtils.isNotEmpty(type)){
                PathType pathType = PathType.valueOf(type);
                switch (pathType){
                    case tomcat:
                        filePath = TOMCATLOGPATH;
                        break;
                    case base:
                        filePath = BASEDUBBOLOGPATH;
                        break;
                    case b2b:
                        filePath = B2BEDUBBOLOGPATH;
                        break;
                    case supervise:
                        filePath = SUPERVISEDUBBOLOGPATH;
                        break;
                }
            }
            File file = new File(filePath);
            if(file.isDirectory()){
                File[] files = file.listFiles();
                for(File f : files){
                    String name = f.getName();
                    Long size = f.length();
                    String length = size+"";
                    if(size>1000){
                        size = size/1000;
                        length = size+"kB";
                    }
                    if(size>1000){
                        size = size/1000;
                        length = size+"MB";
                    }
                    if(size>1000){
                        size = size/1000;
                        length = size+"GB";
                    }
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", name);
                    m.put("length", length);
                    m.put("canRead", f.canRead());
                    m.put("isFile", f.isFile());
                    fileList.add(m);

                }
            }
        }catch (Exception e){
            logger.info("readfile()   Exception:" + e.getMessage());
        }
        return fileList;
    }

    @RequestMapping("/read")
    @ResponseBody
    public Map<String, Object> read(String fileName,String lines,String type,Long start) throws IOException {
        if(lines == null){
            lines = "50";
        }
        PathType pathType = PathType.valueOf(type);
        String filePath = TOMCATLOGPATH;
        switch (pathType){
            case tomcat:
                filePath = TOMCATLOGPATH;
                break;
            case base:
                filePath = BASEDUBBOLOGPATH;
                break;
            case b2b:
                filePath = B2BEDUBBOLOGPATH;
                break;
            case supervise:
                filePath = SUPERVISEDUBBOLOGPATH;
                break;
        }
        File file = new File(filePath+"/"+fileName);
        Long startTime = System.currentTimeMillis();
        List<String> list = readLines(file,Long.parseLong(lines)*1024,start);
        Long endTime = System.currentTimeMillis();
        logger.info("read:"+(endTime-startTime));
        Map<String, Object> m = new HashMap<>();
        m.put("len", file.length());
        m.put("list", list);
        return m;
    }

    private List<String> readLines(File file, Long lines) throws IOException {
        return this.readLines(file, lines, null);
    }

    private List<String> readLines(File file, Long lines,Long start) throws IOException {
        List<String> list = new ArrayList<>();
        RandomAccessFile src = null;
        try {
            src = new RandomAccessFile(file, "r");
            Long length = file.length();
            if(start == null || start == 0L){
                //读取起点
                src.seek(length - lines);
            }else{
                if(start.equals(length)){
                    return list;
                }
                Long readLen = length - start;
                if(readLen.longValue() > 1000L*1024){
                    src.seek(length - lines);
                }else{
                    src.seek(start);
                }

            }
            // 开始读写
            byte[] arr = new byte[1024];
            while ((src.read(arr)) != -1) {
                //一直读，读到结尾
                list.add(new String(arr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(src != null){
                src.close();
            }
        }
        return list;
    }

    @RequestMapping("/download")
    public ResponseEntity<byte[]> export(String fileName, String lines, String type) throws IOException {
        PathType pathType = PathType.valueOf(type);
        String filePath = TOMCATLOGPATH;
        switch (pathType){
            case tomcat:
                filePath = TOMCATLOGPATH;
                break;
            case base:
                filePath = BASEDUBBOLOGPATH;
                break;
            case b2b:
                filePath = B2BEDUBBOLOGPATH;
                break;
            case supervise:
                filePath = SUPERVISEDUBBOLOGPATH;
                break;
        }
        File file = new File(filePath+"/"+fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName+".zip");

        if(lines == null || lines.equals("all")){
            File zipFile = new File("logFile_zip");
            ZipUtils.doCompress(file, zipFile);
            byte[] b = FileUtils.readFileToByteArray(zipFile);
            if(zipFile.exists()){
                zipFile.delete();
            }
            return new ResponseEntity<byte[]>(b, headers, HttpStatus.CREATED);
        }

        Long startTime = System.currentTimeMillis();
        List<String> list = readLines(file,Long.parseLong(lines)*1024);
        Long endTime = System.currentTimeMillis();
        logger.info("read:"+(endTime-startTime));

        File rtnFle = new File("logFile_temp");
        FileUtils.writeLines(rtnFle,list,false);

        File zipFile = new File("logFile_zip");
        ZipUtils.doCompress(rtnFle, zipFile);

        byte[] b = FileUtils.readFileToByteArray(zipFile);
        if(rtnFle.exists()){
            rtnFle.delete();
        }
        if(zipFile.exists()){
            zipFile.delete();
        }
        return new ResponseEntity<byte[]>(b, headers, HttpStatus.CREATED);
    }







    @Override
    protected void init(WebDataBinder webDataBinder) {

    }
}
