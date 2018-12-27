<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="配送单列表" />

<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout"  >
    <div  data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;" >
		<div id="tb" class="search-bar" >
			配送日期: <input class="easyui-datebox" style="width:110px" id="startDate">
	        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
	        <span class="datagrid-btn-separator split-line" ></span>
	        状态: 
		 	<input class="easyui-combobox" style="width:80px" id="status"/>
		 药品编码: 
	 	<input id="productCode" class="easyui-textbox" style="width:100px"/>
	 	药品名称: 
	 	<input id="productName" class="easyui-textbox" style="width:100px"/>
	 	拼音检索: 
	 	<input id="pinyin" class="easyui-textbox" style="width:150px"/>
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
			<%--<shiro:hasPermission name="b2b:monitor:deliveryOrder:doDelete">
				<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="doDelete()"> 删除</a>
			</shiro:hasPermission>--%>
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
		        field:'RETURNSNUM',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'INOUTBOUNDNUM',
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
$(function(){

	//日期
	$('#deliveryDate1').datebox({
		editable:false
	});
	$('#deliveryDate2').datebox({
		editable:false
	});
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
	    panelHeight:100,
	    editable:false,
	    data:[{
	    	label: '',
			value: '全部'
		},{
			label: 'unreceive',
			value: '未收货'
		},{
			label: 'receiving',
			value: '收货中'
		},{
			label: 'closed',
			value: '收货完成'
		}],
		onChange:function(value){
			dosearch();
		}
	});

	//组成员
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		rownumbers:true,
		showFooter: false,
		singleSelect:true,
		height :'100%',
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
			{field:'GOODSNUM',title:'配送数量',width:10,align:'center'},
			/* {field:'inOutBoundGoodsNum',title:'入库数量',width:10,align:'center'},
			{field:'returnsGoodsNum',title:'退货数量',width:10,align:'center'}, */
			{field:'GOODSSUM',title:'配送金额 （元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.GOODSSUM){
						return common.fmoney(row.GOODSSUM);
					}
				}},	
			{field:'BATCHCODE',title:'批号',width:10,align:'center'},
			{field:'BATCHDATE',title:'生产日期',width:10,align:'center'},
			{field:'EXPIRYDATE',title:'有效期',width:10,align:'center'},
			{field:'DO',title:'更多',width:5,align:'center',
        		formatter: function(value,row,index){
        			if(row.PRODUCTCODE != "合计：")
						return "<a class='dgbtn' href='#' onclick='moreInfo("+index+")' class='easyui-linkbutton'></a>";
				}}
		]],
		onLoadSuccess:function(data){
			$('.dgbtn').linkbutton({iconCls:'icon-defect_define',plain:true,height:20}); 
			
		}
	});

	
	//配送单组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :$(".my-north").height(),
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/deliveryOrder/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		toolbar:"#tb",
		/* frozenColumns:[[
			        	{field:'code',title:'配送单号',width:140,align:'center'},
						{field:'orderDate',title:'配送时间',width:130,align:'center',
							formatter: function(value,row,index){
								if (row.orderDate){
									return $.format.date(row.orderDate,"yyyy-MM-dd HH:mm");
								}
							}
						}]], */
		columns:[[
					{field:'CODE',title:'配送单号',width:25,align:'center'},
					{field:'ORDERDATE',title:'配送时间',width:20,align:'center',
						formatter: function(value,row,index){
							if (row.ORDERDATE){
								return $.format.date(row.ORDERDATE,"yyyy-MM-dd HH:mm");
							}
						}
					},
					{field:'VENDORNAME',title:'供应商',width:20,align:'center'},
					{field:'HOSPITALNAME',title:'医疗机构',width:20,align:'center'},
					{field:'SENDERNAME',title:'配送商',width:20,align:'center'}, 
					{field:'BARCODE',title:'条形码',width:30,align:'center'},
					{field:'NUM',title:'配送数量',width:10,align:'center'},
					{field:'SUM',title:'配送金额（元）',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.SUM){
								return common.fmoney(row.SUM);
							}
						}},
				/* 	{field:'inOutBoundNum',title:'入库数量',width:100,align:'center'},
					{field:'returnsNum',title:'退货数量',width:100,align:'center'}, */
					
					{field:'STATUS',title:'状态',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.STATUS == '0'){
								return "未收货";
							}else if(row.STATUS == '1'){
								return "收货中";
							}else if(row.STATUS == '2'){
								return "收货完成";
							}
						}
					},
					{field:'OPERATION',title:'操作',width:10,align:'center',
			    		formatter: function(value,row,index){
			    			var str = "<img title='订单查询("+row.PURCHASEORDERCODE+")' onclick=openPre('"+row.PURCHASEORDERCODE+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png' style='cursor: pointer;'/>"
			    			+"&nbsp;<img title='入库单查询' onclick=openNextR('"+row.CODE+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/redo.png' style='cursor: pointer;'/>"
			    			/* +"&nbsp;<img title='发票查询' onclick=openNextF('"+row.code+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/redo.png' style='cursor: pointer;'/>" */;
							return str;
						}
					}
		   		]],
		onClickRow: function(index,row){
			todg2(row);  
		},
		onDblClickRow: function(index,row){
			top.addTab("入库单查询 ", "/b2b/monitor/inOutBound.htmlx?deliveryOrderCode="+row.CODE);
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
			"query['t#code_S_EQ']":"<c:out value='${code}'/>",
			"query['t#purchaseOrderCode_S_EQ']":"<c:out value='${purchaseOrderCode}'/>"
		}
	});
	
	
	
});
function moreInfo(index){
	var rows = $('#dg2').datagrid('getRows');    // get current page rows
	var row = rows[index];
	top.$.modalDialog({
		title : "信息",
		width : 400,
		height : 200,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/deliveryOrder/moreinfo.htmlx",
		queryParams:{
	        "id":row.ID
	    },
		onLoad:function(){
			var f = parent.$.modalDialog.handler.find("#form1");
			f.form("load", row);
		}
	});
}
function openPre(code){
	top.addTab("订单查询 ", "/b2b/monitor/purchaseOrder.htmlx?code="+code);
}
function openNextR(code){
	top.addTab("入库单查询", "/b2b/monitor/inOutBound.htmlx?deliveryOrderCode="+code);
}
/* function openNextF(code){
	top.addTab("发票查询", "/b2b/monitor/invoice.htmlx?deliveryOrderCode="+code);
} */
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
    var status = $("#status").datebox('getValue');
	$('#dg2').datagrid({
	    url:' <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/deliveryOrder/mxlist.htmlx',  
	    queryParams:{
	        "query['t#deliveryOrderId_L_EQ']":row.ID,
			"query['t#orderDate_D_GE']": startDate,
			"query['t#orderDate_D_LE']": toDate,
			"query['op#status_S_EQ']":status,
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
//=============ajax===============
function doDelete() {
    var selobj = $('#dg').datagrid('getSelected');
    if(selobj == null){
        return;
    }
    console.log(123);
    console.log(selobj);
    $.messager.confirm('确认信息', '您确认要删除吗?', function(r){
        if (r){
            delAjax(selobj.ID);
        }
    });
}

function delAjax(id) {
    $.ajax({
        url:"<c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/deliveryOrder/doDelete.htmlx",
        data:{"id":id},
        dataType:"json",
        type:"POST",
        cache:false,
        success:function(data){
            if(data.success){
                showMsg("删除成功！" + data.msg);
            }else{
                showErr(data.msg);
            }
        },
        error:function(){
            showErr("出错，请刷新重新操作");
        }
    });
}
</script>