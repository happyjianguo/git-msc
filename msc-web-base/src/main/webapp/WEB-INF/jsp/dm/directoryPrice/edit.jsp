<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>遴选药品：</th>
		   		<th>
		   			<c:out value='${directoryPrice.directory.genericName }'/>
		   		</th>
		   </tr>
		   <tr>
				<th>月份：</th>
		   		<th>
		   			<input type="text" name="rcDosageFormName" id="rcDosageFormName" class="easyui-validatebox  easyui-textbox" data-options="required:true" />
		   		</th>
		   </tr>
		    <tr>
				<th>地区：</th>
		   		<th>
		   			<input type="text" name="areaCode" id="areaCode"  />
		   			<input type="hidden" name="areaName" id="areaName"  />
		   		</th>
		   </tr>
		   <tr>
				<th>价格：</th>
		   		<th>
		   			<input type="text" name="price" id="price" class="easyui-validatebox  easyui-textbox" data-options="required:true" />
		   		</th>
		   </tr>
		</table>
		<input type="text" name="id" value="<c:out value='${directoryPrice.id }'/>"  />
		<input type="text" name="directoryId" value="<c:out value='${directoryPrice.directory.id }'/>"  />
	</form>


</div>



<script>

//初始化
$(function(){
	
	
	$('#areaCode').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "areaCode"
		},
		editable:false,
		onSelect: function(data){
			$("#areaName").val(data.field2);
		}
	}).combobox("initClear"); 
	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/directoryPrice/edit.htmlx",
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