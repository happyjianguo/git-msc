<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<style>
#toolbar{
width:100%;
}
#time{
margin-right:30px;float:right;
}
</style>
<html>
<body class="easyui-layout">
	<div  data-options="region:'center',title:''">
		<div id="search" style="padding:6px 0px 3px 3px">
		<form id="form1" name="form1"  method="post">
		<span id="search1">
		区域:
		<input id="combox1" name="combox1" style="width: 75px;" />
		<input id="combox2" name="combox2" style="width: 75px;" />
		<input id="combox3" name="combox3" style="width: 75px;" />
		<span class="datagrid-btn-separator split-line" ></span>
		医院等级:
		<input id="orgLevel" name="orgLevel" style="width:75px;"/>
		医院:
		<input id="hospitalCode" name="hospitalCode" />
		<span class="datagrid-btn-separator split-line" ></span>
		</span>
		时间:
		<input id="beginDate_yy" name="beginDate_yy" />
		<input id="beginDate_mm" name="beginDate_mm" />
		-
		<input id="endDate_yy" name="endDate_yy" />
		<input id="endDate_mm" name="endDate_mm" />
		
		<a href="#"  class="easyui-linkbutton" iconCls="icon-search"
			onclick="dosearch()">查询</a>
		</form>
		</div>
		<div>
		<div id="toolbar" class="search-bar" >
				<shiro:hasPermission name="supervise:hisOutDrugAnalysis:export">
        			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" onclick="exportexcel()">导出</a>
				</shiro:hasPermission>
        		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-chart_bar',plain:true" onclick="showChart()">图表</a>
		        <span id='time'></span>
	    	</div>
			<table id="dg"></table>
		</div>
	</div>
		
	
</body>
</html>
<script>
	function filterFunc() {
		$('#dg').datagrid({
			remoteFilter : true
		});
		$('#dg').datagrid('enableFilter', [ {
			field : 'isDisabled',
			type : 'text',
			isDisabled : 1
		} ]);
	}

	//初始化
	$(function() {
		var treePath = "<c:out value='${treePath}'/>";
		var zoneCodes = (treePath + ",").split(",");
		window.zoneName = "<c:out value='${zoneName}'/>";
		window.queryType = "<c:out value='${queryType}'/>";
		if(window.queryType>2){
			$("#search").hide();
		}
		if(<c:out value="${orgType==1}"/>&&window.queryType==4){
			$("#search").show();
			$("#search1").hide();
		}
		window.jsonStr = decodeURIComponent("<c:out value='${jsonStr}'/>");
		if (jsonStr.length == 0) {
			jsonStr = "{}";
		}
		window.queryParams = eval("(" + jsonStr + ")");
		//console.log(window.queryParams);
		if (jsonStr.length>2) {
			//查询条件不为空，说明不是首次进入
			window.queryParams["queryType"] = "<c:out value='${queryType}'/>";
		}
		window.nextQueryType = "<c:out value='${queryType+1}'/>";

		//datagrid
		$('#dg').datagrid({
			fitColumns : true,
			striped : true,
			singleSelect : true,
			rownumbers : true,
			border : true,
			height :$(this).height() - (window.queryType>2?5:40),
			pagination : true,
			url : "<c:out value='${pageContext.request.contextPath }'/>/supervise/hisOutDrugAnalysis/page.htmlx",
			queryParams : window.queryParams,
			pageSize : 20,
			pageNumber : 1,
			columns : [ getColumns(window.queryParams["queryType"]) ],
			toolbar: "#toolbar",
			onDblClickRow : function(index, row) {
				addOpen(row);
			},
			onLoadSuccess : function(data) {
				var array = data.rows;
				var begindata = array[0]['begindata'];
				var enddata = array[0]['enddata'];
				var timetile="";
				if(begindata==enddata||enddata==""){
					timetile=begindata;
				}else{
					timetile=begindata+" -- "+enddata;
				}
				$("#time").html("查询时间  : "+timetile);
				$('#dg').datagrid('doCellTip', {
					delay : 500
				}); 
				$(".tip").tooltip({  
	                   onShow: function(){  
	                       $(this).tooltip('tip').css({   
	                           width:'150'/* ,          
	                           boxShadow: '1px 1px 3px #292929'      */                     
	                       });  
	                   }  
	               });  
			}
		});
		//初始化月份控件
		var month_datas = new Array();
		for (var i = 0; i < 12; i++) {
			var month_data = new Object();
			month_data.month = i < 9 ? "0" + (i + 1) : i + 1;
			month_data.name = i + 1 + "月";
			month_datas[i] = month_data;
		}
		var defMonth = new Date().getMonth();
        if(defMonth==0){
            defMonth=1;
        }
		defMonth = (defMonth < 10 ? "0" + defMonth : defMonth);
		//初始化年份
		initYearCombobox("beginDate_yy");
		initYearCombobox("endDate_yy");
		$('#beginDate_mm').combobox({
			data : month_datas,
			valueField : 'month',
			textField : 'name',
			width : 60,
			value : defMonth,
			editable : false
		});
		$('#endDate_mm').combobox({
			data : month_datas,
			valueField : 'month',
			textField : 'name',
			width : 60,
			value : defMonth,
			editable : false
		});
		
		//下拉选单
		$('#orgLevel').combobox({
			url: "<c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
		    valueField:'id',    
		    textField:'field2', 
		    queryParams:{
		    	"attributeNo": "hospital"
			},
			editable:false
		}).combobox("initClear"); 

		//下拉
		$('#hospitalCode').combogrid({
			idField : 'code',
			textField : 'fullName',
			url : "<c:out value='${pageContext.request.contextPath }'/>/set/hospital/listByCounty.htmlx",
			pagination : false,
			queryParams : {
				province : zoneCodes[0],
				city : zoneCodes[1],
				county : zoneCodes[2],
				"query['t#isDisabled_I_EQ']" : 0
			},
			columns : [ [ {
				field : 'code',
				title : '机构编码',
				width : 150
			}, {
				field : 'fullName',
				title : '机构名称',
				width : 200
			} ] ],
			width : 160,
			panelWidth : 360,
			delay : 800,
			prompt : "名称模糊搜索",
			keyHandler : {
				enter : function(e) {
					$('#hospitalCode').combogrid('grid').datagrid("reload", {
						province : zoneCodes[0],
						city : zoneCodes[1],
						county : zoneCodes[2],
						"query['t#isDisabled_I_EQ']" : 0,
						"query['t#fullName_S_LK']" : $('#hospitalCode').combogrid("getText")
					});
					$('#hospitalCode').combogrid("setValue", $('#hospitalCode').combogrid("getText"));
				}
			}
		}).combobox("initClear");

		$('#hospitalCode').combogrid('grid').datagrid("reload", {
			province : zoneCodes[0],
			city : zoneCodes[1],
			county : zoneCodes[2],
			"query['t#isDisabled_I_EQ']" : 0,
			"query['t#fullName_S_LK']" : $('#hospitalCode').combogrid("getText")
		});

		//下拉选单
		$('#combox1').combobox({
			url: "<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvlone.htmlx",
		    valueField:'id',    
		    textField:'name', 
		    //value:zoneCodes[0],
		    editable:false,
		    onSelect:function(a,b){
		    	$('#combox2').combobox({
		    		url: "<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx",
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
			combox_data.url="<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx";
			combox_data.queryParams={parentId:zoneCodes[1]}
		//	combox_data.value=zoneCodes[2];
		}
		$('#combox3').combobox(combox_data).combobox("initClear");

		combox_data.onSelect = function(a,b){
	    	$('#combox3').combobox({
	    		url: "<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx",
	    		queryParams:{
	    			parentId:$('#combox2').combobox('getValue')
	    		}
	    	});
	    };
		if (zoneCodes[0]) {
			combox_data.url="<c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx";
			combox_data.queryParams={parentId:zoneCodes[0]}
			//combox_data.value=zoneCodes[1];
		}
		$('#combox2').combobox(combox_data);
		if (zoneCodes[0]) {
			$("input[name='combox1']").parent().hide();
		}
		if (zoneCodes[1]) {
			$("input[name='combox2']").parent().hide();
		} 
		//
	});
	//搜索
	function dosearch() {
		var provinceCode = $("#combox1").combobox("getValue");
		var cityCode = $("#combox2").combobox("getValue");
		var countyCode = $("#combox3").combobox("getValue");
		var hospitalCode = $("#hospitalCode").combobox("getValue");
		var orgLevel=$("#orgLevel").combobox("getValue");
		var query = {};
		for ( var k in window.queryParams) {
			query[k] = window.queryParams[k];
		}
		query["queryType"] = window.nextQueryType;

		if (provinceCode.length != 0) {
			window.nextQueryType = 2;
		}

		if (cityCode.length != 0) {
			window.nextQueryType = 3;
		}

		if (countyCode.length != 0) {
			window.nextQueryType = 4;
		}

		if ($("#hospitalCode").combobox("getValue").length != 0) {
			window.nextQueryType = 5;
		}
		var monthBegin = $("#beginDate_yy").combobox("getValue") + "-" + $("#beginDate_mm").combobox("getValue");
		var monthEnd = $("#endDate_yy").combobox("getValue") + "-" + $("#endDate_mm").combobox("getValue");
		window.queryParams = {
			queryType : window.nextQueryType - 1
		};
		window.queryParams = {
			"query['c#hospitalCode_S_EQ']" : hospitalCode,
			"query['provinceCode_S_EQ']" : provinceCode,
			"query['cityCode_S_EQ']" : cityCode,
			"query['c#countyCode_S_EQ']" : countyCode,
			"query['a#month_S_GE']" : monthBegin,
			"query['a#month_S_LE']" : monthEnd,
			"query['c#orgLevel_S_EQ']" : orgLevel,
			queryType : window.nextQueryType - 1
		};
		$('#dg').datagrid({
			columns : [ getColumns(window.nextQueryType - 1) ],
			queryParams : window.queryParams
		});

	}

	function addOpen(row) {
		//console.log(row);
		var query = {};
		for ( var k in window.queryParams) {
			query[k] = window.queryParams[k];
			//console.log(query[k]);
		}
		var title = "";
		query["queryType"] = window.nextQueryType;
		var url = "/supervise/hisOutDrugAnalysis/index.htmlx?queryType=" + window.nextQueryType + "&jsonStr=";
		if (window.nextQueryType == 1) {
			query["query['provinceCode_S_EQ']"] = row.PROVINCECODE;
			console.log(query);
			url += encodeURIComponent(JSON.stringify(query));
			title = "每出院人次药品费用分析(省)";
		} else if (window.nextQueryType == 2) {
			query["query['cityCode_S_EQ']"] = row.CITYCODE;
			console.log(query);
			url += encodeURIComponent(JSON.stringify(query));
			title = "每出院人次药品费用分析(市)";
		} else if (window.nextQueryType == 3) {
			query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
			console.log(query);
			url += encodeURIComponent(JSON.stringify(query));
			title = "每出院人次药品费用分析(区/县)";
		} else if (window.nextQueryType == 4) {
			query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
			query["query['c#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
			query["query['zoneCode_S_RLK']"] = undefined;
			url += encodeURIComponent(JSON.stringify(query));
			title = "每出院人次药品费用分析(医院)";
		} else if (window.nextQueryType == 5) {
			query["query['provinceCode_S_EQ']"] = undefined;
			query["query['cityCode_S_EQ']"] = undefined;
			query["query['c#countyCode_S_EQ']"] = undefined;
			query["query['c#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
			query["query['departCode_S_EQ']"] = row.DEPARTCODE;
			url += encodeURIComponent(JSON.stringify(query));
			title = "每出院人次药品费用分析(科室)";
		} else if (window.nextQueryType == 6) {
			query["query['provinceCode_S_EQ']"] = undefined;
			query["query['cityCode_S_EQ']"] = undefined;
			query["query['c#countyCode_S_EQ']"] = undefined;
			query["query['c#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
			query["query['departCode_S_EQ']"] = row.DEPARTCODE;
			query["query['doctorCode_S_EQ']"] = row.DOCTORCODE;
			//console.log(query);
			url += encodeURIComponent(JSON.stringify(query));
			title = "每出院人次药品费用分析(医生)";
		} else {
			
			query = {};
			query["query['a#hospitalCode_S_EQ']"] = window.queryParams["query['c#hospitalCode_S_EQ']"];
			query["query['departCode_S_EQ']"] = row.DEPARTCODE;
			query["query['doctorCode_S_EQ']"] = row.DOCTORCODE;
			//console.log(query['month_S_GE']);
			//console.log(query['month_S_LE']);
			console.log(query);
			 query["query['cdate_D_GE']"] = (window.queryParams["query['a#month_S_GE']"] ? (window.queryParams["query['a#month_S_GE']"] + "-01") : "");
			query["query['cdate_D_LE']"] = (window.queryParams["query['a#month_S_LE']"] ? (window.queryParams["query['a#month_S_LE']"] + "-31") : "");
			//console.log(query);
			url = "/supervise/hisRecipe/index.htmlx?queryType=" + window.nextQueryType + "&jsonStr=" + encodeURIComponent(JSON.stringify(query));
			title = "每出院人次药品费用分析(病人)";
		}
        top.addTab(title, url,null,true);
	}
	function getColumns(queryType) {
		var columns = [ {
			field : 'DRUGSUM',
			title : '出院病人药品总费用',
			width : 120,
			align : 'center'
		}, {
			field : 'OUTNUM',
			title : '出院病人人次',
			width : 120,
			align : 'center'
		}, {
			field : 'AVGOUTDRUGSUM',
			title : '<span title="出院病人药品总费用  / 出院病人人次" class="tip">每出院人次药品费用</span>',
			width : 120,
			align : 'center'
		} ];

		var first = {
			field : 'ZONENAME',
			title : '区域',
			width : 120,
			align : 'center',
			formatter : function(value, row, index) {
				return window.zoneName;
			}
		};
		if (queryType == 1) {
			first = {
				field : 'PROVINCENAME',
				title : '省',
				width : 100,
				align : 'center'
			};
		} else if (queryType == 2) {
			first = {
				field : 'CITYNAME',
				title : '市',
				width : 100,
				align : 'center'
			};
		} else if (queryType == 3) {
			first = {
				field : 'COUNTYNAME',
				title : '县/区',
				width : 100,
				align : 'center'
			};
		} else if (queryType == 4) {
			first = {
				field : 'HOSPITALNAME',
				title : '医院',
				width : 100,
				align : 'center'
			};
		} else if (queryType == 5) {
			first = {
				field : 'DEPARTNAME',
				title : '科室',
				width : 100,
				align : 'center'
			};
			var second = {
					field : 'HOSPITALNAME',
					title : '医院',
					width : 100,
					align : 'center'
				};
		} else if (queryType == 6) {
			first = {
				field : 'DOCTORNAME',
				title : '医生',
				width : 100,
				align : 'center'
			};
			var second = {
					field : 'HOSPITALNAME',
					title : '医院',
					width : 100,
					align : 'center'
				};
			var third = {
					field : 'DEPARTNAME',
					title : '科室',
					width : 100,
					align : 'center'
				};
		}
		columns.splice(0, 0, first);
		
		if(third!=undefined){
			columns.splice(0, 0, third);
		}
		if(second!=undefined){
			columns.splice(0, 0, second);
		}
		return columns;
	}
	function showChart() {
		var nextQueryType = (window.queryParams["queryType"]);
		top.$.modalDialog.data = [ $('#dg').datagrid("getRows"), nextQueryType,window.zoneName ];
		top.$.modalDialog({
			title : "查询",
			width : 800,
			height : 460,
			href : "<c:out value='${pageContext.request.contextPath }'/>/supervise/hisOutDrugAnalysis/chart.htmlx"
		});
	}
	
	function exportexcel(){
		var url = "<c:out value='${pageContext.request.contextPath }'/>/supervise/hisOutDrugAnalysis/export.htmlx?";
		for (var k in window.queryParams) {
			url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(window.queryParams[k]);
		}
		window.open(url);
	}
</script>