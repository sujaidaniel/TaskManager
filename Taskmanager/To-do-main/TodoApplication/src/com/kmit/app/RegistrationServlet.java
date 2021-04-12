package com.kmit.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.cj.MysqlConnection;


@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("view/registration.html").forward(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name=request.getParameter("name"),fullname=request.getParameter("fullname"),email=request.getParameter("email"),password=request.getParameter("password"),cpassword=request.getParameter("c-password");
		
		if(!password.equals(cpassword)) {
			request.setAttribute("error","Your Password and Confirm Password do not match");
			request.getRequestDispatcher("ErrorServlet").forward(request, response);
		}
		else {
			ServletContext sc=getServletContext();
			String driverName=sc.getInitParameter("driverName"),url=sc.getInitParameter("driverUrl"),username=sc.getInitParameter("username"),pass=sc.getInitParameter("password");
			
			PrintWriter out=response.getWriter();
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			try {
				Class.forName(driverName);
				conn=DriverManager.getConnection(url,username,pass);
				System.out.println("Connected");
				String sql="select * from login where username=?";
				pstmt=conn.prepareStatement(sql);
				pstmt.setString(1, name);
				rs=pstmt.executeQuery();
				if(rs!=null && rs.next()) {
					request.setAttribute("error","User Already exists");
					request.getRequestDispatcher("ErrorServlet").forward(request, response);
				}
				else {
					String insertSql="insert into login(username,fullname,email,password) values(?,?,?,?)";
					pstmt=conn.prepareStatement(insertSql);
					pstmt.setString(1, name);
					pstmt.setString(2, fullname);
					pstmt.setString(3, email);
					pstmt.setString(4, password);
					
					if(pstmt.executeUpdate()==1) {
						response.sendRedirect("LoginServlet");
					}
					else {
						request.setAttribute("error","Problem in registring.. Please try again!");
						request.getRequestDispatcher("ErrorServlet").forward(request, response);
					}
				}
				
			}catch (Exception e) {
				out.print("<h1>Connection Failed</h1>");
				e.printStackTrace();
				System.out.println("Connection Failed");
			}finally{
				try {
					pstmt.close();
					rs.close();
					conn.close();
				}catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("error","Problem in closing connection");
					request.getRequestDispatcher("ErrorServlet").forward(request, response);
				}
			}
		}
	}

}
