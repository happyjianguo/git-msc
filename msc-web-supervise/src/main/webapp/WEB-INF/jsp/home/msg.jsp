<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div style="padding: 0px;overflow: hidden;background:red;">
		<table id="dg_msg" style="border:0px solid red;overflow: hidden;"></table>
	</div>
<script>
//初始化
$(function(){
	//datagrid
	$('#dg_msg').datagrid({
		fitColumns:true,
		striped:false,
		singleSelect:true,
		rownumbers:true,
		border:false,
		height : 169,
		url:" <c:out value='${pageContext.request.contextPath }'/>/sys/message/recvpageByOrg.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'msg.title',title:'标题',width:30,align:'left',
		        		formatter: function(value,row,index){
							if (row.msg){
								return row.msg.title;
							}
						}},
					{field:'msg.orgName',title:'发送者',width:10,align:'center',
			        		formatter: function(value,row,index){
								if (row.msg){
									return row.msg.orgName;
								}
							}},	
		        	{field:'createDate',title:'日期',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.createDate){
								return $.format.date(row.createDate,"yyyy-MM-dd");
							}
						}
					}, 
		        		
		   		]],
				onDblClickRow: function(index,row){
					top.addTab("订单查询 ", row.msg.attach,null, true);
				},
				onLoadSuccess:function(data){
					var h = (data.rows.length+1) * 25+32;
					if(data.rows.length >=5){
						$("#p3").css("height",h);
						$("#dg_msg").datagrid('resize',{
						       height:h
						})
					}
				}
	});

	
});
</script>