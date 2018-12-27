<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>代号:</th>
		   		<th>
		   			<input type="hidden"  name="pid" id = "pid" >
		   			<input type="hidden" name="id" >
					<input  name="field1" class="easyui-textbox-50" data-options="editable:false,required:true"  >
				</th>
		   </tr>
		   <tr>
		   		<th>显示名称:</th>
		   		<th>
					<input  name="field2" class="easyui-textbox-16" data-options="required:true"  >
				</th>
		   </tr>
		   <tr>
		   		<th>特殊标记:</th>
		   		<th>
					<input  name="field3" class="easyui-textbox-16"  >
				</th>
		   </tr>
		   <tr>
		   		<th>备注:</th>
		   		<th>
					<input  name="field4" class="easyui-textbox-16"  >
				</th>
		   </tr>
		</table>
		</form>
</div>


<script>
$(function(){
    $("input.easyui-textbox-16").textbox({validType: 'maxLength[32]'});//暂时控制
    $("input.easyui-textbox-50").textbox({validType: 'maxLength[50]'});

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/edit.htmlx",
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
