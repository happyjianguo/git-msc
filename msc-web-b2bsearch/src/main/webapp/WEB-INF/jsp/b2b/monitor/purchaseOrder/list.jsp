<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单列表" />
<html>

<body class="easyui-layout" >
<div  data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;" >
	 	 <div id="tb" class="search-bar" >
		订单日期: <input class="easyui-datebox" style="width:110px" id="startDate">
        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
		状态: 
	 	<input class="easyui-combobox" style="width:80px" id="status"/>
	 	药品编码: 
	 	<input id="productCode" class="easyui-textbox" style="width:80px"/>
	 	药品名称: 
	 	<input id="productName" class="easyui-textbox" style="width:80px"/>
	 	拼音检索: 
	 	<input id="pinyin" class="easyui-textbox" style="width:80px"/>
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
        <span class="datagrid-btn-separator split-line" ></span>
<shiro:hasPermission name="b2b:monitor:purchaseOrder:export">
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true"  onclick="exportExcel()">导出Excel</a>
</shiro:hasPermission>
  		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true"  onclick="filterFunc()">数据过滤</a>
    </div>
	<div>
		<table  id="dg"></table>
	</div>
</div>

<div data-options="region:'center',title:''"  class="my-center" >
    <table id="dgDetail"></table>
</div>
</body>
</html>
<script>

//搜索
function dosearch(){
	var startDate="";
	var toDate="";
	var productCode = "";
	var productName = "";
	var pinyin="";
	if($('#productCode').textbox('getValue')!=""){
		productCode=$('#productCode').textbox('getValue');
	}
	if($('#productName').textbox('getValue')!=""){
		productName=$('#productName').textbox('getValue');	
	}
	if($('#pinyin').textbox('getValue')!=""){
		pinyin=$('#pinyin').textbox('getValue').toUpperCase();	
	}
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	
	var status = $("#status").datebox('getValue');
	var data = {
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate,
		"query['t#status_S_EQ']":status,
		"query['d#productCode_S_EQ']":productCode,
		"query['d#productName_S_LK']":productName,
		"query['p#pinyin_S_LK']":pinyin
	};
	
	$('#dg').datagrid('load',data);
}
//查询
function searchDefectsList(row){
    var startDate="";
    var toDate="";
    var productCode = "";
    var productName = "";
    var pinyin="";
    if($('#productCode').textbox('getValue')!=""){
        productCode=$('#productCode').textbox('getValue');
    }
    if($('#productName').textbox('getValue')!=""){
        productName=$('#productName').textbox('getValue');
    }
    if($('#pinyin').textbox('getValue')!=""){
        pinyin=$('#pinyin').textbox('getValue').toUpperCase();
    }
    if($('#startDate').datebox('getValue')!=""){
        startDate = $('#startDate').datebox('getValue');
    }
    if($('#toDate').datebox('getValue')!=""){
        toDate = $('#toDate').datebox('getValue');
    }
    var status = $("#status").datebox('getValue');
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrder/mxlist.htmlx",  
	    queryParams:{  
	    	"query['t#purchaseOrderId_L_EQ']":row.ID,
            "query['t#orderDate_D_GE']": startDate,
            "query['t#orderDate_D_LE']": toDate,
            "query['op#status_S_EQ']":status,
            "query['t#productCode_S_EQ']":productCode,
            "query['t#productName_S_LK']":productName,
            "query['p#pinyin_S_LK']":pinyin
	    }  
	});
}
var isFilter=0;
function filterFunc(){
	if(isFilter == 1) return;
	isFilter=1;
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', [{
		        field:'ORDERDATE',
		        type:'datebox',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'ORDERDATE');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'ORDERDATE',
		                        op: 'LK',
		                        fieldType:'D',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'REQUIREDATE',
		        type:'datebox',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'REQUIREDATE');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'REQUIREDATE',
		                        op: 'LK',
		                        fieldType:'D',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'NUM',
		        type:'text',
		    	isDisabled:1
		        
		    },{
		        field:'DELIVERYNUM',
		        type:'text',
		    	isDisabled:1
		        
		    },{
		        field:'INOUTBOUNDNUM',
		        type:'text',
		    	isDisabled:1
		        
		    },{
		        field:'RETURNSNUM',
		        type:'text',
		    	isDisabled:1
		        
		    },{
		        field:'SUM',
		        type:'text',
		    	isDisabled:1
		        
		    },{
		        field:'STATUS',
		        type:'text',
		       	isDisabled:1
		        
		    },{
		        field:'OPERATION',
		        type:'text',
		       	isDisabled:1
		        
		    }]);
	$('#dg').datagrid('reload');
}
//导出
function exportExcel(){
	var startDate="";
	var toDate="";
	var productCode = "";
	var productName = "";
	var pinyin="";
	if($('#productCode').textbox('getValue')!=""){
		productCode=$('#productCode').textbox('getValue');
	}
	if($('#productName').textbox('getValue')!=""){
		productName=$('#productName').textbox('getValue');	
	}
	if($('#pinyin').textbox('getValue')!=""){
		pinyin=$('#pinyin').textbox('getValue').toUpperCase();	
	}
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	
	var status = $("#status").datebox('getValue');
	var data = {
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate,
		"query['t#status_S_EQ']":status,
		"query['d#productCode_S_EQ']":productCode,
		"query['d#productName_S_LK']":productName,
		"query['p#pinyin_S_LK']":pinyin
	};
	console.log(123456);
	var filter = $('#dg').datagrid('getFilterComponent', 'CODE');
	console.log(filter);
	var url = "<c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrder/export.htmlx?";
	for(var k in data){
		url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(data[k]);
	}
	window.open(url);
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
			label: 'effect',
			value: '已生效'
		},{
			label: 'sending',
			value: '配送中'
		},{
			label: 'sent',
			value: '配送完成'
		},{
			label: 'forceClosed',
			value: '强行结案'
		}],
		onChange:function(value){
			dosearch();
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
			{field:'PRODUCTCODE',title:'药品编码',width:10,align:'center'},
			{field:'PRODUCTNAME',title:'药品名称',width:20,align:'center'},
			{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
			{field:'MODEL',title:'规格',width:10,align:'center'},
			{field:'PRODUCERNAME',title:'生产企业',width:20,align:'center'},
			{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
            {field:'AUTHORIZENO',title:'批准文号',width:15,align:'center'},
			{field:'PRICE',title:'价格(元)',width:10,align:'right',
					formatter: function(value,row,index){
						if (row.PRICE){
							return common.fmoney(row.PRICE);
						}
					}},
			{field:'GOODSNUM',title:'采购数量',width:10,align:'right'},
			{field:'DELIVERYGOODSNUM',title:'配送数量',width:10,align:'right'},
			{field:'GOODSSUM',title:'采购金额(元)',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.GOODSSUM){
						return common.fmoney(row.GOODSSUM);
					}
				}}
		]],
		
		onLoadSuccess: function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
			//onLoadSuccess();
		},
	});
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrder/page.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
        	{field:'CODE',title:'订单号',width:20,align:'center'},
        	{field:'ORDERDATE',title:'订单日期',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.ORDERDATE){
						return $.format.date(row.ORDERDATE,"yyyy-MM-dd HH:mm");
					}
				}
			},       	
			{field:'REQUIREDATE',title:'要求配货时间',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.REQUIREDATE){
						return $.format.date(row.REQUIREDATE,"yyyy-MM-dd HH:mm");
					}
				}
			},
        	{field:'HOSPITALNAME',title:'医疗机构',width:15,align:'center'},
        	{field:'VENDORNAME',title:'供应商',width:15,align:'center'}, 
/*         	{field:'warehouseName',title:'收货地点',width:10,align:'center'}, */
        	{field:'NUM',title:'采购数量',width:10,align:'right'},
        	{field:'DELIVERYNUM',title:'配送数量',width:10,align:'right'},
			{field:'SUM',title:'采购金额（元）',width:15,align:'right',
				formatter: function(value,row,index){
					if (row.SUM){
						return common.fmoney(row.SUM);
					}
				}},
			{field:'STATUS',title:'状态',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.STATUS == '0'){
						return "已生效";
					}else if(row.STATUS == '1'){
						return "配送中";
					}else if(row.STATUS == '2'){
						return "配送完成";
					}else if(row.STATUS == '3'){
						return "强行结案";
					}
				}
			},
			{field:'OPERATION',title:'操作',width:10,align:'center',
	    		formatter: function(value,row,index){
	    			var str = "<img title='订单计划查询("+row.PURCHASEORDERPLANCODE+")' onclick=openPre('"+row.PURCHASEORDERPLANCODE+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png' style='cursor: pointer;'/>"
	    			+"&nbsp;<img title='配送单查询' onclick=openNext('"+row.CODE+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/redo.png' style='cursor: pointer;'/>";
					return str;
				}
			}
   		]],
   		toolbar:"#tb",
		onClickRow: function(index,row){			
        	var selobj1 = $('#dg').datagrid('getSelected');
        	searchDefectsList(selobj1);
		  	return;
		},
		onDblClickRow: function(index,row){
			top.addTab("配送单查询 ", "/b2b/monitor/deliveryOrder.htmlx?purchaseOrderCode="+row.CODE);
		},
		onLoadSuccess: function(data){
			var rows=$(this).datagrid("getRows");
			//大于1行默认选中
			if($(this).datagrid("getRows").length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList($(this).datagrid("getRows")[0]);
			}
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
		queryParams:{
			"query['t#code_S_EQ']":"<c:out value='${code}'/>",
			"query['t#purchaseOrderPlanCode_S_EQ']":"<c:out value='${purchaseOrderPlanCode}'/>"
		}
	});
});
function openPre(code){
	top.addTab("订单计划查询 ", "/b2b/monitor/purchaseOrderPlan.htmlx?code="+code);
}
function openNext(code){
	top.addTab("配送单查询 ", "/b2b/monitor/deliveryOrder.htmlx?purchaseOrderCode="+code);
}
//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "新增订单",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrder/add.htmlx",
		onLoad:function(){
		},
		buttons : [ {
			text : '保存',
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

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/sys/user/del.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');
				showMsg("删除成功");
			} else{
				showErr("出错，请刷新重新操作");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
	
}
</script>