package com.kmit.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SMSServlet")
public class SMSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			int todoid=Integer.parseInt(request.getParameter("todo_id"));
			
			ServletContext sc = getServletContext();
		    String driverName=sc.getInitParameter("driverName"); 
		    String driverUrl=sc.getInitParameter("driverUrl"); 
		    String username=sc.getInitParameter("username"); 
		    String password=sc.getInitParameter("password");
			
			Connection conn=null;
			PreparedStatement pstmt =null;
			ResultSet rs = null;
			
			try {
				Class.forName(driverName);
				conn = DriverManager.getConnection(driverUrl, username, password);
				
				String sql = "select mobile, description from todos t, login l where t.user_id=l.user_id and t.todo_id= ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, todoid);
				rs = pstmt.executeQuery();
				
				//if user is available
				if (rs != null && rs.next()) {
					//user is available
					String number=rs.getString("mobile");
					String message="You have a ToDo : "+rs.getString("description");
					SendSMS.sendSMS(number, message);
					response.sendRedirect("TodoListServlet");
				}
				else {
					request.setAttribute("error","Not able to fetch : TODO_ID not found" );
					request.getRequestDispatcher("ErrorServlet").forward(request, response);
				}
				
				
			}catch(Exception e) {
				e.printStackTrace();
				request.setAttribute("error","Not able to Login : "+e.getMessage() );
				request.getRequestDispatcher("ErrorServlet").forward(request, response);
			}
			finally
			{
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("error","Not able to delete : "+e.getMessage() );
					request.getRequestDispatcher("ErrorServlet").forward(request, response);
				}
			}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
