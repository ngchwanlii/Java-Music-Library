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
		
		
		// 2. check if logged_in user has click on the display favorite song icon
		// get searchType and query from session 
		
		String searchType = (String)session.getAttribute(SEARCH_TYPE);
		String query = (String)session.getAttribute(QUERY);
		
		
		/** FavListServlet condition  - check whether loginUser has click on show Favorite List Icon **/
		String showFavList = request.getParameter("showFavList");		
		// check if user already has fav list records on mySQL fav database table
		String loginUserHasFavList = (String)session.getAttribute(HAS_FAV_SONG_LIST_RECORD);		
		String loginUsername = (String) session.getAttribute(USERNAME);
		
		// check if user has Fav Song Records & CLICKED the show favorite song button
		boolean userClickedShowFavIcon = checkUserClickOnShowFavListIcon(showFavList, loginUserHasFavList);

		// if user has click show fav icon
		if(userClickedShowFavIcon){
			
			/** Generate favorite list page html inside music ibrary base servlet **/
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
			buffer.append(showAllArtistsButton());
			
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
