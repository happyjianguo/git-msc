<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
<div  data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;" >
	<div id="tb" class="search-bar" >
		订单日期: <input class="easyui-datebox" style="width:110px" id="startDate">
        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
         <span class="datagrid-btn-separator split-line" ></span>
		状态: 
	 	<input class="easyui-combobox" style="width:100px" id="status"/>
	 	 <span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="query()">查询</a>
        
    </div>
	<div>
		<table  id="dg" ></table>
	</div>
</div>

<div data-options="region:'center',title:''"  class="my-center" >
    <table id="dgDetail"></table>
</div>
</body>
</html>
<script>
function searchDefectsList(row){
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRequest/mxlist.htmlx",  
	    queryParams:{
	    	"query['t#purchaseClosedRequest.id_L_EQ']":row.id,
	    }  
	});
}
//检索
function query(){
	var startDate="";
	var toDate="";
	var status = $("#status").datebox('getValue');
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	
	
	$('#dg').datagrid('load',{"query['t#createDate_D_GE']": startDate,"query['t#createDate_D_LE']": toDate,"query['t#status_S_EQ']":status});
}

function filterFunc(){
	$('#dg').datagrid('enableFilter', [{
		        field:'closedRequestDate',
		        type:'datebox',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'closedRequestDate');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'closedRequestDate',
		                        op: 'LK',
		                        fieldType:'D',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'num',
		        type:'text',
		    	isDisabled:1
		        
		    },{
		        field:'sum',
		        type:'text',
		    	isDisabled:1
		        
		    },{
		        field:'status',
		        type:'text',
		       	isDisabled:1
		    },{
		        field:'operation',
		        type:'text',
		       	isDisabled:1
		    }]);
}

//删除
function delCp(){

	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var status = selobj.status;
	if(status != 'unaudit'){
		$.messager.alert('错误','只可删除未审核申请单!','info');
		return;
	}
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此申请单?', function(r){
		if (r){
			delAjax(id);
		}
	});
}

function commitOrder(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var status = selobj.status;
	if(status == 'commit'){
		//$.messager.alert('错误','已经下单不可删除!','info');
		return;
	}
	var id= $('#dg').datagrid('getSelected').id;
	
	$.messager.confirm('确认信息', '确认要下单?', function(r){
		if (r){
			commitAjax(id);
		}
	});
}

function commitAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseOrder/commit.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');
				//$('#dgDetail').datagrid('loadData',{total:0,rows:[]});

				showMsg("已成功下单");
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}




//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "备案申请",
		width : 800,
		height : 550,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/productRegister/add.htmlx",
		onLoad:function(){
		},
		buttons : [ {
			text : '提交',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				f.submit();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
			}
		}]
	});
}


	

//初始化
$(function(){
	$("#status").combobox({    
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:'auto',
	    editable:false,
	    data:[{
	    	label: '',
			value: '全部'
		},{
			label: 'unaudit',
			value: '未审核'
		},{
			label: 'agree',
			value: '同意结案'
		},{
			label: 'disagree',
			value: '不同意结案'
		}],
		onChange:function(value){
			query();
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
		showFooter: false,
		columns:[[
			{field:'productCode',title:'药品编码',width:10,align:'center'},
			{field:'productName',title:'药品名称',width:20,align:'center'},
			{field:'dosageFormName',title:'剂型',width:10,align:'center'},		
			{field:'model',title:'规格',width:10,align:'center'},	
			{field:'producerName',title:'生产企业',width:20,align:'center'},
			{field:'unit',title:'单位',width:10,align:'center'},
			{field:'price',title:'价格(元)',width:10,align:'right',
					formatter: function(value,row,index){
						if (row.price){
							return common.fmoney(row.price);
						}
					}},
			{field:'goodsNum',title:'采购数量',width:10,align:'right'},
			{field:'goodsSum',title:'采购金额(元)',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.goodsSum){
						return common.fmoney(row.goodsSum);
					}
				}}
		]],
		
		onLoadSuccess: function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
			//onLoadSuccess();
		},
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRequest/page.htmlx",
		pageSize:10,
		pageNumber:1,
		checkOnSelect:true,
		remoteFilter:true,
		toolbar:"#tb",
		columns:[[
			{field:'code',title:'结案申请单号',width:20,align:'center'},
			{field:'purchaseOrderCode',title:'订单号',width:20,align:'center'},
        	{field:'closedRequestDate',title:'结案申请时间',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.closedRequestDate){
						return $.format.date(row.closedRequestDate,"yyyy-MM-dd HH:mm:ss");
					}
				}
			}, 
			{field:'vendorName',title:'供应商',width:20,align:'center'},
			{field:'reason',title:'原因',width:20,align:'center'},
			{field:'reply',title:'答复',width:20,align:'center'},
			{field:'num',title:'退货数量',width:10,align:'center'},
			{field:'sum',title:'退货金额(元)',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.sum){
						return common.fmoney(row.sum);
					}
				}},
			{field:'status',title:'状态',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.status == 'unaudit'){
						return "未审核";
					}else if(row.status == 'agree'){
						return "同意结案";
					}else if(row.status == 'disagree'){
						return "不同意结案";
					}
				}
			},
			{field:'closedType',title:'结案方式',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.closedType == 'orderClosed'){
						return "订单结案";
					}else if(row.closedType == 'orderDetailClosed'){
						return "明细结案";
					}
				}
			},
			{field:'operation',title:'操作',width:10,align:'center',
	    		formatter: function(value,row,index){
	    			var str = "<img title='订单查询("+row.purchaseOrderCode+")' onclick=openPre('"+row.purchaseOrderCode+"') src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png\" style='cursor: pointer;'/>";
	    			return str;
				}
			}
   		]],
		onClickRow: function(index,row){			
        	var selobj1 = $('#dg').datagrid('getSelected');
        	searchDefectsList(selobj1);
		  	return;
		},
		onLoadSuccess:function(data){
			var rows=$(this).datagrid("getRows");
			//大于1行默认选中
			if($(this).datagrid("getRows").length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList($(this).datagrid("getRows")[0]);
			}
			$('#dg').datagrid('doCellTip',{delay:500});
		}
	});
	filterFunc();
});
function openPre(code){
	top.addTab("订单查询 ", "/b2b/monitor/purchaseOrder.htmlx?code="+code);
}
</script>