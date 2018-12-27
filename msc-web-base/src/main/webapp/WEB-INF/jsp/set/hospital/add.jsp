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
		   			<input type="text" name="fullName" class="easyui-validatebox easyui-textbox-16" data-options="required:true"  style="width:200px"/>
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
		   		<th>法定代表人:</th>
		   		<th>
					<input type="text" name="manager" class="easyui-validatebox easyui-textbox-3" style="width:200px"/>
				</th>
				<th>注册地址:</th>
		   		<th>
		   			<input type="text" name="registryAddr" class="easyui-validatebox easyui-textbox-33" style="width:200px"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>机构类型:</th>
		   		<th>
					<input id="orgType" name="orgType"/>
				</th>
				<th>注册日期:</th>
		   		<th>
		   			<input id="registryDate" name="registryDate" />
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

		   		<th>机构级别:</th>
		   		<th>
		   			<input id="orgLevel" name="orgLevel"/>
		   		</th>
		   	</tr>
		   	  <tr> 
				<th>归档文件序号:</th>
		   		<th>
		   			<input type="text" name="archNo" class="easyui-textbox-20" style="width:200px">
		   		</th>

		   		<th>开户名称:</th>
		   		<th>
		   			<input type="text" name="nameinBank" class="easyui-textbox-3"  style="width:200px"/>
		   		</th>
		   	</tr>
		   	<tr> 
				<th>纳税人登记号:</th>
		   		<th>
		   			<input type="text" name="taxNO" class="easyui-textbox-20" style="width:200px">
		   		</th>

		   		<th>开户银行:</th>
		   		<th>
		   			<input type="text" name="bankName" class="easyui-textbox-16"  style="width:200px"/>
		   		</th>
		   	</tr>
		   	<tr> 
				<th>开业成立日期:</th>
		   		<th>
		   			<input id="openDate" name="openDate"/>
		   		</th>

		   		<th>开户账号:</th>
		   		<th>
		   			<input type="text" name="bankAccount" class="easyui-textbox-20"  style="width:200px"/>
		   		</th>
		   	</tr>
		   	<tr>
		   		<th>注册资金（万元）:</th>
		   		<th>
					<input type="text" name="capital" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2,required:true style="width:200px"/>
				</th>
				<th>总资产（万元）:</th>
		   		<th>
		   			<input type="text" name="totalAssets" class="easyui-textbox" style="width:200px"/>
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
					<input type="text" name="email" class="easyui-textbox-40" style="width:200px"/>
				</th>
				<th>网址:</th>
		   		<th>
		   			<input type="text" name="website" class="easyui-textbox-50" style="width:200px"/>
		   		</th>
		   </tr>
		   <tr> 
				<th>实有床位数:</th>
		   		<th>
		   			<input type="text" name="actBedNum" class="easyui-textbox" style="width:200px">
		   		</th>

		   		<th>编制床位数:</th>
		   		<th>
		   			<input type="text" name="bedNum" class="easyui-textbox"  style="width:200px"/>
		   		</th>
		   	</tr>
		   	  <tr> 
				<th>人员数:</th>
		   		<th>
		   			<input type="text" name="employeeNum" class="easyui-textbox" style="width:200px">
		   		</th>

		   		<th>卫技人员数:</th>
		   		<th>
		   			<input type="text" name="profsNum" class="easyui-textbox"  style="width:200px"/>
		   		</th>
		   	</tr>
		   	<tr>
		   		<th>诊疗科室数:</th>
		   		<th>
					<input type="text" name="roomsNum" class="easyui-textbox" style="width:200px"/>
				</th>
				<th>年门诊人次数:</th>
		   		<th>
		   			<input type="text" name="patientNum" class="easyui-textbox" style="width:200px"/>
		   		</th>
		   </tr>
		   	<tr>
		   		<th>年出院人次数:</th>
		   		<th>
					<input type="text" name="finishNum" class="easyui-textbox" style="width:200px"/>
				</th>
				<th>接口编号:</th>
		   		<th>
		   			<input type="text" name="iocode" class="easyui-textbox-20" style="width:200px"  data-options="required:true" value="<c:out value='${iocode}'/>"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>卫生许可证号:</th>
		   		<th>
					<input type="text" name="hLicenseNo" class="easyui-textbox-20" style="width:200px"/>
				</th>
				<th>有限截止日期:</th>
		   		<th>
		   			<input id="hOutdate" name="hOutdate"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>营业执照:</th>
		   		<th>
					<input type="text" name="licenseNO" class="easyui-textbox" style="width:200px"/>
				</th>
				<th>营业执照有效日期:</th>
		   		<th>
		   			<input id="outDate" name="outDate"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>机构简介:</th>
		   		<th colspan=3>
					<input type="text" name="introduce" class="easyui-textbox-33" style="width:590px"/>
				</th>
				
		   </tr>
		   <tr>
		   		<th>经营范围:</th>
		   		<th colspan=3>
					<input type="text" name="bizscope" class="easyui-textbox-33" style="width:590px"/>
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
	    textField:'name',
		value:''
	});
}
//初始化
$(function(){
	//日期
	$('#registryDate').datebox({
		editable:false
	});
	$('#openDate').datebox({
		editable:false
	});
	$('#hOutdate').datebox({
		editable:false
	});
	$('#outDate').datebox({
		editable:false
	});
    $("input.easyui-textbox-").textbox({validType: 'maxLength[6]'});//暂时控制
    $("input.easyui-textbox-16").textbox({validType: 'maxLength[32]'});
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
    $("input.easyui-textbox-33").textbox({validType: 'maxLength[66]'});
    $("input.easyui-textbox-50").textbox({validType: 'maxLength[100]'});

//下拉选单
	$('#combox1').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvlone.htmlx",
	    valueField:'id',    
	    textField:'name', 
	    value:'3188',
	    editable:false,
	    onSelect:function(a,b){
	    	var pid = $('#combox1').combobox('getValue');
	    	var nextCombox = $('#combox2');
	    	toNextCombox(nextCombox,pid);
	    	$('#combox3').combobox({
	    		url:null,
	    		data: []
	    	});
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
	    editable:false,
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
	    editable:false,
		queryParams:{
			parentId:'3214'
		},
	    onSelect:function(a,b){
	    	
	    },
	    required:true
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
	$('#orgType').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "hospital_type"
		},
		editable:false
	}).combobox("initClear"); 
	$('#orgLevel').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "hospital"
		},
		editable:false
	}); 
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/hospital/add.htmlx",
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