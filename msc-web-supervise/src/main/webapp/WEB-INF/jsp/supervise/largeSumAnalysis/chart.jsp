<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div style="width:100%;height:100%;" id="chart">
</div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
$(function() {
	//console.log(top.$.modalDialog.data[0]);
	function initChart(data, showType,zoneName) {
		$("#chart").empty();
		var axis = [];
		var newData=[];
		$(data).each(function(i) {
			var name = zoneName;
			var value = data[i]['LARGESUMRECIPE'];
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
			newData.push({name:name,value:value});
			console.log(newData[i]);
			axis.push(newData[i].name);
		});
		var myChart = echarts.init(document.getElementById('chart'));
		var option = {
			    title : {
			        text: "大金额处方分析"
			    },
			    tooltip: {
			        trigger: 'axis',
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
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
			   	legend: {
                    data:['大金额处方数']
                },
                grid:{x:120,x2:80,y:35,y2:80},
			    xAxis:[{
		            type : 'category',
		            axisTick : {show: false},
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
			        name: '大金额处方数',
			        type: 'bar',
			        barWidth:60,
	                tooltip:{
	                	show:false,
	                    trigger: 'item'
		           	},
		           	itemStyle:{
		            	normal: {
		            		label : {show: true, position: 'insideTop'},
		            		color:function(color) {
		            			return color.data.value > 0?"#C1232B":"#C6E579";
		            		}
		                }
		            },
		           
			       	data: newData
			    }]
			    
			};
		myChart.setOption(option);
	}
	initChart(top.$.modalDialog.data[0],top.$.modalDialog.data[1],top.$.modalDialog.data[2]);
});
</script>

