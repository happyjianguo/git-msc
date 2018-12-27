<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单计划列表" />
<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;" >
	 <div id="tb" class="search-bar" >

		订单计划日期: <input class="easyui-datebox" style="width:110px" id="startDate">
        ~ <input class="easyui-datebox" style="width:110px" id="toDate">

        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
        <span class="datagrid-btn-separator split-line" ></span>
		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" onclick="checkFunc()">审单确认</a>
  		<span class="datagrid-btn-separator split-line" ></span>
  		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
    </div>
	<div>
		<table  id="dg"></table>
	</div>
	
</div>

<div data-options="region:'center',title:''"   class="my-center" >
    <table id="dgDetail"></table>
</div>
</body>
</html>
<script>
var isFilter=0;
function filterFunc(){
	if(isFilter == 1) return;
	isFilter=1;
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', 
			[{
		        field:'orderDate',
		        type:'datebox',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'orderDate');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'orderDate',
		                        op: 'LK',
		                        fieldType:'D',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'requireDate',
		        type:'datebox',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'requireDate');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'requireDate',
		                        op: 'LK',
		                        fieldType:'D',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'status',
		        type:'text',
		       	isDisabled:1
		        
		    },{
		        field:'num',
		        type:'text',
		       	isDisabled:1
		        
		    },{
		        field:'sum',
		        type:'text',
		       	isDisabled:1
		        
		    }]);
	$('#dg').datagrid('reload');
}
//搜索
function dosearch(){
	var startDate="";
	var toDate="";
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	
	
	$('#dg').datagrid('load',{
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate
	});
	
}
//查询
function searchDefectsList(row){
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/check/mxlist.htmlx", 
	    queryParams:{  
	    	"query['t#purchaseOrderPlan.id_L_EQ']":row.id,
	    }  
	});
}
//初始化
$(function(){
	
	$("#hospital").combobox({    
	    url:" <c:out value='${pageContext.request.contextPath }'/>/set/hospital/list.htmlx",  
	    valueField:'id',    
	    textField:'fullName',   
	    width:180,
	    //editable:false, 
	    onSelect:function(a,b){
	    	
	    }
	});
	$("#status").combobox({    
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:160,
	    editable:false,
	    data:[{
	    	label: '',
			value: '全部'
		},{
			label: 'uneffect',
			value: '未生效'
		},{
			label: 'cancel',
			value: '已取消'
		},{
			label: 'effect',
			value: '已生效'
		}]
	});
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height:$(".my-north").height() ,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/check/page.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
        	{field:'code',title:'订单计划号',width:20,align:'center'},
        	{field:'orderDate',title:'订单计划时间',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.createDate){
						return $.format.date(row.createDate,"yyyy-MM-dd HH:mm");
					}
				}
			},       	
			{field:'requireDate',title:'要求配货时间',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.requireDate){
						return $.format.date(row.requireDate,"yyyy-MM-dd HH:mm");
					}
				}
			},
        	{field:'hospitalName',title:'医疗机构',width:20,align:'center'},
        	//{field:'vendorName',title:'配送企业',width:15,align:'center'},  
        	{field:'warehouseName',title:'收货地点',width:10,align:'center'},
        	{field:'num',title:'采购数量',width:10,align:'center'},
        	
			{field:'sum',title:'采购金额（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.sum){
						return common.fmoney(row.sum);
					}
				}},
			{field:'status',title:'状态',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.status == 'uneffect'){
						return "未生效";
					}else if(row.status == 'cancel'){
						return "已取消";
					}else if(row.status == 'effect'){
						return "已生效";
					}
				}
			}
   		]],
   		toolbar:"#tb",
		onClickRow: function(index,row){
        	searchDefectsList(row);
		  	return;
		},
		onLoadSuccess:function(data){
			/* var rows=data.rows;
			//大于1行默认选中
			if(rows.length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList(rows[0]);
			}
			$('#dg').datagrid('doCellTip',{delay:500});  */
		}
	});


	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : '100%',
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
			{field:'productCode',title:'药品编码',width:10,align:'center'},
			{field:'productName',title:'药品名称',width:20,align:'center'},
			{field:'dosageFormName',title:'剂型',width:10,align:'center'},		
			{field:'model',title:'规格',width:10,align:'center'},	
			{field:'producerName',title:'生产企业',width:20,align:'center'},
			{field:'unit',title:'单位',width:10,align:'center'},
			{field:'price',title:'成交价（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
			{field:'goodsNum',title:'采购数量',width:10,align:'center'},
			{field:'goodsSum',title:'采购金额 （元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.goodsSum){
						return common.fmoney(row.goodsSum);
					}
				}},
			{field:'status',title:'能否供货',width:10,align:'center',
	        		formatter: function(value,row,index){
	        			if (row.status == "cancel"){
	        				return "<a class='dgbtn' href='#' onclick='checkOneAjax("+index+",0)' class='easyui-linkbutton'>不能</a>";
	        			}else{
	        				return "<a class='dgbtn' href='#' onclick='checkOneAjax("+index+",1)' class='easyui-linkbutton'>能</a>";
	        			}
					}
			}
		]],
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				if (row.status == "cancel"){
					$('.dgbtn:eq('+index+')').linkbutton({iconCls:'icon-no',plain:true,height:20}); 
				}else{
					$('.dgbtn:eq('+index+')').linkbutton({iconCls:'icon-ok',plain:true,height:20}); 
				}
			}); 
		}
	});
});


function checkFunc(){
	var selrow = $('#dg').datagrid('getSelected');
	var selobjs = $('#dgDetail').datagrid('getRows');
	if(selrow== null){
		showErr("请先选择一笔订单计划");
		return;
	}
	if(selrow.status == 'cancel' || selrow.status == 'effect'){
		return;
	}
	var id = selrow.id;
	$.messager.confirm('确认信息', '确认审单吗?', function(r){
		if (r){
			checkAjax(id);
		}
	});
	
}
//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "审单成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/check/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}
//=============ajax===============

function checkOneAjax(index,status){
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow.status == 'cancel' || selrow.status == 'effect'){
		return;
	}
	
	var row = $('#dgDetail').datagrid('getRows')[index];;
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/check/checkOne.htmlx",
		data:{
			"id":row.id,
			"status":status
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			searchDefectsList(selrow);
			if(data.success){
				showMsg("操作成功！");
				
			} else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}


function checkAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/check/check.htmlx",
		data:{
			"id":id
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			$('#dg').datagrid('reload');  
			if(data.success){
				//$('#dgDetail').datagrid('reload');  
				showMsg("审单成功！");
				successOpen(data.data);
			} else{
				showMsg("无法采购，取消订单计划");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>