<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="GPO采购集中度" />

<html>
<body class="easyui-layout">
<div style="padding:10px 50px;margin:0 auto;width:220px;">
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
			var url = " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/orderCentralized/hospitalChartPercent1.htmlx";
			var title = "医院GPO采购集中度";
			if (searchType == 1) {
				url = " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/orderCentralized/regionChartPercent1.htmlx";
				title = "区县GPO采购集中度";
			}
			$("#chart").empty();
			$.ajax({
				url:url,
				type:"post",
				dataType:"json",
				data:{year:year,month:month,},
				success:function(data) {
					if (data.length == 0) return;
					//定义竖向坐标
					var yAxis = [],avg = 0,count=0;
					//统计平均值
					$(data).each(function() {
						if (this["VALUE"] > 0) {
							avg += this["VALUE"];
							count++;
						}
					});
					//计算平均值
					if (count > 0) {
						avg = parseInt(avg/count);
						if (avg < 0) avg = 0;
						if(avg==100) {
							avg=50;
						}
					}
					//转换key
					$(data).each(function() {
						this.name=this["NAME"];
						this.value=this["VALUE"]-avg;
						yAxis.push(this.name)
					});
					//设置高度
					var height = data.length*35+100;
					if ($("#chart").height() < height) {
						$("#chart").height(height);
					}
					var myChart = echarts.init(document.getElementById('chart'));
					myChart.setOption({
					    title : {text: title,x:'center',y:'0'},
					    tooltip: {trigger:'axis',axisPointer:{type : 'shadow'}},
					   	grid:{x:300,x2:300},
					    yAxis:[{
				            type : 'category',
				            axisTick : {show: false},
				            data:yAxis,
				    		axisLabel:{
				    			interval:0
				            }
				        }],
					    xAxis:[{
			        	   	type:'value',
			                position: 'top',
			                axisLabel:{
						        formatter:function(value) {
						        	return ((value + avg)/100).toFixed(2);
						        }
			                }
					    }],
					    series: [{
					        type: 'bar',
			                tooltip : {
			                    trigger: 'item',
			                    formatter: function(a) {
						        	return a.name+'<br>GPO采购集中度：'+((a.value + avg)/100).toFixed(2);
			                    }
			                },
				            itemStyle:{
				            	normal: {
				            		color:function(color) {
				            			return color.data.value > 0?"#9E0F05":"#0C7708";
				            		}
				                }
				            },
					       	data: data
					    }]
					    
					});
				}
			});
		}
		$("#search_btn").click(doSearch);

		$('#searchType').combobox({
			data: [{name:"医院",value:0},{name:"区县",value:1}],
			valueField: 'value',
			textField: 'name',
			width:60,
			editable: false,
			value:0,
			onSelect:function(a,b){
		    	doSearch();
		    }
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
