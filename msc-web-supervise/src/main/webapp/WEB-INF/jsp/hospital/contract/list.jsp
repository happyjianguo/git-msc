<%@page import="com.shyl.common.web.datasource.ApplicationContextHolder"%>
<%@page import="com.shyl.msc.common.CommonProperties"%>
<%@page import="com.shyl.common.web.util.VerifyCodeUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<OBJECT  id="obj" classid="clsid:3BC3C868-95B5-47ED-8686-E0E3E94EF366" style="display:none;"></OBJECT>

<div id="tt">
	    <div title="合同列表" class="my-tabs" >
	       <div id="toolbar" class="search-bar" style="height:30px;">
			 <shiro:hasPermission name="hospital:contract:sign"> 
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" onclick="okFunc()">签订</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delFunc()">作废</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="paraOpen()">合同终止申请</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		    </shiro:hasPermission>
		  	<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true"  onclick="filterFunc()">数据过滤</a>
	    </div>
  		<select id="UserList" name="userName" style="display:none"></select>
		<input type="hidden" ID="UserSignedData" name="UserSignedData">
		<input type="hidden" ID="UserCert" name="UserCert"> 
		<table  id="dg" ></table>
	    </div>
	    <div title="合同明细" class="my-tabs" >
	    
	    	<div id="tbd" class="search-bar" >
	    	<shiro:hasPermission name="hospital:contract:sign">
	    		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true"  onclick="deleteD()">删除</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="paraOpenD()">合同终止申请</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  	</shiro:hasPermission>
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
		    },{
		        field:'filePath',
		        type:'text',
		    	isDisabled:1
		    }]);
}
//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-($("#toolbar").length ==0?0:30),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/page.htmlx",
		queryParams:{  
	    	"query['t#code_S_EQ']":"<c:out value='${code}'/>",
	    },
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'code',title:'三方合同编号',width:20,align:'center'},
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
        	/*{field:'filePath',title:'签订PDF',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.filePath){
						return "<a href=\" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/readfile.htmlx?id="+row.id+"\" target='_bank'><img src=\" <c:out value='${pageContext.request.contextPath }'/>/resources/images/fileDown.png\" width=16 height=16  />点击下载</a>";
					}
				}
        	},*/
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
		toolbar:"#toolbar",
		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
		},
		onLoadSuccess: function(data){
			//var rows=$(this).datagrid("getRows");
			//大于1行默认选中
			//if($(this).datagrid("getRows").length > 0) {
				//$(this).datagrid('selectRow', 0);
	        	//searchDefectsList($(this).datagrid("getRows")[0]);
			//}
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
	});
	
		
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-($("#tbd").length ==0?0:50),
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
	});
});



//查询
function searchDefectsList(row){
	$("#contractCode").html(row.code);
	$("#hospitalName").html(row.hospitalName);
	$("#vendorName").html(row.vendorName);
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/mxlist.htmlx",  
	    queryParams:{  
	    	"query['t#contract.id_L_EQ']":row.id,
	    }  
	});
}

</script>