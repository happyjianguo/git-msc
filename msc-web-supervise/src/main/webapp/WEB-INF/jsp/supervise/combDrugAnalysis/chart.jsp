<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div style="width:100%;height:100%;" id="chart">
</div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
$(function() {
	/* console.log(top.$.modalDialog.data[0]); */
	function initChart(data, showType,zoneName) {		
		$("#chart").empty();
		var axis = [];
		var newCicle = [];
		var newData1=[];
		var newData2=[];
		var newData3=[];
		var newData4=[];
		var newData5=[];
		var newData6=[];
		$(data).each(function(i) {
			var name = zoneName;
			var value1 = data[i]['COMBINEDNUM1'];
			var value2 = data[i]['COMBINEDNUM2'];
			var value3 = data[i]['COMBINEDNUM3'];
			var value4 = data[i]['COMBINEDNUM4'];
			var value5 = data[i]['COMBINEDNUM5'];
			var value6 = data[i]['COMBINEDNUM6'];
			
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
			newCicle.push({name:'一联处方数',value:value1},{name:'二联处方数',value:value2},{name:'三联处方数',value:value3},{name:'四联处方数',value:value4},{name:'五联处方数',value:value5},{name:'六联处方数',value:value6});
			newData1.push({name:name,value:value1});
			newData2.push({name:name,value:value2});
			newData3.push({name:name,value:value3});
			newData4.push({name:name,value:value4});
			newData5.push({name:name,value:value5});
			newData6.push({name:name,value:value6}); 
			axis.push(newData1[i].name);
		});
		var myChart = echarts.init(document.getElementById('chart'));
		console.log(showType);
		if(showType==0){
			var newOption = {
					title : {
				        text: '联合用药分析处方数',
				        x:'center'
				    },
				    tooltip : {
				        trigger: 'item',
				        formatter: "{a} <br/>{b} : {c} ({d}%)"
				    },
				    legend: {
				        orient : 'vertical',
				        x : 'left',
				        data:['一联处方数','二联处方数','三联处方数','四联处方数','五联处方数','六联处方数']
				    },
				    grid:{x:120,x2:80,y:35,y2:80},
				    toolbox: {
				        show : true,
				        feature : {
				            mark : {show: true},
				            dataView : {show: true, readOnly: false},
				            magicType : {
				                show: true, 
				                type: ['pie', 'funnel'],
				                option: {
				                    funnel: {
				                        x: '25%',
				                        width: '50%',
				                        funnelAlign: 'left',
				                        max: 1548
				                    }
				                }
				            },
				            restore : {show: true},
				            saveAsImage : {show: true}
				        }
				    },
				    calculable : true,
				    series : [
				        {
				            name:'联合用药', 
				            type:'pie',
				            radius : '55%',
				            center: ['50%', '60%'],
				            data:newCicle,
				            itemStyle:{
				                normal:{
				                      label:{
				                        show: true,
				                        formatter: '{b} : {c} ({d}%)'
				                      },
				                      labelLine :{show:true}
				                    }
				                }
				        }
				    ]
			};
		myChart.setOption(newOption);
		}else if(showType>=3){
			 var option = {
					    title : {
					        text: "联合用药分析"
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
					        data:['一联(%)','二联(%)','三联(%)','四联(%)','五联(%)','六联(%)']
					    },
					    grid:{x:120,x2:180,y:35,y2:80},
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
					        name: '一联(%)',
					        type: 'bar',
					        barWidth:((130/2/axis.length)<14)?14:(125/2/axis.length),
			                tooltip:{
			                	show:false,
			                    trigger: 'item' 
				           	},
				           	itemStyle:{
				            	normal: {
				            		label : {show: true, position: 'insideTop'}
				                }
				            },
					       	data: newData1
					    },{
					    	 name: '二联(%)',
						        type: 'bar',
						        barWidth:((130/2/axis.length)<14)?14:(125/2/axis.length),
				                tooltip:{
				                	show:false,
				                    trigger: 'item'		      
					           	},	
					           	itemStyle:{
					            	normal: {
					            		label : {show: true, position: 'insideTop'}
					                }
					            },
						       	data: newData2
					    	
					    },{
					    	 name: '三联(%)',
						        type: 'bar',
						        barWidth:((130/2/axis.length)<14)?14:(125/2/axis.length),
				                tooltip:{
				                	show:false,
				                    trigger: 'item'		      
					           	},		
					           	itemStyle:{
					            	normal: {
					            		label : {show: true, position: 'insideTop'}
					                }
					            },
						       	data: newData3
					    	
					    },{
					    	 name: '四联(%)',
						        type: 'bar',
						        barWidth:((130/2/axis.length)<14)?14:(125/2/axis.length),
				                tooltip:{
				                	show:false,
				                    trigger: 'item'		      
					           	},
					           	itemStyle:{
					            	normal: {
					            		label : {show: true, position: 'insideTop'}
					                }
					            },
						       	data: newData4
					    	
					    },{
					    	 name: '五联(%)',
						        type: 'bar',
						        barWidth:((130/2/axis.length)<14)?14:(125/2/axis.length),
				                tooltip:{
				                	show:false,
				                    trigger: 'item'		      
					           	},	
					           	itemStyle:{
					            	normal: {
					            		label : {show: true, position: 'insideTop'}
					                }
					            },
						       	data: newData5
					    	
					    },{
					    	 name: '六联(%)',
						        type: 'bar',
						        barWidth:((130/2/axis.length)<14)?14:(125/2/axis.length),
				                tooltip:{
				                	show:false,
				                    trigger: 'item'		      
					           	},
					           	itemStyle:{
					            	normal: {
					            		label : {show: true, position: 'insideTop'}
					                }
					            },
						       	data: newData6
					    	
					    }]
					    
					};
				myChart.setOption(option);
		}
			
	}
	initChart(top.$.modalDialog.data[0],top.$.modalDialog.data[1],top.$.modalDialog.data[2]);
});
</script>

