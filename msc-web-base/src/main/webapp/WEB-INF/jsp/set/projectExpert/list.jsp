<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
	<div  data-options="region:'west',title:'项目',collapsible:false" class="my-west" style="width:30%;">
		<table  id="dg" ></table>
	</div>
       
	<div data-options="region:'center',title:'专家组'" class="my-center" style="">
			
			
				<table id="dg2" ></table>
		
	</div>
</body>
</html>
<script>
//搜索
function search(){
	var selrow = $('#dg').datagrid('getSelected');
	$('#dg2').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/set/projectExpert/page.htmlx",  
	    queryParams:{
	    	"query[t#project.id_L_EQ]":selrow.id
	    }
	}); 
}

//初始化
$(function(){

	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-35,
		pagination:true,
		pageSize : 20,
		pageNumber : 1,
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/page.htmlx",
		remoteFilter: true,
		columns:[[
					{field:'code',title:'项目编号',width:10,align:'center'},
					{field:'name',title:'项目名称',width:10,align:'center'}
		   		]],
		onClickRow: function(index,row){
			search();  
		}
	});
	
	$('#dg').datagrid('getPager').pagination({
		showPageList : false,
		showRefresh : false,
		displayMsg : "共{total}记录"
	});
	
	//药房设置
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		editable:true,
		height :  $(this).height()-35,
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'expert.code',title:'编号',width:20,align:'center',
						formatter: function(value,row,index){
							return row.expert.code;
						}},
		        	{field:'expert.name',title:'姓名',width:20,align:'center',
							formatter: function(value,row,index){
								return row.expert.name;
							}},
		        	{field:'expert.courseName',title:'所属学科',width:20,align:'center',
								formatter: function(value,row,index){
									return row.expert.courseName;
								}},
		        	{field:'expert.sex',title:'性别',width:15,align:'center',
						formatter: function(value,row,index){
							if (row.expert.sex==1){
								return "男";
							}else if (row.expert.sex==0){
								return "女";
							}
						}},
		        	{field:'expert.mobile',title:'手机',width:20,align:'center',
							formatter: function(value,row,index){
								return row.expert.mobile;
							}},
		        	{field:'expert.orgName',title:'机构名称',width:30,align:'left',
								formatter: function(value,row,index){
									return row.expert.orgName;
								}}
   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"随机生成专家组",
			handler: function(){
				addOpen();	
			}
		}]
	});

});

//弹窗增加
function addOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow==null){
		showErr("请先选择项目");
		return;
	}
	top.$.modalDialog({
		title : "随机选取",
		width : 700,
		height : 400,
		iconCls: 'icon-add',
		href : "<c:out value='${pageContext.request.contextPath }'/>/set/projectExpert/add.htmlx",
		queryParams:{
			"projectId":selrow.id
		},
		onLoad:function(){
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg2');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				top.addFunc();
				/* var f = top.$.modalDialog.handler.find("#form1");
				f.submit(); */
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


</script>