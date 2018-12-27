<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form method="POST" style="display:none;" id="form1">
	<input type="hidden" name="jsonStr" id="jsonStr"/>
</form>
<div data-options="region:'center',title:''">
	<table  id="dg" ></table>
</div>

<script type="text/javascript">
//初始化
$(function(){
	rowMap = {};
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:false,
		border:true,
		height : 420,
		pagination:true,
		pageSize:10,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'药品编码',width:50,align:'center'},
		        	{field:'productGCode',title:'标准编码',width:100,align:'center'},
		        	{field:'name',title:'药品名称',width:150,align:'center'},
		        	{field:'model',title:'规格',width:100,align:'center'},
		        	{field:'packDesc',title:'包装规格',width:70,align:'center'},
		        	{field:'drug.dosageFormName',title:'剂型',width:50,align:'center',
		        		formatter: function(value,row,index){
		        			return row.drug.dosageFormName;
		        		}},
		        	{field:'company.fullName',title:'厂家',width:200,align:'center',
		        		formatter: function(value,row,index){
		        			return row.company.fullName;
		        		}
		        	},
		        	{field:'authorizeNo',title:'国药准字',width:60,align:'center'},
		        	{field:'regeditNo',title:'注册证号',width:60,align:'center'},
					{field:'ddd',title:'ddd值',width:60,align:'center'}
		        	
		        	
		   		]],
   		onClickRow: function(index,row) {
   			if (rowMap[row.code]) {
   	   			rowMap[row.code] = undefined;
   			} else {
   				rowMap[row.code] = row;
   			}
   		},
	    onLoadSuccess: function (data) {
    		$(data.rows).each(function(i,n) {
    			if(rowMap[n.code]) {
    				$("#dg").datagrid("selectRow",i);
    			}
    		});
	    }
	});
	$.post(" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/getSign.htmlx",function(data) {
		$('#dg').datagrid({
			url:"http://49.4.89.196/medic/menu/productSource/page.htmlx",
			queryParams:data
		})
		$('#dg').datagrid('enableFilter');
	},'json');

	$("#form1").form({
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = $(this).form('validate');
			if (!isValid) {
				top.$.messager.progress('close');
			}

			var rows = $("#dg").datagrid("getSelections");
			var jsonStr = JSON.stringify(rows);
			$.post(" <c:out value='${pageContext.request.contextPath }'/>/dm/product/saveProduct.htmlx", {jsonStr:jsonStr}, function(result) {
					top.$.messager.progress('close');
					if(result.success){
						top.$.modalDialog.openner.datagrid('reload');
						top.$.modalDialog.handler.dialog('close');
						showMsg("处理成功！");
					}else{
						showErr(result.msg);
					}
			},"json");
			
			return false;
		}
	});
	
});
</script>