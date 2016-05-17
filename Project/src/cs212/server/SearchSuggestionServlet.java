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

public class SearchSuggestionServlet extends MusicLibraryBaseServlet  {
	
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
		
	
		// check if user clicked SEARCH SUGGESTIOn
		String clickedSearchSuggestionButton = (String) request.getParameter(SEARCH_SUGGESTION_BUTTON);
		
		// generate html page
		// get writer
		PrintWriter writer = prepareResponse(response);
		
		// String buffer		
		StringBuffer buffer = new StringBuffer();
		
		try {
			
		
			// get login user last time stamp
			String loginUserTimeStamp = (String) session.getAttribute(LOGIN_TIMESTAMP);
			
			// LOGO
			buffer.append(logo());
			
			buffer.append(horizontalLine());
			
			// set header
			buffer.append(initHtmlAndTitle("Search Suggestion Page"));
			
			
			// set style (css)
			buffer.append(style());
			
			buffer.append(header("Popular Search Suggestion Page"));
		
			// css style float left
			buffer.append(divClass("alignleft"));
			
			// display last login time
			buffer.append("Last Login Time: " + loginUserTimeStamp);
			
			// css close
			buffer.append(divClose());
			
		
			// css style float right
			buffer.append(divClass("alignright"));
			
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
			
			// css style
			buffer.append(divClass("welcome_msg_style"));
			
			// horizontal line
			buffer.append(horizontalLine());

			buffer.append(divClose());
			
			// searchBar remain at song result page
			buffer.append(searchBar());
			
			// added suggest search 
			buffer.append(goToSearchSuggestionButton());
			
			// add view search histroy
			buffer.append(goToViewSearchHistoryButton());
			
			// show all artist by ALPHABETICALLY button
			buffer.append(showAllArtistsAlphabeticallyButton());
			
			// show all artist by PLAY COUNT button
			buffer.append(showAllArtistByPlayCountButton());

			buffer.append(divClose());
			
			// css style
			buffer.append(divClass("alignright"));
			
			// Top 100 Artist Chart
			buffer.append(goToViewTop100ArtistChartButton());
			
			buffer.append(divClose());
			
			
			// css style
			buffer.append(divClass("table_result"));
			
			// align clear history button on right
			buffer.append(alignDivDirection("right"));
			
			buffer.append(divClass("button_style"));
			
			// don't need to have clearSearchHistory button in SearchSuggestion
			
			buffer.append(divClose());
			
			buffer.append(divClose());
			
			
			buffer.append(setSearchSuggestionTableFormat("Searched Type", "Searched Queries", "Search Counts"));
			
			
			// building searched history table content
			JSONArray searchedArray;
			
			// retrieve search suggestion
			// clickedSearchSuggestionButton has value of searchType
			searchedArray = DBHelper.retrieveSearchSuggestionTableContent(dbconfig, clickedSearchSuggestionButton);
			
			for(int i = 0; i < searchedArray.size(); i++){
				
				
				JSONObject obj = (JSONObject) searchedArray.get(i);
				String searchType = (String)obj.get("searchType");
				String searchQuery = (String)obj.get("searchQuery");
				String searchCount = (String)obj.get("searchCount");
			
				buffer.append(displaySearchSuggestionEachRow(searchType, searchQuery, searchCount));
			
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

		return;
	
	}
	
	
}
