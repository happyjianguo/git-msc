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
		   <tr class="search1">
				<th>区域：</th>
		   		<th colspan=3>
		   			<input id="combox1" name="combox1" style="width:80px;" />
		   			<input id="combox2" name="combox2" style="width:80px;" />
		   			<input id="combox3" name="combox3" style="width:80px;" />
		   		</th>
		   </tr>
		   <tr class="search1">
	   		<th>医院：</th>
	   		<th>
	   			<input id="hospitalCode" name="hospitalCode" />
	   		</th>
	   		<th>医院等级：</th>
	   		<th>
	   			<input id="hospitalLeavel" name="hospitalLeavel" />
	   		</th>
		   </tr>
		<%} else {%>
			<tr class="search1">
		   		<th>医院等级：</th>
		   		<th colspan="3">
		   			<input id="hospitalLeavel" name="hospitalLeavel" />
		   		</th>
		   </tr>
		
		<%}%>
		   <tr>
		   		<th>药品分类：</th>
		   		<th>
		   			<input id="ypxz" name="ypxz" />
		   		</th>
		   		<th>医保药品：</th>
		   		<th>
		   			<input id="insuranceDrugType" name="insuranceDrugType" />
		   		</th>
		   </tr>
		   <tr>
		   		<th>基本药物：</th>
		   		<th>
		   			<input id="baseDrugType" name="baseDrugType" />
		   		</th>
		   		<th>抗菌药物：</th>
		   		<th>
		   			<input id="absDrugType" name="absDrugType" />
		   		</th>
		   </tr>
		   <tr>
		   		<th>特殊药品：</th>
		   		<th>
		   			<input id="specialDrugType" name="specialDrugType" />
		   		</th>
		   		<th>功效分类：</th>
		   		<th>
		   			<input id="auxiliaryType" name="auxiliaryType" />
		   		</th>
		   </tr>
		   <tr>
		   		<th>查询时间：</th>
		   		<th colspan="3">
		   			<input id="beginDate_yy" name="beginDate_yy" /><input id="beginDate_mm" name="beginDate_mm" /> 
		   			- <input id="endDate_yy" name="endDate_yy"  /><input id="endDate_mm" name="endDate_mm" /> 
		   		</th>
		   </tr>
		</table>
	</form>


</div>



<script>
//初始化
$(function(){
	var treePath = "<%= treePath%>";
	var zoneCodes = (treePath+",").split(",");
	if(<c:out value="${orgType==1}"/>){
		$(".search1").hide();
	}
	//初始化月份控件
	var month_datas = new Array();
	for(var i=0; i<12; i++){
		var month_data = new Object();
		month_data.month = i<9?"0"+(i+1):i+1;
		month_data.name = i+1+"月";
		month_datas[i] = month_data;
	}
	var defMonth = new Date().getMonth()+1;
	var defMonth1 = new Date().getMonth();
    if(defMonth1==0){
        defMonth1=1;
    }
	defMonth = (defMonth<10?"0"+defMonth:defMonth);
	defMonth1 = (defMonth1<10?"0"+defMonth1:defMonth1);
	initYearCombobox("beginDate_yy");
	initYearCombobox("endDate_yy");
	$('#beginDate_mm').combobox({
		data: month_datas,
		valueField: 'month',
		textField: 'name',
		width:60,
		value:defMonth1,
		editable: false
	});
	$('#endDate_mm').combobox({
		data: month_datas,
		valueField: 'month',
		textField: 'name',
		width:60,
		value:defMonth1,
		editable: false
	});
	$('#orderType').combobox({
		data: [{name:"按金额",value:""},{name:"按数量",value:"1"}],
		valueField: 'value',
		textField: 'name',
		width:60,
		editable: false
	});
	$('#ypxz').combobox({
		data: [{name:"全部",value:""},{name:"西药",value:"0"},{name:"中成药",value:"1"},{name:"中草药",value:"2"}],
		valueField: 'value',
		textField: 'name',
		width:60,
		editable: false
	});
	$('#sumType').combobox({
		data: [{name:"全院",value:""},{name:"门诊",value:"0"},{name:"住院",value:"1"}],
		valueField: 'value',
		textField: 'name',
		width:60,
		editable: false
	});
	$('#auxiliaryType').combobox({
		data: [{name:"全部",value:""},{name:"治疗药品",value:"0"},{name:"辅助药品",value:"1"}],
		valueField: 'value',
		textField: 'name',
		width:60,
		editable: false
	});
	$.ajax({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx',
		dataType:"json",
		method:"POST",
		data:{
			"attributeNo":"drug_type",
			"field1":"05"
		},success: function(data) {
			data.splice(0,0,{id:data[0].parentId,name:"医保药品"});
			data.splice(0,0,{id:"-1",name:"非医保药品"});
			data.splice(0,0,{id:"",name:"全部"});
			$('#insuranceDrugType').combotree({
				data:data,
			    valueField:'id',    
			    textField:'name', 
				parentField:'parentId',
		    	pageSize:1000,
			    queryParams:{
					"attributeNo":"drug_type",
					"field1":"05"
				}
			}); 
		}
	});

	$.ajax({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx',
		dataType:"json",
		method:"POST",
		data:{
			"attributeNo":"drug_type",
			"field1":"01"
		},success: function(data) {
			data.splice(0,0,{id:data[0].parentId,name:"基本药品"});
			data.splice(0,0,{id:"-1",name:"非基本药品"});
			data.splice(0,0,{id:"",name:"全部"});
			$('#baseDrugType').combotree({
				data:data,
			    valueField:'id',    
			    textField:'name', 
				parentField:'parentId'
			}); 
		}
	});
	$.ajax({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx',
		dataType:"json",
		method:"POST",
		data:{
			"attributeNo":"drug_type",
			"field1":"02"
		},success: function(data) {
			data.splice(0,0,{id:data[0].parentId,name:"抗菌药物"});
			data.splice(0,0,{id:"-1",name:"非抗菌药物"});
			data.splice(0,0,{id:"",name:"全部"});
			$('#absDrugType').combotree({
				data:data,
			    valueField:'id',    
			    textField:'name', 
				parentField:'parentId'
			}); 
		}
	});
	$.ajax({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/dm/drugType/listByParams.htmlx',
		dataType:"json",
		method:"POST",
		data:{
			"attributeNo":"drug_type",
			"field1":"03"
		},success: function(data) {
			data.splice(0,0,{id:data[0].parentId,name:"特殊药品"});
			data.splice(0,0,{id:"-1",name:"非特殊药品"});
			data.splice(0,0,{id:"",name:"全部"});
			$('#specialDrugType').combotree({
				data:data,
			    valueField:'id',    
			    textField:'name', 
				parentField:'parentId'
			}); 
		}
	});
	
	//下拉
	$('#hospitalCode').combogrid({
		idField:'code',    
		textField:'fullName',
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/hospital/listByCounty.htmlx',
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
	$('#hospitalLeavel').combobox({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx',
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "hospital"
		},
		editable:false
	}).combobox("initClear"); 
	//下拉选单
		$('#combox1').combobox({
			url: ' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvlone.htmlx',
		    valueField:'id',    
		    textField:'name', 
		    value:zoneCodes[0],
		    editable:false,
		    onSelect:function(a,b){
		    	$('#combox2').combobox({
		    		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx',
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
			combox_data.url=' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx';
			combox_data.queryParams={parentId:zoneCodes[1]}
			combox_data.value=zoneCodes[2];
		}
		$('#combox3').combobox(combox_data);

		combox_data.onSelect = function(a,b){
	    	$('#combox3').combobox({
	    		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx',
	    		queryParams:{
	    			parentId:$('#combox2').combobox('getValue')
	    		}
	    	});
	    };
		if (zoneCodes[0]) {
			combox_data.url=' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx';
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