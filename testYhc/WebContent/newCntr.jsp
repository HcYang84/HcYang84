<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
	function fn_Save(){
		if(confirm("저장하시겠습니까")){
			alert(frm.fromYm.value);
		}
	}
</script>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body style="width: 1080">
	<form action="newCntr.do" name="frm" >
		<table>
		 	<colgroup>
				<col width="140"/>
				<col width="140"/>
				<col width="140"/>
				<col width="140"/>
				<col width="140"/>
		 	</colgroup>
			<tr>
				<td>계약시작년월 <input type="text" width="80" maxlength="6" name="fromYm"></td>
				<td>계약종료년월 <input type="text" width="80" maxlength="6" name="toYm"></td>
				<td>상품코드<input type="text" width="80" maxlength="3" name="productCd"></td>
				<td>담보코드<input type="text" width="80" maxlength="3" name="porductDtlCd"></td>
				<td><input type="button" width="80" value="생성" onclick="javascript:fn_Save()"></td>
			</tr>
			
		</table>
	</form>
</body>
</html>