<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="chart" style="width: 100%;height:100%;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	var name = '<c:out value='${name}'/>';
	var dateS = '<c:out value='${dateS}'/>';
	var dateE = '<c:out value='${dateE}'/>';
	var legend = ['采购总金额','基本药物','非基本药物'];
	var myChart = echarts.init(document.getElementById('chart'));
	myChart.setOption({
	    legend: {
	        data:legend
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
	        name: legend[0],
	        type: 'bar',
	        data: []
	    },{
	        name: legend[1],
	        type: 'bar',
	        data: []
	    },{
	        name: legend[2],
	        type: 'bar',
	        data: []
	    }]
	});
	myChart.showLoading();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltradejbgoods/chart.htmlx",
		data:{
			"name":name,
			"dateS":dateS,
			"dateE":dateE
		},
		type:"post",
		dataType:"json",
		success:function(data){
			var arr_x = new Array();
			var arr_y1 = new Array();
			var arr_y2 = new Array();
			var arr_y3 = new Array();
			if(data.success){
				$(data.data).each(function(index,item){
					arr_x.push(item.YEAR);
					arr_y1.push(item.SUM);
					arr_y2.push(item.JBSUM);
					arr_y3.push(item.FJBSUM);
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
				},{
				       data: arr_y3
				}]
			});
		}
	
	});
	
</script>
