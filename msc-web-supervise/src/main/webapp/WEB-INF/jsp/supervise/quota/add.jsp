<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>指标编码：</th>
		   		<th>
		   			<input type="text" id="code" name="code" class="easyui-validatebox easyui-textbox" data-options="required:true"/>
		   		</th>
		   		<th>指标名称：</th>
		   		<th>
		   			<input type="text" id="name" name="name" class="easyui-validatebox easyui-textbox" data-options="required:true"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>指标描述：</th>
		   		<th colspan="3">
		   			<input type="text" id="remark" name="remark" class="easyui-validatebox easyui-textbox" data-options="required:true" size="80"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>计算指标：</th>
		   		<th colspan="3">
		   			<input type="text" id="calculation" name="calculation" class="easyui-validatebox easyui-textbox" data-options="required:true" size="80"/>
		   		</th>
		   </tr>
		   <tr>
				<th>最小值：</th>
		   		<th>
		   			<input type="text" name="min" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2"  />
		   		</th>
				<th>最大值：</th>
		   		<th>
		   			<input type="text" name="max" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2"  />
		   		</th>
		   </tr>
		   <tr>
				<th>类别：</th>
				<th>
				<input type="text" name="type" id="type"  />
				</th> 	
				<th>是否禁用：</th>
				<th>
					<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
				</th> 	
		   </tr>
		</table>
		<input type="hidden" name="id"  >
	</form>


</div>



<script>

//初始化
$(function(){
	$("#code").textbox({validType:'length[0,30]'});
	$("#remark").textbox({validType:'length[0,200]'});


	$('#type').combobox({
		required: true,
	    valueField:'id',    
	    textField:'name', 
	    data:[{id:"all",name:"全院"},
	          {id:"ordinary",name:"普通门诊"},
	          {id:"urgent",name:"紧急门诊"},
	          {id:"his",name:"住院"}],
		editable:false
	});
});
</script>