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

public class ArtistInfoServlet extends MusicLibraryBaseServlet {
	
	
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
		
		/** Condition check - check user click on which artist info **/
		String artistInfo =  getParameterValue(request, "artistInfo");
		
		// get login user last time stamp
		String loginUserTimeStamp = (String) session.getAttribute(LOGIN_TIMESTAMP);
				
		// generate html page
		// get writer
		PrintWriter writer = prepareResponse(response);
		
		// String buffer		
		StringBuffer buffer = new StringBuffer();
		
		// LOGO
		buffer.append(logo());
		
		buffer.append(horizontalLine());
		
		// set header
		buffer.append(initHtmlAndTitle("Artist Information"));
		
		// set style (css)
		buffer.append(style());
				
		// header of search page - Song Finder
		buffer.append(header("Artist Information"));
		
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
		
		// fav welcome message
		buffer.append(welcomeMsg("Welcome to artist information page!"));
		
		// horizontal line
		buffer.append(horizontalLine());

		// searchBar remain at song result page
		buffer.append(searchBar());
		
		
		// TOOD: added suggest search 
		buffer.append(goToSearchSuggestionButton());
		
		// TODO: added view search history bar 
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
		
		
		buffer.append(tableHeadWithBody());
		// if user has clicked specific artistInfo, display the content here 
		if(artistInfo != null){
			
			try {
				
				JSONArray artistInfoArray = DBHelper.retrieveArtistInfoTableContent(dbconfig, artistInfo);
				
				boolean displayedImage = false; 
				
				for(int i = 0; i < artistInfoArray.size(); i++){
					
					JSONObject obj = (JSONObject) artistInfoArray.get(i);
					
					String name = (String)obj.get("name");
					String listeners = (String)obj.get("listeners");
					String playcount = (String)obj.get("playcount");
					String bio = (String)obj.get("bio");				
					String artistImage = (String)obj.get("image");
					
					
					if(!displayedImage){
						
						// set image style - to circle
						buffer.append(displayArtistImage(artistImage, name, "img-circle"));
						
						displayedImage = true;
					}
					
					
					// then display artist info
					buffer.append(displayArtistInfoTable(name,listeners, playcount, bio));
					
				}
				
				
			} 
			catch (SQLException e) {
				
				// if artistInfo can't be retrieve print error message
				buffer.append(errorMsg("Error in retrieve artist info"));
				e.printStackTrace();
			}
			
		}
		
		
		// closing table </table> <-- NOTE
//		buffer.append("</table>");
		
		buffer.append(divClose());
		
		// finish building the table - added footer()
		buffer.append(footer());
		
		
		// print out html page
		writer.println(buffer);
					
		return;
		
		
		
	}
	
}
