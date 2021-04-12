package com.kmit.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.sql.*;


@WebServlet("/TodoListServlet")
public class TodoListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	private static List<Todo> todos = new ArrayList<Todo>();
	       
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String name = (String) request.getSession().getAttribute("name");
			int user_id=(int) request.getSession().getAttribute("user_id");
			
			todos.clear();
			
			
			ServletContext sc = getServletContext();
		    String driverName=sc.getInitParameter("driverName"); 
		    String driverUrl=sc.getInitParameter("driverUrl"); 
		    String username=sc.getInitParameter("username"); 
		    String password=sc.getInitParameter("password");
		    
		    Connection conn=null;
			PreparedStatement pstmt =null;
			ResultSet rs = null;
			
			try
			{
				Class.forName(driverName);
				conn = DriverManager.getConnection(driverUrl, username, password);
			
				if (conn != null && !conn.isClosed()) {

					String sql = "select * from todos where user_id = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, user_id);
					rs = pstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							Todo t=new Todo();
							t.setTodo_id(rs.getInt("todo_id"));
							t.setCategory(rs.getString("category"));
							t.setDescription(rs.getString("description"));
				            todos.add(t);
				        }  
					}	
				}
				
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println("<html>"
						+ "<head>"
						+ "<title>Todo - List</title>"
						+ "<link href='css/bootstrap.min.css' rel='stylesheet'>"
						+ "<link href='css/login.css' rel='stylesheet'>"
						+ "<script src='js/jquery.min.js'></script>"
						+ "<script src='js/bootstrap.min.js'></script>"
						+ "</head>"
						+ "<body>"
						+ "<header>"
						+ "<nav class='navbar navbar-default'>"
						+ "<a href='/' class='navbar-brand'>Brand</a>"
						+ "<ul class='nav navbar-nav'>"
						+ "<li class='active'><a href='#'>Home</a></li>"
						+ "<li><a href='TodoListServlet'>Todos</a></li>"
						+ "</ul>"
						+ "<ul class='nav navbar-nav navbar-right'>"
						+ "<li><a href='AccountUpdateServlet'><font size='4'>Hi, "+name+"</font></a></li>"
						+ "<li><a href='LogoutServlet'>Logout</a></li>"
						+ "</ul>"
						+ "</nav>"
						+ "</header>"
						+ "<div class='container'>"
						+ "<h1 align='center'>TODO List</h1>"
						+ "<form class='form-search' action='TodoSearchServlet' >"
						+ "<div class='input-append'>"
						+ "<input type='text' class='search-query' name='keyword'>"
						+ "<button type='submit' class='btn btn-large'>Search</button>"
						+ "</div>"
						+ "</form>"
						+ "<table class='table table-striped'>"
						+ "<caption>Your Todos are</caption>"
						+ "<thead>"
						+ "<th>Description</th>"
						+ "<th>Category</th>"
						+ "<th>Actions</th>"
						+ "</thead>"
						+ "<tbody>");
				
						for(Todo todo : todos) {	
							out.println("<tr>"
									+ "<td>"+todo.getDescription()+"</td>"
									+ "<td>"+todo.getCategory()+"</td>"
									+ "<td>&nbsp;&nbsp;<a class='btn btn-danger' href='TodoDeleteServlet?todo_id="+todo.getTodo_id()+"'>Delete</a>&nbsp;&nbsp;<a class='btn btn-info' href='SMSServlet?todo_id="+todo.getTodo_id()+"'>Send</a></td>"
									+ "</tr>");
						}
				out.println("</tbody>"
						+ "</table>"
						+ "<a class='btn btn-success' href='TodoAddServlet'>Add New Todo</a>"
						+ "</div>"
						+ "</body>"
						+ "</html>");
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				request.setAttribute("error","Todo List failed : "+e.getMessage() );
				request.getRequestDispatcher("ErrorServlet").forward(request, response);
			}
			finally
			{
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("error","Todo List failed : "+e.getMessage() );
					request.getRequestDispatcher("ErrorServlet").forward(request, response);
				}
			}
		}

}
