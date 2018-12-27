<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="single-dg">
	<table  id="dg" ></table>
</div>


<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height : 380,
		pagination:true,
		url:"<c:out value='${pageContext.request.contextPath }'/>/supervise/medicine/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'药品编码',width:100,align:'center'},
		        	{field:'name',title:'药品名称',width:100,align:'center'},
		        	{field:'dosageFormName',title:'剂型',width:100,align:'center'},
		        	{field:'producerName',title:'生产企业',width:100,align:'center'},
		        	{field:'model',title:'规格',width:100,align:'center'},
		        	{field:'packDesc',title:'包装规格',width:100,align:'center'},
		        	{field:'auxiliaryType',title:'是否辅助药品',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png' />";
							}
						}
		        	},
		        	{field:'authorizeNo',title:'国药准字',width:100,align:'center'},
		        	{field:'importFileNo',title:'注册证号',width:100,align:'center'},
		        	{field:'isGPOPurchase',title:'是否GPO药品',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.isGPOPurchase==1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
					}},
		        	{field:'gpoName',title:'GPO名称',width:100,align:'center'},
		        	{field:'baseDrugType',title:'是否基药',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.baseDrugType != undefined&&row.baseDrugType!=null){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}},
		        	{field:'absDrugType',title:'是否抗菌药物',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.absDrugType != undefined&&row.absDrugType!=null){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}},
		        	{field:'isUrgent',title:'是否急救药品',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.isUrgent==1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}},
		        	{field:'isDisabled',title:'是否禁用',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png' />";
							}
						}
		        	}
		   		]],
				onLoadSuccess: function(data) {
					 $.each(data.rows,function(index,row){
						 console.log(row);
						if(row.projectCode == "1"){
							$('#dg').datagrid('selectRow', index);
						}
						if(row.auxiliaryType==1){
							$('#dg').datagrid('selectRow', index);
						}
					}); 
				},
		   		onClickRow:function(index,row) {
					var selobjs = $('#dg').datagrid('getSelections');
		  			var isSelected = 0;
		  			$.each(selobjs,function(index){
		  				if(this.id == row.id){
		  					isSelected = 1;
		  					return false;
		  				}
		  			});
		  			if(isSelected){
		  				setAuxiliary(row,1);
		  			}else{
		  				setAuxiliary(row,0);
		  			}
		   		}
	});

	$('#dg').datagrid('enableFilter', [{
        field:'isDisabled',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'未禁用'},
                  {value:'1',text:'禁用'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isDisabled');
                } else {
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
	},{
        field:'isGPOPurchase',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isGPOPurchase');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isGPOPurchase',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'baseDrugType',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'baseDrugType');
                } else {
                	var op = (value == "1"?"NOT":"IS");
                	$('#dg').datagrid('addFilterRule', {
                        field: 'baseDrugType',
                        op: op,
                        fieldType:'NULL',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'absDrugType',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'absDrugType');
                } else {
                	var op = (value == "1"?"NOT":"IS");
                	$('#dg').datagrid('addFilterRule', {
                        field: 'absDrugType',
                        op: op,
                        fieldType:'NULL',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'isUrgent',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isUrgent');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isUrgent',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'auxiliaryType',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'auxiliaryType');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'auxiliaryType',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	}]
	
	);
	
});


//=============ajax===============
function setAuxiliary(row, isAuxiliary){
	var selrow = $('#dg').datagrid('getSelected');
	$.ajax({
		url:"<c:out value='${pageContext.request.contextPath }'/>/supervise/medicine/setAuxiliary.htmlx",
		data:{
			"productId":row.id,
			"isAuxiliary":isAuxiliary
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				if(isAuxiliary==1){
					showMsg("新增成功！");
				}else{
					showMsg("删除成功！");
				}
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