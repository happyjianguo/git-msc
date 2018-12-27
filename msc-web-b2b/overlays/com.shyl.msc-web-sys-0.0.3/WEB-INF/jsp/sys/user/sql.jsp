<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<body class="easyui-layout" >
<div style="padding:10px 0px 0px 10px">
	<textarea rows="6" cols="50%" id="mysql" name="mysql" style="height:100px;" >select * from t_sys_user</textarea>
	<a style="margin-left:15px;vertical-align: top;padding:10px;height:50px;"  class="easyui-linkbutton"  onclick=ff() >查询  </a>   
	<a style="margin-left:15px;vertical-align: top;padding:10px;height:50px;"  class="easyui-linkbutton"  onclick=delFunc() >执行excute  </a>   
	<a style="margin-left:15px;vertical-align: top;padding:10px;height:50px;"  class="easyui-linkbutton"  onclick=csvFunc() >导出XLS  </a>   
	<a style="margin-left:15px;vertical-align: top;padding:10px;height:50px;"  class="easyui-linkbutton"  onclick=uploadFunc() >导入XLS  </a>   
	
</div>
<div style="padding:10px">
	<table  id="dg" ></table>
	
	<form id="fileId" method="POST" enctype="multipart/form-data">
			<input type="file" name="myfile" style="display:none" id="myfile" />
		</form>
</div>
</body>
</html>


<script>
$("#myfile").change(function(){
	$.messager.confirm('确认信息', '确认导入么?', function(r){
		if (r){
			$("#fileId").submit();
		}else{
			location.reload(true);
		}
	});
});

$("#fileId").form({
	url :"${pageContext.request.contextPath}/sys/sql/upload.htmlx",
	onSubmit : function() {
		return true;
	},
	success : function(result) {
		$('#myfile').val("");
		result = $.parseJSON(result);
		if(result.success){
			$('#dg').datagrid('reload');
			showMsg(result.msg);
		}else{
			importErrOpen(result.msg);
		}
	},
	error:function(){
		showErr("出错，请刷新重新操作");
	}
});

function delFunc(){
	
	$.messager.confirm('确认信息', '确认要执行SQL吗?', function(r){
		if (r){
			var mysql = $("#mysql").val();
			delAjax(mysql);
		}
	});
}

function csvFunc(){
	
	$.messager.confirm('确认信息', '确认要导出excel吗?', function(r){
		if (r){
			var mysql = $("#mysql").val();
			window.open("${pageContext.request.contextPath }/sys/sql/csv.htmlx?mysql="+mysql);
		}
	});
}

function uploadFunc(){
	
	$("#myfile").click();
}


function ff(){
	var mysql = $("#mysql").val();
	queryAjax(mysql);
}

function aa(rowtitle,mysql){

	var columns = "";
	var array =[];
	var columns=[];
	for ( var key in rowtitle[0]) {
		 array.push({field:'',title:'',width:''});
	}
     columns.push(array);
     var i = 0;
     for ( var key in rowtitle[0]) {
    	columns[0][i]['field']= key;
 	    columns[0][i]['title']= key;
 	    columns[0][i]['width']= '100';
 	    i++;
	}
     
	//用户组
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :  $(this).height()-150,
		url:"${pageContext.request.contextPath }/sys/sql/dosql.htmlx",
		queryParams:{
			"mysql":mysql
		},
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:columns,
		onClickRow: function(index,row){
			
		},
		onLoadSuccess:function(){
			//$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});
}


//初始化
//=============ajax===============
function queryAjax(mysql){
	
	$.ajax({
		url:"${pageContext.request.contextPath }/sys/sql/dosql.htmlx",
				data:{
					"mysql":mysql
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					aa(data.rows,mysql);
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}

function delAjax(mysql){
	
	$.ajax({
		url:"${pageContext.request.contextPath }/sys/sql/del.htmlx",
				data:{
					"mysql":mysql
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						showMsg(data.msg);
					}else{
						showErr(data.msg);
					}
				
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}



</script>