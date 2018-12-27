<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="县区GPO采购金额对比" />

<html>
<body class="easyui-layout">
<div style="padding:10px 50px;margin:0 auto;width:550px;">
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
				url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/orderCentralized/regionChartSum.htmlx",
				type:"post",
				dataType:"json",
				data:{year:year,month:month,treepath:regionCode0+","+regionCode1},
				success:function(data) {
					if (data.length == 0) return;

					var axis = [];
					$(data).each(function(i) {
						data[i].name=data[i]['NAME'];
						data[i].value=data[i]['VALUE'];
						axis.push(data[i].name);
					});
					var height = axis.length*30+100;
					if (axis.length > 0) {
						$("#chart").height(height);
					}
					var myChart = echarts.init(document.getElementById('chart'));
					myChart.setOption({
					    title : {
					        text: '县区GPO采购金额对比',
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
				            data:axis,
				    		axisLabel:{
				    			interval:0
				            }
				        }],
					    xAxis:[{
			        	   	type:'value',
			                position: 'top',
			                axisLabel:{
						        formatter:"{value}元"
			                }
					    }],
					    series: [{
					        name: 'GPO订单销售金额',
					        type: 'bar',
					        barWidth:14,
				            label: {
				                normal: {
							        formatter:function(o) {
							        	return o.value+"元";
							        },
				                    show: true,
				                    position: 'right'
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
