package com.yhc.kakaoQuiz.model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CntrModel {

	String prcMode = "";	//	 C : 저장, T: 보험료계산 , R :철회, R2 : 철회취소
	
	String poliNo = "";		//계약번호
	String stDt = "";		//시작일자
	String edDt = "";		//종료일자
	int cntrPeriod = 0;		//계약기간
	int maxCntrPeriod = 0;  //최대계약기간
	String plicd = "";		//상품코드
	String plinm = "";		//상품명
	String csStatCd = "";   //계약상태코드
	String csStatNm = "";   //계약상태
	String ipdtm = "";		//입력일시
	BigDecimal tprm = null;	//총보험료
	
	String[] daoboCd = null;
	ArrayList<DamboModel> damboList = null;
	
	public String getPrcMode() {
		return prcMode == null ? "" : prcMode;
	}
	public void setPrcMode(String prcMode) {
		this.prcMode = prcMode;
	}
	public String getPoliNo() {
		return poliNo == null ? "" : poliNo;
	}
	public void setPoliNo(String poliNo) {
		this.poliNo = poliNo;
	}
	public String getStDt() {
		return stDt == null ? "" : stDt;
	}
	public void setStDt(String stDt) {
		this.stDt = stDt;
	}
	public String getEdDt() {
		return edDt == null ? "" : edDt;
	}
	public void setEdDt(String edDt) {
		this.edDt = edDt;
	}
	public String getPlicd() {
		return plicd == null ? "" : plicd;
	}
	public void setPlicd(String plicd) {
		this.plicd = plicd;
	}
	public int getCntrPeriod() {
		return cntrPeriod;
	}
	public void setCntrPeriod(int cntrPeriod) {
		this.cntrPeriod = cntrPeriod;
	}
	
	public int getMaxCntrPeriod() {
		return maxCntrPeriod;
	}
	public void setMaxCntrPeriod(int maxCntrPeriod) {
		this.maxCntrPeriod = maxCntrPeriod;
	}
	public String getPlinm() {
		return plinm == null ? "" : plinm;
	}
	public void setPlinm(String plinm) {
		this.plinm = plinm;
	}
	public String getIpdtm() {
		return ipdtm == null ? "" : ipdtm;
	}
	public void setIpdtm(String ipdtm) {
		this.ipdtm = ipdtm;
	}
	public BigDecimal getTprm() {
		return tprm == null ? new BigDecimal("0") : tprm ;
	}
	public void setTprm(BigDecimal tprm) {
		this.tprm = tprm;
	}
	public String getCsStatCd() {
		return csStatCd == null ? "" : csStatCd;
	}
	public void setCsStatCd(String csStatCd) {
		this.csStatCd = csStatCd;
	}
	public String getCsStatNm() {
		if("01".equals(this.getCsStatCd())) {
			return "정상";
		}else if("02".equals(this.getCsStatCd())) {
			return "철회";
		}else if("03".equals(this.getCsStatCd())) {
			return "만료";
		}else {
			return "오류";
		}
		
		
	}
	public void setCsStatNm(String csStatNm) {
		this.csStatNm = csStatNm;
	}
	public String[] getDaoboCd() {
		return daoboCd ;
	}
	public void setDaoboCd(String[] daoboCd) {
		this.daoboCd = daoboCd;
	}
	public ArrayList<DamboModel> getDamboList() {
		return damboList == null ? new ArrayList<DamboModel>() : damboList;
	}
	public void setDamboList(ArrayList<DamboModel> damboList) {
		this.damboList = damboList;
	}
	
	
	
}
