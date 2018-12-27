<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="GPO采购金额对比" />

<html>
<body class="easyui-layout">
<div style="padding:10px 50px;margin:0 auto;width:290px;">
<input id="searchType"/>
<input id="year"/>
<input id="month"/>
</div>
<div style="width:100%;overflow-y: auto;height:100%;">
<div id="chart" style="width:100%;">
</div>
</div>
</body>
</html>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	
	$(function() {
		function doSearch() {
			var year = $('#year').combobox('getValue');
			var month = $('#month').combobox('getValue');
			var searchType = $('#searchType').combobox('getValue');
			var url = " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/orderCentralized/hospitalChartSum.htmlx";
			var title = "医院GPO采购集中度（万元）";
			if (searchType == 1) {
				url = " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/orderCentralized/regionChartSum.htmlx";
				title = "区县GPO采购集中度（万元）";
			}
			$("#chart").empty();
			$.ajax({
				url:url,
				type:"post",
				dataType:"json",
				data:{year:year,month:month},
				success:function(data) {
					if (data.length == 0) return;
					var axis = [];
					$(data).each(function(i) {
						data[i].name=data[i]['NAME'];
						data[i].value=data[i]['VALUE'];
						axis.push(data[i].name);
					});
					var height = axis.length*30+100;
					if (axis.length > 0) {
						$("#chart").height(height);
					}
					var myChart = echarts.init(document.getElementById('chart'));
					myChart.setOption({
					    title : {
					        text: title,
					        x:'center',
					        y:'0'
					    },
					    tooltip: {
					        trigger: 'axis',
					        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
					            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
					        }
					   	},
					   	grid:{x:300,x2:300},
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
					        name: 'GPO订单销售金额',
					        type: 'bar',
					        barWidth:14,
				            label: {
				                normal: {
				                    show: true,
				                    position: 'right'
				                }
				            },
			                tooltip:{
			                	show:false,
			                    trigger: 'item',
			                    formatter: function(a) {
						        	return 'GPO药品采购金额'+ a.value + "万元";
			                    }
				           	},
					       	data: data
					    }]
					    
					});
				}
			});
		}
		$("#search_btn").click(doSearch);
		//初始化报表类型
		$('#searchType').combobox({
			data: [{name:"医院",value:0},{name:"区县",value:1}],
			valueField: 'value',
			textField: 'name',
			width:60,
			editable: false,
			value:0,
			onSelect:doSearch
		});
		//初始化年份
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
