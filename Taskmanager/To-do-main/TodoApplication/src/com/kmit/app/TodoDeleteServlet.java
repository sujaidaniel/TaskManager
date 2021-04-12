package com.kmit.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TodoDeleteServlet")
public class TodoDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int todo_id=Integer.parseInt(request.getParameter("todo_id"));
		
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
			
			String sql="delete from todos where todo_id=?;";
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, todo_id);

			
			if(pstmt.executeUpdate()==1) {
				response.sendRedirect("TodoListServlet");
			}
			else {
				request.setAttribute("error","Deletion failed");
				request.getRequestDispatcher("ErrorServlet").forward(request, response);
			}
		}catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("error","Deletion failed");
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


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
