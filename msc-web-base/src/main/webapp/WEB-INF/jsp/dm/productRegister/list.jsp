<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'申请单列表',collapsible:false" class="my-north" style="height:300px;">
	<div id="search-bar" style="padding:6px">
		申请日期: <input class="easyui-datebox" style="width:110px" id="startDate">
	    ~ <input class="easyui-datebox" style="width:110px" id="toDate">
    	状态: 
	 	<input class="easyui-combobox" style="width:100px" id="status"/>
	    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="query()">查询</a>
	</div>
	<div>
		<table  id="dg" ></table>
	</div>
</div>

<div data-options="region:'center',title:''"   style="background:#eee;">
    <table id="dgDetail"></table>
</div>
</body>
</html>
<script>

//检索
function query(){
	var startDate="";
	var toDate="";
	var status = $("#status").datebox('getValue');
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	
	
	$('#dg').datagrid('load',{"query['t#createDate_D_GE']": startDate,"query['t#createDate_D_LE']": toDate,"query['t#status_S_EQ']":status});
}

//查询
function searchDefectsList(value){
	if(value == "")
		return;
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productRegister/mxlist.htmlx",  
	    queryParams:{  
	    	"query['t#productRegister.id_L_EQ']":value.id
	    }  
	});
}
//删除
function delCp(){

	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var status = selobj.status;
	if(status != 'unaudit'){
		$.messager.alert('错误','只可删除未审核申请单!','info');
		return;
	}
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此申请单?', function(r){
		if (r){
			delAjax(id);
		}
	});
}



//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "备案申请",
		width : 800,
		height : 550,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/productRegister/add.htmlx",
		onLoad:function(){
		},
		buttons : [ {
			text : '提交',
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


	

//初始化
$(function(){
	$("#status").combobox({    
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:160,
	    editable:false,
	    data:[{
	    	label: '',
			value: '全部'
		},{
			label: 'unaudit',
			value: '未审核'
		},{
			label: 'agree',
			value: '同意'
		},{
			label: 'disagree',
			value: '不同意'
		}]
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height()-$("#search-bar").height() -12,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productRegister/page.htmlx",
		pageSize:10,
		pageNumber:1,
		checkOnSelect:true,
		columns:[[
			{field:'createDate',title:'申请日期',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.createDate){
						return $.format.date(row.createDate,"yyyy-MM-dd HH:mm:ss");
					}
				}
			},
        	{field:'orgName',title:'机构名称',width:25,align:'center'},
        	{field:'auditor',title:'审核人',width:20,align:'center'},
        	{field:'suggestion',title:'审核意见',width:20,align:'center'},
        	{field:'status',title:'状态',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.status == 'unaudit'){
						return "未审核";
					}else if(row.status == 'agree'){
						return "同意";
					}else if(row.status == 'disagree'){
						return "不同意";
					}
				}
			}
   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"备案申请",
			handler: function(){
				addOpen();	
			}
		}/* ,'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delCp();
				return;
				auth();
			}
		} */],
		onClickRow: function(index,row){			
        	var selobj1 = $('#dg').datagrid('getSelected');
        	searchDefectsList(selobj1);
		  	return;
		},
		onLoadSuccess: function(data){
			var rows=$(this).datagrid("getRows");
			//大于1行默认选中
			if($(this).datagrid("getRows").length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList($(this).datagrid("getRows")[0]);
			}
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
			{field:'productName',title:'药品名称',width:20,align:'center'},
			{field:'dosageFormName',title:'剂型名称 ',width:15,align:'center'},
			{field:'model',title:'规格',width:10,align:'center'},
			{field:'packDesc',title:'包装规格 ',width:15,align:'center'},
			{field:'producerName',title:'生产企业 ',width:15,align:'center'},
			{field:'authorizeNo',title:'国药准字 ',width:15,align:'center'}
		]]
	});
	
});

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productRegister/delete.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');
				$('#dgDetail').datagrid('loadData',{total:0,rows:[]});

				showMsg("申请单已删除");
			} else {
				showMsg("删除失败");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>