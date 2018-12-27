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
			var value = data[i]['DRUGSUM']/data[i]['SUM'];
			if (showType == 1) {
				name = data[i]['PROVINCENAME'];
			} else if (showType == 2) {
				name = data[i]['CITYNAME'];
			} else if (showType == 3) {
				name = data[i]['COUNTYNAME'];
			} else if (showType == 4) {
				name = data[i]['HOSPITALNAME'];
			} else if (showType == 5) {
				name = data[i]['MONTH'];
			} 
			value = parseFloat(value*100).toFixed(1);
			newData.push({name:name,value:value});
			console.log(newData[i]);
			axis.push(newData[i].name);
		});
			var myChart = echarts.init(document.getElementById('chart'));
			var option = {
				    title : {
				        text: "医院药品收入比(%)"
				    },
				    tooltip: {
				        trigger: 'axis',
				        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
				            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
				        }
				   	},
				   	legend: {
	                    data:['药品与总收入比(%)']
	                },
	                grid:{x:120,x2:80,y:35,y2:80},
				    xAxis:[{
			            type : 'category',
			            axisTick : {show: false},
			            data:axis,
			    		axisLabel:{
			    			rotate:0,
			    			interval:0
			            }
			        }],
				    yAxis:[{
		        	   	type:'value',
		                position: 'top'
				    }],
				    series: [{
				        name: '药品与总收入比(%)',
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
			           	/* markLine : {
			                data : [
			                    {type : 'average', name : '平均值'}		                 
			                ],
			                itemStyle:{
		                        normal:{color:'green'}
		                    }
			            }, */
				       	data: newData
				    }]
				    
				};
		myChart.setOption(option);
	}
	initChart(top.$.modalDialog.data[0],top.$.modalDialog.data[1],top.$.modalDialog.data[2]);
});
</script>

