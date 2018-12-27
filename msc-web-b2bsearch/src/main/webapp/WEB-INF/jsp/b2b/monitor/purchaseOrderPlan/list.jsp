<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单计划列表" />
<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;"  >
	 <div id="tb" class="search-bar" >
		订单计划日期: <input class="easyui-datebox" style="width:110px" id="startDate">
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
		<shiro:hasPermission name="b2b:monitor:purchaseOrderPlan:export">
				<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true"  onclick="exportExcel()">导出Excel</a>
		</shiro:hasPermission>
  		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true"  onclick="filterFunc()">数据过滤</a>
    </div>
	<div>
		<table  id="dg"></table>
	</div>
</div>

<div data-options="region:'center',title:''"   class="my-center" >
	<shiro:hasPermission name="monitor:purchaseOrderPlan:cancelDetail"> 
	 	<div id="tbDetail" class="search-bar" >
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true" onclick="cancelDetail()">删除</a>
		        <span class="datagrid-btn-separator split-line" ></span>
	    </div>
    </shiro:hasPermission>
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
	    url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrderPlan/mxlist.htmlx", 
	    queryParams:{  
	    	"query['t#purchaseOrderPlanId_I_EQ']":row.ID,
            "query['t#orderDate_D_GE']": startDate,
            "query['t#orderDate_D_LE']": toDate,
            "query['op#status_S_EQ']":status,
            "query['t#productCode_S_EQ']":productCode,
            "query['t#productName_S_LK']":productName,
            "query['p#pinyin_S_LK']":pinyin
	    }  
	});
}
function openNext(code){
	top.addTab("订单查询 ", "/b2b/monitor/purchaseOrder.htmlx?purchaseOrderPlanCode="+code);
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
    var url = "<c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrderPlan/export.htmlx?";
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
	    panelHeight:"auto",
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
		}],
		onChange:function(value){
			dosearch();
		}
	});
	
	
	
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height(),
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrderPlan/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
        	{field:'CODE',title:'订单计划号',width:22,align:'center'},
        	{field:'ORDERDATE',title:'订单计划时间',width:15,align:'center',
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
        	/* {field:'warehouseName',title:'收货地点',width:10,align:'center'}, */
        	{field:'VENDORNAME',title:'供应商',width:15,align:'center'},  			    
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
					if (row.STATUS == 0){
						return "未生效";
					}else if(row.STATUS == 1){
						return "已取消";
					}else if(row.STATUS == 2){
						return "已生效";
					}
				}
			},
			{field:'OPERATION',title:'操作',width:10,align:'center',
	    		formatter: function(value,row,index){
	    			var str = "<img title='订单查询' onclick=openNext('"+row.CODE+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/redo.png' style='cursor: pointer;'/>";
					return str;
				}
			}
   		]],
   		toolbar:"#tb",
		onClickRow: function(index,row){			
        	searchDefectsList(row);
		},
		onDblClickRow: function(index,row){
	
			
		},
		queryParams:{
			"query['t#code_S_EQ']":"<c:out value='${code}'/>",
			"query['t#purchasePlanCode_S_EQ']":"<c:out value='${planCode}'/>"
		},
		onLoadSuccess: function(data){
			var rows=$(this).datagrid("getRows");
			//大于1行默认选中
			if($(this).datagrid("getRows").length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList($(this).datagrid("getRows")[0]);
			}
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});
	
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : '90%',
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
			{field:'PRICE',title:'价格（元）',width:10,align:'right',
					formatter: function(value,row,index){
						if (row.PRICE){
							return common.fmoney(row.PRICE);
						}
					}},			
			{field:'GOODSNUM',title:'采购数量',width:10,align:'right'},
			{field:'GOODSSUM',title:'采购金额 （元）',width:15,align:'right',
				formatter: function(value,row,index){
					if (row.GOODSSUM){
						return common.fmoney(row.GOODSSUM);
					}
				}},
            {field:'DELIVERYGOODSNUM',title:'配送数量',width:10,align:'right'},
            {field:'DELIVERYGOODSSUM',title:'配送金额',width:10,align:'right',
                formatter: function(value,row,index){
                    if (row.DELIVERYGOODSSUM){
                        return common.fmoney(row.DELIVERYGOODSSUM);
                    }
                }},
			{field:'STATUS',title:'能否采购',width:10,align:'center',
	    		formatter: function(value,row,index){
					if (row.STATUS == 0){
						return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
					}else if (row.STATUS == 1){
						return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/del-16.png\" />";
					}
				}
			}
		]],
		toolbar:"#tbd",
	});
	
});

function cancelDetail(){
	var selrow = $('#dg').datagrid('getSelected');
	var selrowD = $('#dgDetail').datagrid('getSelected');
	if(selrow.STATUS != 'uneffect'){
		showErr("请先选择未生效的订单计划");
		return;
	}
	if(selrowD== null){
		showErr("请先选择一笔订单计划明细");
		return;
	}
	
	var id = selrowD.ID;
	$.messager.confirm('确认信息', '确认删除此订单明细吗?', function(r){
		if (r){
			cancelDetailAjax(id);
		}
	});
}
//=============ajax===============
function cancelDetailAjax(id){
	var selrow = $('#dg').datagrid('getSelected');
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrderPlan/cancelDetail.htmlx",
		data:{
			"id":id
		},
		dataType:"json",
		type:"POST",
		cache:false,
		//traditional: true,//支持传数组参数
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
</script>