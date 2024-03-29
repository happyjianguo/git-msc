<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<style type="text/css">
        
        .portal{padding:0;margin:0;overflow:auto;border:0px solid #99bbe8;}
        .datagrid-header {
			/* display:none; */
		}
    </style>
    
<script type="text/javascript" src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery.portal.js"></script>
<body>
    <div id="pp">
        <div style="width:30%;"></div>
        <div style="width:70%;"></div>
    </div>
    <div id="tt">
        <a href="javascript:void(0)" class="icon-items" onclick="javascript:parent.addTab('更多公告', '/set/notice/more.htmlx', null)"></a>
    </div>
    <script type="text/javascript">
        var panels = [
                      {id:'p1',title:'公告',height:270,collapsible:true,iconCls:'icon-defect_define',tools:'#tt',href:' <c:out value='${pageContext.request.contextPath }'/>/home/notice.htmlx'},
                      {id:'p2',title:'统计',height:300,collapsible:true,iconCls:'icon-process',href:' <c:out value='${pageContext.request.contextPath }'/>/home/monitorTotal.htmlx'},
                      {id:'p3',title:'医疗机构采购金额',height:270,collapsible:true,iconCls:'icon-process',href:' <c:out value='${pageContext.request.contextPath }'/>/home/hospitaltradejbgoods.htmlx'},
                      {id:'p4',title:'医院药品收入',height:300,collapsible:true,iconCls:'icon-process',href:' <c:out value='${pageContext.request.contextPath }'/>/home/hisDrugRatio.htmlx'},
                      
                      /*{id:'p3',title:'违约情况',height:500,collapsible:true,iconCls:'icon-process',href:' <c:out value='${pageContext.request.contextPath }'/>/home/abnormalOrder.htmlx'},
                      {id:'p4',title:'合同执行情况',height:500,collapsible:true,iconCls:'icon-process',href:' <c:out value='${pageContext.request.contextPath }'/>/home/contractM.htmlx'} ,
                      {id:'p4',title:'违约情况',height:300,collapsible:true,iconCls:'icon-chart_bar',href:' <c:out value='${pageContext.request.contextPath }'/>/home/contractChart.htmlx'},
                      {id:'p5',title:'交易情况',height:300,collapsible:true,iconCls:'icon-chart_bar',href:' <c:out value='${pageContext.request.contextPath }'/>/home/tradeChart.htmlx'} */
                  ];
        function getCookie(name){
            var cookies = document.cookie.split(';');
            if (!cookies.length) return '';
            for(var i=0; i<cookies.length; i++){
                var pair = cookies[i].split('=');
                if ($.trim(pair[0]) == name){
                    return $.trim(pair[1]);
                }
            }
            return '';
        }
        function getPanelOptions(id){
            for(var i=0; i<panels.length; i++){
                if (panels[i].id == id){
                    return panels[i];
                }
            }
            return undefined;
        }
        function getPortalState(){
            var aa = [];
            for(var columnIndex=0; columnIndex<2; columnIndex++){
                var cc = [];
                var panels = $('#pp').portal('getPanels', columnIndex);
                for(var i=0; i<panels.length; i++){
                    cc.push(panels[i].attr('id'));
                }
                aa.push(cc.join(','));
            }
            return aa.join(':');
        }
        function addPanels(portalState){
            var columns = portalState.split(':');
            for(var columnIndex=0; columnIndex<columns.length; columnIndex++){
                var cc = columns[columnIndex].split(',');
                for(var j=0; j<cc.length; j++){
                    var options = getPanelOptions(cc[j]);
                    if (options){
                        var p = $('<div/>').attr('id',options.id).appendTo('body');
                        p.panel(options);
                        $('#pp').portal('add',{
                            panel:p,
                            columnIndex:columnIndex
                        });
                    }
                }
            }
            
        }
        
        $(function(){
            $('#pp').portal({
            	width:$(this).width(),
            	height:$(this).height(),
                onStateChange:function(){
                    var state = getPortalState();
                    var date = new Date();
                    date.setTime(date.getTime() + 24*3600*1000);
                    document.cookie = 'portal-state='+state+';expires='+date.toGMTString();
                }
            });
            var state = getCookie('portal-state');
            if (!state){
                state = 'p1,p2:p3,p4,p5';    // the default portal state
            }
            addPanels(state);
            $('#pp').portal('resize');
            
            
        });
    </script>
</body>
</html>