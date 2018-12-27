<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		     <tr>
		   		<th>药品目录编码：</th>
		   		<th>
		   			<input type="text" name="code" class="easyui-validatebox  easyui-textbox-50" readonly/>
		   		</th>
		   		<th>通用名：</th>
		   		<th>
		   			<input type="text" name="genericName" class="easyui-validatebox  easyui-textbox-33"  data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
		   		<th>英文名称：</th>
		   		<th>
		   			<input type="text" name="englishName" class="easyui-validatebox  easyui-textbox-50"   />
		   		</th>
		   		<th>拼音简称：</th>
		   		<th>
		   			<input type="text" name="pinyin" class="easyui-validatebox  easyui-textbox-50"   />
		   		</th>
		   </tr>
		   <tr>			
		   		<th>剂型：</th>
		   		<th>
		   			<input id="dosageForm" name="dosageFormId" />
		   		</th>
		   		<th>中西药分类：</th>
		   		<th>
		   			<input id="drugType" name="drugType"  data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>药品性状：</th>
		   		<th>
		   			<input type="text" name="style" class="easyui-validatebox  easyui-textbox-6"   />
		   		</th>
		   		<th>处方药：</th>
		   		<th>
		   			<input id="prescription" name="prescription"  />
		   		</th>
		   </tr>
		   <tr>
				<th>质量标准类型：</th>
		   		<th>
		   			<input id="qualityType" name="qualityType"  />
		   		</th>
		   		<th>质量标准编号：</th>
		   		<th>
		   			<input type="text" name="qsno" class="easyui-validatebox  easyui-textbox-20"   />
		   		</th>
		   </tr>
		   <tr>
				<th>特殊药品分类：</th>
		   		<th>
		   			<input id="specialDrugType" name="specialDrugType"  />
		   		</th>
		   		<th>抗菌药物：</th>
		   		<th>
		   			<input id="absDrugType" name="absDrugType"  />
		   		</th>
		   </tr>
		   <tr>
		   		<th>药理分类：</th>
		   		<th>
		   			<input id="pharmacologyType" name="pharmacologyType"  data-options="required:true" />
		   		</th>
		   		<th>新编药物学分类：</th>
		   		<th>
		   			<input id="newlyDrugType" name="newlyDrugType"  />
		   		</th>
		   </tr>
		   <tr>
				<th>归档文件序号：</th>
		   		<th>
		   			<input type="text" name="archNo" class="easyui-validatebox  easyui-textbox-20"   />
		   		</th>
		   		<th>备注：</th>
		   		<th>
		   			<input type="text" name="notes" class="easyui-validatebox  easyui-textbox-16"   />
		   		</th>
		   </tr>
		   <tr>
				<th>备用标识：</th>
		   		<th>
		   			<input id="backTag" name="backTag"  />
		   		</th>
				<th>耗材分类：</th>
		   		<th>
		   			<input id="suppliesType" name="suppliesType"  />
		   		</th>
		   </tr>
		   <tr>		
				<th>是否禁用：</th>
		   		<th>
		   			<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
		   		</th>
		   		<th></th>
		   		<th>
		   		</th>
		   </tr>
		</table>
		<input type="hidden" name="id" value=""  >
	</form>


</div>



<script>

//初始化
$(function(){

    $("input.easyui-textbox-6").textbox({validType: 'maxLength[12]'});//暂时控制
    $("input.easyui-textbox-16").textbox({validType: 'maxLength[32]'});
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
    $("input.easyui-textbox-33").textbox({validType: 'maxLength[60]'});
    $("input.easyui-textbox-50").textbox({validType: 'maxLength[50]'});

	
	$('#dosageForm').combotree({
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/dosageForm/list.htmlx",
	    valueField:'id',    
	    textField:'name', 
		parentField:'parentId',
    	pageSize:1000,
		state:"closed",
	    queryParams:{
	    	"query['t#isDisabled_I_NE']": 1
		},
		required: true,
		editable:false,
		onLoadSuccess:function() {
			$(this).tree("collapseAll");
		}
	});
	
	$('#prescription').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "drug_prescription"
		},
		editable:false
	}).combobox("initClear"); 
	$('#drugType').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
		state:"closed",
	    queryParams:{
	    	"attributeNo": "drug_drugType"
		},
		required: true,
		editable:false,
		onLoadSuccess:function() {
			$(this).tree("collapseAll");
		}
	}).combobox("initClear"); 
	$('#backTag').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "drug_backTag"
		},
		editable:false
	}).combobox("initClear"); 
	$('#qualityType').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "drug_qualityType"
		},
		editable:false
	}).combobox("initClear"); 
	$('#specialDrugType').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx",
		queryParams:{
			"attributeNo":"drug_type",
			"field1":"03"
		}
	}).combobox("initClear"); 
	$('#absDrugType').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx",
		queryParams:{
			"attributeNo":"drug_type",
			"field1":"02"
		}
	}).combobox("initClear"); 
	$('#pharmacologyType').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx",
		queryParams:{
			"attributeNo":"drug_type",
			"field1":"06"
		}
	}).combobox("initClear"); 
	$('#newlyDrugType').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx",
		queryParams:{
			"attributeNo":"drug_type",
			"field1":"04"
		}
	}).combobox("initClear"); 
	$('#suppliesType').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx",
		queryParams:{
			"attributeNo":"drug_type",
			"field1":"07"
		}
	}).combobox("initClear"); 
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/drug/add.htmlx",
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