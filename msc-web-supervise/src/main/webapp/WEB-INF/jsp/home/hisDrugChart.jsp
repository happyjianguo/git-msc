<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="chart" style="width: 1600px;height:250px;margin-top:20px;"></div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	
	var legend = ['药品总收入(万)','医院总收入(万)'];
	var myChart = echarts.init(document.getElementById('chart'));
	 myChart.setOption({
		 title : {
		        text: "收入(万)",
		        x:'40px'
		    }, 
	    legend: {
	        data:legend
	    },
	    grid:{x:120,x2:80,y:35,y2:60},
	    tooltip: {
	        trigger: 'axis',
	        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
	            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
	        }
	   	},
	    toolbox: {
	        feature: {
	            //dataView: {show: true, readOnly: false},
	            //magicType: {show: true, type: ['line', 'bar']},
	            //restore: {show: true},
	            //saveAsImage: {show: true}
	        }
	    },
		toolbox: {
	        show : true,
	        feature : {
	            mark : {show: true},
	            dataView : {show: true, readOnly: false},
	            magicType : {show: true, type: ['line', 'bar']},
	            restore : {show: true},
	            saveAsImage : {show: true}
	        }
	    },
	    xAxis: {
	        data: []
	    },
	    yAxis: {},
	    series: [{
	        name: '药品总收入(万)',
	        type: 'bar',
	        /* barWidth:((780/2/axis.length)<14)?14:(750/2/axis.length), */
	        itemStyle:{
            	normal: {
                }
            },
	        data: []
	    },
	    {
	        name: '医院总收入(万)',
	        type: 'bar',
	        /* barWidth:((780/2/axis.length)<14)?14:(750/2/axis.length), */
	        itemStyle:{
            	normal: {
                }
            },
	        data: []
	    }]
	}); 
	myChart.showLoading();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/hisDrugIncomeRatio/show.htmlx",
		type:"post",
		dataType:"json",
		success:function(data){
			var arr_x = new Array();
			var arr_y1 = new Array();
			var arr_y2 = new Array();
			//var arr_y2 = new Array();
			console.log(data);
			if(data!=null){
				$(data.rows).each(function(index,item){
					arr_x.push(item.HOSPITALNAME);
					arr_y1.push(parseFloat(item.DRUGSUM/10000).toFixed(2));
					arr_y2.push(parseFloat(item.SUM/10000).toFixed(2));
					//arr_y2.push(item.ORDERNUM);
				});
			}
			myChart.hideLoading();
			myChart.setOption({
				xAxis: {
					axisLabel:{
		    			rotate:16,
		    			interval:0
		            },
					data: arr_x
			    },
			    yAxis:[{
	        	   	type:'value',
	                position: 'top'
			    }],
			    series: [{
			    	barWidth:((595/arr_x.length)<14)?14:(587.5/arr_x.length),
			       data: arr_y1
			    },{
			    	barWidth:((595/arr_x.length)<14)?14:(587.5/arr_x.length),
			       data: arr_y2
			    }]
			});
		}
	
	});
	
</script>
