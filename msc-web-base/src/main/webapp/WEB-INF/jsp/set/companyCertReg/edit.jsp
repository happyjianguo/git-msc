<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post" enctype="multipart/form-data">
		<table class="table-bordered">
		 	<tr>
				<th width="15%">证照图片：</th>
		   		<th width="85%" colspan=3>
					<div id="localImag">
						<%--预览，默认图片--%>
						<img id="preview" alt="" src="<c:out value='${pageContext.request.contextPath }'/>/set/companyCertReg/readfile.htmlx?fileid=${companyCertReg.imagePath}" style="width: 300px; height: 200px;" />
					</div>
		   		</th>
		   </tr>
		 	<tr>
				<th width="15%">公司名称：</th>
		   		<th width="35%">
		   			${companyCertReg.company.fullName}
		   		</th>
		   		<th width="15%">证件类型：</th>
		   		<th width="35%">
		   			${companyCertReg.typeName}
		   		</th>
		   </tr>
		   <tr>
				<th>证照代码：</th>
		   		<th>
		   			${companyCertReg.code}
		   		</th>
		   		<th>证照名称：</th>
		   		<th>
		   			${companyCertReg.name}
		   		</th>
		   </tr>
		   <tr>
				<th>发证日期：</th>
		   		<th>
		   			${companyCertReg.issueDate}   		
		   		</th>
		   		<th>有效期截止：</th>
		   		<th>
		   			${companyCertReg.validDate}   		
		   		</th>
		   </tr>
		   <tr>
				<th>发证部门：</th>
		   		<th>
		   			${companyCertReg.dept}   		
		   		</th>
		   		<th>证照范围：</th>
		   		<th>
		   			${companyCertReg.scope}   			
		   		</th>
		   </tr>
		   <tr>
				<th>备注：</th>
		   		<th colspan=3>
		   			${companyCertReg.note}
		   		</th>
		   </tr>
		</table>
		<table class="table-bordered">
		    <tr>
				<th width="15%">审核状态：</th>
		   		<th width="35%">
		   			<input type="text" id="auditStatus" name="auditStatus">
		   		</th>
		   		<th width="15%">审核时间：</th>
		   		<th width="35%" id="auditTime">
		   		</th>
		   </tr>
		    <tr>
				<th>审核意见：</th>
		   		<th colspan=3>	   			
		   			<input type="text" name="auditNote" value="${companyCertReg.auditNote}" class="easyui-validatebox  easyui-textbox" style="width:590px" />
		   		</th>
		   </tr>
		</table>
		<input type="hidden" name="id" value="${companyCertReg.id}"  >
		<input type="hidden" name="auditStatus1" value="${companyCertReg.auditStatus}"  >
	</form>


</div>

<script>
var auditTime = document.getElementById("auditTime");
auditTime.innerHTML = $.format.date("${companyCertReg.auditTime}","yyyy-MM-dd HH:mm:ss");

//初始化
$(function(){
	$('#auditStatus').combobox({
		valueField:'label',    
	   	textField:'value',
	   	value:"${companyCertReg.auditStatus=='send'?'':companyCertReg.auditStatus}",
	   	width:80,
	   	panelHeight:'auto',
	   	data: [{
			label: 'pass',
			value: '通过'
		},{
			label: 'back',
			value: '退回'
		}],
		editable:false,
		required:true,
		onSelect:function(record){
			
		}
	});
    $("input.easyui-textbox").textbox({validType: 'maxLength[67]'});//暂时控制
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/companyCertReg/edit.htmlx",
		onSubmit : function() {
			var auditStatus = form1.auditStatus1.value;
			if(auditStatus != "send"){
				$.messager.alert("错误","资料已审核！","info");
				return false;
			}
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
		success:function(result) {
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				top.$.modalDialog.openner.datagrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("审核成功！");
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>