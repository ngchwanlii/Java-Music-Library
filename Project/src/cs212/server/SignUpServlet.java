package cs212.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.DBConfig;
import database.DBHelper;

public class SignUpServlet extends MusicLibraryBaseServlet {
	
	
	
	// post method should use for sign-up (hide user sign up info, without display them in URL)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
		

		// get session
		HttpSession session = request.getSession();
	
		/** checking possible signup error **/
		// check if username taken
		String usernameError = (String)session.getAttribute(USERNAME_TAKEN_ERROR);
		// check if password not match
		String passwordNotMatchError = (String)session.getAttribute(PASSWORD_NOT_MATCH_ERROR);
		
		// reset error message from session
		// after assigned to variable to identify (Ex: String usernameError, passwordNotMatchError)		
		session.removeAttribute(USERNAME_TAKEN_ERROR);
		session.removeAttribute(PASSWORD_NOT_MATCH_ERROR);
		
		
		//output text box requesting user name
		PrintWriter writer = prepareResponse(response);
		
		// this is thread-safe due to java implementation
		StringBuffer buffer = new StringBuffer();
		
		// html page for login page		
		buffer.append(initHtmlAndTitle("SignUp Page"));
		// css style
		buffer.append(style());
		// title 
		buffer.append(header("SignUp Page"));
		
		// provide an option for user to login
		// styling
		buffer.append(alignDivDirection("right"));
		
		// back to sign up page
		buffer.append(goToLoginButton());
		
		// close div
		buffer.append(divClose());
		
		// need a horizontal line
		buffer.append(horizontalLine());
		
		
		// got sign up error - display the error message
		if(usernameError != null){
			buffer.append(errorMsg("Opps.. username taken, try again!"));
		}
		else if(passwordNotMatchError != null) {
			buffer.append(errorMsg("Opps.. password not match, try again!"));
		}
		
		
		// build signup form
		buffer.append(signUpForm());
		
		// get footer
		buffer.append(footer());
		
		// write out the html page
		writer.println(buffer);
		
		
	}
	
}
