<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div style="padding: 0px;overflow: hidden;background:red;">
		<table id="dg_contractH" style="border:0px solid red;overflow: hidden;"></table>
	</div>
<script>
//初始化
$(function(){
	
	//datagrid
	$('#dg_contractH').datagrid({
		fitColumns:true,
		striped:false,
		singleSelect:true,
		rownumbers:true,
		border:false,
		height : 169,
		url:" <c:out value='${pageContext.request.contextPath }'/>/home/contractH.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'contract.code',title:'合同编号',width:20,align:'center',
		        		formatter: function(value,row,index){
		        			return row.contract.code;
						}},
		        	{field:'product.name',title:'药品名称',width:10,align:'center',
			        		formatter: function(value,row,index){
			        			return row.product.name;
							}},
		        	{field:'contractNum',title:'合同量',width:10,align:'center',
							formatter: function(value,row,index){
								if (row.contractNum>0){
									return row.contractNum;
								}
					}},
					{field:'purchasePlanNum',title:'计划量',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.purchasePlanNum>0){
								return row.purchasePlanNum;
							}
					}},
		        	{field:'purchaseNum',title:'采购量',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.purchaseNum>0){
								return row.purchaseNum;
							}
		        	}},
		        	{field:'deliveryNum',title:'配送量',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.deliveryNum>0){
								return row.deliveryNum;
							}
		        	}},
		        	{field:'returnsNum',title:'退货量',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.returnsNum>0){
								return row.returnsNum;
							}
		        	}}
		   		]],
				onDblClickRow: function(index,row){
					top.addTab("合同查询 ", "/hospital/contract.htmlx?code="+row.contract.code,null, true);
				},
				onLoadSuccess:function(data){
					var h = (data.rows.length+1) * 25+32;
					if(data.rows.length >=5){
						$("#p6").css("height",h);
						$("#dg_contractH").datagrid('resize',{
						       height:h
						})
					}
				}
	});

	
});
</script>