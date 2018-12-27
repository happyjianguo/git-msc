<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div style="padding: 0px;">
		<table id="dg_inout" style="border:0px solid red"></table>
	</div>
<script>
//初始化
$(function(){
	//datagrid
	$('#dg_inout').datagrid({
		fitColumns:true,
		striped:false,
		singleSelect:false,
		rownumbers:true,
		border:false,
		height : 169,
		url:" <c:out value='${pageContext.request.contextPath }'/>/home/inoutpage.htmlx",
		pagination:false,
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'receiveDate',title:'配送日期',width:30,align:'center',
						formatter: function(value,row,index){
							if (row.receiveDate){
								return $.format.date(row.receiveDate,"yyyy-MM-dd HH:mm:ss");
							}
					}},
		        	{field:'code',title:'配送单编号',width:30,align:'center'},
		        	{field:'num',title:'数量',width:30,align:'center'},
		        	{field:'status',title:'状态',width:20,align:'center',
		        		formatter: function(value,row,index){
							return "待收货";
						}
		        	}
		        		
		   		]],
		onDblClickRow: function(index,row){
			top.addTab("收货","/hospital/delivery/list.htmlx?code="+row.code, null, true);
		},
   		onLoadSuccess: function(data){
   			var h = (data.rows.length+1) * 25+32;
			if(data.rows.length >=5){
				$("#p4").css("height",h);
				$("#dg_inout").datagrid('resize',{
				       height:h
				})
			}
		}
	});
	
});
</script>