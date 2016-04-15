import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SongServlet extends MusicLibraryBaseServlet  {
	
	
	/**
	 *  GET /song - receive user input and selected type for searching a song
	 *  
	 *  - perform a search task on music library
	 *  - return an html page of searched result
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		// get the search_type - user selected type will go here
		// if user haven't select any type, this selected_type will be null		
		// this will return the option value ="artist/song_title/tag" based on user selected drop down list
		String search_type = request.getParameter("search_type");
		
		// get query 
		// if user haven't key in anything,  this query will be null
		String query = request.getParameter("query");
		
		// if either 1 of the option is not selected, redirect back to search page
		if(search_type == null || query == null){
			// means user haven't select the 
			response.sendRedirect(response.encodeRedirectURL("/search"));
			return;
		}	
		
		// reach this line means user has selected search type and input query
				
		// TODO: perform search task on musiclibrary		
		// 2. load song_data_processor content
		
		// load musicLibrary content
		ThreadSafeMusicLibrary threadSafeML = (ThreadSafeMusicLibrary) request.getServletContext().getAttribute("music_library");
		
		// create a JSONArray
		JSONArray searchResultsArray = null;
		// check searchType
		if(search_type.equals("artist")){
			searchResultsArray = threadSafeML.searchByArtist(query);
		}
		else if(search_type.equals("song_title")){
			searchResultsArray = threadSafeML.searchByTitle(query);
		}
		else if(search_type.equals("tag")){
			searchResultsArray = threadSafeML.searchByTag(query);
		}
		
		// prepare to build html page
		
		// set header
		String responseHtml = init_html_and_title("Result Page");
		// set style (css)
		String style  = style();
				
		// header of search page - Song Finder
		String header  = header("Song Finder");
		
		// welcome message
		String welcome_msg = welcome_msg();
		
		// horizontal line
		String horizontalLine = horizontal_line();
		
		// searchBar remain at song result page
		String searchBar = searchBar();
		
		// table format - artist <-> song title
		String result_table = tableFormat("Artist", "Song Title");
		
		// check if searchResults is empty or have results
		if(!searchResultsArray.isEmpty()){
			
			// loop through each similar_songObject in JSONArray
			// and build up the result_table	
			for(int i = 0; i < searchResultsArray.size(); i++){
				
				// get each similar song
				JSONObject song = (JSONObject) searchResultsArray.get(i);
				
				// build the result table content
				result_table += tableContent((String)song.get("artist"), (String)song.get("title"));			
			}
			
			// finish building the table
			responseHtml += (style + header + welcome_msg + horizontalLine + searchBar + result_table);
			
		}
		else {
			// error message
			responseHtml += "<font color=\"red\"><p><b>No " + query + " found in this music-library. Try insert another query." + "</b></p></font>";
		}
		
		
		// get writer
		PrintWriter writer = prepareResponse(response);
		// print out html page
		writer.println(responseHtml);  
			
	}

}
