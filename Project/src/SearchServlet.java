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
		String responseHtml = header("Search Page");
		
		// get style (css)
		String style  = style();
		
		// header of search page - Song Finder
		String header  = "<div class=\"center\"><center><h1>Song Finder</h1></center><br/>";
			
		// welcome message
		String welcome_msg = "<center><p>Welcome to song finder! Select a search type and type in a query and we'll display you a list of similar songs you might like!</p></center>";
		
		// horizontal line
		String horizontalLine = "<hr>";
		
		// form that has to be submit to SongServlet.class
		String search_form = "<label>Search Type:</label>"
							+ "<form action=\"song\" method=\"get\">"
							+ "<select name=\"search_type\">"	// note search_type here
		    				+ "<option value=\"artist\">Artist</option>"
		    				+ "<option value=\"song_title\">Song Title</option>"
		    				+ "<option value=\"tag\">Tag</option>"    				
		    				+ "</select>"
							+ "</form>";
		
		String query_form = "<label>Query:</label>"
							+ "<form action=\"song\" method=\"get\">"
							+ "<input type=\"text\" name=\"query\">" // note query here
							+ "<input type=\"submit\" value=\"Submit\">"
							+ "</form>";
		
		responseHtml += (style + header
						+ welcome_msg + horizontalLine
						+ search_form + query_form
						+ footer());
					
		PrintWriter writer = prepareResponse(response);
		writer.println(responseHtml);
		
	}
	
	
}
