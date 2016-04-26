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
	
	
	// css/js styling
	protected String style(){
		
		return "<style>.center {"
				+ "margin: auto; "
				+ "width: 80%; "				
				+ "padding: 10px;}"
				+ "form {display: inline; padding: 10px;}"						
				+ "</style>";
				
		
	}
	
	
	/************************
	 * 						*
	 *  Header zone			*
	 * 						*
	 ************************/	
	// set the title of header
	// for Project 3 - part 2 - Web Interface
	// title = Song Finder
	protected String initHtmlAndTitle(String title){
		
		return "<html><head><title>" + title + "</title></head><body>"; 
		
	}
	
	// return boldHeader
	protected String header(String boldTitle){
		
		return "<div class=\"center\"><center><h1>" + boldTitle + "</h1></center><br/>";
		
	}
	
	// return a search type with drop downlist + query input text field
	protected String searchBar(){
		
		String search_form = "<label>Search Type:</label>"
				+ "<form action=\"song\" method=\"get\">"
				+ "<select name=\"search_type\">"	// note search_type here
				+ "<option value=\"artist\">Artist</option>"
				+ "<option value=\"song_title\">Song Title</option>"
				+ "<option value=\"tag\">Tag</option>"    				
				+ "</select>";

		String query_form = "<label>Query:</label>"				
				+ "<input type=\"text\" name=\"query\">" // note query here
				+ "<input type=\"submit\" value=\"Submit\">"
				+ "</form>";
		
		
		return search_form + query_form;
		
	}
	
	// return welcome msg
	protected String welcomeMsg(){
		// welcome message
		return "<center><p>Welcome to song finder! Select a search type and type in a query and we'll display you a list of similar songs you might like!</p></center>";
	}
	
	
	/************************
	 * 						*
	 *  Body/Content zone	*
	 * 						*
	 ************************/
	
	// return table format style
	protected String tableFormat(String col1, String col2){
		return "<body><table border=\"2px\" width=\"100%\"><tr>"
				+ "<td><strong> " + col1 + "</strong></td>"
				+ "<td><strong>" + col2 + "</strong></td>"
				+ "</tr>";
	}
	
	// return table content 
	protected String tableContent(String artist, String songTitle){
		
		return "<tr><td>" + artist + "</td><td>" + songTitle + "</td></tr>";
		
	}
	


	/************************
	 * 						*
	 *  Footer zone			*
	 * 						*
	 ************************/
	// return footer of html page
	protected String footer(){
		return "</body></html>";
	}
	

	/************************
	 * 						*
	 *  Utilities tool 		*
	 * 						*
	 ************************/
	// horizontal line
	protected String horizontalLine(){
		
		return "<hr>";
	}

	// query not found msg
	protected String notFound(String searchType, String query){
		return "<font color=\"red\"><p><b>No similar songs to " + searchType + " \"" + query + "\" found in this music-library. Try search again." + "</b></p></font>";
	}
	
	protected String backToSearchButton(){
		return "<form action=\"search\" method=\"get\"><input type=\"submit\" value=\"Search Again\"></form>";
	}
	
}
