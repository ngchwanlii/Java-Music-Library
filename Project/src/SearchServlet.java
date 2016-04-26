import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends MusicLibraryBaseServlet {
	
	/**
	 * GET /search:
	 *  
	 * 1. return a search web page
	 * 2. allow user to select specific searchType
	 * 3. allow user to type in a search queries 
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// set header of html page
		String responseHtml = initHtmlAndTitle("Search Page");
		
		// get style (css)
		String style  = style();
		
		// header of search page - Song Finder
		String header  = header("Song Finder");
			
		// welcome message
		String welcome_msg = welcomeMsg();
		
		// horizontal line
		String horizontalLine = horizontalLine();
		
		// form that has to be submit to SongServlet.class
		String searchBar = searchBar();
		
		responseHtml += (style + header
						+ welcome_msg + horizontalLine
						+ searchBar
						+ footer());
					
		PrintWriter writer = prepareResponse(response);
		writer.println(responseHtml);
		
	}
	
	
}
