<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="chart" style="width: 100%;height:100%;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	var month = '${month}';
	
	var legend = ['平均订单审核时间（小时）','平均配送时间（小时）'];
	var myChart = echarts.init(document.getElementById('chart'));
	myChart.setOption({
		title:{
			text:"配送效率分析（供应商）",
			subtext: month,  
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
        	} 
	    },
	    toolbox: {
	        feature: {
	            dataView: {show: true, readOnly: false},
	            magicType: {show: true, type: ['line', 'bar']},
	            restore: {show: true},
	            saveAsImage: {show: true}
	        }
	    },
	    grid: {
	        containLabel: true
	    },
	    xAxis: {
	        data: []
	    },
	    yAxis: {},
	    series: [{
	        name: legend[0],
	        type: 'bar',
	        stack: '时间',
	        data: []
	    },{
	        name: legend[1],
	        type: 'bar',
	        stack: '时间',
	        data: []
	    }]
	});
	myChart.showLoading();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/vendorC1/chart2.htmlx",
		data:{
			"month":month
		},
		type:"post",
		dataType:"json",
		success:function(data){
			var arr_x = new Array();
			var arr_y1 = new Array();
			var arr_y2 = new Array();
			if(data.success){
				$(data.data).each(function(index,item){
					arr_x.push(item.vendorName);
					arr_y1.push(item.reviewHour);
					arr_y2.push(item.deliveryHour);
				});
				
			}
			myChart.hideLoading();
			myChart.setOption({
				 xAxis: {
				        type: 'value' 
				    },
				yAxis: {
					type: 'category',  
					data: arr_x
			    },
			    series: [{
			       data: arr_y1
			    },{
				       data: arr_y2
				}]
			});
		}
	
	});
	
</script>
