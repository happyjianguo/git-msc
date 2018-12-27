<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>单位编码:</th>
		   		<th>
					<input type="text" name="code" class="easyui-validatebox easyui-textbox-20" data-options="required:true" style="width:200px"/>
				</th>
				<th>单位名称:</th>
		   		<th>
		   			<input type="text" name="fullName" class="easyui-validatebox easyui-textbox-16" data-options="required:true" style="width:200px"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>单位简称:</th>
		   		<th>
					<input type="text" name="shortName" class="easyui-validatebox easyui-textbox-16" data-options="required:true" style="width:200px"/>
				</th>
				<th>拼音简称:</th>
		   		<th>
		   			<input type="text" name="pinyin" class="easyui-textbox-50"  style="width:200px"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>所在地区:</th>
		   		<th colspan=3>
		   			<input id="combox1" name="combox1" style="width:80px;" />
		   			<input id="combox2" name="combox2" style="width:80px;" />
		   			<input id="combox3" name="combox3" style="width:80px;" />
		   		</th>		
		   	</tr>
		    <tr> 
				<th>机构隶属关系:</th>
		   		<th>
		   			<input id="reportType" name="reportType"/>
		   		</th>

		   		<th>归档文件序号:</th>
		   		<th>
		   			<input type="text" name="archNo" class="easyui-textbox-20"  style="width:200px"/>
		   		</th>
		   	</tr>
		   	<tr>
		   		<th>通讯地址:</th>
		   		<th>
					<input type="text" name="postAddr" class="easyui-textbox-33" style="width:200px"/>
				</th>
				<th>邮政编码:</th>
		   		<th>
		   			<input type="text" name="postCode" class="easyui-textbox-20" style="width:200px"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>电话号码:</th>
		   		<th>
					<input type="text" name="telephone" class="easyui-textbox-20" style="width:200px"/>
				</th>
				<th>传真:</th>
		   		<th>
		   			<input type="text" name="fax" class="easyui-textbox-20" style="width:200px"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>电子邮箱:</th>
		   		<th>
					<input type="text" name="email" class="easyui-textbox-50" style="width:200px"/>
				</th>
				<th>网址:</th>
		   		<th>
		   			<input type="text" name="website" class="easyui-textbox-50" style="width:200px"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>备注:</th>
		   		<th>
					<input type="text" name="notes" class="easyui-textbox-33" style="width:200px"/>
				</th>
				<th>是否禁用:</th>
		   		<th>
		   			<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
		   		</th>
		   </tr>
		</table>
	</form>
</div>



<script>
function toNextCombox(nextCombox,pid){
	nextCombox.combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx",
		queryParams:{
			parentId:pid
		},
	    valueField:'id',    
	    textField:'name'
	});
}
//初始化
$(function(){
	//下拉选单
	$('#combox1').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvlone.htmlx",
	    valueField:'id',    
	    textField:'name', 
	    value:'3188',
	    onSelect:function(a,b){
	    	var pid = $('#combox1').combobox('getValue');
	    	var nextCombox = $('#combox2');
	    	toNextCombox(nextCombox,pid);
	    }
	});
	$('#combox2').combobox({
	    valueField:'id',    
	    textField:'name', 
	    url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx",
		queryParams:{
			parentId:'3188'
		},
	    value:'3214',
	    onSelect:function(a,b){
	    	var pid = $('#combox2').combobox('getValue');
	    	var nextCombox = $('#combox3');
	    	toNextCombox(nextCombox,pid);
	    }
	});
	$('#combox3').combobox({
	    valueField:'id',    
	    textField:'name', 
	    url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx",
		queryParams:{
			parentId:'3214'
		},
	    onSelect:function(a,b){
	    	
	    }
	});
	$('#reportType').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "org_subjection"
		},
		editable:false
	}).combobox("initClear");

    $("input.easyui-textbox-16").textbox({validType: 'maxLength[32]'});//暂时控制
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
    $("input.easyui-textbox-33").textbox({validType: 'maxLength[60]'});
    $("input.easyui-textbox-50").textbox({validType: 'maxLength[50]'});

	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/regulationorg/add.htmlx",
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
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>