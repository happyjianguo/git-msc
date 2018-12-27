<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="回款额度分析" />

<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout">
<div style="width:100%;height:100%;">
	<div style="width:260px;margin:20px auto;">
		<input id="org" name="org"/> 
		<input id="year" name="year"/> 	
		<input id="month" name="month" />	 
	</div>
	<div id="chart1" style="width:99%;height:60%;margin:0 auto;"></div>
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
		var arr_unpayAmt = new Array();
		var arr_prepayAmt = new Array();
		var arr_amt = new Array();
		for(var i=0;i<data.pays.length;i++) {
			arr_org.push(data.pays[i]["name"]);
			var unpayAmt = parseFloat(data.pays[i]["unpayAmt"])/10000;
			var prepayAmt = parseFloat(data.pays[i]["prepayAmt"])/10000;
			arr_unpayAmt.push(unpayAmt.toFixed(2));
			arr_prepayAmt.push(prepayAmt.toFixed(2));
			arr_amt.push((unpayAmt+prepayAmt).toFixed(2));
		}
	
		var myChart1 = echarts.init(document.getElementById('chart1'));
		myChart1.setOption({
			title : {
		        text: '回款额度分析（万天） ',
		        x:'center',
		        y:'0'
		    },
			tooltip : {
		        trigger: 'axis',
		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		        }
		    },
		    toolbox: {
		        feature: {
		            dataView: {show: true, readOnly: false},
		            magicType: {show: true, type: ['line', 'bar']},
		            restore: {show: true},
		            saveAsImage: {show: true}
		        }
		    },
		    legend: {
		    	y:'bottom',
		        data:['提前还款额度', '回款可用额度', '未还款额度']
		    },
		   	grid:{
		   		x:300,
		   		x2:300
		   	},
		    xAxis : [
		        {
		            type : 'value'
		        }
		    ],
		    yAxis : [
		        {
		            type : 'category',
		            axisTick : {show: false},
		            data : arr_org
		        }
		    ],
		    series : [
		        {
		            name:'回款可用额度',
		            type:'bar',
		            label: {
		                normal: {
		                	formatter:function(o) {
					        	return o.value;
					        },
		                    show: true,
		                    position: 'right'
		                }
		            },
		            data:arr_amt
		        },
		        {
		            name:'未还款额度',
		            type:'bar',
		            stack: '总量',
		            label: {
		                normal: {
		                	formatter:function(o) {
					        	return o.value;
					        },
		                    show: true,
		                    position: 'left'
		                }
		            },
		            data:arr_unpayAmt
		        },
		        {
		            name:'提前还款额度',
		            type:'bar',
		            stack: '总量',
		            label: {
		                normal: {
		                	formatter:function(o) {
					        	return o.value;
					        },
		                    show: true,
		                    position: 'right'
		                }
		            },
		            data:arr_prepayAmt
		        }
		    ]		    
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
