package cs212.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cs212.util.concurrent.ReentrantLock;
import database.DBConfig;
import database.DBHelper;

public class SearchServlet extends MusicLibraryBaseServlet {
	
	/**
	 * GET /search:
	 *  
	 * 1. return a search web page
	 * 2. allow user to select specific searchType
	 * 3. allow user to type in a search queries 
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		// setting 
		
		// get session
		HttpSession session = request.getSession();
		
		// get dbconfig from web-container
		DBConfig dbconfig = (DBConfig) request.getServletContext().getAttribute(DBConfig.DBCONFIG);
		
		// get favLock - this SongServlet (involve retrieve / update to favListTable)
		ReentrantLock favLock = (ReentrantLock) request.getServletContext().getAttribute(MusicLibraryBaseServlet.FAVTABLE_LOCK);
		
		// ready a writer
		PrintWriter writer = prepareResponse(response);
		
		// String buffer		
		StringBuffer buffer = new StringBuffer();
		
	
		// check if this user is logged in or not
		String loggedIn = (String) session.getAttribute(LOGGED_IN);
		
		// get username from session's object that bind with attribute name [USERNAME]
		String username = (String) session.getAttribute(USERNAME);
		
		// user must loggedIn or signup to use search function
		if(loggedIn == null){
			
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS +  "=" + NOT_LOGGED_IN));
			return;
		}
		
		// check if the user has Fav Song Records
		try {
			
			favLock.lockRead();
			boolean loginUserHasFavSongRecords = DBHelper.favTableUsernameExist(dbconfig, username);
			
			// if user has Fav Song Record - set attribute FavSongRecordExists 
			if(loginUserHasFavSongRecords){
				
				session.setAttribute(HAS_FAV_SONG_LIST_RECORD, username);
				
			}
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			favLock.unlockRead();
		}
		
		
		
		// set header of html page
		buffer.append(initHtmlAndTitle("Search Page"));
		
		// get style (css)
		buffer.append(style());
		
		// header of search page - Song Finder
		buffer.append(header("Song Finder"));
			
	
		// styling to right
		buffer.append(alignDivDirection("right"));
		
		
		// login welcome message
		buffer.append(loginWelcomeMsg(username));		
	
		// logout link
		buffer.append(logoutLink());
		
		// css style for icon
		buffer.append(divClass("fav_icon"));
		
		// show favorite list icon
		buffer.append(showFavListIcon(username));
		
		// inner closing for icon
		buffer.append(divClose());
		
		// close div
		buffer.append(divClose());
		
		// welcome message
		buffer.append(welcomeMsg());
		
		// horizontal line
		buffer.append(horizontalLine());
		
		// form that has to be submit to SongServlet.class
		buffer.append(searchBar());
		
		// footer
		buffer.append(footer());
					
		
		writer.println(buffer);
		
	}
	

	
	
}
