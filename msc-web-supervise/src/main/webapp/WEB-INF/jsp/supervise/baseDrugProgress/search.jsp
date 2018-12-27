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
				
		   </tr>
		  <tr>
		 		<th>医疗机构名称</th>
	   			<th><input id="healthStationType" name="healthStationType" style="width:200px;"/></th>
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
			value: '非政府办乡镇卫生院卫生服务中心'
		},{
			label: 'outpatientDepartment',
			value: '非政府办门诊所'
		}],
		editable:false,
		value:'healthStation',
		onSelect:function(record){
			
		}
	});
	
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