<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		 <tr>
				<th>报量项目：</th>
		   		<th>
		   			<input type="text" name="project.id" id="projectId"  />
		   		</th>
		   </tr>
		   <tr>
				<th>通用名：</th>
		   		<th>
		   			<input type="text" name="genericName" id="genericName" class="easyui-validatebox  easyui-textbox" data-options="required:true"/>
		   		</th>
		   </tr>
		   <tr>
				<th>剂型名称：</th>
		   		<th>
		   			<input type="text" name="dosageFormName" id="dosageFormName" class="easyui-validatebox  easyui-textbox" data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>规格：</th>
		   		<th>
		   			<input type="text" name="model" id="model" class="easyui-validatebox  easyui-textbox" data-options="required:true" />
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
				<th>批次：</th>
		   		<th>
		   			<input id="batch" name="batch" />
		   		</th>
		   </tr>
		   
		</table>
		<input type="hidden" name="id" value=""  >
	</form>


</div>



<script>

//初始化
$(function(){

	$('#minUnit').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'field2',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "packType"
		},
		editable:false
	});
	//下拉
	$('#projectId').combogrid({
		idField:'id',    
		textField:'name',
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/project/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
	    columns: [[
	        {field:'code',title:'项目编码',width:100},
	        {field:'name',title:'项目名称',width:180},
	        {field:'type',title:'项目类型',width:60,
				formatter: function(value,row,index){
					if (value == "base"){
						return "基药类型";
					} else {
						return "非基药类型";
					}
				}}
	    ]],
	    panelWidth:360,
		required: true,
		delay:800,
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#projectId').combogrid('grid').datagrid("reload",{"query['t#name_S_LK']":q});
                $('#projectId').combogrid("setValue", q);
            }

        }
	});
	$('#projectId').combogrid('grid').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
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
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/projectDetail/add.htmlx",
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