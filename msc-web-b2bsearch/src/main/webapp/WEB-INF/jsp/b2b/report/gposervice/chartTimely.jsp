<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="chart" style="width:100%;height:100%;">
</div>
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>
<script type="text/javascript">
	$(function() {
			//打开页面的dom
			opener = top.$.modalDialog.openner;
			var year = opener.find('#year').combobox('getValue');
			var month = opener.find('#month').combobox('getValue');

			var myChart = echarts.init(document.getElementById('chart'));
			myChart.setOption({
			    title : {
			        text: 'GPO药品分布情况（占用百分比） ',
			        x:'center',
			        y:'0'
			    }
			});
			myChart.showLoading();
			$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/gposervice/chartTimely.htmlx",
				type:"post",
				dataType:"json",
				data:{year:year,month:month,"vendorId":"<c:out value='${vendorId}'/>"},
				success:function(data) {
					var newdata = []
					$(data).each(function(i) {
						if(this.value > 0) {
							newdata.push(this);
						}
					})
					myChart.setOption({
						legend : {
							data:data,
							y:"bottom",
							x:"center"
						},
					    series : [{
				            name: 'GPO药品分布',
				            type: 'pie',
				            radius : '40%',
				            center: ['50%', '50%'],
				            data:newdata,
				            label: {
				                normal: {
							        formatter:function(o) {
							        	return o.name+"交易"+o.value+"笔"
							        },
				                    show: true,
				                    position: 'right'
				                }
				            },
				            itemStyle: {
				                emphasis: {
				                    shadowBlur: 10,
				                    shadowOffsetX: 0,
				                    shadowColor: 'rgba(0, 0, 0, 0.5)'
				                }
				            }
					    }]
					});
					myChart.hideLoading();
				}
			});
	})
	
</script>
