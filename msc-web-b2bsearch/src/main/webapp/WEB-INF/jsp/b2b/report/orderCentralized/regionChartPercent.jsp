<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="区县GPO采购集中度" />

<html>
<body class="easyui-layout">
<div style="padding:10px 50px;margin:0 auto;width:520px;">
所在地区:
<input id="combox1" name="combox1" style="width:80px;" />
<input id="combox2" name="combox2" style="width:80px;" />
年份：<input id="year"/>
月份：<input id="month"/>
<a id="search_btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询  </a> 
</div>
<div style="width:100%;overflow-y: auto;height:100%;">
	<div id="chart" style="width:100%;">
	</div>
</div>
</body>
</html>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	
	$(function() {
		function doSearch() {
			var year = $('#year').combobox('getValue');
			var month = $('#month').combobox('getValue');
			var regionCode0 = $('#combox1').combobox('getValue');
			var regionCode1 = $('#combox2').combobox('getValue');
			$("#chart").empty();
			$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/orderCentralized/regionChartPercent.htmlx",
				type:"post",
				dataType:"json",
				data:{year:year,month:month,treepath:regionCode0+","+regionCode1},
				success:function(data) {
					if (data.length == 0) return;
					//定义竖向坐标
					var yAxis = [],avg = 0,count=0;
					//统计平均值
					$(data).each(function() {
						if (this["VALUE"] > 0) {
							avg += this["VALUE"];
							count++;
						}
					});
					//计算平均值
					if (count > 0) {
						avg = parseInt(avg/count);
						if (avg < 0) avg = 0;
					}
					//转换key
					$(data).each(function() {
						this.name=this["NAME"];
						this.value=this["VALUE"]-avg;
						yAxis.push(this.name)
					});
					//设置高度
					var height = data.length*35+100;
					if ($("#chart").height() < height) {
						$("#chart").height(height);
					}
					var myChart = echarts.init(document.getElementById('chart'));
					myChart.setOption({
					    title : {
					        text: '区县GPO采购集中度',
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
				            data:yAxis,
				    		axisLabel:{
				    			interval:0
				            }
				        }],
					    xAxis:[{
			        	   	type:'value',
			                position: 'top',
			                axisLabel:{
						        formatter:function(value) {
						        	return (value + avg).toFixed(2) + "%";
						        }
			                }
					    }],
					    series: [{
					        type: 'bar',
			                tooltip : {
			                    trigger: 'item',
			                    formatter: function(a) {
						        	return a.name+'<br>GPO药品采购百分比：'+(a.value + avg).toFixed(2) + "%";
			                    }
			                },
				            itemStyle:{
				            	normal: {
				            		color:function(color) {
				            			return color.data.value > 0?"#9E0F05":"#0C7708";
				            		}
				                }
				            },
					       	data: data
					    }]
					    
					});
				}
			});
		}
		$("#search_btn").click(doSearch);
		initYearCombobox("year");
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
			editable: false
		});
		//身份下拉
		$('#combox1').combobox({
			url: " <c:out value='${pageContext.request.contextPath }'/>/dm/regioncode/lvlone.htmlx",
		    valueField:'id',    
		    textField:'name', 
		    value:'3188',
		    editable:false,
		    onSelect:function(a,b) {
		    	var pid = $('#combox1').combobox('getValue');
		    	var nextCombox = $('#combox2');
		    	toNextCombox(nextCombox,pid);
		    }
		});
		
		$('#combox2').combobox({
		    valueField:'id',    
		    textField:'name', 
		    url: " <c:out value='${pageContext.request.contextPath }'/>/dm/regioncode/lvltwo.htmlx",
			queryParams:{
				parentId:'3188'
			},
		    value:'3214',
		    editable:false,
		    onSelect:function(a,b){
		    	var pid = $('#combox2').combobox('getValue');
		    	var nextCombox = $('#combox3');
		    	toNextCombox(nextCombox,pid);
		    },
		    onLoadSuccess:function() {
		    	//非首次加载
		    	if (!window.unLoadFirst) {
		    		window.unLoadFirst = true;
		    		doSearch();
		    	}
		    }
		});
	})
	
</script>
