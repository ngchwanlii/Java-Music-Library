package cs212.server;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * Allows a user to log in
 */
public class LoginServlet extends MusicLibraryBaseServlet {
	

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
		
		// check logged in status
		
		// 1. logged_in
		String loggedIn = (String) session.getAttribute(LOGGED_IN);
		
	
		// 2. not_logged_in		
		String loginUsernameError = (String) session.getAttribute(LOGIN_USERNAME_NOT_MATCH_ERROR);
		String loginPasswordError = (String) session.getAttribute(LOGIN_PASSWORD_NOT_MATCH_ERROR);
	
		// reset session error message
		session.removeAttribute(LOGIN_USERNAME_NOT_MATCH_ERROR);
		session.removeAttribute(LOGIN_PASSWORD_NOT_MATCH_ERROR);
		
		
		//if user already logged in, redirect to "favorite-list song finder class"
		if(loggedIn != null) {
			
			response.sendRedirect(response.encodeRedirectURL("/favlist"));
			return;
		}
		
		// if not_logged_in
		// html page for login page		
		buffer.append(initHtmlAndTitle("Login Page"));
		
		// css style
		buffer.append(style());
		
		// title 
		buffer.append(header("Login Page"));
		
		// styling
		buffer.append(alignDivDirection("left"));
		
		// back to sign up page
		buffer.append(goToSignUpButton());
		
		// close div
		buffer.append(divClose());
		
		// need a horizontal line
		buffer.append(horizontalLine());
		

		// if got login error - display the error message		
		if(loginUsernameError != null && loginPasswordError != null){
			buffer.append(errorMsg("Username or Password is invalid. Try again!"));
		}
		
		
		// display login form
		buffer.append(loginForm());
		
		// get footer
		buffer.append(footer());
		
		// write out the html page
		writer.println(buffer);
		
	}

	
}