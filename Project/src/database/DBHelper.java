package database;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import cs212.server.MusicLibraryBaseServlet;


public class DBHelper {
		
	// class variable 	
	
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
	
	
	/** favTable **/
	public static final String createFavTable = "CREATE TABLE IF NOT EXISTS fav"
												+ "(" 
												+ "username VARCHAR(100) NOT NULL, " 
												+ "artist TEXT NOT NULL, "
												+ "songTitle TEXT NOT NULL, "
												+ "songTrackID VARCHAR(100) NULL" + 												 										
												")";
	
	
	private static final String insertFavSongListStatement = "INSERT INTO fav (username, artist, songTitle, songTrackID) VALUES (?, ?, ?, ?)";
	public static final String favTable = "fav";
	
	// check songID has been inserted into mySQL table (useful for switching status of favorite song (added or not added)
	private static final String checkFavTableUsernameStmt = "SELECT * FROM fav WHERE username=?";	
	private static final String showFavTableStmt = "SELECT artist, songTitle, songTrackID FROM fav WHERE username=?";
	private static final String checkFavUsernameAndSongTrackIDStmt = "SELECT * FROM fav WHERE username=? AND songTrackID=?";
	
	
	/** userTable **/
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
		
		/** DEBUG USE - DROP TABLE **/		
//			PreparedStatement dropStatement = con.prepareStatement(dropTableStmt + userTable);
//			dropStatement.executeUpdate();
		
						
	}
	
	public static boolean loginAuthentication(DBConfig dbconfig, String username, String password) throws SQLException{
			
		Connection con = getConnection(dbconfig);
		PreparedStatement retrieveStmt = con.prepareStatement(loginAuthentication);
	
		// the way to varified, trim username and password and conver to lower-case
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
	
	
	
//	/** userTable **/
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
	
	
//	
//	/** userTable **/
//	// check if user password exists in mySQL database
//	public static boolean passwordExist(DBConfig dbconfig, String password) throws SQLException {
//		
//		Connection con = getConnection(dbconfig);
//				
//		PreparedStatement retrieveStmt = con.prepareStatement(checkPasswordStmt);
//		// set the ? in (SELECT * FROM user WHERE username=?) to the username that we want to check
//		password = password.trim().toLowerCase();
//		
//		retrieveStmt.setString(1, password);
//		
//		ResultSet result = retrieveStmt.executeQuery();
//		
//		// we don't need a while loop here, username is PRIMARY key, and no duplicate is allowed + the mySQL syntax we use 
//		// can find particular row record with the username provided
//		
//		// if found username exists - return true
//		try {
//			if(result.next()){
//				return true;
//			}
//			else{
//				// otherwise - false
//				return false;
//			}
//		}
//		finally {
//			// need to close connection after return value (close connection every time a retrieve data execution from mySQL
//			con.close();
//		}
//		
//		
//	}
	
	
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
	
//	/** favTable **/
//	// check if trackID exists in mySQL favTable database 
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
	
	
	
	/** DEBUG TABLE **/
	// show table method - this is just extra method that use for debugging
	// copy paste this code -> [DBHelper.showTableContent(dbconfig)] into TestlastFMClient 
	// after invoking [LastFMClient.fetchAndStoreArtists(artists, dbconfig)] to DISPLAY TABLE content	
//	public static void showTableContent(DBConfig dbconfig, String tableType) throws SQLException {
//		
//		/** DEBUG USE - display the table content - SUCCESS OKAY!!!! **/
//		//reuse the statement to insert a new value into the table
//		Connection con = getConnection(dbconfig);
//		
//		PreparedStatement retrieveStmt = con.prepareStatement(selectAllStmt + tableType);
//		ResultSet result = retrieveStmt.executeQuery();
//		
//		int rowCounter = 0;
//		//iterate over the ResultSet
//		while (result.next()) {
//			//for each result, get the value of the columns name and id
//			String username = result.getString("username");
//			String fullname = result.getString("fullname");
//			String password = result.getString("password");
//			
//			System.out.printf("\n*** ROW %d ***\n", rowCounter++);
//			System.out.printf("username: %s\n"
//							+ "fullname: %s\n"
//							+ "password: %s\n", username, fullname, password);
//		}
//		
//		
//		// close each connection after a request
//		con.close();
//		
//	}
	
	/** SHOW FAV TABLE **/
	// show fav list table content
	public static void generateFavTableContent(DBConfig dbconfig, StringBuffer buffer, String loginUsername) throws SQLException{
			
//TODO: consider having this method return a data structure containing the relevant information rather than appending to a buffer.		
			Connection con = getConnection(dbconfig);
		
			
			PreparedStatement retrieveStmt = con.prepareStatement(showFavTableStmt);
		
			/** IMPORTANT ! This tiny bug!! **/			
			//TODO: here
			loginUsername = loginUsername.trim().toLowerCase();
			retrieveStmt.setString(1, loginUsername);
					
			ResultSet result = retrieveStmt.executeQuery();
				
			while(result.next()){
				
				String artist = result.getString("artist");
				String songTitle = result.getString("songTitle");
				String songTrackID = result.getString("songTrackID");
			
				buffer.append(MusicLibraryBaseServlet.favListTableContent(artist, songTitle, songTrackID));
				
			}
		
			con.close();
	
			
	}
	
	
	
	
}
