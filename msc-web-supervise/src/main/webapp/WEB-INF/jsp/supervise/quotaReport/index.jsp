<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="指标明细" />

<html>
<body class="easyui-layout">
<div style="padding:10px 50px;margin:0 auto;width:920px;">
指标类型：
<input id="queryType" name="queryType"/>
查询时间：<input id="yearS"/><input id="monthS"/>至<input id="yearE"/><input id="monthE"/>
医院/区县:
<input id="type" name="type" style="width:80px;" />
维度:
<input id="ttype" name="ttype" style="width:80px;" />
<a id="search_btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询  </a> 
</div>
<div style="width:100%;height:100%;">
	<div id="chart" style="width:50%;height:300px;">
	</div>
</div>
</body>
</html>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	$(function() {
		function doSearch() {
			var monthS = $('#yearS').combobox('getValue')+"-"+$('#monthS').combobox('getValue');
			var monthE = $('#yearE').combobox('getValue')+"-"+$('#monthE').combobox('getValue');
			var queryType = $('#queryType').combobox('getValue');
			var type = $('#type').combobox('getValue');
			var ttype = $('#ttype').combobox('getValue');
			var options = {
				"query[a#month_S_GE]":monthS,
				"query[a#month_S_LE]":monthE
			};
			echarts.init(document.getElementById('chart')).setOption(queryByType(queryType,type,ttype,options));
		}
		$("#search_btn").click(doSearch);
		initYearCombobox("yearS");
		initYearCombobox("yearE");
		//初始化月份控件
		var month_datas = new Array();
		for(var i=0; i<12; i++){
			var month_data = new Object();
			month_data.month = i<9?"0"+(i+1):i+1;
			month_data.name = i+1+"月";
			month_datas[i] = month_data;
		}
		$('#monthS').combobox({
			data: month_datas,
			valueField: 'month',
			textField: 'name',
			value:'01',
			width:60,
			editable: false
		});
		$('#monthE').combobox({
			data: month_datas,
			valueField: 'month',
			textField: 'name',
			value:'12',
			width:60,
			editable: false
		});
		//指标查询下拉
		$('#queryType').combobox({
		    valueField:'id',    
		    textField:'name', 
		    value:0,
		    editable:false,
		    data:[{id:0,name:"药品总收入"},
		          {id:1,name:"总收入"},
		          {id:2,name:"每就诊人均药品费用"},
		          {id:3,name:"每就诊人均费用"},
		          {id:4,name:"每出院人均药品费用"},
		          {id:5,name:"每出院人均费用"},
		          {id:6,name:"每床日药品费用"},
		          {id:7,name:"每床日费用"}]
		});
		//指标查询下拉
		$('#type').combobox({
		    valueField:'id',    
		    textField:'name', 
		    value:'Hospital',
		    editable:false,
		    data:[{id:"Hospital",name:"医院"},{id:"County",name:"区县"}]
		});
		//统计方式
		$('#ttype').combobox({
		    valueField:'id',    
		    textField:'name', 
		    value:'month',
		    editable:false,
		    data:[{id:"month",name:"月份"},{id:"医院",name:"医院"}]
		});
		
	})

function getData(method,data) {
	var result = [];
	$.ajax({
		url:"<c:out value='${pageContext.request.contextPath }'/>/supervise/quotaReport/"+method+".htmlx",
		data:data,
		dataType:"json",
		async: false,
		method:"post",
		success:function(data) {
			result = data;
		}
	})
	return result;
}
function queryByType(queryType,type,ttype,options) {
	var data0 = [];
	var data1 = [];
	var key = "";
	var keyName = "";
	if (queryType==0) {
		var data0 = getData("groupHisBy"+type,options);
		var data1 = getData("groupClinicBy"+type,options);
		key = "DRUGSUM";
		keyName = "药品总费用";
	} else if (queryType == 1) {
		var data0 = getData("groupHisBy"+type,options);
		var data1 = getData("groupClinicBy"+type,options);
		key = "SUM";
		keyName = "总收入";
	} else if (queryType == 2) {
		var data1 = getData("groupClinicBy"+type,options);
		key = "RJDRUGSUM";
		keyName = "每就诊人均药品费用";
	} else if (queryType == 3) {
		var data1 = getData("groupClinicBy"+type,options);
		key = "RJSUM";
		keyName = "每就诊人均费用";
	} else if (queryType == 4) {
		var data0 = getData("groupHisBy"+type,options);
		key = "RJDRUGOUTSUM";
		keyName = "每出院人均药品费用";
	} else if (queryType == 5) {
		var data0 = getData("groupHisBy"+type,options);
		key = "RJOUTSUM";
		keyName = "每出院人均费用";
	} else if (queryType == 6) {
		var data0 = getData("groupHisBy"+type,options);
		key = "RJDRUGDAYSUM";
		keyName = "每床日药品费用";
	} else if (queryType == 7) {
		var data0 = getData("groupHisBy"+type,options);
		key = "RJDAYSUM";
		keyName = "每床日费用";
	}
	return ttype=="month"?getWdByMonth(data0,data1,key,keyName):getWdByHis(data0,data1,key,keyName);
}
//按维度统计（月份）
function getWdByMonth(data0,data1,key,keyName) {

	var months = {};
	var sumArr = {};
	for(var i=0;i<data0.length;i++) {
		var obj = sumArr[data0[i].CODE];
		if(obj == undefined) {
			obj = {name:data0[i].NAME,data:[]};
		}
		obj.data[data0[i].MONTH]=(data0[i][key]||0);
		months[data0[i].MONTH] = 1;
		sumArr[data0[i].CODE] = obj;
	}
	for(var i=0;i<data1.length;i++) {
		var obj = sumArr[data1[i].CODE];
		if(obj == undefined) {
			obj = {name:data1[i].NAME,data:[]};
		}
		obj.data[data1[i].MONTH]= Number(((obj.data[data1[i].MONTH]||0)+data1[i][key]).toFixed(2));
		months[data1[i].MONTH] = 1;
		sumArr[data1[i].CODE] = obj;
	}
    var k = 0;
    var newMonth=[];
	for(var k in months) {
		newMonth.push(k);
	}
	newMonth.sort();
	var newData = [];
	var hisNames = [];
	for (var k in sumArr) {
		var obj = {name:sumArr[k].name,type:'line',stack: '总量'};
		var data=[];
		for(var i=0;i<newMonth.length;i++) {
			data[i]=sumArr[k].data[newMonth[i]]||0;
		}
		obj.data=data;
		newData.push(obj);
		hisNames.push(sumArr[k].name);
	}

	return {
		    tooltip: {
		        trigger: 'axis'
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
		    xAxis: {
		        type: 'category',
		        boundaryGap: false,
		        data: newMonth
		    },
		    yAxis: {
		        type: 'value'
		    },
		    series: newData
		};
}

//按维度统计（医院）
function getWdByHis(data0,data1,key,keyName) {

	var hiss = {};
	var sumArr = {};
	for(var i=0;i<data0.length;i++) {
		var obj = sumArr[data0[i].MONTH];
		if(obj == undefined) {
			obj = {name:data0[i].MONTH,data:[]};
		}
		obj.data[data0[i].NAME]=(data0[i][key]||0);
		hiss[data0[i].NAME] = 1;
		sumArr[data0[i].MONTH] = obj;
	}
	for(var i=0;i<data1.length;i++) {
		var obj = sumArr[data1[i].MONTH];
		if(obj == undefined) {
			obj = {name:data1[i].MONTH,data:[]};
		}
		obj.data[data1[i].NAME]= Number(((obj.data[data1[i].NAME]||0)+data1[i][key]).toFixed(2));
		hiss[data1[i].NAME] = 1;
		sumArr[data1[i].MONTH] = obj;
	}
  var k = 0;
  var newMonth=[];
	for(var k in hiss) {
		newMonth.push(k);
	}
	var newData = [];
	var hisNames = [];
	for (var k in sumArr) {
		var obj = {name:sumArr[k].name,type:'line',stack: '总量'};
		var data=[];
		for(var i=0;i<newMonth.length;i++) {
			data[i]=sumArr[k].data[newMonth[i]]||0;
		}
		obj.data=data;
		newData.push(obj);
		hisNames.push(sumArr[k].name);
	}

	return {
		    tooltip: {
		        trigger: 'axis'
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
		    xAxis: {
		        type: 'category',
		        boundaryGap: false,
		        data: newMonth
		    },
		    yAxis: {
		        type: 'value'
		    },
		    series: newData
		};
}
</script>
