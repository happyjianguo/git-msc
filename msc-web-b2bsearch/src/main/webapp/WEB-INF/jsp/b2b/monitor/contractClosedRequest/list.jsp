<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="合同结案申请查询" />

<html>
<body class="easyui-layout" >			
	<div id="tt">
	    <div title="合同列表" class="my-tabs" >
			<table  id="dg" ></table>
	    </div>
	    <div title="合同明细" class="my-tabs" >
	    	<div id="tbd" class="search-bar" >
	    		<b>三方合同编号：</b><i id="contractCode"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    		<b>申请人：</b><i id="closedMan"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    		<b>原因：</b><i id="reason"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    	</div>
	       	<table id="dgDetail"></table>
	    </div>
	</div>
</body>

</html>
<script>
function searchDefectsList(row){
	$("#contractCode").html(row.contractCode);
	$("#closedMan").html(row.closedMan);
	$("#reason").html(row.reason);
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/contractClosedRequest/mxlist.htmlx",  
	    queryParams:{  
	    	"id":row.id,
	    }  
	});
}
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	//日期
	$('#orderDate1').datebox({
		editable:false
	});
	$('#orderDate2').datebox({
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
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :$(this).height() - 33,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/contractClosedRequest/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:"#tb",
		columns:[[
        	{field:'contractCode',title:'合同编号',width:15,align:'center'},
        	{field:'closedRequestDate',title:'结案申请时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.closedRequestDate){
						return $.format.date(row.closedRequestDate,"yyyy-MM-dd HH:mm");
					}
				}
			}, 
			{field:'closedMan',title:'结案申请人',width:10,align:'center'},
			{field:'reason',title:'原因',width:20,align:'center'},
			{field:'reply',title:'答复',width:20,align:'center'},
			{field:'status',title:'状态',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.status == 'unaudit'){
						return "未审核";
					}else if(row.status == 'agree'){
						return "同意终止";
					}else if(row.status == 'disagree'){
						return "不同意终止";
					}
				}
			}
   		]],
   		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
		},
	});

	$('#dg').datagrid('enableFilter', 
			[{
		        field:'closedRequestDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'status',
		        type:'combobox',
		        options:{
		            panelHeight:'auto',
		            editable:false,
		            data:[{value:'',text:'-请选择-'},
		                {value:'unaudit',text:'未审核'},
		                {value:'agree',text:'同意终止'},
		                {value:'disagree',text:'不同意终止'}
		                
		            ],
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'status');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'status',
		                        op: 'EQ',
		                        fieldType:'S',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    }]);
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height()-33,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		showFooter: true,
		toolbar:"#tbd",
		columns:[[
        	{field:'productCode',title:'药品编码',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.code;
					}
				}
        	},
        	{field:'productName',title:'药品名称',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.name;
					}
				}
        	},
        	{field:'product.genericName',title:'通用名',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.genericName;
					}
				}
        	},
        	{field:'product.dosageFormName',title:'剂型',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.dosageFormName;
					}
				}},
        	{field:'product.model',title:'规格',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.model;
					}
				}},
        	{field:'product.packDesc',title:'包装',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.packDesc;
					}
				}},
        	{field:'product.unitName',title:'单位',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.unitName;
					}
				}},
        	{field:'product.producerName',title:'生产企业',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.producerName;
					}
				}},
        	{field:'price',title:'单价（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
        	{field:'contractNum',title:'合同数量',width:10,align:'right'},
        	{field:'contractAmt',title:'合同金额',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.contractAmt){
						return common.fmoney(row.contractAmt);
					}
				}},
   		]],		
	});
	
})

function openPreP(code){
	top.addTab("配送单查询 ", "/b2b/monitor/deliveryOrder.htmlx?code="+code);
}

//=============ajax===============

</script>