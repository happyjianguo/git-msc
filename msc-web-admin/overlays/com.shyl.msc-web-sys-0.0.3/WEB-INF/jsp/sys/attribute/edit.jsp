<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>属性代号:</th>
		   		<th>
		   			<span id = "showattributeNo"></span>
					<input type="hidden" name="attributeNo">
					<input type="hidden" name="id">
				</th>
		   </tr>
		   <tr>
		   		<th>属性名称:</th>
		   		<th>
					<input type="text" name="name" class="easyui-textbox" data-options="required:true"  >
				</th>
		   </tr>
		   <tr>
		   		<th>属性描述:</th>
		   		<th>
					<input type="text" name="description" class="easyui-textbox" data-options="required:true"  >
				</th>
		   </tr>
		</table>
		</form>
</div>


<script>
$(function(){
	//$("input").textbox({validType:'length[0,100]'});
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/sys/attribute/edit.htmlx",
		
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
				showMsg(result.msg);
			}else{
				showErr("出错，请刷新重新操作");
			}
		}
	});
	
})



</script>
