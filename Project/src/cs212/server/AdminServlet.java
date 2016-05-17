package cs212.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminServlet extends MusicLibraryBaseServlet  {
	
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
		

		// get session
		HttpSession session = request.getSession();
	
		
		//output text box requesting user name
		PrintWriter writer = prepareResponse(response);
		
		// this is thread-safe due to java implementation
		StringBuffer buffer = new StringBuffer();
		
		String loginError = (String) session.getAttribute(USERNAME_OR_PASSWORD_NOT_EXIST);
		
		session.removeAttribute(USERNAME_OR_PASSWORD_NOT_EXIST);
		
		String adminLoggedIn = (String) session.getAttribute(ADMINNAME);
		
		
		
		
		
		// ADDED LOGO
		buffer.append(logo()); 
		
		buffer.append(horizontalLine());
		 
		// html page for login page		
		buffer.append(initHtmlAndTitle("Admin Login Page"));
		// css style
		buffer.append(style());
		// title 
		buffer.append(header("Admin Login Page"));
		
		// provide an option for user to login
		// styling
		buffer.append(alignDivDirection("left"));
		
		// back to sign up page
		buffer.append(goToLoginButton());
		
		// close div
		buffer.append(divClose());
		
		// need a horizontal line
		buffer.append(horizontalLine());
		
		
		// if got login error - display the error message		
		if(loginError != null){
			buffer.append(errorMsg("AdminName or Password is invalid. Try again!"));
		}
		
		
		// else succesfully logged in
		if(adminLoggedIn != null){
			
			// warning icon
			buffer.append("<center><img src='https://maxcdn.icons8.com/Color/PNG/96/Security/warning_shield-96.png' title='Warning Shield' width='96'></center>");
			
			// print out a message for admin to ready to shut down the server
			buffer.append("<font color='red'><center><h2 style='margin-top: 20px;'>" + "Ready to shutdown by clicking button below!" + "</h2></center></font>");
		
			// shutdown button - redirect to shutdown servlet to gracefully shutdown the server
			// note about threads
			buffer.append("<center>" + goToShutDownButton() + "</center>");
			
			
		}
		else {
			// build admin login form
			buffer.append(adminLoginForm());
		}
		
		// get footer
		buffer.append(footer());
		
		// write out the html page
		writer.println(buffer);
		
		
	}
	
	
}
