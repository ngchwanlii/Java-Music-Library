package cs212.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;

public class FacebookLoginServlet extends MusicLibraryBaseServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get session
		HttpSession session = request.getSession();
		
		// create facebook obj
		Facebook fb = new FacebookFactory().getInstance();
		
		// bind facebook obj into session attribute 
		session.setAttribute("facebook", fb);
		
		System.out.println(fb.getOAuthAuthorizationURL("/callback"));
		
		
		
		
	}
	
	
	
}
