package cs212.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cs212.data.ThreadSafeMusicLibrary;
import cs212.util.concurrent.ReentrantLock;
import database.DBConfig;
import database.DBHelper;

public class SongServlet extends MusicLibraryBaseServlet  {
	
	
	// get method
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		// setting
		HttpSession session = request.getSession();
		
		// get dbconfig from web-container
		DBConfig dbconfig = (DBConfig) request.getServletContext().getAttribute(DBConfig.DBCONFIG);
		
		// get favLock - this SongServlet (involve retrieve / update to favListTable)
		ReentrantLock favLock = (ReentrantLock) request.getServletContext().getAttribute(MusicLibraryBaseServlet.FAVTABLE_LOCK);
	
		// get writer
		PrintWriter writer = prepareResponse(response);
		
		// String buffer		
		StringBuffer buffer = new StringBuffer();
		
		
		// 1. base case - check user login
		boolean userLogin = checkUserLogin(session, response);
		
		if(!userLogin){
			// redirect to login page					
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS +  "=" + NOT_LOGGED_IN));
			return;
		}
		
		
		
		/** condition check 2 - CHECK USER CORRECTLY SEARCH QUERY **/		
		// get the search_type - user selected type will go here		
		String search_type = getParameterValue(request, SEARCH_TYPE); 
	
		// get query 
		// if user haven't key in anything,  this query will be null
		String query = getParameterValue(request, QUERY);
		
		// get songInfo (if user has click on a particular song title) 
		String songInfo = getParameterValue(request, SONG_INFO);
		
		// get songArtist (this is the songArtist where the user clicked on the song title previously)
		String songArtist = getParameterValue(request, "songArtist");
		
		// get login user last time stamp
		String loginUserTimeStamp = (String) session.getAttribute(LOGIN_TIMESTAMP);
		
		
		boolean userClickedSearchButton = checkLoginUserClickedSearchButton(search_type, query);
	
		// 2. check user clicked search button
		if(!userClickedSearchButton){
			
			// means user haven't select the query or search_type
			response.sendRedirect(response.encodeRedirectURL("/search"));
			// redirect back to search page
			return;			
		}
			
		// reach this line means user has selected search type and input query
		
		/** set condition SESSION for SEARCH TYPE and QUERY **/
		// else, set the session - useful for adding to favorite star icon display
		session.setAttribute(SEARCH_TYPE, search_type);
		session.setAttribute(QUERY, query);
	
		// get username from session
		String username = (String) session.getAttribute(USERNAME);
	
		//TODO: added advance feature 5 - save search history
		try {
			
			DBHelper.saveSearchHistory(dbconfig, username, search_type, query);
		
			
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}
		
		
		// perform search task on musiclibrary		
		// 2. load song_data_processor content
		
		// load musicLibrary content
		ThreadSafeMusicLibrary threadSafeML = (ThreadSafeMusicLibrary) request.getServletContext().getAttribute(MUSIC_LIB);
		
		// create a JSONArray
		JSONArray searchResultsArray = null;
		// check searchType
		if(search_type.equals(ARTIST)){
			searchResultsArray = threadSafeML.searchByArtist(query);
		}
		else if(search_type.equals(SONG_TITLE)){
			searchResultsArray = threadSafeML.searchByTitle(query);
		}
		else if(search_type.equals(TAG)){
			searchResultsArray = threadSafeML.searchByTag(query);
		}
		
		// prepare to build html page
	
		// set header
		buffer.append(initHtmlAndTitle("Song Page"));
		
		// set style (css)
		buffer.append(style());
				
		// header of search page - Song Finder
		buffer.append(header("Song Finder"));
		
		
		// css style float left
		buffer.append(divClass("alignleft"));
		
		// display last login time
		buffer.append("Last Login Time: " + loginUserTimeStamp);
		
		// css close
		buffer.append(divClose());
		
		// css style float right
		buffer.append(divClass("alignright"));	
		
		// login welcome message
		buffer.append(loginWelcomeMsg(username));		
	
		// logout link
		buffer.append(logoutLink());
	
		/*** need add a FavList link to display all added song ***/		
		// show fav table list icon
		
		// css style for icon
		buffer.append(divClass("fav_icon"));
		
		// show favorite list icon
		buffer.append(showFavListIcon());
		
		// inner closing for icon
		buffer.append(divClose());
		
		// outer closing for right container
		buffer.append(divClose());
		
		// css style
		buffer.append(divClass("welcome_msg_style"));
		
		// welcome message
		buffer.append(welcomeMsg("Welcome to song finder! Select a search type and type in a query and we'll display you a list of similar songs you might like!"));
		
		buffer.append(divClose());
		
		// horizontal line
		buffer.append(horizontalLine());
		
		// searchBar remain at song result page
		buffer.append(searchBar());
		
		// TOOD: added suggest search 
		buffer.append(goToSearchSuggestionButton());
		
		// added view search history
		buffer.append(goToViewSearchHistoryButton());
		
		// show all artist button
		buffer.append(showAllArtistsAlphabeticallyButton());
		
		
		// add show all artist playcount
		buffer.append(showAllArtistByPlayCountButton());
		
		// if user has click on song_info, display the clicked song artist + song title + returned similar song list
		if(songInfo != null){
			
			buffer.append(songInfoBar(songArtist, query));
			
		}
		
		// css style
		buffer.append(divClass("table_result"));
		
		
		// table format - artist <-> song title
		buffer.append(tableFormat("Artist", "Song Title", "Favorites"));
		
		// check if searchResults is empty or have results
		if(!searchResultsArray.isEmpty()){
			
			// loop through each similar_songObject in JSONArray
			// and build up the result_table	
			for(int i = 0; i < searchResultsArray.size(); i++){
				
				// get each similar song
				JSONObject song = (JSONObject) searchResultsArray.get(i);
				
				
				/** GOOD DEBUG  **/
//				System.out.println(song.toJSONString());
					
				favLock.lockRead();
			
				// ready to form each table row with added option to add favs song
				try {
					
					boolean userHashRecordInFavList = DBHelper.checkFavUsernameAndSongIDExist(dbconfig, username, (String)song.get("trackId"));
				
					// means this has been added to favTable in mySQL
					if(userHashRecordInFavList){
						
						// change to full star icon
						String imgPath = generateImgPath("fullStar");
						
						
						// build the result table content										
						buffer.append(tableContent(username, (String)song.get("artist"), 
								(String)song.get("title"), (String)song.get("trackId"), imgPath));
						
					}
					else {
						
						String imgPath = generateImgPath("emptyStar");					
					
					
						// build the result table content					
						buffer.append(tableContent(username, (String)song.get("artist"), 
								(String)song.get("title"), (String)song.get("trackId"), imgPath));
						
					}
				}
				catch (SQLException | NullPointerException e) {
					
					e.printStackTrace();
				}
				finally {
					favLock.unlockRead();
				}
			
			}
			
			// closing table </table> <-- NOTE
			buffer.append("</table>");
			
			buffer.append(divClose());
			
			// finish building the table - added footer()
			buffer.append(footer());
			
		}
		else {
			// error message
			buffer.append(goToSearchButton());
			buffer.append(notFound(search_type, query));
			
		}
				
		// print out html page
		writer.println(buffer);  
			
	}
	
	// give the correct path that fetch to the star icon
	private String generateImgPath(String starStatus){
		
		String path;
	
		if(starStatus.equals("fullStar")){
			path = "https://maxcdn.icons8.com/Color/PNG/24/Messaging/filled_star-24.png\" title=\"Star Filled\" width=\"24\"";
			
		}
		else{
			path = "https://maxcdn.icons8.com/Color/PNG/24/Astrology/outlined_star-24.png\" title=\"Star Empty\" width=\"24\"";
		}
		

		return path;
		
		
	}
	
	

}
