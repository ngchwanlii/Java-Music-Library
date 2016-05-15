package cs212.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cs212.util.concurrent.ReentrantLock;
import database.DBConfig;
import database.DBHelper;

/*
 * Servlet invoked at login.
 * Creates cookie and redirects to main ListServlet.
 */
public class VerifyUserServlet extends MusicLibraryBaseServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//VerifyUser does not accept GET requests. Just redirect to login with error status.
		String formType = request.getParameter(PAGENAME);
		
		
		
		if(formType.equals(SIGNUPPAGE)) {
			response.sendRedirect(response.encodeRedirectURL("/signup"));
		}
		else if(formType.equals(LOGINPAGE)) {
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		

		// get dbconfig from web-container
		DBConfig dbconfig = (DBConfig) request.getServletContext().getAttribute(DBConfig.DBCONFIG);
		
		// get lock from web-container
		ReentrantLock userLock = (ReentrantLock) request.getServletContext().getAttribute(MusicLibraryBaseServlet.USERTABLE_LOCK);
		
		// get session for checking and setting status
		HttpSession session = request.getSession();
		

		
		// check which from type has passed in
		// 1. signup page
		// 2. login page	
		String formType = request.getParameter(PAGENAME);
		String changePassword = request.getParameter(CHG_PASSWORD);
		
		try {
			
			// 0. if user need to change password			
			if(formType.equals(CHANGEPASSWORDPAGE)){
				
				String username = request.getParameter(USERNAME);
				String oldPassword = request.getParameter(PASSWORD);
				String newPassword = request.getParameter(NEWPASSWORD);
				
				// authentication
				boolean userExists = DBHelper.userAuthentication(dbconfig, username, oldPassword);
				
				// if user does not exists in database record, print out error message
				if(!userExists){
					
					// set user login error
					session.setAttribute(USERNAME_OR_PASSWORD_NOT_EXIST, username);
					
					response.sendRedirect("/changepassword?" + STATUS + "=" + USERNAME_OR_PASSWORD_NOT_EXIST );
					return;
				}
				
				// else, user exists, user can now change new password
				DBHelper.changeNewPassword(dbconfig, newPassword, username);
				
				// then redirect user to login page
				response.sendRedirect("/login");
				return;
				
				
			}
					
			// 1. signup page logic here
			else if(formType.equals(SIGNUPPAGE)){
				
				// get the parameters value from user signed-up form
				String username = request.getParameter(USERNAME);
				String fullname = request.getParameter(FULLNAME);
				String password = request.getParameter(PASSWORD);
				String password2 = request.getParameter(CONFIRMPASSWORD);
				
							
				// 1. check if username exists or not in mySQL table
				
				// acquire readLock 1st - because we need to check the mySQL table 
				// (and if there's write operation encountered, we cannot read the data and have to wait)
				// acquire readLock
				userLock.lockRead();
				// local variable is thread-safe
				boolean userExist = DBHelper.userExist(dbconfig, username);
				// release readLock
				userLock.unlockRead();
				
				// if username already exists, prompt user to enter another username
				if(userExist){
					
					// bind signup username_taken_error object to session for checking purpose at signup page
					// so if signup page's session.getAttribute(USERNAME_TAKEN_ERROR) != null, which mean there's USERNAME_TAKEN_ERROR there
					session.setAttribute(USERNAME_TAKEN_ERROR, username);
					
					
				
					// redirect back to sign up page with session's status has USERNAME_TAKEN_ERROR
					response.sendRedirect(response.encodeRedirectURL("/signup?" + STATUS + "=" + USERNAME_TAKEN_ERROR));
					return;
				}
				
				
				// reach this line mean username is not taken  
			
				// check if 2nd password entered is match				
				// if two password not match - redirect back to signup page
				if(!password.equals(password2)){
					
			
					// set password not match error
					session.setAttribute(PASSWORD_NOT_MATCH_ERROR, username);
					
					// redirect back to sign up page with session's status has PASSWORD_NOT_MATCH_ERROR
					response.sendRedirect(response.encodeRedirectURL("/signup?" + STATUS +  "=" + PASSWORD_NOT_MATCH_ERROR));
					return;
				}
			
				// reach this line mean signup check passed - ready to save this user info to mySQL database
				// ready to addUser to mySQL table
				// need to acquire writeLock 1st before addUser (write operation)
			
				// add user to mySQL
				userLock.lockWrite();				
				// set username = logged in username
				session.setAttribute(USERNAME, username);
				DBHelper.addUser(dbconfig, username, fullname, password);
				// release lock
				userLock.unlockWrite();	
				
				response.sendRedirect("/login");
				return;
				
			}			
			// 2. login page logic here
			else if(formType.equals(LOGINPAGE)){
				
				// PseduoCode
				// 1. check username from DB
				// 2. check password from DB
				// 3. if not match or error, set different error signal
				// 4. if no error, go to fav_list page
			
				String loggedIn = (String)session.getAttribute(LOGGED_IN);
				
				// base case - if user logged in, redirect them to fav list
				if(loggedIn != null) {
					
					response.sendRedirect(response.encodeRedirectURL("/favlist"));
					return;
				}
				
			
				String username = request.getParameter(USERNAME);
				String password = request.getParameter(PASSWORD);
				
				
				userLock.lockRead();
				
				// login authentication
				boolean loginStatusOK = DBHelper.userAuthentication(dbconfig, username, password);			
				
				userLock.unlockRead();
				
			
				// if username and password is invalid - set 
				if(!loginStatusOK){
			
					// set user login error
					session.setAttribute(LOGIN_USERNAME_NOT_MATCH_ERROR, username);
					// set password login error
					session.setAttribute(LOGIN_PASSWORD_NOT_MATCH_ERROR, username);
					
					response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS +  "=" + NOT_LOGGED_IN));
					return;
					
				}
				
				
				boolean loginUserHasFavSongRecords = DBHelper.favTableUsernameExist(dbconfig, username);
				
				if(loginUserHasFavSongRecords){
					session.setAttribute(HAS_FAV_SONG_LIST_RECORD, username);
				}
			
				
				// reach this line mean login check passed											
				
				// set login status equal to LOGGED in
				session.setAttribute(LOGGED_IN, username);
				
				// set username = logged in username
				session.setAttribute(USERNAME, username);
				
				
				
			
				// go to fav-song list page
				response.sendRedirect(response.encodeRedirectURL("/search"));
				return;
			}
			
			
		}	
		catch (SQLException e) {				
			e.printStackTrace();
		} 
		
		
		
		
		
	}

	
	
}
