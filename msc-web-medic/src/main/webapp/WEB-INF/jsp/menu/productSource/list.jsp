<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="" />

<body class="easyui-layout" >
	<form id="fileId" method="POST" enctype="multipart/form-data" style="display:none;">
		<input type="file" name="myfile" class="file" id="myfile" />
		
	</form>
	<div data-options="region:'center',title:''">
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
		rownumbers:false,
		border:true,
		height : $(this).height()-30,
		pagination:true,
		url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productSource/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'药品编码',width:50,align:'center'},
		        	{field:'productGCode',title:'标准编码',width:100,align:'center'},
		        	{field:'name',title:'药品名称',width:150,align:'center'},
		        	{field:'model',title:'规格',width:100,align:'center'},
		        	{field:'packDesc',title:'包装规格',width:70,align:'center'},
		        	{field:'drug.dosageFormName',title:'剂型',width:50,align:'center',
		        		formatter: function(value,row,index){
		        			return row.drug.dosageFormName;
		        		}},
		        	{field:'company.fullName',title:'厂家',width:200,align:'center',
		        		formatter: function(value,row,index){
		        			return row.company.fullName;
		        		}
		        	},
		        	{field:'authorizeNo',title:'批准文号',width:60,align:'center'},
		        	{field:'nationalAuthorizeNo',title:'国药准字',width:60,align:'center'},
		        	{field:'regeditNo',title:'注册证号',width:60,align:'center'}
		        	
		        	
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"导入药品数据",
			handler: function(){
				$("#myfile").click();
			}
		},{
			iconCls: 'icon-add',
			text:"导出药品数据模板",
			handler: function(){
				window.open("<c:out value='${pageContext.request.contextPath }'/>/resources/template/productSource.xls");
			}
		},{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen(false);
			}
			
		},{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delAjax(false);
			}
			
		}],
		onLoadSuccess:function(data) {
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
		onDblClickRow : function(row) {
			addOpen(true);
		}
	});
	$('#dg').datagrid('enableFilter');
	$("#fileId").form({
		url :"<c:out value='${pageContext.request.contextPath }'/>/menu/productSource/importExcel.htmlx",
		onSubmit : function() {return true;},
		success : function(result) {
			$('#myfile').val("");
			result = $.parseJSON(result);
			if(result.success){
				$('#dg').datagrid('reload');
				showMsg(result.msg);
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请重新操作");
			$('#dg').datagrid('reload');
		}
	});
	
	$("#myfile").change(function(){
		$.messager.confirm('确认信息', '确认导入本地药品目录么?', function(r){
			if (r){
				$("#fileId").submit();
			} else {
				location.reload(true);
			}
		});
	});
});

//弹窗增加
function addOpen(isedit) {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : "<c:out value='${pageContext.request.contextPath }'/>/menu/productSource/"+(isedit?"edit.htmlx":"add.htmlx"),
		onLoad:function() {
			var f = top.$.modalDialog.handler.find("#form1");
			if(isedit && selrow) {
				if(!isempty(selrow.authorizeBeginDate)){
					selrow.authorizeBeginDate = $.format.date(selrow.authorizeBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.authorizeOutDate)){
					selrow.authorizeOutDate = $.format.date(selrow.authorizeOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.importBeginDate)){
					selrow.importBeginDate = $.format.date(selrow.importBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.importOutDate)){
					selrow.importOutDate = $.format.date(selrow.importOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.patentBeginDate)){
					selrow.patentBeginDate = $.format.date(selrow.patentBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.patentOutDate)){
					selrow.patentOutDate = $.format.date(selrow.patentOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.protectBeginDate)){
					selrow.protectBeginDate = $.format.date(selrow.protectBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.protectOutDate)){
					selrow.protectOutDate = $.format.date(selrow.protectOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.newDrugBeginDate)){
					selrow.newDrugBeginDate = $.format.date(selrow.newDrugBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.newDrugOutDate)){
					selrow.newDrugOutDate = $.format.date(selrow.newDrugOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.consignBeginDate)){
					selrow.consignBeginDate = $.format.date(selrow.consignBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.consignOutDate)){
					selrow.consignOutDate = $.format.date(selrow.consignOutDate,"yyyy-MM-dd");
				}
				if(selrow.baseDrugType==null){
					selrow.baseDrugType = "";
    			}
    			if(selrow.insuranceDrugType==null){
    				selrow.insuranceDrugType = "";
    			}
    			selrow.genericName = selrow.drug.genericName;
    			selrow.producerId = selrow.company.id;
    			selrow.producerName = selrow.company.fullName;
    			f.find("#drugId").val(selrow.drug.id);
    			f.find("#producerId").val(selrow.company.id);
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
function delAjax(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			$.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productSource/delete.htmlx",
				data:"id="+selobj.id,
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data) {
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
	});
}
</script>