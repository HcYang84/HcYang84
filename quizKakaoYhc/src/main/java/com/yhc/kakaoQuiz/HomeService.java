package com.yhc.kakaoQuiz;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.yhc.kakaoQuiz.model.CntrModel;
import com.yhc.kakaoQuiz.model.DamboModel;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeService {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeService.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	public String getCntrList(Model model) {
		
		ConnectionUtil connUtil = new ConnectionUtil();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		ArrayList resultList = new ArrayList();
		CntrModel cntrModel = null;
		
        try{
	        conn = connUtil.getConnection();
	        StringBuffer sb = new StringBuffer();
	        			 sb.append("SELECT A.POLI_NO,	");
	        			 sb.append("	A.PLICD,		");
	        			 sb.append("	B.PLINM,		");
	        			 sb.append("	A.STDT,			");
	        			 sb.append("	A.EDDT,			");
	        			 sb.append("	A.CNTR_PERIOD,	");
	        			 sb.append("	A.CS_STAT_CD,	");
	        			 sb.append("	A.TPRM,			");
	        			 sb.append("	A.IPDTM			");
	        			 sb.append("FROM yhc.tbct0001 A	");
	        			 sb.append("   , yhc.tbpd0001 B	");
	        			 sb.append("WHERE A.PLICD = B.PLICD");
	        			 
	        pstmt = conn.prepareStatement(sb.toString());
	        
	        System.out.println(sb.toString());
	        rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	cntrModel = new CntrModel();
	        	cntrModel.setPoliNo(rs.getString("POLI_NO"));
	        	cntrModel.setPlicd(rs.getString("PLICD"));
	        	cntrModel.setPlinm(rs.getString("PLINM"));
	        	cntrModel.setStDt(rs.getString("STDT"));
	        	cntrModel.setEdDt(rs.getString("EDDT"));
	        	cntrModel.setCntrPeriod(rs.getInt("CNTR_PERIOD"));
	        	cntrModel.setCsStatCd(rs.getString("CS_STAT_CD"));
	        	cntrModel.setTprm(rs.getBigDecimal("TPRM"));
	        	cntrModel.setIpdtm(rs.getString("IPDTM"));
	        	
	        	resultList.add(cntrModel);
			}
	        model.addAttribute("cntrList", resultList);
        			 
        }catch(SQLException e){
        	System.out.println("Sql수행중 오류발생 : " + e.getMessage());
        }
    finally{
        try{
            if( conn != null && !conn.isClosed()){
                conn.close();
            }
        }catch( SQLException e){
            e.printStackTrace();
        }
    }
        if(resultList != null && resultList.size()>0) {
        	model.addAttribute("rsMsg", resultList.size() +" 건이 조회되었습니다." );
        }else {
        	model.addAttribute("rsMsg", "조회된 내용이 없습니다." );
        }
		return "home";
	}
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	public String tmpCalculatePrm(Model model, CntrModel cntrModel) {
		logger.info("Service caculatePrm Start");
		
		ConnectionUtil connUtil = new ConnectionUtil();
		Connection conn = null;
		try {
			conn = connUtil.getConnection();
			
			String strErr = calculatePrm(cntrModel, conn);
			if(!"".equals(strErr)) {
				model.addAttribute("rtnMsg" , strErr);
				return "newCntr";
			}
			System.out.println("계산후 총 보험료 !!!! " + cntrModel.getTprm());
			
		}catch(SQLException se) {
			se.printStackTrace();
			model.addAttribute("rsMsg", "보험료 계산 중 오류가 발생했습니다. "+se.getMessage());
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
            try{
                if( conn != null && !conn.isClosed()){
                    conn.close();
                }
            }catch( SQLException e){
                e.printStackTrace();
            }
        }

        model.addAttribute("rsMsg", "계산되었습니다." );
		return "";
	}
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	public String createCntr(Model model, CntrModel cntrModel) {
		logger.info("Service createCntr Start");
		
		ConnectionUtil connUtil = new ConnectionUtil();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String prcMode = cntrModel.getPrcMode();
		String strErr ="";
		try {
			conn = connUtil.getConnection();
			
			strErr = calculatePrm(cntrModel, conn);
			System.out.println("strErr " + strErr);
			if(!"".equals(strErr)) {
				model.addAttribute("rtnMsg" , strErr);
				return "newCntr";
			}
			
			System.out.println("계산후 총 보험료 !!!! " + cntrModel.getTprm());
			
			//보험료 계산 저장하지 않고 return;
			if("T".equals(prcMode)) {
				return "";
			}
			//신규 계약번호 채번 ( insert )  추 후 오류발생시 삭제 
			String poliNo = getNewPoliNo(conn);
			
			if(poliNo != null && !"".equals(poliNo)) {
				System.out.println("신규계약번호 : " + poliNo);
			}else {
				return "";
			}
			cntrModel.setPoliNo(poliNo);
			
			// 보험원장 생성 
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE yhc.TBCT0001 			");
			sb.append("    SET PLICD = ?	 		");
			sb.append(" 	 , STDT = ? 			");
			sb.append(" 	 , EDDT = REPLACE(DATE_ADD(DATE_ADD(?, INTERVAL ? MONTH), INTERVAL -1 DAY),'-','') 			");
			sb.append(" 	 , CNTR_PERIOD = ? 		");
			sb.append(" 	 , CS_STAT_CD = ? 		");
			sb.append(" 	 , TPRM = ? 			");
			sb.append(" 	 , IPDTM = SYSDATE() 	");
			sb.append(" WHERE POLI_NO = ? 			");
			pstmt = conn.prepareStatement(sb.toString());
			int pstmtI = 1;
			pstmt.setString(pstmtI++, cntrModel.getPlicd());	//상품코드
			pstmt.setString(pstmtI++, cntrModel.getStDt());	//계약시작일자
			pstmt.setString(pstmtI++, cntrModel.getStDt());	//계약시작일자
			pstmt.setInt(pstmtI++, cntrModel.getCntrPeriod());	//계약기간
			pstmt.setInt(pstmtI++, cntrModel.getCntrPeriod());	//계약기간
			pstmt.setString(pstmtI++, "01");	//계약상태 01 정상 (최초계약시 정상)
			pstmt.setBigDecimal(pstmtI++, cntrModel.getTprm());	//총보험료
			pstmt.setString(pstmtI++, poliNo);	//계약번호
			pstmt.executeUpdate();
			pstmt.close();

			 
			
			
			StringBuffer insertDamboSql = new StringBuffer();
					insertDamboSql.append("   INSERT INTO yhc.tbct0002											");
					insertDamboSql.append("   ( POLI_NO, PLICD, DAMBO_CD, BOJANG_AMT, GIJUN_AMT, PRM, IPDTM )	");
					insertDamboSql.append("   VALUES (?,?,?,?,?,?, SYSDATE() )	");
			
			for(int i = 0 ; i < cntrModel.getDamboList().size() ; i++) {
				DamboModel dm = cntrModel.getDamboList().get(i);
				pstmtI = 1;
				pstmt = conn.prepareStatement(insertDamboSql.toString());
				pstmt.setString(pstmtI++, poliNo);
				pstmt.setString(pstmtI++, cntrModel.getPlicd());
				pstmt.setString(pstmtI++, dm.getDamboCd());
				pstmt.setBigDecimal(pstmtI++, dm.getBojangAmt());
				pstmt.setBigDecimal(pstmtI++, dm.getGijunAmt());
				pstmt.setBigDecimal(pstmtI++, dm.getPrm());
				pstmt.executeUpdate();
			}
			
		}catch(SQLException se) {
			se.printStackTrace();
			strErr = "계약등록중 오류가 발생했습니다";
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
            try{
                if( conn != null && !conn.isClosed()){
                    conn.close();
                }
            }catch( SQLException e){
                e.printStackTrace();
            }
        }

		return strErr;
	}

	public String getNewPoliNo(Connection conn) {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String newPoliNo = "";

		LocalDate now = LocalDate.now();
		String yearMonth = now.getYear() +""+ now.getMonthValue();
		
		try {
			String getNewPoliNoSql = "select cast(ifnull(MAX(POLI_NO),CONCAT(?,'0000'))+1 as char) AS NEW_POLI_NO from yhc.tbct0001 where poli_no like ? ";
			System.out.println(yearMonth);
			System.out.println(getNewPoliNoSql);
			pstmt = conn.prepareStatement(getNewPoliNoSql);
			pstmt.setString(1, yearMonth);
			pstmt.setString(2, yearMonth+"%");
			rs = pstmt.executeQuery();
			if(rs.next()) {
				newPoliNo = rs.getString("NEW_POLI_NO");
			}
			System.out.println("  newPoliNo  : " + newPoliNo);
			rs.close();
			pstmt.close();
			
			if(newPoliNo!=null && !"".equals(newPoliNo)) {
				String insertNewPolino = "INSERT INTO yhc.tbct0001 (POLI_NO) VALUES (?)";
				pstmt = conn.prepareStatement(insertNewPolino);
				pstmt.setString(1, newPoliNo);
				int result =  pstmt.executeUpdate();
				System.out.println("result"   + result);
				if(result < 1) {
					System.out.println("채번오류");
				}
			}
			pstmt.close();
		}catch (SQLException se) {
			se.printStackTrace();
			return "";
		}
		
		
		return newPoliNo;
		
	}
	
	public String calculatePrm(CntrModel cntrModel, Connection conn) throws SQLException {
		
		ConnectionUtil connUtil = new ConnectionUtil();
		
		String rtnVal = "";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		if(conn == null) {
			conn = connUtil.getConnection();
		}
		
		String chkPlicd = "SELECT PLICD, MAX_PERIOD FROM YHC.TBPD0001 WHERE PLICD = ? ";
		
		int maxPeriod = 0;
		if("".equals(cntrModel.getPlicd())) {
			return "상품코드를 입력해주세요.";
		}else {
			try {
				pstmt = conn.prepareStatement(chkPlicd);
				pstmt.setString(1, cntrModel.getPlicd());
				rs = pstmt.executeQuery();
				if(rs.next()) {
					maxPeriod = rs.getInt("MAX_PERIOD");
					rs.close();
				}else {
					rs.close();
					return "존재하지 않는 상품코드 입니다.";
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				return "오류발생"+e.getMessage();
			}finally {
				try {
					if(rs!=null) {rs.close();}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if(cntrModel.getCntrPeriod() < 1) {
			return "계약기간을 입력해주세요";
		}else {
			if(cntrModel.getCntrPeriod() > maxPeriod) {
				return "해당상품은 정해진 계약기간("+maxPeriod+"개월) 을 초과 할 수 없습니다.";
			}
		}	
		
		ArrayList<DamboModel> damboList = new ArrayList<DamboModel>();	//조회된 담보정보List
		
		BigDecimal sumPrm = new BigDecimal("0");							//담보별 보험료 합계
		BigDecimal cntrPeriod = new BigDecimal(cntrModel.getCntrPeriod());	//계약기간
		BigDecimal tPrm  = new BigDecimal("0");							//계산 총보험료
		if(cntrModel.getDaoboCd() == null || cntrModel.getDaoboCd().length < 1) {
			return "1개 이상의 담보를 입력해주세요.";	
		}
		
		if("".equals(cntrModel.getStDt())){
			return "계약시작일자를 입력해주세요.";
		}else {
			try{
		         SimpleDateFormat  dateFormat = new  SimpleDateFormat("yyyyMMdd");
		         dateFormat.setLenient(false);
		         dateFormat.parse(cntrModel.getStDt());

		    }catch (Exception  e){
		        return  "유효한 날자를 입력해주세요 ( 계약시작일 ) ." + cntrModel.getStDt();
		    }
		}
		
		if(cntrModel.getCntrPeriod() < 1) {
			return "계약기간을 입력해주세요";
		}
		
		System.out.println("cntrPeriod   : " + cntrPeriod);
		
		HashSet<String> hashSet = new HashSet<String>(Arrays.asList(cntrModel.getDaoboCd()));
		String[] resultArr = hashSet.toArray(new String[0]);

		System.out.println(" resultArr.length " + resultArr.length);
		System.out.println(" cntrModel.getDaoboCd() " + cntrModel.getDaoboCd().length );
		if(cntrModel.getDaoboCd().length != resultArr.length) {
			return "동일한 담보를 두개이상 등록 할 수 없습니다. ";
		}
		//담보별 보험료 계산
		for(int i = 0 ; i < cntrModel.getDaoboCd().length ; i ++) {
			//TODO 보험료 계산모듈과 계약저장모듈 분기처리 하기..
			StringBuffer chkDamboSql = new StringBuffer();
			chkDamboSql.append("  SELECT A.PLICD, 								");
			chkDamboSql.append("  		 A.DAMBO_CD,							");
			chkDamboSql.append("  		 A.DAMBO_NM,							");
			chkDamboSql.append("  		 IFNULL(A.BOJANG_AMT,0) AS BOJANG_AMT,	");
			chkDamboSql.append("  		 IFNULL(A.GIJUN_AMT,0) AS GIJUN_AMT,	");
			chkDamboSql.append("  		 TRUNCATE((A.BOJANG_AMT / A.GIJUN_AMT) , 2) AS PRM	");
			chkDamboSql.append("  FROM YHC.TBPD0002 A 			");
			chkDamboSql.append(" WHERE PLICD = ? 				");
			chkDamboSql.append("   AND DAMBO_CD = ? 			");
			System.out.println(cntrModel.getDaoboCd()[i]);
			pstmt = conn.prepareStatement(chkDamboSql.toString());
			pstmt.setString(1, cntrModel.getPlicd());
			pstmt.setString(2, cntrModel.getDaoboCd()[i]);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				DamboModel dm = new DamboModel();
				dm.setDamboCd(rs.getString("DAMBO_CD"));
				dm.setDamboNm(rs.getString("DAMBO_NM"));
				dm.setBojangAmt(rs.getBigDecimal("BOJANG_AMT"));
				dm.setGijunAmt(rs.getBigDecimal("GIJUN_AMT"));
				dm.setPrm(rs.getBigDecimal("PRM"));			//담보별 보험료 
				BigDecimal prm = rs.getBigDecimal("PRM");
				sumPrm = sumPrm.add(prm);
				System.out.println("   sumPrm  : " + sumPrm);
				damboList.add(dm);
			}else {
				return "존재하지 않는 담보코드 " + cntrModel.getDaoboCd()[i];
			}
		}
			
		tPrm = sumPrm.multiply(cntrPeriod);
		cntrModel.setDamboList(damboList);
		System.out.println(" tPrm : " + tPrm);
		cntrModel.setTprm(tPrm);
		
		
		return rtnVal;
	}
	
	
	//변경 후 재계산
	public String reCalculatePrm(CntrModel cntrModel, Connection conn) throws SQLException {
		
		ConnectionUtil connUtil = new ConnectionUtil();
		
		String rtnVal = "";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		if(conn == null) {
			conn = connUtil.getConnection();
		}
		
		
		//담보별 보험료 재 계산
			//TODO 보험료 계산모듈과 계약저장모듈 분기처리 하기..
			StringBuffer getDamboDtlSql = new StringBuffer();
						 getDamboDtlSql.append("	SELECT A.POLI_NO                   							");
						 getDamboDtlSql.append("         , A.PLICD                  							");
						 getDamboDtlSql.append("         , B.DAMBO_CD               							");
						 getDamboDtlSql.append("         , C.DAMBO_NM               							");
						 getDamboDtlSql.append("  		 , IFNULL(C.BOJANG_AMT,0) AS BOJANG_AMT					");
						 getDamboDtlSql.append("  		 , IFNULL(C.GIJUN_AMT,0) AS GIJUN_AMT					");
						 getDamboDtlSql.append("  		 , TRUNCATE((C.BOJANG_AMT / C.GIJUN_AMT) , 2) AS PRM	");
						 getDamboDtlSql.append("  		 , A.CNTR_PERIOD										");        
						 getDamboDtlSql.append("         , A.IPDTM                  							");
						 getDamboDtlSql.append("      FROM yhc.tbct0001 A           							");
						 getDamboDtlSql.append("         , yhc.tbct0002 B           							");
						 getDamboDtlSql.append("         , yhc.tbpd0002 C           							");
						 getDamboDtlSql.append("    WHERE A.POLI_NO = ?											");    
						 getDamboDtlSql.append("      AND A.POLI_NO = B.POLI_NO         						");
						 getDamboDtlSql.append("      AND B.PLICD = C.PLICD         							");
						 getDamboDtlSql.append("      AND B.DAMBO_CD = C.DAMBO_CD   							");
						 
			pstmt = conn.prepareStatement(getDamboDtlSql.toString());
			pstmt.setString(1, cntrModel.getPoliNo());
			rs = pstmt.executeQuery();
			BigDecimal sumPrm = new BigDecimal("0");
			while(rs.next()) {
				DamboModel dm = new DamboModel();
				dm.setDamboCd(rs.getString("DAMBO_CD"));
				dm.setDamboNm(rs.getString("DAMBO_NM"));
				dm.setBojangAmt(rs.getBigDecimal("BOJANG_AMT"));
				dm.setGijunAmt(rs.getBigDecimal("GIJUN_AMT"));
				dm.setPrm(rs.getBigDecimal("PRM"));
				sumPrm = sumPrm.add(rs.getBigDecimal("PRM").multiply(rs.getBigDecimal("CNTR_PERIOD")));
				System.out.println("   sumPrm  : " + sumPrm);
			}
			
			rs.close();
			
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE TBCT0001 				");
			sb.append("    SET TPRM = ?	 				");		// 총보험료 
			sb.append(" 	 , MODIFY_DTM = SYSDATE() 	");		// 수정일자
			sb.append(" WHERE POLI_NO = ? 				");
			
			pstmt = conn.prepareStatement(sb.toString());
			int pstmtI = 1;
			pstmt.setBigDecimal(pstmtI++, sumPrm);	//총보험료
			pstmt.setString(pstmtI++, cntrModel.getPoliNo());	//계약번호
			pstmt.executeUpdate();
			pstmt.close();
		
		return rtnVal;
	}
	
	
	public String viewCntr(Model model, CntrModel cntrModel) {
		logger.info("Service viewCntr Start");
		
		ConnectionUtil connUtil = new ConnectionUtil();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String prcMode = cntrModel.getPrcMode();
		if("".equals(cntrModel.getPoliNo())) {
			return "조회할 계약번호를 입력해주세요";
		}
		
		try {
			conn = connUtil.getConnection();
			
			// 계약조회 
			StringBuffer getCntrSql = new StringBuffer();
						 getCntrSql.append("	SELECT A.POLI_NO                  ");
						 getCntrSql.append("         , A.PLICD                 ");
						 getCntrSql.append("         , A.STDT                  ");
						 getCntrSql.append("         , A.EDDT                  ");
						 getCntrSql.append("         , A.CNTR_PERIOD           ");
						 getCntrSql.append("         , A.CS_STAT_CD            ");
						 getCntrSql.append("         , A.TPRM                  ");
						 getCntrSql.append("         , A.IPDTM                 ");
						 getCntrSql.append("         , B.PLINM                 ");
						 getCntrSql.append("         , B.MAX_PERIOD            ");
						 getCntrSql.append("      FROM yhc.tbct0001 A          ");
						 getCntrSql.append("         , yhc.tbpd0001 B          ");
						 getCntrSql.append("    WHERE A.POLI_NO = ?				");
						 getCntrSql.append("      AND A.PLICD = B.PLICD        ");
						 
			pstmt = conn.prepareStatement(getCntrSql.toString());
			pstmt.setString(1, cntrModel.getPoliNo());	//상품코드
			rs = pstmt.executeQuery();
			if(rs.next()) {
				cntrModel.setPoliNo(rs.getString("POLI_NO"));
				cntrModel.setPlicd(rs.getString("PLICD"));
				cntrModel.setStDt(rs.getString("STDT"));
				cntrModel.setEdDt(rs.getString("EDDT"));
				cntrModel.setCntrPeriod(rs.getInt("CNTR_PERIOD"));
				cntrModel.setCsStatCd(rs.getString("CS_STAT_CD"));
				cntrModel.setTprm(rs.getBigDecimal("TPRM"));
				cntrModel.setIpdtm(rs.getString("IPDTM"));
				cntrModel.setPlinm(rs.getString("PLINM"));
				cntrModel.setMaxCntrPeriod(rs.getInt("MAX_PERIOD"));
			}else {
				pstmt.close();
				return "계약정보를 조회하지 못했습니다. " + cntrModel.getPoliNo();
			}
			
			pstmt.close();
			
			//담보 상세조회
			StringBuffer getDamboDtlSql = new StringBuffer();
						 getDamboDtlSql.append("	SELECT A.POLI_NO                ");
						 getDamboDtlSql.append("         , A.PLICD                  ");
						 getDamboDtlSql.append("         , A.DAMBO_CD               ");
						 getDamboDtlSql.append("         , B.DAMBO_NM               ");
						 getDamboDtlSql.append("         , A.BOJANG_AMT             ");
						 getDamboDtlSql.append("         , A.GIJUN_AMT              ");
						 getDamboDtlSql.append("         , A.PRM                    ");
						 getDamboDtlSql.append("         , A.IPDTM                  ");
						 getDamboDtlSql.append("      FROM yhc.tbct0002 A           ");
						 getDamboDtlSql.append("         , yhc.tbpd0002 B           ");
						 getDamboDtlSql.append("    WHERE A.POLI_NO = ?				");
						 getDamboDtlSql.append("      AND A.PLICD = B.PLICD         ");
						 getDamboDtlSql.append("      AND A.DAMBO_CD = B.DAMBO_CD   ");
			
			pstmt = conn.prepareStatement(getDamboDtlSql.toString());
			pstmt.setString(1, cntrModel.getPoliNo());	//상품코드
			rs = pstmt.executeQuery();
			ArrayList<DamboModel> damboList = new ArrayList<DamboModel>();	//조회된 담보정보List
			while (rs.next()){
				DamboModel dm = new DamboModel();
				dm.setPoliNo(rs.getString("POLI_NO"));
				dm.setDamboCd(rs.getString("DAMBO_CD"));
				dm.setDamboNm(rs.getString("DAMBO_NM"));
				dm.setBojangAmt(rs.getBigDecimal("BOJANG_AMT"));
				dm.setGijunAmt(rs.getBigDecimal("GIJUN_AMT"));
				dm.setPrm(rs.getBigDecimal("PRM"));
				dm.setIpdtm(rs.getString("IPDTM"));
				damboList.add(dm);
			}
			cntrModel.setDamboList(damboList);
			
		}catch(SQLException se) {
			se.printStackTrace();
			model.addAttribute("rsMsg", "계약조회중 오류가 발생했습니다. "+se.getMessage());
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
            try{
                if( conn != null && !conn.isClosed()){
                    conn.close();
                }
            }catch( SQLException e){
                e.printStackTrace();
            }
        }
		model.addAttribute("cntrModel", cntrModel);
        model.addAttribute("rsMsg", "조회되었습니다." );
		return "";
	}
	
	
	public String cancelCntr(Model model, CntrModel cntrModel) {
		logger.info("Service cancelCntr Start");
		
		ConnectionUtil connUtil = new ConnectionUtil();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String prcMode = cntrModel.getPrcMode();
		try {
			conn = connUtil.getConnection();
			
			String poliNo = cntrModel.getPoliNo();
			System.out.println(" **** 철회 계약번호  : " + poliNo);
			if("".equals(poliNo)) {
				return "철회할 계약번호를 입력해주세요";
			}
			
			// 보험원장 상태변경  
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE YHC.TBCT0001				");
			sb.append("    SET CS_STAT_CD = ?	 		");		// 계약상태 
			sb.append(" 	 , MODIFY_DTM = SYSDATE() 	");		// 수정일자
			sb.append(" WHERE POLI_NO = ? 				");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			int pstmtI = 1;
			pstmt.setString(pstmtI++, "R".equals(prcMode) ? "02" : "01");	//계약상태 02 철회 , 01 정상
			pstmt.setString(pstmtI++, poliNo);	//계약번호
			pstmt.executeUpdate();
			pstmt.close();

			
		}catch(SQLException se) {
			se.printStackTrace();
			model.addAttribute("rsMsg", "계약 철회 처리 중 오류가 발생했습니다. "+se.getMessage());
		}catch(Exception e) {
			e.printStackTrace();
			
		}finally{
            try{
                if( conn != null && !conn.isClosed()){
                    conn.close();
                }
            }catch( SQLException e){
                e.printStackTrace();
            }
        }

        model.addAttribute("rsMsg", "저장되었습니다." );
		return "home";
	}
	
	
	public String delDambo(Model model, CntrModel cntrModel) {
		logger.info("Service delDambo Start");
		
		ConnectionUtil connUtil = new ConnectionUtil();
		Connection conn = null;
		PreparedStatement pstmt = null;
		String strErr = "";
		try {
			conn = connUtil.getConnection();
			
			String poliNo = cntrModel.getPoliNo();
			System.out.println(" **** 담보삭제대상 계약번호  : " + poliNo);
			
			if("".equals(poliNo)) {
				return "철회할 계약번호를 입력해주세요";
			}
			if(cntrModel.getDaoboCd() == null || cntrModel.getDaoboCd().length < 1) {
				return "삭제할 담보코드를 입력해주세요.";
			}
			
			StringBuffer deleteDamboSql = new StringBuffer();
			deleteDamboSql.append("   DELETE FROM yhc.tbct0002	");
			deleteDamboSql.append("   WHERE POLI_NO = ? 	");
			deleteDamboSql.append("     AND DAMBO_CD = ? 	");
	
			for(int i = 0 ; i < cntrModel.getDaoboCd().length ; i++) {
				int pstmtI = 1;
				pstmt = conn.prepareStatement(deleteDamboSql.toString());
				pstmt.setString(pstmtI++, poliNo);
				pstmt.setString(pstmtI++, cntrModel.getDaoboCd()[i]);
				pstmt.executeUpdate();
			}
			strErr = reCalculatePrm(cntrModel, conn);
			viewCntr(model, cntrModel);	//재조회
			
		}catch(SQLException se) {
			se.printStackTrace();
			strErr = "담보 삭제  처리 중 오류가 발생했습니다. ";
		}catch(Exception e) {
			e.printStackTrace();
			strErr = "담보 삭제  처리 중 오류가 발생했습니다. ";
		}finally{
            try{
                if( conn != null && !conn.isClosed()){
                    conn.close();
                }
            }catch( SQLException e){
                e.printStackTrace();
            }
        }
		return "";
	}
	
	
	
	public String addDambo(Model model, CntrModel cntrModel) {
		logger.info("Service delDambo Start");
		
		ConnectionUtil connUtil = new ConnectionUtil();
		Connection conn = null;
		PreparedStatement pstmt = null;
		String strErr = "";
		try {
			conn = connUtil.getConnection();
			
			String poliNo = cntrModel.getPoliNo();
			System.out.println(" **** 담보 추가 대상 계약번호  : " + poliNo);
			
			if("".equals(poliNo)) {
				return "추가할 계약번호를 입력해주세요";
			}
			if(cntrModel.getDaoboCd() == null || cntrModel.getDaoboCd().length != 1) {
				return "추가할 담보코드를 입력해주세요.";
			}
			
			StringBuffer insertDamboSql = new StringBuffer();
						insertDamboSql.append("   INSERT INTO yhc.tbct0002											");
						insertDamboSql.append("   ( POLI_NO, PLICD, DAMBO_CD, BOJANG_AMT, GIJUN_AMT, PRM, IPDTM )	");
						insertDamboSql.append("  SELECT ? , 														");
						insertDamboSql.append("         ? , 														");
						insertDamboSql.append("  		A.DAMBO_CD,													");
						insertDamboSql.append("  		IFNULL(A.BOJANG_AMT,0) AS BOJANG_AMT,						");
						insertDamboSql.append("  		IFNULL(A.GIJUN_AMT,0) AS GIJUN_AMT,							");
						insertDamboSql.append("  		TRUNCATE((A.BOJANG_AMT / A.GIJUN_AMT) , 2) AS PRM,			");
						insertDamboSql.append("         SYSDATE() 													");
						insertDamboSql.append("  FROM YHC.TBPD0002 A 			");
						insertDamboSql.append(" WHERE PLICD = ? 				");
						insertDamboSql.append("   AND DAMBO_CD = ? 				");
	
					int pstmtI = 1;
					pstmt = conn.prepareStatement(insertDamboSql.toString());
					pstmt.setString(pstmtI++, poliNo);
					pstmt.setString(pstmtI++, cntrModel.getPlicd());
					pstmt.setString(pstmtI++, cntrModel.getPlicd());
					pstmt.setString(pstmtI++, cntrModel.getDaoboCd()[0]);
					if(pstmt.executeUpdate() < 1) {
						strErr = "존재하 않는 담보코드 입니다." ;
						pstmt.close();
						return strErr; 
					}
					pstmt.close();
	
			strErr = reCalculatePrm(cntrModel, conn);
			viewCntr(model, cntrModel);	//재조회
			
		}catch(SQLException se) {
			System.out.println(se.getSQLState());
			System.out.println(se.getMessage());
			se.printStackTrace();
			model.addAttribute("rsMsg", "담보 추가 처리 중 오류가 발생했습니다. "+se.getMessage());
			strErr = "담보 추가 처리 중 오류가 발생했습니다";
			if("23000".equals(se.getSQLState())) {
				strErr = "동일한 담보코드를 추가할 수 없습니다.";
			}
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("rsMsg", "담보 추가  처리 중 오류가 발생했습니다. "+e.getMessage());
		}finally{
            try{
            	pstmt.close();
                conn.close();
            }catch( Exception e){
                e.printStackTrace();
            }
        }
		return strErr;
	}
	
	
	public String changeCntrPeriod(Model model, CntrModel cntrModel) {
		logger.info("Service changeCntrPeriod Start");
		
		ConnectionUtil connUtil = new ConnectionUtil();
		Connection conn = null;
		PreparedStatement pstmt = null;
		String strErr = "";
		try {
			conn = connUtil.getConnection();
			
			String poliNo = cntrModel.getPoliNo();
			System.out.println(" **** 계약기간 변경 대상 계약번호  : " + poliNo);
			
			if("".equals(poliNo)) {
				return "변경할 계약번호를 입력해주세요";
			}
			System.out.println("cntrModel.getCntrPeriod()cntrModel.getCntrPeriod()cntrModel.getCntrPeriod() :" + cntrModel.getCntrPeriod());
			if(cntrModel.getCntrPeriod() < 1) {
				return "최소 계약기간은 1개월 입니다.";
			}
			
				// 계약기간변경   
				StringBuffer sb = new StringBuffer();
				sb.append(" UPDATE YHC.TBCT0001				");
				sb.append("    SET CNTR_PERIOD = ?	 		");		// 계약기간
				sb.append(" 	 , EDDT = REPLACE(DATE_ADD(DATE_ADD(STDT, INTERVAL ? MONTH), INTERVAL -1 DAY),'-','') 			");
				sb.append(" 	 , MODIFY_DTM = SYSDATE() 	");		// 수정일자
				sb.append(" WHERE POLI_NO = ? 				");
						
					pstmt = conn.prepareStatement(sb.toString());
	
					int pstmtI = 1;
					pstmt = conn.prepareStatement(sb.toString());
					pstmt.setInt(pstmtI++, cntrModel.getCntrPeriod());
					pstmt.setInt(pstmtI++, cntrModel.getCntrPeriod());
					pstmt.setString(pstmtI++, poliNo);
					if(pstmt.executeUpdate() < 1) {
						strErr = "변경에 실패했습니다." ;
						pstmt.close();
						return strErr; 
					}
					pstmt.close();
	
			strErr = reCalculatePrm(cntrModel, conn);
			viewCntr(model, cntrModel);	//재조회
			
		}catch(SQLException se) {
			se.printStackTrace();
			model.addAttribute("rsMsg", "계약기간 변경중 오류가 발생했습니다. "+se.getMessage());
			strErr = "계약기간 변경중 오류가 발생했습니다";
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("rsMsg", "담보 추가  처리 중 오류가 발생했습니다. "+e.getMessage());
			strErr = "계약기간 변경중 오류가 발생했습니다";
		}finally{
            try{
            	if(pstmt !=null) {pstmt.close();}
                if(conn !=null) {conn.close();}
            }catch( Exception e){
                e.printStackTrace();
            }
        }
		return strErr;
	}
	
}
