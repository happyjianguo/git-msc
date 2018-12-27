<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="GPO药品集采情况" />

<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout">
<div style="padding:10px 50px;margin:0 auto;width:220px;">
<input id="year"/>
<input id="month"/>
</div>
<div style="width:100%;height:100%;overflow-y: auto;">
<div id="chart0" style="width:97%;height:40%;"></div>
<div id="chart1" style="width:99%;height:60%;margin:0 auto;"></div>
</div>
</body>
</html>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	
	$(function() {
		function doSearch(){
			var year = $('#year').combobox('getValue');
			var month = $('#month').combobox('getValue');
			var myChart0 = echarts.init(document.getElementById('chart0'));
			myChart0.setOption({
			    title : {
			        text: 'GPO药品分布情况（占用百分比） ',
			        x:'center',
			        y:'0'
			    }
			});
	
			myChart0.showLoading();
			$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productCentralized/chart.htmlx",
				type:"post",
				dataType:"json",
				data:{year:year,month:month},
				success:function(data) {
					myChart0.setOption({
						    series : [{
					            name: 'GPO药品分布',
					            type: 'pie',
					            radius : '40%',
					            center: ['50%', '50%'],
					            data:[
					                  {value:data.gpo,name:"GPO药品"+(data.gpo)+"%"},
					                  {value:(100-data.gpo),name:"非GPO药品"+(100-data.gpo)+"%"},
					            ],
					            itemStyle: {
					                emphasis: {
					                    shadowBlur: 10,
					                    shadowOffsetX: 0,
					                    shadowColor: 'rgba(0, 0, 0, 0.5)'
					                }
					            }
					    }]
					});
					myChart0.hideLoading();
	
					
					var centralizeds = [];
					var xAxis = [];
					for(var i=data.centralized.length-1;i>=0;i--) {
						var centralized = data.centralized[i];
						centralizeds.push({name:centralized["NAME"],value:centralized["VALUE"]});
						xAxis.push(centralized["NAME"]);
					}
					var height = xAxis.length*20+100;
					if ($("#chart1").height() < height) {
						$("#chart1").height(height);
					}
					var myChart1 = echarts.init(document.getElementById('chart1'));
					myChart1.setOption({
					    title : {
					        text: 'GPO药品集中度',
					        x:'center',
					        y:'0'
					    },
					    tooltip: {
					        trigger: 'axis',
					        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
					            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
					        }
					   	},
					   	grid:{
					   		x:300,
					   		x2:300
					   	},
					    yAxis:[{
				            type : 'category',
				            axisTick : {show: false},
				            data:xAxis,
				    		axisLabel:{
				    			interval:0
				            }
				        }],
					    xAxis:[{
			        	   	type:'value',
			                position: 'top',
			        	   	data:[20,40,60,80,100], 
				    		axisLabel:{formatter:'{value} %'}
					    }],
					    series: [{
					        name: 'GPO药品占比',
					        type: 'bar',
				            label: {
				                normal: {
							        formatter:function(o) {
							        	return o.value+"%";
							        },
				                    show: true,
				                    position: 'right'
				                }
				            },
			                tooltip:{
			                    trigger: 'item',
			                    formatter: function(a) {
						        	return 'GPO药品采购百分比：'+ a.value + "%";
			                    }
				           	},
					       	data: centralizeds
					    }]
					    
					});
					
					myChart1.hideLoading();
				}
			});
		}
		//初始化年份
		initYearCombobox("year");
		//年份选中事件
		$('#year').combobox({onSelect:doSearch});
		//初始化月份控件
		var month_datas = new Array();
		month_datas[0] = {month:"",name:"全部"};
		for(var i=0; i<12; i++){
			var month_data = new Object();
			month_data.month = i<9?"0"+(i+1):i+1;
			month_data.name = i+1+"月";
			month_datas[i+1] = month_data;
		}
		$('#month').combobox({
			data: month_datas,
			valueField: 'month',
			textField: 'name',
			width:60,
			editable: false,
			onSelect:doSearch
		});
    	doSearch();
	})
	
</script>
