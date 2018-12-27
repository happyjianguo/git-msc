<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js//My97DatePicker/WdatePicker.js"></script>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>年月:</th>
		   		<th>
					<input type="text" id="month" name="month" class="Wdate" onFocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM'})" />
				</th>	
				<th>是否县级公立医院改革试点医院:</th>
				<th>
					<input id="isReformHospital" name="isReformHospital"  data-options="required:true" />
				</th>
			</tr>	
			<tr>
		   		<th>基本药物品规数:</th>
		   		<th>
					<input type="text" name="baseDrugTotal" class="easyui-validatebox  easyui-textbox" />				
				</th>		
				<th>全部药物品规数:</th>
		   		<th>
					<input type="text" name="DrugTotal" class="easyui-validatebox  easyui-textbox" />				
				</th>		
			</tr>
			<tr>
		   		<th>基本药物销售金额:</th>
		   		<th>
					<input type="text" name="baseDrugTrade" class="easyui-validatebox  easyui-textbox"/>				
				</th>		
				<th>全部药物销售金额:</th>
		   		<th>
					<input type="text" name="DrugTrade" class="easyui-validatebox  easyui-textbox" />				
				</th>		
			</tr>
				
		</table>
	</form>
</div>


<script>
//初始化
$(function(){
    $("input").textbox({validType:'length[0,50]'});
	$('#month').validatebox({ 
		required:true 
	});
	//下拉
	$("#isReformHospital").combobox({
		valueField:'label',    
	   	textField:'value',
	   	panelHeight:'auto',
	   	data: [{
			label: '1',
			value: '是'
		},{
			label: '0',
			value: '否'
		}],
		editable:false,
		value:'1',
		onSelect:function(record){
			
		}
	});
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/baseDrugProvide/add.htmlx",
		onSubmit : function() {
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
				showMsg("添加成功");
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