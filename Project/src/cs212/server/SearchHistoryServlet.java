package cs212.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import database.DBConfig;
import database.DBHelper;

public class SearchHistoryServlet extends MusicLibraryBaseServlet  {
	
	// both GET and POST is acceptable
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		// get session
		HttpSession session = request.getSession();
		
		// get dbconfig from web-container
		DBConfig dbconfig = (DBConfig) request.getServletContext().getAttribute(DBConfig.DBCONFIG);
		
		// 1. base case - check user login
		boolean userLogin = checkUserLogin(session, response);
		
		if(!userLogin){
			// redirect to login page					
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS +  "=" + NOT_LOGGED_IN));
			return;
		}
		
		
		// check if user already has fav list records on mySQL fav database table				
		String loginUsername = (String) session.getAttribute(USERNAME);
		
		String clickedClearSearchButton = (String) request.getParameter(CLEAR_SEARCH_HISTORY_BUTTON);
		
		// generate html page
		// get writer
		PrintWriter writer = prepareResponse(response);
		
		// String buffer		
		StringBuffer buffer = new StringBuffer();
		
		try {
			
			// if user clicked clear button, clear the searched history
			if(clickedClearSearchButton != null){
			
				
				DBHelper.clearSearchedHistory(dbconfig, loginUsername);
				
			}
			
			
	
			// set header
			buffer.append(initHtmlAndTitle("Search History Page"));
			
			// set style (css)
			buffer.append(style());
					
			// header of search page - Song Finder
			buffer.append(header("Search History Page"));
			
			// styling to right for welcome user messag
			buffer.append(alignDivDirection("right"));
			
			// login welcome message
			buffer.append(loginWelcomeMsg(loginUsername));		
		
			// logout link
			buffer.append(logoutLink());
			
			
			// css style for icon
			buffer.append(divClass("fav_icon"));
			
			// show favorite list icon
			buffer.append(showFavListIcon());
			
			// inner closing for icon
			buffer.append(divClose());
			
			// outer div close
			buffer.append(divClose());
			
			// horizontal line
			buffer.append(horizontalLine());

			// searchBar remain at song result page
			buffer.append(searchBar());
			
			// TODO: added view search history button
			buffer.append(goToViewSearchHistoryButton());
			
			// show all artist by ALPHABETICALLY button
			buffer.append(showAllArtistsAlphabeticallyButton());
			
			// show all artist by PLAY COUNT button
			buffer.append(showAllArtistByPlayCountButton());

			
			
			
			// css style
			buffer.append(divClass("table_result"));
			
			// align clear history button on right
			buffer.append(alignDivDirection("right"));
			
			buffer.append(divClass("button_style"));
			// clear history button
			buffer.append(clearSearchHistoryButton());
			
			buffer.append(divClose());
			
			buffer.append(divClose());
			
			// view search history table format
			buffer.append(setSearchHistoryTableFormat("Searched Type", "Searched Queries"));
			
			// building searched history table content
			
			// generate artist play count table
			JSONArray searchedHistoryArray = DBHelper.retrieveSearchHistoryTableContent(dbconfig, loginUsername);
			
			
			
			for(int i = 0; i < searchedHistoryArray.size(); i++){
				
				
				JSONObject obj = (JSONObject) searchedHistoryArray.get(i);
				String searchType = (String)obj.get("searchType");
				String searchQuery = (String)obj.get("searchQuery");
				
				buffer.append(displaySearchedHistoryEachRow(searchType, searchQuery));
				
				
			}
		
			// closing table </table> <-- NOTE
			buffer.append("</table>");
			
			buffer.append(divClose());
			
			// finish building the table - added footer()
			buffer.append(footer());
			
			
			
		}
		catch (SQLException e) {

			e.printStackTrace();
		}
		
		
		// print out html page
		writer.println(buffer); 

		
	
	}
	
	
	
	
	
	
}
