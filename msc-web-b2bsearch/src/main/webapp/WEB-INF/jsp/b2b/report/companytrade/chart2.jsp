<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="chart" style="width: 100%;height:100%;overflow-y: auto;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	var rowsO = top.$.modalDialog.data[0];
	var arr_x = new Array();
	var arr_y = new Array();
	$(rowsO).each(function(index,item){
		arr_x.push(item.NAME);
		arr_y.push(item.ORDERSUM);
	});
	var myChart = echarts.init(document.getElementById('chart'));
	myChart.setOption({
	    title: {
	        text: '企业交易额情况',
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
	   	grid:{x:200},
	    xAxis:[{
    	   	type:'value',
            position: 'top'
	    }],
	    yAxis: {
	    	type : 'category',
            axisTick : {show: false},
            data:arr_x,
    		axisLabel:{
    			interval:0
            }
	    },
	    series: [{
	        name: '交易金额',
	        type: 'bar',
            label: {
                normal: {
                    show: true,
                    position: 'right'
                }
            },
	       	data: arr_y
	    }]
	});
	//myChart.showLoading();
	
</script>
