package com.yhc.kakaoQuiz;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.yhc.kakaoQuiz.model.CntrModel;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	
	@RequestMapping(value = "/testPage", method = RequestMethod.GET)
	public String testPage(Model model, HttpServletRequest request, HttpServletResponse response) {
		
		String prcMode = request.getParameter("prcMode");
		return "testPage";
	}
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		new HomeService().getCntrList(model);
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/newCntr", method = RequestMethod.GET)
	public String newCntr(Locale locale, Model model) {
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "newCntr";
	}
	
	
	@RequestMapping(value = "/calculatePrm", method = RequestMethod.GET)
	public String calculatePrm(Model model, HttpServletRequest request) {
		
		String prcMode = request.getParameter("prcMode");
		
		CntrModel cntrModel = new CntrModel();
		cntrModel.setStDt(request.getParameter("stDt"));
		cntrModel.setEdDt(request.getParameter("edDt"));
		cntrModel.setPlicd(request.getParameter("plicd"));
		cntrModel.setCntrPeriod((request.getParameter("cntrPeriod") == null || "".equals(request.getParameter("cntrPeriod").trim())) ? 0 : Integer.parseInt(request.getParameter("cntrPeriod")));
		cntrModel.setDaoboCd(request.getParameterValues("damboCd"));
		cntrModel.setPrcMode(prcMode);
		
		HomeService homeservice = new HomeService();
		homeservice.tmpCalculatePrm(model, cntrModel);
		model.addAttribute("cntrModel" , cntrModel);
		
		return "newCntr";
	}
	
	@RequestMapping(value = "/createCntr", method = RequestMethod.GET)
	public String createCntr(Model model, HttpServletRequest request, HttpServletResponse response) {
		
		String prcMode = request.getParameter("prcMode");
		
		CntrModel cntrModel = new CntrModel();
		cntrModel.setStDt(request.getParameter("stDt"));
		cntrModel.setEdDt(request.getParameter("edDt"));
		cntrModel.setPlicd(request.getParameter("plicd"));
		cntrModel.setCntrPeriod((request.getParameter("cntrPeriod") == null || "".equals(request.getParameter("cntrPeriod").trim())) ? 0 : Integer.parseInt(request.getParameter("cntrPeriod")));
		cntrModel.setDaoboCd(request.getParameterValues("damboCd"));
		cntrModel.setPrcMode(prcMode);
		
		HomeService homeservice = new HomeService();
		model.addAttribute("cntrModel" , cntrModel);
		String strErr = homeservice.createCntr(model, cntrModel);
		if(!"".equals(strErr)) {
			model.addAttribute("rtnMsg" , strErr);
			return "newCntr";
		}else {
			homeservice.viewCntr(model, cntrModel);
			model.addAttribute("rtnMsg" , "처리되었습니다.");
			return "viewCntr";
		}
	}
	
	@RequestMapping(value = "/viewCntr", method = RequestMethod.GET)  
	public String viewCntr(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		logger.info("  * viewCntr Start ");
		String poliNo = request.getParameter("poliNo");
		CntrModel cntrModel = new CntrModel();
		cntrModel.setPoliNo(poliNo);
		HomeService homeservice = new HomeService();
		homeservice.viewCntr(model, cntrModel);
		model.addAttribute("cntrModel" , cntrModel);
		return "viewCntr";
	}
	
	@RequestMapping(value = "/modifyCntr", method = RequestMethod.GET)  
	public String modifyCntr(Model model, HttpServletRequest request) {
		logger.info("  * changeCntr Start ");
		String poliNo = request.getParameter("poliNo");
		
		CntrModel cntrModel = new CntrModel();
		cntrModel.setPoliNo(poliNo);
		
		HomeService homeservice = new HomeService();
		homeservice.viewCntr(model, cntrModel);
		model.addAttribute("cntrModel" , cntrModel);
		
		if(!"01".equals(cntrModel.getCsStatCd())) {
			model.addAttribute("rtnMsg", "정상계약만 변경가능합니다.");
			return "viewCntr";
		}
		
		return "modifyCntr";
	}
	
	@RequestMapping(value = "/cancelCntr", method = RequestMethod.GET)
	public String cancelCntr(Model model, HttpServletRequest request) {
		logger.info("  * cancelCntr Start " );
		
		String poliNo = request.getParameter("poliNo");
		String prcMode = request.getParameter("prcMode");
		// prcMode R :철회, R2 : 철회취소
		
		CntrModel cntrModel = new CntrModel();
		cntrModel.setPoliNo(poliNo);
		cntrModel.setPrcMode(prcMode);
		
		HomeService homeservice = new HomeService();
		homeservice.viewCntr(model, cntrModel);
		
		if("R".equals(prcMode)) {
			if(!"01".equals(cntrModel.getCsStatCd())) {
				model.addAttribute("rtnMsg", "정상계약만 철회 가능합니다.");
				return "viewCntr";
			}
			model.addAttribute("rtnMsg", "철회 되었습니다.");
		}else if("R2".equals(prcMode)) {
			if(!"02".equals(cntrModel.getCsStatCd())) {
				model.addAttribute("rtnMsg", "철회계약만 취소 가능합니다.");
				return "viewCntr";
			}
			model.addAttribute("rtnMsg", "철회취소 되었습니다.");
		}
		
		homeservice.cancelCntr(model, cntrModel);
		
		//변경 후 재조회
		homeservice.viewCntr(model, cntrModel);
		
		model.addAttribute("cntrModel" , cntrModel);
		return "viewCntr";
	}
	
	
	@RequestMapping(value = "/delDambo", method = RequestMethod.GET)
	public String delDambo(Model model, HttpServletRequest request) {
		logger.info("  * cancelCntr Start " );
		
		String poliNo = request.getParameter("poliNo");
		String damboCd[] = request.getParameterValues("damboCd");
		
		CntrModel cntrModel = new CntrModel();
		cntrModel.setPoliNo(poliNo);		
		cntrModel.setDaoboCd(damboCd);
		
		
		HomeService homeservice = new HomeService();
		homeservice.viewCntr(model, cntrModel);
		
		if(!"01".equals(cntrModel.getCsStatCd())) {
			model.addAttribute("rtnMsg", "정상계약만 담보변경(삭제) 가능합니다.");
			return "viewCntr";
		}
		
		if(damboCd.length > 1) {
			model.addAttribute("rtnMsg", "담보삭제는 1개씩 가능합니다.");
			return "viewCntr";
		}
		if(cntrModel.getDamboList().size() <= 1) {
			model.addAttribute("rtnMsg", "삭제할 수 없습니다. 1개 이상의 담보가 필요합니다.");
			return "viewCntr";
		}
	
		
		String strErr = homeservice.delDambo(model, cntrModel);
		if(!"".equals(strErr)) {
			model.addAttribute("rtnMsg" , "처리되었습니다.");
		}
		model.addAttribute("cntrModel" , cntrModel);
		
		return "viewCntr";
		
	}
	
	
	@RequestMapping(value = "/addDambo", method = RequestMethod.GET)
	public String addDambo(Model model, HttpServletRequest request) {
		logger.info("  * cancelCntr Start " );
		
		String poliNo = request.getParameter("poliNo");
		String damboCd[] = request.getParameterValues("damboCd");
		
		CntrModel cntrModel = new CntrModel();
		cntrModel.setPoliNo(poliNo);		
		cntrModel.setDaoboCd(damboCd);
		
		
		HomeService homeservice = new HomeService();
		homeservice.viewCntr(model, cntrModel);
		
		if(!"01".equals(cntrModel.getCsStatCd())) {
			model.addAttribute("rtnMsg", "정상계약만 담보추가 가능합니다.");
			return "viewCntr";
		}
		
		if(damboCd.length > 1) {
			model.addAttribute("rtnMsg", "담보추가는 1개씩 가능합니다.");
			return "viewCntr";
		}
		
		String strErr = homeservice.addDambo(model, cntrModel);
		homeservice.viewCntr(model, cntrModel);
		if("".equals(strErr)) {
			model.addAttribute("rtnMsg" , "처리되었습니다.");
		}else {
			model.addAttribute("rtnMsg" , strErr);
		}
		model.addAttribute("cntrModel" , cntrModel);
		
		return "viewCntr";
		
	}
	
	@RequestMapping(value = "/changeCntrPeriod", method = RequestMethod.GET)
	public String changeCntrPeriod(Model model, HttpServletRequest request) {
		logger.info("  * changeCntrPeriod Start " );
		
		String poliNo = request.getParameter("poliNo");
		int cntrPeriod = (request.getParameter("cntrPeriod") == null || "".equals(request.getParameter("cntrPeriod").trim())) ? 0 : Integer.parseInt(request.getParameter("cntrPeriod"));
		
		CntrModel cntrModel = new CntrModel();
		cntrModel.setPoliNo(poliNo);		
		
		
		HomeService homeservice = new HomeService();
		homeservice.viewCntr(model, cntrModel);
		
		if(!"01".equals(cntrModel.getCsStatCd())) {
			model.addAttribute("rtnMsg", "정상계약만 변경 가능합니다.");
			return "viewCntr";
		}
		if(cntrModel.getMaxCntrPeriod() < cntrPeriod) {
			model.addAttribute("rtnMsg", "최대 계약기간 "+cntrModel.getMaxCntrPeriod()+"개월 을 초과할 수 없습니다.");
			return "viewCntr";
		}
		cntrModel.setCntrPeriod(cntrPeriod);
		String strErr = homeservice.changeCntrPeriod(model, cntrModel);
		homeservice.viewCntr(model, cntrModel);
		if("".equals(strErr)) {
			model.addAttribute("rtnMsg" , "처리되었습니다.");
		}else {
			model.addAttribute("rtnMsg" , strErr);
		}
		model.addAttribute("cntrModel" , cntrModel);
		
		return "viewCntr";
		
	}
	
		
	
}
