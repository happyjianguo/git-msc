<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="GPO供应商配送效率排名" />
<html>
<body class="easyui-layout">
<div style="padding:10px 50px;width:90%;min-width:800px;text-align:center;">
<input id="year"/>
<input id="month"/>
</div>
<div style="width:100%;height:100%;overflow-y: auto;">
<div id="chart" style="width:100%;min-width:900px;overflow-y: auto;">
</div>
</div>
</body>
</html>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	
	$(function() {
		function openChartTimely(vendorCode) {
			top.$.modalDialog.openner = $(document);
			top.$.modalDialog({
				title : "添加",
				width : 700,
				height : 400,
				href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/gposervice/chartTimely.htmlx?vendorCode="+vendorCode
			});
		}
		function doSearch() {
			var year = $('#year').combobox('getValue');
			var month = $('#month').combobox('getValue');
			$("#chart").empty();
			$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/gposervice/chartRank.htmlx",
				type:"post",
				dataType:"json",
				data:{year:year,month:month},
				success:function(data) {
					if (data.length == 0) return;
					var axis = [];
					$(data).each(function(i) {
						data[i].name = this["VENDORNAME"];
						data[i].value = this["VALUE"];
						data[i].vendorCode = this["VENDORCODE"];
						axis.push(this["VENDORNAME"]);
					});
					var height = axis.length*35+100;
					if (axis.length > 0) {
						$("#chart").height(height);
					}
					var myChart = echarts.init(document.getElementById('chart'));
					myChart.on("CLICK", function(param) {
						openChartTimely(param.data.vendorCode);
					})
					myChart.setOption({
					    title : {
					        text: '供应商平均配送时长对比（小时）',
					        x:'center',
					        y:'0'
					    },
					    tooltip: {
					        trigger: 'axis',
					        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
					            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
					        }
					   	},
					   	grid:{
					   		x:300,
					   		x2:300
					   	},
					    yAxis:[{
				            type : 'category',
				            axisTick : {show: false},
				            data:axis,
				    		axisLabel:{
				    			interval:0
				            }
				        }],
					    xAxis:[{
			        	   	type:'value',
			                position: 'top'
					    }],
					    series: [{
					        name: '平均配送时长',
					        type: 'bar',
					        barWidth:20,
				            label: {
				                normal: {
				                    show: true,
				                    position: 'right'
				                }
				            },
					       	data: data
					    }]
					    
					});
				}
			});
		}
		$("#search_btn").click(doSearch);
		initYearCombobox("year");
		//年份选中事件
		$('#year').combobox({onSelect:doSearch});
		//初始化月份控件
		var month_datas = new Array();
		month_datas[0] = {month:"",name:"全部"};
		for(var i=0; i<12; i++){
			var month_data = new Object();
			month_data.month = i<9?"0"+(i+1):i+1;
			month_data.name = i+1+"月";
			month_datas[i+1] = month_data;
		}
		$('#month').combobox({
			data: month_datas,
			valueField: 'month',
			textField: 'name',
			width:60,
			editable: false,
			onSelect:doSearch
		});
		doSearch();
	})
	
</script>
