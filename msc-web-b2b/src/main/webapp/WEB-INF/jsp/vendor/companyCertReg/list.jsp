<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout" >			
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>

</body>
</html>

<script>

//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -3,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/companyCertReg/page.htmlx",
		queryParams:{
		},
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
					{field:'createDate',title:'创建时间',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.createDate){
								return $.format.date(row.createDate,"yyyy-MM-dd HH:mm");
							}
						}
					},
		        	{field:'company.name',title:'公司名称',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.company){
								return row.company.fullName;
							}
						}
	    			},
		        	{field:'companyType',title:'公司类型',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			var ss = ""
							if (row.company){
								if(row.company.isProducer == 1){
									ss += "厂商";
								}
								if(row.company.isVendor == 1){
									if(ss != ""){
										ss += "、";
									}
									ss += "供应商";
								}
							}
		        			return ss;
						}
	    			},
		        	{field:'typeName',title:'证照类型',width:10,align:'center'},
		        	{field:'code',title:'证照代码',width:10,align:'center'},
		        	{field:'name',title:'证照名称',width:10,align:'center'},
		        	{field:'validDate',title:'证照有效期',width:10,align:'center'},
	    			{field:'auditStatus',title:'审核状态',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.auditStatus){
								if(row.auditStatus == 'create'){
									return "待发送";
								}
								if(row.auditStatus == 'send'){
									return "审核中";
								}
								if(row.auditStatus == 'pass'){
									return "审核通过";
								}
								if(row.auditStatus == 'back'){
									return "已退回";
								}							
							}
						}
	    			}
		   		]],
		toolbar:  [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen();
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();
			}
		},'-',{
			iconCls: 'icon-tip',
			text:"发送",
			handler: function(){
				sendFunc();
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
        
	}]);
	
});


//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/companyCertReg/add.htmlx",
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

//弹窗修改
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "修改",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/companyCertReg/edit.htmlx",
		queryParams:{
			id:selrow.id
		},
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");
				f.form("load", selrow);
			}
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

//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj.id);
		}
	});
}

//发出
function sendFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	if(selobj.auditStatus == 'send' || selobj.auditStatus == 'pass'){
		$.messager.alert("错误","资料已发送！","info");
		return;
	}
	sendAjax(selobj.id);
}

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/companyCertReg/del.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function sendAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/companyCertReg/send.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("发送成功！");
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>