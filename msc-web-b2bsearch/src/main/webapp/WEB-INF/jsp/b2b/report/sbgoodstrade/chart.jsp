<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="chart" style="width: 100%;height:100%;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	var year =<c:out value='${year}'/>;
	var myChart = echarts.init(document.getElementById('chart'));
	myChart.setOption({
	    legend: {
	        data:['社保交易金额','非社保交易金额']
	    },
	    tooltip: {},
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
	        name: '社保交易金额',
	        type: 'bar',
	        data: []
	    },{
	        name: '非社保交易金额',
	        type: 'bar',
	        data: []
	    }]
	});
	myChart.showLoading();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/sbgoodstrade/chart.htmlx",
		data:{
			year:year
		},
		type:"post",
		dataType:"json",
		success:function(data){
			var arr_x = new Array();
			var arr_y1 = new Array();
			var arr_y2 = new Array();
			if(data.success){
				$(data.data).each(function(index,item){
					arr_x.push(item.YM);
					arr_y1.push(item.SBSUM);
					arr_y2.push(item.FSBSUM);
				});
			}
			myChart.hideLoading();
			myChart.setOption({
				xAxis: {
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
