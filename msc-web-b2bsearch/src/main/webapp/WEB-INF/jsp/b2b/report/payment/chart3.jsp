<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="回款周期分析" />

<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout">
<div style="width:100%;height:100%;overflow-y: auto;">
	<div style="width:260px;margin:20px auto;">
		<input id="org" name="org"/> 
		<input id="year" name="year"/> 		
		<input id="month" name="month" />	  
	</div>
	<div id="chart1" style="width:100%;"></div>
</div>
</body>
</html>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	function change(org,year,month){
		$.ajax({
			url: " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/payment/chart.htmlx",
			data:{
				org:org,
				year:year,
				month:month		
			},
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){		
				chart(data);			
			}
		});	
	}
	function chart(data){
		var arr_org = new Array();
		var arr_day = new Array();
		for(var i=0;i<data.pays.length;i++) {
			arr_org.push(data.pays[i]["name"]);	
			var prepayAmt = parseFloat(data.pays[i]["prepayAmt"]);
			var unpayAmt = parseFloat(data.pays[i]["unpayAmt"]);
			var sum = parseFloat(data.pays[i]["sum"]);

			arr_day.push((30 - (prepayAmt+unpayAmt)/sum).toFixed(2));			
		}
		var height = arr_day.length*35+100;
		if (arr_day.length > 0) {
			$("#chart1").height(height);
		}
		var myChart1 = echarts.init(document.getElementById('chart1'));
		myChart1.setOption({
			title: {
		        text: '回款周期分析（天）',
		        x: "center"
		    },
		    tooltip: {},
		   	grid:{
		   		x:300,
		   		x2:300
		   	},
		    toolbox: {
		        feature: {
		            dataView: {show: true, readOnly: false},
		            magicType: {show: true, type: ['line', 'bar']},
		            restore: {show: true},
		            saveAsImage: {show: true}
		        }
		    },
		    xAxis: {
		    	type:'value',
                position: 'top'
		    },
		    yAxis: {
		    	data: arr_org
		    },
		    series: [{
		        name: '天数',
		        type: 'bar',
		        barWidth:20,
		        label: {
	                normal: {
				        formatter:function(o) {
				        	return o.value;
				        },
	                    show: true,
	                    position: 'right'
	                }
	            },
		        data: arr_day
		    }]
		});
		
		myChart1.hideLoading();
	}
	$(function() {
		var datas = new Array();
		datas.push({name:"医院",value:"0"});
		datas.push({name:"区县",value:"1"});
		
		$('#org').combobox({
			data: datas,
			valueField: 'value',
			textField: 'name',
			width:80,
			editable: false,
			value:"0",
			onSelect: function(record){
				var year = $("#year").combo('getValue');
				var month = $("#month").combo('getValue');
				change(record.value,year,month);
			}			
		});
		
		initYearCombobox("year");
		$('#year').combobox({		
			onSelect: function(record){
				var org = $("#org").combo('getValue');
				var month = $("#month").combo('getValue');
				change(org,record.year,month);
			}
		});
				
		initMonthCombobox("month");
		$('#month').combobox({		
			onSelect: function(record){
				var org = $("#org").combo('getValue');
				var year = $("#year").combo('getValue');
				change(org,year,record.month);
			}
		});
		
		var org = $("#org").combo('getValue');
		var year = $("#year").combo('getValue');
		var month = $("#month").combo('getValue');
		change(org,year,month);
	})
	
</script>
