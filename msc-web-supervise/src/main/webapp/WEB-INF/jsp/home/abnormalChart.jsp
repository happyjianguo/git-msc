<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="abnormalChart" style="width: 100%;height:250px;margin-top:10px;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	
	var legend_abnormalChart = ['订单数'];
	var myChart_abnormalChart = echarts.init(document.getElementById('abnormalChart'));
	myChart_abnormalChart.setOption({
		legend: {
	        data:legend_abnormalChart
	    },
	    tooltip: {},
	    toolbox: {
	        feature: {
	            //dataView: {show: true, readOnly: false},
	            magicType: {show: true, type: ['line', 'bar']},
	            //restore: {show: true},
	            //saveAsImage: {show: true}
	        }
	    },
	    xAxis: {
	        data: []
	    },
	    yAxis: {},
	    series: [{
	        name: legend_abnormalChart[0],
	        type: 'bar',
	        data: []
	    }]
	});
	myChart_abnormalChart.showLoading();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/home/contractChart.htmlx",
		type:"post",
		dataType:"json",
		success:function(data){
			var arr_x = new Array();
			var arr_y1 = new Array();
			//var arr_y2 = new Array();
			if(data.success){
				$(data.data).each(function(index,item){
					arr_x.push(item.MONTH);
					arr_y1.push(item.COUNT);
					//arr_y2.push(item.ORDERNUM);
				});
			}
			myChart_abnormalChart.hideLoading();
			myChart_abnormalChart.setOption({
				xAxis: {
					data: arr_x
			    },
			    series: [{
			       data: arr_y1
			    }]
			});
		}
	
	});
	
</script>
