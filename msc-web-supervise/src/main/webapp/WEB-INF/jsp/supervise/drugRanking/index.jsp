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
#search{
display:none;
}
</style>
<html>
<body class="easyui-layout">

	<div data-options="region:'center',title:''">
	<div id="toolbar" class="search-bar" >
				<a href="#" id="search" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="openSearch()">筛选</a>
				<shiro:hasPermission name="supervise:drugRanking:export">
        			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" onclick="exportexcel()">导出</a>
				</shiro:hasPermission>
        		<!-- <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a> -->
		        <span id='time'></span>
	    	</div>
		<table id="dg"></table>
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
		window.zoneName = "<c:out value='${zoneName}'/>";
		window.queryType = "<c:out value='${queryType}'/>";
		window.jsonStr = decodeURIComponent("<c:out value='${jsonStr}'/>");
		if (jsonStr.length == 0) {
			jsonStr = "{}";
			$("#search").show();
		}
		if(<c:out value="${orgType==1}"/>&&window.queryType==4){
			$("#search").show(); 
		}
		window.queryParams = eval("(" + jsonStr + ")");
		if (jsonStr.length > 2) {
			//查询条件不为空，说明不是首次进入
			window.queryParams["queryType"] = "<c:out value='${queryType}'/>";
		}
		window.nextQueryType = "<c:out value='${queryType+1}'/>";

		//datagrid
		$('#dg').datagrid({
			fitColumns : false,
			striped : true,
			singleSelect:true,
			rownumbers : true,
			border : true,
			height : $(this).height() - 2,
			pagination : true,
			url : "<c:out value='${pageContext.request.contextPath }'/>/supervise/drugRanking/page.htmlx",
			queryParams : window.queryParams,
			pageSize : 20,
			pageNumber : 1,
			columns : [ getColumns(window.queryParams["queryType"]) ],
			toolbar : "#toolbar",
			onDblClickRow : function(index, row) {
				openChild(row);
			},
			onLoadSuccess : function(data) {
				var array = data.rows;
				var begindata = array[0]['begindata'];
				var enddata = array[0]['enddata'];
				var timetile="";
				console.log(begindata+"--"+enddata);
				if(begindata==enddata||enddata==""||enddata==null||enddata==undefined){
					timetile=begindata;
				}else{
					timetile=begindata+" -- "+enddata;
				}
				$("#time").html("查询时间  : "+timetile);
				$('#dg').datagrid('doCellTip', {
					delay : 500
				});
			}
		});

	});
	//弹窗增加
	function openSearch() {
		top.$.modalDialog({
			title : "查询",
			width : 600,
			height : 360,
			href : "<c:out value='${pageContext.request.contextPath }'/>/supervise/drugRanking/search.htmlx",
			onLoad : function() {
			},
			buttons : [ {
				text : '查询',
				iconCls : 'icon-ok',
				handler : function() {
					window.nextQueryType = (window.jsonStr.length <= 2 ? 1 : window.queryParams["queryType"]);
					top.$.modalDialog.openner = $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
					var f = top.$.modalDialog.handler.find("#form1");
					var monthBegin = f.find("#beginDate_yy").combobox("getValue") + "-" + f.find("#beginDate_mm").combobox("getValue");
					var monthEnd = f.find("#endDate_yy").combobox("getValue") + "-" + f.find("#endDate_mm").combobox("getValue");
					try {
						var provinceCode = f.find("#combox1").combobox("getValue");
						if (provinceCode.length != 0) {
							window.nextQueryType = 2;
						}
						var cityCode = f.find("#combox2").combobox("getValue")
						if (cityCode.length != 0) {
							window.nextQueryType = 3;
						}
						var countyCode = f.find("#combox3").combobox("getValue");
						if (countyCode.length != 0) {
							window.nextQueryType = 4;
						}
					} catch (e) {
					}
					if (f.find("#hospitalCode").combobox("getValue").length > 0||f.find("#orgLevel").combobox("getValue").length > 0) {
						window.nextQueryType = 5;
					}
					if(<c:out value="${orgType==1}"/>){
						window.nextQueryType +=1;
					} 
					window.queryParams = {
						  "query['h#hospitalCode_S_EQ']" : f.find("#hospitalCode").combobox("getValue"),
						"query['c#orgLevel_S_EQ']" : f.find("#orgLevel").combobox("getValue"),
						"query['h#type_L_EQ']" : f.find("#sumType").combobox("getValue"),
						"query['h#month_S_GE']" : monthBegin,
						"query['h#month_S_LE']" : monthEnd,
						 "query['c#provinceCode_S_EQ']" : provinceCode,
						"query['c#cityCode_S_EQ']" : cityCode, 
						 "query['c#countyCode_S_EQ']" : countyCode,  
						queryType : window.nextQueryType - 1,
						orderType : f.find("#orderType").combobox("getValue") 
					};
					$('#dg').datagrid({
						columns : [ getColumns(window.nextQueryType - 1) ],
						queryParams : window.queryParams
					});

					top.$.modalDialog.handler.dialog('destroy');
					top.$.modalDialog.handler = undefined;
				}
			}, {
				text : '取消',
				iconCls : 'icon-cancel',
				handler : function() {
					top.$.modalDialog.handler.dialog('destroy');
					top.$.modalDialog.handler = undefined;
				}
			} ]
		});
	}
	function openChild(row) {
		var query = {};
		for ( var k in window.queryParams) {
			query[k] = window.queryParams[k];
		}
		
		var title = "";
		query["queryType"] = window.nextQueryType;
		var url = "/supervise/drugRanking/index.htmlx?queryType=" + window.nextQueryType + "&jsonStr=";
		if (window.nextQueryType == 1) {
			query["query['provinceCode_S_EQ']"] = row.PROVINCECODE;
			url += encodeURIComponent(JSON.stringify(query));
			title = "医院药品排名(省)";
		} else if (window.nextQueryType == 2) {
			query["query['c#provinceCode_S_EQ']"] = row.PROVINCECODE;
			query["query['c#cityCode_S_EQ']"] = row.CITYCODE;
			query["query['h#productCode_S_EQ']"] = row.PRODUCTCODE;
			url += encodeURIComponent(JSON.stringify(query));
			title = "医院药品排名(市)";
		} else if (window.nextQueryType == 3) {
			query["query['c#cityCode_S_EQ']"] = row.CITYCODE;
			query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
			query["query['h#productCode_S_EQ']"] = row.PRODUCTCODE;
			url += encodeURIComponent(JSON.stringify(query));
			title = "医院药品排名(区/县)";
		} else if (window.nextQueryType == 4) {
			query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
			query["query['h#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
			query["query['c#provinceCode_S_EQ']"] = undefined;
			query["query['zoneCode_S_RLK']"] = undefined;
			query["query['h#productCode_S_EQ']"] = row.PRODUCTCODE;
			url += encodeURIComponent(JSON.stringify(query));
			title = "医院药品排名(医院)";
		} else if (window.nextQueryType == 5) {
			query["query['c#provinceCode_S_EQ']"] = undefined;
			query["query['c#cityCode_S_EQ']"] = undefined;
			query["query['c#countyCode_S_EQ']"] = undefined;
			query["query['h#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
			query["query['departCode_S_EQ']"] = row.DEPARTCODE;
			query["query['doctorCode_S_EQ']"] = row.DOCTORCODE;
			query["query['h#productCode_S_EQ']"] = row.PRODUCTCODE;
			url += encodeURIComponent(JSON.stringify(query));
			title = "医院药品排名(科室)";
		} else if (window.nextQueryType == 6) {
			query["query['c#provinceCode_S_EQ']"] = undefined;
			query["query['c#cityCode_S_EQ']"] = undefined;
			query["query['c#countyCode_S_EQ']"] = undefined;
			query["query['h#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
			query["query['departCode_S_EQ']"] = row.DEPARTCODE;
			query["query['doctorCode_S_EQ']"] = row.DOCTORCODE;
			query["query['h#productCode_S_EQ']"] = row.PRODUCTCODE;
			url += encodeURIComponent(JSON.stringify(query));
			title = "医院药品排名(医生)";
		} else {
			query = {};
			query["query['a#hospitalCode_S_EQ']"] = window.queryParams["query['h#hospitalCode_S_EQ']"];
			/* query["query['doctorCode_S_EQ']"] = window.queryParams["query['doctorCode_S_EQ']"]; */
			query["query['departCode_S_EQ']"] = row.DEPARTCODE;
			query["query['doctorCode_S_EQ']"] = row.DOCTORCODE;
			query["query['productCode_S_EQ']"] = row.PRODUCTCODE;
			query["sumType"] = window.queryParams["query['h#type_L_EQ']"];
			query["query['cdate_D_GE']"] = (window.queryParams["query['h#month_S_GE']"] ? (window.queryParams["query['h#month_S_GE']"] + "-01") : "");
			query["query['cdate_D_LE']"] = (window.queryParams["query['h#month_S_LE']"] ? (window.queryParams["query['h#month_S_LE']"] + "-31") : "");
			url = "/supervise/clinicRecipe/index.htmlx?queryType=" + window.nextQueryType + "&jsonStr=" + encodeURIComponent(JSON.stringify(query));
			title = "医院药品排名(病人)";
		}
        top.addTab(title, url,null,true);
	}
	function getColumns(queryType) {
		var columns = [ {
			field : 'PRODUCTCODE',
			title : '药品编码',
			width : 75,
			align : 'center'
		}, {
			field : 'PRODUCTNAME',
			title : '药品名称',
			width : 150,
			align : 'center'
		}, {
			field : 'DOSAGEFORMNAME',
			title : '剂型',
			width : 100,
			align : 'center'
		}, {
			field : 'MODEL',
			title : '规格',
			width : 100,
			align : 'center'
		}, {
			field : 'PACKDESC',
			title : '包装规格',
			width : 100,
			align : 'center'
		}, {
			field : 'PRODUCERNAME',
			title : '生产企业',
			width : 100,
			align : 'center'
		} ,{field:'ISGPOPURCHASE',title:'是否GPO药品',width:100,align:'center',
    		formatter: function(value,row,index){
    			if (row.ISGPOPURCHASE ==1){
					return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
				}
			}
		}, { 
			field : 'AUTHORIZENO',
			title : '国药准字(批准文号)',
			width : 150,
			align : 'center'
		}, {
			field : 'ABSDRUGTYPE',
			title : '抗菌药物标志',
			width : 100,
			align : 'center',
				 formatter : function(value, row, index) {
					if (row.ABSDRUGTYPE != ""&&row.ABSDRUGTYPE!=0) {
						return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
					}
				} 
		}, {
			field : 'AUXILIARYTYPE',
			title : '辅助用药标志',
			width : 100,
			align : 'center',
			formatter : function(value, row, index) {
				if (row.AUXILIARYTYPE == 1) {
					return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
				}
			}
		}, {
			field : 'SUM',
			title : '金额',
			width : 100,
			align : 'center'
		},  {
			field : 'NUM',
			title : '使用数量',
			width : 100,
			align : 'center'
		} , {
			field : 'PURCAHSENUM',
			title : '总采购数量',
			width : 100,
			align : 'center'
		}, {
			field : 'GPOPURCAHSENUM',
			title : 'GPO采购数量',
			width : 100,
			align : 'center'
		} ,{
			field : 'NOTGPOPURCAHSENUM',
			title : '非GPO采购数量',
			width : 100,
			align : 'center'
		}, {
			field : 'PURCAHSESUM',
			title : '总采购金额',
			width : 100,
			align : 'center'
		}, {
			field : 'GPOPURCAHSESUM',
			title : 'GPO采购金额',
			width : 100,
			align : 'center'
		} , {
			field : 'NOTGPOPURCAHSESUM',
			title : '非GPO采购金额',
			width : 100,
			align : 'center'
		}];

		var first = {
			field : 'ZONENAME',
			title : '区域',
			width : 75,
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
				title : '区域',
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
	
	function exportexcel(){
		var nextQueryType = (window.jsonStr.length <= 2 ? 0 : window.queryParams["queryType"]);
		var orderType = window.queryParams["orderType"] || 0;
		var url = "<c:out value='${pageContext.request.contextPath }'/>/supervise/drugRanking/export.htmlx?";
		for ( var k in window.queryParams) {
			url += "&" + encodeURIComponent(k) + "=" + encodeURIComponent(window.queryParams[k]);
		}
		window.open(url);
	}

</script>