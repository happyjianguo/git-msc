<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="患者及配送点维护" />

<html>

<body class="easyui-layout"  >
	<div  data-options="region:'west',title:'患者',collapsible:false" style="width:50%;min-width:300px;background: rgb(238, 238, 238)">
		<div style="padding:3px">
			<input id="ss1" style="display:none"/>
			<div id="mm">
				<div data-options="name:'name' ">患者姓名</div>
				<div data-options="name:'idCode'">身份证号</div>
			</div> 
		</div>
			<table  id="dg" ></table>
	</div>
	
	<div data-options="region:'center',title:'收货地址'" style="width:50%;min-width:300px;background: rgb(238, 238, 238);">
		<div style="padding:3px">
			<input id="ss2" style="display:none"/>
			<div id="mm">
				<div data-options="name:'name' ">名称</div>
			</div> 
		</div>
		<table id="dg2" ></table>
	</div>
</body>

</html>
<script>
$(function(){
	//患者新增修改
	function addOpen(isedit) {
		var href = " <c:out value='${pageContext.request.contextPath }'/>/set/patient/add.htmlx";
		var selrow;
		if (isedit) {
			selrow = $('#dg').datagrid('getSelected')
			href = " <c:out value='${pageContext.request.contextPath }'/>/set/patient/edit.htmlx";
		}
		top.$.modalDialog({
			title : "患者及配送点信息",
			width : 600,
			height : 390,
			href : href,
			onLoad:function(){
				if(isedit && selrow){
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
	//地址新增修改
	function subaddOpen(isEdit) {
		var selrow = $('#dg').datagrid('getSelected');
		if(selrow == null){
			$.messager.alert('提示','请先选择患者!','info');
			return;
		}
		var href = " <c:out value='${pageContext.request.contextPath }'/>/set/address/add.htmlx";
		if (isEdit) {
			href = " <c:out value='${pageContext.request.contextPath }'/>/set/address/edit.htmlx";
		}
		top.$.modalDialog({
			title : "配送点",
			width : 400,
			height : 400,
			href : href,
			onLoad:function(){
				if(isEdit && selrow) {
					var f = parent.$.modalDialog.handler.find("#form1");
					f.form("load", $('#dg2').datagrid('getSelected'));
				}
			},
			buttons : [ {
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					top.$.modalDialog.openner= $('#dg2');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
					var f = top.$.modalDialog.handler.find("#form1");
					var pid = top.$.modalDialog.handler.find("#pid");
					pid.val(selrow.id);
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

	//患者删除
	function delAjax(id){
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/set/patient/delete.htmlx",
			data:"id="+id,
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){
				if(data.success){				
					$('#dg').datagrid('reload');
					showMsg("删除成功");
				} 
			},
			error:function(){
				showErr("出错，请刷新重新操作");
			}
		});	
	}
	//配送点删除
	function subdelAjax(id){
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/set/address/delete.htmlx",
			data:"id="+id,
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){
				if(data.success){				
					$('#dg2').datagrid('reload');
					showMsg("删除成功");
				} 
			},
			error:function(){
				showErr("出错，请刷新重新操作");
			}
		});	
	}
	//搜索框
	$("#ss1").searchbox({
		searcher:function(value,name){
			var query={};
			query["query['t#"+name+"_S_LK']"] = value;
			$('#dg').datagrid('load',query); 
		},
		prompt:'搜索在此输入',
		menu:'#mm',
		width:200
	});
	//搜索框
	$("#ss2").searchbox({
		searcher:function(value,name){
			if($('#dg').datagrid('getSelected') == null){
				$.messager.alert('提示','请先选择患者!','info');
				return;
			}
			var query={id:$('#dg').datagrid('getSelected').id};
			query["query['w#"+name+"_S_LK']"] = value;
			$('#dg2').datagrid('load',query); 
		},
		prompt:'搜索在此输入',
		menu:'#mm',
		width:200
	});
	//患者
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height:$(this).height()-60,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/patient/page.htmlx",
		pageSize:10,
		pageNumber:1,
		pagination:true,
		idField:'id',
		columns:[[
					{field:'id',title:'ID',width:15,align:'center'},
					{field:'idCode',title:'身份证',width:15,align:'center'},
					{field:'name',title:'姓名',width:15,align:'center'},
		        	{field:'isDisabled',title:'是否禁用',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
							}
						}
	    			}
		   		]],

		onDblClickRow: function(index,field,value){
			addOpen(true);
		},
		onClickRow: function(index,row){
			$("#ss2").searchbox("setValue","");
			$('#dg2').datagrid({
				url:" <c:out value='${pageContext.request.contextPath }'/>/set/address/page.htmlx",
				queryParams:{
					id:$('#dg').datagrid('getSelected').id
				}
			})
		},
   		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen(false);	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				var selobj = $('#dg').datagrid('getSelected');
				if(selobj == null){
					$.messager.alert('错误','没有选中行!','info');
					return;
				}
				var id= selobj.id;
				$.messager.confirm('确认信息', '确认要删除此属性?', function(r){
					if (r){
						delAjax(id);
					}
				});
			}
		}],
		
	});
	
	//收货点
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		editable:true,
		height :  $(this).height()-90,
		pagination:true,
		pageSize:10,
		pageNumber:1,
		idField:'id',
		columns:[[
					{field:'name',title:'名称',width:50,align:'center'},
					{field:'isDisabled',title:'是否禁用',width:20,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
							}
						}
	    			}
		   		]],
   		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				subaddOpen(false);  
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				var selobj = $('#dg2').datagrid('getSelected');
				if(selobj == null){
					$.messager.alert('错误','没有选中行!','info');
					return;
				}
				var id= selobj.id;
				$.messager.confirm('确认信息', '确认要删除配送点吗?', function(r){
					if (r){
						subdelAjax(id);
					}
				});
			}
		}],
		onDblClickRow: function(index,field,value){
			subaddOpen(true);
		}
	});
})

</script>