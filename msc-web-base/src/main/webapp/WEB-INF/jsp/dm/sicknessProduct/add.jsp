<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


	<div class="open-dg" >
		<table id="dg"></table>
		<input type="hidden" id="sicknessCode" value="<c:out value='${sicknessCode }'/>"/>
	</div>

	<script>
	
	//初始化
	$(function(){
		//datagrid
		$('#dg').datagrid({
			fitColumns:true,
			striped:true,
			singleSelect:false,
			rownumbers:true,
			border:true,
			height :  380,
			pagination:true,
			url:" <c:out value='${pageContext.request.contextPath }'/>/dm/sicknessProduct/page.htmlx",
			queryParams:{
		    	sicknessCode:$("#sicknessCode").val(),
		    },
			pageSize:10,
			pageNumber:1,
			remoteFilter: true,
			columns:[[
						{field:'CODE',title:'药品编码',width:20,align:'center'},
						{field:'NAME',title:'药品名称',width:10,align:'center'},
						{field:'GENERICNAME',title:'通用名',width:10,align:'center'},
						{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
						{field:'PRODUCERNAME',title:'生产企业',width:10,align:'center'},
						{field:'MODEL',title:'规格',width:10,align:'center'},
						{field:'PACKDESC',title:'包装规格',width:10,align:'center'}
			   		]],
	   		onClickRow:function(index,row){
	   			var selobjs = $('#dg').datagrid('getSelections');
	   			var isSelected = 0;
	   			$.each(selobjs,function(index){
	   				if(this.ID == row.ID){
	   					isSelected = 1;
	   					return false;
	   				}
	   			});

	   			if(isSelected){
	   				addAjax(row.CODE);
	   			}else{
	   				delAjax(row.CODE);
	   			}
	   		},
	   		toolbar:"#tb",
	   		onLoadSuccess:function(data){
				$.each(data.rows,function(index,row){
					if(row.SELECTED){
						$('#dg').datagrid('selectRow', index);
					}
				}); 
			}
			
		});
		$('#dg').datagrid('removeFilterRule');
		$('#dg').datagrid('enableFilter', [{
			 field:'CODE',
			 type:'text',
			 fieldType:'p#S'
		},{
			 field:'NAME',
			 type:'text',
			 fieldType:'p#S'
		},{
			 field:'GENERICNAME',
			 type:'text',
			 fieldType:'p#S'
		},{
			 field:'DOSAGEFORMNAME',
			 type:'text',
			 fieldType:'p#S'
		},{
			 field:'PRODUCERNAME',
			 type:'text',
			 fieldType:'p#S'
		},{
			 field:'MODEL',
			 type:'text',
			 fieldType:'p#S'
		},{
			 field:'PACKDESC',
			 type:'text',
			 fieldType:'p#S'
		}]
		
		);
		
	});





	


	//=============ajax===============
	

	function addAjax(productCode){
		
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/dm/sicknessProduct/add.htmlx",
			data:{
				productCode : productCode,
				sicknessCode : $("#sicknessCode").val()
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
				//top.$.modalDialog.openner.datagrid('reload');
				//top.$.modalDialog.handler.dialog('close');
				showErr("出错，请刷新重新操作");
			}
		});	
	}

	function delAjax(productCode){
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/dm/sicknessProduct/del.htmlx",
			data:{
				productCode : productCode,
				sicknessCode : $("#sicknessCode").val()
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
				//top.$.modalDialog.openner.datagrid('reload');
				//top.$.modalDialog.handler.dialog('close');
				showErr("出错，请刷新重新操作");
			}
		});	
	}


	</script>	
	