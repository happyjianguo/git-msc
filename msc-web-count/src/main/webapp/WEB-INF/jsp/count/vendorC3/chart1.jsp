<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="chart" style="width: 100%;height:100%;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	var vendorCode = '${vendorCode}';
	var vendorName = '${vendorName}';
	
	var legend = ['品种数','交易金额'];
	var myChart = echarts.init(document.getElementById('chart'));
	myChart.setOption({
		title:{
			text:"市场占有率分析（时间）",
			subtext: vendorName,  
			x: 'center'
		},
	    legend: [{
	        data:legend,
	        show: true,
	        y:'bottom'
	    },{
	        data:legend,
	        show: true,
	        y:'bottom'
	    }],
	    tooltip: {
	    	trigger: 'axis', //'item' | 'axis'  
	    	axisPointer: {
           		type: 'shadow'
        	} 
	    },
	    toolbox: {
	        feature: {
	        	 mark: {show: false},  
	            dataView: {show: true, readOnly: false},
	            //magicType: {show: true, type: ['line', 'bar']},
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
	    },{
	        name: legend[1],
	        type: 'line',
	        data: []
	    }]
	});
	myChart.showLoading();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/vendorC3/chart1.htmlx",
		data:{
			"vendorCode":vendorCode
		},
		type:"post",
		dataType:"json",
		success:function(data){
			var arr_x = new Array();
			var arr_y = new Array();
			var arr_y1 = new Array();
			if(data.success){
				$(data.data).each(function(index,item){
					arr_x.push(item.month);
					arr_y.push(item.contractPurchaseSpecCount);
					arr_y1.push(item.purchaseSum);
					
				});
			}
			myChart.hideLoading();
			myChart.setOption({
				 xAxis: [{
					 type : 'category',
					 data: arr_x
				    }],
				yAxis: [ {
					  type: 'value',
					  name: legend[0],
                      position: 'left'
				},{
					  type: 'value',
                      name: legend[1],
                      position: 'right'
           }],
			    series: [{
			       data: arr_y
			    },{
			    	yAxisIndex: 1,
			    	
			       data: arr_y1
			    }]
			});
		}
	
	});
	
</script>
