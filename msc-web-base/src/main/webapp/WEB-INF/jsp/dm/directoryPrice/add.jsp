<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>遴选药品：</th>
		   		<th>
		   			<input type="text" name="directory.id" id="genericName" />
		   		</th>
		   </tr>
		   <tr>
				<th>月份：</th>
		   		<th>
		   			<input type="text" name="month" id="month" class="easyui-validatebox  easyui-textbox" data-options="required:true" />
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
	</form>


</div>



<script>

//初始化
$(function(){
	$('#genericName').combogrid({
		idField:'id',    
		textField:'genericName', 
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/directory/page.htmlx",
	    queryParams:{
		},
		pagination : true,
		pageSize : 10,
		pageNumber : 1,
		width:'auto',
		panelWidth:360,
		required:true,
		columns : [ [
			        {field:'genericName',title:'通用名',width:80},
			        {field:'dosageFormName',title:'剂型',width:80},
			        {field:'model',title:'规格',width:80},
			        {field:'minUnit',title:'最小使用单位',width:80}]],//panelHeight:'auto',
		keyHandler : {
			query : function(q) {
				//动态搜索
				$('#genericName').combogrid('grid').datagrid("reload",{
									"query['t#genericName_S_LK']" : q
								});
				$('#genericName').combogrid("setValue", q);
			}

		},
		onSelect: function(data){
		}
	}).combobox("initClear"); 
	$('#genericName').combogrid('grid').datagrid('getPager').pagination({
		showPageList : false,
		showRefresh : false,
		displayMsg : "共{total}记录"
	});
	
	$('#areaCode').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "areaCode"
		},
		required:true,
		editable:false,
		onSelect: function(data){
			$("#areaName").val(data.field2);
		}
	}).combobox("initClear"); 
	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/directoryPrice/add.htmlx",
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