package cs212.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cs212.data.ThreadSafeMusicLibrary;
import database.DBConfig;
import database.DBHelper;

public class AllArtistServlet extends MusicLibraryBaseServlet {
	
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
			
			// check the clicked SHOW ALL BUTTON TYPE
			String showType = getParameterValue(request, "showtype");
			
		
			// generate html page
			// get writer
			PrintWriter writer = prepareResponse(response);
			
			// String buffer		
			StringBuffer buffer = new StringBuffer();
			
			// LOGO
			buffer.append(logo());
			
			buffer.append(horizontalLine());
			
			// set header
			buffer.append(initHtmlAndTitle("All Artist Page"));
			
			// set style (css)
			buffer.append(style());
					
			// header of search page - Song Finder
			buffer.append(header("All Artist Page"));
			
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
			if(showType.equals("byAlphabet")){
				buffer.append(welcomeMsg("All Artists' name displayed alphabetically!"));
			}
			else if(showType.equals("byPlayCount")) {
				buffer.append(welcomeMsg("All Artists' name displayed based on playcount in ascending order!"));
			}
			
			buffer.append(divClose());
			
			// horizontal line
			buffer.append(horizontalLine());

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
			
			if(showType.equals("byAlphabet")){
				// display all artist table format
				buffer.append(allArtistByAlphabetTableFormat("Artists"));
			}
			else if(showType.equals("byPlayCount")){
				buffer.append(allArtistByPlayCountTableFormat("Artists", "Playcount"));
			}
			
			
			
			if(showType.equals("byAlphabet")){
				// load musicLibrary content
				ThreadSafeMusicLibrary threadSafeML = (ThreadSafeMusicLibrary) request.getServletContext().getAttribute(MUSIC_LIB);
				
				TreeSet<String> sortedArtists = threadSafeML.getSortedArtistName();
				
				for(String str : sortedArtists){
					
					buffer.append(displayArtistNameEachRow(str));
					
				}
			}
			else if(showType.equals("byPlayCount")) {
				
				try {
					
					// generate artist play count table
					JSONArray artistPlayCountContentArray = DBHelper.retrieveArtistByPlayCountTableContent(dbconfig);
					
					for(int i = 0; i < artistPlayCountContentArray.size(); i++){
						
						
						JSONObject obj = (JSONObject) artistPlayCountContentArray.get(i);
						String artist = (String)obj.get("artist");
						String playcount = (String)obj.get("playcount");
						
						buffer.append(displayArtistNameAndPlayCountEachRow(artist, playcount));
						
						
					}
					
					
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				
			}
			
			// closing table </table> <-- NOTE
			buffer.append("</table>");
			
			buffer.append(divClose());
			
			// finish building the table - added footer()
			buffer.append(footer());
			
			
			// print out html page
			writer.println(buffer);
						
			return;
			
			
			
		}
		
		
}
