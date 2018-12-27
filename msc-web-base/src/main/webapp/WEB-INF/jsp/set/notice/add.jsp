<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post" enctype="multipart/form-data">
		<table class="table-bordered">
		   <tr>
				<th>标题：</th>
		   		<th>
		   			<input type="text" id = "title" name="title" class="easyui-validatebox  easyui-textbox-33" data-options="required:true" >
		   		</th>
		   </tr>
		   <tr>
		   		<th>内容：</th>
		   		<th>
		   			<textarea rows="6" cols="50%" id="content" name="content" placeholder="最多500个汉字" ></textarea>
					
				</th>
		   </tr>
		   <tr>
		   		<th>上传附件: &nbsp;&nbsp; <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="addFile()"></a></th>
		   		<th  >
					<div id="filesdiv">
						<div><input type="file" name="file" class="file" id="file" accept="application/pdf"/></div>
					</div>
				</th>
		   </tr>
		</table>
		<input type="hidden" name="id" value=""  >
	</form>


</div>



<script>
function addFile(){
	var d = $("<div><input type='file' name='file' class='file'  /></div>");
	$("#filesdiv").append(d);
}
//初始化
$(function(){
    $("input.easyui-textbox-33").textbox({validType: 'maxLength[60]'});//暂时控制

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/notice/add.htmlx",
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = $(this).form('validate');
			if(m9k.getLength($("#content").val())>1000){
				showErr("内容长度"+m9k.getLength($("#content").val())+",不能超过1000字节");
				isValid = false;
			}
			//判断文件大小 不超过15m
            try{
				var dom = document.getElementsByName("file");
				for(var i=0;i<dom.length;i++){
					if(dom[i].files[0]){
						var fileSize =  dom[i].files[0].size;//文件的大小，单位为字节B
						if(fileSize>15728640){
							isValid = false;
							showErr("附件超大,最大支持15M");
						}
					}
				}
			}catch (e){

            }
			if (!isValid) {
				top.$.messager.progress('close');
			}
			return isValid;
		},
		success : function(result) {
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				top.$.modalDialog.openner.datagrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("新增成功！");
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});

function makerUpload(chunk){
	 Uploader(chunk,function(files){
		 if(files && files.length>0){
			 $("#res").text("成功上传："+files.join(","));
		 }
	 });
	}
	
/**
 * 创建上传窗口 公共方法
 * @param chunk 是否分割大文件
 * @param callBack 上传成功之后的回调
 */
function Uploader(chunk,callBack){
	var APPID="";
	var complaintNos = "";
	var addWin = $('<div style="overflow: hidden;"/>');
	var upladoer = $('<iframe/>');
	/* upladoer.attr({'src':' <c:out value='${pageContext.request.contextPath }'/>/uploader.jsp?chunk='+chunk,width:'100%',height:'100%',frameborder:'0',scrolling:'no'});
	 */
	 upladoer.attr({'src':" <c:out value='${pageContext.request.contextPath }'/>/uploader.jsp?chunk="+chunk+"&complaintNo='"+complaintNos+"'&APPID='"+APPID+"'",width:'100%',height:'100%',frameborder:'0',scrolling:'no'});
	 addWin.window({
		title:"上传文件",
		height:350,
		width:550,
		minimizable:false,
		modal:true,
		collapsible:false,
		maximizable:false,
		resizable:false,
		content:upladoer,
		onClose:function(){
			var fw = GetFrameWindow(upladoer[0]);
			var files = fw.files;
			$(this).window('destroy');
			callBack.call(this,files);
		},
		onOpen:function(){
			var target = $(this);
			setTimeout(function(){
				var fw = GetFrameWindow(upladoer[0]);
				fw.target = target;
			},100);
		}
	});
}

/**
 * 根据iframe对象获取iframe的window对象
 * @param frame
 * @returns {Boolean}
 */
function GetFrameWindow(frame){
	return frame && typeof(frame)=='object' && frame.tagName == 'IFRAME' && frame.contentWindow;
}
 
 function getFileName(s){
		var oldFileName = $("#fileList").val();
		var newfileName = "";
		if(oldFileName !=''){
			newfileName = oldFileName+"、"+s;
		}else{
			newfileName = oldFileName+s;
		}
		$("#fileList").val(newfileName);
		var files = s.split("、");
		var str = "";
		if(files.length >0)
			$("#filePath").val(files[0]);
		return;
		
		for(var i = 0;i<files.length;i++){
			str+="<div  style='float:left;margin-left:5px;padding-top:9px;;position: relative;'><span class='delFile' data-file='"+files[i]+"' title='删除'  style='position:absolute;top:0px;right:2px;'>x</span> <a href='/upload/"+files[i]+"'>"+files[i]+"</a></div>"
		}
			$("#fileNames").append(str).find(".delFile").click(function(){
				var fileName = $(this).attr("data-file");
				$.ajax({
					url:" <c:out value='${pageContext.request.contextPath }'/>/ccm/complaint/delFile.htmlx",
					data:"fileName="+fileName,
					dataType:"json",
					type:"POST",
					cache:false,
					success:function(data){
						if(data.isok == "Y"){
							
						} 
					},
					error:function(){
					}
				});	
				$(this).parent("div").css("display","none");
				
			});
	}
</script>