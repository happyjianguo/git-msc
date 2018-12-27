<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			<tr>
				<th>付款时间：</th>
				<th><input id="payDate" name="payDate" /></th>
			</tr>
			<tr>
				<th>付款金额：</th>
				<th>
				<input id="sum" name="sum" class="easyui-validatebox  easyui-numberbox" data-options="tipPosition:'left',min:0,precision:2,required:true" />
				&nbsp;&nbsp;(待付款：<span  id="maxAmt"  /><input type="hidden"  id="maxAmt_h"  />)
				</th>
			</tr>
			<tr>
				<th>付款方：</th>
				<th><input id="payer" name="payer" class="easyui-validatebox easyui-textbox" /></th>
			</tr>
			<tr>
				<th>收款方：</th>
				<th><input id="reciever" name="reciever" class="easyui-validatebox easyui-textbox"/></th>
			</tr>
			<tr>
				<th>银行名称：</th>
				<th><input id="bankName" name="bankName" class="easyui-validatebox easyui-textbox"/></th>
			</tr>
			<tr>
				<th>支行：</th>
				<th><input id="bankBranch" name="bankBranch" class="easyui-validatebox easyui-textbox"/></th>
			</tr>
			<tr>
				<th>账号：</th>
				<th><input id="accNo" name="accNo" class="easyui-validatebox easyui-textbox"/></th>
			</tr>
			<tr>
				<th>经办人：</th>
				<th><input id="responsor" name="responsor" class="easyui-validatebox easyui-textbox"/></th>
			</tr>
			<tr>
				<th>付款方式：</th>
				<th><input id="payMethod" name="payMethod" class="easyui-validatebox easyui-textbox"/></th>
			</tr>
		</table>
		<input type="hidden" id="settleId" name="settleId" />
		
	</form>
</div>

<script>
//初始化
$(function(){
	//日期
	$('#payDate').datebox({
		editable:false,
		required:true
	});

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/vendor/pay/mkpayment.htmlx",
		onSubmit : function() {
			//$("#pwd").textbox('setValue',$.md5($("#pwd").textbox('getValue')));
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = $(this).form('validate');
			var sum = $("#sum").numberbox("getValue");
			var maxAmt_h = $("#maxAmt_h").val();
			if((sum-maxAmt_h).toFixed(2)>0){
				isValid = false;
				showErr("付款金额超上限");
			}
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
				successOpen(result.data);
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});
//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "付款成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/pay/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

</script>