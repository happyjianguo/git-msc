<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
			<tr>
		   		<th>编码:</th>
		   		<th>
					<input type="text" name="code" class="easyui-validatebox  easyui-textbox-100" data-options="required:true" style="width:200px" >
				</th>
		   </tr>
		   <tr>
		   		<th>名称:</th>
		   		<th>
					<input type="text" name="name" class="easyui-validatebox  easyui-textbox-80" data-options="required:true" style="width:200px" >
				</th>
		   </tr>
		</table>
	</form>
</div>



<script>
//初始化
$(function(){
    $("input.easyui-textbox-80").textbox({validType: 'maxLength[80]'});//暂时控制
    $("input.easyui-textbox-100").textbox({validType: 'maxLength[100]'});

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/sickness/add.htmlx",
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
				showMsg("新增成功！");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>