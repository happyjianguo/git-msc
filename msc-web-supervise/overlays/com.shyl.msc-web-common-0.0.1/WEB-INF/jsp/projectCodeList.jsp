<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
		<c:forEach items = "${list}" var = "map" > 
			<div style="float:left;width:24%;text-align: center;height:40px;    background-color: #f5f9ff;    border-right: 1px solid #ddd;border-bottom: 1px solid #ddd;">
				<a id="btn" href="javascript:choosePJ('${map.id}','${map.name}')" class="easyui-linkbutton" data-options="plain:true,width: '100%',height: '100%'">${map.name}</a>
			</div>
		</c:forEach> 
		<input type="hidden" name="id" value=""  >
</div>



<script>
function choosePJ(code,name){
	top.$.modalDialog.handler.dialog('close');
	top.changePJC(code,name);
}

</script>