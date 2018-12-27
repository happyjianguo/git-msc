<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>省:</th>
		   		<th>
					<input id="combox1" name="combox1" style="width:150px;">
				</th>
		   </tr>
		   <tr>
		   		<th>市:</th>
		   		<th>
					<input id="combox2" name="combox2" style="width:150px;">
				</th>
		   </tr>
		   <tr>
				<th>地区编码：</th>
		   		<th>
		   			<input type="text" name="code" class="easyui-validatebox  easyui-textbox" data-options="required:true" readonly>
		   		</th>
		   </tr>
		   <tr>
		   		<th>地区名称：</th>
		   		<th>
					<input type="text" name="name" class="easyui-validatebox  easyui-textbox-16" data-options="required:true" >
				</th>
		   </tr>
		   <tr>
		   		<th>是否禁用：</th>
		   		<th>
		   			<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
				</th>
		   </tr>
		</table>
		<input type="hidden" name="id" >
	</form>


</div>



<script>
function toCombox2(pid){
	if(pid == null)
		return;
	$('#combox2').combobox({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx',
		queryParams:{
			parentId:pid
		},
		value:'',
	    valueField:'id',    
	    textField:'name', 
		required: false
	});
	
}
//初始化
$(function(){
	$('#combox1').combobox({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvlone.htmlx',
	    valueField:'id',    
	    textField:'name', 
	    value:'<c:out value='${combox1}'/>',
	    editable:false,
	    onSelect:function(a,b){
	    	var pid = $('#combox1').combobox('getValue');
	    	toCombox2(pid);
	    }
	});
	
	$('#combox2').combobox({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx',
		queryParams:{
			parentId:'<c:out value='${combox1}'/>'
		},
	    valueField:'id',    
	    textField:'name', 
	    value:'<c:out value='${combox2}'/>',
	    editable:false,
		required: false
	});
    $("input.easyui-textbox-16").textbox({validType: 'maxLength[16]'});
		
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/edit.htmlx",
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = $(this).form('validate');
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