<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
            <div  data-options="region:'west',title:'疾病',collapsible:false" style="width:350px;background: rgb(238, 238, 238);padding:3px">
				<table  id="dg" ></table>
            </div>
            
            <div data-options="region:'center',title:'疾病药品'" style="background: rgb(238, 238, 238);padding:3px">
				<div class="search-bar">
				    <input id="ss" />
					<div id="mm">
						<div data-options="name:'name' ">药品名称</div>
						<div data-options="name:'code'">药品编码</div>
					</div>
				</div>
            	<div style="padding:3px;">
					<table id="dg2" ></table>
				</div>
            </div>

			

</body>

</html>
<script>
$(function(){
	$('#ss').searchbox({
		searcher:function(value,name){ 
			searchProduct(value,name);
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	//搜索
	function searchProduct(val,name){
		var selrow = $('#dg').datagrid('getSelected');
		if(name=="name"){
			$('#dg2').datagrid('load',{
				"query['p#name_S_LK']": val,
				sicknessCode:selrow.code,
			});	
		}else if(name=="code"){
			$('#dg2').datagrid('load',{
				"query['p#code_S_LK']": val,
				sicknessCode:selrow.code,
			});	
		}else{
			$('#dg2').datagrid('load',{
				sicknessCode:selrow.code,
			});
		}
	}
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height :  $(this).height()-40,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/sickness/page.htmlx",
		pageSize:20,
		pageNumber:1,
		columns:[[
		        	{field:'code',title:'编码',width:50,align:'center'},
		        	{field:'name',title:'名称',width:50,align:'center'}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen();			
			}
		}],
		onClickRow: function(index,row){
			search();  
		},
		onDblClickRow: function(index,row){
			editOpen();		
		}
		

	});
	$("#dg").datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false
	});
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		rownumbers:true,
		height :  $(this).height()-70,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
			{field:'code',title:'药品编码',width:15,align:'center'},
        	{field:'name',title:'药品名称',width:10,align:'center'},
        	{field:'genericName',title:'通用名',width:10,align:'center'},
        	{field:'dosageFormName',title:'剂型',width:10,align:'center'},
        	{field:'producerName',title:'生产企业',width:10,align:'center'},
        	{field:'model',title:'规格',width:10,align:'center'},
        	{field:'packDesc',title:'包装规格',width:10,align:'center'},
        	{field:'isDisabled',title:'是否禁用',width:10,align:'center',
        		formatter: function(value,row,index){
					if (row.isDisabled == 1){
						return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
					}
				}
        	}
   		]],
   		toolbar: [{
			iconCls: 'icon-add',
			text:"添加药品",
			handler: function(){
				openProduct();			
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delProduct();
			}
		}],
		onLoadSuccess: function(data){
		},
		onClickRow: function(index,row){
		}
	});

	//$('#dg2').datagrid('enableFilter', []);
	//$('#dg2').datagrid('removeFilterRule');
	
})

//搜索
function search(){
	var selrow = $('#dg').datagrid('getSelected');

	$('#dg2').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/dm/sickness/product/page.htmlx",  
	    queryParams:{
	    	sicknessCode:selrow.code,
	    }
	}); 
}

//保存
function save(){
	var nodes = $('#dg2').datagrid('getSelections');
	var user_arr = new Array();
	$.each(nodes,function(index){
		user_arr[index] = this.ID;
	});
	var groupNode = $('#dg').datagrid('getSelected');
	saveAjax(groupNode.id,user_arr);
}


//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 400,
		height : 150,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/sickness/add.htmlx",
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

function openProduct(){
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow == null){
		showErr("请先选择疾病");
		return;
	}
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 420,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/sicknessProduct/add.htmlx",
	    queryParams:{
	    	sicknessCode:selrow.code,
	    },
		onLoad:function(){
			
		},
		onDestroy:function(){
			$('#dg2').datagrid("reload");
		}
	});
}

function delProduct(){
	var selobjs = $('#dg2').datagrid('getSelections');
	if(selobjs.length == 0){
		showErr("请至少选择一行");
		return;
	}
	var datas = new Array();
	$.each(selobjs,function(index,row){
		 var data = new Object();
	     data.code = row.code;
	     datas[datas.length] = data;
	});
	if(datas.length > 0){
		$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
			if (r){
				delProductAjax(datas);
			}
		});
	}
}

//弹窗修改
function editOpen() {
	top.$.modalDialog({
		title : "编辑",
		width : 400,
		height : 150,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/sickness/edit.htmlx",
		onLoad:function(){
			var selrow = $('#dg').datagrid('getSelected');
			if(selrow){
				var f = parent.$.modalDialog.handler.find("#form1");
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

//=============ajax===============
function delProductAjax(datas){
	var data = JSON.stringify(datas);
	var selrow = $('#dg').datagrid('getSelected');
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/sicknessProduct/delProduct.htmlx",
		data:{
			data : data,
			sicknessCode:selrow.code,
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg(data.msg);
			}else{
				showErr(data.msg);
			}
			$('#dg2').datagrid("reload");
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function addAjax(userId){
	var groupNode = $('#dg').datagrid('getSelected');
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/dm/sickness/add.htmlx",
			data:{
				userId:userId,
				groupId:groupNode.id
			},
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){
				if(data.success){
					showMsg("新增成功！");
				}else{
					showErr(data.msg);
				}
			},
			error:function(){
				//top.$.modalDialog.openner.datagrid('reload');
				//top.$.modalDialog.handler.dialog('close');
				showErr("出错，请刷新重新操作");
			}
		});	
	}

</script>