package cs212.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cs212.data.ThreadSafeMusicLibrary;
import cs212.util.concurrent.ReentrantLock;

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
		
			// 1. base case - check user login
			boolean userLogin = checkUserLogin(session, response);
			
			if(!userLogin){
				// redirect to login page					
				response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS +  "=" + NOT_LOGGED_IN));
				return;
			}
			
			
			// check if user already has fav list records on mySQL fav database table
			String loginUserHasFavList = (String)session.getAttribute(HAS_FAV_SONG_LIST_RECORD);		
			String loginUsername = (String) session.getAttribute(USERNAME);
			
			// generate html page
			// get writer
			PrintWriter writer = prepareResponse(response);
			
			// String buffer		
			StringBuffer buffer = new StringBuffer();
			
			// set header
			buffer.append(initHtmlAndTitle("All Artist Page"));
			
			// set style (css)
			buffer.append(style());
					
			// header of search page - Song Finder
			buffer.append(header("All Artist Page"));
			
			// styling to right for welcome user messag
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
			buffer.append(welcomeMsg("All Artists' name displayed alphabetically!"));
			
			// horizontal line
			buffer.append(horizontalLine());

			// searchBar remain at song result page
			buffer.append(searchBar());
			
			// show all artist button
			buffer.append(showAllArtistsButton());
			
			// css style
			buffer.append(divClass("table_result"));
			
			// display all artist table format
			buffer.append(allArtistTableFormat("Artists"));
			
			// load musicLibrary content
			ThreadSafeMusicLibrary threadSafeML = (ThreadSafeMusicLibrary) request.getServletContext().getAttribute(MUSIC_LIB);
			
			
			TreeSet<String> sortedArtists = threadSafeML.getSortedArtistName();
			
			for(String str : sortedArtists){
				
				buffer.append(displayArtistNameEachRow(str));
				
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
