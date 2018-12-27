<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>姓名：</th>
		   		<th>
		   			<input type="text" name="name" class="easyui-validatebox easyui-textbox" data-options="required:true,validType:'maxLength[6]'">
		   		</th>
		   </tr>
		   <tr>
		   		<th>身份证：</th>
		   		<th>
					<input type="text" name="idCode" class="easyui-validatebox easyui-textbox" data-options="required:true,validType:'maxLength[50]'" >
				</th>
		   </tr>
		   <tr>
		   		<th>性别：</th>
		   		<th>
		   			<label><input type="radio" name="sex" value="男" checked>男 </label>
					<label><input type="radio" name="sex" value="女" >女 </label>
					<label><input type="radio" name="sex" value="其他" >其他 </label>
				</th>
		   </tr>
		   <tr>
		   		<th>手机号：</th>
		   		<th>
					<input type="text" name="mobile" class="easyui-validatebox easyui-textbox" data-options="required:true,validType:'mobile'" >
				</th>
		   </tr>
		   <tr>
		   		<th>送货地址：</th>
		   		<th>
					<input type="text" name="address" class="easyui-validatebox easyui-textbox" data-options="required:true,validType:'maxLength[60]'" >
				</th>
		   </tr>
		   <tr>
		   		<th>健康卡号：</th>
		   		<th>
					<input type="text" name="jkCode" class="easyui-validatebox easyui-textbox"  data-options="validType:'maxLength[50]'" >
				</th>
		   </tr>
		   <tr>
		   		<th>社保卡号：</th>
		   		<th>
					<input type="text" name="sbCode" class="easyui-validatebox easyui-textbox" data-options="validType:'maxLength[50]'" >
				</th>
		   </tr>
		   <tr>
		   		<th>医保卡号：</th>
		   		<th>
					<input type="text" name="ybCode" class="easyui-validatebox easyui-textbox" data-options="validType:'maxLength[50]'" >
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
//初始化
$(function(){

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/patient/add.htmlx",
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