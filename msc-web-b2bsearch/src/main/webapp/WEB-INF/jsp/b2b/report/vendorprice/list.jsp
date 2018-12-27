<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >  

	<div class="search-bar">
		<form id="form1">
		供应商
		<input id="vendorCode" name="vendorCode" /> 
		日期: <input class="easyui-datebox" style="width:110px" id="dateS" data-options="required:true" value="<c:out value='${dateS }'/>">
	    	~ <input class="easyui-datebox" style="width:110px" id="dateE" data-options="required:true" value="<c:out value='${dateE }'/>">
		<a id="btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()" >查询  </a> 	
		</form>
	</div>
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>

</body>
</html>
<script>
function dosearch(){
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#vendorCode").textbox("getValue");
	var para2 = $("#dateS").combo('getValue');
	var para3 = $("#dateE").combo('getValue');
	
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/vendorprice/page.htmlx",
		queryParams:{
			"vendorCode":para1,
			"startDate":para2,
			"endDate":para3
	    }
	}); 
}

//初始化
$(function(){
	$('#vendorCode').combogrid({
		idField:'code',    
		textField:'fullName',
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/page.htmlx",
		queryParams:{
	    	"query['t#isDisabled_I_EQ']": 0,
	    	"query['t#isVendor_I_EQ']": 1
		},
		pagination:true,
		required:true,
		pageSize:10,
		pageNumber:1,
		delay:800,
	    columns: [[
	        {field:'code',title:'供应商编码',width:100},
	        {field:'fullName',title:'供应商名称',width:400}
	    ]],
	    panelWidth:510,
	    panelHeight:310,
	    onClickRow: function(index,row){  
	    	$('#vendorName').textbox("setValue", row.fullName);
		},
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#vendorCode').combogrid('grid').datagrid("reload",{"query['t#fullName_S_LK']":q});
                $('#vendorCode').combogrid("setValue", q);
            }

        }
	});	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		showFooter: true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		pageSize:50,
		pageNumber:1,
		columns:[[
        	{field:'vendorName',title:'供应商',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.segmentMap){
						return row.segmentMap.vendorName;
					}
				}},   	
        	{field:'code',title:'药品编码',width:10,align:'center'}, 	
        	{field:'name',title:'药品名称',width:30,align:'center'},
        	{field:'dosageFormName',title:'剂型',width:10,align:'center'},
        	{field:'model',title:'规格',width:10,align:'center'},
        	{field:'packDesc',title:'包装',width:10,align:'center'},
        	{field:'producerName',title:'厂家',width:30,align:'center'},
        	{field:'startDate',title:'起始日期',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.segmentMap){
						return $.format.date(row.segmentMap.start.createDate, "yyyy-MM-dd HH:mm");
					}
				}},
			{field:'endDate',title:'截止日期',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.segmentMap){
						return $.format.date(row.segmentMap.end.createDate, "yyyy-MM-dd HH:mm");
					}
				}},
			{field:'startPrice',title:'起始价格',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.segmentMap){
						return common.fmoney(row.segmentMap.start.finalPrice);
					}
				}},
			{field:'endPrice',title:'截止价格',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.segmentMap){
						return common.fmoney(row.segmentMap.end.finalPrice);
					}
				}},
			{field:'rate',title:'价格浮动率',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.segmentMap){
						return row.segmentMap.rate;
					}
				}}
   		]]
	});
});


</script>