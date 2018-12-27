<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>单位编码：</th>
		   		<th>
		   			<input type="text" name="code" class="easyui-validatebox  easyui-textbox-20"  data-options="required:true" readonly/>
		   		</th>
		   		<th>单位名称：</th>
		   		<th>
		   			<input type="text" name="fullName" class="easyui-validatebox  easyui-textbox-16"  data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>单位简称：</th>
		   		<th>
		   			<input type="text" name="shortName" class="easyui-validatebox  easyui-textbox-16"  data-options="required:true" />
		   		</th>
		   		<th>拼音简称：</th>
		   		<th>
		   			<input type="text" name="pinyin" class="easyui-validatebox  easyui-textbox-50"  />
		   		</th>
		   </tr>
		   <tr>
				<th>法定代表人：</th>
		   		<th>
		   			<input type="text" name="manager" class="easyui-validatebox  easyui-textbox-3"  />
		   		</th>
		   		<th>注册地址：</th>
		   		<th>
		   			<input type="text" name="registryAddr" class="easyui-validatebox  easyui-textbox-33"  />
		   		</th>
		   </tr>
		   <tr>
				<th>曾用名：</th>
		   		<th>
		   			<input type="text" name="alias" class="easyui-validatebox  easyui-textbox-3"  />
		   		</th>
		   		<th>注册日期：</th>
		   		<th>
		   			<input id="registryDate" name="registryDate" />
		   		</th>
		   </tr>
		   <tr>
		   	<th>国家：</th>
		   		<th>
		   			<input id="country" name="country" />
		   		</th>
		   		<th>公司类型：</th>
		   		<th colspan=3>
		   			厂商<input type="checkbox"  id="isProducer" name="isProducer" value="1" />
		   			供应商<input type="checkbox"  id="isVendor" name="isVendor" value="1" />
		   			配送商<input type="checkbox"  name="isSender" id="isSender" value="1" />
		   			GPO<input type="checkbox"  name="isGPO" id="isGPO" value="1" />
		   		</th>
		   </tr>
		   <tr>
				<th>所在地区：</th>
		   		<th colspan=3>
		   			<input id="combox1" name="combox1" style="width:80px;" />
		   			<input id="combox2" name="combox2" style="width:80px;" />
		   			<input id="combox3" name="combox3" style="width:80px;" />
		   		</th>
		   		
		   </tr>
		   <tr>
				<th>登记注册类型：</th>
		   		<th>
		   			<input id="companyType" name="companyType"/>		   		
		   		</th>
		   		<th>归档文件序号：</th>
		   		<th>
		   			<input type="text" name="archNo" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   </tr>
		   <tr>
				<th>年销售额：</th>
		   		<th>
		   			<input type="text" name="salesValue" class="easyui-validatebox  easyui-textbox"  />
		   		</th>
		   		<th>年份：</th>
		   		<th>
		   			<input type="text" name="salesYear" class="easyui-validatebox  easyui-textbox"  />
		   		</th>
		   </tr>
		   <tr>
				<th>开业成立日期：</th>
		   		<th>
		   			<input id="openDate" name="openDate" />
		   		</th>
		   		<th>人员数：</th>
		   		<th>
		   			<input type="text" name="employeeNum" class="easyui-validatebox  easyui-textbox"  />
		   		</th>
		   </tr>
		   <tr>
				<th>注册资金（万元）：</th>
		   		<th>
		   			<input type="text" name="capital" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2"  />
		   		</th>
		   		<th>总资产：</th>
		   		<th>
		   			<input type="text" name="totalAssets" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2"  />
		   		</th>
		   </tr>
		   <tr>
				<th>通讯地址：</th>
		   		<th>
		   			<input type="text" name="postAddr" class="easyui-validatebox  easyui-textbox-33"  />
		   		</th>
		   		<th>邮政编码：</th>
		   		<th>
		   			<input type="text" name="postCode" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   </tr>
		   <tr>
				<th>电话号码：</th>
		   		<th>
		   			<input type="text" name="telephone" class="easyui-validatebox  easyui-textbox-20"  >
		   		</th>
		   		<th>传真：</th>
		   		<th>
		   			<input type="text" name="fax" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   </tr>
		   <tr>
				<th>电子邮箱：</th>
		   		<th>
		   			<input type="text" name="email" class="easyui-validatebox  easyui-textbox-50"  />
		   		</th>
		   		<th>网址：</th>
		   		<th>
		   			<input type="text" name="website" class="easyui-validatebox  easyui-textbox-50"  />
		   		</th>
		   </tr>
		   <tr>
				<th>生产许可证号：</th>
		   		<th>
		   			<input type="text" name="plincenseNo" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   		<th>有限截止日期：</th>
		   		<th>
		   			<input id="poutDate" name="poutDate" />
		   		</th>
		   </tr>
		   <tr>
				<th>营业执照：</th>
		   		<th>
		   			<input type="text" name="licenseNO" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   		<th>营业执照有效日期：</th>
		   		<th>
		   			<input id="outDate" name="outDate" />
		   		</th>
		   </tr>
		   <tr>
				<th>信用等级：</th>
		   		<th>
		   			<input type="text" name="creditLevel" class="easyui-validatebox  easyui-textbox-6"  />
		   		</th>
		   		<th>信用证号：</th>
		   		<th>
		   			<input type="text" name="creditNo" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   </tr>
		   <tr>
				<th>信用证有效期：</th>
		   		<th>
		   			<input id="creditOutDate" name="creditOutDate" />
		   		</th>
		   		<th>SFDA对应的链接：</th>
		   		<th>
		   			<input type="text" name="sfdaURL" class="easyui-validatebox  easyui-textbox-50"  />
		   		</th>
		   </tr>
		   <tr>
				<th>是否有GMP证书：</th>
		   		<th>
		   			<input type="text" name="hasGsp" class="easyui-validatebox  easyui-textbox"  />
		   		</th>
		   		<th>GMP证书：</th>
		   		<th>
		   			<input type="text" name="gsPNo" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   </tr>
		   <tr>
				<th>授权人姓名：</th>
		   		<th>
		   			<input type="text" name="authorizor" class="easyui-validatebox  easyui-textbox-3"  />
		   		</th>
		   		<th>授权人联系电话：</th>
		   		<th>
		   			<input type="text" name="authorizorPhone" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   </tr>
		   <tr>
				<th>授权人证件号：</th>
		   		<th>
		   			<input type="text" name="authorizorNo" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
				<th>接口编码：</th>
		   		<th>
		   			<input type="password" id="iocode" name="iocode" class="easyui-validatebox  easyui-textbox"/>
		   		</th>
		   </tr>
		   <tr>
				<th>公司简介：</th>
		   		<th colspan="3">
		   			<input type="text" name="introduce" class="easyui-validatebox  easyui-textbox-33"  style="width:590px"/>
		   		</th>
		   		
		   </tr>
		   <tr>
				<th>生产范围：</th>
		   		<th colspan="3">
		   			<input type="text" name="saleScope" class="easyui-validatebox  easyui-textbox-33"  style="width:590px"/>
		   		</th>
		   		
		   </tr>
		   <tr>
				<th>备 注：</th>
		   		<th>
		   			<input type="text" name="notes" class="easyui-validatebox  easyui-textbox-33"  />
		   		</th>
		   		<th>是否禁用：</th>
				<th>
					<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
				</th>   		
		   </tr>	   
		</table>
		<input type="hidden" name="id" value=""  >
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
	    textField:'name',
	    value:''
	});
}
//初始化
$(function(){
	//日期
	$('#registryDate').datebox({
	    //required:true
		editable:false
	});
	$('#openDate').datebox({
		editable:false
	});
	$('#poutDate').datebox({
		editable:false
	});
	$('#outDate').datebox({
		editable:false
	});
	$('#creditOutDate').datebox({
		editable:false
	});
    $("input.easyui-textbox-3").textbox({validType: 'maxLength[6]'});//暂时控制
    $("input.easyui-textbox-6").textbox({validType: 'maxLength[12]'});
    $("input.easyui-textbox-16").textbox({validType: 'maxLength[32]'});
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
    $("input.easyui-textbox-33").textbox({validTYpe: 'maxLength[66]'});
    $("input.easyui-textbox-50").textbox({validTYpe: 'maxLength[50]'});

	//下拉选单
	$('#combox1').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvlone.htmlx",
	    valueField:'id',    
	    textField:'name', 
	    value:'<c:out value='${combox1}'/>',
	    onSelect:function(a,b){
	    	var pid = $('#combox1').combobox('getValue');
	    	var nextCombox = $('#combox2');
	    	toNextCombox(nextCombox,pid);
	    	$('#combox3').combobox({
	    		url:null,
	    		data: [],
	    		value:''
	    	});
	    }
	});
	
	$('#combox2').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx",
		queryParams:{
			parentId:'<c:out value='${combox1}'/>'
		},
	    valueField:'id',    
	    textField:'name', 
	    value:'<c:out value='${combox2}'/>',
	    onSelect:function(a,b){
	    	var pid = $('#combox2').combobox('getValue');
	    	var nextCombox = $('#combox3');
	    	toNextCombox(nextCombox,pid);
	    }
	});
	$('#combox3').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx",
		queryParams:{
			parentId:'<c:out value='${combox2}'/>'
		},
	    valueField:'id',    
	    textField:'name', 
	    value:'<c:out value='${combox3}'/>',
	    onSelect:function(a,b){
	    	
	    }
	});
	$("#iocode").parent().on("focus", "input", function() {
		$("#iocode").textbox("setValue","");
	});
	
	$('#companyType').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2',
	    queryParams:{
	    	"attributeNo": "company_type"
		},
		editable:false
	}).combobox("initClear"); 
	$('#country').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "country"
		},
		editable:false
	}).combobox("initClear"); 
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/company/edit.htmlx",
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			if(!($("#isProducer").is(':checked')||$("#isVendor").is(':checked')||$("#isGPO").is(':checked'))){
				top.$.messager.progress('close');
				showErr("公司身份必须勾选一个");
				return false;
			}
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
				showMsg("修改成功！");
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