<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="single-dg">
	<table id="dgDetail"></table>
</div>



<script>
//初始化
$(function() {
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  455,
		pagination:true,
		url:"${pageContext.request.contextPath }/b2b/report/productPriceMdf/mxpage.htmlx",
		queryParams:{
			"query[t#productCode_S_EQ]":${productCode },
			"query[t#gpoCode_S_EQ]":${gpoCode },
		},
		pageSize:20,
		pageNumber:1,
		columns:[[
            {field:'createDate',title:'变动日期',width:10,align:'center',sortable:true,
                formatter: function(value,row,index){
                    if (row.createDate){
                        return $.format.date(row.createDate,"yyyy-MM-dd");
                    }
                }
            },
            {field:'productCode',title:'药品编码',width:10,align:'center'},
        	{field:'productName',title:'药品名称',width:10,align:'center'},
        	{field:'gpoName',title:'GPO名称',width:10,align:'center'},
        	{field:'vendorName',title:'供应商名称',width:10,align:'center'},
        	{field:'finalPrice',title:'价格',width:10,align:'center'}
   		]]
	});
});
</script>


