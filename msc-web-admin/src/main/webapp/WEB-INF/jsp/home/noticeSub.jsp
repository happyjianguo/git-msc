<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div style="padding: 5px; text-align: center">
 	<span id="title" style="font-size: 20px"><c:out value='${notice.title }'/></span>
</div>
<div style="padding: 5px;  color: #999; text-align: center">
	<span id="date" style="font-size: 12px"><fmt:formatDate value="${notice.createDate }"  pattern="yyyy-MM-dd" /></span>
</div>
<div style="padding: 5px;  font-size: 14px;line-height:20px;text-indent:2em;margin:10px;color: #333; text-align: left">
	<span id="content" ><c:out value='${notice.content }'/></span>
</div>

<c:if test="${notice.fileManagement != null}" >  
		<div style="padding: 5px;margin:20px; text-align: left;border-top:1px solid #999;" id="fileDiv">
			附件：
			<c:forEach items="${notice.fileManagement}" var="li" > 
				<div style="padding:3px 0px;"><a href='<c:out value='${pageContext.request.contextPath}'/>/dm/notice/readfile.htmlx?fileid=<c:out value='${li.fileURL}'/>' id="file" >
				<img src='<c:out value='${pageContext.request.contextPath}'/>/resources/images/fileDown.png' width=16 height=16  />
				<c:out value='${li.fileName }'/>
				</a>
				</div>
			</c:forEach> 
		</div>
	
</c:if>

<script>
function setData_notice(a,b,c,d,e){
	$("#title").html(a);
	$("#date").html($.format.date(b,"yyyy-MM-dd"));
	$("#content").html(c);
	if(e == null){
		$("#fileDiv").hide();
	}else{
		$("#file").append(e);
		$("#file").attr("href","<c:out value='${pageContext.request.contextPath }'/>/"+d);
	}
	
}
function initFile(flag){
	if(flag == 0){
		$('#fileName').textbox("destroy");
		$("#file").show();
		$("#fileChanged").val(1);
	}else{
		$("#fileChanged").val(0);
	}
}
//初始化
$(function(){
	$('#fileName').textbox({
		editable:false,
		icons: [{
			iconCls:'icon-cancel',
			handler: function(e){
				$('#fileName').textbox("destroy");
				$("#file").show();
				$("#fileChanged").val(1);
			}
		}]
	})
	
	$("#form1").form({
		url :"<c:out value='${pageContext.request.contextPath }'/>/dm/notice/edit.htmlx",
		onSubmit : function() {
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
				showMsg("修改成功！");
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>