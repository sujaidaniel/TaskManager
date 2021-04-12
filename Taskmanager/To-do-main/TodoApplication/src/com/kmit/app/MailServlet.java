package com.kmit.app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;

@WebServlet("/MailServlet")
public class MailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn=null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email=request.getParameter("email");	
		System.out.println(email);
		ServletContext sc=getServletContext();
		PrintWriter out=response.getWriter();
		String driverName=sc.getInitParameter("driverName"),url=sc.getInitParameter("driverUrl"),username=sc.getInitParameter("username"),pass=sc.getInitParameter("password");
		try {
			Class.forName(driverName);
			conn=DriverManager.getConnection(url,username,pass);
			String sql="select * from login where email=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,email);
			rs=pstmt.executeQuery();
			
			//if user is available
			if (rs != null && rs.next()) {
				//user is available
				String message="Username :"+rs.getString("username")+"\nPassword: "+rs.getString("password");
				SendMail.send(email, message);
			}
			else {
				request.setAttribute("error","Not able to fetch : Email ID not found" );
				request.getRequestDispatcher("ErrorServlet").forward(request, response);
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.print("e1");
			request.setAttribute("error","Not Found : "+e.getMessage() );
			request.getRequestDispatcher("ErrorServlet").forward(request, response);
		}
		finally {
			try {
				
				pstmt.close();
				rs.close();
				conn.close();
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.print("e1");
				request.setAttribute("error","Not able to Login : "+e.getMessage() );
				request.getRequestDispatcher("ErrorServlet").forward(request, response);
			}
			
		}
		response.sendRedirect("LoginServlet");
	}

}
