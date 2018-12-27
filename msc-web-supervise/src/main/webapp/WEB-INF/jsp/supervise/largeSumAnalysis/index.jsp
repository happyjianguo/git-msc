<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<style>
#toolbar{
width:100%;
}
#time{
margin-right:30px;float:right;
}
#search{
display:none;
}
</style>
<html>
	
<body class="easyui-layout">
	<div data-options="region:'center',title:''" >
		<div style="padding:6px 0px 3px 3px" id="search">
		<form id="form1" name="form1"  method="post">
		<span id = "search1">
			区域:<input id="combox1" name="combox1" style="width:75px;" />
		   		<input id="combox2" name="combox2" style="width:75px;" />
		   		<input id="combox3" name="combox3" style="width:75px;" />
		   		<span class="datagrid-btn-separator split-line" ></span>
		   	医院等级:<input id="hospitalLeavel" name="hospitalLeavel" style="width:75px;"/>
			医院:<input id="hospitalCode" name="hospitalCode" style="width:80px;"/>
				<span class="datagrid-btn-separator split-line" ></span>
		</span>
			金额设置:<input id="setSum" name="setSum" style="width:70px;" class="easyui-textbox"/>
			时间:<input id="beginDate_yy" name="beginDate_yy" /><input id="beginDate_mm" name="beginDate_mm" /> 
		   			- <input id="endDate_yy" name="endDate_yy"  /><input id="endDate_mm" name="endDate_mm" /> 
        	<a href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn">查询</a>
        	</form>
        	<div id="toolbar" class="search-bar" >
				<shiro:hasPermission name="supervise:largeSumAnalysis:export">
        			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" onclick="exportexcel()">导出</a>
				</shiro:hasPermission>
        		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-chart_bar',plain:true" onclick="showChart()">图表</a>
		        <span id='time'></span>
	    	</div>
		</div>
	<div>
		<table id="dg"></table>
	</div>
	</div>
</body>
</html>

<script>


//初始化
$(function(){
	var treePath = "<c:out value='${treePath}'/>";
	var zoneCodes = (treePath+",").split(",");
	window.zoneName = "<c:out value='${zoneName}'/>";
	window.queryType = "<c:out value='${queryType}'/>";
	window.jsonStr = decodeURIComponent("<c:out value='${jsonStr}'/>");
	if (jsonStr.length == 0) {
		jsonStr = "{}";
	}
	if("<c:out value='${isFirst}'/>" == "1"){
		$("#search").show();
		if("<c:out value='${orgType}'/>"=="1"){
			$("#search1").hide();
		}
	}
	window.queryParams = eval("("+jsonStr+")");
	if (jsonStr.length > 2) {
		//查询条件不为空，说明不是首次进入
		window.queryParams["queryType"] = "<c:out value='${queryType}'/>";
	}
	window.nextQueryType = "<c:out value='${queryType+1}'/>";

	$("#dg").datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - (window.queryType>2?2:39),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/largeSumAnalysis/page.htmlx",
		queryParams:window.queryParams,
		pageSize:20,
		pageNumber:1,
		columns:[getColumns(window.queryParams["queryType"])],
		toolbar: "#toolbar",
		onDblClickRow: function(index,row){
			addOpen(row);
		},
		onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});	
	//输入框
	$('#setSum').textbox("setValue","<c:out value='${minSum}'/>");
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
	$('#hospitalCode').combogrid({
		idField:'code',    
		textField:'fullName',
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/hospital/listByCounty.htmlx',
		pagination:false,
		queryParams:{
			province:zoneCodes[0], 
			city:zoneCodes[1], 
			county:zoneCodes[2]
		},
	    columns: [[
	        {field:'code',title:'机构编码',width:150},
	        {field:'fullName',title:'机构名称',width:200}
	    ]],
	    width:160,
	    panelWidth:360,
		delay:800,
		prompt:"名称模糊搜索",
		keyHandler: { 
	        enter: function(e) {
                $('#hospitalCode').combogrid('grid').datagrid("reload",{
        			province:zoneCodes[0], 
        			city:zoneCodes[1], 
        			county:zoneCodes[2],
        			"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
                $('#hospitalCode').combogrid("setValue", $('#hospitalCode').combogrid("getText"));
	        }
        }
	}).combobox("initClear");
	$('#hospitalCode').combogrid('grid').datagrid("reload",{
		province:zoneCodes[0], 
		city:zoneCodes[1], 
		county:zoneCodes[2],
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
	    //value:zoneCodes[0],
	    editable:false,
	    onSelect:function(a,b){
	    	$('#combox2').combobox({
	    		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx',
	    		queryParams:{
	    			parentId:$('#combox1').combobox('getValue')
	    		},
	    		value:""
	    	});
	    	//$('#combox2').combobox('setValue',"")
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
	$('#combox3').combobox(combox_data).combobox("initClear");

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
	$('#combox2').combobox();

	if(zoneCodes[0]) {
		$("input[name='combox1']").parent().hide();
	}
	if(zoneCodes[1]) {
		$("input[name='combox2']").parent().hide();
	}  
	//搜索功能
	$("#query_btn").click(function() {
		 var query = {};
		 for (var k in window.queryParams) {
			query[k]=window.queryParams[k];
		}
		query["queryType"] = window.nextQueryType;  
		var hospitalCode = $("#hospitalCode").combobox("getValue");
		var provinceCode = $("#combox1").combobox("getValue");
     	if (provinceCode.length != 0) {
			 window.nextQueryType = 2; 
		}
		var cityCode = $("#combox2").combobox("getValue");
		if (cityCode.length != 0) {
			window.nextQueryType = 3;
		}
		var countyCode = $("#combox3").combobox("getValue");
		if (countyCode.length != 0) {
			window.nextQueryType = 4;
		}	
		if ($("#hospitalCode").combobox("getValue").length !=0) {
			window.nextQueryType = 5;
		}  
		var hospitalLeavel = $("#hospitalLeavel").combobox("getValue");
		if ($("#hospitalLeavel").combobox("getValue").length !=0) {
			window.nextQueryType = 5;
		} 
		var begindata = $("#beginDate_yy").combobox("getValue")+"-"+$("#beginDate_mm").combobox("getValue");
		var enddata = $("#endDate_yy").combobox("getValue")+"-"+$("#endDate_mm").combobox("getValue");
		var minSum = $("#setSum").textbox("getValue");
		console.log(minSum);
		window.queryParams={
			    "query['z#hospitalCode_S_EQ']" :hospitalCode,
				"query['z#provinceCode_S_EQ']" : provinceCode,
				"query['z#cityCode_S_EQ']" : cityCode,
				"query['z#countyCode_S_EQ']" : countyCode, 
				"query['z#orgLevel_S_EQ']" :hospitalLeavel,
				"query['month_S_GE']":begindata,
				"query['month_S_LE']":enddata,
				"query['drugSum_L_GE']":minSum,
				queryType:window.nextQueryType-1
		}; 
	     $('#dg').datagrid({
				columns:[getColumns(window.nextQueryType-1)],
				queryParams:window.queryParams
		});  
	});

});

//钻取
function addOpen(row) {
	var query = {};
	for (var k in window.queryParams) {
		query[k]=window.queryParams[k];
	}
	var title = "";
	query["queryType"] = window.nextQueryType;
	var url = "/supervise/largeSumAnalysis.htmlx?queryType="+window.nextQueryType+"&jsonStr=";
	if (window.nextQueryType == 1) {
		query["query['provinceCode_S_EQ']"] = row.PROVINCECODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "大金额处方分析(省)";
	} else if (window.nextQueryType == 2) {
        query["query['provinceCode_S_EQ']"] = row.PROVINCECODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "大金额处方分析(市)";
	} else if (window.nextQueryType == 3) {
        query["query['cityCode_S_EQ']"] = row.CITYCODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "大金额处方分析(区/县)";
	} else if (window.nextQueryType == 4) {
		query["query['z#countyCode_S_EQ']"] = row.COUNTYCODE;
		query["query['zoneCode_S_RLK']"] = undefined;
		url += encodeURIComponent(JSON.stringify(query));
		title = "大金额处方分析(医院)";
	} else if (window.nextQueryType == 5) {
		query["query['provinceCode_S_EQ']"] = undefined;
		query["query['cityCode_S_EQ']"] = undefined;
		query["query['z#countyCode_S_EQ']"] = undefined;
        query["query['z#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "大金额处方分析(科室)";
	} else if (window.nextQueryType == 6) {
		query["query['provinceCode_S_EQ']"] = undefined;
		query["query['cityCode_S_EQ']"] = undefined;
		query["query['countyCode_S_EQ']"] = undefined;
		query["query['departCode_S_EQ']"] = row.DEPARTCODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "大金额处方分析(医生)";
	} else{
		query = {};
		query["query['a#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
		query["query['a#departCode_S_EQ']"] = row.DEPARTCODE;
		query["query['doctorCode_S_EQ']"] = row.DOCTORCODE;
		query["query['drugSum_L_GE']"] = (window.queryParams["query['drugSum_L_GE']"]==undefined?($("#setSum").textbox("getValue")):(window.queryParams["query['drugSum_L_GE']"]));
		query["query['month_S_GE']"] = window.queryParams["query['month_S_GE']"];
		query["query['month_S_LE']"] = window.queryParams["query['month_S_LE']"];
		url = "/supervise/clinicRecipe/index.htmlx?queryType="+window.nextQueryType+"&jsonStr="+encodeURIComponent(JSON.stringify(query));
		title = "大金额处方分析(门诊处方)";
	}
    top.addTab(title, url,null,true);
}	
function getColumns(queryType) {
	var columns = [
			{field:'LARGESUMRECIPE',title:'大金额处方数',width:280,align:'center'}, 
		   ];

	var first = {field:'ZONENAME',title:'区域',width:300,align:'center',formatter: function(value,row,index){
		return window.zoneName;
	}};
	var second =null;
	var third = null;
	if (queryType == 1) {
		first = {field:'PROVINCENAME',title:'省',width:300,align:'center'};
	} else if (queryType == 2) {
		first = {field:'CITYNAME',title:'市',width:300,align:'center'};
	} else if (queryType == 3) {
		first = {field:'COUNTYNAME',title:'区/县',width:300,align:'center'};
	} else if (queryType == 4) {
		first = {field:'HOSPITALNAME',title:'医院',width:300,align:'center'};
	} else if (queryType == 5) {
		first = {field:'DEPARTNAME',title:'科室',width:300,align:'center'};
		second = {field:'HOSPITALNAME',title:'医院',width:300,align:'center'};
	} else if (queryType == 6) {
		first = {field:'DOCTORNAME',title:'医生',width:300,align:'center'};
		second = {field:'HOSPITALNAME',title:'医院',width:300,align:'center'};
		third = {field:'DEPARTNAME',title:'科室',width:300,align:'center'};
	}
	columns.splice(0,0,first);
	if(queryType==5){
		columns.splice(0,0,second);
	}else if(queryType==6){
		columns.splice(0,0,second);
		columns.splice(1,0,third);
	}	
	var begindate = window.queryParams["query['month_S_GE']"];
	var enddate = window.queryParams["query['month_S_LE']"];
	console.log(time);
	if (begindate!=undefined && begindate!="") {
		console.log("查询时间  : "+begindate+"-"+enddate);
		$("#time").html("查询时间  : "+begindate+" -- "+enddate);
	}
	return columns;
}
function showChart() {
	var nextQueryType = (window.queryParams["queryType"]);
	top.$.modalDialog.data= [$('#dg').datagrid("getRows"), nextQueryType,window.zoneName];
	top.$.modalDialog({
		title : "查询",
		width : 800,
		height : 460,
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/largeSumAnalysis/chart.htmlx"
	});
}
function getheaders(){
	var headers = new Array(6);
	var beanNames = new Array(6);
	var columns = getColumns(window.queryParams["queryType"]);
	var value = $('#dg').datagrid("getRows");
	//console.log(columns);
	 for(var i=0;i<columns.length;i++){
		 console.log(columns[i]);
		 headers[i]=columns[i]['field'];
	}  
	 return headers;
}
function getbeanNames(){
	var headers = new Array(6);
	var beanNames = new Array(6);
	var columns = getColumns(window.queryParams["queryType"]);
	var value = $('#dg').datagrid("getRows");
	//console.log(columns);
	 for(var i=0;i<columns.length;i++){
		 console.log(columns[i]);
		 beanNames[i]=columns[i]['title'];
	}  
	 return beanNames;
}

function exportexcel(){
	var nextQueryType = (window.jsonStr.length <= 2?0:window.queryParams["queryType"]);
	var orderType = window.queryParams["orderType"]||0;
	var headers = getheaders();
	var beanNames = getbeanNames();
	var url = " <c:out value='${pageContext.request.contextPath }'/>/supervise/largeSumAnalysis/export.htmlx?";
	for (var k in window.queryParams) {
		url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(window.queryParams[k]);
	}
	url +="&headers="+encodeURIComponent(headers)+"&beanNames="+encodeURIComponent(beanNames);
	window.open(url);
}
</script>