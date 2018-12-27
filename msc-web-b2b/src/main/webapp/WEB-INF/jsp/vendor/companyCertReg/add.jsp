<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post" enctype="multipart/form-data">
		<table class="table-bordered">
		 	<tr>
				<th width="15%">证照图片：</th>
		   		<th width="85%" colspan=3>
		   			<div>
						<input type="file" style="width: 224px" name="pic" onchange="javascript:setImagePreview(this,localImag,preview);"/>
					</div>
					<div id="localImag">
						<%--预览，默认图片--%>
						<img id="preview" alt="" src="" style="width: 300px; height: 200px;" />
					</div>
		   		</th>
		   </tr>
		 	<tr>
				<th width="15%">公司名称：</th>
		   		<th width="35%">
		   			<input type="text" id="company" name="company.id" />
		   		</th>
		   		<th width="15%">证件类型：</th>
		   		<th width="35%">
		   			<input type="text" id="typeCode" name="typeCode" />
		   			<input type="hidden" name="typeName" value="" />
		   		</th>
		   </tr>
		   <tr>
				<th>证照代码：</th>
		   		<th>
		   			<input type="text" name="code" class="easyui-validatebox  easyui-textbox"  data-options="required:true" />
		   		</th>
		   		<th>证照名称：</th>
		   		<th>
		   			<input type="text" name="name" class="easyui-validatebox  easyui-textbox"  data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>发证日期：</th>
		   		<th>
		   			<input type="text" id="issueDate" name="issueDate" class="easyui-validatebox  easyui-textbox" />
		   		</th>
		   		<th>有效期截止：</th>
		   		<th>
		   			<input type="text" id="validDate" name="validDate" class="easyui-validatebox  easyui-textbox"  />
		   		</th>
		   </tr>
		   <tr>
				<th>发证部门：</th>
		   		<th>
		   			<input type="text" name="dept" class="easyui-validatebox  easyui-textbox"  />
		   		</th>
		   		<th>证照范围：</th>
		   		<th>
		   			<input type="text" name="scope" class="easyui-validatebox  easyui-textbox"  />
		   		</th>
		   </tr>
		   <tr>
				<th>备注：</th>
		   		<th colspan=3>
		   			<input type="text" name="note" class="easyui-validatebox  easyui-textbox" style="width:590px" />
		   		</th>
		   </tr>
		</table>
		<input type="hidden" name="id" value=""  >
	</form>


</div>

<script>

//检查图片的格式是否正确,同时实现预览
function setImagePreview(obj, localImagId, imgObjPreview) {
  	var array = new Array('gif', 'jpeg', 'png', 'jpg', 'bmp'); //可以上传的文件类型
  	if (obj.value == '') {
    	$.messager.alert("让选择要上传的图片!");
    	return false;
  	}else {
    	var fileContentType = obj.value.match(/^(.*)(\.)(.{1,8})$/)[3]; //这个文件类型正则很有用 
    	////布尔型变量
    	var isExists = false;
    	//循环判断图片的格式是否正确
    	for (var i in array) {
      		if (fileContentType.toLowerCase() == array[i].toLowerCase()) {
	        	//图片格式正确之后，根据浏览器的不同设置图片的大小
	        	if (obj.files && obj.files[0]) {
	        		if(obj.files[0].size > 512000){
	        			$.messager.alert('错误','图片大小不能大于500K!','info');
	              		return false;
	        		}
		          	//火狐下，直接设img属性 
		          	imgObjPreview.style.display = 'block';
		          	imgObjPreview.style.width = '300px';
		         	imgObjPreview.style.height = '200px';
		          	//火狐7以上版本不能用上面的getAsDataURL()方式获取，需要一下方式 
		          	imgObjPreview.src = window.URL.createObjectURL(obj.files[0]);
	        	}else {
		          	//IE下，使用滤镜 
		          	obj.select();
		          	obj.blur();
		          	var imgSrc = document.selection.createRange().text;
		          	//必须设置初始大小 
		          	localImagId.style.width = "300px";
		          	localImagId.style.height = "200px";
		          	//图片异常的捕捉，防止用户修改后缀来伪造图片 
	          		try {
	            		localImagId.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
	            		localImagId.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
	          		}catch (e) {
	            		$.messager.alert("您上传的图片格式不正确，请重新选择!");
	            		return false;
	          		}
	          		imgObjPreview.style.display = 'none';
	          		document.selection.empty();
        		}
        		isExists = true;
       	 		return true;
      		}
    	}
    	if (isExists == false) {
      		$.messager.alert("上传图片类型不正确!");
      		return false;
    	}
    	return false;
  	}
}

//初始化
$(function(){
	//日期
	$('#issueDate').datebox({
		editable:false
	});
	$('#validDate').datebox({
		editable:false
	});

	//下拉
	$('#company').combogrid({
		idField:'id',    
		textField:'fullName',
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		delay:800,
	    columns: [[
	        {field:'code',title:'公司编码',width:100},
	        {field:'fullName',title:'公司名称',width:280},
	        {field:'type',title:'公司类型',width:100,align:'center',
        		formatter: function(value,row,index){
        			var ss = ""
					if(row.isProducer == 1){
						ss += "厂商";
					}
					if(row.isVendor == 1){
						if(ss != ""){
							ss += "、";
						}
						ss += "供应商";
					}
					
        			return ss;
				}
			}
	    ]],
	    panelWidth:500,
		required: true,
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#company').combogrid('grid').datagrid("reload",{"query['t#fullName_S_LK']":q});
                $('#company').combogrid("setValue", q);
            }

        }
	});
	
	$('#typeCode').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'field1',    
	    textField:'field2', 
	    queryParams:{
	    	"attributeNo": "cert_type"
		},
		onChange: function(n,o){
			form1.typeName.value = $('#typeCode').combobox('getText');
        },
		required: true,
		editable:false
	}).combobox("initClear");
	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/vendor/companyCertReg/add.htmlx",
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
		success:function(result) {
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				top.$.modalDialog.openner.datagrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("新增成功！");
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