package com.kmit.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.annotation.WebServlet;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.cj.protocol.Resultset;

@WebServlet("/TodoAddServlet")
public class TodoAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("view/addTodo.html").forward(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int user_id=(int)request.getSession().getAttribute("user_id");
		String description=(String)request.getParameter("todo");
		String category = request.getParameter("category");
		
		PreparedStatement pstmt=null;
		Connection conn=null;
		try {
			ServletContext sc=getServletContext();
			String driverName=sc.getInitParameter("driverName");
			String driverUrl=sc.getInitParameter("driverUrl");
			String username=sc.getInitParameter("username");
			String password=sc.getInitParameter("password");
			
			Class.forName(driverName);
			conn=DriverManager.getConnection(driverUrl,username,password);
			
			String sql="insert into todos(category,description, user_id) values(?,?,?);";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, category);
			pstmt.setString(2,description);
			pstmt.setInt(3,user_id);
			
			if(pstmt.executeUpdate()==1) {
				response.sendRedirect("TodoListServlet");
			}
			else {
				request.setAttribute("error","Addition failed");
				request.getRequestDispatcher("ErrorServlet").forward(request, response);
			}
		}catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("error","Addition failed");
			request.getRequestDispatcher("ErrorServlet").forward(request, response);
			
		}finally {
			try {
				pstmt.close();
				conn.close();
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}

}
