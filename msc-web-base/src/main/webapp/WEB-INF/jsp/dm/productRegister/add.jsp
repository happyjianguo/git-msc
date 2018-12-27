<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1" method="post">
		<table class="table-bordered">
			<tr>
				<th>申请机构:</th>
				<th><span style="font-weight:500;"><c:out value='${user.organization.orgName }'/></span></th>
				<th>申请人:</th>
				<th><span style="font-weight:500;"><c:out value='${user.name }'/></span></th>
			</tr>
			<tr align="left">
				<th colspan="4">申请单明细:</th>
			</tr>
		</table>
		<table id="form1_addcplist_datagrid">
		</table>
		<input type="hidden" name="rows" id="rows" >
	</form>
	<div id="toolbar" class="search-bar" >
		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addOpen()">增行</a>
		<span class="datagrid-btn-separator split-line" ></span>
		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delFunc()">删行</a>
	</div>
</div>

<script>
var editIndex = undefined;
function endEditing(){
	if (editIndex == undefined){return true}
	if ($('#form1_addcplist_datagrid').datagrid('validateRow', editIndex)){
		var ed = $('#form1_addcplist_datagrid').datagrid('getEditor', {index:editIndex,field:'spec'});
		$('#form1_addcplist_datagrid').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}

function onClickRow(index){
	if (editIndex != index){
		if (endEditing()){
			$('#form1_addcplist_datagrid').datagrid('selectRow', index)
					.datagrid('beginEdit', index);
			editIndex = index;
		} else {
			$('#form1_addcplist_datagrid').datagrid('selectRow', editIndex);
		}
	}
	var ed = $('#form1_addcplist_datagrid').datagrid('getEditor', {index:index,field:'name'});
	var ed_goodsNum = $('#form1_addcplist_datagrid').datagrid('getEditor', {index:index,field:'goodsNum'});
	
}

function append(){
	if (endEditing()){
		$('#form1_addcplist_datagrid').datagrid('appendRow',{productName:'',genericName:'',dosageFormName:'',model:'',packDesc:'',producerName:''});
		editIndex = $('#form1_addcplist_datagrid').datagrid('getRows').length-1;
		$('#form1_addcplist_datagrid').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
		var ed = $('#form1_addcplist_datagrid').datagrid('getEditor', {index:editIndex,field:'name'});
		var ed_goodsNum = $('#form1_addcplist_datagrid').datagrid('getEditor', {index:editIndex,field:'goodsNum'});
	}
}

function addOpen(){
    append();
    return;
    auth();
}

function delFunc(){
    var row = $('#form1_addcplist_datagrid').datagrid('getSelected');
    if(row == null){
        $.messager.alert('提示','没有选中行!','info');
        return;
    }
    var rowIndex = $('#form1_addcplist_datagrid').datagrid('getRowIndex', row);
    $('#form1_addcplist_datagrid').datagrid('deleteRow', rowIndex);
    return;
    auth();
}
//初始化
$(function(){
	$('#form1_addcplist_datagrid').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-350,
		pagination:false,
		editable:true,
		pageSize:10,
		pageNumber:1,
		onClickRow: onClickRow,
		//onClickCell:onClickCell,
		columns:[[
        	{field:'productName',title:'药品名称',width:20,align:'center',editor:{type:'textbox',options:{required: true}}},
        	{field:'dosageFormName',title:'剂型名称',width:20,align:'center',editor:{type:'textbox',options:{required: true}}},
        	{field:'model',title:'规格',width:20,align:'center',editor:{type:'textbox',options:{required: true}}},
        	{field:'packDesc',title:'包装规格',width:20,align:'center',editor:{type:'textbox',options:{required: true}}},
        	{field:'producerName',title:'生产企业',width:20,align:'center',editor:{type:'textbox',options:{required: true}}},
        	{field:'authorizeNo',title:'国药准字',width:20,align:'center',editor:{type:'textbox',options:{required: true}}}
		]],
		toolbar: "#toolbar"
	});
	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/productRegister/add.htmlx",
		onSubmit : function() {
			var s = $('#form1_addcplist_datagrid').datagrid('getRows');
			var row = $('#form1_addcplist_datagrid').datagrid('getSelected');
			var rowIndex = $('#form1_addcplist_datagrid').datagrid('getRowIndex', row);
			$('#form1_addcplist_datagrid').datagrid('endEdit', rowIndex);
			if(s == ""){
				showErr("请填写申请单明细资料");
				return false;
			}
			$('#rows').val(JSON.stringify(s));
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = $(this).form('validate');
			if (!isValid) {
				top.$.messager.progress('close');
			}
			return isValid;
		},
		success : function(result) {
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				top.$.modalDialog.openner.datagrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("提交成功！");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	
});

</script>

