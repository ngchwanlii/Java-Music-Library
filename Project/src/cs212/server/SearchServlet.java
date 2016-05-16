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
		
	
		
		
		// get username from session's object that bind with attribute name [USERNAME]
		String username = (String) session.getAttribute(USERNAME);
		
		
		/****************************************
		 *	CONDITION 1 - check user login 	 	*
		 ****************************************/
		
		// 1. base case - check user login
		boolean userLogin = checkUserLogin(session, response);
		
		if(!userLogin){
			// redirect to login page					
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS +  "=" + NOT_LOGGED_IN));
			return;
		}
		
		
		/********************************************************************************
		 *		CONDITION 2 - check user has fav song records 	 						*
		 ********************************************************************************/
		
		// check if the user has Fav Song Records
		try {
			
			favLock.lockRead();
			boolean loginUserHasFavSongRecords = DBHelper.favTableUsernameExist(dbconfig, username);
			
			// if user has Fav Song Record - set attribute FavSongRecordExists 
			if(loginUserHasFavSongRecords){
				
				session.setAttribute(HAS_FAV_SONG_LIST_RECORD, username);
				
			}
			
			
		} 
		catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			favLock.unlockRead();
		}
		
		// get login user last time stamp
		String loginUserTimeStamp = (String) session.getAttribute(LOGIN_TIMESTAMP);
		

		// set header of html page
		buffer.append(initHtmlAndTitle("Search Page"));
		
		// get style (css)
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
		
		// css style for icon
		buffer.append(divClass("fav_icon"));
		
		// show favorite list icon
		buffer.append(showFavListIcon());
		
		// inner closing for icon
		buffer.append(divClose());
		
		// close div
		buffer.append(divClose());
		
		// css style
		buffer.append(divClass("welcome_msg_style"));
		
		// welcome message
		buffer.append(welcomeMsg("Welcome to song finder! Select a search type and type in a query and we'll display you a list of similar songs you might like!"));
		
		buffer.append(divClose());
		
		// horizontal line
		buffer.append(horizontalLine());
		
		// form that has to be submit to SongServlet.class
		buffer.append(searchBar());
		
		// TODO: added view search history button
		buffer.append(goToViewSearchHistoryButton());
		
		// show all artist alpha button
		buffer.append(showAllArtistsAlphabeticallyButton());
		
		// add show all artist playcount
		buffer.append(showAllArtistByPlayCountButton());
		
		// footer
		buffer.append(footer());
					
		
		writer.println(buffer);
		
	}
	

	
	
}
