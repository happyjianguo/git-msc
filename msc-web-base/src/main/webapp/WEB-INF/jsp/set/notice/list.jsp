<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

					
<body class="easyui-layout">
<!-- <div class="search-bar" >
		<input id="ss" />
		<div id="mm">
			<div data-options="name:'title' ">标题</div>
			<div data-options="name:'content' ">内容</div>
		</div>
		</div>
		-->
		<div class="single-dg">
			<table  id="dg" ></table>
		</div> 

</body>
</html>

<script>
//初始化
$(function(){
	/* $('#ss').searchbox({
		searcher:function(value,name){ 
			search(value,name);
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	});  */
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/notice/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		multiSort:true,
		sortName:"createDate",
		columns:[[
		        	
					{field:'createDate',title:'创建日期',width:10,align:'center',sortable:true,
						formatter: function(value,row,index){
							if (row.createDate){
								return $.format.date(row.createDate,"yyyy-MM-dd");
							}
						}
					}, 
					{field:'publishDate',title:'发布日期',width:10,align:'center',sortable:true,
						formatter: function(value,row,index){
							if (row.publishDate){
								return $.format.date(row.publishDate,"yyyy-MM-dd");
							}
						}
					}, 
		        	{field:'title',title:'标题',width:20,align:'left',sortable:true},
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
		    		},
		    		{field:'status',title:'操作',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.status == 0){
								return "<a href='#' class='dgbtn' data-options=\"iconCls:'icon-ok'\" onclick=statusFunc("+row.id+",1)>发布</a>"
							}else if (row.status == 1){
								return "<a href='#' class='dgbtn'  data-options=\"iconCls:'icon-no'\" onclick='statusFunc("+row.id+",0)'>取消发布</a>"
							}
						}
					}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen();	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		},onLoadSuccess:function(data){
			$('.dgbtn').linkbutton({plain:true,height:20}); 
		}
	});

	$('#dg').datagrid('enableFilter', [{
		 field:'filePath',
		 type:'text',
		 isDisabled:1
	},{
        field:'createDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'D',
        options:{
        	editable:false
        }
    },{
        field:'publishDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'S',
        options:{
        	editable:false
        }
    },{
        field:'status',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'未发布'},
                  {value:'1',text:'已发布'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'status');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'status',
                        op: 'LK',
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

//发布 or 取消
function statusFunc(id,status){
	statusAjax(id,status);
}
//搜索
function search(val,name){
	if(name=="title"){
		$('#dg').datagrid('load',{
			"query['t#title_S_LK']": val
		});	
	}else if(name=="content"){
		$('#dg').datagrid('load',{
			"query['t#content_S_LK']": val
		});	
	}
	
}

//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/notice/add.htmlx",
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
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/notice/edit.htmlx",
		queryParams:{
			
		},
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");
				
				/* if(selrow.fileName != null && selrow.fileName!=""){
					top.initFile(1);
				}else{
					top.initFile(0);
				} */
				f.form("load", selrow);
				top.setFileData(selrow.fileManagement);
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
function statusAjax(id,status){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/notice/status.htmlx",
		data:{
			"id":id,
			"status":status
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				//showMsg("操作成功！");
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/notice/del.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
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