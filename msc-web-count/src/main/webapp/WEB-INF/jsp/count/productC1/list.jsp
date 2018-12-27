<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
	<div id='tb' class="search-bar">
		年月：
		<input  style="width:100px" id="month"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	药品名称：
	 	<input id="productName" class="easyui-textbox" />
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch()">查询</a>
	</div>
	<div class="single-dg">
			<table id="dg" ></table>
		</div>
</body>
</html>

<script>
//搜索
function dosearch(){
	var month = $("#month").datebox('getValue');
	var productName = $('#productName').textbox('getValue');
	var data = {
		"query['t#month_S_EQ']":month,
		"query['t#product.name_S_LK']":productName,
	};
	$('#dg').datagrid('load',data);
}


//初始化
$(function(){
	
	easyuiMonth($("#month"));
	
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height()-4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/productC1/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb',
		columns:[[						
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'product.code',title:'药品编码',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.code;
							}
					}},
		        	{field:'product.name',title:'药品名称',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.name;
							}
					}},
		        	{field:'product.dosageFormName',title:'剂型',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.dosageFormName;
							}
					}},
		        	{field:'product.model',title:'规格',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.model;
							}
					}},
		        	{field:'product.packDesc',title:'包装',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.packDesc;
							}
					}},
		        	{field:'product.producerName',title:'厂家',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.producerName;
							}
					}},
		        	{field:'purchaseNum',title:'交易数量',width:10,align:'center',sortable:"true"},
		        	{field:'purchaseSum',title:'交易金额',width:10,align:'center',sortable:"true"}
		   		]]
		 
	});
		$('#dg').datagrid('enableFilter', 
			[{
		        field:'purchaseNum',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'purchaseSum',
		        type:'text',
		    	isDisabled:1
		    }]
		);
		
	
});



//=============ajax===============
	

</script>