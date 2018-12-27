<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout">
	<div class="single-dg">
		<table id="dg"></table>
	</div>

</body>
</html>

<script>
function addFilter(){
	$('#dg').datagrid('enableFilter', [{
		 field:'CODE',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'NAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'PINYIN',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'GENERICNAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'DOSAGEFORMNAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'PRODUCERNAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'MODEL',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'PACKDESC',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'UNITNAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'FINALPRICE',
		 type:'text',
		 isDisabled:1
	},{
		 field:'VENDORNAME',
		 type:'text'
	},{
		 field:'NUM',
		 type:'text',
		 isDisabled:1
	}]
	
	);
}

//初始化
$(function(){
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/serviceJudge/page.htmlx",
		filterDelay:100,
		columns:[[
		        	{field:'name',title:'名称',width:20,align:'center'},
		        	{field:'describe',title:'描述',width:15,align:'center'},
		        	{field:'deduct',title:'扣分',width:10,align:'center'},
		        	{field:'auditor',title:'审核人',width:10,align:'center'},
		        	{field:'auditDate',title:'审核日期',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.auditDate){
								return $.format.date(row.auditDate,"yyyy-MM-dd HH:mm");
							}
						}},
		        	{field:'status',title:'状态',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.status=="unaudit"){
								return "未审核";
							}else if (row.status=="agree"){
								return "同意";
							}else if (row.status=="disagree"){
								return "不同意";
							}
						}}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addPara();
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();
			}
		}],
		onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});
	
	addFilter();

	//弹窗新增
	function addPara() {
		top.$.modalDialog({
			title : "添加",
			width : 600,
			height : 400,
			href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/serviceJudge/add.htmlx",
			onLoad:function(){
				
			},
			buttons : [ {
				text : '保存',
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
	
	
	
});

//删除
function delFunc(){
	
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	if(selobj.status !="unaudit"){
		showErr("只能删除【未审核】的投诉");
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj.id);
		}
	});
}




//=============ajax===============


function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/serviceJudge/delete.htmlx",
		data:{
			"id":id
		},
		dataType:"json",
		type:"POST",
		cache:false,
		//traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			} else{
				showMsg("该数据已被使用，无法删除");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>