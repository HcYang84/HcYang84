<%@page import="com.yhc.kakaoQuiz.model.DamboModel"%>
<%@page import="com.yhc.kakaoQuiz.model.CntrModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	String rtnMsg = (String)request.getAttribute("rtnMsg");
	rtnMsg = rtnMsg == null ? "" : rtnMsg;    
	
	CntrModel cntrModel = (CntrModel)request.getAttribute("cntrModel");
	      if(cntrModel == null ) {cntrModel = new CntrModel();}
			
	
%>
<!DOCTYPE html>
<html>
<head>

<style type="text/css">
	body {
		margin: 0;
		padding: 0;
	}
	
	.td_list{
	  border: 1px solid black;
	  margin: 0;
	  padding: 0;
	}
	
</style>

<script type="text/javascript">

	var rtnMsg = "<%=rtnMsg%>";
	
	window.onload = function(){
		if(rtnMsg != ""){
			alert(rtnMsg);
		}
	}

	function fn_Save(){
		frm.submit();
	}
	
	function fn_view(poliNo){
		  var url    ="viewCntr.do?poliNo="+poliNo;
		  window.open(url,"계약상세조회","width=500;height=600");
	}
	
	function fn_Save(prcMode){
		frm.prcMode.value = prcMode;
		if(prcMode == "C"){
			frm.action = "createCntr";
			if(confirm("저장하시겠습니까")){
				frm.submit();
			}
		}else{
			frm.action = "calculatePrm";
			frm.submit();
		}
	}
</script>
<meta charset="UTF-8">
<title>계약 신규등록</title>
</head>
<body style="width: 1080">
	<form action="" name="frm" >
		<input type="hidden" id="prcMode" name="prcMode">
		<table>
		 	<colgroup>
				<col width="*"/>
				<col width="140"/>
		 	</colgroup>
			<tr>
				<td></td>
				<td>
					<input type="button" width="80" value="보험료계산" onclick="javascript:fn_Save('T')">
					<input type="button" width="80" value="저장" onclick="javascript:fn_Save('C')">
				</td>
			</tr>
			
		</table>
						
	
		<table>
		 	<colgroup>
				<col width="140"/>
				<col width="140"/>
				<col width="140"/>
				<col width="140"/>
				<col width="140"/>
		 	</colgroup>
			<tr>
				<td>계약시작일<input type="text" width="80" maxlength="8" name="stDt" value=<%=cntrModel.getStDt() %>></td>
				<td>계약기간 <input type="text" width="80" maxlength="6" name="cntrPeriod" value=<%=cntrModel.getPlicd() %>></td>
				<td>상품코드<input type="text" width="80" maxlength="5" name="plicd" value=<%=cntrModel.getPlicd() %>></td>
				<td>담보코드<input type="text" width="80" maxlength="5" name="damboCd"></td>
				<td>담보코드<input type="text" width="80" maxlength="5" name="damboCd"></td>
			</tr>
		</table>
		
		<%if(cntrModel != null && !"".equals(cntrModel.getPlicd())){ %>
	
				<table>
				 	<colgroup>
						<col width="140"/>
						<col width="140"/>
						<col width="140"/>
						<col width="140"/>
						<col width="140"/>
				 	</colgroup>
					<tr>
						<td>상품코드</td>
						<td>상품명</td>
						<td>계약시작일자</td>
						<td>계약종료일자</td>
						<td>계약기간</td>
						<td>총보험료</td>
					</tr>
					<tr>
						<td><%=cntrModel.getPlicd() %></td>
						<td><%=cntrModel.getPlinm() %></td>
						<td><%=cntrModel.getStDt() %></td>
						<td><%=cntrModel.getEdDt() %></td>
						<td><%=cntrModel.getCntrPeriod() %></td>
						<td><%=cntrModel.getTprm() %></td>
					</tr>
				</table>
				
				<%if(cntrModel.getDamboList().size() > 0){ 
					for(int i = 0 ; i < cntrModel.getDamboList().size() ; i ++ ){
						DamboModel dm = cntrModel.getDamboList().get(i);
				%>
				
				<table>
				 	<colgroup>
						<col width="140"/>
						<col width="140"/>
						<col width="140"/>
						<col width="140"/>
						<col width="140"/>
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
		
		<%} %>
		
	</form>
</body>
</html>