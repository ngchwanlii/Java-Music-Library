package cs212.server;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cs212.data.SongDataProcessor;
import cs212.data.ThreadSafeMusicLibrary;
import cs212.util.concurrent.ReentrantLock;
import database.DBConfig;
import database.DBHelper;

public class MusicLibraryServer {
	
	
	// DEFAULT_PORT assigned
	public static final int DEFAULT_PORT = 9051;
	public static final int MAX_THREADS = 10;
	public static final String MUSIC_DATAPATH = "input/lastfm_subset";
	public static DBConfig dbconfig;
	
	public static void main(String[] args) throws Exception {
		
		// set new server by default_port
		Server server = new Server(DEFAULT_PORT);
		
		// web-container 
		// create ServletHander for attaching servlets
		ServletContextHandler servhandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		server.setHandler(servhandler);
		
		// add Event Listener
		servhandler.addEventListener(new ServletContextListener() {

			@Override
			public void contextDestroyed(ServletContextEvent arg0) {
				// when server shut down - do nothing
				// option - put some task here if you want server do something after shutdown
			}

			
			@Override
			// set the intialized context  
			public void contextInitialized(ServletContextEvent sce) {
				
				
				// number of threads for the thread pool
				int nThreads = MAX_THREADS;
				
										
				// create musiclibrary (thread version)		  - [task: set content of musiclibrary and ready for build up]
				ThreadSafeMusicLibrary threadSafe_musicLibrary = new ThreadSafeMusicLibrary(MUSIC_DATAPATH);
				
				// create song_data_processor (thread version) - [task: addSong and build up musiclibrary]
				SongDataProcessor processSongData = new SongDataProcessor(threadSafe_musicLibrary, MUSIC_DATAPATH, nThreads);
				
				// create new dbconfig
				DBConfig dbconfig = setupDBConfig();
				
				LastFMClient lastFMClient = new LastFMClient();
				
		
				
				// create 2 types of reentrant lock (lock logic for addUser to mySQL + retrieve data from mySQL table (for login authentication)
				
				// why 2 types? both userLock and favLock has similar features -  (retrieve / update userTable)
				// but they update to their own table, so we use 2 reentrant lock, where other thread can simultaneously 
				// update to 2 different table 
				
				// this lock is for userLock (retrieve / update userTable) 
				ReentrantLock userLock = new ReentrantLock();
				
				// this lock is for favLock (retrieve / update favListTable);
				ReentrantLock favLock = new ReentrantLock();
		
				
				// must create user table 1st
				try {
					
				
					// create userTable					
					DBHelper.createUserTable(dbconfig);
//					
//					// create favTable
					DBHelper.createFavTable(dbconfig);

//					//  create ArtistTable					
					DBHelper.createArtistTable(dbconfig);
//			
					
					// create SearchHistoryTable
					DBHelper.createSearchHistoryTable(dbconfig);
					
					
					// create loginUserTimeTable
					DBHelper.createUserLoginTimeTable(dbconfig);
					
					
					
//					/** DEBUG MSG **/
////					System.out.println("created Usertable and Favtable and Artist");
//										
////					// fetch and store artist information using lastFM API					
					
					/** DEBUG USE FOR quickly setup server **/
//					LastFMClient.fetchSingleArtist("50 Cent", dbconfig);
					
					
					/** MAIN USE **/
//					LastFMClient.fetchAndStoreArtists(threadSafe_musicLibrary.getSortedArtistName(), dbconfig);
//
////					/** DEBUG MSG **/
////					System.out.println("finish fetch and store artist in MusicLibraryServer");
//
//				
//					// create ArtistPlayCountTable
//					DBHelper.createArtistPlayCountTable(dbconfig);					
				
					/** DEBUG MSG **/
//					System.out.println("finish createArtistPlayCountTable in MusicLibraryServer");
					
					
					
					
					/** DEBUG dropUserTable **/
					
					// TODO: TESTING ARTIST IMAGE NOW!!
//					DBHelper.clearTables(dbconfig, DBHelper.artistInfoTable);
//					
//					DBHelper.clearTables(dbconfig, "user");					
//					DBHelper.clearTables(dbconfig, "fav");				
//					DBHelper.clearTables(dbconfig, DBHelper.artistPlayCountTable);
//					DBHelper.clearTables(dbconfig, "artist");
//					DBHelper.clearTables(dbconfig, "time");
					/** DEBUG dropFavTable **/
				} 
				catch (SQLException e) {				
					e.printStackTrace();
				}
				
				
			
				// set attribute music_lib
				sce.getServletContext().setAttribute(MusicLibraryBaseServlet.MUSIC_LIB,  threadSafe_musicLibrary);
				
				// set dbconfig data
				sce.getServletContext().setAttribute(DBConfig.DBCONFIG,  dbconfig);
				
				// set attribute userLock into web container
				sce.getServletContext().setAttribute(MusicLibraryBaseServlet.USERTABLE_LOCK,  userLock);
				
				// set attribute favLock into web container
				sce.getServletContext().setAttribute(MusicLibraryBaseServlet.FAVTABLE_LOCK,  favLock);
				
				
				
							
			}
						
		});
		
		// add servlets		
		
		// search-servlet - go to a search page
		servhandler.addServlet(SearchServlet.class, "/search");
		// song-servlet for executing a search on music library and return an html page
		servhandler.addServlet(SongServlet.class, "/song");
		
		/** TODO: add appropriate servlet **/
		// signup-servlet - go to sign up page
		servhandler.addServlet(SignUpServlet.class, "/signup");
		
		// login-servlet - if user has been logged-in, display same song finder and a "add Fav Song" features
		servhandler.addServlet(LoginServlet.class, "/login");
		
		// change new password servlet - let user to change new password
		servhandler.addServlet(ChangePasswordServlet.class, "/changepassword");
		
		// logout-server 
		servhandler.addServlet(LogoutServlet.class, "/logout");
		
		// verify-user servlet - go to verify (hidden logic page)
		servhandler.addServlet(VerifyUserServlet.class, "/verifyuser");
		
		// fav-list servlet - go to favList 
		servhandler.addServlet(FavListServlet.class, "/favlist");
		
		// consider wild card, where user simply type in an unreachable path
		// redirect them to sign up page
		servhandler.addServlet(SignUpServlet.class, "/*");
		
		servhandler.addServlet(CheckServlet.class, "/check");
		
		/** Advance Features **/
		servhandler.addServlet(AllArtistServlet.class, "/allartists");
		
		servhandler.addServlet(ArtistInfoServlet.class, "/artistinfo");
		
		
		
		/** NEW UPDATE **/
		servhandler.addServlet(SearchHistoryServlet.class, "/searchhistory");
		
		
		
		
		
		//set the list of handlers for the server
		server.setHandler(servhandler);
		
		// server start
		server.start();
		// waiting to join
		server.join();
		
	}
	
	// setupDBConfig
	public static DBConfig setupDBConfig(){
		
		try {
			BufferedReader reader = Files.newBufferedReader(Paths.get("dbconfig.json"));
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(reader);
			
			String username = (String) object.get("username");
			String password = (String) object.get("password");
			String db = (String) object.get("db");
			String host = (String) object.get("host");
			String port = (String) object.get("port");

			dbconfig = new DBConfig(username, password, db, host, port);
			
		} catch(Exception e) {
			fail("DBConfi setup fail: " + e.getMessage());
		}
		
		return dbconfig;
		
	}
	

}
