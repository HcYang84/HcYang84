<%@page import="java.util.ArrayList"%>
<%@page import="com.yhc.kakaoQuiz.model.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	ArrayList cntrList = (ArrayList)request.getAttribute("cntrList");
	if(cntrList == null ){
		cntrList = new ArrayList();
	}
	
	String rtnMsg = (String)request.getAttribute("rtnMsg");
		   rtnMsg = rtnMsg == null ? "" : rtnMsg;		
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
	
	function fn_new(){
		window.open("newCntr","신규계약등록","width=500;height=600");
	}
	
	function fn_view(poliNo){
		  var url    ="viewCntr.do?poliNo="+poliNo;
		  window.open(url,"계약상세조회","width=500;height=600");
	}
</script>
<meta charset="UTF-8">
<title>계약관리</title>
</head>
<body style="width: 1080">
	<form action="" name="frm" >
		<input type="hidden" id="poliNo" name="poliNo">
		<table>
		 	<colgroup>
				<col width="10%"/>
				<col width="*"/>
				<col width="10%"/>
				<col width="10%"/>
				<col width="10%"/>
				<col width="10%"/>
				<col width="15%"/>
				<col width="10%"/>
				<col width="5%"/>
		 	</colgroup>
			<tr>
				<td colspan="7"></td>
				<td>
					<input type="button" value="계약등록" onclick="javascript:fn_new()">
				</td>
			</tr>
		 	
			<tr>
				<td class="td_list">계약번호</td>
				<td class="td_list">상품정보</td>
				<td class="td_list">계약기간</td>
				<td class="td_list">보험시작일</td>
				<td class="td_list">보험종료일</td>
				<td class="td_list">총보험료</td>
				<td class="td_list">계약상태</td>
				<td class="td_list">상세</td>
			</tr>
			<%if(cntrList.size() > 0){ 
				for(int i = 0;i<cntrList.size() ; i ++){
					CntrModel cntrModel = new CntrModel();
					cntrModel = (CntrModel)cntrList.get(i);
			%>
			<tr>
				<td class="td_list"><%=cntrModel.getPoliNo() %></td>
				<td class="td_list"><%=cntrModel.getPlinm() %></td>
				<td class="td_list"><%=cntrModel.getCntrPeriod() %></td>
				<td class="td_list"><%=cntrModel.getStDt() %></td>
				<td class="td_list"><%=cntrModel.getEdDt() %></td>
				<td class="td_list"><%=cntrModel.getTprm() %></td>
				<td class="td_list"><%=cntrModel.getCsStatNm()%></td>
				<td class="td_list" align="center"><input type="button" value="상세" onclick="javascript:fn_view('<%=cntrModel.getPoliNo() %>')"> </td>
			</tr>
			<%} 
			}%>
			
		</table>
	</form>
	<form id="popupFrm">
		<input type="hidden" id="poliNo">
	</form>
</body>
</html>