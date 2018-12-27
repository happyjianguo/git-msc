<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<style type="text/css">
	.file {
		position: absolute;
		top: 0px;
		left: 0px;
		height: 30px;
		width: 85px;
		filter: alpha(opacity : 0);
		opacity: 0;
		cursor: pointer;
	}

</style>
<body class="easyui-layout" >			
	<div class="single-dg">
		<form id="fileId" method="POST" enctype="multipart/form-data">
			<input type="file" name="myfile" class="file" id="myfile" />
		</form>
		<shiro:hasPermission name="set:company:add">
			<div id="toolbar" class="search-bar" >
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addOpen()">添加</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delFunc()">删除</a>
				<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-import',plain:true"  onclick='$("#myfile").click();'>导入</a>
				<a href="<c:out value='${pageContext.request.contextPath }'/>/template/companyTemplate.xlsx"  class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" target="_blank">模板下载</a>
		    </div>
	    </shiro:hasPermission>
		<table  id="dg" ></table>
	</div>

</body>
</html>

<script>

    $("#myfile").change(function(){
        $.messager.confirm('确认信息', '确认导入厂家资料么?', function(r){
            if (r){
                $("#fileId").submit();
            }else{
                location.reload(true);
            }
        });
    });

    $("#fileId").form({
        url :" <c:out value='${pageContext.request.contextPath }'/>/set/company/upload.htmlx",
        onSubmit : function() {
            top.$.messager.progress({
                title : '提示',
                text : '数据导入中，请稍后....'
            });
            return true;
        },
        success : function(result) {
            $('#myfile').val("");
            top.$.messager.progress('close');
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
var companyType = "<c:out value='${companyType}'/>";
if(companyType =='0'){
	companyType = "t.isProducer=1";
}else if(companyType =='1'){
	companyType = "t.isVendor=1";
}else if(companyType =='2'){
	companyType = "t.isSender=1";
}else if(companyType =='3'){
	companyType = "t.isGPO=1";
}
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -3,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/company/page.htmlx",
		queryParams:{
			companyType:companyType
		},
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'单位编码',width:10,align:'center'},
		        	{field:'fullName',title:'单位名称',width:10,align:'center'},
		        	{field:'shortName',title:'单位简称',width:10,align:'center'},
		        	{field:'comtype',title:'公司类型',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			var ss = ""
							if (row.isProducer == 1){
								ss +="厂商 ";
							}
		        			if (row.isVendor == 1){
								ss +="供应商 ";
							}
		        			if (row.isSender == 1){
								ss +="配送商 ";
							}
		        			if (row.isGPO == 1){
								ss +="GPO ";
							}
		        			return ss;
						}
	    			},
	    			{field:'isDisabled',title:'是否禁用',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
							}
						}
	    			}
		   		]],
		toolbar: "#toolbar",
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
	       field:'comtype',
	       type:'combobox',
	       options:{
	           panelHeight:'auto',
	           editable:false,
	           data:[{value:'',text:'-请选择-'},
	                 {value:'0',text:'厂商'},
	                 {value:'1',text:'供应商'},
	                 {value:'2',text:'配送商'},
	                 {value:'3',text:'GPO'}],
	           value:"<c:out value='${companyType}'/>",
	           onChange:function(value){
	        	   $('#dg').datagrid('removeFilterRule', 'isProducer');
	        	   $('#dg').datagrid('removeFilterRule', 'isVendor');
	        	   $('#dg').datagrid('removeFilterRule', 'isSender');
	        	   $('#dg').datagrid('removeFilterRule', 'isGPO');
	               if (value == '0') {
		               	$('#dg').datagrid('addFilterRule', {
		                       field: 'isProducer',
		                       op: 'LK',
		                       fieldType:'I',
		                       value: 1
		                   });
	               }else if (value == '1') {
		               	$('#dg').datagrid('addFilterRule', {
		                       field: 'isVendor',
		                       op: 'LK',
		                       fieldType:'I',
		                       value: 1
		                   });
	              }else if (value == '2') {
		               	$('#dg').datagrid('addFilterRule', {
		                       field: 'isSender',
		                       op: 'LK',
		                       fieldType:'I',
		                       value: 1
		                   });
	              }else if (value == '3') {
		               	$('#dg').datagrid('addFilterRule', {
		                       field: 'isGPO',
		                       op: 'LK',
		                       fieldType:'I',
		                       value: 1
		                   });
	              }
	               $('#dg').datagrid('doFilter');
	           }
	       }
	   },{
       field:'isDisabled',
       type:'combobox',
       options:{
           panelHeight:'auto',
           editable:false,
           data:[{value:'',text:'-请选择-'},
                 {value:'0',text:'未禁用'},
                 {value:'1',text:'禁用'}],
           onChange:function(value){
        	   $('#dg').datagrid('removeFilterRule', 'isProducer');
        	   $('#dg').datagrid('removeFilterRule', 'isVendor');
        	   $('#dg').datagrid('removeFilterRule', 'isSender');
               if (value == '') {
            	   $('#dg').datagrid('removeFilterRule', 'isDisabled');
               }else {
	               	$('#dg').datagrid('addFilterRule', {
	                       field: 'isDisabled',
	                       op: 'EQ',
	                       fieldType:'I',
	                       value: value
	                   });
              }

               $('#dg').datagrid('doFilter');
           }
       }
   }]);
	
});

//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj.id);
		}
	});
}



//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/company/add.htmlx",
		onLoad:function(){
			
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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

//弹窗修改
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "修改",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/company/edit.htmlx",
		queryParams:{
			"regionCode":selrow.regionCode==null?"":selrow.regionCode,
		},
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");
				if(!isempty(selrow.registryDate)){
					selrow.registryDate = $.format.date(selrow.registryDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.openDate)){
					selrow.openDate = $.format.date(selrow.openDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.poutDate)){
					selrow.poutDate = $.format.date(selrow.poutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.outDate)){
					selrow.outDate = $.format.date(selrow.outDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.creditOutDate)){
					selrow.creditOutDate = $.format.date(selrow.creditOutDate,"yyyy-MM-dd");
				}
				f.form("load", selrow);
			}
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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

//=============ajax===============
function delAjax(id){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/set/company/del.htmlx",
				data:"id="+id,
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');  
						showMsg("删除成功！");
					} 
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}


</script>