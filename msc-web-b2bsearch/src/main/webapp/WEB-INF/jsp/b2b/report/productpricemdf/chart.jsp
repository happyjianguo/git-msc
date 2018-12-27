<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="chart" style="width: 100%;height:100%;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	var productCode =<c:out value='${productCode}'/>;//alert(22);
	
	var myChart = echarts.init(document.getElementById('chart'));
	myChart.setOption({
	    title: {
	        text: '药品价格变动情况',
	        x: "center"
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
	        name: '价格',
	        type: 'line',
	        data: []
	    }]
	});
	myChart.showLoading();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productPriceMdf/chart.htmlx",
		data:{
			"query[t#productCode_S_LK]":productCode
		},
		type:"post",
		dataType:"json",
		success:function(data){
			console.log(data);
			var arr_x = new Array();
			var arr_y = new Array();
			if(data.success){
				$(data.data).each(function(index,item){
					arr_x.push($.format.date(item.createDate,"yyyy-MM-dd"));
					arr_y.push(item.finalPrice);
				});
			}
			myChart.hideLoading();
			myChart.setOption({
				xAxis: {
					data: arr_x
			    },
			    series: [{
			       // 根据名字对应到相应的系列
			       name: '交易金额',
			       data: arr_y
			    }]
			});
		}
	
	});
	
</script>
