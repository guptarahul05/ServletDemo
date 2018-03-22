package com.rahul.webJDBC;

import java.io.IOException;
import java.util.List;
import java.lang.String;
import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	@Override
	public void init() throws ServletException {
		super.init();
		//create dbutilobj and pass int conn pool/datasource
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		}
		catch (Exception exc) {
			throw new ServletException(exc);
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//list studnets ...in mvc fashion
		try {
			//read command param
			String theCommand = request.getParameter("command");
			if(theCommand == null)
				theCommand = "LIST";
			//route based on command
			switch (theCommand) {
			case "LIST":
				listStudents(request, response);
				break;
			case "ADD":
				addStudent(request,response);
				break;
			case "LOAD":
				loadStudent(request,response);
				break;
			case "UPDATE":
				updateStudent(request,response);
				break;
			case "DELETE":
				deleteStudent(request,response);
				break;
			default:
				listStudents(request, response);
			}
			
			
//			listStudents(request,response);
		}
		catch (Exception exc) {
			throw new ServletException(exc);
		}
	}


	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = Integer.parseInt(request.getParameter("studentId"));
		studentDbUtil.deleteStudent(id);
		listStudents(request, response);
	}


	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		Student theStudent = new Student(id, firstName, lastName, email);
		
		studentDbUtil.updateStudent(theStudent);
		listStudents(request, response);
		
	}


	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String theStudentId = request.getParameter("studentId");
		Student theStudent = studentDbUtil.getStudents(theStudentId);
		
		request.setAttribute("The_Student", theStudent);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
	}


	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		Student theStudent = new Student(firstName,lastName,email);
		
		studentDbUtil.addStudent(theStudent);
		
		listStudents(request, response);
		
	}


	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//get students from db util
		List<Student> students = studentDbUtil.getStudents();
		//add student to the request
		request.setAttribute("STUDENT_LIST", students);
		//sent to jsp
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

}
