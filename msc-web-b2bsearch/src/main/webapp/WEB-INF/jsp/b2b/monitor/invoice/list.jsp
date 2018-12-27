<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="发票管理" />
<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;" >
	<div id="tb" class="search-bar" >
		发票日期: <input class="easyui-datebox" style="width:110px" id="startDate">
        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
	    <span class="datagrid-btn-separator split-line" ></span>
	   	<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
    </div>
	<div>
		<table  id="dg"></table>
	</div>
</div>

<div data-options="region:'center',title:''"   class="my-center">
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
		        field:'isRed',
		        type:'combobox',
		        options:{
		            panelHeight:'auto',
		            editable:false,
		            data:[{value:'',text:'-请选择-'},
		                  {value:'0',text:'否'},
		                  {value:'1',text:'是'}],
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'isRed');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'isRed',
		                        op: 'LK',
		                        fieldType:'I',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'noTaxSum',
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
	var data = {
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate
	};
	
	$('#dg').datagrid('load',data);
}
//查询
function searchDefectsList(row){
	if(row.isRed == 1){
		isRedFlag = 1;
	}else{
		isRedFlag = 0;
	}
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/invoice/mxlist.htmlx",  
	    queryParams:{  
	    	"query['t#invoice.id_L_EQ']":row.id
	    }  
	});
}

var isRedFlag = 0;
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
	$("#vendor").combobox({    
	    url:" <c:out value='${pageContext.request.contextPath }'/>/dm/company/listVendor.htmlx",  
	    valueField:'ID',    
	    textField:'FULLNAME',  
	    width:180
	});
	
	
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/invoice/page.htmlx",
		pageSize:10,
		pageNumber:1,
		toolbar:"#tb",
		columns:[[
	        	{field:'code',title:'单号',width:20,align:'center'},
	        	{field:'vendorName',title:'供应商',width:20,align:'center'},
	        	{field:'hospitalName',title:'医疗机构',width:20,align:'center'},
	        	{field:'internalCode',title:'发票号码',width:20,align:'center'},
	        	{field:'orderDate',title:'发票日期',width:20,align:'center',
					formatter: function(value,row,index){
						if (row.orderDate){
							return $.format.date(row.orderDate,"yyyy-MM-dd");
						}
					}
				}, 
	        	{field:'isRed',title:'是否冲红',width:10,align:'center',
	        		formatter: function(value,row,index){
						if (row.isRed == '0'){
							return "否";
						}else {
							return "是";
						}
					}
				},
	        	{field:'noTaxSum',title:'不含税金额（元）',width:10,align:'right',
					formatter: function(value,row,index){
						if (row.noTaxSum){
							if (row.isRed == '0'){
								return common.fmoney(row.noTaxSum);
							}else {
								return "-"+common.fmoney(row.noTaxSum);
							}
						}
					}},
	        	{field:'sum',title:'含税金额（元）',width:10,align:'right',
					formatter: function(value,row,index){
						if (row.sum){
							if (row.isRed == '0'){
								return common.fmoney(row.sum);
							}else {
								return "-"+common.fmoney(row.sum);
							}
						}
				}}/* ,
				{field:'operation',title:'操作',width:10,align:'center',
		    		formatter: function(value,row,index){
		    			var str = "";
		    			if(row.deliveryOrReturnsCode.substring(0,1) == 'P'){
		    				str = "<img title='配送单查询("+row.deliveryOrReturnsCode+")' onclick=openPreP('"+row.deliveryOrReturnsCode+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png' style='cursor: pointer;'/>";
		    			}else{
		    				str = "<img title='退货单查询("+row.deliveryOrReturnsCode+")' onclick=openPreT('"+row.deliveryOrReturnsCode+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png' style='cursor: pointer;'/>";
		    			}
		    			return str;
					}
				} */
	   		]],
			rowStyler: function(index,row){
				if(row.isRed == 1)
					return 'color:red;'; // return inline style
				// the function can return predefined css class and inline style
				// return {class:'r1', style:{'color:#fff'}};	
				
			},
		onClickRow: function(index,row){
        	searchDefectsList(row);
		},
		onLoadSuccess: function(data){
			var rows=data.rows;
			//大于1行默认选中
			if(rows.length > 0) {
				$(this).datagrid('selectRow', 0);
				searchDefectsList(rows[0]);
			}
		},
		queryParams:{
			"query['t#code_S_EQ']":"<c:out value='${code}'/>",
			"query['t#deliveryOrReturnsCode_S_EQ']":"<c:out value='${deliveryOrderCode}'/>"
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
			{field:'packDesc',title:'包装规格',width:10,align:'center'},
			{field:'price',title:'价格（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);			
					}
				}
			},
			{field:'goodsNum',title:'数量',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.goodsNum){
						if (isRedFlag == '1'){
							return "-"+common.fmoney(row.goodsNum);
						}else {
							return common.fmoney(row.goodsNum);
						}
					}
				}
			},		
			{field:'taxRate',title:'税率',width:10,align:'center',
        		formatter: function(value,row,index){
					if (row.taxRate){
						return row.taxRate+"%";
					}
				}
			},
			{field:'noTaxPrice',title:'不含税金额（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.noTaxSum){
						return common.fmoney(row.noTaxSum);
					}
				}
			},
			{field:'goodsSum',title:'含税金额(元)',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.goodsSum){
						if (isRedFlag == '1'){
							return "-"+common.fmoney(row.goodsSum);
						}else {
							return common.fmoney(row.goodsSum);
						}
					}
				}
			}
		]],
		rowStyler: function(index,row){
			if(isRedFlag == 1)
				return 'color:red;'; // return inline style
			// the function can return predefined css class and inline style
			// return {class:'r1', style:{'color:#fff'}};	
			
		},
	});
});
function openPreP(code){
	top.addTab("配送单查询 ", "/b2b/monitor/deliveryOrder.htmlx?code="+code);
}
function openPreT(code){
	top.addTab("退货单查询 ", "/b2b/monitor/returnsOrder.htmlx?code="+code);
}
</script>