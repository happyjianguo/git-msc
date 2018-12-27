<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>属性代号:</th>
		   		<th>
					<input type="text" name="attributeNo" class="easyui-textbox-20" data-options="required:true" >
				</th>
		   </tr>
		   <tr>
		   		<th>属性名称:</th>
		   		<th>
					<input type="text" name="name" class="easyui-textbox-6" data-options="required:true"  >
				</th>
		   </tr>
		   <tr>
		   		<th>属性描述:</th>
		   		<th>
					<input type="text" name="description" class="easyui-textbox-67" data-options="required:true"  >
				</th>
		   </tr>
		</table>
		</form>
</div>


<script>
$(function(){
    $("input.easyui-textbox-6").textbox({validType: 'maxLength[12]'});//暂时控制
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
    $("input.easyui-textbox-67").textbox({validType: 'maxLength[100]'});

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/sys/attribute/add.htmlx",
		
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
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	
})



</script>
