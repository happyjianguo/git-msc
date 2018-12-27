package com.shyl.msc.set.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.GridFSDAO;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Notice;
import com.shyl.msc.set.service.INoticeService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.FileManagement;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IFileManagementService;


/**
 * 公告
 * @author dell
 *
 */
@Controller
@RequestMapping("/set/notice")
public class NoticeController extends BaseController{
	@Resource
	private IRegionCodeService regionCodeService;
	
	@Resource
	private  INoticeService noticeService;
	@Resource
	private  IFileManagementService fileManagementService;
	@Resource
	private GridFSDAO gridFSDAO;
	
	private static final int BUFFER_SIZE = 100 * 1024;
	private static final String UPLOADFOLDER = "upload/notice/";
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "set/notice/list";
	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("/more")
	public String more(){
		return "set/notice/morelist";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Notice> page(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);
		DataGrid<Notice> page =  noticeService.query(user.getProjectCode(), pageable);
		for (Notice notice : page.getRows()) {
			List<FileManagement> l =fileManagementService.findByKeyFlag(user.getProjectCode(), "notice_"+notice.getId());
			for (FileManagement fileManagement : l) {
				notice.getFileManagement().add(fileManagement);
			}
		}
		return page;
	}
	
	/**
	 * 分页查询more ,供index使用
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/morepage")
	@ResponseBody
	public DataGrid<Notice> morepage(PageRequest pageable, @CurrentUser User user){
		pageable.setSort(new Sort(Direction.DESC,"publishDate"));
		DataGrid<Notice> page =  noticeService.query(user.getProjectCode(), pageable);
		for (Notice notice : page.getRows()) {
			List<FileManagement> l =fileManagementService.findByKeyFlag(user.getProjectCode(), "notice_"+notice.getId());
			for (FileManagement fileManagement : l) {
				notice.getFileManagement().add(fileManagement);
			}
		}
		return page;
	}
	
	/**
	 * 全部查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<Notice> list(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);
		List<Notice> list =  noticeService.list(user.getProjectCode(), pageable);
		return list;
	}
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "set/notice/add";
	}
	
	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Notice notice,@RequestParam("file") CommonsMultipartFile[] files,HttpServletRequest request,@CurrentUser User user){
		Message message = new Message();
		try{
			notice.setStatus(0);
			notice = noticeService.save(user.getProjectCode(), notice);
			for (CommonsMultipartFile multipartFile : files) {
				if(!StringUtils.isEmpty(multipartFile.getOriginalFilename())){
					//String rootPath = request.getSession().getServletContext().getRealPath("/");
					//String fp = this.saveFile(multipartFile,rootPath);
					DiskFileItem fi = (DiskFileItem)multipartFile.getFileItem(); 
				    File f = fi.getStoreLocation();
					String fileid = gridFSDAO.saveFile(f, multipartFile.getOriginalFilename(), "notice");
					
					FileManagement fm = new FileManagement();
					fm.setFileName(multipartFile.getOriginalFilename());
					fm.setFileURL(fileid);
					fm.setKeyFlag("notice_"+notice.getId()+"");
					fileManagementService.save(user.getProjectCode(), fm);
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(){
		return "set/notice/edit";
	}
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Notice notice,String delId,@RequestParam("file") CommonsMultipartFile[] file,HttpServletRequest request,@CurrentUser User user){
		Message message = new Message();
		try{
			if(delId != null){
				String[] delisArr = delId.split(",");
				for (int i = 0; i < delisArr.length; i++) {
					if(!delisArr[i].equals("")){
						Long id = Long.parseLong(delisArr[i]);
						fileManagementService.delete(user.getProjectCode(), id);
					}
					
				}
			}
			
			for (CommonsMultipartFile multipartFile : file) {
				if(!StringUtils.isEmpty(multipartFile.getOriginalFilename())){
					DiskFileItem fi = (DiskFileItem)multipartFile.getFileItem(); 
				    File f = fi.getStoreLocation();
					String fileid = gridFSDAO.saveFile(f, multipartFile.getOriginalFilename(), "notice");
					FileManagement fm = new FileManagement();
					fm.setFileName(multipartFile.getOriginalFilename());
					fm.setFileURL(fileid);
					fm.setKeyFlag("notice_"+notice.getId()+"");
					fileManagementService.save(user.getProjectCode(), fm);
				}
			}
			
			noticeService.updateWithInclude(user.getProjectCode(), notice,"title","content");
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}

	/**
	 * 删除
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id, @CurrentUser User user){
		Message message = new Message();
		
		try{
			Notice n = noticeService.getById(user.getProjectCode(), id);
			if(n == null || n.getStatus() == 1){
				message.setSuccess(false);
				message.setMsg("公告已发布，无法删除");
			}else{
				message.setSuccess(true);
				noticeService.delete(user.getProjectCode(), id);
			}

		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return  message;
	}
	
	/**
	 *  发布 or 取消
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/status", method = RequestMethod.POST)
	@ResponseBody
	public Message status(Long id,Integer status, @CurrentUser User user){
		Message message = new Message();
		
		try{
			Notice n = noticeService.getById(user.getProjectCode(), id);
			n.setStatus(status);
			if(status == 1){
				n.setPublishDate((new SimpleDateFormat("yyyy-MM-dd")).format(new Date()));
			}else{
				n.setPublishDate("");
			}
			noticeService.update(user.getProjectCode(), n);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return  message;
	}
	
	/**
	 * 查看附件
	 * @param user
	 * @param resp
	 */
	@RequestMapping("/readfile")
	public void exportExcel(String fileid,@CurrentUser User user, HttpServletResponse response){
		
		gridFSDAO.findFileByIdToOutputStream(fileid, "notice", response);
	}
	
	/**
	 * 上传
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/upload")
	@ResponseBody
	public Message upload(@RequestParam("file") CommonsMultipartFile file,HttpServletRequest request) {
		Message message = new Message();
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		//System.out.println("附件上传"+s);
		try{
			System.out.println("附件上传");
			System.out.println(file);
			//regionCodeService.delete(id);
			this.saveFile(file,rootPath);
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return  message;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
		
	}
	private String saveFile(MultipartFile file,String rootPath) throws IOException {
		System.out.println("开始上传文件");

		String name = System.currentTimeMillis()+"";
		String fn = file.getOriginalFilename();
		String[] hzarr = fn.split("[.]");
		int hzarrLen = hzarr.length;
		if(hzarrLen >1){
			name += "."+hzarr[hzarrLen-1];
		}

		// 检查文件目录，不存在则创建
		String uploadPath = rootPath+UPLOADFOLDER;
		
		
		File folder = new File(uploadPath);
		if (!folder.exists()) {
			System.out.println("新建文件夹");
			folder.mkdirs();
		}
		System.out.println("folder==" + folder);
		// 目标文件
		File destFile = new File(folder, name);
		folder.setWritable(true, false);

		// 文件已存在删除旧文件（上传了同名的文件）
		if (destFile.exists()) {
			System.out.println("删除旧文件");
			destFile.delete();
			destFile = new File(folder, name);
		}
		// 合成文件
		appendFile(file.getInputStream(), destFile);
		//System.out.println("文件保存完成url="+uploadPath+name);
//			FileManagement f = new FileManagement();
//			f.setFileName(name);
//			f.setFileURL(uploadPath + name);
//			f.setKeyFlag(complaintNo);
//			FileManagement fileold = fileManagementService.queryByUrl(f.getFileURL());
//			if (fileold == null) {
//				fileManagementService.save(f);
//			}

		 return UPLOADFOLDER+name;

	}
	
	private void appendFile(InputStream in, File destFile) {
		OutputStream out = null;
		try {
			System.out.println(">>>>进这里开始上传文件<<<<<<");
			// plupload 配置了chunk的时候新上传的文件append到文件末尾
			if (destFile.exists()) {
				out = new BufferedOutputStream(new FileOutputStream(destFile, true), BUFFER_SIZE);
			} else {
				out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE);
			}
			in = new BufferedInputStream(in, BUFFER_SIZE);

			int len = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (Exception e) {
			// logger.error(e.getMessage());
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				// logger.error(e.getMessage());
			}
		}
	}
}
