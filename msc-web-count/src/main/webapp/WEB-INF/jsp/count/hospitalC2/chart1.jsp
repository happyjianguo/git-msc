<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="chart" style="width: 100%;height:100%;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	var hospitalCode = '${hospitalCode}';
	var hospitalName = '${hospitalName}';
	
	var legend = ['品种占比'];
	var myChart = echarts.init(document.getElementById('chart'));
	myChart.setOption({
		title:{
			text:"采购品种占比分析",
			subtext: hospitalName,  
			x: 'center'
		},
	    legend: {
	        data:legend,
	        show: true,
	        y:'bottom'
	    },
	    tooltip: {
	    	trigger: 'axis', //'item' | 'axis'  
	    	axisPointer: {
           		type: 'shadow'
        	},
        	formatter: '{b}:\n{c}%'  
	    },
	    toolbox: {
	        feature: {
	            dataView: {show: true, readOnly: false},
	            magicType: {show: true, type: ['line', 'bar']},
	            restore: {show: true},
	            saveAsImage: {show: true}
	        }
	    },
	    xAxis: {
	        data: []
	    },
	    yAxis: {},
	    series: [{
	        name: legend[0],
	        type: 'bar',
	        data: []
	    }]
	});
	myChart.showLoading();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/hospitalC2/chart1.htmlx",
		data:{
			"hospitalCode":hospitalCode
		},
		type:"post",
		dataType:"json",
		success:function(data){
			var arr_x = new Array();
			var arr_y1 = new Array();
			if(data.success){console.log(data);
				$(data.data).each(function(index,item){
					arr_x.push(item.month);
					arr_y1.push(item.segmentStr);
				});
			}
			myChart.hideLoading();
			myChart.setOption({
				 xAxis: {
					 data: arr_x
				    },
				yAxis: { 
					axisLabel: {   
	                  formatter: '{value} %'  
	                }
			    },
			    series: [{
			       data: arr_y1
			    }]
			});
		}
	
	});
	
</script>
