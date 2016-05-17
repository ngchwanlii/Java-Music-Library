package cs212.server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.server.Server;

import database.DBConfig;

public class ShutdownServlet extends MusicLibraryBaseServlet {
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
	
		// get session
		HttpSession session = request.getSession();
		
	
		String adminLoggedIn = (String) session.getAttribute(ADMINNAME);
		
		// get server from web-container
		Server server = (Server) request.getServletContext().getAttribute(MusicLibraryServer.JETTY_SERVER);
		
		if(adminLoggedIn == null){
			
			// redirect back them to admin login page
			response.sendRedirect(response.encodeRedirectURL("/admin?" + STATUS +  "=" + NOT_LOGGED_IN));
			return;
		}
		
		// else - perform shutdown operation	
		// shut down
		server.setStopTimeout(10000L);
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        
                        server.stop();
                    } 
                    catch (Exception ex) {
                        System.out.println("Failed when attempt to stop server");
                    }
                }
            }.start();
        }
        catch (Exception ex) {
	      System.out.println("Can't stop server");
	      
        }	
	}
}