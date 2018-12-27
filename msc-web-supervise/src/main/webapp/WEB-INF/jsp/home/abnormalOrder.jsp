<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />

<div style="padding: 0px;">
		<table id="dg_ab" style="border:0px solid red"></table>
	</div>
<script>
//初始化
$(function(){
	//datagrid
	$('#dg_ab').datagrid({
		fitColumns:true,
		striped:false,
		singleSelect:false,
		rownumbers:true,
		border:false,
		height : 169,
		url:" <c:out value='${pageContext.request.contextPath }'/>/home/abnormalOrder.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'code',title:'订单编号',width:50,align:'center'},
		        	{field:'DATEA',title:'违约情况',width:50,align:'center',
		        		formatter: function(value,row,index){
		        			var s = "";
		        			if(row.isNumContract)
		        				s += "数量违约;";
		        			if(row.isTimeContract)
		        				s += "时间违约;";
		        			if(row.isManyDeliveryContract)
		        				s += "多次配送违约;";
		        			if(row.isReturnsContract)
		        				s += "退货违约;";
							return s;
						}
		        	},
		        	{field:'hospitalName',title:'医疗机构',width:50,align:'center'},
		        	{field:'vendorName',title:'供应商',width:50,align:'center'},
		        	{field:'orderDate',title:'订单日期',width:30,align:'center',
						formatter: function(value,row,index){
							if (row.orderDate){
								return $.format.date(row.orderDate,"yyyy-MM-dd");
							}
						}
					}
		        		
		   		]],
		onDblClickRow: function(index,row){
			top.addTab("订单查询 ", "/b2b/monitor/purchaseOrder.htmlx?code="+row.code, null, true);
		},
		onLoadSuccess:function(data){
			var h = (data.rows.length+1) * 25+32;
			if(data.rows.length >=5){
				$("#p3").css("height",h);
				$("#dg_ab").datagrid('resize',{
				       height:h
				})
			}
		}
	});


	$(document).on("click",".monitor_open_a",function(event) {
		top.addTab("订单查询 ", "/b2b/monitor/purchaseOrder.htmlx?code="+$(this).text() , null, true);
	});
	
});
</script>