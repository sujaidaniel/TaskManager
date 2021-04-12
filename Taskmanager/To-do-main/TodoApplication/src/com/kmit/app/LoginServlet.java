package com.kmit.app;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Connection conn=null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("view/login.html").forward(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name=request.getParameter("name"),password=request.getParameter("password");
//		System.out.print(name+" "+password);	
		
		String gRecaptchaResponse=request.getParameter("g-recaptcha-response");
		System.out.println(gRecaptchaResponse);
		boolean verify=VerifyRecaptcha.verify(gRecaptchaResponse);
		if(!verify) {
			request.setAttribute("error", "Captcha Not Verified");
			request.getRequestDispatcher("ErrorServlet").forward(request, response);
			return;
		}
		ServletContext sc=getServletContext();
		String driverName=sc.getInitParameter("driverName"),url=sc.getInitParameter("driverUrl"),username=sc.getInitParameter("username"),pass=sc.getInitParameter("password");
		try {
			Class.forName(driverName);
			conn=DriverManager.getConnection(url,username,pass);
			String sql="select * from login where username=? and password=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,name);
			pstmt.setString(2, password);
			rs=pstmt.executeQuery();
			
			if(rs!=null && rs.next()) {
				HttpSession session=request.getSession();
				session.setAttribute("user_id", rs.getInt("user_id"));
				session.setAttribute("name", rs.getString("username"));
				response.sendRedirect("TodoListServlet");
			}
			else {
				request.setAttribute("error","Invalid Login");
				request.getRequestDispatcher("ErrorServlet").forward(request, response);
			}
			
		}catch (Exception e) {	
			// TODO: handle exception
			e.printStackTrace();
			request.setAttribute("error","Not able to login");
			request.getRequestDispatcher("ErrorServlet").forward(request, response);
		}finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}

}
