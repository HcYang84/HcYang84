package com.yhc.kakaoQuiz;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

public class ConnectionUtil {
	private static final Logger logger = LoggerFactory.getLogger(HomeService.class);
	
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost/yhc";
			return DriverManager.getConnection(url, "yhc", "P@ssw0rd");
		}catch(ClassNotFoundException e){
		    e.printStackTrace();
		       System.out.println(" === 드라이버 로딩 실패 === ");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
