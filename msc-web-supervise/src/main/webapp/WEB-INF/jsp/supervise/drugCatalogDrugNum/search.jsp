<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		<%
			String treePath = (String)request.getAttribute("treePath");
			Integer searchType = (Integer)request.getAttribute("searchType");
			if (searchType == 1) {
		%>
		   <tr>
				<th>区域:</th>
		   		<th colspan=3>
		   			<input id="combox1" name="combox1" style="width:80px;" />
		   			<input id="combox2" name="combox2" style="width:80px;" />
		   			<input id="combox3" name="combox3" style="width:80px;" />
		   		</th>
		   </tr>
		<tr>
	   		<th>医院：</th>
	   		<th>
	   			<input id="hospitalCode" name="hospitalCode" />
	   		</th>
	   		<th>医院等级：</th>
	   		<th>
	   			<input id="orgLevel" name="orgLevel" />
	   		</th>
		   </tr>
		<!-- <tr>
		   		<th>基本药物：</th>
		   		<th colspan="3">
		   			<input id="baseDrugType" name="baseDrugType" />
		   		</th>
	    </tr> -->
		<%} else {%>
			<tr>
		   		<th>医院等级：</th>
		   		<th>
		   			<input id="orgLevel" name="orgLevel" />
		   		</th>
		   	<!-- 	<th>基本药物：</th>
		   		<th>
		   			<input id="baseDrugType" name="baseDrugType" />
		   		</th> -->
		   </tr>
		
		<%}%>
		</table>
	</form>


</div>



<script>
//初始化
$(function(){
	var treePath = "<%= treePath%>";
	var zoneCodes = (treePath+",").split(",");
	
	//下拉
	$('#hospitalCode').combogrid({
		idField:'code',    
		textField:'fullName',
		url: '<c:out value='${pageContext.request.contextPath }'/>/set/hospital/listByCounty.htmlx',
		pagination:false,
		queryParams:{
			province:zoneCodes[0], 
			city:zoneCodes[1], 
			county:zoneCodes[2],
			"query['t#isDisabled_I_EQ']" : 0
		},
	    columns: [[
	        {field:'code',title:'机构编码',width:150},
	        {field:'fullName',title:'机构名称',width:200}
	    ]],
	    width:200,
	    panelWidth:360,
		delay:800,
		prompt:"名称模糊搜索",
		keyHandler: { 
	        enter: function(e) {
                $('#hospitalCode').combogrid('grid').datagrid("reload",{
        			province:zoneCodes[0], 
        			city:zoneCodes[1], 
        			county:zoneCodes[2],
        			"query['t#isDisabled_I_EQ']" : 0,
        			"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
                $('#hospitalCode').combogrid("setValue", $('#hospitalCode').combogrid("getText"));
	        }
        }
	});
	$('#hospitalCode').combogrid('grid').datagrid("reload",{
		province:zoneCodes[0], 
		city:zoneCodes[1], 
		county:zoneCodes[2],
		"query['t#isDisabled_I_EQ']" : 0,
		"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
	//下拉选单
	$('#orgLevel').combobox({
		url: '<c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx',
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "hospital"
		},
		editable:false
	}).combobox("initClear"); 

	$.ajax({
		url: '<c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx',
		dataType:"json",
		method:"POST",
		data:{
			"attributeNo":"drug_type",
			"field1":"01"
		},success: function(data) {
			data.splice(0,0,{id:"[]",name:"全部"});
			$('#baseDrugType').combobox({
				data:data,
			    valueField:'id',    
			    textField:'name'
			}); 
		}
	});
	//下拉选单
	$('#combox1').combobox({
		url: '<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvlone.htmlx',
	    valueField:'id',    
	    textField:'name', 
	    value:zoneCodes[0],
	    editable:false,
	    onSelect:function(a,b){
	    	$('#combox2').combobox({
	    		url: '<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx',
	    		queryParams:{
	    			parentId:$('#combox1').combobox('getValue')
	    		},
	    		value:""
	    	});
	    	$('#combox2').combobox('setValue',"")
	    	$('#combox3').combobox({
	    		url:null,
	    		data: []
	    	});
	    }
	});
	var combox_data = {
	    valueField:'id',    
	    textField:'name', 
	    editable:false
	}
	
	if (zoneCodes[1]) {
		combox_data.url='<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx';
		combox_data.queryParams={parentId:zoneCodes[1]}
		combox_data.value=zoneCodes[2];
	}
	$('#combox3').combobox(combox_data);

	combox_data.onSelect = function(a,b){
    	$('#combox3').combobox({
    		url: '<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx',
    		queryParams:{
    			parentId:$('#combox2').combobox('getValue')
    		}
    	});
    };
	if (zoneCodes[0]) {
		combox_data.url='<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx';
		combox_data.queryParams={parentId:zoneCodes[0]}
		combox_data.value=zoneCodes[1];
	}
	$('#combox2').combobox(combox_data);

	if(zoneCodes[0]) {
		$("input[name='combox1']").parent().hide();
	}
	if(zoneCodes[1]) {
		$("input[name='combox2']").parent().hide();
	}
	
	
});
</script>