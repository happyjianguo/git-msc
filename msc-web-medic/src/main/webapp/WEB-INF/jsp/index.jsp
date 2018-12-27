<%@page import="com.shyl.sys.entity.User"%>
<%@page import="com.shyl.msc.common.CommonProperties"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String flag = session.getAttribute("needChgPwd")+"";
	if(flag.equals("1"))
		response.sendRedirect(request.getContextPath() + "/sys/user/firstPwd.htmlx");
	System.out.println("index.jsp.sessionId = "+session.getId());

	String indexTitle = "";
	String bgImg = "headbg.png";

	User currentUser = session.getAttribute("currentUser")==null?new User():(User)session.getAttribute("currentUser");
	if(currentUser.getOrganization().getOrgType() == 1){
		indexTitle = "医疗机构版 ";
		bgImg = "headbgH.png";
	}else if(currentUser.getOrganization().getOrgType() == 2){
		indexTitle = "供应商版";
		bgImg = "headbgG.png";
	}else if(currentUser.getOrganization().getOrgType() == 3){
		indexTitle = "监管机构版";
		bgImg = "headbg.png";
	}else if(currentUser.getOrganization().getOrgType() == 5){
		indexTitle = "系统运维版";
		bgImg = "headbg.png";
	}else if(currentUser.getOrganization().getOrgType() == 6){
		indexTitle = "GPO版";
		bgImg = "headbgG.png";
	}

%>
<!DOCTYPE html>
<html>
<head>
	<c:set var="base" value="${pageContext.request.contextPath }"/>
	<title><c:out value='${hometitle }'/></title>

	<link rel="stylesheet" type="text/css" href="<c:out value='${base }'/>/resources/js/jquery-easyui/themes/bootstrap/easyui.css">
	<link rel="stylesheet" type="text/css" href="<c:out value='${base }'/>/resources/js/jquery-easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="<c:out value='${base }'/>/resources/css/base.css" />
	<script  type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/jquery.min.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/jquery.md5.js"></script>
	<script  type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/jquery.easyui.min.js"></script>
	<script  type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/jquery.easyui.tree.js"></script>
	<script  type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/easyui_more.js"></script>
	<script  type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/extBrowser.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/myVali.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/common.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/application.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/jquery-dateFormat.min.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery.raty.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery.raty.min.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-migrate-1.2.1.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/backspace.js"></script>
	<script type="text/javascript" src="<c:out value='${base }'/>/resources/js/jquery-easyui/datagrid-filter.js"></script>

	<script  type="text/javascript" src="<c:out value='${base }'/>/resources/js/ca/Sgl_CA.js"></script>

	<style type="text/css">
		.sys-name{
			position: absolute;font-size: 25px;font-weight: bold;color: #eee;line-height: 60px;padding-left: 10px;
		}
		#indexhead a { color:#eee;padding:3px; transition:0.5s; }
		#indexhead a:hover {color:#eee;padding:3px;background:#0F6099;text-decoration: none;border-radius:3px;}
	</style>
</head>

<body class="easyui-layout">
<!-- 上边 -->
<div id="indexhead" data-options="region:'north',border:false"
	 style="height:60px;background:white;background-image: url(<c:out value='${base }'/>/resources/images/<%=bgImg %>)">
	<img src="<c:out value='${base }'/>/resources/images/${logo}" style="margin-left:30px;margin-top:5px;width:50px;float:left; border-radius:4px"></img>

	<span class="sys-name"><c:out value='${hometitle }'/>
		<i style="margin-left:20px;font-size: 20px;">
		<%=indexTitle %>

		</i></span>
	<div style="line-height: 60px;float:right;margin-right:20px;">
		<span style="font-weight:bold;color: #eee;margin-right:10px;" >  </span>
		<span style="font-weight:bold;color: #eee;margin-right:10px;" ><c:out value='${currentUser.name}'/>（<c:out value='${currentUser.empId}'/>）  </span>
		<span style="font-weight:bold;color: #eee;margin-right:10px;" ><c:out value='${orgName}'/>  </span>
		<a href="#" style="font-weight:bold;margin-right:10px;" onClick="updateOpen()">修改密码</a>
		<a href="<c:out value='${base }'/>/sys/logout.htmlx" style="font-weight:bold;">登出</a>
	</div>
</div>
<!-- 左边 -->
<div data-options="region:'west',split:true,title:'功能导航',collapsed:false" style="width:180px;padding:0px;">
	<ul id="leftTree"  style="margin:5px;"></ul>
</div>
<!-- 右边 -->
<!--<div data-options="region:'east',split:true,collapsed:true,title:'East'" style="width:200px;padding:0px;">

    <div id="p1" class="easyui-panel" title="My Panel"        style="width:192px;height:150px;padding:10px;background:#fafafa;"              data-options="iconCls:'icon-save',collapsible:true,maximizable:true">
                        <p>panel content.</p>
                          <p>panel content.</p>
                          </div>
                          <div id="p2" class="easyui-panel" title="My Panel"        style="width:192px;height:150px;padding:10px;background:#fafafa;"              data-options="iconCls:'icon-save',collapsible:true,maximizable:true">
                        <p>panel content.</p>
                          <p>panel content.</p>
                          </div>

</div>-->
<!-- 下边 -->
<!--
<div data-options="region:'south',border:false" style="height:28px;" class="footer">
    <div style="padding-left:20px;padding-top:8px;">用户：XXX</div>
</div>
 -->
<!-- 中间 -->
<div data-options="region:'center',href:'<c:out value='${pageContext.request.contextPath}'/>/home/center.htmlx'">
</div>

<!-- 消息 -->
<div id='msgwin' style="padding: 10px;"></div>
</body>
</html>

<script>

    //弹窗增加
    function updateOpen() {

        top.$.modalDialog({
            title : "修改密码",
            width : 400,
            height : 250,
            href : "<c:out value='${pageContext.request.contextPath }'/>/sys/user/updatePsw.htmlx",
            onLoad:function(){
                var f = parent.$.modalDialog.handler.find("#form1");
                f.find("#empId").textbox("setValue","<c:out value='${currentUser.empId}'/>");
                f.find("#id").val("<c:out value='${currentUser.id}'/>");
            },
            buttons : [ {
                text : '保存',
                iconCls : 'icon-ok',
                handler : function() {
                    var f = top.$.modalDialog.handler.find("#form1");
                    f.submit();
                }
            }, {
                text : '取消',
                iconCls : 'icon-cancel',
                handler : function() {
                    top.$.modalDialog.handler.dialog('destroy');
                    top.$.modalDialog.handler = undefined;
                }
            }]
        });
    }
    var nameIdMap = new Object();
    $(function(){
        $('#leftTree').tree({
            idField:'id',
            treeField:'name',
            parentField:'parentId',
            iconCls:'icon',
            url:'<c:out value='${base }'/>/sys/menu/perlist.htmlx',
            queryParams:{
                "sysId":"admin",
                "orgType":<c:out value='${currentUser.organization.orgType}'/>
            },
            method:'get',
            animate:true,
            lines:true,
            state:'state',
            onClick: function(node){
                var url = node.url;
                if(url!=null&&url != ""){
                    addTab(node.text,url,node.icon);
                }
            },

            onLoadSuccess : function(node, data) {
                mkNameIdMap(data);

                var t = $(this);
                var rooNode = $("#leftTree").tree('getRoots');
                for (var i=0;i<rooNode.length-2;i++){
                    if(rooNode[i].state=="open"){
                        //t.tree('collapse',rooNode[i].target);//折叠菜单1，2
                    }
                }

                //selectTreeNode("");
                //getTreeId();
            }



            //var msgtimeout;
            //$("#msgwin").window({
            //		title:"提示",
            //	width:235,
            //		height:100,
            //		left:$(this).width()-235,
            //		top:$(this).height(),
            //		minimizable:false,
            //		maximizable:false,
            //	collapsible:false,
            //	closed:true,
            //	onOpen:function(){
            //	$("#msgwin").parent().animate({
            //	    top:$("body").height()-100
            //	});
            //	clearTimeout(msgtimeout);
            //	msgtimeout = setTimeout(closeMsging,3000);
            //}
            //});
        })


    });

    function mkNameIdMap(data){
        //生成 name id 对应map
        for (var i=0;i<data.length;i++){
            eval("nameIdMap.A"+data[i].id+"='"+data[i].url+"'");
            if(data[i].children){
                mkNameIdMap(data[i].children);
            }
        }
    }

    function getTreeId(url){
        for ( var key in nameIdMap) {
            if(url == nameIdMap[key]){
                return key;
            }
        }
        return -1;
    }

    function selectTreeNode(url){
        url = url.split("?")[0];
        var nodeid = getTreeId(url);
        if(nodeid!=null&&nodeid.length>0){
            var node = $('#leftTree').tree('find', nodeid.substring(1));
            $('#leftTree').tree('select', node.target);
        }
    }
    function showMsg(text){
        top.$.messager.show({
            title :  '消息',
            msg : text,
            timeout : 1000 * 2
        });

        //$("#msgwin").removeClass("errmsg");
        //$("#msgwin").window({
        //	content:text
        //});
        //$('#msgwin').window('open');
    }
    //function closeMsging(){
    //		$("#msgwin").parent().animate({
    //			 top:$("body").height()
    //		},"normal","linear",function(){
    //			$("#msgwin").window("close");
    //		});
    //	}

    function showErr(text){
        top.$.messager.show({
            title :  '错误',
            msg : text,
            timeout : 1000 * 2
        });
        //$("#msgwin").addClass("errmsg");
        //$("#msgwin").window({
        //	content:text
        //});
        //$('#msgwin').window('open');
    }

</script>