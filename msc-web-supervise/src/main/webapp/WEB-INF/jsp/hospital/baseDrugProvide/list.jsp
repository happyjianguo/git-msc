<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<tag:head title="医院基本药物配备使用比例" />
<style type="text/css">
.file {
	position: absolute;
	top: 0px;
	left: 0px;
	height: 30px;
	width: 85px;
	filter: alpha(opacity : 0);
	opacity: 0;
	cursor: pointer;
}
</style>
<html>

<body class="easyui-layout"  >
	<form id="fileId" method="POST" enctype="multipart/form-data">
		<input type="file" name="myfile" class="file" id="myfile" />
	</form>
	<div class="single-dg"><table id="dg"></table></div>
</body>
</html>

<script>
$(function(){
	$("#myfile").change(function(){
		$.messager.confirm('确认信息', '确认导入资料吗?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/baseDrugProvide/upload.htmlx",
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据导入中，请稍后....'
			});
			return true;
		},
		success : function(result) {
			$('#myfile').val("");
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				$('#dg').datagrid('reload');
				showMsg(result.msg);
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	//datagrid
	$("#dg").datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height : $(this).height() - 4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/baseDrugProvide/page.htmlx",
		pageSize:20,
		pageNumber:1,
		pagination:true,
		remoteFilter: true,
		columns:[[
			{field:'month',title:'年月',width:13,align:'center'}, 
			{field:'baseDrugTotal',title:'基本药物品规数',width:15,align:'center'},
			{field:'drugTotal',title:'全部药物品规数',width:15,align:'center'},
			{field:'drugRatio',title:'<span title=" 基本药物品规数  / 全部药物品规数 " class="tip">基本药物品规数占比</span>',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.drugTotal != 0){
						return (parseFloat(row.baseDrugTotal/row.drugTotal)*100).toFixed(1)+'%';
					}else{
						return 0;
					}
			}},
			{field:'baseDrugTrade',title:'基本药物销售金额',width:15,align:'center'},
			{field:'drugTrade',title:'全部药物销售金额',width:15,align:'center'},
			{field:'drugTradeRatio',title:'<span title=" 基本药物销售金额  / 全部药物销售金额 " class="tip">基本药物销售金额占比</span>',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.drugTrade != 0){
						return (parseFloat(row.baseDrugTrade/row.drugTrade)*100).toFixed(1)+'%';
					}else{
						return 0;
					}
			}},
			{field:'isReformHospital',title:'是否县级公立医院改革试点医院',width:17,align:'center',
				formatter: function(value,row,index){
					if (row.isReformHospital==1){
						return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
					}
			}}
   		]],
   		toolbar: [{
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
			iconCls: 'icon-import',
			text:"导入",
			handler: function(){
				$("#myfile").click();
			}
		},'-',{
			iconCls: 'icon-xls',
			text:"模板下载",
			handler: function(){
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/resources/template/baseDrugProvideTemplate.xls");			
			}
		}],
		onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
			$(".tip").tooltip({  
                onShow: function(){  
                    $(this).tooltip('tip').css({   
                        width:'220',          
                        boxShadow: '2px 2px 4px #292929'                          
                    });  
                }  
            });
		},
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});
});
//弹窗新增
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/baseDrugProvide/add.htmlx",
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

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/baseDrugProvide/delete.htmlx",
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

//弹窗修改
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "修改",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/baseDrugProvide/edit.htmlx",
		queryParams:{
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
</script>