<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>代号:</th>
		   		<th>
		   			<input type="hidden"  name="pid" id = "pid" >
					<input type="text" name="field1" class="easyui-textbox" data-options="required:true"  >
				</th>
		   </tr>
		   <tr>
		   		<th>显示名称:</th>
		   		<th>
					<input type="text" name="field2" class="easyui-textbox" data-options="required:true"  >
				</th>
		   </tr>
		   <tr>
		   		<th>特殊标记:</th>
		   		<th>
					<input type="text" name="field3" class="easyui-textbox"  >
				</th>
		   </tr>
		   <tr>
		   		<th>备注:</th>
		   		<th>
					<input type="text" name="field4" class="easyui-textbox"  >
				</th>
		   </tr>
		</table>
		</form>
</div>


<script>
$(function(){
	//$("input").textbox({validType:'length[0,100]'});
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/add.htmlx",
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
