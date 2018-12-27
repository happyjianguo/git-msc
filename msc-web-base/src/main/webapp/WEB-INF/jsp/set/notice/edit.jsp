<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post" enctype="multipart/form-data">
		<table class="table-bordered">
		   <tr >
				<th style="width:30%">标题：</th>
		   		<th>
		   			<input type="text" id= "title" name="title" class="easyui-validatebox  easyui-textbox-33" data-options="required:true" >
		   		</th>
		   </tr>
		   <tr>
		   		<th>内容：</th>
		   		<th>
					<textarea rows="6"  cols="50%" id="content" name="content" placeholder="最多500个汉字"></textarea>
				</th>
		   </tr>
		   <tr>
		   		<th>附件： &nbsp;&nbsp; <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="addFile()"></a></th>
		   		<th>
			   		<div id="filesdiv">
			   			
			   		</div>
		   			
				</th>
		   </tr>
		</table>
		<input type="hidden" name="id" value=""  >
		<input type='file' name='file' class='file' style="display:none" />
		<input type="hidden" id="delId" name="delId" value=""  >
	</form>


</div>



<script>
function setFileData(rows){
	for(var i=0;i<rows.length;i++){
		var d = $("<div style='padding:3px 0px;'><input type='text' name='fileName' class='file' accept='application/pdf' id='fileName"+rows[i].id+"'  /></div>");
		$("#filesdiv").append(d);
		$('#fileName'+rows[i].id).textbox({
			editable:false,
			width:200,
			value:rows[i].id+"、"+rows[i].fileName,
			icons: [{
				iconCls:'icon-no',
				handler: function(e){
					var val = $(e.data.target).textbox("getValue");
					val = val.split("、")[0];
					$("#delId").val($("#delId").val()+val+",");
					$(e.data.target).textbox("destroy");
					
				}
			}]
		})
	}
}

function addFile(){
	var d = $("<div style='padding:3px 0px;'><input type='file' name='file' class='file'  /></div>");
	$("#filesdiv").append(d);
}

//初始化
$(function(){
    $("input.easyui-textbox-33").textbox({validType: 'maxLength[60]'});//暂时控制

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/notice/edit.htmlx",
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
				showMsg("修改成功！");
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>