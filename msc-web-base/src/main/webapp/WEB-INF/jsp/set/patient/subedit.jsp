<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>地址:</th>
		   		<th>
					<input type="text" name="name" class="easyui-textbox-33" data-options="required:true"  />
					<input type="hidden" id="pid" name="patient.id"/>
		   			<input type="hidden" name="id"/>
				</th>
		   </tr>
		   <tr>
		   		<th>联系人:</th>
		   		<th>
					<input type="text" name="contact" class="easyui-textbox-3" />
				</th>
		   </tr>
		   <tr>
		   		<th>联系电话:</th>
		   		<th>
					<input type="text" name="phone" class="easyui-textbox-20" />
				</th>
		   </tr>
		   <tr>
		   		<th>默认收货点:</th>
		   		<th>
					<label><input type="radio" name="isDefault" value="1">是 </label>
					<label><input type="radio" name="isDefault" value="0" checked>否 </label>
				</th>
		   </tr>
		   <tr>
		   		<th>是否禁用:</th>
		   		<th>
					<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
				</th>
		   </tr>
		</table>
	</form>
</div>


<script>
$(function(){
	$('#type').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/getTypeSelect.htmlx",
	    valueField:'id',    
	    textField:'name', 
	    panelHeight:50,
	    queryParams:{
		},
		required: true,
		editable:false
	});
    $("input.easyui-textbox-3").textbox({validType: 'maxLength[6]'});//暂时控制
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
    $("input.easyui-textbox-33").textbox({validType: 'maxLength[60]'});

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/address/edit.htmlx",
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
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	
})



</script>
