package cs212.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cs212.util.concurrent.ReentrantLock;
import database.DBConfig;
import database.DBHelper;

/**
 * 
 * Base class for all servlets for MusicLibrary web application
 * @author Jay Ng
 * @date 04/12/2016
 * 
 */
public class MusicLibraryBaseServlet extends HttpServlet {
		
	// general variable
	public static final String MUSIC_LIB = "music_library";
	public static final String DATA = "data";
	public static final String USERNAME = "username";
	public static final String FULLNAME = "fullname";		
	public static final String PASSWORD = "password";
	public static final String CONFIRMPASSWORD = "confirmpassword";
	public static final String NEWPASSWORD = "new_password";
	public static final String STATUS = "status";
	
	// few type of error
	public static final String ERROR = "error";
	public static final String USERNAME_TAKEN_ERROR = "username_taken_error";
	public static final String PASSWORD_NOT_MATCH_ERROR = "password_not_match_error";
	public static final String USERNAME_OR_PASSWORD_NOT_EXIST = "username_or_password_does_not_exist_in_user_database";
	
	// check login status
	public static final String LOGGED_IN = "logged_in";
	public static final String NOT_LOGGED_IN = "not_logged_in";
	public static final String LOGIN_USERNAME_NOT_MATCH_ERROR = "username_not_match_error";
	public static final String LOGIN_PASSWORD_NOT_MATCH_ERROR = "login_not_match_error";
	

	// check logout status
	public static final String LOGOUT = "logged_out";
	// value of LOGOUT_LINK
	
	
	// page type
	// use for verify  user to identify which form to process
	public static final String PAGENAME =  "pagename";
	public static final String LOGINPAGE =  "loginpage";
	public static final String SIGNUPPAGE =  "signuppage";
	public static final String CHANGEPASSWORDPAGE =  "changepasswordpage";
	
	
	// favList page status track
	public static final String HAS_FAV_SONG_LIST_RECORD =  "has_fav_song_record";
	public static final String SEARCHED_QUERY_AS_LOGIN_USER =  "searched";
	public static final String SHOW_FAV_LIST =  "show_fav_list";
	
	
	// SongServlet page status track
	public static final String SEARCH_TYPE = "search_type";
	public static final String QUERY = "query";
	public static final String SONG_INFO = "songInfo";	
	public static final String ARTIST = "artist";
	public static final String SONG_TITLE = "song_title";
	public static final String TAG = "tag";

	// lock mechanism
	// userLock
	public static final String USERTABLE_LOCK = "user_table_reentrant_lock";
	// favLock
	public static final String FAVTABLE_LOCK = "fav_table_reentrant_lock";
	
	
	/** NEW UPDATE **/
	// change password
	public static final String CHG_PASSWORD = "chg_password";
	// base color for artist image
	public static final String BLUE_COLOR = "#55ACEE";
	// clear search button
	public static final String CLEAR_SEARCH_HISTORY_BUTTON = "clear_search_history";
	// search suggestion
	public static final String SEARCH_SUGGESTION_BUTTON = "searchSuggestion";
	
	// login user time stamp
	public static final String LOGIN_TIMESTAMP = "time";
	
	
	// HTML 200 - OK
	// set content type and status
	// return a print writer
	protected PrintWriter prepareResponse(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		return response.getWriter();

	}
	
	// getParameterValue - handy method 
	// when a request provided from any servlet class, return the value of the parameter
	protected String getParameterValue(HttpServletRequest request, String key) {
		return request.getParameter(key);
	}
	
	
	// css/js styling
	protected String style(){
		
		return "<style>" 
				
				+ "h1, h2, p, label {"
				+ "font-family: verdana;"
				+ "}"
				
				+ "table {"
				+ "border-collapse: collapse;"
				+ "width: 100%;"
				+ "}"

				+ "th, td {"				
				+ "padding: 8px;"
				+ "}"	

				+ "tr:nth-child(even){background-color: #f2f2f2}"

				+ "th {"
				+ "background-color: #5DB761;"
				+ "color: white;"
				+ "}"
				
				
				
				
				+ "#button-green {"
				+ "font-family: 'Montserrat', Arial, Helvetica, sans-serif;"
				+ "width: 100%;"
				+ "border: #fbfbfb solid 4px;"
				+ "cursor:pointer;"
				+ "background-color: #00CD00;"
				+ "color:white;"
				+ "font-size:14px;"
				+ "padding-top:12px;"
				+ "padding-bottom:12px;"
				+ "margin-top:-4px;"
				+ "margin-bottom: 0px;"
				+ "font-weight:800;"
				+ "}"

				+ "#button-green:hover{"
				+ "background-color: white;"
				+ "color: #00CD00;"
				+ "}"
				
				
				+ "#button-blue{"
				+ "font-family: 'Montserrat', Arial, Helvetica, sans-serif;"
				+ "width: 150px;"
				+ "border: #fbfbfb solid 4px;"
				+ "cursor:pointer;"
				+ "background-color: #3B5998;"
				+ "color:white;"
				+ "font-size:14px;"
				+ "padding-top:12px;"
				+ "padding-bottom:12px;"				
				+ "font-weight:800;"
				+ "}"

				+ "#button-blue:hover{"
				+ "background-color: white;"
				+ "color: #3B5998;"
				+ "}"
				
				
				+ "#button-grey{"
				+ "font-family: 'Montserrat', Arial, Helvetica, sans-serif;"				
				+ "width: 150px;"
				+ "border:black solid 2px;"
				+ "cursor:pointer;"
				+ "background-color: #F1F1F1;"
				+ "color:black;"
				+ "font-size:14px;"
				+ "padding-top:12px;"
				+ "padding-bottom:12px;"				
				+ "font-weight:800;"
				+ "}"
				
				+ "#button-blue:hover{"
				+ "background-color: white;"
				+ "color: #F1F1F1;"
				+ "}"
				
				
				+ ".submit:hover {"
				+ "color: #00CD00;"
				+ "}"
				
				+ ".submit_blue:hover {"
				+ "color: #3B5998;"
				+ "}"
				
				+ ".submit_grey:hover {"
				+ "color: black;"
				+ "}"
				
				
				+ "body {"
				+ "background-color:white;"
				+ "}"
				
				+ "hr {"
				+ "display: block;"
				+ "height: 5px;"
				+ "border: 0;"
				+ "border-top: 1px solid #00CD00;"
				+ "margin: 1em 0;"
				+ "padding: 0;"
				+ "}"
				
				
				
				+ "input[type=submit] {"
				+ "font-family: verdana;"
				+ "font-weight: bold;"
				+ "}"
				
				+ "select {"
				+ "padding: auto;"
				+ "margin-right: 5px"
				+ "}"
				
				+ ".center {"
				+ "margin: auto; "
				+ "width: 100%; "				
				+ "padding: 10px;}"
				+ "form {display: inline;}"
				
				+ ".form {" 
				+ "margin: auto;"				
				+ "width:250px;"
				+ "width:250px;"
				+ "padding-top: 100px;}"
				
				+ ".bottom_form {"				
				+ "margin: auto;"				
				+ "width:250px;"
				+ "width:250px;"
				+ "padding-top: 5px;"
				+ "padding-left: 30px;"				
				+ "}"
				
				+ ".table_result{"
				+ "padding-top: 50px;}"
				
				+ ".fav_icon {"
				+ "padding-right: 80px;"
				+ "padding-top: 20px;"
				+ "}"
				
				+ ".img-circle {"
				+ "border-radius: 50%;"
				+ "}"
				
				+ ".button_style {"
				+ "display: inline;"
				+ " padding: 0px 2px 0px 2px;"
				+ "}"
				
				+ ".alignleft {"
				+ "float: left;"
				+ "}"
				
				+ ".alignright {"
				+ "float: right;"
				+ "}"
				
				+ ".welcome_msg_style {"
				+ "padding-top: 100px;"
				+ "}"
				
				+ ".clear_button_style {"
				+ " padding-bottom: 2px;"
				+ "}"
				
				+ "a {"
				+ "text-decoration: none;"
				+ "color: black;"
				+ "}"
			
				+ ".view_search_history_padding{"
				+ "padding-left: 5px;"				
				+ "}"

				+ ".icon {" 
				+ "width: 2px;"
				+ "height: 2px;"
				+ "margin-top: 10px"
				+ "display: inline-block;" 				
				+ "vertical-align: middle;"
				+ "}"
	
				+ ".logo {"
				+ "background-position: left center;"
				+ "background-repeat: no-repeat;"
				+ "padding-left: 16px;"
				+ "}"
				
				
				+ "</style>";
					
	}
	
	
	/************************
	 * 						*
	 *  Header zone			*
	 * 						*
	 ************************/	
	// set the title of header
	// for Project 3 - part 2 - Web Interface
	// title = Song Finder
	protected String initHtmlAndTitle(String title){
		
		return "<html><head><title>" + title + "</title></head>"; 
		
	}
	
	protected String logo(){
		return "<p style='line-height: 30px'><img src='https://lh6.ggpht.com/REHOrd6LmH1HaQTJLMr32LFwXTOI0IBJyrvcLVhteFc2HbUEdPctYSoX-fNG8Ra_pg=w500' style='vertical-align: middle' alt='Music Never Sleep' height='150' width='150'><h1>Musicfy</h1></p>";
	}
	
	// java script for facebook
	protected String javaScript(){
		
		return "  <body>"
				+ "<script>"
				+ "function statusChangeCallback(response) {"
				+ "console.log('statusChangeCallback');"
				+ "console.log(response);"
				+ "if (response.status === 'connected') {"
				+ "testAPI();}" 
				+ "else if (response.status === 'not_authorized') {"
				+ "document.getElementById('status').innerHTML = 'Please log ' + 'into this app.';}"
				+ " else {"
				+ "document.getElementById('status').innerHTML = 'Please log ' + 'into Facebook.';}"
				+ "}"
//				+ "function checkLoginState() {"
//				+ "FB.getLoginStatus(function(response) {"
//				+ "statusChangeCallback(response);"
//				+ "});"
//				+ "}"
				
				+ "function checkLoginState() {"
				+ "FB.getLoginStatus(function(response) {"
				+ "statusChangeCallback(response);"
				+ "});"
				+ "}"
				
				
				+ "window.fbAsyncInit = function() {"
				+ "FB.init({"
				+ "appId      : '228334240883505',"
				+ "xfbml      : true,"
				+ "version    : 'v2.6'});"
				+ "};"
				
				
//				+ "FB.getLoginStatus(function(response) {"
//				+ "statusChangeCallback(response);"
//				+ "});"
				
				
				+ "(function(d, s, id){"
				+ "var js, fjs = d.getElementsByTagName(s)[0];"
				+ "if (d.getElementById(id)) {return;}"
				+ "js = d.createElement(s); js.id = id;"
				+ "js.src = \"//connect.facebook.net/en_US/sdk.js\";"
				+ "fjs.parentNode.insertBefore(js, fjs);"
				+ "}(document, 'script', 'facebook-jssdk'));"
				
				+ "function testAPI() {"
				+ "console.log('Welcome!  Fetching your information.... ');"
				+ "FB.api('/me', function(response) {"
				+ "console.log('Successful login for: ' + response.name);"
				+ "document.getElementById('status').innerHTML = 'Thanks for logging in, ' + response.name + '!';"
				+ "});"
				+ "}"
				
				
				
				
				+ "</script>";
	}
	
	// TODO: added new facebook plugin button
	protected String faceBookLoginButton(){
		
//		return "<center><div class=\"fb-login-button\"" 
//				+ "width=\"300px\""
//				+ "data-max-rows=\"1\"" 
//				+ "data-size=\"large\"" 
//				+ "data-show-faces=\"false\"" 
//				+ "data-auto-logout-link=\"false\""
//				+ "onlogin=\"checkLoginState();\">"
//				+ "Login with Facebook"
//				+ "</div></center>";
		
		return "<div class='bottom_form'>"
				+ "<fb:login-button size=\"large\" scope=\"public_profile,email\"  onlogin=\"checkLoginState();\">"
				+ "Login with Facebook"
				+ "</fb:login-button><br>"
				+ "</div>";

//		<fb:login-button size="xlarge"
//                onlogin="Log.info.bind('onlogin callback')">
		
	}
	
	
	
	
	
	
	
	// return boldHeader
	protected String header(String boldTitle){
	
		return "<div class=\"center\"><center><h2>" + boldTitle + "</h2></center><br/></div>";
		
	}
	
	/************************
	 * 						*
	 *  Form type			*
	 * 						*
	 ************************/
	
	// return a search type with drop downlist + query input text field
	protected String searchBar(){
	
		String search_form = "<label>Search Type:</label>"				
				+ "<form action=\"song\" method=\"get\">"
				+ "<select name=\"search_type\">"	// note search_type here
				+ "<option value=\"artist\">Artist</option>"
				+ "<option value=\"song_title\">Song Title</option>"
				+ "<option value=\"tag\">Tag</option>"    				
				+ "</select>";

		String query_form = "<label> Query:</label>"				
				+ "<input type=\"text\" name=\"query\">" // note query here
				+ "<div class=\"button_style\">"					
				+ "<input type=\"submit\" value=\"Search\">"
				+ "</div>"
				+ "</form>";
		
		
		return search_form + query_form;
		
	}
	
	/****************************************************************************************************************
	 * 																												*
	 *  										SignUp logic														*											*
	 * 																												*
	 *****************************************************************************************************************/
	
	
	
	// return a signup form
	protected String signUpForm(){
		
		// pass method use "post" here - we have to hide the parameters (where user type in their user info: username, password etc)  
		
		String signUpForm = "<div class =\"form\">"  
				+ "<form action=\"verifyuser\" method=\"post\">"
				+ "<label>Username:</label></br>"
				+ "<input type=\"text\" name=\"username\" style=\"width: 200px\"></br>"
				+ "<label>Fullname:</label></br>"
				+ "<input type=\"text\" name=\"fullname\" style=\"width: 200px\"></br>"
				+ "<label>Password:</label></br>"
				+ "<input type=\"password\" name=\"password\" style=\"width: 200px\"></br>"
				+ "<label>Confirm password:</label></br>"
				+ "<input type=\"password\" name=\"confirmpassword\" style=\"width: 200px\"></br>"
				+ "</br>"
				+ "<div class='submit'>"
				+ "<input type=\"submit\" value=\"Sign Up\" id='button-green' style=\"width: 200px\">"
				+ "<input type=\"hidden\" name=\"pagename\" value=\"signuppage\">"
				+ "</div>"
				+ "</form></div>";
				
		return signUpForm;
	}
	
	/********************************************END OF SignUp Logic *********************************************************/
	
	
	
	/****************************************************************************************************************
	 * 																												*
	 *  										LOGIN logic														*											*
	 * 																												*
	 *****************************************************************************************************************/
	
	
	// return a signup form
	protected String loginForm(){
		
		// pass method use "post" here - we have to hide the parameters (where user type in their user info: username, password etc)  
		
		String loginForm = "<div class = \"form\">"  
				+ "<form  action=\"verifyuser\" method=\"post\">"
				+ "<label>Username:</label></br>"
				+ "<input type=\"text\" name=\"username\" style=\"width: 200px\"></br>"
				+ "<label>Password:</label></br>"
				+ "<input type=\"password\" name=\"password\" style=\"width: 200px\"></br>"				
				+ "</br>"
				+ "<div class='submit'>"
				+ "<input type=\"submit\" value=\"Login\" id='button-green' style=\"width: 200px\">"
				+ "<input type=\"hidden\" name=\"pagename\" value=\"loginpage\">"
				+ "</div>"
				+ "</form></div>";
				
		return loginForm;
	}
	
	/** align right div - use for styling loginWelcomeMsg and logout link **/
	protected String alignDivDirection (String direction){
		return "<div align=\"" + direction +  "\">";
	}
	
	/** closing div helper **/
	protected String divClose(){
		return "</div>";
	}
	
	/** new added LoginWelcomeBar **/
	protected String loginWelcomeMsg(String username){
		
		username = username.substring(0, 1).toUpperCase() + username.substring(1);
	
		return "Hello, " +  username + "! |";	
		
	}
	
	/** new added Logout button link **/
	protected String logoutLink(){
		
		
		return " 	<a href=/logout>Logout</a>";
	}
	
	/** new added show fav list icon **/
	
	// need username + songTrackID parameters to show the favorite list from mySQL
	protected String showFavListIcon(){
		
				
		return "<a href=\"/favlist\">"
				+ "<img src=\"https://maxcdn.icons8.com/Color/PNG/48/Data/list-48.png\" title=\"Favorite List\" width=\"48\">"
				+ "</a>";
			
		
	}
	
	
	
	
	/********************************************END OF Login Logic *********************************************************/
	
	/****************************************************************************************************************
	 * 																												*
	 *  										Change new Password logic														*											*
	 * 																												*
	 *****************************************************************************************************************/
	
	
	// return a signup form
		protected String changePasswordForm(){
			
			// pass method use "post" here - we have to hide the parameters (where user type in their user info: username, password etc)  
			
			String changePasswordForm = "<div class =\"form\">"  
					+ "<form action=\"verifyuser\" method=\"post\">"
					+ "<label>Username:</label></br>"
					+ "<input type=\"text\" name=\"username\" style=\"width: 200px\"></br>"										
					+ "<label>Current Password:</label></br>"
					+ "<input type=\"password\" name=\"password\" style=\"width: 200px\"></br>"
					+ "<label>New password:</label></br>"
					+ "<input type=\"password\" name=\"new_password\" style=\"width: 200px\"></br>"
					+ "</br>"
					+ "<input type=\"submit\" value=\"Submit\" style=\"width: 200px\">"
					+ "<input type=\"hidden\" name=\"pagename\" value=\"changepasswordpage\">"				
					+ "</form></div>";
					
			return changePasswordForm;
		}
	
	
	
	
	
	// return welcome msg
	protected String welcomeMsg(String msg){
		// welcome message
		return "<center><p>" + msg + "</p></center>";
	}
	
	
	/************************
	 * 						*
	 *  Body/Content zone	*
	 * 						*
	 ************************/
	
	// return table format style
	protected String tableFormat(String col1, String col2, String col3){
		return "<body><table border=\"2px\" width=\"100%\">"				
				+ "<tr>"
				+ "<th><strong><center>" + col1 + "</center></strong></th>"
				+ "<th><strong><center>" + col2 + "</center></strong></th>"
				+ "<th><strong><center>" + col3 + "</center></strong></th>"
				+ "</tr>";
				
		
	}
	
	// return table format style
	protected String favTableFormat(String artistCol, String songTitleCol, String songTrackIDCol, String deleteFavs){
		return "<body><table border=\"2px\" width=\"100%\">"				
				+ "<tr>"
				+ "<th><strong><center>" + artistCol + "</center></strong></th>"
				+ "<th><strong><center>" + songTitleCol + "</center></strong></th>"				
				+ "<th><strong><center>" + songTrackIDCol + "</center></strong></th>"
				+ "<th><strong><center>" + deleteFavs + "</center></strong></th>"
				+ "</tr>";
	
	}
	
	
	// return table content 
	protected String tableContent(String username, String artist, String songTitle, String songTrackID,  String imgPath){
		
	

		return "<tr>"
				+"<td>" + artist + "</td>"								
				+"<td><a href=\"/song?search_type=song_title" +  "&query=" + songTitle + "&songInfo=" + songTitle + "&songArtist=" + artist +  "\">" + songTitle + "</td>"
				+"<td><center>"
				+ "<a href=\"/check?favusername=" + username + "&artist=" + artist + "&songtitle=" + songTitle + "&trackid=" +   songTrackID  + "\">" 
				+ "<img src=\"" + imgPath + "\">"
				+ "</a>"
				+ "</center>"
				+"</td>"
				+"</tr>";
		
	
	}
	
	// show the songInfo "artist + songTitle" when user click on a particular song 
	protected String songInfoBar(String artist, String songTitle){
		
		return "</br></br><h3>Song Info</h3>Artist: " + artist + "</br>Song Title: " + songTitle + "</br>";
		
	}
	
	
	// return favorite list table content 
	public static String favListTableContent(String artist, String songTitle, String songTrackID, String deleteIconAndParameter){
		
	
		return "<tr>"
				+"<td>" + artist + "</td>"
				+"<td>" + songTitle + "</td>"
				+"<td>" + songTrackID + "</td>"
				+"<td>" + deleteIconAndParameter + "</td>"			
				+"</tr>";
		
	}
	

	/************************
	 * 						*
	 *  Footer zone			*
	 * 						*
	 ************************/
	// return footer of html page
	protected String footer(){
		return "</body></html>";
	}
	

	/************************
	 * 						*
	 *  Utilities tool 		*
	 * 						*
	 ************************/
	
	// div class
	protected String divClass(String cssStyle){
		
		return "<div class=\"" + cssStyle + "\">";
		
	}
	
	
	/** DEBUG **/
	// errorMessage (for signup / login page)
	protected String errorMsg(String msg){
		
		return "<font color=\"red\"><center><h2 style=\"margin-top: 100px;\">" + msg + "</h2></center></font>";
		
	}
	
	
	// horizontal line
	protected String horizontalLine(){
		
		return "<hr>";
	}
	
	// break line
	protected String breakLine(){
		return "<br>";
	}

	// query not found msg
	protected String notFound(String searchType, String query){
		return "<font color=\"red\"><p><b>No similar songs to " + searchType + " \"" + query + "\" found in this music-library. Try search again." + "</b></p></font>";
	}
	
	// TODO: search suggestion button
	protected String goToSearchSuggestionButton(){


		return "<form action=\"searchsuggestion\" method=\"get\">"
				+ "<input type=\"submit\" style=\"font-weight: bold;  color: blue; \" value=\"Search Suggestion\">"
				+ "<div class=\"button_style\">"				
				+ "<select name=\"searchSuggestion\">"	// note searchSuggestion name here		
				+ "<option value=\"artist\">Artist</option>"
				+ "<option value=\"song_title\">Song Title</option>"				
				+ "</select>"
				
				+ "</div>" 				
				+ "</form>";
		
		
	}
	
	
	// option searchButton that let user navigate back to search page
	protected String goToSearchButton(){
		return "<form action=\"search\" method=\"get\">"
				+ "<div class=\"button_style\">"
				+ "<input type=\"submit\" value=\"Search Again\">"
				+ "</div>"
				+ "</form>";
				
	}	
	
	// option signUpButton that let user navigate back to signup page
	protected String goToSignUpButton(){
		return "<form action=\"signup\" method=\"get\">"
				+ "<div class='submit_blue'>"
				+ "<input type=\"submit\" value=\"Go to Sign Up Page\" id='button-blue'>"
				+ "</div>"
				+ "</form>";
	}
	
	// option loginButton that let user navigate back to login page
	protected String goToLoginButton(){
		return "<form action=\"login\" method=\"get\">"
				+ "<div class='submit_blue'>"
				+ "<input type=\"submit\" value=\"Go to Login Page\" id='button-blue'>"
				+ "</div>" 
				+ "</form>";
				
	}

	
	// option to go to change password button
	protected String goToChangePasswordButton(){
		
		return "<div class =\"form\">"
				+ "<form action=\"changepassword\" method=\"post\">"
				+ "<div class='submit_grey'>" 
				+ "<input type=\"submit\" style=\"width: 200px\" value=\"Change New Password?\" id='button-grey'>"
				+ "</div>"
				+ "</form></div>";
	}
	
	// TODO: go to searchHistory Button
	
	protected String goToViewSearchHistoryButton(){
		
		return "<form action='searchHistory' method='get'>"
				+ "<div class=\"button_style\">"
				+ "<input type=\"hidden\" name=\"clickedSearchHistory\" value=\"clickedSearchHistory\">"
				+ "<input type=\"submit\" value=\"View search history\">"				
				+ "</div>" 
				+ "</form>";
		
	}
	
	// TODO: clear history button
	// allow user to clear searched history
	protected String clearSearchHistoryButton(){
		
		
		return "<form action=\"searchHistory\" method=\"get\">"
				+ "<div class=\"clear_button_style\">"
				+ "<input type=\"hidden\" name=\"clear_search_history\" value=\"clearSearch\">"
				+ "<input type=\"submit\" value=\"Clear search history\">"
				+ "</div>"
				+ "</form>";
	}
	
	
	
	
	
	
	// TODO: search history format
	protected String setSearchHistoryTableFormat(String searchType, String query){
		
		return "<body><table border=\"2px\" width=\"100%\">"				
					+ "<tr>"
					+ "<th><strong><center>" + searchType + "</center></strong></th>"									
					+ "<th><strong><center>" + query + "</center></strong></th>"
					+ "</tr>";
								
	}
	
	
	// TODO: set search suggestion table format
	protected String setSearchSuggestionTableFormat(String searchType, String query, String searchCount){
		
		return "<body><table border=\"2px\" width=\"100%\">"				
					+ "<tr>"
					+ "<th><strong><center>" + searchType + "</center></strong></th>"									
					+ "<th><strong><center>" + query + "</center></strong></th>"
					+ "<th><strong><center>" + searchCount + "</center></strong></th>"
					+ "</tr>";
								
	}
	
	
	
	
	
	
	/**************************************************************************************************************************************
	 * 														ADVANCE FEATURES 															  *									
	 **************************************************************************************************************************************/
	
	// display all artist table format
	protected String allArtistByAlphabetTableFormat(String col1){
				
		return "<body><table border=\"2px\" width=\"100%\">"				
					+ "<tr>"
					+ "<th><strong><center>" + col1 + "</center></strong></th>"									
					+ "</tr>";
								
	} 
	
	// display all artist by play count table format
	protected String allArtistByPlayCountTableFormat(String artist, String playcount){
		
		return "<body><table border=\"2px\" width=\"100%\">"				
					+ "<tr>"
					+ "<th><strong><center>" + artist + "</center></strong></th>"									
					+ "<th><strong><center>" + playcount + "</center></strong></th>"
					+ "</tr>";
								
	}
		
	
	// display all artist button
	protected String showAllArtistsAlphabeticallyButton(){
		
			return " <form action=\"allartists\" method=\"get\">"
				+ "<div class=\"button_style\">"
				+ "<input type=\"submit\" value=\"View all artist alphabetically\">"
				+ "<input type=\"hidden\" name=\"showtype\" value=\"byAlphabet\">"
				+ "</div>"
				+ "</form>"; 
	}
	
	protected String showAllArtistByPlayCountButton(){
		
		return " <form action=\"allartists\" method=\"get\">"
				+ "<div class=\"button_style\">"
				+ "<input type=\"submit\" value=\"View all artist by playcount\">"
				+ "<input type=\"hidden\" name=\"showtype\" value=\"byPlayCount\">"
				+ "</div>"
				+ "</form>"; 
	}
	
	
	// display all artist name table content
	protected String  displayArtistNameEachRow(String artist){
		
	
		return "<tr><td><a href=\"/artistinfo?artistInfo=" + artist +  "\">" + artist  + "</td></tr>";
		
	
		
	}
	
	// display artist name + playcount table content
	protected static String  displayArtistNameAndPlayCountEachRow(String artist, String playcount){
		
		return "<tr>"
				+ "<td><a href=\"/artistinfo?artistInfo=" + artist +  "\">" + artist  + "</td>"
				+ "<td>" + playcount + "</td>"
				+ "</tr>";
		
	}
	
	// display artist info for each row [name, listeners, playcount, bio]
	protected static String  displayArtistInfoTable(String artist, String listeners, String playcount, String bio){
		
		return 
				"<tr><th><strong>Name</strong></th><td>" + artist + "</td></tr>"
				+ "<tr><th><strong>Listeners</strong></th><td>" + listeners + "</td></tr>"
				+ "<tr><th><strong>Playcount</strong></th><td>" + playcount + "</td></tr>"
				+ "<tr><th><strong>Bio</strong></th><td>" + bio + "</td></tr>";
				
		
	}
	
	// for displaying artist Image
	protected static String displayArtistImage(String artistImageURL, String artist, String imageStyle){
		
		
		
		return "<img class=\"" + imageStyle + "\"" + "src=\"" + artistImageURL + "\"" + "alt=\"" + artist + "\"" 
				+ "style=\"margin: 20px auto 80px auto;display:block; border-style: solid; border-width: 5px; border-color:" + BLUE_COLOR +  "\">";
		
		

		
//return "<body><div class=\"center\"><center><h1>" + boldTitle + "</h1></center><br/></div>";
//"<img src=\"https://maxcdn.icons8.com/Color/PNG/48/Data/list-48.png\" title=\"Favorite List\" width=\"48\">"
				
		
		
	}
	
	// display searched type, searched query and searchCount
	protected static String  displaySearchSuggestionEachRow(String searchType, String searchQuery, String searchCount){
		
		return "<tr>"
				+ "<td>" + searchType + "</td>"
				+ "<td>" + searchQuery + "</td>"
				+ "<td>" + searchCount + "</td>"
				+ "</tr>";
		
	}
	
	// display searched type and searched query
	protected static String  displaySearchedHistoryEachRow(String searchType, String searchQuery){
		
		return "<tr>"
				+ "<td>" + searchType + "</td>"
				+ "<td>" + searchQuery + "</td>"
				+ "</tr>";
		
	}
	
	
	protected static String tableHeadWithBody(){
		
		return "<body><table border=\"2px\" width=\"100%\">";
	}
	

	
/**************************************************************************************************************************************
 * 														CONDITION CHECK															  *									
 **************************************************************************************************************************************/

	/** General condition **/
	// check user login - for mostly servlet
	protected boolean checkUserLogin(HttpSession session, HttpServletResponse response) throws ServletException, IOException{
		
	
		String loggedIn =  (String) session.getAttribute(LOGGED_IN);
		
		// base case check
		// which mean haven't login yet
		if(loggedIn == null){
				
			return false;
		}
		
		return true;
		
	}
	
	
	/** SongServlet - check if user clicked search button & insert query properly **/
	protected boolean checkLoginUserClickedSearchButton(String search_type, String query) throws ServletException, IOException{
	
		// if either 1 of the option is not selected, redirect back to search page
		if( (search_type == null || query == null)){			
			return false;
		}	
		return true;
		
	}
	
	
	/** FavListServlet redirect from Song  condition - when user click on add fav song in SongServlet
	 * 	
	 * 	1. If the song that wanted to add does not appear in mySQL table, add it to mySQL table
	 *  2. else, skip it
	 * 
	 * **/
	protected boolean checkAddFavSongAction(HttpSession session, HttpServletRequest request, 
										HttpServletResponse response, DBConfig dbconfig, ReentrantLock favLock) throws ServletException, IOException {
		
		String favUsername = request.getParameter("favusername");
		String artist = request.getParameter("artist");
		String songTitle = request.getParameter("songtitle");		
		String songTrackID = request.getParameter("trackid");
	
		
		 // remove session after use
		session.removeAttribute(SEARCH_TYPE);
		session.removeAttribute(QUERY);
		 
		 
		// if user has clicked the link, add the favorite song and update to mySQL table
		if(favUsername != null && artist != null && songTitle != null && songTrackID != null){
			
			// acquire write lock to write to mySQL favTable 
			favLock.lockWrite();
	
			//update to mySQL favTable					
			try {
				
				// check if user already has a record in fav list, don't add this song because it's already in mySQL favlist table 				
				boolean userHasSameSongRecorded  = DBHelper.checkFavUsernameAndSongIDExist(dbconfig, favUsername, songTrackID);
				
				// need to set this to determine a user has a favorite song list 
				session.setAttribute(USERNAME, favUsername);
				
				if(!userHasSameSongRecorded) {
			
					// added to fav mySQL table
					DBHelper.addFavorite(dbconfig, favUsername, artist, songTitle, songTrackID);
				
				}			
				return true;
		
			} 
			catch (SQLException e) {
				
				e.printStackTrace();
			}
			finally {
				
				favLock.unlockWrite();
			}
			
		}		
		return false;
				
	}
	
	
	
	/** NEW UPDATE **/	
	protected String goToViewTop100ArtistChartButton(){

		return "<div><i class=\"icon\">"
				+"<a href=/chart>"				
				+ "<img src=\"https://maxcdn.icons8.com/Color/PNG/96/Data/combo_chart-96.png\"  width=\"48\">"
				+ "</a>"
				+ "</i><br>Top 100 Artist Chart</div>";
				
		
	}
	
	// return table content 
	protected String displaytop100ArtistChartContent(String rank, String artistImagePath, String artistName) {
		
		
		return "<tr>"
				+"<td>" + rank + "</td>"
				+"<td><center>"				 
				+ "<img src=\"" + artistImagePath + "\" alt=\"" + artistName + "\"" + ">"
				+ "</a>"
				+ "</center>"
				+"</td>"							
				+"<td><center><a href=\"/artistinfo?artistInfo=" + artistName +  "\">" + artistName  + "</td>"				
				+"</tr>";

				
		
	}
	
	
	
	
	
	


}
