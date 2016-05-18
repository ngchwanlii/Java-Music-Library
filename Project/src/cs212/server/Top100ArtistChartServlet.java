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

public class Top100ArtistChartServlet extends MusicLibraryBaseServlet { 
	
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
			buffer.append(initHtmlAndTitle("Top 100 Artists Chart"));
			
			// set style (css)
			buffer.append(style());
					
			// header of search page - Song Finder
			buffer.append(header("Top 100 Artists Chart"));
			
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
			
			// welcome msg
			buffer.append(welcomeMsg("Welcome, here's Top 100 Artists!"));
			
			buffer.append(divClose());
			
			// horizontal line
			buffer.append(horizontalLine());

			// css style float left
			
			buffer.append(divClass("alignleft"));
			
			// searchBar remain at song result page
			buffer.append(searchBar());
			
			// TOOD: added suggest search 
			buffer.append(goToSearchSuggestionButton());
			
			// TODO: added view search history button
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

			// table format
			buffer.append(tableFormat("Rank", "Artist Image", "Artist Name"));
			
			// Code logic here
			try {
				JSONArray top100ArtistsArray = DBHelper.retrieveTop100ArtistsChartContent(dbconfig);
				
				for(int i = 0; i < top100ArtistsArray.size(); i++){
						
					JSONObject obj = (JSONObject) top100ArtistsArray.get(i);
					String rank = (String)obj.get("rank");
					String image = (String)obj.get("image");
					String artist = (String)obj.get("artist"); 
								
					buffer.append(displaytop100ArtistChartContent(rank, image, artist));
					
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// closing table </table> <-- NOTE
			buffer.append("</table>");
			
			buffer.append(divClose());
			
			// finish building the table - added footer()
			buffer.append(footer());
			
			
			writer.println(buffer);
			
			return;
		}
		
		
		
	
}
