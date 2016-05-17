package cs212.server;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cs212.util.concurrent.ReentrantLock;
import database.DBConfig;
import database.DBHelper;

public class CheckServlet extends MusicLibraryBaseServlet {
	
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
		
		// check delete icon
		String deleteClicked = request.getParameter("delete");
		
		if(deleteClicked != null){
			
			String favusername = request.getParameter("favusername");
			String trackID = request.getParameter("trackid");
			
			try {
				
				DBHelper.deleteFavorite(dbconfig, favusername, trackID);
				
				// after delete, redirect back to favList
				response.sendRedirect(response.encodeRedirectURL("/favlist"));
				return;
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			
			
			
		}
		
		
		
		String searchType = (String)session.getAttribute(SEARCH_TYPE);
		String query = (String)session.getAttribute(QUERY);
	
		
		// 3. check if user clicked add to Fav Song link 
		boolean userClickedAddFavSong = checkAddFavSongAction(session, request, response, dbconfig, favLock);
		
		if(userClickedAddFavSong){
			// redirect back to same page, just the star icon changed IMPORTANT STEP - [used searchType & query to identify this]!
			// send the queried, and search_type back to song page ( this will verified and help display fullstar added favorite song icon)
			response.sendRedirect(response.encodeRedirectURL("/song?search_type=" + searchType  +  "&query=" +  query));
			return;
		
		}
		else {
			response.sendRedirect(response.encodeRedirectURL("/song"));
			// return the response
			return;
		}
		
		
		
		
		
	}
	
}
