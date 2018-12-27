<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div style="width:100%;height:100%;" id="chart">
</div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
$(function() {
	function initChart(data, showType,orderType,zoneName) {
		
		$("#chart").empty();
		var axis = [];
		var newData=[];
		$(data).each(function(i) {
			var name = zoneName;
			var value = data[i]['AVGOUTDRUGSUM'];
			if (showType == 1) {
				name = data[i]['PROVINCENAME'];
			} else if (showType == 2) {
				name = data[i]['CITYNAME'];
			} else if (showType == 3) {
				name = data[i]['COUNTYNAME'];
			} else if (showType == 4) {
				name = data[i]['HOSPITALNAME'];
			} else if (showType == 5) {
				name = data[i]['DEPARTNAME'];
			} else if (showType == 6) {
				name = data[i]['DOCTORNAME'];
			} 
			/* if (orderType == 1) {
				value = data[i]['NUM'];
			} */
			newData.push({name:name,value:value});
			axis.push(newData[i].name);
		});
		var myChart = echarts.init(document.getElementById('chart'));
		var option = {
			    title : {
			        text: "住院药品使用分析",
			        x:'center',
			        y:'5'
			    },
			     tooltip: {
			        trigger: 'axis',
			        show: true,
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			        }
			   	},
			   	toolbox: {
			        show : true,
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, readOnly: true},
			            magicType : {show: true, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			   	grid:{x:80,x2:80,y:35,y2:180},
			    /* legend: {
                    data:['销量']
                }, */
			    xAxis:[{
		            type : 'category',
		            axisTick : {show: true},
		            data:axis,
		    		axisLabel:{
		    			rotate:60,
		    			interval:0
		            }
		        }],
			    yAxis:[{
	        	   	type:'value',
	                position: 'top'
			    }],
			    series: [{
			        name: '每出院人次药品费用',
			        type: 'bar',
			        //barWidth:((780/2/axis.length)<14)?14:(750/2/axis.length),
			        barWidth:70,
	                tooltip:{
	                	show:true,
	                    trigger: 'item'
	                    /* formatter: function(a) {
				        	return (a.value);
	                    } */
		           	},
			       	data: newData
			       	/* itemStyle:{
			       		normal:{title:{show:true}}
			       	} */
			    }]
			    
			};
		/* console.log(JSON.stringify(option)); */
		myChart.setOption(option);
	}
	initChart(top.$.modalDialog.data[0],top.$.modalDialog.data[1],top.$.modalDialog.data[2],top.$.modalDialog.data[3]);
});
</script>
