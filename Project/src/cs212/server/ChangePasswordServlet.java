package cs212.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.DBConfig;
import database.DBHelper;

public class ChangePasswordServlet extends MusicLibraryBaseServlet {
	
	// login should use doPost method (user login info hidden from URL)
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{		
			
			// use session to set user name  
			// setting an attribute on session scope will be available to all 
			// the requests in the user session (unless user logout or terminate the session) 
			HttpSession session = request.getSession();
					
			//output text box requesting user name
			PrintWriter writer = prepareResponse(response);
			// this is thread-safe due to java implementation
			StringBuffer buffer = new StringBuffer();
			
			
			String userExistError = (String) session.getAttribute(USERNAME_OR_PASSWORD_NOT_EXIST);
			
			
			// remove previous error session 
			session.removeAttribute(USERNAME_OR_PASSWORD_NOT_EXIST);
			
			// LOGO
			buffer.append(logo());
			
			buffer.append(horizontalLine());
			
			// html page for login page		
			buffer.append(initHtmlAndTitle("Change Password Page"));
			
			// css style
			buffer.append(style());
			
			// title 
			buffer.append(header("Change Password Page"));
			
			// styling
			buffer.append(alignDivDirection("left"));
			
			// back to sign up page
			buffer.append(goToLoginButton());
			
			// close div
			buffer.append(divClose());
			
			// need a horizontal line
			buffer.append(horizontalLine());
			
			
			if(userExistError != null){
				buffer.append(errorMsg("Username or Password does not exist. Please go to Sign Up page!"));
			}
						
			// change new password form
			buffer.append(changePasswordForm());
			
			
			
			// get footer
			buffer.append(footer());
			
			// write out the html page
			writer.println(buffer);
			
		}
	

	
	
}
