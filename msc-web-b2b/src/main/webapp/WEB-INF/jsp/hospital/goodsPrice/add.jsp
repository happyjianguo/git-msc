<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div class="open-dg" >
	<table id="dg"></table>
</div>



<script>
var goodsId = '<c:out value='${goodsId}'/>';
var productCode = '<c:out value='${productCode}'/>';

//初始化
$(function(){

	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height :  460,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goods/pageByProduct.htmlx",
		queryParams:{
			"productCode":productCode
		},
		columns:[[
		        	{field:'vendorName',title:'供应商',width:30,align:'center'},
		        	{field:'finalPrice',title:'价格',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.finalPrice){
								return common.fmoney(row.finalPrice);
							}
						}},
					{field:'beginDate',title:'生效日期',width:30,align:'center',
			        		formatter: function(value,row,index){
			        			var beginDate = "";
			        			
			        			if(row.beginDate){
			        				beginDate = $.format.date(row.beginDate,"yyyy-MM-dd");
			        			}
			        			
								return beginDate ;
					}},{field:'hospitalCode',title:'价格类型',width:15,align:'center',
		        		formatter: function(value,row,index){
		        			if(row.hospitalCode){
		        				return "指定价格";
		        			}else{
		        				return "统一价格";
		        			}
					}}
		   		]],
   		onClickRow:function(index,row){
   			var selobjs = $('#dg').datagrid('getSelections');
   			var isSelected = 0;
   			$.each(selobjs,function(index){
   				if(this.ID == row.ID){
   					isSelected = 1;
   					return false;
   				}
   			});

   			if(isSelected){
   				addAjax(row);
   			}else{
   				delAjax(row);
   			}
   		},
   		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				if(row.selected){
					$('#dg').datagrid('selectRow', index);
				}
			}); 
		}
		
	});

	
	
});



//搜索
function dosearch(){
	
	var para1 = $("#code").textbox("getValue");
	var para2 = $("#name").textbox("getValue");
	$('#dg').datagrid('load',{
		"code":para1,
		"name":para2
	}); 
}


//=============ajax===============


function addAjax(row){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsPrice/add.htmlx",
		data:{
			"goodsId":goodsId,
			"productCode":productCode,
			"vendorCode":row.vendorCode,
			"finalPrice":row.finalPrice,
			"biddingPrice":row.biddingPrice,
			"beginDate":row.beginDate
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("新增成功！");
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			//top.$.modalDialog.openner.datagrid('reload');
			//top.$.modalDialog.handler.dialog('close');
			showErr("出错，请刷新重新操作");
		}
	});	
}

function delAjax(row){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsPrice/del.htmlx",
		data:{
			"productCode":productCode,
			"vendorCode":row.vendorCode,
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("删除成功！");
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			//top.$.modalDialog.openner.datagrid('reload');
			//top.$.modalDialog.handler.dialog('close');
			showErr("出错，请刷新重新操作");
		}
	});	
}


</script>