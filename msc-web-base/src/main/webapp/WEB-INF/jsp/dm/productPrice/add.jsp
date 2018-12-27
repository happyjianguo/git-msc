<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		  
		  <tr>	
		   		<th>统一价格/指定医院：</th>
		   		<th>
		   			<label><input type="radio" name="tType" value="0" checked onclick='selType(0)'>统一价格 </label>
		   			<label><input type="radio" name="tType" value="1"  onclick='selType(1)'>指定医院 </label>
		   			<label><input type="radio" name="tType" value="2"  onclick='selType(2)'>个人价格 </label>

		   		</th>
		   </tr>
		   <tr id='hid'>	
		   		<th>医疗机构：</th>
		   		<th>
		   			<input type="hidden" name="productCode" value="<c:out value='${productCode}'/>"  />	
		   			<input type="hidden" name="vendorCode" value="<c:out value='${vendorCode}'/>" />	
		   			<input id="hospital" name="hospitalCode"  />	
				</th>
		   </tr>
		   <tr id='effectTypeid'>	
		   		<th>统一价格生效方式：</th>
		   		<th>
		   			<input id="effectType" name="effectType"  />	
				</th>
		   </tr>
		   <tr>
				<th>中标价：</th>
		   		<th>
		   			<input type="text" name="biddingPrice" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2,required:true"  />
		   		</th>
		   	</tr>
		   	<tr>
				<th>成交价：</th>
		   		<th>
		   			<input type="text" name="finalPrice" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2,required:true"  />
		   		</th>
		   	</tr>
		    <tr>	
		   		<th>生效日期：</th>
		   		<th>
		   			<input id="effectDate" name="effectDate" />
		   		</th>
		   </tr>
		   <tr>	
		   		<th>有效日期：</th>
		   		<th>
		   			<input id="beginDate" name="beginDate" /> -
		   			<input id="outDate" name="outDate" validType="dateSE['#beginDate']" />
		   		</th>
		   </tr>
		    <!-- <tr>	
		   		<th>禁用：</th>
		   		<th>
		   			<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
		   		</th>
		   </tr> -->
		</table>
		<input type="hidden" name="id" value=""  >
	</form>


</div>



<script>
 function selType(type){
	if(type == 1){
		$("#hid").show();
		$("#effectTypeid").hide();
		$('#effectType').combobox("setValue","");
	}else if(type == 0){
		$('#hospital').combobox("setValue","");
		$("#hid").hide();
		$("#effectTypeid").show();
	}else if(type == 2){
		$('#hospital').combobox("setValue","");
		$('#effectType').combobox("setValue","");
		$("#hid").hide();
		$("#effectTypeid").hide();
	}
	
} 
//初始化
$(function(){
	
	//日期
	$('#beginDate').datebox({
		editable:false,
		required:true
	});
	$('#outDate').datebox({
		editable:false,
		required:true
	});
	$('#effectDate').datebox({
		editable:false,
		required:true
	});
	$('#effectType').combobox({
	    valueField:'label',    
	    textField:'value',
	    value:0,
	    data: [{
			label: '0',
			value: '不覆盖指定价格'
		},{
			label: '1',
			value: '覆盖所有'
		}],
		editable:false,
		width:"auto",
		panelHeight:"auto"
	});
	
	$('#hospital').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/list.htmlx",
	    valueField:'code',    
	    textField:'fullName', 
	    queryParams:{
	    	"query['t#isDisabled_I_EQ']":0
		},
		editable:false
	})/* .combobox("initClear") */; 
	$("#hid").hide();
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/productPrice/add.htmlx",
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
				showMsg("新增成功！");
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			top.$.modalDialog.openner.datagrid('reload');
			top.$.modalDialog.handler.dialog('close');
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>