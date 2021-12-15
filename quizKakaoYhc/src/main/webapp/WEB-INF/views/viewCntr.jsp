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
	
	function fn_Modify(prcMode){
		if(prcMode == "R"){
			if(confirm("계약을 철회하시겠습니까?")){
				frm.action = "cancelCntr";
				frm.prcMode.value = prcMode;
				alert(frm.poliNo.value);
				frm.submit();
			}
		}else if(prcMode == "R2"){
			if(confirm("계약철회를 취소하시겠습니까?")){
				frm.action = "cancelCntr";
				frm.prcMode.value = prcMode;
				alert(frm.poliNo.value);
				frm.submit();
			}
		}
	}
	
	function fn_delDambo(damboCd){
		if(confirm("해당 담보를 삭제하시겠습니까?")){
			frm.action = "delDambo";
			frm.damboCd.value = damboCd; 
			frm.submit();
		}
	}
	
	function fn_addDambo(){
		if(frm.addDamboCd.value==""){
			alert("추가할 담보코드를 입력해주세요");
			frm.damboCd.focus();
			return;
		}
		if(confirm("담보를 추가 하시겠습니까?")){
			frm.action = "addDambo";
			frm.damboCd.value = frm.addDamboCd.value; 
			frm.submit();
		}
	}
	
	function fn_changePeriod(){
		if(frm.cntrPeriod.value==""){
			alert("변경할 기간을 입력해주세요 ");
			frm.cntrPeriod.focus();
			return;
		}
		if(confirm("계약기간을 변경하시겠습니까?")){
			frm.action = "changeCntrPeriod";
			frm.submit();
		}
	}
	
	


	
</script>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<meta charset="UTF-8">
<title>계약 상세조회</title>
</head>
<body style="width: 1080">
	<form action="" name="frm" style="width: 1080">
		<input type="hidden" id="prcMode" name="prcMode">
		<input type="hidden" id="poliNo" name="poliNo" value="<%=cntrModel.getPoliNo() %>">
		<input type="hidden" id="damboCd" name="damboCd">
		<%if(cntrModel != null && !"".equals(cntrModel.getPlicd())){ %>
				<table style="border-color: black; border: 1">
				 	<colgroup>
						<col width="12%"/>
						<col width="12%"/>
						<col width="*"/>
						<col width="12%"/>
						<col width="12%"/>
						<col width="10%"/>
						<col width="15%"/>
						<col width="10%"/>
				 	</colgroup>
					<tr>
						<td colspan="7"></td>
						<td align="right">
							<%if("01".equals(cntrModel.getCsStatCd())){ %>
								<input type="button" width="80" value="계약철회" onclick="javascript:fn_Modify('R')">
							<%}else if("02".equals(cntrModel.getCsStatCd())){ %>
								<input type="button" width="80" value="철회취소" onclick="javascript:fn_Modify('R2')">
							<%} %>
						</td>
					</tr>
					<tr>
						<td colspan="1" align="left"> * 계약정보 </td>
						<td colspan="7" align="right">
							담보추가 : <input type="text" width="40" maxlength="5" name="addDamboCd">
							<input type="button" width="80" value="담보추가" onclick="javascript:fn_addDambo()">
							&nbsp;
							기간변경: <input type="text" width="40" maxlength="5" name="cntrPeriod">
							<input type="button" width="80" value="기간변경" onclick="javascript:fn_changePeriod()">
						</td>
					</tr>
					<tr>
						<td class="td_list">계약번호</td>
						<td class="td_list">상품코드</td>
						<td class="td_list">상품명</td>
						<td class="td_list">계약시작일자</td>
						<td class="td_list">계약종료일자</td>
						<td class="td_list">계약기간</td>
						<td class="td_list">총보험료</td>
						<td class="td_list">계약상태</td>
					</tr>
					<tr>
						<td class="td_list"><%=cntrModel.getPoliNo() %></td>
						<td class="td_list"><%=cntrModel.getPlicd() %></td>
						<td class="td_list"><%=cntrModel.getPlinm() %></td>
						<td class="td_list"><%=cntrModel.getStDt() %></td>
						<td class="td_list"><%=cntrModel.getEdDt() %></td>
						<td class="td_list"><%=cntrModel.getCntrPeriod() %></td>
						<td class="td_list"><%=cntrModel.getTprm() %></td>
						<td class="td_list"><%=cntrModel.getCsStatNm() %></td>
					</tr>
				</table>
				
				<table>
				<%if(cntrModel.getDamboList().size() > 0){ 
				%>
				 	<colgroup>
						<col width="15%"/>
						<col width="*"/>
						<col width="15%"/>
						<col width="15%"/>
						<col width="15%"/>
				 	</colgroup>
				 	<tr>
						<td colspan="5" align="left"> * 담보상세</td>
					</tr>
					<tr>
						<td class="td_list">담보코드</td>
						<td class="td_list">담보명</td>
						<td class="td_list">보장금액</td>
						<td class="td_list">기준금액</td>
						<td class="td_list">담보별보험료</td>
						<td class=""></td>
						
					</tr>
				<%
					for(int i = 0 ; i < cntrModel.getDamboList().size() ; i ++ ){
						DamboModel dm = cntrModel.getDamboList().get(i);
					%>
					<tr>
						<td class="td_list"><%=dm.getDamboCd()  %></td>
						<td class="td_list"><%=dm.getDamboNm()  %></td>
						<td class="td_list"><%=dm.getBojangAmt()  %></td>
						<td class="td_list"><%=dm.getGijunAmt()  %></td>
						<td class="td_list"><%=dm.getPrm() %></td>
						<td class="td_list"><input type="button" width="80" value="담보삭제" onclick="javascript:fn_delDambo('<%=dm.getDamboCd()  %>')"></td>
					</tr>
				
				<%	
					}
				}
				%>	
			</table>	
		
		<%}else{ %>
			<table>
				 	<colgroup>
						<col width="100%"/>
				 	</colgroup>
					<tr>
						<td class="td_list">조회된 계약이 없습니다.</td>
					</tr>
				</table>
		<%} %>
		
	</form>
</body>
</html>
