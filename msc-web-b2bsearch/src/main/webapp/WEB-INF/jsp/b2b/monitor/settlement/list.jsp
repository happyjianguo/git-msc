<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单计划列表" />
<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;" >
	<div id="tb" class="search-bar" >
		结算单日期：<input class="easyui-datebox" style="width:110px" id="startDate">
        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
      	状态：
	 	<input class="easyui-combobox" style="width:100px" id="status"/>
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()">查询</a>
	 	<span class="datagrid-btn-separator split-line" ></span>
		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
	</div>
	<table  id="dg"></table>
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
		        field:'accBeginDate',
		        type:'datebox',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'accBeginDate');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'accBeginDate',
		                        op: 'LK',
		                        fieldType:'D',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'accEndDate',
		        type:'datebox',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'accEndDate');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'accEndDate',
		                        op: 'LK',
		                        fieldType:'D',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
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
		        field:'num',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'sum',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'paidAmt',
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
	var status = $("#status").combo('getValue');
	var data = {
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate,
		"query['t#status_S_EQ']":status
	};
	$('#dg').datagrid('load',data);
}
//查询
function searchDefectsList(row){
	if(row == "")
		return;
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/settlement/mxlist.htmlx",  
	    queryParams:{
	    	"query['t#settlement.id_L_EQ']":row.id
	    }  
	});
}
//初始化
$(function(){
	$('#ss').searchbox({
		searcher:function(value, name) {
			dosearch();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
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
			label: 'unpay',
			value: '未付款'
		},{
			label: 'paying',
			value: '付款中'
		},{
			label: 'paid',
			value: '已付款'
		}]
	});
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/settlement/page.htmlx",
		queryParams:{
			"query['t#code_S_EQ']":"<c:out value='${code}'/>"
		},
		pageSize:10,
		pageNumber:1,
		toolbar:"#tb",
		columns:[[
        		{field:'code',title:'结算单号',width:20,align:'center'},
	        	{field:'hospitalName',title:'医疗机构',width:15,align:'center'},
	        	{field:'vendorName',title:'供应商',width:15,align:'center'},
	        	{field:'accBeginDate',title:'账期起始日期',width:10,align:'center',
					formatter: function(value,row,index){
						if (row.accBeginDate){
							return $.format.date(row.accBeginDate,"yyyy-MM-dd");
						}
					}},
	        	{field:'accEndDate',title:'账期结束日期',width:10,align:'center',
					formatter: function(value,row,index){
						if (row.accEndDate){
							return $.format.date(row.accEndDate,"yyyy-MM-dd");
						}
					}},
	        	{field:'sum',title:'结算金额',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.sum){
								return common.fmoney(row.sum);
							}
						}},
	        	{field:'paidAmt',title:'已付款',width:10,align:'right',
							formatter: function(value,row,index){
								if (row.paidAmt){
									return common.fmoney(row.paidAmt);
								}
							}},
	        	{field:'orderDate',title:'结算日期',width:10,align:'center',
					formatter: function(value,row,index){
						if (row.orderDate){
							return $.format.date(row.orderDate,"yyyy-MM-dd");
						}
					}},
	        	{field:'status',title:'状态',width:15,align:'center',
					formatter: function(value,row,index){
						if (row.status == 'unpay'){
							return "未付款";
						}else if(row.status == 'paying'){
							return "付款中";
						}else if(row.status == 'paid'){
							return "已付款";
						}
					}
	        	}
   		]],
		onClickRow: function(index,row){			
        	searchDefectsList(row);
		},
		onDblClickRow: function(index,row){;
			top.addTab("付款单查询 ", "/b2b/monitor/payment.htmlx?settlementCode="+row.code);
		},
		onLoadSuccess: function(data){
			var rows=$(this).datagrid("getRows");
			//大于1行默认选中
			if($(this).datagrid("getRows").length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList($(this).datagrid("getRows")[0]);
			}
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
			{field:'code',title:'结算明细单号',width:20,align:'center'},
			{field:'invoiceCode',title:'发票号码',width:20,align:'center'},
			{field:'sum',title:'结算金额',width:20,align:'right',
				formatter: function(value,row,index){
					if (row.sum){
						return common.fmoney(row.sum);
					}
			}},
			{field:'operation',title:'操作',width:10,align:'center',
	    		formatter: function(value,row,index){
	    			var str = "<img title='发票查询("+row.INVOICECODE+")' onclick=openPre('"+row.INVOICECODE+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png' style='cursor: pointer;'/>";
	    			return str;
				}
			}
		]],
		queryParams:{
			"query['t#code_S_EQ']":"<c:out value='${code}'/>"
		}
	});
});
function openPre(code){
	top.addTab("发票查询 ", "/b2b/monitor/invoice.htmlx?code="+code);
}
//=============ajax===============

</script>