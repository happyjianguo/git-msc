<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div style="padding: 0px;overflow: hidden;background:red;">
		<table id="dg_contractM" style="border:0px solid red;overflow: hidden;"></table>
	</div>
<script>
//初始化
$(function(){
	
	//datagrid
	$('#dg_contractM').datagrid({
		fitColumns:true,
		striped:false,
		singleSelect:true,
		rownumbers:true,
		border:false,
		height : 169,
		url:" <c:out value='${pageContext.request.contextPath }'/>/home/contractM.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'contract.code',title:'合同编号',width:20,align:'center',
		        		formatter: function(value,row,index){
		        			return row.contract.code;
						}},
					{field:'contract.hospitalName',title:'医院名称',width:20,align:'center',
		        		formatter: function(value,row,index){
		        			return row.contract.hospitalName;
						}},
		        	{field:'product.name',title:'药品名称',width:10,align:'center',
			        		formatter: function(value,row,index){
			        			return row.product.name;
							}},
		        	{field:'contractNum',title:'合同量',width:10,align:'center',
							formatter: function(value,row,index){
								if (row.contractNum){
									return common.fmoney(row.contractNum);
								}
					}},
					{field:'purchasePlanNum',title:'计划量',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.purchasePlanNum){
								return common.fmoney(row.purchasePlanNum);
							}
					}},
		        	{field:'purchaseNum',title:'采购量',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.purchaseNum){
								return common.fmoney(row.purchaseNum);
							}
		        	}},
		        	{field:'deliveryNum',title:'配送量',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.deliveryNum){
								return common.fmoney(row.deliveryNum);
							}
		        	}},
		        	{field:'returnsNum',title:'退货量',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.returnsNum){
								return common.fmoney(row.returnsNum);
							}
		        	}}
		   		]],
				onDblClickRow: function(index,row){
					top.addTab("合同查询 ", "/hospital/contract.htmlx?code="+row.contract.code,null, true);
				},
				onLoadSuccess:function(data){
					var h = (data.rows.length+1) * 25+32;
					if(h < 200){
						h = 200;
					}
					$("#p4").css("height",h);
					$("#dg_contractM").datagrid('resize',{
					       height:h
					})
				}
	});

	
});
</script>