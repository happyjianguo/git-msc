<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>药品目录编码：</th>
		   		<th>
		   			<input id="drugId" name="drugId"   data-options="required:true" />
		   		</th>
		   		<th>药品编码：</th>
		   		<th>
		   			<input type="text" name="code" class="easyui-validatebox  easyui-textbox-50" data-options="required:true"  />
		   		</th>
		   </tr>
		   <tr>
				<th>药品名称：</th>
		   		<th>
		   			<input type="text" name="name" class="easyui-validatebox  easyui-textbox-33"  data-options="required:true" />
		   		</th>
		   		<th>产品类别：</th>
		   		<th>
		   			<input id="type" name="type"  class="easyui-textbox-6" />
		   		</th>
		   </tr>
		    <tr>
		   		<th>拼音简称：</th>
		   		<th>
		   			<input type="text" name="pinyin" class="easyui-validatebox  easyui-textbox-50"  />
		   		</th>
				<th>基本药物：</th>
		   		<th>
		   			<input id="baseDrugType" name="baseDrugType"  />
		   		</th>
		   </tr>
		   <tr>
				<th>医保编码：</th>
		   		<th>
		   			<input type="text" name="ybdrugsNO" class="easyui-validatebox  easyui-textbox-20"   />
		   		</th>
		   		<th>医保药品：</th>
		   		<th>
		   			<input id="insuranceDrugType" name="insuranceDrugType"  />
		   		</th>
		   </tr>
		   <tr>
				<th>商品名：</th>
		   		<th>
		   			<input type="text" name="tradeName" class="easyui-validatebox  easyui-textbox-33"  />
		   		</th>
				<th>生产企业：</th>
		   		<th>
		   			<input id="producer" name="producerId"  />
		   		</th>
		   </tr>
		   <tr>	   	
		   		<th>药品规格：</th>
		   		<th>
		   			<input type="text" name="model" id="model" class="easyui-validatebox  easyui-textbox-66" data-options="required:true" readonly="readonly"/>
		   		</th>	
		   		<th>包装材质：</th>
		   		<th>
		   			<input id="packageMaterial" name="packageMaterial"  />
		   		</th>
		   		
		   </tr>	
		    <tr>	   	
		   		<th>剂量：</th>
		   		<th>
		   			<input type="text" name="dose" id="dose" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:3,required:true"   />
		   		</th>	
		   		<th>剂量单位：</th>
		   		<th>
		   			<input id="doseUnit" name="doseUnit"  id="doseUnit"   data-options="required:true"/>
		   		</th>
		   		
		   </tr>
		   <tr>
				<th>包装单位：</th>
		   		<th>
		   			<input id="unit" name="unit"  id="unit"  data-options="required:true" />
		   		</th>
		   		<th>最小制剂单位：</th>
		   		<th>
		   			<input id="minunit" name="minunit"  id="minunit"  data-options="required:true"/>
		   		</th>
		   </tr>
		    <tr>
				<th>包装规格：</th>
		   		<th>
		   			<input type="text" name="packDesc" id="packDesc" class="easyui-validatebox  easyui-textbox-16"  data-options="required:true" readonly="readonly"/>
		   		</th>
		   		<th>单位转换比：</th>
		   		<th>
		   			<input type="text" name="convertRatio" id="convertRatio" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:0,required:true"   />
		   		</th>
		   </tr>
		   <tr>
				<th>国家药品代码：</th>
		   		<th>
		   			<input type="text" name="nationalCode" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
				<th>定价类型：</th>
		   		<th>
		   			<input id="priceType" name="priceType"  />
		   		</th>
		   </tr>
		   <tr> 
		   		<th>物价编码：</th>
		   		<th>
		   			<input type="text" name="priceFileNo" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>  	
		   		<th>药品本位码：</th>
		   		<th>
		   			<input type="text" name="standardCode" class="easyui-validatebox  easyui-textbox-30"  />
		   		</th>
		   </tr>
		    <tr>
		    	<th>批准文号：</th>
		   		<th>
		   			<input type="text" name="authorizeNo" class="easyui-validatebox  easyui-textbox-100" />
		   		</th>
		   		<th></th><th></th>
		    </tr>
		   <tr>
		   		<th>国药准字：</th>
		   		<th>
		   			<input type="text" name="nationalAuthorizeNo" class="easyui-validatebox  easyui-textbox-100" />
		   		</th>
		   		<th>国药准字有效期：</th>
		   		<th>
		   			<input id="authorizeBeginDate" name="authorizeBeginDate" /> -
		   			<input id="authorizeOutDate" name="authorizeOutDate" validType="dateSE['#authorizeBeginDate']" />
		   		</th>
		   </tr>
		   <tr>
				<th>进口药品注册证号：</th>
		   		<th>
		   			<input type="text" name="importFileNo" class="easyui-validatebox  easyui-textbox-30"  />
		   		</th>
		   		<th>进口药品注册证有效期：</th>
		   		<th>
		   			<input id="importBeginDate" name="importBeginDate" /> -
		   			<input id="importOutDate" name="importOutDate" validType="dateSE['#importBeginDate']" />
		   		</th>
		   </tr>
		   <tr>
				<th>产品来源：</th>
		   		<th>
		   			<input id="productSource" name="productSource"  />
		   		</th>
		   		<th>原料来源：</th>
		   		<th>
		   			<input id="materialSource" name="materialSource"  />
		   		</th>
		   </tr>
		   
		   <tr>
				<th>GMP认证：</th>
		   		<th>
		   			<input type="checkbox"  name="gMPFlag"  value="1" />
		   		</th>
		   		<th>质量层次：</th>
		   		<th>
		   			<input id="qualityLevel" name="qualityLevel"  />
		   		</th>
		   </tr>
		   <tr>
				<th>专利类型：</th>
		   		<th>
		   			<input id="patentType" name="patentType"  />
		   		</th>
		   		<th>专利有效期：</th>
		   		<th>
		   			<input id="patentBeginDate" name="patentBeginDate" /> -
		   			<input id="patentOutDate" name="patentOutDate" validType="dateSE['#patentBeginDate']" />
				</th>
		   </tr>
		   <tr>
				<th>优质优价中成药：</th>
		   		<th>
		   			<input type="checkbox"  name="hqgpcmmFlag"  value="1" />
		   		</th>
		   		<th>中医院集诊必备中成药：</th>
		   		<th>
		   			<input type="checkbox"  name="emergencyFlag"  value="1" />
		   		</th>
		   </tr>
		   <tr>
				<th>中药保护品种：</th>
		   		<th>
		   			<input type="checkbox"  name="protectFlag"  value="1" />
		   		</th>
		   		<th>中药保护品种有效期：</th>
		   		<th>
		   			<input id="protectBeginDate" name="protectBeginDate" /> -
		   			<input id="protectOutDate" name="protectOutDate" validType="dateSE['#protectBeginDate']" />
				</th>
		   </tr>
		   <tr>
				<th>新药：</th>
		   		<th>
		   			<input type="checkbox"  name="newDrugFlag"  value="1" />
		   		</th>
		   		<th>新药有效期：</th>
		   		<th>
		   			<input id="newDrugBeginDate" name="newDrugBeginDate" /> -
		   			<input id="newDrugOutDate" name="newDrugOutDate" validType="dateSE['#newDrugBeginDate']" />
		   		</th>
		   </tr>
		   <tr>
				<th>委托加工：</th>
		   		<th>
		   			<input type="checkbox"  name="consignFlag"  value="1" />
		   		</th>
		   		<th>委托加工有效期：</th>
		   		<th>
		   			<input id="consignBeginDate" name="consignBeginDate" /> -
		   			<input id="consignOutDate" name="consignOutDate" validType="dateSE['#consignBeginDate']" />
				</th>
		   </tr>
		   <tr>
				<th>委托加工企业编码：</th>
		   		<th>
		   			<input type="text" name="consigncoCode" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   		<th>委托加工企业名称：</th>
		   		<th>
		   			<input type="text" name="consigncoName" class="easyui-validatebox  easyui-textbox-16"  />
		   		</th>
		   </tr>
		   <tr>
				<th>一级总代理编码：</th>
		   		<th>
		   			<input type="text" name="level1AgentCode" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   		<th>一级总代理名称：</th>
		   		<th>
		   			<input type="text" name="level1AgentName" class="easyui-validatebox  easyui-textbox-16"  />
		   		</th>
		   </tr>
		   <tr>
				<th>限定日剂量（DDD）：</th>
		   		<th>
		   			<input type="text" name="ddd" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   		<th>归档文件序列号：</th>
		   		<th>
		   			<input type="text" name="archFileNo" class="easyui-validatebox  easyui-textbox-20"  />
		   		</th>
		   </tr>
		   <tr>
		   		<th>gpo药品：</th>
		   		<th>
		   			<input type="checkbox"  name="isGPOPurchase"  value="1" />
		   		</th>
		   		<th><span class="gpo_sp">gpo药品：</span></th>
		   		<th>
		   			<span class="gpo_sp">
		   			<input type="text" name="gpoId" id="gpoId" class="easyui-combobox"/>
		   			</span>
		   		</th>	
		   			
		   </tr>	
		   <tr>
				<th>零售价：</th>
		   		<th>
		   			<input type="text" name="finalPrice" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2"  />
		   		</th>
		   		<th>遴选药品目录：</th>
				<th>
					<input id="directoryId" name="directoryId" />
				</th> 
		   </tr>
		   <tr>
		   		<th>是否紧急配送药：</th>
				<th>
					<label><input type="radio" name="isUrgent" value="1" checked >是 </label>
					<label><input type="radio" name="isUrgent" value="0" >否 </label>
				</th> 
				<th>是否禁用：</th>
				<th>
					<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
				</th> 	
		   </tr>
		   <tr>
		   <th>是否国家谈判药品</th>
				<th>
					<label><input type="radio" name="isNationalNegotiations" value="1">是 </label>
					<label><input type="radio" name="isNationalNegotiations" value="0" checked>否 </label>
				</th> 
				<th></th><th></th> 	 	
		   </tr>
		   <tr>
		   		<th>备注：</th>
		   		<th>
		   			<input type="text" name="notes" class="easyui-validatebox  easyui-textbox-33"  />
		   		</th>	
		   		<th>标准编码：</th>
		   		<th>
		   			<input type="text" name="productGCode" class="easyui-validatebox  easyui-textbox"   readonly="readonly"/>
		   		</th> 	
		   </tr>
		</table>
		<input type="hidden" name="id" value=""  >
	</form>


</div>



<script>

//初始化
$(function(){

    $("input.easyui-textbox-16").textbox({validType: 'maxLength[32]'});//暂时控制
    $("input.easyui-textbox-20").textbox({validType: 'maxLength[20]'});
    $("input.easyui-textbox-30").textbox({validType: 'maxLength[30]'});
    $("input.easyui-textbox-33").textbox({validType: 'maxLength[60]'});
    $("input.easyui-textbox-50").textbox({validType: 'maxLength[50]'});
    $("input.easyui-textbox-66").textbox({validType: 'maxLength[100]'});
    $("input.easyui-textbox-100").textbox({validType: 'maxLength[100]'});

	function initPackDesc() {
		var dose = $("input[name='dose']").val();
		var doseUnit = $("#doseUnit").combobox("getText");
		var unitName = $("#unit").combobox("getText");
		var minunit = $("#minunit").combobox("getValue");
		var convertRatio = $("input[name='convertRatio']").val();
		$("#model").textbox("setText",dose+doseUnit);
		$("#packDesc").textbox("setText",convertRatio+minunit+"/"+unitName);
		$("#model").textbox("setValue",dose+doseUnit);
		$("#packDesc").textbox("setValue",convertRatio+minunit+"/"+unitName);
	}
	$("#dose").numberbox({onChange:function() {
		initPackDesc()
	}});
	$("#convertRatio").numberbox({onChange:function() {
		initPackDesc()
	}});
	//日期
	$('#authorizeBeginDate').datebox({
		editable:false
	});
	$('#authorizeOutDate').datebox({
		editable:false
	});
	$('#importBeginDate').datebox({
		editable:false
	});
	$('#importOutDate').datebox({
		editable:false
	});
	$('#patentBeginDate').datebox({
		editable:false
	});
	$('#patentOutDate').datebox({
		editable:false
	});
	$('#protectBeginDate').datebox({
		editable:false
	});
	$('#protectOutDate').datebox({
		editable:false
	});
	$('#newDrugBeginDate').datebox({
		editable:false
	});
	$('#newDrugOutDate').datebox({
		editable:false
	});
	$('#consignBeginDate').datebox({
		editable:false
	});
	$('#consignOutDate').datebox({
		editable:false
	});
	
	//下拉
	$('#drugId').combogrid({
		idField:'id',    
		textField:'genericName',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/drug/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		delay:800,
	    columns: [[
	        {field:'code',title:'目录编码',width:100},
	        {field:'genericName',title:'通用名',width:180},
	        {field:'dosageFormName',title:'剂型',width:60,align:'center'}
	    ]],
	    panelWidth:360,
		required: true,
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#drugId').combogrid('grid').datagrid("reload",{"query['t#genericName_S_LK']":q});
                $('#drugId').combogrid("setValue", q);
            }

        }
	});
	
	$('#drugId').combogrid('grid').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
	});	
	
	
	
	$('#producer').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/list.htmlx",
		required: true,
	    valueField:'id',    
	    textField:'fullName', 
	    queryParams:{
	    	"query['t#isProducer_I_EQ']":1,
	    	"query['t#isDisabled_I_EQ']":0
		},
		editable:false
	});
	
	$('#type').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "product_type"
		},
		required: true,
		editable:false
	}).combobox("initClear");
	
	$('#doseUnit').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "dose_unit"
		},
		required: true,
		editable:false,
	    onSelect:function(a,b){
	    	initPackDesc();
	    }
	});
	$('#packageMaterial').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "pack_meterial"
		},
		editable:false
	}).combobox("initClear"); 
	
	$('#unit').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "packType"
		},
		required: true,
		editable:false,
	    onSelect:function(a,b){
	    	initPackDesc();
	    }
	});
	
	$('#minunit').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'field2',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "packType"
		},
		required: true,
		editable:false,
	    onSelect:function(a,b){
	    	initPackDesc();
	    }
	});
	
	$('#priceType').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "product_priceType"
		},
		editable:false
	}).combobox("initClear"); 
	$('#baseDrugType').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx",
		queryParams:{
			"attributeNo":"drug_type",
			"field1":"01"
		}
	}).combobox("initClear"); 
	$('#insuranceDrugType').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx",
		queryParams:{
			"attributeNo":"drug_type",
			"field1":"05"
		}
	}).combobox("initClear"); 
	$('#productSource').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "product_materialSour"
		},
		editable:false
	}).combobox("initClear"); 
	
	$('#materialSource').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "product_materialSour"
		},
		editable:false
	}).combobox("initClear"); 
	$('#qualityLevel').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "product_qualityLevel"
		},
		editable:false
	}).combobox("initClear"); 
	
	$('#patentType').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "product_patentType"
		},
		editable:false
	}).combobox("initClear"); 
	
	$('#gpoId').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/list.htmlx",
	    valueField:'id',    
	    textField:'fullName', 
	    queryParams:{
	    	"query['t#isGPO_I_EQ']":1,
	    	"query['t#isDisabled_I_EQ']":0
		},
		editable:false
	});
	
	//下拉
	$('#directoryId').combogrid({
		idField:'id',    
		textField:'genericName',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/directory/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		delay:800,
	    columns: [[
	        {field:'id',title:'ID',width:100},
	        {field:'genericName',title:'通用名',width:180},
	        {field:'dosageFormName',title:'剂型',width:60,align:'center'}
	    ]],
	    panelWidth:360,
		required: false,
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#directoryId').combogrid('grid').datagrid("reload",{"query['t#genericName_S_LK']":q});
                $('#directoryId').combogrid("setValue", q);
            }

        }
	});
	
	$('#directoryId').combogrid('grid').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
	});	
	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/product/add.htmlx",
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
	$("input[name='isGPOPurchase']").click(function() {
		if ($(this).prop("checked")) {
			$("#gpoId").combobox({required:true})
			$(".gpo_sp").show();
		} else {
			$("#gpoId").combobox({required:false})
			$(".gpo_sp").hide();
		}
	});
	$(".gpo_sp").hide();
});
</script>