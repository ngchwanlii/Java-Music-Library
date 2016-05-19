package database;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.Timestamp;


/*** ARTIST INFO GOT BUG - NO BEATLES **/


public class DBHelper {
		
	
	
	/** for partial search **/
	
	private static final String artistPartialSearchStmt = "SELECT name FROM artist WHERE name LIKE (LOWER(CONCAT('%', ?, '%')))"; 
	private static final String titlePartialSearchStmt = "SELECT songtitle FROM songTitle WHERE songtitle LIKE (LOWER(CONCAT('%', ?, '%')))";
	private static final String tagPartialSearchStmt = "SELECT tag FROM tag WHERE tag LIKE (LOWER(CONCAT('%', ?, '%')))";
	
	

	/** create admin table **/
	private static final String createAdminTable = "CREATE TABLE IF NOT EXISTS admin"
													+ "("
													+ "adminName VARCHAR(100) NOT NULL, "
													+ "password TEXT NOT NULL"
													+ ")";
	private static final String insertAdminInfo = "INSERT INTO admin (adminName, password) VALUES (?, ?)";
	
	public static final String adminTable = "admin";	
	
	private static final String adminAuthenticationStmt = "SELECT * FROM admin WHERE adminName=? AND password=?";
	
	
	
	/** userTable **/
	private static final String createUserTable = "CREATE TABLE IF NOT EXISTS user" +
													"(" + 
													"username VARCHAR(100) NOT NULL PRIMARY KEY, " + 
													"fullname TEXT NOT NULL, " + 
													"password TEXT NOT NULL" + 											
													")";
	private static final String insertUserInfoStatement = "INSERT INTO user (username, fullname, password) VALUES (?, ?, ?)";
	private static final String dropTableStmt = "DROP TABLE ";
	private static final String selectAllStmt = "SELECT * FROM ";	
	private static final String userTable = "user";
	
	// check if username already exists in mySQL table
	private static final String checkUserTableUsernameStmt = "SELECT * FROM user WHERE username=?";
	private static final String checkPasswordStmt = "SELECT * FROM user WHERE password=?";
	private static final String loginAuthentication = "SELECT * FROM user WHERE username=? AND password=?";
	
	// TODO: new added change password statement
	private static final String changeNewPasswordStmt = "UPDATE user SET password=? WHERE username=?";
	
	
	/** favTable **/
	public static final String createFavTable = "CREATE TABLE IF NOT EXISTS fav"
												+ "(" 
												+ "username VARCHAR(100) NOT NULL, " 
												+ "artist TEXT NOT NULL, "
												+ "songTitle TEXT NOT NULL, "
												+ "songTrackID VARCHAR(100) NULL" + 												 										
												")";
	
	
	private static final String insertFavSongListStatement = "INSERT INTO fav (username, artist, songTitle, songTrackID) VALUES (?, ?, ?, ?)";
	
	
	private static final String deleteFavSongStatement = "DELETE FROM fav WHERE username=? AND songTrackID=?";

	
	public static final String favTable = "fav";
	
	// check songID has been inserted into mySQL table (useful for switching status of favorite song (added or not added)
	private static final String checkFavTableUsernameStmt = "SELECT * FROM fav WHERE username=?";	
	private static final String showFavTableStmt = "SELECT artist, songTitle, songTrackID FROM fav WHERE username=?";
	private static final String checkFavUsernameAndSongTrackIDStmt = "SELECT * FROM fav WHERE username=? AND songTrackID=?";
	
	
	
	// Persistent MusicLibrary Storage
	/** ArtistInfoTable - from lastFMAPI - different than artist from persistent music library**/
	private static final String createLastFMArtistInfoTableStmt = "CREATE TABLE IF NOT EXISTS artistInfo" 
												   + "(" 
												   + "name LONGTEXT NOT NULL, " 
												   + "listeners INTEGER, " 
												   + "playcount INTEGER, "  
												   + "bio LONGTEXT, "
												   + "image LONGTEXT"
												   + ")";
			
	private static final String insertLastFMArtistInfoStatement = "INSERT INTO artistInfo (name, listeners, playcount, bio, image) VALUES (?, ?, ?, ?, ?)";	
	public static final String artistInfoTable = "artistInfo";
	private static final String showArtistInfoTableStmt = "SELECT * from artistInfo WHERE name=?";
	
	
	
	// Artist table - from artistMusicLibrary to persistent database
	private static final String createArtistTable = "CREATE TABLE IF NOT EXISTS artist" 
													   + "(" 
													   + "name LONGTEXT NOT NULL" 			  			     			   													 
													   + ")";
	private static final String insertArtistStatement = "INSERT INTO artist (name) VALUES (?)";
	public static final String artistTable = "artist";
	
	
	
	
	// Song Title table
	private static final String createSongTitleTable = "CREATE TABLE IF NOT EXISTS songTitle" 
													   + "(" 
													   + "name LONGTEXT NOT NULL," 			  			     			   
													   + "songtitle LONGTEXT NOT NULL,"
													   + "trackID LONGTEXT NOT NULL"
													   + ")";
	private static final String insertSongTitleStatement = "INSERT INTO songTitle (name, songtitle, trackID) VALUES (?, ?, ?)";
	public static final String songTitleInfoTable = "songTitle";
	
	
	
	// Tag table
	private static final String createTagTable = "CREATE TABLE IF NOT EXISTS tag" 
												   + "(" 
												   + "tag LONGTEXT NOT NULL, " 			  			     			   			   
												   + "trackID LONGTEXT NOT NULL"
												   + ")";
	private static final String insertTagStatement = "INSERT INTO tag (tag, trackID) VALUES (?, ?)";
	public static final String tagInfoTable = "tag";
	
	
	// TrackID table
	private static final String createTrackIDTable = "CREATE TABLE IF NOT EXISTS trackID" 
												   + "("
												   + "songTrackID LONGTEXT NOT NULL"
												   + "songtitle LONGTEXT NOT NULL" 			  			     			   			   			   
												   + ")";
	private static final String insertTrackIDStatement = "INSERT INTO trackID (songTrackID, tag) VALUES (?, ?)";
	public static final String trackIDInfoTable = "trackID";
		
	
	/** ArtistPlayCount table **/
	private static final String createArtistPlayCountTable = "CREATE TABLE IF NOT EXISTS artistPlayCount"
															+ "("
															+ "name LONGTEXT NOT NULL, "
															+ "playcount INTEGER"
															+ ")";
	
	private static final String insertLastFMArtistPlayCount = "INSERT INTO artistPlayCount (name, playcount) VALUES (?, ?)";
	public static final String artistPlayCountTable = "artistPlayCount";
	
	private static final String checkArtistInfoOrderByPlayCount = "SELECT name, playcount FROM artistInfo ORDER BY playcount DESC";
	
	
	
	
	private static final String showArtistNameByPlayCount = "SELECT * FROM artistPlayCount ORDER BY playcount DESC";
	
	
	/*** Search History Table ***/
	
	/** DELETE ID IF GOT BUG **/
	private static final String createSearchHistoryTable = 	"CREATE TABLE IF NOT EXISTS searchHistory"
															+ "("
															+ "ID int NOT NULL AUTO_INCREMENT, "
															+ "username VARCHAR(100) NOT NULL, "
															+ "searchType TEXT NOT NULL, "
															+ "searchQuery TEXT NOT NULL, "
															+ "searchCount LONG NOT NULL, "
															+ "PRIMARY KEY (ID)"
															+ ")";
	private static final String searchHistoryTable = "searchHistory";
	private static final String insertSearchHistory = "INSERT INTO searchHistory (username, searchType, searchQuery, searchCount) VALUES (?, ?, ?, ?)";
	private static final String showSearchHistoryByUsername = "SELECT searchType, searchQuery FROM searchHistory WHERE username=? ORDER BY ID DESC";
	private static final String clearSearchHistoryStmt = "DELETE FROM searchHistory WHERE username=?";
	
	private static final String checkSearchCounter = "SELECT searchCount FROM searchHistory WHERE username=? AND searchQuery=?";
	
	private static final String incrementSearchCount = "UPDATE searchHistory SET searchCount=searchCount+1 WHERE searchQuery=?";
	
	
	/** Search Suggestion Table **/	

	private static final String showSearchSuggestion = "SELECT  MAX(searchCount) AS searchCount, searchType, searchQuery FROM searchHistory WHERE searchType=? GROUP BY searchQuery ORDER BY searchCount DESC";
	
	/** Last login Time **/
	private static final String createLoginTimeTable = 	"CREATE TABLE IF NOT EXISTS time "
															+ "("
															+ "username VARCHAR(100) NOT NULL, "															
															+ "lastLoginTime TIMESTAMP"
															+ ")";
	
	private static final String insertLoginUserTimeStmt = "INSERT INTO time (username, lastLoginTime) VALUES (?, ?)";
	
	private static final String showUserLastLoginTimeStmt = "SELECT lastLoginTime FROM time WHERE username=? ORDER BY lastLoginTime DESC";
	
	private static final String checkUserTimeStampExist = "SELECT lastLoginTime FROM time WHERE username=?";
	
	
	
	
	/** Add TopArtistChart **/
	private static final String createTop100ArtistChart = 	"CREATE TABLE IF NOT EXISTS top100ArtistChart "
															+ "("
															+ "rank int NOT NULL PRIMARY KEY AUTO_INCREMENT, "
															+ "image LONGTEXT,"
															+ "artist LONGTEXT NOT NULL"																
															+ ")";
	public static final String top100ArtistChartTable = "top100ArtistChart";
	
	
	private static final String insertTop100Artist = "INSERT INTO top100ArtistChart (image, artist) VALUES (?, ?)";
	
	
	
	private static final String getTop100ArtistChart = "SELECT * FROM top100ArtistChart";
	
	
	
	
	
	/**** partial search ***/
	public static String artistPartialSearchResult(DBConfig dbconfig, String artist) throws SQLException {
	
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		PreparedStatement retrieveStmt = con.prepareStatement(artistPartialSearchStmt); 
	
	
		retrieveStmt.setString(1, artist);				
		
		
		ResultSet result = retrieveStmt.executeQuery();
		String completeName = null;
		try {
			if(result.next()){
				
				
				completeName = result.getString("name");
		
			}
			
		}
		finally {
			con.close();
		}
		
		return completeName; 
		
	}
	
	
	public static String titlePartialSearchResult(DBConfig dbconfig, String title) throws SQLException{
		

		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		PreparedStatement retrieveStmt = con.prepareStatement(titlePartialSearchStmt); 
	
		retrieveStmt.setString(1, title);				
		
		
		ResultSet result = retrieveStmt.executeQuery();
		String completeTitle = null;
		try {
			if(result.next()){
				
				completeTitle = result.getString("songtitle");
				
				
			}
			
		}
		finally {
			con.close();
		} 
		
		return completeTitle;
		
	}
	
	
	public static String tagPartialSearchResult(DBConfig dbconfig, String tag) throws SQLException{
		
		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		PreparedStatement retrieveStmt = con.prepareStatement(tagPartialSearchStmt); 
	
		retrieveStmt.setString(1, tag);				
		
		
		ResultSet result = retrieveStmt.executeQuery();
		String completeTag = null;
		
		try {
			if(result.next()){
				
				completeTag = result.getString("tag");
			
			}
			
		}
		finally {
			con.close();
		} 
		
		return completeTag;
		
	}
	
	
	
	
	/************************************************
	 *			Admin Table 						* 
	 ************************************************/
	
	public static void createAdminTable(DBConfig dbconfig) throws SQLException { 
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, adminTable)){
			PreparedStatement adminTableStmt = con.prepareStatement(createAdminTable);		
			adminTableStmt.executeUpdate();
		}
		
	
		// close connection after each request 
		con.close();
	
	}
	
	// set adminName and password
	public static void setAdmin(DBConfig dbconfig, String adminName, String password) throws SQLException{
		
	
		Connection con = getConnection(dbconfig);
		
		PreparedStatement updateAdminStmt = con.prepareStatement(insertAdminInfo);
		
		adminName = adminName.trim().toLowerCase();
		password = password.trim().toLowerCase();
	
		updateAdminStmt.setString(1, adminName);		
		updateAdminStmt.setString(2, password);
				
						
		updateAdminStmt.execute();	
	
		con.close();
	}
	
	
	
	// admin authentication
	public static boolean adminLoginAuthentication(DBConfig dbconfig, String adminName, String password) throws SQLException{
	
		Connection con = getConnection(dbconfig);
		
		PreparedStatement retrieveStmt = con.prepareStatement(adminAuthenticationStmt);
		
		// the way to verified, trim username and password and convert to lower-case
		adminName = adminName.trim().toLowerCase();
		password = password.trim().toLowerCase();
		
		retrieveStmt.setString(1, adminName);
		retrieveStmt.setString(2, password);
		
		// execute
		ResultSet result = retrieveStmt.executeQuery();
		
		try {
			if(result.next()){
				return true;
			}
			else{
				return false;
			}
		}
		finally {
			con.close();
		}
	
		
	}
	
	
	
	
	
	
	
	/************************************************
	 *			Login User Time Table 				* 
	 ************************************************/
	
	public static void createUserLoginTimeTable(DBConfig dbconfig) throws SQLException { 
	
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, "time")){
			PreparedStatement tableStmt = con.prepareStatement(createLoginTimeTable);		
			tableStmt.executeUpdate();
		}
		
	
		// close connection after each request 
		con.close();
	
	}
	
	
	// keep track user latest login time
	public static void insertLoginUserTimeStamp(DBConfig dbconfig, String username) throws SQLException {
		
		Connection con = getConnection(dbconfig);
		// 3. creates a PreparedStatement object for sending parameterized SQL statements to the database
		// insert artist info to table
		PreparedStatement updateStmt = con.prepareStatement(insertLoginUserTimeStmt);
		
		
		// get current time stamp from java
		java.util.Date currentDate = new java.util.Date();
		
		Timestamp currentTimeStamp = new java.sql.Timestamp(currentDate.getTime());
		

		// trim all white space and make it to lower case (because mySQL SELECT is case and space sensitive)
		// easier for retrieving data when user at login page
		username = username.trim().toLowerCase();
		
		updateStmt.setString(1, username);
		updateStmt.setTimestamp(2, currentTimeStamp);
	
		updateStmt.execute();	
	
		con.close();
		
		
	}
	
	// show user last login time - retrieve from mySQL database
	public static String retrieveUserLastLoginTime(DBConfig dbconfig, String username) throws SQLException {
				
		Connection con = getConnection(dbconfig);
		
		PreparedStatement retrieveStmt = con.prepareStatement(showUserLastLoginTimeStmt);
		
		
		retrieveStmt.setString(1, username);
		
		ResultSet result = retrieveStmt.executeQuery();
		
		String timeStamp = null;
		
		try {
			// get last login time stamp of this user
			if(result.next()){
				
				timeStamp = result.getTimestamp("lastLoginTime").toString();
			}
			return timeStamp; 
		
		}
		finally {
			con.close();
		}
		
	}
	
	
	public static boolean checkUserTimeStampExists(DBConfig dbconfig, String username) throws SQLException {
		
		Connection con = getConnection(dbconfig);
		
		PreparedStatement checkStmt = con.prepareStatement(checkUserTimeStampExist);
		
		
		username = username.trim().toLowerCase();
		
		checkStmt.setString(1, username);
		
		
		
		ResultSet result = checkStmt.executeQuery();
		
		
		
		try {
			// means user has recorded a time stamp
			if(result.next()){
				
				return true;
			}
			else {
				// else user has no record on time stamp
				return false;
			}
			
		}
		finally {
			con.close();
		}
		
			
		
	}
	
	
	
	
	
	/************************************************
	 *			User Table 							* 
	 ************************************************/
	
	// TODO: create searchHistory mySQL database
	public static void createSearchHistoryTable(DBConfig dbconfig) throws SQLException {
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, searchHistoryTable)){
			PreparedStatement tableStmt = con.prepareStatement(createSearchHistoryTable);		
			tableStmt.executeUpdate();
		}
		
	
		// close connection after each request 
		con.close();
		
	}
	
	
	
	// TODO: added saveSearchHistory to database
	public static void saveSearchHistory(DBConfig dbconfig, String username, String search_type, String  query) throws SQLException {
		
		Connection con = getConnection(dbconfig);
		
		// prepare statment for check search counter
		PreparedStatement retrieveStmt = con.prepareStatement(checkSearchCounter);

//		// set retrieveStmt for checking
		retrieveStmt.setString(1, username);
//		
		retrieveStmt.setString(2, query);
		
		// insert search History stmt
		PreparedStatement updateStmt = con.prepareStatement(insertSearchHistory);
		
		ResultSet result = retrieveStmt.executeQuery();
		
		// means there is a record on this search
		if(result.next()){
			updateStmt.setLong(4, result.getLong("searchCount") + 1);
		}
		else {
			updateStmt.setLong(4, 1);
		}
		
		result.close();
		
		
		username = username.trim().toLowerCase();
		
		updateStmt.setString(1, username);		
		updateStmt.setString(2, search_type);
		updateStmt.setString(3, query);	
		
		updateStmt.execute();	
	
		con.close();
		
		
	}
	
	// update search count
//	public static void updateSearchCount (DBConfig dbconfig, String query) throws SQLException {
//	
//		Connection con = getConnection(dbconfig);
//		
//		PreparedStatement updateStmt = con.prepareStatement(incrementSearchCount);
//		
//		updateStmt.setString(1, query);
//		
//		updateStmt.execute();	
//		
//		con.close();
//		
//		
//	}
	
	
	
	// TODO: retrieve searchedHistory table content 
	// retrieve searchedHistory table content
	public static JSONArray retrieveSearchHistoryTableContent(DBConfig dbconfig, String username) throws SQLException{
	
		Connection con = getConnection(dbconfig);
		
		PreparedStatement retrieveStmt = con.prepareStatement(showSearchHistoryByUsername);
		
		retrieveStmt.setString(1, username);
		
		ResultSet result = retrieveStmt.executeQuery();
			
		JSONArray jsonArray = new JSONArray();
		
		while(result.next()){
			
		
			String searchType = result.getString("searchType");
			String searchQuery = result.getString("searchQuery");
			
			JSONObject jsonObj = new JSONObject();
		
			jsonObj.put("searchType", searchType);
			jsonObj.put("searchQuery", searchQuery);
		
			jsonArray.add(jsonObj);
			
		}
		
		// close connection after each request 
		con.close();
		
		return jsonArray;
		
	}
	
	
	public static JSONArray retrieveSearchSuggestionTableContent(DBConfig dbconfig, String clickedSearchType) throws SQLException{
		
		Connection con = getConnection(dbconfig);
		
		// retrieve in descending order
		PreparedStatement retrieveStmt = con.prepareStatement(showSearchSuggestion);
	
		// set retrieveStmt !!!!!
		retrieveStmt.setString(1, clickedSearchType);
		
		ResultSet result = retrieveStmt.executeQuery();
			
		JSONArray jsonArray = new JSONArray();
		
		while(result.next()){
			
		
			String searchType = result.getString("searchType");
			String searchQuery = result.getString("searchQuery");
			Long searchCounter = result.getLong("searchCount");
			
			// convert search counter to string - for print out
			String strSearchCounter = searchCounter.toString();
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("searchType", searchType);
			jsonObj.put("searchQuery", searchQuery);
			jsonObj.put("searchCount", strSearchCounter);
			
			jsonArray.add(jsonObj);
			
		}
		
		// close connection after each request 
		con.close();
		
		return jsonArray;
		
	}
	

	
	// TODO: clear search history button
	public static void clearSearchedHistory(DBConfig dbconfig, String username) throws SQLException{
		
	
		
		Connection con = getConnection(dbconfig);
		
		//create a statement object
		PreparedStatement clearStmt = con.prepareStatement(clearSearchHistoryStmt);
		
		clearStmt.setString(1, username);
		
		if(tableExists(con, searchHistoryTable)) {				
			clearStmt.executeUpdate();
		}
		
		con.close();
		
	}
	

		
	// TODO: new added changeNewPassword method - allow user to change new password 
	// OK SUCCESS
	public static void changeNewPassword(DBConfig dbconfig, String newPassword, String username ) throws SQLException {
		
		Connection con = getConnection(dbconfig);
		// 3. creates a PreparedStatement object for sending parameterized SQL statements to the database
		// insert artist info to table
		PreparedStatement updateStmt = con.prepareStatement(changeNewPasswordStmt);
		
	
		// trim all white space and make it to lower case (because mySQL SELECT is case and space sensitive)
		// easier for retrieving data when user at login page
		username = username.trim().toLowerCase();		
		newPassword = newPassword.trim().toLowerCase();
		
		updateStmt.setString(1, newPassword);		
		updateStmt.setString(2, username);
		
		updateStmt.execute();	
	
		con.close();
		
	}
	
	
	
	
	
	// create a user table - save to mySQL database
	// @param dbconfig
	// @throws SQLException
	public static void createUserTable(DBConfig dbconfig) throws SQLException {		
		
		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, userTable)){
			PreparedStatement tableStmt = con.prepareStatement(createUserTable);		
			tableStmt.executeUpdate();
		}
	
		
		// close connection after each request 
		con.close();
		
		
		
						
	}
	
	// check if user inside mySQL [user] database
	public static boolean userAuthentication(DBConfig dbconfig, String username, String password) throws SQLException{
			
		Connection con = getConnection(dbconfig);
		PreparedStatement retrieveStmt = con.prepareStatement(loginAuthentication);
	
		// the way to verified, trim username and password and convert to lower-case
		username = username.trim().toLowerCase();
		password = password.trim().toLowerCase();
		
		
		// set the value 
		retrieveStmt.setString(1, username);
		retrieveStmt.setString(2, password);
		
		// execute
		ResultSet result = retrieveStmt.executeQuery();
		
		try {
			if(result.next()){
				return true;
			}
			else{
				return false;
			}
		}
		finally {
			con.close();
		}
		
		
	}
	
	
	
	/** userTable **/
	// check if user exists in mySQL userTable database 
	public static boolean userExist(DBConfig dbconfig, String username) throws SQLException {
		
		Connection con = getConnection(dbconfig);
				
		PreparedStatement retrieveStmt = con.prepareStatement(checkUserTableUsernameStmt);
		
		
		username = username.trim().toLowerCase();
		
		retrieveStmt.setString(1, username);
		
		ResultSet result = retrieveStmt.executeQuery();
		
		// we don't need a while loop here, username is PRIMARY key, and no duplicate is allowed + the mySQL syntax we use 
		// can find particular row record with the username provided
		
		// if found username exists - return true
		try {
			if(result.next()){
				return true;
			}
			else{
				// otherwise - false
				return false;
			}
		}
		finally {
			// need to close connection after return value (close connection every time a retrieve data execution from mySQL
			con.close();
		}	
	}
	
	
	
	/** userTable **/
	// already implement lock mechanism in verifyuser servlet 
	// addUser to mySQL data base - this have to be thread-safe (write operation)
	public static void addUser(DBConfig dbconfig, String username, String fullname, String password) throws SQLException{
		
		Connection con = getConnection(dbconfig);
		// 3. creates a PreparedStatement object for sending parameterized SQL statements to the database
		// insert artist info to table
		PreparedStatement updateStmt = con.prepareStatement(insertUserInfoStatement);
		
		// trim all white space and make it to lower case (because mySQL SELECT is case and space sensitive)
		// easier for retrieving data when user at login page
		username = username.trim().toLowerCase();
		fullname = fullname.trim().toLowerCase();
		password = password.trim().toLowerCase();
		
		
		updateStmt.setString(1, username);		
		updateStmt.setString(2, fullname);
		updateStmt.setString(3, password);		
						
		updateStmt.execute();	
	
		con.close();
	}
	
	
	
	/************************************************
	 *			Favorite List Table					* 
	 ************************************************/
	
	
	/** favTable **/
	// createFavTable method
	public static void createFavTable(DBConfig dbconfig) throws SQLException {		
		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, favTable)){
			PreparedStatement tableStmt = con.prepareStatement(createFavTable);		
			tableStmt.executeUpdate();
		}
		
		
		// close connection after each request 
		con.close();
			
		
						
	}
	
	/** favTable **/
	public static void addFavorite(DBConfig dbconfig, String username, String artist, String songTitle, String songTrackID) throws SQLException{
	
		
		Connection con = getConnection(dbconfig);
		// 3. creates a PreparedStatement object for sending parameterized SQL statements to the database
		// insert artist info to table
		
		// trim all white space and make it to lower case (because mySQL SELECT is case and space sensitive)
		// easier for retrieving data when user at login page
		username = username.trim().toLowerCase();
	
		PreparedStatement updateStmt = con.prepareStatement(insertFavSongListStatement);
		
		updateStmt.setString(1, username);
		updateStmt.setString(2, artist);		
		updateStmt.setString(3, songTitle);
		updateStmt.setString(4, songTrackID);					
		
		updateStmt.execute();	
						
		con.close();
		
		
	}
	
	// delete favorite song list
	public static void deleteFavorite(DBConfig dbconfig, String favusername, String songTrackID) throws SQLException {
		
		Connection con = getConnection(dbconfig);
		
		favusername = favusername.trim().toLowerCase();
		
	
		PreparedStatement deleteStmt = con.prepareStatement(deleteFavSongStatement);
		
		deleteStmt.setString(1, favusername);
		deleteStmt.setString(2, songTrackID);
		
		deleteStmt.execute();
		
		con.close();
		
	}
	
	
	/** favTable **/
	// check if username exists in mySQL favTable database 
	public static boolean favTableUsernameExist(DBConfig dbconfig, String username) throws SQLException {
		
		Connection con = getConnection(dbconfig);
				
		PreparedStatement retrieveStmt = con.prepareStatement(checkFavTableUsernameStmt);
		
		// trim and convert to lower case
		username = username.trim().toLowerCase();
	
		retrieveStmt.setString(1, username);
		
		ResultSet result = retrieveStmt.executeQuery();
		
		// if found username exists - return true
		try {
			if(result.next()){
				return true;
			}
			else{
				// otherwise - false
				return false;
			}
		}
		finally {
			// need to close connection after return value (close connection every time a retrieve data execution from mySQL
			con.close();
		}	
	}
	
	/** favTable **/
	// check if trackID exists in mySQL favTable database 
	public static boolean checkFavUsernameAndSongIDExist(DBConfig dbconfig, String username, String trackID) throws SQLException {
		
		Connection con = getConnection(dbconfig);
				
		PreparedStatement retrieveStmt = con.prepareStatement(checkFavUsernameAndSongTrackIDStmt);	
		
		username.trim().toLowerCase();
	
		retrieveStmt.setString(1, username);
		retrieveStmt.setString(2, trackID);
		
		ResultSet result = retrieveStmt.executeQuery();
		
		
		// if found trackID exists - return true
		try {
			if(result.next()){
				return true;
			}
			else{
				// otherwise - false
				return false;
			}
		}
		finally {
			// need to close connection after return value (close connection every time a retrieve data execution from mySQL
			con.close();
		}	
	}
	
	
	
	/************************************************
	 *			LastFM ArtistInfo Table				* 
	 ************************************************/
	public static void createArtistInfoDetailsTable(DBConfig dbconfig) throws SQLException {		
		
	
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, artistInfoTable)){
			PreparedStatement tableStmt = con.prepareStatement(createLastFMArtistInfoTableStmt);		
			tableStmt.executeUpdate();
		}
		
		
		// close connection after each request 
		con.close();
		
	}
	
	
	/************************************************
	 *			Persistent MusicLibrary storage		* 
	 ************************************************/
	
	
	// create Artist Table
	
	public static void createArtistTable(DBConfig dbconfig) throws SQLException {		
		
		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, artistTable)){
			PreparedStatement tableStmt = con.prepareStatement(createArtistTable);		
			tableStmt.executeUpdate();
		}
		
		
		// close connection after each request 
		con.close();
		
	}
	
	
	
	// Create Song Title Table
	public static void createSongTitleTable(DBConfig dbconfig) throws SQLException {		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, songTitleInfoTable)){
			PreparedStatement tableStmt = con.prepareStatement(createSongTitleTable);		
			tableStmt.executeUpdate();
		}
		
		
		// close connection after each request 
		con.close();
		
	}
	
	// Create Tag Table
	public static void createTagTable(DBConfig dbconfig) throws SQLException {		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, tagInfoTable)){
			PreparedStatement tableStmt = con.prepareStatement(createTagTable);		
			tableStmt.executeUpdate();
		}
		
		
		// close connection after each request 
		con.close();
		
	}
	
	// Create TrackID Table
	public static void createTrackIDTable(DBConfig dbconfig) throws SQLException {		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 	
	
		if(!tableExists(con, trackIDInfoTable)){
		
			PreparedStatement tableStmt = con.prepareStatement(createTrackIDTable);		
			tableStmt.executeUpdate();
		}
		
		
		// close connection after each request 
		con.close();
		
	}
	
	// create top 100 Artist Chart
	public static void createTop100ArtistChart(DBConfig dbconfig) throws SQLException {		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, top100ArtistChartTable)){
			PreparedStatement tableStmt = con.prepareStatement(createTop100ArtistChart);		
			tableStmt.executeUpdate();
		}
	
		// close connection after each request 
		con.close();
		
		
	}
	
	
	/************************************************
	 *			Add to mySQL database				* 
	 ************************************************/
	
	// addArtist - from artistMusicLibrary to database
	public static void addArtistTable(DBConfig dbconfig, String artist) throws SQLException{
		
		Connection con = getConnection(dbconfig);
		
		PreparedStatement updateArtistInfoStmt = con.prepareStatement(insertArtistStatement);

		updateArtistInfoStmt.setString(1, artist);
		
		// remember execute!
		updateArtistInfoStmt.execute();
		
		con.close();

	}
	
	
	// 2. addSongTitle - from titleMusicLibrary to database
	public static void addSongTitleTable(DBConfig dbconfig, String artistName, String songTitle, String trackID) throws SQLException{
		
	
		
		Connection con = getConnection(dbconfig);
		
		PreparedStatement updateSongTitleStmt = con.prepareStatement(insertSongTitleStatement);

		updateSongTitleStmt.setString(1, artistName);
		updateSongTitleStmt.setString(2, songTitle);
		updateSongTitleStmt.setString(3, trackID);
		
		// remember execute!
		updateSongTitleStmt.execute();
		
		con.close();

	}
	
	
	
	// 3. addTag - from tagMusicLibrary to database	
	public static void addTagTable(DBConfig dbconfig, String tag, String trackID) throws SQLException{
		
		Connection con = getConnection(dbconfig);
		
		PreparedStatement updateTagStmt = con.prepareStatement(insertTagStatement);

		updateTagStmt.setString(1, tag);
		updateTagStmt.setString(2, trackID);
	
		// remember execute!
		updateTagStmt.execute();
		
		con.close();

	}

	// 4. addTrackID - from trackIDMusicLibrary to database
	public static void addTrackIDTable(DBConfig dbconfig, String trackID, String tag) throws SQLException{

		Connection con = getConnection(dbconfig);
		
		PreparedStatement updateTrackIDStmt = con.prepareStatement(insertTrackIDStatement);
		
		updateTrackIDStmt.setString(1, trackID);
		updateTrackIDStmt.setString(2, tag);
	
		updateTrackIDStmt.execute();
		
		con.close();

	}
	
	
	
	/** LASTFM TopArtistChart **/
	public static void addTopArtistChartInfoLastFM(DBConfig dbconfig, String image, String artist) throws SQLException{
	
		Connection con = getConnection(dbconfig);
		
		PreparedStatement updateArtistInfoStmt = con.prepareStatement(insertTop100Artist);
		
		updateArtistInfoStmt.setString(1, image);
		updateArtistInfoStmt.setString(2, artist);
		
		// remember execute!
		updateArtistInfoStmt.execute();
		
		con.close();
		
		
	}
	/** LASTFM artistsInfo table **/
	// updateTable method - update/insert artist info to database
	public static void addArtistInfoLastFM(DBConfig dbconfig, String name, Integer listeners, Integer playcount, String bio, String artistImage) throws SQLException{
		
	
		Connection con = getConnection(dbconfig);
		// 3. creates a PreparedStatement object for sending parameterized SQL statements to the database
		// insert artist info to table
		PreparedStatement updateArtistInfoStmt = con.prepareStatement(insertLastFMArtistInfoStatement);
		
		
		updateArtistInfoStmt.setString(1, name);		
		updateArtistInfoStmt.setInt(2, listeners);
		updateArtistInfoStmt.setInt(3, playcount);		
		updateArtistInfoStmt.setString(4, bio);				
		updateArtistInfoStmt.setString(5, artistImage);
		updateArtistInfoStmt.execute();	
		
		
		// close each connection after a request
		con.close();
		
	}
	
	
	
	/************************************************
	 *			Artist Play-count Table				* 
	 ************************************************/	
	public static void createArtistPlayCountTable(DBConfig dbconfig) throws SQLException {		
		
		
		// 1. get connection from database config		
		Connection con = getConnection(dbconfig);
		
		// 2. check if table exits or not
		// if table exits - we won't create this table
		// else - create artist table 		
		if(!tableExists(con, "artistPlayCount")){
			PreparedStatement tableStmt = con.prepareStatement(createArtistPlayCountTable);		
			tableStmt.executeUpdate();
		}
		
		con.close();
		
		// add the sorted artist play count based on pre-created ArtistInfoTable in mySQL
		// start new connection
		con = getConnection(dbconfig);
	
		PreparedStatement retrieveStmt = con.prepareStatement(checkArtistInfoOrderByPlayCount);
	
			
		ResultSet result = retrieveStmt.executeQuery();
			
		while(result.next()){
			
			String artist = result.getString("name");
			Integer playcount = result.getInt("playcount");
			
			addArtistPlayCount(dbconfig, artist, playcount);
			
		}
	
		
		// close connection after each request 
		con.close();
		
		
	}
	
	public static void addArtistPlayCount(DBConfig dbconfig, String artist, Integer playcount) throws SQLException{
		
		Connection con = getConnection(dbconfig);
		// 3. creates a PreparedStatement object for sending parameterized SQL statements to the database
		// insert artist info to table
		PreparedStatement updateArtistInfoStmt = con.prepareStatement(insertLastFMArtistPlayCount);
	
		updateArtistInfoStmt.setString(1, artist);				
		updateArtistInfoStmt.setInt(2, playcount);		
						
		updateArtistInfoStmt.execute();	
		
		
		// close each connection after a request
		con.close();
		
	}
	
	
	
	

	/** general use **/
	// check if tableExists (helper method for clearTables method)
	public static boolean tableExists(Connection con, String table) throws SQLException {

		
		
		DatabaseMetaData metadata = con.getMetaData();
		ResultSet resultSet;
		resultSet = metadata.getTables(null, null, table, null);

		if(resultSet.next()) {
			// Table exists
			return true;
		}		
		return false;
	}
	
	/** general use **/
	// clearTables method - clear specific table
	public static void clearTables(DBConfig dbconfig, String table) throws SQLException {

		Connection con = getConnection(dbconfig);
		
		//create a statement object
		PreparedStatement clearStmt = con.prepareStatement(dropTableStmt + table);
		
		if(tableExists(con, table)) {				
			clearStmt.executeUpdate();
		}
		
		con.close();
	}
	
	
	/** general use **/
	// getConnection method - get the connection from database
	public static Connection getConnection(DBConfig dbconfig) throws SQLException {

		String username  = dbconfig.getUsername();
		String password  = dbconfig.getPassword();
		String db  = dbconfig.getDb();

		try {
			// load driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception e) {
			System.err.println("Can't find driver");
			System.exit(1);
		}

		// format "jdbc:mysql://[hostname][:port]/[dbname]"
		//note: if connecting through an ssh tunnel make sure to use 127.0.0.1 and
		//also to that the ports are set up correctly
		String host = dbconfig.getHost();
		String port = dbconfig.getPort();
		String urlString = "jdbc:mysql://" + host + ":" + port + "/"+db;
		Connection con = DriverManager.getConnection(urlString,
				username,
				password);

		return con;
	}
		
	
	// show fav list table content
	public static JSONArray retrieveFavTableContent(DBConfig dbconfig, String loginUsername) throws SQLException{
			

			Connection con = getConnection(dbconfig);
		
			PreparedStatement retrieveStmt = con.prepareStatement(showFavTableStmt);
			
			loginUsername = loginUsername.trim().toLowerCase();
			retrieveStmt.setString(1, loginUsername);
					
			ResultSet result = retrieveStmt.executeQuery();
			
		
			/** FIXED - CODE REVIEW POINT **/
			// use a JSONArray to store data
			// 3 data structure info for returning 
			// JSONObject [artist, songTitle, songTrackID]
			JSONArray jsonArray = new JSONArray();
			while(result.next()){
				
				String artist = result.getString("artist");
				String songTitle = result.getString("songTitle");
				String songTrackID = result.getString("songTrackID");
				
				// create new jsonObject each time we have a new artist + songTitle + songTrackID info
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("artist", artist);
				jsonObj.put("songTitle", songTitle);
				jsonObj.put("trackID", songTrackID);
				
				jsonArray.add(jsonObj);
				
			}
			
			con.close();
			
			return jsonArray;
	
			
	}
	
	// generate playcount table content
	public static JSONArray retrieveArtistByPlayCountTableContent(DBConfig dbconfig) throws SQLException{
	
		Connection con = getConnection(dbconfig);
		
		PreparedStatement retrieveStmt = con.prepareStatement(showArtistNameByPlayCount);
		
		ResultSet result = retrieveStmt.executeQuery();
			
		JSONArray jsonArray = new JSONArray();
		
		while(result.next()){
			
			String artist = result.getString("name");
			Integer playcount = result.getInt("playcount");
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("artist", artist);
			jsonObj.put("playcount", playcount.toString());
			
			jsonArray.add(jsonObj);
			
			
		}
		
		// close connection after each request 
		con.close();
		
		return jsonArray;
		
	}
		
	
	// generate artist info table content
	public static JSONArray retrieveArtistInfoTableContent(DBConfig dbconfig, String artist) throws SQLException{
	
		Connection con = getConnection(dbconfig);
		
		PreparedStatement retrieveStmt = con.prepareStatement(showArtistInfoTableStmt);
		
		retrieveStmt.setString(1, artist);
		
		ResultSet result = retrieveStmt.executeQuery();
			
		JSONArray jsonArray = new JSONArray();
	
		while(result.next()){
			
			String name = result.getString("name");
			Integer listeners = result.getInt("listeners");
			Integer playcount = result.getInt("playcount");
			String bio = result.getString("bio");
			String artistImage = result.getString("image");
			
		
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("name", name);
			jsonObj.put("listeners", listeners.toString());
			jsonObj.put("playcount", playcount.toString());		
			jsonObj.put("bio", bio);
			jsonObj.put("image", artistImage);
			
			jsonArray.add(jsonObj);
			
		
			
		}
		
		// close connection after each request 
		con.close();
		return jsonArray;
		
	}
	
	
	// top 100 Artist Chart
	public static JSONArray retrieveTop100ArtistsChartContent(DBConfig dbconfig) throws SQLException{
		
		/** DEBUG LATER **/
		
		
		Connection con = getConnection(dbconfig);
		
		
		PreparedStatement retrieveStmt = con.prepareStatement(getTop100ArtistChart);
		
	
		ResultSet result = retrieveStmt.executeQuery();
			
		JSONArray jsonArray = new JSONArray();
	
		
		while(result.next()){
			
			
			int rank = result.getInt("rank");
			String strRank = Integer.valueOf(rank).toString();
			
			String artist = result.getString("artist");
			
			String artistImage = result.getString("image");
	
		
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("rank", strRank);
			
			jsonObj.put("artist", artist);
			
			jsonObj.put("image", artistImage);
			
			jsonArray.add(jsonObj);
			
			
		}		
		// close connection after each request 
		con.close();
		return jsonArray;
		
	}
	
	
	
	
	
}
