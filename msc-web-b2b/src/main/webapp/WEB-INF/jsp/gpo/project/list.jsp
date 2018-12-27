<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
	<div id="tt">
		<div title="招标项目" class="my-tabs">
			<table id="dg" class="single-dg"></table>
		</div>
		<div title="招标明细" class="my-tabs">
			<form id="fileId" method="POST" enctype="multipart/form-data">
				<input type="file" name="myfile" class="file" id="myfile" style="display: none" /> 
				<input type='hidden' id="projectId" name='projectId' />
			</form>
			<table id="dg1" class="single-dg"></table>
		</div>
	</div>
</body>
</html>

<script>
var detailRow = '';
$("#myfile").change(function(){
	if(detailRow == '') return;
	$.messager.confirm('确认信息', '确认导入药品招标项目么?', function(r){
		if (r){
			$("#projectId").val(detailRow.id);
			$("#fileId").submit();
		}else{
			location.reload(true);
		}
	});
});

$("#fileId").form({
	url :" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/importExcel.htmlx",
	onSubmit : function() {
		return true;
	},
	success : function(result) {
		$('#myfile').val("");
		result = $.parseJSON(result);
		if(result.success){
			$('#dg1').datagrid('reload');
			showMsg(result.msg);
		}else{
			showMsg(result.msg);
		}
	},
	error:function(){
		showErr("出错，请刷新重新操作");
	}
});
//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	
	
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - 33,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'项目编号',width:10,align:'center'},
		        	{field:'name',title:'项目名称',width:10,align:'center'},
		        	{field:'type',title:'类型',width:10,align:'center',
						formatter: function(value,row,index){
							if (value == "all"){
								return "不区分";
							} else if (value == "base"){
								return "基药类型";
							} else {
								return "非基药类型";
							}
						}},
		        	{field:'startDate',title:'报名开始日期',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.startDate){
								return $.format.date(row.startDate,"yyyy-MM-dd");
							}
						}
		        	},
		        	{field:'endDate',title:'报名截至日期',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.endDate){
								return $.format.date(row.endDate,"yyyy-MM-dd");
							}
						}
		        	},
		        	{field:'startMonthDef',title:'默认计划开始时间',width:10,align:'center'},
		        	{field:'endMonthDef',title:'默认计划结束时间',width:10,align:'center'},
		        	{field:'projectStusName',title:'状态',width:10,align:'center'},
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
			iconCls: 'icon-ok',
			text:"开始报量",
			handler: function(){
				reportFunc();	
			}
		},'-',{
			iconCls: 'icon-disable',
			text:"结束报量",
			handler: function(){
				editFunc();	
			}
		},'-',{
			iconCls: 'icon-ok',
			text:"开始投标",
			handler: function(){
				tenderFunc();	
			}
		},'-',{
			iconCls: 'icon-disable',
			text:"结束投标",
			handler: function(){
				evalFunc();	
			}
		}/* ,'-',{
			iconCls: 'icon-ok',
			text:"招标完成",
			handler: function(){
				doneFunc();	
			}
		} */],
		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
		}
	});
	$('#dg').datagrid('enableFilter', 
		[{
	        field:'type',
	        type:'combobox',
	        options:{
	            panelHeight:'auto',
	            editable:false,
	            data:[{value:'',text:'-请选择-'},
	                  {value:'all',text:'不区分'},
	                  {value:'base',text:'基本药物'},
	                  {value:'notBase',text:'非基本药物'}
	                  ],
	            onChange:function(value){
	                if (value == ''){
	                	$('#dg').datagrid('removeFilterRule', 'type');
	                } else {
	                	$('#dg').datagrid('addFilterRule', {
	                        field: 'type',
	                        op: 'EQ',
	                        fieldType:'S',
	                        value: value
	                    });
	                }
	                $('#dg').datagrid('doFilter');
	            }
	        }
	    },{
	        field:'startDate',
	        type:'datebox',
	        op:['EQ','GE'],
	        fieldType:'D',
	        options:{
	        	editable:false
	        }
	    },{
	        field:'endDate',
	        type:'datebox',
	        op:['EQ','GE'],
	        fieldType:'D',
	        options:{
	        	editable:false
	        }
	    },{
	        field:'options',
	        type:'text',
	    	isDisabled:1
	    }]);
	
	//datagrid
	$('#dg1').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - 33,
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'projectCode',title:'项目编码',width:10,align:'center',formatter: function(value,row,index){
        		return row.project.code;
        	}},
        	{field:'projectName',title:'项目名称',width:10,align:'center',formatter: function(value,row,index){
        		return row.project.name;
        	}},
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
        	{field:'num',title:'报量数',width:10,align:'center',formatter: function(value,row,index){
        		return row.num;
        	}},
        	{field:'hospitalNum',title:'报量医院',width:10,align:'center',formatter: function(value,row,index){
        		return "<a class='dgbtn2' href='#' onclick='openHospital("+index+")' class='easyui-linkbutton'>"+row.hospitalNum+"</a>";
        	}},
        	{field:'vendorNum',title:'投标供应商',width:10,align:'center',formatter: function(value,row,index){
        		return "<a class='dgbtn2' href='#' onclick='openVendor("+index+")' class='easyui-linkbutton'>"+row.vendorNum+"</a>";
        	}}
   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				add1Open();	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				del1Func();	
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
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/export.htmlx");
			}
		}],
		onDblClickRow:function(index,row) {
			//choose1Open(row);
		}
	});
	$('#dg1').datagrid('enableFilter',[{
		 field:'project.code',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'project.name',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'directory.genericName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'directory.dosageFormName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'directory.model',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'directory.qualityLevel',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'directory.minUnit',
		 type:'text',
		 fieldType:'t#S'
	}]);
	
});


function searchDefectsList(row){
	detailRow = row;
	$('#dg1').datagrid('load', {
			"projectId":row.id
	});
}
//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	if(selobj.projectStus != 'create'){
		showErr("只有【项目建立】状态才可以删除");
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj.id);
		}
	});
}
//开始报量
function reportFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	if(selobj.projectStus != 'create'){
		showErr("只有【项目建立】状态才可以开始报量");
		return;
	}
	$.messager.confirm('确认信息', '您确认要开始报量吗?', function(r){
		if (r){
			reportAjax(selobj.id);
		}
	});
}
//结束报量
function editFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	if(selobj.projectStus != 'report'){
		showErr("只有【报量中】状态才可以结束报量");
		return;
	}
	$.messager.confirm('确认信息', '您确认要结束报量吗?', function(r){
		if (r){
			editAjax(selobj.id);
		}
	});
}
//开始投标
function tenderFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	if(selobj.projectStus != 'edit'){
		showErr("只有【报量结束】状态才可以开始投标");
		return;
	}
	$.messager.confirm('确认信息', '您确认要开始投标吗?', function(r){
		if (r){
			tenderAjax(selobj.id);
		}
	});
}
//结束投标
function evalFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	if(selobj.projectStus != 'tender'){
		showErr("只有【投标中】状态才可以结束投标");
		return;
	}
	$.messager.confirm('确认信息', '您确认要结束投标吗?', function(r){
		if (r){
			evalAjax(selobj.id);
		}
	});
}
//招标完成
function doneFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	if(selobj.projectStus != 'eval'){
		showErr("只有【评标中】状态才可以招标完成");
		return;
	}
	$.messager.confirm('确认信息', '您确认要招标完成吗?', function(r){
		if (r){
			doneAjax(selobj.id);
		}
	});
}
//折页1删除
function del1Func(){
	if(detailRow == '') return;
	var selobj = $('#dg1').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			del1Ajax(selobj);
		}
	});
}
//折页1 查看报量医院
function openHospital(index){
	var row = $("#dg1").datagrid("getRows")[index];
	
	if(detailRow.id == null) return;
	top.$.modalDialog({
		title : "报量医院",
		width : 1000,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/hospital.htmlx",
		onLoad:function(){
			var f = top.$.modalDialog.handler.find("#projectDetailId").val(row.id);
			top.search();
		},
		onClose:function() {
			top.$.modalDialog.handler.dialog('destroy');
			top.$.modalDialog.handler = undefined;
			//$('#dg1').datagrid('reload');  
		}
	});
}

//折页1 查看投标供应商
function openVendor(index){
	var row = $("#dg1").datagrid("getRows")[index];
	if(detailRow.id == null) return;
	top.$.modalDialog({
		title : "投标供应商",
		width : 1000,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/vendor.htmlx",
		onLoad:function(){
			var f = top.$.modalDialog.handler.find("#projectDetailId").val(row.id);
			top.search();
		},
		onClose:function() {
			top.$.modalDialog.handler.dialog('destroy');
			top.$.modalDialog.handler = undefined;
			//$('#dg1').datagrid('reload');  
		}
	});
}

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/delete.htmlx",
		data:{id:id},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
function reportAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/report.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
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
function editAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/edit.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
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
function tenderAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/tender.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
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
function evalAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/eval.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
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
function doneAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/done.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
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
function del1Ajax(row){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/delete.htmlx",
		data:{
			"projectId":row.project.id,
			"directoryId":row.directory.id
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg1').datagrid('reload');  
				showMsg("删除成功！");
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 500,
		height : 300,
		href : " <c:out value='${pageContext.request.contextPath }'/>/gpo/project/add.htmlx",
		buttons : [{
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

//折页1弹窗增加
function add1Open() {
	if(detailRow.id == null){ 
		showMsg("请到第一折页【双击】选择数据");
		return;
	}
	top.$.modalDialog({
		title : "添加",
		width : 1000,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/directory.htmlx",
		onLoad:function(){
			var f = top.$.modalDialog.handler.find("#projectId").val(detailRow.id);
			top.search();
		},
		onClose:function() {
			top.$.modalDialog.handler.dialog('destroy');
			top.$.modalDialog.handler = undefined;
			$('#dg1').datagrid('reload');  
		}
	});
}

//折页1窗口定标
function choose1Open(row) {
	if(detailRow.id == null) return;
	top.$.modalDialog({
		title : "定标",
		width : 1000,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/choose.htmlx",
		onLoad:function(){
			var f = top.$.modalDialog.handler.find("#projectDetailId").val(row.id);
			top.search();
		},
		onClose:function() {
			top.$.modalDialog.handler.dialog('destroy');
			top.$.modalDialog.handler = undefined;
			//$('#dg1').datagrid('reload');  
		}
	});
}

</script>