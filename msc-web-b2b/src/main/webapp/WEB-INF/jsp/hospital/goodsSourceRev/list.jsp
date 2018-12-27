<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>

<body class="easyui-layout">
	<div data-options="region:'center'" style="height:100%;">
		<div style="padding:5px">
				药品名称：<input id="productName" class="easyui-validatebox  easyui-textbox" style="width:80px;"/>
				<a  class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn"  >查询  </a>
            <shiro:hasPermission name="hospital:goodsSourceRev:exportExcel2">
                <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls'" onclick="exportexcel()">导出</a>
            </shiro:hasPermission>
			</div>
		<table id="dg" ></table>
	</div>
	
<script>
$(function(){
	//用户组
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceRev/sourcePage.htmlx",
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height:$(this).height()-35,
		pageSize:10,
		pageNumber:1,
		pagination:true,
		idField:'id',
		columns:[[
					{field:'PRODUCTNAME',title:'医院药品名称',width:100,align:'center'},
					{field:'PRODUCTNAME0',title:'监管药品名称',width:150,align:'center'},
					{field:'MODEL',title:'医院规格',width:100,align:'center'},
					{field:'MODEL0',title:'监管规格',width:100,align:'center'},
					{field:'PACKDESC0',title:'监管包装规格',width:100,align:'center'},
					{field:'PRODUCERNAME',title:'医院厂家',width:150,align:'center'},
					{field:'PRODUCERNAME0',title:'监管药品厂家',width:150,align:'center'},
					{field:'CONVERTRATIO0',title:'医院转换比',width:100,align:'center'},
					{field:'CONVERTRATIO1',title:'监管转换比',width:100,align:'center'},
					{field:'UNITNAME',title:'医院单位',width:50,align:'center'},
					{field:'AUTHORIZENO',title:'医院国药准字',width:100,align:'center'},
					{field:'AUTHORIZENO0',title:'监管国药准字',width:100,align:'center',
						formatter: function(value,row,index) {
							return (value==""||!value)?row.IMPORTFILENO:row.AUTHORIZENO0;
						}
					},
					{field:'STANDARDCODE',title:'医院本位码',width:100,align:'center'},
					{field:'STANDARDCODE0',title:'监管本位码',width:100,align:'center'},
					{field:'YJCODE',title:'医院药交ID',width:100,align:'center'},
					{field:'PRODUCTCODE',title:'监管药品编码',width:150,align:'center'}
		   		]],
			<%--toolbar: [{--%>
					<%--iconCls: 'icon-export',--%>
					<%--text:"导出",--%>
					<%--handler: function() {--%>
						<%--window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceRev/exportExcel.htmlx?query[status_L_EQ]=99");--%>
					<%--}--%>
			<%--}],--%>
			onLoadSuccess:function(data){
				$.each(data.rows,function(index,row){
					$('#dg2').datagrid('beginEdit', index);
				}); 
				$('#dg2').datagrid('doCellTip',{delay:500}); 
			},
			queryParams:{
				"query[t#status_L_EQ]":2
			}
	});
	$("#query_btn").click(function() {
		$('#dg').datagrid("load",{
			"query[t#status_L_EQ]":2,
			"query[productName_S_LK]":$("#productName").textbox("getValue")
		});
	});
});
function exportexcel(){
    window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceRev/exportExcel.htmlx?query[status_L_EQ]=99");
}

</script>
</body>
</html>