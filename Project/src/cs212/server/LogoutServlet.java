package cs212.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutServlet extends MusicLibraryBaseServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	
		
		/** DEBUG USE **/
//		String clickedLogoutStatus = request.getParameter("logoutLink");		
//		System.out.println("clickedLogout: " + clickedLogoutStatus);
	
		HttpSession session = request.getSession();
	
		// we don't need to set session.setAtrribute(USERNAME, null)
		// the session.invalidate() will throw those preset attribute to garbage collection 
		session.invalidate();
		
		response.sendRedirect(response.encodeRedirectURL("/login"));
		
	}
	
}
