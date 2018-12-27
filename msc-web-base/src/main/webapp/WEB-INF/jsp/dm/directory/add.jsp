<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>通用名：</th>
		   		<th>
		   			<input type="text" name="genericName" id="genericName" class="easyui-validatebox  easyui-textbox-33" data-options="required:true"/>
		   		</th>
		   </tr>
		   <tr>
				<th>推荐剂型名称：</th>
		   		<th>
		   			<input type="text" name="rcDosageFormName" id="rcDosageFormName" class="easyui-validatebox  easyui-textbox-16" data-options="required:true" />
		   		</th>
		   </tr>
		    <tr>
				<th>剂型名称：</th>
		   		<th>
		   			<input type="text" name="dosageFormName" id="dosageFormName" class="easyui-validatebox  easyui-textbox-16" data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>规格：</th>
		   		<th>
		   			<input type="text" name="model" id="model" class="easyui-validatebox  easyui-textbox-66" data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>质量层次：</th>
		   		<th>
		   			<input id="qualityLevel" name="qualityLevel" style="width:150px;"/>
		   			<input type="hidden"  id="qualityLevelCode" name="qualityLevelCode"/>
		   		</th>
		   </tr>
		   <tr>
				<th>最小使用单位：</th>
		   		<th>
		   			<input id="minUnit" name="minUnit" style="width:150px;" />
		   		</th>
		   </tr>
		   <tr>
				<th>生产厂家：</th>
		   		<th>
		   			<input id="producerNames" name="producerNames" style="width:150px;"  class="easyui-validatebox  easyui-textbox-66" data-options="required:true" />
		   		</th>
		   </tr>
		    <tr>
				<th>批次：</th>
		   		<th>
		   			<input id="batch" name="batch" style="width:150px;" class="easyui-validatebox  easyui-textbox-10" data-options="required:true" />
		   		</th>
		   </tr>
		   
		    <tr>
				<th>备注：</th>
		   		<th>
		   			<input id="note" name="note" style="width:150px;"  class="easyui-validatebox  easyui-textbox-50" data-options="required:true" />
		   		</th>
		   </tr>
		</table>
		<input type="hidden" name="id" value=""  >
	</form>


</div>



<script>

//初始化
$(function(){

    $("input.easyui-textbox-33").textbox({validType: 'maxLength[60]'});//暂时控制
    $("input.easyui-textbox-10").textbox({validType: 'maxLength[10]'});
    $("input.easyui-textbox-16").textbox({validType: 'maxLength[32]'});
    $("input.easyui-textbox-50").textbox({validType: 'maxLength[50]'});
    $("input.easyui-textbox-66").textbox({validType: 'maxLength[110]'});

	$('#minUnit').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'field2',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "packType",
		},
		editable:false
	});
	
	$('#qualityLevel').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'field2',    
	    textField:'field2',
	    queryParams:{
	    	"attributeNo": "product_qualityLevel"
		},
		editable:false,
		onSelect:function(a,b){
			$("#qualityLevelCode").val(a.field1);
		}
	}).combobox("initClear"); 
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/directory/add.htmlx",
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