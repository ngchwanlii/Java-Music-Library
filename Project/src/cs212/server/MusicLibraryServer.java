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
import org.json.simple.JSONArray;
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
	private static final String ADMIN_NAME = "Jay";
	private static final String SECRET_KEY = "jay890703vivfam";
	public static String JETTY_SERVER = "server";
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
			
				// check if server is shutdown!
				System.out.println("Server is shutdown");
				
				
			}

			
			@Override
			// set the intialized context  
			public void contextInitialized(ServletContextEvent sce) {
				
			try{

				// number of threads for the thread pool
				int nThreads = MAX_THREADS;
				
									
				// create musiclibrary (thread version)		  - [task: set content of musiclibrary and ready for build up]
				ThreadSafeMusicLibrary threadSafe_musicLibrary = new ThreadSafeMusicLibrary(MUSIC_DATAPATH);
				
				
				
				// create song_data_processor (thread version) - [task: addSong and build up musiclibrary]
				SongDataProcessor processSongData = new SongDataProcessor(threadSafe_musicLibrary, MUSIC_DATAPATH, nThreads);
			
				
				LastFMClient lastFMClient = new LastFMClient();
				
			
				// this lock is for userLock (retrieve / update userTable) 
				ReentrantLock userLock = new ReentrantLock();
				
				// this lock is for favLock (retrieve / update favListTable);
				ReentrantLock favLock = new ReentrantLock();
				
				// this lock is for search history
				ReentrantLock searchHistoryLock = new ReentrantLock();
		
				
				// create new dbconfig
				DBConfig dbconfig = setupDBConfig();
				
				/*********************
				 *  Database zone	 *
				 *********************/
				
				/****** artistTable in mySQL ******/
				
				// Storing MusicLibrary persistently
				//  create ArtistTable - Artist name is key			
//				DBHelper.createArtistTable(dbconfig);
//				
//			
//				JSONObject artistMusicLib = threadSafe_musicLibrary.getArtistMusicLibrary();
//				
//				JSONArray artistArray = (JSONArray) artistMusicLib.get("artistMusicLibrary");
//				
//				for(int i = 0; i < artistArray.size(); i++){
//					
//					String artist = (String) artistArray.get(i);
//					
//					DBHelper.addArtistTable(dbconfig, artist);
//					
//				}
//				
//				
//				System.out.println("END of artistTable");
//				
//				/****** END of artistTable ******/
//				
//				
//				
//				/****** songTitle in mySQL ******/				
//				//  create TitleTable - [songtitle, artistname, trackID]
//				DBHelper.createSongTitleTable(dbconfig);
//				
//				JSONObject titleMusicLibObj = threadSafe_musicLibrary.getSongTitleMusicLibrary();
//				
//				JSONArray songsArray = (JSONArray) titleMusicLibObj.get("artistMusicLibrary");
//				
//				for(int i = 0; i < songsArray.size(); i++){
//					
//					JSONObject songInfo = (JSONObject) songsArray.get(i);
//				
//					String artistName = (String)songInfo.get("artistName");
//					String songTitle = (String)songInfo.get("songTitle");
//					String trackID = (String)songInfo.get("trackID");
//					
//					// ready to add to database
//					DBHelper.addSongTitleTable(dbconfig, artistName, songTitle, trackID);
//					
//				
//				}
//				
//				System.out.println("END of songTitleTable");
//				
//				/****** END of songTitle ******/
//				
//				
//			
//				/****** Tag in mySQL ******/					
//				// create TagTable - [tag, trackID]
//				DBHelper.createTagTable(dbconfig);
//				
//				
//				JSONObject tagMusicLibObj = threadSafe_musicLibrary.getTagMusicLibrary();
//				
//				JSONArray tagsArray = (JSONArray) tagMusicLibObj.get("tagMusicLibrary");
//				
//				for(int i = 0; i < tagsArray.size(); i++){
//					
//					JSONObject tagInfo = (JSONObject) tagsArray.get(i);
//			
//					String tag = (String)tagInfo.get("tag");
//					String trackID = (String)tagInfo.get("trackID");
//					
//					// ready to add to database
//					DBHelper.addTagTable(dbconfig, tag, trackID);
//					
//				
//				}
//				
//				System.out.println("END of tagTable");
//				
//				/****** END of tag ******/
//				
//				
//				
//				/****** TrackID in mySQL ******/
//				// create TrackIDTable [trackID, songTitle]
//				DBHelper.createTrackIDTable(dbconfig);
//				
//				JSONObject trackIDMusicLibObj = threadSafe_musicLibrary.getTrackIDMusicLibrary();
//				
//				JSONArray trackIDArray = (JSONArray) trackIDMusicLibObj.get("trackIDMusicLibrary");
//				
//				for(int i = 0; i < trackIDArray.size(); i++){
//					
//					JSONObject trackIDInfo = (JSONObject) trackIDArray.get(i);
//			
//					String trackID = (String)trackIDInfo.get("trackID");
//					String tag = (String)trackIDInfo.get("tag");
//				
//					// ready to add to database
//					DBHelper.addTrackIDTable(dbconfig, trackID, tag);
//					
//				}
				
//				System.out.println("END of trackIDTable");
				
//				/****** END of trackID ******/
//				
//				
//				
//				
//				// create adminTable
				DBHelper.createAdminTable(dbconfig);
				
				// insert admin info = preset by admin only
				DBHelper.setAdmin(dbconfig, ADMIN_NAME, SECRET_KEY);
				
				
				
				
				// create userTable					
				DBHelper.createUserTable(dbconfig);
				
				// create favTable
				
				/** RESUME LATER **/
				DBHelper.createFavTable(dbconfig);

			

				// create SearchHistoryTable
				DBHelper.createSearchHistoryTable(dbconfig);
				
				
				// create loginUserTimeTable
				DBHelper.createUserLoginTimeTable(dbconfig);
				
				// create Top 100 Artist Chart 
				DBHelper.createTop100ArtistChart(dbconfig);
				
				// create artistDetailsInfoTable
				DBHelper.createArtistInfoDetailsTable(dbconfig);

				
//					/** DEBUG MSG **/
//					System.out.println("created Usertable and Favtable, SearchHistoryTable, LoginTimeTable, top100Chart");
////											
//					// fetch and store artist information using lastFM API										
//					/** DEBUG USE FOR quickly setup server **/
////					LastFMClient.fetchSingleArtist("Radiohead", dbconfig);
//					
//			
//					LastFMClient.fetchTopArtistsChart(dbconfig); 
//					
//					LastFMClient.fetchAndStoreArtists(threadSafe_musicLibrary.getSortedArtistName(), dbconfig);
//					
//					/** DEBUG MSG **/
//					System.out.println("finish fetch and store artist in MusicLibraryServer");
//					
//					DBHelper.createArtistPlayCountTable(dbconfig);
					
					
					
					/** DEBUG MSG **/
					System.out.println("created artistPlayCountTable  - the END");
					
					
				/** MAIN USE - RESUME THESE AFTER
				
				 LastFMClient.fetchTopArtistsChart(dbconfig); 
				  
				LastFMClient.fetchAndStoreArtists(threadSafe_musicLibrary.getSortedArtistName(), dbconfig);
				
				// create ArtistPlayCountTable
				DBHelper.createArtistPlayCountTable(dbconfig);	
				
				
				**/
				
				
				
				// set jetty server into web container - use for shut down later on
				sce.getServletContext().setAttribute(JETTY_SERVER, server);
				
				// set attribute music_lib
				sce.getServletContext().setAttribute(MusicLibraryBaseServlet.MUSIC_LIB,  threadSafe_musicLibrary);
				
				// set dbconfig data
				sce.getServletContext().setAttribute(DBConfig.DBCONFIG,  dbconfig);
				
				// set attribute userLock into web container
				sce.getServletContext().setAttribute(MusicLibraryBaseServlet.USERTABLE_LOCK,  userLock);
				
				// set attribute favLock into web container
				sce.getServletContext().setAttribute(MusicLibraryBaseServlet.FAVTABLE_LOCK,  favLock);
				
				
				sce.getServletContext().setAttribute("searchHistoryLock",  searchHistoryLock);
				
					
					
					/** DEBUG dropUserTable **/
														
//					clearAllTable();

					/** DEBUG dropFavTable **/
				} 
				catch (SQLException e) {				
					e.printStackTrace();
				}
						
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
		servhandler.addServlet(SearchHistoryServlet.class, "/searchHistory");
		
		servhandler.addServlet(SearchSuggestionServlet.class, "/searchsuggestion");
		
		// Top 100 Artist Chart
		servhandler.addServlet(Top100ArtistChartServlet.class, "/chart");
		
		// Admin page
		servhandler.addServlet(AdminServlet.class, "/admin");
		
		// Gracefully shutdown servlet
		servhandler.addServlet(ShutdownServlet.class, "/shutdown");
		
		
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
	
	// for clearing all table
	public static void clearAllTable(){
		
//		DBHelper.clearTables(dbconfig, DBHelper.adminTable);
//		DBHelper.clearTables(dbconfig, DBHelper.artistTable);
//		DBHelper.clearTables(dbconfig, DBHelper.songTitleInfoTable);
//		DBHelper.clearTables(dbconfig, DBHelper.tagInfoTable);
//		DBHelper.clearTables(dbconfig, DBHelper.trackIDInfoTable);				
//		DBHelper.clearTables(dbconfig, DBHelper.artistInfoTable);
//		DBHelper.clearTables(dbconfig, "user");					
//		DBHelper.clearTables(dbconfig, "fav");				
//		DBHelper.clearTables(dbconfig, DBHelper.artistPlayCountTable);
//		DBHelper.clearTables(dbconfig, "time");
//		DBHelper.clearTables(dbconfig, "searchHistory");							
//		DBHelper.clearTables(dbconfig, DBHelper.top100ArtistChartTable);	
		
		
	}
	

}
