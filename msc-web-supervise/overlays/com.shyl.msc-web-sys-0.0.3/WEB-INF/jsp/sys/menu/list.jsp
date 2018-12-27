<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<div style="padding:10px 10px 0px 10px">
	角色类型：
	<input name="orgType" id="orgType">
</div>

<div style="padding:10px">
	<table  id="tg" ></table>
</div>

</body>
</html>

<script>
//初始化
$(function(){
	$('#orgType').combobox({
	    valueField:'label',    
	    textField:'value',
	    groupField:'group',
	    groupFormatter: function(group){
			return '<span style="color:#0081c2">' + group + '</span>';
		},
		panelHeight:"auto",
	    data: [{
			label: 'b2b-1',
			value: '医院',
			group:'web-b2b系统'
		},{
			label: 'b2b-2',
			value: '供应商',
			group:'web-b2b系统'
		},{
			label: 'b2b-3',
			value: '监管机构',
			group:'web-b2b系统'
		},{
			label: 'b2b-6',
			value: 'GPO',
			group:'web-b2b系统'
		},{
			label: 'admin-9',
			value: '系统管理员',
			group:'web-admin系统'
		},{
			label: 'admin-5',
			value: '系统运维',
			group:'web-admin系统'
		},{
			label: 'pe-5',
			value: '系统运维',
			group:'web-pe系统'
		},{
			label: 'sup-1',
			value: '医院',
			group:'web-sup系统'
		},{
			label: 'sup-3',
			value: '监管机构',
			group:'web-sup系统'
		}],
		value:"admin-9",
		editable:false,
		onSelect:function(record){
			var val = record.label;
			$('#tg').treegrid('load',{
				"sysId":val.split("-")[0],
				"orgType":val.split("-")[1]
			}); 
		}
	});
	
	//datagrid
	$('#tg').treegrid({
		idField:'id',
	    treeField:'name',
	    parentField:'parentId',
	    iconCls:'icon',
	    method:'post',
	    animate:true,
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :  $(this).height()-60,
		url:"${pageContext.request.contextPath}/sys/menu/list.htmlx",
		queryParams:{
			"sysId":"admin",
			"orgType":"9"
		},
		columns:[[
        	{field:'name',title:'资源名称',width:8},
        	{field:'url',title:'资源路径',width:8},
			{field:'operation',title:'操作',width:5,align:'center',
	    		formatter: function(value,row,index){
	    			return "<a href='#' onclick='lefteditOpen("+row.resourceId+");'>查看资源</a>";
				}
			}
   		]],
   		onLoadSuccess: function(row){
			$(this).treegrid('enableDnd', row?row.id:null);
		},
		onDrop:function(targetRow,sourceRow,point){
			dragAjax(targetRow.id,sourceRow.id,point);
		},
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
		onDblClickRow:function(row){
			editOpen();	
			return;
		}
	});

	
	$('#ss').searchbox({
		searcher:function(value,name){ 
			search(value);
		},
		prompt:'搜索在此输入栏位',
		width:150
	}); 
});
//弹窗修改left

function lefteditOpen(id) {
	top.$.modalDialog({
		title : "编辑资源",
		width : 600,
		height : 400,
		href : "${pageContext.request.contextPath}/sys/resource/edit.htmlx",
		queryParams:{
			id:id
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#tg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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
//清空
function clearWin(id){
	$("#"+id).find("input[type=text]").each(function(){
			$(this).val("");
		 });
}

//删除
function delFunc(){
	var selobj = $('#tg').treegrid('getSelected');
	if(selobj == null){
		return;
	}
	var id= $('#tg').datagrid('getSelected').id;
	
	$.messager.confirm('确认信息', '确认要删除此目录?', function(r){
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

//弹窗增加
function addOpen() {
	var selrow = $('#tg').treegrid('getSelected');
	var parentId = -1;
	if(selrow){
		parentId = selrow.id;
	}
	var val = $('#orgType').combobox("getValue");
	top.$.modalDialog({
		title : "添加菜单",
		width : 800,
		height : 400,
		href : "${pageContext.request.contextPath}/sys/menu/add.htmlx",
		queryParams:{  
			 "sysId":val.split("-")[0],
			 "orgType":val.split("-")[1],
			 "parentId":parentId
		},
		onDestroy:function(){
			$('#tg').treegrid("reload");
		}
	});
}

//弹窗修改
function editOpen() {
	var selrow = $('#tg').treegrid('getSelected');
	top.$.modalDialog({
		title : "修改菜单",
		width : 600,
		height : 400,
		href : "${pageContext.request.contextPath}/sys/menu/edit.htmlx",
		queryParams:{  
			 "id":selrow.id
		},
		onLoad:function(){
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#tg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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

//=============ajax===============
function delAjax(id){
	$.ajax({
				url:"${pageContext.request.contextPath }/sys/menu/delete.htmlx",
				data:"id="+id,
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#tg').treegrid('reload');  
						showMsg("删除成功！");
					} 
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}

function dragAjax(targetId,sourceId,point){
	$.ajax({
				url:"${pageContext.request.contextPath }/sys/menu/drag.htmlx",
				data:{
					targetId:targetId,
					sourceId:sourceId,
					point:point
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						//dgreload();
						showMsg("调整完毕！");
					} 
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}

</script>