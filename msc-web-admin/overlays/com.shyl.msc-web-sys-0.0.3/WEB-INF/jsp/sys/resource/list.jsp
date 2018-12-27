<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<body class="easyui-layout"  >
            <div  data-options="region:'west',title:'资源列表',collapsible:false" style="width:50%;background: rgb(238, 238, 238);padding:2px">
					<div class="search-bar">
						<input id="ss" />
						<div id="mm">
							<div data-options="name:'name' ">资源名称</div>
							<div data-options="name:'icon' ">资源图标</div>
							<div data-options="name:'url'">资源路径</div>
						</div>
					</div>
					<table  id="dgL" ></table>
            </div>
            
            <div data-options="region:'center',title:'权限列表'" style="background: rgb(238, 238, 238);padding:2px">
            	<table id="dg" ></table>
            </div>
            
            
			

</body>
</html>

<script>
function searchList(val,name){
	if(name=="name"){
		$('#dgL').datagrid('reload',{
			"query['t#name_S_LK']": val
		});	
		
	}else if(name=="icon"){
		$('#dgL').datagrid('reload',{
			"query['t#icon_S_LK']": val
		});	
	}else if(name=="url"){
		$('#dgL').datagrid('reload',{
			"query['t#url_S_LK']": val
		});	
	}
	
}
//初始化
$(function(){
	$('#ss').searchbox({
		searcher:function(value,name){ 
			searchList(value,name);
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	//datagrid
	$('#dgL').datagrid({
		idField:'id',    
	    treeField:'name',
	    parentField:'parentId',
	    method:'post',
	    animate:true,
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :  $(this).height()-68,
		url:"${pageContext.request.contextPath}/sys/resource/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'name',title:'资源名称',width:10,align:'left'},
		        	{field:'icon',title:'资源图标',width:10,align:'left',
		        		formatter: function(value,row,index){
		        			if(row.icon)
		        				return "<a class='dgbtn' href='#'  class='easyui-linkbutton' data-options=\"iconCls:'"+row.icon+"'\" ></a>"+row.icon;
						}},
		        	{field:'url',title:'资源路径',width:20,align:'left'}
		   		]],
   		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				leftaddOpen(); 
				return;	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delResourceFunc();
				return;	
			}
		}],
		onClickRow:function(index,row){
			listFunc(row.id);
			return;
		},
		onDblClickRow:function(index,row){
			lefteditOpen(); 
			return;
		},
		onLoadSuccess:function(data){
			$('.dgbtn').linkbutton({plain:true,height:20}); 
		}
	});
	
	$("#dgL").datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false
	})	

	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :  $(this).height()-60,
		url:"${pageContext.request.contextPath}/sys/authority/list.htmlx",
		columns:[[
		        	{field:'name',title:'权限名称',width:11},
		        	{field:'url',title:'权限路径',width:11},
		        	{field:'permCode',title:'权限编码',width:11}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen(); 
				return;	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();
				return;	
			}
		}],
		onDblClickRow:function(index,row){
			editOpen(); 
			return;
		}
	});

	//弹窗增加left
	function leftaddOpen() {
		top.$.modalDialog({
			title : "添加资源",
			width : 600,
			height : 400,
			href : "${pageContext.request.contextPath}/sys/resource/add.htmlx",
			onLoad:function(){
			},
			buttons : [ {
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					top.$.modalDialog.openner= $('#dgL');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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
	//弹窗修改left
	function lefteditOpen() {
		var selrow = $('#dgL').datagrid('getSelected');
		top.$.modalDialog({
			title : "编辑资源",
			width : 600,
			height : 400,
			href : "${pageContext.request.contextPath}/sys/resource/edit.htmlx",
			queryParams:{  
		    	"id":selrow.id
			},
			onLoad:function(){
				var selrow = $('#dgL').datagrid('getSelected');
				if(selrow){
					var f = parent.$.modalDialog.handler.find("#form1");
					f.form("load", selrow);
				}
			},
			buttons : [ {
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					top.$.modalDialog.openner= $('#dgL');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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
	//弹窗增加
	function addOpen() {
		top.$.modalDialog({
			title : "添加权限",
			width : 600,
			height : 400,
			href : "${pageContext.request.contextPath}/sys/authority/add.htmlx",
			onLoad:function(){
				var selrow = $('#dgL').datagrid('getSelected');
				if(selrow){
					var rn = top.$.modalDialog.handler.find("#resourceName");
					rn.html(selrow.name);
					var pid = top.$.modalDialog.handler.find("#parentId");
					pid.val(selrow.id);
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

	//弹窗修改
	function editOpen() {
		top.$.modalDialog({
			title : "编辑权限",
			width : 600,
			height : 400,
			href : "${pageContext.request.contextPath}/sys/authority/edit.htmlx",
			onLoad:function(){
				var selrow = $('#dg').datagrid('getSelected');
				if(selrow){
					var f = parent.$.modalDialog.handler.find("#form1");
					f.form("load", selrow);
				}
				selrow = $('#dgL').datagrid('getSelected');
				if(selrow){
					var rn = top.$.modalDialog.handler.find("#resourceName");
					rn.html(selrow.name);
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
});

//查询权限列表
function listFunc(id){
	$('#dg').datagrid('load',{
		'id': id
	});	
}
//删除资源
function delResourceFunc(){
	var selobj = $('#dgL').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	var id= $('#dgL').datagrid('getSelected').id;
	
	$.messager.confirm('确认信息', '确认要删除此资源?', function(r){
		if (r){
			delResourceAjax(id);
		}
	});
}

//删除
function delFunc(){

	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	var id= $('#dg').datagrid('getSelected').id;
	$.messager.confirm('确认信息', '确认要删除此成员?', function(r){
		if (r){
			delAjax(id);
		}
	});
}

//搜索
function search(val){
	$('#dg').datagrid('load',{
		"query['t#fieldA_S_LK']": val
	});	
}

//=============ajax===============
function delResourceAjax(id){
	$.ajax({
				url:"${pageContext.request.contextPath }/sys/resource/del.htmlx",
				data:"id="+id,
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dgL').datagrid('reload');  
						showMsg("删除成功！");
					} 
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}

function delAjax(id){
	$.ajax({
				url:"${pageContext.request.contextPath }/sys/authority/del.htmlx",
				data:"id="+id,
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$("#dg").datagrid('reload');
						showMsg("删除成功！");
					}else{
						showMsg("该数据已被使用，无法删除");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}
</script>