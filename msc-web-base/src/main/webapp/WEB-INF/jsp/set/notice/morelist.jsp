<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

					
<body class="easyui-layout">
		<div class="single-dg">
			<table  id="dg" ></table>
		</div> 
</body>
</html>

<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/notice/morepage.htmlx",
		queryParams:{
			"query['t#status_I_EQ']": 1
		},
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
					{field:'publishDate',title:'发布日期',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.publishDate){
								return $.format.date(row.publishDate,"yyyy-MM-dd");
							}
						}
					}, 
		        	{field:'title',title:'标题',width:20,align:'left'},
		        	{field:'filePath',title:'附件',width:20,align:'left',
			        		formatter: function(value,row,index){
			        			var files = row.fileManagement;
			        			var rtn = "";
			        			for (var i=0;i<files.length;i++) {
			        				var fileobje = files[i];
			        				rtn += "<a href=\" <c:out value='${pageContext.request.contextPath }'/>/set/notice/readfile.htmlx?fileid="+fileobje.fileURL+"\" >";
			        				rtn += "<img src=\" <c:out value='${pageContext.request.contextPath }'/>/resources/images/fileDown.png\" width=16 height=16  />"+fileobje.fileName+"</a>";
								}
								return rtn;
							}
		    		}
		   		]],
		onDblClickRow: function(index,row){
			editOpen(row.id);
		}
	});

	$('#dg').datagrid('enableFilter', [{
		 field:'filePath',
		 type:'text',
		 isDisabled:1
	},{
        field:'publishDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'S',
        options:{
        	editable:false
        }
    }]);
	
});

//弹窗详情
function editOpen(id) {
	top.$.modalDialog({
		title : "详情",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/home/notice/sub.htmlx",
		queryParams:{
			id:id
		}
	});
}

//=============ajax===============


</script>