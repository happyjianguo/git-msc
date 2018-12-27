<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="入库单" />

<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout"  >
    <div  data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;" >
		<div id="tb" class="search-bar" >
			入库日期: <input class="easyui-datebox" style="width:110px" id="startDate">
	        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
	        药品编码: 
	 	<input id="productCode" class="easyui-textbox" style="width:150px"/>
	 	药品名称: 
	 	<input id="productName" class="easyui-textbox" style="width:150px"/>
	 	拼音检索: 
	 	<input id="pinyin" class="easyui-textbox" style="width:150px"/>
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
		    <span class="datagrid-btn-separator split-line" ></span>
		   	<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
	        
	    </div>
		<table  id="dg" ></table>
    </div>
         
    <div data-options="region:'center',title:''"   class="my-center" >
		<table id="dg2" ></table>
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
		        field:'NUM',
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
		        
		    },{
		        field:'GPOCODE',
		        type:'combobox',
		        options:{
		            panelHeight:'auto',
		            editable:false,
		            data:[{value:'',text:'-请选择-'},
		                  {value:'0',text:'否'},
		                  {value:'1',text:'是'}],
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'GPOCODE');
		                } else {
		                	var op = (value == "1"?"NOT":"IS");
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'GPOCODE',
		                        op: op,
		                        fieldType:'NULL',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		        
		    }]);
	$('#dg').datagrid('reload');
}

$(function(){

	//日期
	$('#deliveryDate1').datebox({
		editable:false
	});
	$('#deliveryDate2').datebox({
		editable:false
	});


	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		
		height :$(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/inOutBound/page.htmlx",
		pageSize:10,
		pageNumber:1,
		toolbar:"#tb",
		columns:[[
		        	{field:'CODE',title:'入库单号',width:20,align:'center'},
		        	{field:'ORDERDATE',title:'入库时间',width:20,align:'center',
						formatter: function(value,row,index){
							if (row.ORDERDATE){
								return $.format.date(row.ORDERDATE,"yyyy-MM-dd HH:mm");
							}
						}
					}, 
					{field:'VENDORNAME',title:'供应商',width:20,align:'center'},
					{field:'HOSPITALNAME',title:'医疗机构',width:20,align:'center'},	
					{field:'GPOCODE',title:'是否GPO订单',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.GPOCODE != undefined&&row.GPOCODE!=null){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
							}
					}},
					{field:'OPERATOR',title:'操作人',width:10,align:'center'},
					{field:'NUM',title:'入库数量',width:10,align:'center'},
					{field:'SUM',title:'入库金额（元）',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.SUM){
								return common.fmoney(row.SUM);
							}
						}},
					{field:'OPERATION',title:'操作',width:10,align:'center',
			    		formatter: function(value,row,index){
			    			if(row.GPOCODE != undefined&&row.GPOCODE!=null){
			    				var str = "<img title='配送单查询("+row.DELIVERYORDERCODE+")' onclick=openPreP('"+row.DELIVERYORDERCODE+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png' style='cursor: pointer;'/>";
				    			return str;
			    			}else{
			    				return ;
			    			}
						}
					}
		   		]],
		onClickRow: function(index,row){
			todg2(row);  
		},
		onLoadSuccess: function(data){
			var rows=data.rows;
			//大于1行默认选中
			if($(this).datagrid("getRows").length > 0) {
				$(this).datagrid('selectRow', 0);
				todg2(rows[0]);
			}
		},
		queryParams:{
			"query['t#deliveryOrderCode_S_EQ']":"<c:out value='${deliveryOrderCode}'/>",
			"query['t#code_S_EQ']":"<c:out value='${code}'/>",
		}
	});

	//组成员
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		rownumbers:true,
		height :'100%',
		singleSelect:true,
		showFooter: false,
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
			{field:'PRODUCTCODE',title:'药品编码',width:10,align:'center'},
			{field:'PRODUCTNAME',title:'药品名称',width:20,align:'center'},
			{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
			{field:'MODEL',title:'规格',width:10,align:'center'},
        	{field:'PRODUCERNAME',title:'生产企业',width:20,align:'center'},
			{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
			{field:'PRICE',title:'价格（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.PRICE){
						return common.fmoney(row.PRICE);
					}
				}},
			{field:'GOODSNUM',title:'入库数量',width:10,align:'center'},
			{field:'GOODSSUM',title:'入库金额 （元）',width:13,align:'right',
				formatter: function(value,row,index){
					if (row.GOODSSUM){
						return common.fmoney(row.GOODSSUM);
					}
				}},	
			{field:'BATCHCODE',title:'批号',width:10,align:'center'},
			{field:'BATCHDATE',title:'生产日期',width:10,align:'center'},
			{field:'EXPIRYDATE',title:'有效期',width:10,align:'center'},
   		]]
	});
	
	$('#ss').searchbox({
		searcher:function(value, name) {
			dosearch();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
})

function openPreP(code){
	top.addTab("配送单查询 ", "/b2b/monitor/deliveryOrder.htmlx?code="+code);
}
function todg2(row){
    var startDate="";
    var toDate="";
    var hospitalName="";
    var vendorName ="";
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
	$('#dg2').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/inOutBound/mxlist.htmlx",  
	    queryParams:{
	        "query['t#inOutBoundId_L_EQ']":row.ID,
            "query['t#orderDate_D_GE']": startDate,
            "query['t#orderDate_D_LE']": toDate,
            "query['t#productCode_S_EQ']":productCode,
            "query['t#productName_S_LK']":productName,
            "query['p#pinyin_S_LK']":pinyin
	    }
	}); 
	
}
//搜索
function dosearch(){
	var startDate="";
	var toDate="";
	var hospitalName="";
	var vendorName ="";
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
	var data = {
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate,
		"query['d#productCode_S_EQ']":productCode,
		"query['d#productName_S_LK']":productName,
		"query['p#pinyin_S_LK']":pinyin
	};
	if(${isMonitor!=null}){
		//data["query['t#"+$('#ss').searchbox("getName")+"_S_LK']"] = $('#ss').searchbox("getValue");
	}
	$('#dg').datagrid('load',data);
}
//=============ajax===============

</script>