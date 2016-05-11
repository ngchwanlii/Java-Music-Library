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

import cs212.util.concurrent.ReentrantLock;
import database.DBConfig;
import database.DBHelper;

public class FavListServlet extends MusicLibraryBaseServlet {
	
	// both GET and POST is acceptable
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get session
		HttpSession session = request.getSession();
		
		// setting
		// get dbconfig from web-container
		DBConfig dbconfig = (DBConfig) request.getServletContext().getAttribute(DBConfig.DBCONFIG);
	
		// get favLock - this SongServlet (involve retrieve / update to favListTable)
		ReentrantLock favLock = (ReentrantLock) request.getServletContext().getAttribute(MusicLibraryBaseServlet.FAVTABLE_LOCK);
		
		
		// 1. base case - check user login
		boolean userLogin = checkUserLogin(session, response);
		
		if(!userLogin){
			// redirect to login page					
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS +  "=" + NOT_LOGGED_IN));
			return;
		}
		
		
		// 1. check if logged_in user has click on the display favorite song icon
		 
		
		/** FavListServlet condition  - check whether loginUser has click on show Favorite List Icon **/				
		// check if user already has fav list records on mySQL fav database table
		
		/*** FIXED CODE REVIEW POINT 
		 * 
		 * Removed showFavList, use session for the checking 
		 *
		 **/
		String loginUsername = (String)session.getAttribute(USERNAME);		
	
		
		// if user has click show fav icon
		if(loginUsername != null){
			
			/** Generate favorite list page html inside music library base servlet **/
			// generate a page that show all favorite added song
			// get writer
			PrintWriter writer = prepareResponse(response);
			
			// String buffer		
			StringBuffer buffer = new StringBuffer();
			
			// set header
			buffer.append(initHtmlAndTitle("Favorite Song List Page"));
			
			// set style (css)
			buffer.append(style());
					
			// header of search page - Song Finder
			buffer.append(header("Favorite Song List"));
			
		 
			// styling to right
			buffer.append(alignDivDirection("right"));
					
			// login welcome message
			buffer.append(loginWelcomeMsg(loginUsername));		
		
			// logout link
			buffer.append(logoutLink());
			
			
			// css style for icon
			buffer.append(divClass("fav_icon"));
			
			// show favorite list icon
			buffer.append(showFavListIcon(loginUsername));
			
			// inner closing for icon
			buffer.append(divClose());
			
			// outer div close
			buffer.append(divClose());

			// fav welcome message
			buffer.append(welcomeMsg("Here your favorite song list!"));
			
			// horizontal line
			buffer.append(horizontalLine());
			
			// searchBar remain at song result page
			buffer.append(searchBar());
			
			// show all artist button
			buffer.append(showAllArtistsAlphabeticallyButton());
			
			// add show all artist playcount
			buffer.append(showAllArtistByPlayCountButton());
			
			// css style
			buffer.append(divClass("table_result"));
			
			// set fav table foramt
			buffer.append(favTableFormat("Artist", "Song Title", "Song Track ID"));
			
			// need to acquire write lock
			favLock.lockRead();
			
			try {
				
				
				/** FIXED CODE REVIEW POINT -returned as an JSONArray, then use buffer to generate the html page content **/
				// create up fav table content 
				JSONArray favContentArray = DBHelper.retrieveFavTableContent(dbconfig, loginUsername);
				
				for(int i = 0; i < favContentArray.size(); i++){
					
					JSONObject obj = (JSONObject) favContentArray.get(i);
					
					String artist = (String)obj.get("artist");
					String songTitle = (String)obj.get("songTitle");
					String songTrackID = (String)obj.get("trackID"); 
					
					buffer.append(favListTableContent(artist, songTitle, songTrackID));
					
				}		
			}
			catch (SQLException e){
				
			}
			finally {
				favLock.unlockRead();
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
	
	
	
}
