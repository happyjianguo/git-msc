<%@page import="com.shyl.sys.entity.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>编号:</th>
		   		<th>
					<input type="text" name="code" class="easyui-validatebox easyui-textbox-20" data-options="required:true" style="width:200px" >
					
				</th>
				<th>所属学科:</th>
		   		<th>
		   			<input  id="courseCode" name="courseCode" />
		   			<input type="hidden" id="courseName" name="courseName" />
		   		</th>
		   </tr>
		      <tr>
		   		<th>姓名:</th>
		   		<th>
					<input type="text" name="name" class="easyui-validatebox easyui-textbox-3" data-options="required:true" style="width:200px">
				</th>
				<th>性别:</th>
		   		<th>
		   			<input  id="sex" name="sex" />
		   		</th>
		   </tr>
		      <tr>
		   		<th>手机:</th>
		   		<th>
					<input type="text" name="mobile" class="easyui-validatebox easyui-textbox" data-options="validType:'mobile'"  style="width:200px">
				</th>
				<th>固定电话:</th>
		   		<th>
					<input type="text" name="tel" class="easyui-validatebox easyui-textbox" data-options="validType:'phone'"  style="width:200px">
				</th>
		   	</tr>
		    <tr >
		   		<th>所属机构:</th>
		   		<th colspan="3">
		   			<input name="orgType" id="orgType">
		   			<input name="orgCode" id="orgCode">
		   			<input type="hidden" name="orgName" id="orgName" >
		   		</th>
		   		
		   	</tr>
		   
		</table>
		<input type="hidden" name="id" >
	</form>


</div>



<script>
function toNextCombox(){
	var type = $('#orgType').combobox('getValue');
	
	$('#orgCode').combogrid("setValue","");
	 $('#orgCode').combogrid('grid').datagrid("reload",{
		 "query['t#isDisabled_I_EQ']":0
	});
	
}
//初始化
$(function(){
	$('#sex').combobox({
		valueField:'label',    
	   	textField:'value',
	   	width:80,
	   	panelHeight:'auto',
	   	data: [{
			label: '1',
			value: '男'
		},{
			label: '0',
			value: '女'
		}],
		editable:false,
		value:1,
		onSelect:function(record){
			
		}
	});
	
	$('#courseCode').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "expert"
		},
		required: true,
		editable:false,
	    onSelect:function(record){
	    	$('#courseName').val(record.field2);
	    }
	});

    $("input.easyui-textbox-3").textbox({validType: 'maxLength[6]'});//暂时控制
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
	
	$('#orgType').combobox({
	    valueField:'label',    
	    textField:'value',
	    width:80,
	    panelHeight:'auto',
	    data: [{
			label: '1',
			value: '医院'
		}],
		editable:false,
		value:1,
		onSelect:function(record){
			toNextCombox();
		}
	});
	
	//下拉
	$('#orgCode').combogrid({
		idField:'code',    
		textField:'fullName', 
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx",
	    queryParams:{
	    	"query['t#isDisabled_I_EQ']":0
		},
		pagination : true,
		pageSize : 10,
		pageNumber : 1,
		width:200,
		panelWidth:300,
		columns : [ [ {
			field : 'code',
			title : '编码',
			width : 100
		}, {
			field : 'fullName',
			title : '医院名称',
			width : 180
		}]],//panelHeight:'auto',
		keyHandler : {
			query : function(q) {
				//动态搜索
				$('#orgCode')
						.combogrid('grid')
						.datagrid(
								"reload",
								{
									"query['t#fullName_S_LK']" : q
								});
				$('#orgCode').combogrid("setValue", q);
			}

		},
		onSelect: function(rowIndex, rowData){
			$("#orgName").val(rowData.fullName);
		}
	}).combobox("initClear"); 
	$('#orgCode').combogrid('grid').datagrid('getPager').pagination({
		showPageList : false,
		showRefresh : false,
		displayMsg : "共{total}记录"
	});
	
	/* $('#orgId').combobox({
		editable:false
	}); */
	
	$("#form1").form({
		url :"<c:out value='${pageContext.request.contextPath }'/>/set/expert/edit.htmlx",
		onSubmit : function() {
			//$("#pwd").textbox('setValue',$.md5($("#pwd").textbox('getValue')));
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
				showMsg("修改成功");
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	
});

</script>