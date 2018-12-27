<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="供应商断供情况分析" />

<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout">
<div style="width:100%;height:100%;overflow-y: auto;">
	<div style="width:80px;margin:20px auto;">
		<input id="year" name="year"/> 		 
	</div>
	<div id="chart1" style="width:99%;min-width:900px;"></div>
</div>
</body>
</html>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	function change(year){
		$.ajax({
			url: " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/gposervice/chartB.htmlx",
			data:{
				year:year			
			},
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){		
				chart(data);			
			}
		});	
	}
	
	var nameCodeObj = new Object();
	function chart(data){
		var arr_org = new Array();
		var arr_num = new Array();
		
		for(var i=0;i<data.supplys.length;i++) {
			arr_org.push(data.supplys[i]["NAME"]);	
			var num = 0;		
			if(data.supplys[i]["NUM"]){
				num = parseInt(data.supplys[i]["NUM"]);
			}
			arr_num.push(num);		
			nameCodeObj[data.supplys[i]["NAME"]] = data.supplys[i]["CODE"];
		}
		var height = arr_num.length*35+100;
		if (arr_num.length > 0) {
			$("#chart1").height(height);
		}
		var myChart1 = echarts.init(document.getElementById('chart1'));
		myChart1.setOption({
			title: {
		        text: '供应商断供情况分析',
		        x: "center"
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
	            type : 'category',
	            axisTick : {show: false},
		    	data: arr_org,
		    },
		    series: [{
		        name: '断供次数',
		        type: 'bar',
		        barWidth:20,
		        label: {
	                normal: {
				        formatter:function(o) {
				        	return o.value+"次";
				        },
	                    show: true,
	                    position: 'right'
	                }
	            },
		        data: arr_num
		    }]
		});
		myChart1.on('click', function (params) {
			top.$.modalDialog({
				title : "明细情况",
				width : 1200,
				height : 500,
				href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/gposervice/chartBDetail.htmlx",
				queryParams:{
					"hospitalCode": nameCodeObj[params.name],
					"year":$("#year").combo('getValue')
				},
				buttons : [{
					text : '取消',
					iconCls : 'icon-cancel',
					handler : function() {
						top.$.modalDialog.handler.dialog('destroy');
						top.$.modalDialog.handler = undefined;
					}
				}]
			});
		});
		myChart1.hideLoading();
		
	}
	
	$(function() {	
		initYearCombobox("year");
		$('#year').combobox({		
			onSelect: function(record){	
				change(record.year);
			}
		});
				
		var year = $("#year").combo('getValue');
		change(year);
	})
	
</script>
