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
        	System.out.println("Sql????????? ???????????? : " + e.getMessage());
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
        	model.addAttribute("rsMsg", resultList.size() +" ?????? ?????????????????????." );
        }else {
        	model.addAttribute("rsMsg", "????????? ????????? ????????????." );
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
			System.out.println("????????? ??? ????????? !!!! " + cntrModel.getTprm());
			
		}catch(SQLException se) {
			se.printStackTrace();
			model.addAttribute("rsMsg", "????????? ?????? ??? ????????? ??????????????????. "+se.getMessage());
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

        model.addAttribute("rsMsg", "?????????????????????." );
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
			
			System.out.println("????????? ??? ????????? !!!! " + cntrModel.getTprm());
			
			//????????? ?????? ???????????? ?????? return;
			if("T".equals(prcMode)) {
				return "";
			}
			//?????? ???????????? ?????? ( insert )  ??? ??? ??????????????? ?????? 
			String poliNo = getNewPoliNo(conn);
			
			if(poliNo != null && !"".equals(poliNo)) {
				System.out.println("?????????????????? : " + poliNo);
			}else {
				return "";
			}
			cntrModel.setPoliNo(poliNo);
			
			// ???????????? ?????? 
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
			pstmt.setString(pstmtI++, cntrModel.getPlicd());	//????????????
			pstmt.setString(pstmtI++, cntrModel.getStDt());	//??????????????????
			pstmt.setString(pstmtI++, cntrModel.getStDt());	//??????????????????
			pstmt.setInt(pstmtI++, cntrModel.getCntrPeriod());	//????????????
			pstmt.setInt(pstmtI++, cntrModel.getCntrPeriod());	//????????????
			pstmt.setString(pstmtI++, "01");	//???????????? 01 ?????? (??????????????? ??????)
			pstmt.setBigDecimal(pstmtI++, cntrModel.getTprm());	//????????????
			pstmt.setString(pstmtI++, poliNo);	//????????????
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
			strErr = "??????????????? ????????? ??????????????????";
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
					System.out.println("????????????");
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
			return "??????????????? ??????????????????.";
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
					return "???????????? ?????? ???????????? ?????????.";
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				return "????????????"+e.getMessage();
			}finally {
				try {
					if(rs!=null) {rs.close();}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if(cntrModel.getCntrPeriod() < 1) {
			return "??????????????? ??????????????????";
		}else {
			if(cntrModel.getCntrPeriod() > maxPeriod) {
				return "??????????????? ????????? ????????????("+maxPeriod+"??????) ??? ?????? ??? ??? ????????????.";
			}
		}	
		
		ArrayList<DamboModel> damboList = new ArrayList<DamboModel>();	//????????? ????????????List
		
		BigDecimal sumPrm = new BigDecimal("0");							//????????? ????????? ??????
		BigDecimal cntrPeriod = new BigDecimal(cntrModel.getCntrPeriod());	//????????????
		BigDecimal tPrm  = new BigDecimal("0");							//?????? ????????????
		if(cntrModel.getDaoboCd() == null || cntrModel.getDaoboCd().length < 1) {
			return "1??? ????????? ????????? ??????????????????.";	
		}
		
		if("".equals(cntrModel.getStDt())){
			return "????????????????????? ??????????????????.";
		}else {
			try{
		         SimpleDateFormat  dateFormat = new  SimpleDateFormat("yyyyMMdd");
		         dateFormat.setLenient(false);
		         dateFormat.parse(cntrModel.getStDt());

		    }catch (Exception  e){
		        return  "????????? ????????? ?????????????????? ( ??????????????? ) ." + cntrModel.getStDt();
		    }
		}
		
		if(cntrModel.getCntrPeriod() < 1) {
			return "??????????????? ??????????????????";
		}
		
		System.out.println("cntrPeriod   : " + cntrPeriod);
		
		HashSet<String> hashSet = new HashSet<String>(Arrays.asList(cntrModel.getDaoboCd()));
		String[] resultArr = hashSet.toArray(new String[0]);

		System.out.println(" resultArr.length " + resultArr.length);
		System.out.println(" cntrModel.getDaoboCd() " + cntrModel.getDaoboCd().length );
		if(cntrModel.getDaoboCd().length != resultArr.length) {
			return "????????? ????????? ???????????? ?????? ??? ??? ????????????. ";
		}
		//????????? ????????? ??????
		for(int i = 0 ; i < cntrModel.getDaoboCd().length ; i ++) {
			//TODO ????????? ??????????????? ?????????????????? ???????????? ??????..
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
				dm.setPrm(rs.getBigDecimal("PRM"));			//????????? ????????? 
				BigDecimal prm = rs.getBigDecimal("PRM");
				sumPrm = sumPrm.add(prm);
				System.out.println("   sumPrm  : " + sumPrm);
				damboList.add(dm);
			}else {
				return "???????????? ?????? ???????????? " + cntrModel.getDaoboCd()[i];
			}
		}
			
		tPrm = sumPrm.multiply(cntrPeriod);
		cntrModel.setDamboList(damboList);
		System.out.println(" tPrm : " + tPrm);
		cntrModel.setTprm(tPrm);
		
		
		return rtnVal;
	}
	
	
	//?????? ??? ?????????
	public String reCalculatePrm(CntrModel cntrModel, Connection conn) throws SQLException {
		
		ConnectionUtil connUtil = new ConnectionUtil();
		
		String rtnVal = "";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		if(conn == null) {
			conn = connUtil.getConnection();
		}
		
		
		//????????? ????????? ??? ??????
			//TODO ????????? ??????????????? ?????????????????? ???????????? ??????..
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
			sb.append("    SET TPRM = ?	 				");		// ???????????? 
			sb.append(" 	 , MODIFY_DTM = SYSDATE() 	");		// ????????????
			sb.append(" WHERE POLI_NO = ? 				");
			
			pstmt = conn.prepareStatement(sb.toString());
			int pstmtI = 1;
			pstmt.setBigDecimal(pstmtI++, sumPrm);	//????????????
			pstmt.setString(pstmtI++, cntrModel.getPoliNo());	//????????????
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
			return "????????? ??????????????? ??????????????????";
		}
		
		try {
			conn = connUtil.getConnection();
			
			// ???????????? 
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
			pstmt.setString(1, cntrModel.getPoliNo());	//????????????
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
				return "??????????????? ???????????? ???????????????. " + cntrModel.getPoliNo();
			}
			
			pstmt.close();
			
			//?????? ????????????
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
			pstmt.setString(1, cntrModel.getPoliNo());	//????????????
			rs = pstmt.executeQuery();
			ArrayList<DamboModel> damboList = new ArrayList<DamboModel>();	//????????? ????????????List
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
			model.addAttribute("rsMsg", "??????????????? ????????? ??????????????????. "+se.getMessage());
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
        model.addAttribute("rsMsg", "?????????????????????." );
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
			System.out.println(" **** ?????? ????????????  : " + poliNo);
			if("".equals(poliNo)) {
				return "????????? ??????????????? ??????????????????";
			}
			
			// ???????????? ????????????  
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE YHC.TBCT0001				");
			sb.append("    SET CS_STAT_CD = ?	 		");		// ???????????? 
			sb.append(" 	 , MODIFY_DTM = SYSDATE() 	");		// ????????????
			sb.append(" WHERE POLI_NO = ? 				");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			int pstmtI = 1;
			pstmt.setString(pstmtI++, "R".equals(prcMode) ? "02" : "01");	//???????????? 02 ?????? , 01 ??????
			pstmt.setString(pstmtI++, poliNo);	//????????????
			pstmt.executeUpdate();
			pstmt.close();

			
		}catch(SQLException se) {
			se.printStackTrace();
			model.addAttribute("rsMsg", "?????? ?????? ?????? ??? ????????? ??????????????????. "+se.getMessage());
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

        model.addAttribute("rsMsg", "?????????????????????." );
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
			System.out.println(" **** ?????????????????? ????????????  : " + poliNo);
			
			if("".equals(poliNo)) {
				return "????????? ??????????????? ??????????????????";
			}
			if(cntrModel.getDaoboCd() == null || cntrModel.getDaoboCd().length < 1) {
				return "????????? ??????????????? ??????????????????.";
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
			viewCntr(model, cntrModel);	//?????????
			
		}catch(SQLException se) {
			se.printStackTrace();
			strErr = "?????? ??????  ?????? ??? ????????? ??????????????????. ";
		}catch(Exception e) {
			e.printStackTrace();
			strErr = "?????? ??????  ?????? ??? ????????? ??????????????????. ";
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
			System.out.println(" **** ?????? ?????? ?????? ????????????  : " + poliNo);
			
			if("".equals(poliNo)) {
				return "????????? ??????????????? ??????????????????";
			}
			if(cntrModel.getDaoboCd() == null || cntrModel.getDaoboCd().length != 1) {
				return "????????? ??????????????? ??????????????????.";
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
						strErr = "????????? ?????? ???????????? ?????????." ;
						pstmt.close();
						return strErr; 
					}
					pstmt.close();
	
			strErr = reCalculatePrm(cntrModel, conn);
			viewCntr(model, cntrModel);	//?????????
			
		}catch(SQLException se) {
			System.out.println(se.getSQLState());
			System.out.println(se.getMessage());
			se.printStackTrace();
			model.addAttribute("rsMsg", "?????? ?????? ?????? ??? ????????? ??????????????????. "+se.getMessage());
			strErr = "?????? ?????? ?????? ??? ????????? ??????????????????";
			if("23000".equals(se.getSQLState())) {
				strErr = "????????? ??????????????? ????????? ??? ????????????.";
			}
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("rsMsg", "?????? ??????  ?????? ??? ????????? ??????????????????. "+e.getMessage());
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
			System.out.println(" **** ???????????? ?????? ?????? ????????????  : " + poliNo);
			
			if("".equals(poliNo)) {
				return "????????? ??????????????? ??????????????????";
			}
			System.out.println("cntrModel.getCntrPeriod()cntrModel.getCntrPeriod()cntrModel.getCntrPeriod() :" + cntrModel.getCntrPeriod());
			if(cntrModel.getCntrPeriod() < 1) {
				return "?????? ??????????????? 1?????? ?????????.";
			}
			
				// ??????????????????   
				StringBuffer sb = new StringBuffer();
				sb.append(" UPDATE YHC.TBCT0001				");
				sb.append("    SET CNTR_PERIOD = ?	 		");		// ????????????
				sb.append(" 	 , EDDT = REPLACE(DATE_ADD(DATE_ADD(STDT, INTERVAL ? MONTH), INTERVAL -1 DAY),'-','') 			");
				sb.append(" 	 , MODIFY_DTM = SYSDATE() 	");		// ????????????
				sb.append(" WHERE POLI_NO = ? 				");
						
					pstmt = conn.prepareStatement(sb.toString());
	
					int pstmtI = 1;
					pstmt = conn.prepareStatement(sb.toString());
					pstmt.setInt(pstmtI++, cntrModel.getCntrPeriod());
					pstmt.setInt(pstmtI++, cntrModel.getCntrPeriod());
					pstmt.setString(pstmtI++, poliNo);
					if(pstmt.executeUpdate() < 1) {
						strErr = "????????? ??????????????????." ;
						pstmt.close();
						return strErr; 
					}
					pstmt.close();
	
			strErr = reCalculatePrm(cntrModel, conn);
			viewCntr(model, cntrModel);	//?????????
			
		}catch(SQLException se) {
			se.printStackTrace();
			model.addAttribute("rsMsg", "???????????? ????????? ????????? ??????????????????. "+se.getMessage());
			strErr = "???????????? ????????? ????????? ??????????????????";
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("rsMsg", "?????? ??????  ?????? ??? ????????? ??????????????????. "+e.getMessage());
			strErr = "???????????? ????????? ????????? ??????????????????";
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
