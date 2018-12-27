<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>区域：</th>
				<th><input id="combox1" name="combox1" style="width:80px;" />
		   			<input id="combox2" name="combox2" style="width:80px;" />
		   			<input id="combox3" name="combox3" style="width:80px;" /></th>
				<th>医疗机构名称</th>
	   			<th><input id="hospitalCode" name="hospitalCode" /></th>
		   </tr>
		   <tr>
		   		<th>医疗机构级别：</th>
		   		<th><input id="orgLevel" name="orgLevel" /></th>
		   		<th>医疗机构类别：</th>
		   		<th><input id="orgType" name="orgType" /></th>
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
	var treePath = "<c:out value='${treePath}'/>";
	var zoneCodes = (treePath+",").split(",");
	console.log(zoneCodes);
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
	defMonth = (defMonth<10?"0"+defMonth:defMonth);
	defMonth1 = (defMonth1<10?"0"+defMonth1:defMonth1);
    if(defMonth1==0){
        defMonth1=1;
    }
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
	//下拉
	$('#hospitalCode').combogrid({
		idField:'code',    
		textField:'fullName',
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/hospital/listByCounty.htmlx',
		pagination:false,
		queryParams:{
			province:null, 
			city:null, 
			county:null,
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
                	province:null, 
        			city:null, 
        			county:null,
        			"query['t#isDisabled_I_EQ']" : 0,
        			"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
                $('#hospitalCode').combogrid("setValue", $('#hospitalCode').combogrid("getText"));
	        }
        }
	});
	$('#hospitalCode').combogrid('grid').datagrid("reload",{
		province:null, 
		city:null, 
		county:null,
		"query['t#isDisabled_I_EQ']" : 0,
		"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
	//下拉选单 
	$('#orgLevel').combobox({
		url: ' <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx',
	    valueField:'id',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "hospital"
		},
		editable:false
	}).combobox("initClear"); 
	
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