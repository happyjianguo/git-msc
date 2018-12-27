<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="" />

<body class="easyui-layout" >

	<div data-options="region:'north',title:''">
		<div style="padding:3px 3px;">
			通用名称：<input id="productName" class="easyui-textbox"/>
			厂家：<input id="producerName" class="easyui-textbox"/>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn"  >查询</a>
		</div>
	</div>
	<div data-options="region:'center',title:''">
		<table  id="dg" ></table>
	</div>
</body>
</html>

<script>
//初始化
$(function(){
    function loader(param,success,error){
    	if (!param["query[productName]"]) {
    		param["query[productName]"] = "";
    	}
    	if (!param["query[producerName]"]) {
    		param["query[producerName]"] = "";
    	}
    	if (param["query[productName]"]!="" || param["query[producerName]"]!="") {
	        $.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productCloud/cloud.htmlx",
	            dataType: 'jsonp',
				type:"POST",
	            data: param,
	            success: function(data){
	                success(data);
	            },
	            error: function(){
	                error.apply(this, arguments);
	            }
	        });
    	} else {
    		 var items = $.map({}, function(item,index){
                 return {
                     id: index,
                     name: item
                 };
             });
             success(items);
    	}
    }
	//datagrid
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : "100%",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		loader: loader,
		columns:[[
		        	{field:'DRUG_CODE',title:'药品编码',width:100,align:'center'},
		        	{field:'PRODUCT_CODE',title:'产品code',width:100,align:'center'},
		        	{field:'DRUG_NAME',title:'药品名称',width:100,align:'center'},
		        	{field:'TRADE_NAME',title:'商品名',width:100,align:'center'},
		        	{field:'SPEC',title:'药品规格  ',width:100,align:'center'},
		        	{field:'USE_UNIT',title:'药品单位  ',width:100,align:'center'},
		        	{field:'WRAP_SPEC',title:'包装规格',width:100,align:'center'},
		        	{field:'DOSEAGE_FROM',title:'药品剂型',width:100,align:'center'},
		        	{field:'MANUFACTURE_NAME',title:'生产企业',width:100,align:'center'},
		        	{field:'INJECTION_FLAG',title:'针剂标志',width:100,align:'center'},
					{field:'CAT_CATEGORY1',title:'药品性质 （中药、西药）',width:100,align:'center'},
					{field:'STAND_RATE',title:'单位转换比',width:100,align:'center'},
					{field:'USE_UNIT',title:'最小制剂单位',width:100,align:'center'},
					{field:'NATIONAL_BASIC_DRUG',title:'基本药物标志    ',width:100,align:'center'},
					{field:'PRESCRIPTION',title:'处方药标志',width:100,align:'center'},
					{field:'ENABLE_FLAG',title:'在用标志',width:100,align:'center'},
					{field:'PERMIT_NUMBER',title:'注册证号或批准文号',width:100,align:'center'},
					{field:'ANTIBACTERIALS_FLAG',title:'抗菌药物标志',width:100,align:'center'},
					{field:'ATC_DDD',title:'抗菌药DDD值',width:100,align:'center'},
					{field:'BULLETIN_DOSEAGE_NAME',title:'医保剂型归类',width:100,align:'center'},
					{field:'MEDICARE_TYPE',title:'医保类别',width:100,align:'center'},
					{field:'DRUG_CATEGORY',title:'药理分类1',width:100,align:'center'},
					{field:'MIDDLE_CATEGORY',title:'药理分类2',width:100,align:'center'},
					{field:'SMALL_CATEGORY',title:'药理分类3',width:100,align:'center'},
					{field:'FOURTH_CATEGORY',title:'药理分类4',width:100,align:'center'},
					{field:'SPECIAL_MEDI',title:'特殊药品分类',width:100,align:'center'},
		        	{field:'ATC_CODE',title:'atc编码',width:100,align:'center'},
		        	{field:'ATC_NAME',title:'atc通用名',width:100,align:'center'},
		        	{field:'MEDICARE_NAME',title:'医保通用名',width:100,align:'center'}
		   		]],
		onLoadSuccess:function(data) {
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});
	$("#query_btn").click(function() {
		$('#dg').datagrid("load",{
			"query[productName]":$("#productName").textbox("getValue"),
			"query[producerName]":$("#producerName").textbox("getValue")
		}); 
		
	});
});


</script>