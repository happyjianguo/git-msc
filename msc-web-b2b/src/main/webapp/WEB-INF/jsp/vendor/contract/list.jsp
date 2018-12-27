<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单计划列表" />
<html>
<body class="easyui-layout" >

	<div id="tt">
	    <div title="合同列表" class="my-tabs" >
	    	<div id="tb" class="search-bar" >
		        <span class="datagrid-btn-separator split-line" ></span>
				<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" onclick="checkFunc()">签订</a>
				<span class="datagrid-btn-separator split-line" ></span>
				<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true" onclick="uncheckFunc()">驳回</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
		    </div>
			<div>
				<table  id="dg"></table>
			</div>
	     </div>
	    <div title="合同明细" class="my-tabs" >
	    	<div id="tbd" class="search-bar" >
	    		<b>三方合同编号：</b><i id="contractCode"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    		<b>医院名称：</b><i id="hospitalName"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    		<b>供应商名称：</b><i id="vendorName"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    	</div>
	    	<table id="dgDetail"></table>
	    </div>
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
		        field:'status',
		        type:'combobox',
		        options:{
		            panelHeight:'auto',
		            editable:false,
		            data:[{value:'',text:'-请选择-'},
		                {value:'unsigned',text:'待签订'},
		                {value:'rejected',text:'已驳回'},
		                {value:'hospitalSigned',text:'医院签订'},
		                {value:'signed',text:'已签订'},
		                {value:'executed',text:'执行完'},
		                {value:'cancel',text:'已作废'},
		                {value:'stop',text:'已终止'}
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
		    },{
		        field:'startValidDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'S',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'endValidDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'S',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'effectiveDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'hospitalConfirmDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'vendorConfirmDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'gpoConfirmDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
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
	$("#contractCode").html(row.code);
	$("#hospitalName").html(row.hospitalName);
	$("#vendorName").html(row.vendorName);
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/contract/mxlist.htmlx", 
	    queryParams:{  
	    	"query['t#contract.id_L_EQ']":row.id,
	    }  
	});
}
//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-33,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/contract/page.htmlx",
		pageSize:20,
		pageNumber:1,
		columns:[[
        	{field:'code',title:'三方合同编号',width:18,align:'center'},
        	{field:'hospitalName',title:'医院名称',width:10,align:'center'},
        	{field:'gpoName',title:'GPO名称',width:10,align:'center'},
        	{field:'vendorName',title:'供应商名称',width:10,align:'center'},
        	{field:'hospitalConfirmDate',title:'医院确认时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.hospitalConfirmDate){
						return $.format.date(row.hospitalConfirmDate,"yyyy-MM-dd HH:mm");
					}
				}
        	},
        	{field:'gpoConfirmDate',title:'gpo确认时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.gpoConfirmDate){
						return $.format.date(row.gpoConfirmDate,"yyyy-MM-dd HH:mm");
					}
				}
        	},
        	{field:'vendorConfirmDate',title:'供应商确认时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.vendorConfirmDate){
						return $.format.date(row.vendorConfirmDate,"yyyy-MM-dd HH:mm");
					}
				}
        	},
        	{field:'startValidDate',title:'有效期起',width:10,align:'center'},
        	{field:'endValidDate',title:'有效期止',width:10,align:'center'},
        	{field:'effectiveDate',title:'生效时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.effectiveDate){
						return $.format.date(row.effectiveDate,"yyyy-MM-dd HH:mm");
					}
				}
        	},
        	{field:'filePath',title:'签订PDF',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.filePath){
						return "<a href=' <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/readfile.htmlx?id="+row.id+"' target='_bank'><img src=' <c:out value='${pageContext.request.contextPath }'/>/resources/images/fileDown.png' width=16 height=16  />点击下载</a>";
					}
				}
        	},
			{field:'status',title:'状态',width:10,align:'center',
				formatter: function(value,row,index){
					if (value == "unsigned"){
						return "待签订";
					} else if (value == "rejected"){
						return "已驳回";
					} else if (value == "hospitalSigned"){
						return "医院签订";
					} else  if(value == "signed"){
						return "已签订";
					} else if(value == "executed"){
						return "执行完";
					} else if(value == "cancel"){
						return "已作废";
					} else if(value == "stop"){
						return "已终止";
					}
				}}
   		]],
   		toolbar:"#tb",
   		onDblClickRow: function(index,row){
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
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
		height : $(this).height()-33,
		pagination:true,
		pageSize:20,
		pageNumber:1,
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
        	{field:'purchasePlanNum',title:'采购计划数量',width:10,align:'right'},
        	{field:'purchaseNum',title:'采购数量',width:10,align:'right'},
        	{field:'deliveryNum',title:'配送数量',width:10,align:'right'},
        	{field:'returnsNum',title:'退货数量',width:10,align:'right'},
        	{field:'status',title:'是否终止',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.status == "stop"){
						return "已终止";
					}
				}
        	}
			
		]],
		onDblClickRow: function(index,row){
			checkDetailAjax(row.id);
		},
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				$('#dgDetail').datagrid('beginEdit', index);
			}); 
		}
	});
});


function checkFunc(){
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow== null){
		showErr("请先选择一笔合同");
		return;
	}
	
	if(selrow.status != "hospitalSigned" ){
		showErr("请选择［医院签订］的合同");
		return;
	}  
	
	$.messager.confirm('确认信息', '确认签订吗?', function(r){
		if (r){
			checkAjax(selrow.id);
		}
	});
	
}

function uncheckFunc(){
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow== null){
		showErr("请先选择一笔合同");
		return;
	}
	if(selrow.status != "hospitalSigned" ){
		showErr("请选择［医院签订］的合同");
		return;
	}  
	
	$.messager.confirm('确认信息', '确认拒签吗?', function(r){
		if (r){
			uncheckAjax(selrow.id);
		}
	});
	
}

//=============ajax===============




function checkAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/contract/check.htmlx",
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
				showMsg("签订成功！");
				//successOpen(data.data);
			} else{
				showErr("无法采购，取消订单计划");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function uncheckAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/contract/uncheck.htmlx",
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
				showMsg("拒签！");
				//successOpen(data.data);
			} else{
				showErr("无法采购，取消订单计划");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>