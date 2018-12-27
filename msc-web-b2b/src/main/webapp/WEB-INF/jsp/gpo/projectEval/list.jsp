<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单计划列表" />
<style>
/* .datagrid-cell-c4-productId,.datagrid-cell-c4-status{
	color:#0081c2;
} */
</style>
<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;" >
	 <div id="tb" class="search-bar" >
		项目名称：<input  id="projectId" >
  		<span class="datagrid-btn-separator split-line" ></span>
  		<a href="#" id='doneBtn' class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true,disabled:true" onclick="doneFunc()">招标完成</a>
  		<span class="datagrid-btn-separator split-line" ></span>
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

var minPrice;
//查询
function searchDefectsList(row){
	minPrice = row.segmentBD;
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/pageMX.htmlx",  
	    queryParams:{  
	    	"query['t#projectDetail.id_L_EQ']":row.id,
	    }  
	});
}
//初始化
$(function(){
	
	$("#projectId").combobox({
	    url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/projectComb.htmlx",  
	    valueField:'id',    
	    textField:'name',   
	    width:'auto',
	    editable:false, 
	    onSelect:function(data){
	    	$("#doneBtn").linkbutton('enable');
	    	$('#dg').datagrid({
	    		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/page.htmlx",
	    		queryParams:{  
	    			"query['t#project.id_L_EQ']": data.id
	    		}  
	    	});
	    }
	});
	
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height:$(".my-north").height() ,
		pagination:true,
		
		pageSize:10,
		pageNumber:1,
		columns:[[
        	{field:'directory.genericName',title:'通用名',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.genericName;
        	}},
        	{field:'directory.dosageFormName',title:'剂型',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.dosageFormName;
        	}},
        	{field:'directory.model',title:'规格',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.model;
        	}},
        	{field:'directory.qualityLevel',title:'质量层次',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.qualityLevel;
        	}},
        	{field:'directory.minUnit',title:'最小制剂单位',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.minUnit;
        	}},
        	{field:'directory.note',title:'厂家备注',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.note;
        	}},
        	{field:'segmentBD',title:'区域最低价',width:10,align:'center',formatter: function(value,row,index){
        		if (row.segmentBD){
					return common.fmoney(row.segmentBD)+"（"+row.segmentStr+"）";
				}
        	}},
        	{field:'price',title:'价格比对',width:10,align:'center',
				formatter: function(value,row,index){
					return "<a  href='#' onclick='directoryPriceOpen("+index+")' class='easyui-linkbutton'>比价</a>";
			}}
   		]],
   		toolbar:"#tb",
		onClickRow: function(index,row){
        	searchDefectsList(row);
		  	return;
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
		height : '100%',
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
			{field:'vendorCode',title:'供应商编码',width:10,align:'center'},
        	{field:'vendorName',title:'供应商名称',width:20,align:'center'},
        	{field:'price',title:'价格',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				},styler: function(value,row,index){
					if (row.price - minPrice > 0){
						return 'color:red;';
					}else{
						return 'color:green;';
					}
				}},
			{field:'producerCode',title:'厂商编码',width:10,align:'center'},
        	{field:'producerName',title:'厂商名称',width:10,align:'center'},
        	{field:'productId',title:'对应药品',width:10,align:'center',
				editor:{
					type:'combogrid',
					options:{
						idField:'id',    
						textField:'name',
						pagination:true,
						
						/* url: " <c:out value='${pageContext.request.contextPath }'/>/dm/product/page.htmlx",
						queryParams:{
							 "query['t#isDisabled_I_NE']":"1",
							 "query['t#name_S_LK']":'石',
							 "query['t#producerName_S_LK']":'先声'
						}, */
						pageSize:20,
						pageNumber:1,
						panelAlign:"right",
					    columns: [[
					        {field:'id',title:'id',width:80},
					        {field:'code',title:'药品编码',width:80},
					        {field:'name',title:'药品名称',width:150},
					        {field:'dosageFormName',title:'剂型',width:80},
					        {field:'model',title:'规格',width:80},
					        {field:'packDesc',title:'包装规格',width:80},
					        {field:'producerName',title:'生产厂家',width:150}
					    ]],
					    width:200,
					    panelWidth:650,
						/* required: true, */
						delay:800,
						prompt:"名称模糊搜索",
						keyHandler: {
				            query: function(q) {
				            	/* var type = $('#orgType').combobox('getValue');
				            	if(type == 4){// 供应商c 取资料同 供应商
				            		type = 2;
				            	}
				                //动态搜索
				                $('#orgId').combogrid('grid').datagrid("reload",{"query['t#orgName_S_LK']":q, "query['t#orgType_I_EQ']":type});
				                $('#orgId').combogrid("setValue", q); */
				            }

				        }
					}
				},
				formatter: function(value,row,index){
					return row.productId;
				}
			},
			{field:'status',title:'评标结果',width:10,align:'center',
				editor:{
					type:'combobox',
					options:{
						valueField:'label',    
					    textField:'value',panelHeight:'auto',
						data: [{
							label: 'unwin',
							value: '未中标'
						},{
							label: 'win',
							value: '中标'
						}]
					}
				},
				formatter: function(value,row,index){
					if(row.status =="unwin"){
						return "未中标";
					}else if(row.status =="win"){
						return "中标";
					}
					return "";
				}
			},
			{field:'avgScore',title:'专家打分',width:10,align:'center',
				formatter: function(value,row,index){
					return "<a id='score"+row.id+"' href='#' onclick='add1open("+index+")' class='easyui-linkbutton'>"+row.avgScore+"</a>";
			}},
        	{field:'do',title:'操作',width:10,align:'center',
				formatter: function(value,row,index){
					return "<a class='dgbtn' href='#' onclick='doChoose("+index+")' class='easyui-linkbutton'>确认</a>";
			}}
		]],
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				$('.dgbtn:eq('+index+')').linkbutton({iconCls:'icon-ok',plain:true,height:20}); 
			}); 
		},
		onClickRow:function(index,row){
			try{
				$('#dgDetail').datagrid('beginEdit', index);
			}catch(e){}
			var ed = $('#dgDetail').datagrid('getEditor', {index:index,field:'productId'});
			if(ed.target){
				$(ed.target).combogrid('grid').datagrid({
					url: " <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/productComb.htmlx",
					queryParams:{
						 "query['t#isDisabled_I_NE']":"1",
						 "productName":row.productName,
						 "producerName":row.producerName
					}
				}); 
			}
		},
		onDblClickRow:function(index,row){
			openCompanyCert(row);
		}
	});
});

//公司资质
function openCompanyCert(row){
	top.$.modalDialog({
		title : "公司资质",
		width : 800,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/companyCert.htmlx",
		queryParams:{
			"vendorCode":row.vendorCode
		},
		onLoad:function(){
			
		},
		onClose:function() {
			top.$.modalDialog.handler.dialog('destroy');
			top.$.modalDialog.handler = undefined;
			//$('#dg1').datagrid('reload');  
		}
	});
	
}
//比价
function directoryPriceOpen(index){
	var  row = $("#dg").datagrid("getRows")[index];
	top.$.modalDialog({
		title : "比价",
		width : 800,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/directoryPrice.htmlx",
		queryParams:{
			"directoryId":row.directory.id
		},
		onLoad:function(){
			
		},
		onClose:function() {
			top.$.modalDialog.handler.dialog('destroy');
			top.$.modalDialog.handler = undefined;
			//$('#dg1').datagrid('reload');  
		}
	});
	
}
//招标完成
function doneFunc(){
	var projectId = $('#projectId').combobox('getValue');		
	$.messager.confirm('确认信息', '您确认要招标完成吗?', function(r){
		if (r){
			doneAjax(projectId);
		}
	});
}

//明细评标
function doChoose(index){
	var row = $("#dgDetail").datagrid("getRows")[index];

	var ed1 = $('#dgDetail').datagrid('getEditor', {index:index,field:'productId'});
	if(ed1 == null){
		showErr("请选择对应药品");
		return;
	}
	var ed = $('#dgDetail').datagrid('getEditor', {index:index,field:'status'});
	if(ed == null){
		showErr("请确定评标结果");
		return;
	}
	
	
	var isValid = $(ed.target).textbox('isValid');
	if(!isValid){
		return;
	}		
	var status = $(ed.target).combobox("getValue");
	var productId = $(ed1.target).combobox("getValue");
	var score = row.avgScore;
	doChooseAjax(row.id,productId,status,score);
}
//折页1窗口定标
var flagRow;
function add1open(index) {
	var  row = $("#dgDetail").datagrid("getRows")[index];
	var projectId = $("#projectId").combobox("getValue");
	top.$.modalDialog({
		title : "专家打分",
		width : 400,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/expert.htmlx",
		queryParams:{
			"projectId":projectId
		},
		onLoad:function(){
			
		},
		onClose:function() {
			top.$.modalDialog.handler.dialog('destroy');
			top.$.modalDialog.handler = undefined;
			//$('#dg1').datagrid('reload');  
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				var score = top.getScore();
				row.avgScore = score;
				$("#score"+row.id).text(score);
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
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
function doChooseAjax(id,productId,status,score){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/dochoose.htmlx",
		data:{
			"id":id,
			"status":status,
			"productId":productId,
			"score":score
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("操作成功！");
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function doneAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/done.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				location.reload();
				showMsg(data.msg);
			}else{
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>