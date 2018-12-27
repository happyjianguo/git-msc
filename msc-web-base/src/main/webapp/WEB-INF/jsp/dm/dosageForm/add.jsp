<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>剂型编号：</th>
		   		<th>
		   			<input type="text" name="code" class="easyui-validatebox  easyui-textbox-20"  data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>剂型名称：</th>
		   		<th>
		   			<input type="text" name="name" class="easyui-validatebox  easyui-textbox-16"  data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>英文名称：</th>
		   		<th>
		   			<input type="text" name="englishName" class="easyui-validatebox  easyui-textbox-50"   />
		   		</th>
		   </tr>
		   <tr>
				<th>上层分类：</th>
		   		<th>
		   			<input id="parentId" name="parentId" style="width:150px;" />
		   		</th>
		   </tr>
		   <tr>
				<th>备注：</th>
		   		<th>
		   			<input type="text" name="notes" class="easyui-validatebox  easyui-textbox-16"   />
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
		<input type="hidden" name="id" value=""  >
	</form>


</div>



<script>

//初始化
$(function(){
	$('#parentId').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/dosageForm/list.htmlx",
		required: false
	}).combobox("initClear");

    $("input.easyui-textbox-16").textbox({validType: 'maxLength[32]'});//暂时控制
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
    $("input.easyui-textbox-50").textbox({validType: 'maxLength[50]'});


	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/dosageForm/add.htmlx",
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
				top.$.modalDialog.openner.treegrid('reload');
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
</script>