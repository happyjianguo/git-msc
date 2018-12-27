<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js//My97DatePicker/WdatePicker.js"></script>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>年月:</th>
		   		<th>
					<input type="text" id="month" name="month" class="easyui-textbox" readonly="readonly"/>
				</th>	
				<th>机构名称:</th>
				<th>
					<input id="healthStationType" name="healthStationType"  data-options="required:true" />
				</th>
			</tr>	
			<tr>
		   		<th>是否集中采购且采购品规数、金额占比均不低于60%:</th>
				<th>
					<input id="isHighSixty" name="isHighSixty"  data-options="required:true" />
				</th>	
				<th>是否已实行药品零差率销售:</th>
				<th>
					<input id="isImplementedStation" name="isImplementedStation"  data-options="required:true" />
				</th>		
			</tr>
			<tr>
		   		<th>是否承担30%以上基本公共卫生服务的卫生站:</th>
		   		<th>
					<input id="isGeneralStation" name="isGeneralStation" data-options="required:true" />				
				</th>		
				<th>是否已实施一般诊疗费收费的卫生站:</th>
		   		<th>
					<input id="isThirdHealthStation" name="isThirdHealthStation" data-options="required:true" />				
				</th>	
			</tr>
			<tr>
		   		<th>是否已纳入城乡居民医保门诊统筹实施范围的卫生站:</th>
		   		<th>
					<input id="isInHealthInsurance" name="isInHealthInsurance" data-options="required:true" />				
				</th>		
			</tr>
		</table>
		<input type="hidden" name="id" value=""  >
	</form>
</div>


<script>
//初始化
$(function(){    $("input").textbox({validType:'length[0,50]'});
	$('#month').validatebox({ 
		required:true 
	});
	//下拉
	$("#healthStationType").combobox({
		valueField:'label',    
	   	textField:'value',
	   	panelHeight:'auto',
	   	data: [{
			label: 'healthStation',
			value: '村卫生站'
		},{
			label: 'healthServiceCentre',
			value: '非政府办乡镇卫生院、社区卫生服务中心'
		},{
			label: 'outpatientDepartment',
			value: ' 非政府办门诊部、诊所（医务室）'
		}],
		editable:false,
		value:'healthStation',
		onSelect:function(record){
			
		}
	});
	//下拉
	$("#isHighSixty").combobox({
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
		value:'0',
		onSelect:function(record){
			
		}
	});
	//下拉
	$("#isImplementedStation").combobox({
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
		value:'0',
		onSelect:function(record){
			
		}
	});
	$("#isGeneralStation").combobox({
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
		value:'0',
		onSelect:function(record){
			
		}
	});
	$("#isThirdHealthStation").combobox({
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
		value:'0',
		onSelect:function(record){
			
		}
	});
	$("#isInHealthInsurance").combobox({
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
		value:'0',
		onSelect:function(record){
			
		}
	});
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/baseDrugProgress/edit.htmlx",
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