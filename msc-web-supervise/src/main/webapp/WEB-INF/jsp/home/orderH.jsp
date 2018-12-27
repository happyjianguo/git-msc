<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div style="padding: 0px;overflow: hidden;background:red;">
		<table id="dg_order" style="border:0px solid red;overflow: hidden;"></table>
	</div>
<script>
//初始化
$(function(){
	
	//datagrid
	$('#dg_order').datagrid({
		fitColumns:true,
		striped:false,
		singleSelect:true,
		rownumbers:true,
		border:false,
		height : 169,
		url:" <c:out value='${pageContext.request.contextPath }'/>/home/statusHpage.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'code',title:'订单编号',width:20,align:'center'},
		        	{field:'num',title:'采购数量',width:10,align:'center'},
		        	{field:'deliveryNum',title:'配送数量',width:10,align:'center'},
					{field:'inOutBoundNum',title:'入库数量',width:10,align:'center'},
		        	{field:'status',title:'状态',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			var s = row.status;
		        			if(s == "effect" ){
		        				return "已生效";
		        			}else if(s == "sending" ){
		        				return "配送中";
		        			}else return s;
						}
		        	}
		   		]],
				onDblClickRow: function(index,row){
					top.addTab("订单查询 ", "/b2b/monitor/purchaseOrder.htmlx?code="+row.code,null, true);
				},
				onLoadSuccess:function(data){
					var h = (data.rows.length+1) * 25+32;
					if(data.rows.length >=5){
						$("#p5").css("height",h);
						$("#dg_order").datagrid('resize',{
						       height:h
						})
					}
				}
	});

	
});
</script>