import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SongServlet extends MusicLibraryBaseServlet  {
	
	
//	public static String selected;
	
//	public static void main(String[] args) {
//		
//		
//		getSelected();
//
//	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		/** Ask Professor about this drop down list**/
		// get the search_type - user selected type will go here
		// if user haven't select any type, this selected_type will be null
		String selected_type = request.getParameter("search_type");
		
		// get query 
		// if user haven't key in anything,  this query will be null
		String query = request.getParameter("query");
		
		// if either 1 of the option is not selected, redirect back to search page
		if(selected_type == null || query == null){
			// means user haven't select the 
			response.sendRedirect(response.encodeRedirectURL("/search"));
			return;
		}	
		
		// now i know search_type and query is valid
				
		// TODO: perform search task on musiclibrary
		// 1. load musicLibrary content
		// 2. load song_data_processor content
		
		/** Ask professor Question on how to shutDown a searchPool**/
		
		
	}
	

	
}
