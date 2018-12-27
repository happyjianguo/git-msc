<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.shyl.sys.entity.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	List<Map<String,Object>> courseList = request.getAttribute("courseList")==null?new ArrayList():(List<Map<String,Object>>)request.getAttribute("courseList");
%>
<style>
.expert{
	width:100px;
	height:30px;
	background: #f2f2f2;
	border-radius：5px;
	border:1px #666 solid;
	color:#666;
	float:left;
	text-align: center;
	line-height: 30px;
	margin:10px;
}
</style>
<div>
<div style="text-align: center;font-size:20px;margin:10px;">项目名称：<c:out value='${projectName }'/></div>
	<div >
	<%
		for(int i=0;i<courseList.size();i++){
			Map m = courseList.get(i);
	%>
	<div style="margin:10px;float:left;">
       	<input  id="course<%=i %>" name="course<%=i %>" class="course" valcount="<%=m.get("COUNT") %>" valcode="<%=m.get("COURSECODE") %>" valname="<%=m.get("COURSENAME") %>"   />
       	</div>
   	<%} %>
   	</div>
   	<div style="clear: both;text-align: center;padding:20px;">
		<a id="btn" href="#" >开始抽取</a>
	</div>
   	
   	<div style="margin:10px;" id="experts">
   		
   		
   	</div>
	<form id="form1" name="form1"  method="post">
		
		   
		</table>
	</form>


</div>



<script>

//初始化
$(function(){
	<%
	for(int i=0;i<courseList.size();i++){
		Map m = courseList.get(i);
	%>
		$('#course<%=i %>').textbox({
			width: 200,
			buttonText:"<%=m.get("COURSENAME") %>（<%=m.get("COUNT") %>人）",
			buttonAlign:"left",
			buttonIcon:"icon-user",
			cls:"margin:20px;"
		});
	<%} %>
	
	
	$('#btn').linkbutton({
	    iconCls: 'icon-search',
	    width:300,
	    height:40,
	    onClick:function(){
	    	rdmCourse();	
	    }
	});
	
	
	
	$("#form1").form({
		url :"<c:out value='${pageContext.request.contextPath }'/>/set/expert/add.htmlx",
		onSubmit : function() {
			//$("#pwd").textbox('setValue',$.md5($("#pwd").textbox('getValue')));
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = $(this).form('validate');
			if (!isValid) {
				top.$.messager.progress('close');
			}
			return isValid;
		},
		success : function(result) {
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				top.$.modalDialog.openner.datagrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("新增成功");
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	
});

function addFunc(){
	var expertArr = new Object();
	$(".expert").each(function(index){
		var expertId = $(this).attr("valId");
		expertArr[expertId] = expertId;
		
	});
	addAjax(expertArr);
}
function rdmCourse(){
	var expertArr = new Object();
	var expertArrFlag = 1;
	$(".course").each(function(index){
		var v = $(this).val()-0;
		var count = $(this).attr("valcount")-0;
		var code = $(this).attr("valcode");
		if(v>count){
			var name = $(this).attr("valname");
			alert(name+" 专家数超出");
			expertArrFlag = 0;
			return false;
		}
		if(v && v != 0){
			expertArr[code] = v;
		}
	});
	if(expertArrFlag == 1){
		rdmCourseAjax(expertArr);
	}
}

//=============ajax===============
function addAjax(expertArr){
	$.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/set/projectExpert/add.htmlx",
				data:{
					"projectId":"<c:out value='${projectId }'/>",
					"expertArr":JSON.stringify(expertArr)
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						top.$.modalDialog.openner.datagrid('reload');
						top.$.modalDialog.handler.dialog('close');
						showMsg("保存成功");
					} else{
						showErr("出错，请刷新重新操作");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}
function rdmCourseAjax(expertArr){
	$('#btn').linkbutton('disable');
	$.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/set/projectExpert/rdmCourse.htmlx",
				data:{
					"expertArr":JSON.stringify(expertArr)
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						showExperts(data.data);
					} else{
						showErr("出错，请刷新重新操作");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}
var expertArr = new Array();
var expertLen;
function showExperts(data){
	$("#experts").empty();
	expertArr = new Array();
	expertArr.push("???");
	expertArr.push("***");
	expertLen = data.length;
	for (var i = 0; i < data.length; i++) {
		var obj = $("<div class='expert' style='display:none' valId='"+data[i].id+"' valName='"+data[i].name+"'>???</div>");
		$("#experts").append(obj);
		expertArr.push(data[i].name);
	}
	$(".expert").each(function(index){
		$(this).fadeIn(index*200);
	});
	numFlag = 0;
	showExpert(0);
}

var numFlag;
var timeout;
function showExpert(num){
	var obj = $(".expert:eq("+num+")");
	if(numFlag++<10){
		obj.html(expertArr[numFlag%expertArr.length]);
		timeout = window.setTimeout("showExpert("+num+")", 100);
	}else{
		clearTimeout(timeout);
		obj.html(obj.attr("valName"));
		if(num+1 < expertLen){
			numFlag = 0;
			showExpert(num+1);
		}else{
			$('#btn').find(".l-btn-text").html("重新抽取");
			$('#btn').linkbutton('enable');
		}
	}
	
}

</script>