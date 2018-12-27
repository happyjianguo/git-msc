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
            {id:'p1',title:'采购统计',height:200,collapsible:true,iconCls:'icon-return_material',href:' <c:out value='${pageContext.request.contextPath }'/>/home/total.htmlx'},
            {id:'p2',title:'公告栏',height:200,collapsible:true,iconCls:'icon-defect_define',tools:'#tt',href:' <c:out value='${pageContext.request.contextPath }'/>/home/notice.htmlx'},
            {id:'p3',title:'系统消息',height:200,collapsible:true,iconCls:'icon-msg',href:' <c:out value='${pageContext.request.contextPath }'/>/home/msg.htmlx'},
            {id:'p4',title:'收货区',height:200,collapsible:true,iconCls:'icon-process',href:' <c:out value='${pageContext.request.contextPath }'/>/home/inout.htmlx'},
            {id:'p5',title:'订单追踪',height:200,collapsible:true,iconCls:'icon-process',href:' <c:out value='${pageContext.request.contextPath }'/>/home/orderH.htmlx'} ,
            {id:'p6',title:'合同执行情况',height:200,collapsible:true,iconCls:'icon-chart_bar',href:' <c:out value='${pageContext.request.contextPath }'/>/home/contractH.htmlx'} 
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
            for(var columnIndex=0; columnIndex<3; columnIndex++){
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
                state = 'p1,p2:p3,p4,p5,p6';    // the default portal state
            }
            addPanels(state);
            $('#pp').portal('resize');
            
            
        });
    </script>
</body>
</html>