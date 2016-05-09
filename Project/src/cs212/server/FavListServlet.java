package cs212.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	
	
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get session
		HttpSession session = request.getSession();
		
		// setting
		// get dbconfig from web-container
		DBConfig dbconfig = (DBConfig) request.getServletContext().getAttribute(DBConfig.DBCONFIG);
	
		// get favLock - this SongServlet (involve retrieve / update to favListTable)
		ReentrantLock favLock = (ReentrantLock) request.getServletContext().getAttribute(MusicLibraryBaseServlet.FAVTABLE_LOCK);
		
		
		// check if status is correct to access this fav_list page (user should login 1st to get to this page)
		String loggedIn =  (String) session.getAttribute(LOGGED_IN);
		
		// base case check
		// which mean haven't login yet
		if(loggedIn == null){
			
			// redirect to login page			
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS +  "=" + NOT_LOGGED_IN));
			return;
		}
		
		
		/** condition check 2 -  CHECK LOGGED IN USER CLICK SHOW FAV LIST ICON **/
		
		// check if user has click the showFavList icon
		String showFavList = request.getParameter("showFavList");
		// check if user already has fav list records on mySQL fav database table
		String loginUserHasFavList = (String)session.getAttribute(HAS_FAV_SONG_LIST_RECORD);		
		String loginUsername = (String) session.getAttribute(USERNAME);
		
		
		// if user has clicked show fav table icon + user already has record favList records in mySQL favList database
		if( showFavList != null && loginUserHasFavList != null ){
			
		
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
			buffer.append(favListWelcomeMsg());
			
			// horizontal line
			buffer.append(horizontalLine());
			
			// searchBar remain at song result page
			buffer.append(searchBar());
			
			// css style
			buffer.append(divClass("table_result"));
			
			// set fav table foramt
			buffer.append(favTableFormat("Artist", "Song Title", "Song Track ID"));
			
			// need to acquire write lock
			favLock.lockRead();
			
			try {
			
				// make up fav table content 
				DBHelper.generateFavTableContent(dbconfig, buffer, loginUsername);
				
				
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
			
		
		/** condition 3 - CHECK ADD TO FAV SONG ACTION and setting for addToFavorite**/
		// check if user has clicked add to Fav Song link or not
		
		/** RESUME LATER **/
		// extra condition check if user has clicked the favList icon		
		String favUsername = request.getParameter("favusername");
		String artist = request.getParameter("artist");
		String songTitle = request.getParameter("songtitle");		
		String songTrackID = request.getParameter("trackid");
	
		
		
		// get searchType and query from session 
		 String searchType = (String)session.getAttribute(SEARCH_TYPE);
		 String query = (String)session.getAttribute(QUERY);
		 
		 // remove session after use
		 session.removeAttribute(SEARCH_TYPE);
		 session.removeAttribute(QUERY);
		 
		 
		// if user has clicked the link, add the favorite song and update to mySQL table
		if(favUsername != null && artist != null && songTitle != null && songTrackID != null){
			
			// acquire write lock to write to mySQL favTable 
			favLock.lockWrite();
	
			//update to mySQL favTable			
			boolean userHashRecordInFavList; 
			
			try {
			
				// added to fav mySQL table
				// check if user already has a record in fav list, don't add this song because it's already in mySQL favlist table 
				
				userHashRecordInFavList = DBHelper.checkFavUsernameAndSongIDExist(dbconfig, favUsername, songTrackID);
				
				session.setAttribute(HAS_FAV_SONG_LIST_RECORD, favUsername);
				
				if(!userHashRecordInFavList) {
			
					DBHelper.addFavorite(dbconfig, favUsername, artist, songTitle, songTrackID);
			
					// redirect back to same page, just the star icon changed IMPORTANT STEP - [used searchType & query to identify this]!
					// send the queried, and search_type back to song page ( this will verified and help display fullstar added favorite song icon)
					response.sendRedirect(response.encodeRedirectURL("/song?search_type=" + searchType  +  "&query=" +  query));
					return;
				}
				else {
					
					response.sendRedirect(response.encodeRedirectURL("/song?search_type=" + searchType  +  "&query=" +  query));
					return;
					
				}
				
			
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			finally {
				// release writelock
				favLock.unlockWrite();
			}
			
		}
		else {			
			
			response.sendRedirect(response.encodeRedirectURL("/song"));
			return;
			
		}		
	}
	
	
	
}
