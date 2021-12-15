package com.yhc.kakaoQuiz.model;

import java.math.BigDecimal;

public class DamboModel {

	String poliNo = "";		//계약번호
	String plicd = ""; 		//상품코드
	String damboCd = "";	//담보코드
	String damboNm = "";	//담보명
	BigDecimal bojangAmt = null;	//보장금액
	BigDecimal gijunAmt = null;	//기준금액
	BigDecimal prm = null;		//보험료
	
	String ipdtm = "";		//입력일시
	
	public String getPoliNo() {
		return poliNo == null ? "" : poliNo;
	}
	public void setPoliNo(String poliNo) {
		this.poliNo = poliNo;
	}
	public String getPlicd() {
		return plicd == null ? "" : plicd;
	}
	public void setPlicd(String plicd) {
		this.plicd = plicd;
	}
	public String getDamboCd() {
		return damboCd == null ? "" : damboCd;
	}
	public void setDamboCd(String damboCd) {
		this.damboCd = damboCd;
	}
	public String getDamboNm() {
		return damboNm == null ? "" : damboNm;
	}
	public void setDamboNm(String damboNm) {
		this.damboNm = damboNm;
	}
	public BigDecimal getBojangAmt() {
		return bojangAmt == null ? new BigDecimal("0") : bojangAmt;
	}
	public void setBojangAmt(BigDecimal bojangAmt) {
		this.bojangAmt = bojangAmt;
	}
	public BigDecimal getGijunAmt() {
		return gijunAmt  == null ? new BigDecimal("0") : gijunAmt;
	}
	public void setGijunAmt(BigDecimal gijunAmt) {
		this.gijunAmt = gijunAmt;
	}
	public BigDecimal getPrm() {
		return prm  == null ? new BigDecimal("0") : prm;
	}
	public void setPrm(BigDecimal prm) {
		this.prm = prm;
	}
	public String getIpdtm() {
		return ipdtm == null ? "" : ipdtm;
	}
	public void setIpdtm(String ipdtm) {
		this.ipdtm = ipdtm;
	}
	
	
	
	
	
	
	
}
