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
		采购计划日期: <input class="easyui-datebox" style="width:110px" id="startDate">
        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
		药品编码: 
	 	<input id="productCode" class="easyui-textbox" style="width:150px"/>
	 	药品名称: 
	 	<input id="productName" class="easyui-textbox" style="width:150px"/>
	 	拼音检索: 
	 	<input id="pinyin" class="easyui-textbox" style="width:150px"/>
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
        <span class="datagrid-btn-separator split-line" ></span>
		<shiro:hasPermission name="admin:PurchaseOrderPlan:updateBatch">
			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="doUpdateBatch()">批量生成订单计划</a>
			<span class="datagrid-btn-separator split-line" ></span>
		</shiro:hasPermission>

  		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true"  onclick="filterFunc()">数据过滤</a>
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
//批量生成订单计划
function doUpdateBatch() {
    $.ajax({
        url :"${pageContext.request.contextPath }/b2b/monitor/purchasePlan/doUpdateBatch.htmlx",
        type:"POST",
        success:function(data){
            if (data.success) {
                showMsg("新增成功！");
            } else {
                showErr(data.msg);
            }
        } ,
        error : function() {
            showErr("出错，请刷新重新操作");
        }
    });
}
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
	
	var data = {
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate,
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
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchasePlan/mxlist.htmlx", 
	    queryParams:{
            "query['t#orderDate_D_GE']": startDate,
            "query['t#orderDate_D_LE']": toDate,
           	"query['t#productCode_S_EQ']":productCode,
            "query['t#productName_S_LK']":productName,
	    	"query['t#purchasePlanId_I_EQ']":row.ID,
			"query['p#pinyin_S_LK']":pinyin
	    }
	});
}
function openNext(code){
	top.addTab("订单计划查询 ", "/b2b/monitor/purchaseOrderPlan.htmlx?planCode="+code);
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
		        field:'URGENCYLEVEL',
		        type:'combobox',
		        options:{
		            panelHeight:'auto',
		            editable:false,
		            data:[{value:'',text:'-请选择-'},
		                {value:'0',text:'紧急'},
		                {value:'1',text:'不紧急'}
		            ],
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'URGENCYLEVEL');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'URGENCYLEVEL',
		                        op: 'EQ',
		                        fieldType:'S',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'OPERATION',
		        type:'text',
		       	isDisabled:1
		        
		    }]);
	$('#dg').datagrid('reload');
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
	
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchasePlan/page.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
        	{field:'CODE',title:'采购计划号',width:22,align:'center'},
        	{field:'ORDERDATE',title:'采购计划时间',width:15,align:'center',
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
        	 {field:'WAREHOUSENAME',title:'收货地点',width:10,align:'center'}, 
		    
        	{field:'NUM',title:'采购数量',width:10,align:'right'},      	

			{field:'SUM',title:'采购金额（元）',width:15,align:'right',
				formatter: function(value,row,index){
					if (row.SUM){
						return common.fmoney(row.SUM);
					}
				}},
			{field:'URGENCYLEVEL',title:'紧急程度',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.URGENCYLEVEL == '0'){
						return "紧急";
					}else if(row.URGENCYLEVEL == '1'){
						return "不紧急";
					}
				}
			},
			{field:'OPERATION',title:'操作',width:10,align:'center',
	    		formatter: function(value,row,index){
	    			var str = "<img title='订单计划查询' onclick=openNext('"+row.CODE+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/redo.png' style='cursor: pointer;'/>";
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
			"query['t#code_S_EQ']":"<c:out value='${code}'/>"
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
			{field:'VENDORNAME',title:'供应商',width:20,align:'center'},
			{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
            {field:'AUTHORIZENO',title:'批准文号',width:10,align:'center'},
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
				}}
		]]/* ,
		onLoadSuccess: function(data){
			$('#dgDetail').datagrid('doCellTip',{delay:500}); 
		} */
	});
	
});
//=============ajax===============

</script>