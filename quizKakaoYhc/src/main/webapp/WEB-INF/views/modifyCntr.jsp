<%@page import="com.yhc.kakaoQuiz.model.DamboModel"%>
<%@page import="com.yhc.kakaoQuiz.model.CntrModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	String rtnMsg = (String)request.getAttribute("rtnMsg");
	rtnMsg = rtnMsg == null ? "" : rtnMsg;    
	
	CntrModel cntrModel = (CntrModel)request.getAttribute("cntrModel");
	System.out.println(cntrModel);
	System.out.println(cntrModel.getPlicd());
			
	
%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
	var rtnMsg = "<%=rtnMsg%>";
	window.onload = function(){
		if(rtnMsg != ""){
			alert(rtnMsg);
		}
	}
	
	function fn_doModify(){
		if(comfirm("변경하시겠습니까? ")){
			frm.action = "modifyCntr";
			frm.submit();
	}
	
</script>
<meta charset="UTF-8">
<title>계약 정보변경</title>
</head>
<body style="width: 1080">
	<form action="" name="frm" style="width: 1080">
		<input type="hidden" id="prcMode" value="C">
		<input type="hidden" id="poliNo" value="<%=cntrModel.getPoliNo() %>">
		
		
		<%if(cntrModel != null && !"".equals(cntrModel.getPlicd())){ %>
				<table>
				 	<colgroup>
						<col width="70%"/>
						<col width="*"/>
				 	</colgroup>
					<tr>
						<td></td>
						<td>
							<input type="button" width="80" value="계약변경" onclick="javascript:fn_Modify('C')">
						</td>
					</tr>
				</table>	
				<table style="border: 1">
				 	<colgroup>
						<col width="15%"/>
						<col width="10%"/>
						<col width="*"/>
						<col width="15%"/>
						<col width="15%"/>
						<col width="5%"/>
				 	</colgroup>
					<tr>
						<td>계약번호</td>
						<td>상품코드</td>
						<td>상품명</td>
						<td>계약시작일자</td>
						<td>계약종료일자</td>
						<td>계약기간</td>
						<td>총보험료</td>
					</tr>
					<tr>
						<td><%=cntrModel.getPoliNo() %></td>
						<td><%=cntrModel.getPlicd() %></td>
						<td><%=cntrModel.getPlinm() %></td>
						<td><%=cntrModel.getStDt() %></td>
						<td><%=cntrModel.getEdDt() %></td>
						<td><input type="text" name="cntrPeriod" value = "<%=cntrModel.getCntrPeriod() %>"></td>
						<td><%=cntrModel.getTprm() %></td>
					</tr>
				</table>
				
				<%if(cntrModel.getDamboList().size() > 0){ 
					for(int i = 0 ; i < cntrModel.getDamboList().size() ; i ++ ){
						DamboModel dm = cntrModel.getDamboList().get(i);
				%>
				
				<table>
				 	<colgroup>
						<col width="15%"/>
						<col width="*"/>
						<col width="15%"/>
						<col width="15%"/>
						<col width="15%"/>
				 	</colgroup>
					<tr>
						<td>담보코드</td>
						<td>담보명</td>
						<td>보장금액</td>
						<td>기준금액</td>
						<td>담보별보험료</td>
					</tr>
					<tr>
						<td><%=dm.getDamboCd()  %></td>
						<td><%=dm.getDamboNm()  %></td>
						<td><%=dm.getBojangAmt()  %></td>
						<td><%=dm.getGijunAmt()  %></td>
						<td><%=dm.getPrm()  %></td>
					</tr>
				</table>	
				
				<%	
					}
				}
				%>	
		
		<%}else{ %>
			<table style="border: 1">
				 	<colgroup>
						<col width="100%"/>
				 	</colgroup>
					<tr>
						<td>조회된 계약이 없습니다.</td>
					</tr>
				</table>
		<%} %>
		
	</form>
</body>
</html>
