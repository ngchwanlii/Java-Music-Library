import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Base class for all servlets for MusicLibrary web application
 * @author Jay Ng
 * @date 04/12/2016
 * 
 */
public class MusicLibraryBaseServlet extends HttpServlet {
	
	// HTML 200 - OK
	// set content type and status
	// return a print writer
	protected PrintWriter prepareResponse(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		return response.getWriter();

	}
	
	// set the title of header
	// for Project 3 - part 2 - Web Interface
	// title = Song Finder
	protected String header(String title){
		
		return "<html><head><title>" + title + "</title></head><body>"; 
		
	}
	
	protected String style(){
		
		return "<style>.center {"
				+ "margin: auto; "
				+ "width: 80%; "				
				+ "padding: 10px;}"
				+ "form {display: inline; padding: 10px;}"						
				+ "</style>";
				
		
	}
	
	
	// return footer of html page
	protected String footer(){
		return "</body></html>";
	}
	
	
}
