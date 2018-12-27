<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="tradeChart" style="width: 100%;height:250px;margin-top:10px;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	
var year_tradeChart =<c:out value='${year}'/>;
var legend_tradeChart = ['采购金额'];
var myChart_tradeChart = echarts.init(document.getElementById('tradeChart'));
myChart_tradeChart.setOption({
	legend: {
        data:legend_tradeChart
    },
    tooltip: {},
    toolbox: {
        feature: {
            
            magicType: {show: true, type: ['line', 'bar']},

        }
    },
    xAxis: {
        data: []
    },
    yAxis: {},
    series: [{
        name: legend_tradeChart[0],
        type: 'bar',
        data: []
    }]
});
myChart_tradeChart.showLoading();
$.ajax({
	url:" <c:out value='${pageContext.request.contextPath }'/>/report/trade/chart.htmlx",
	data:{
		year:year_tradeChart
	},
	type:"post",
	dataType:"json",
	success:function(data){
		var arr_x = new Array();
		var arr_y = new Array();
		if(data.success){
			$(data.data).each(function(index,item){
				arr_x.push(item.MONTH);
				arr_y.push(item.ORDERSUM);
			});
		}
		myChart_tradeChart.hideLoading();
		myChart_tradeChart.setOption({
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
