import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;



public class MusicLibraryServer {
	
	
	// DEFAULT_PORT assigned
	public static final int DEFAULT_PORT = 9051;
	public static final int MAX_THREADS = 10;
	
	public static void main(String[] args) {
		
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
				
				// the musiclibrary's database path for search (string type here - it will be handle as path in music_library)
				String musicLibraryDataBasePath = "input/lastfm_subset";
				// number of threads for the thread pool
				int nThreads = MAX_THREADS;
				
				// create Threadpool and SearchPool						
				// ThreadPool for building up the music library concurrently
				ThreadPool threadPool = new ThreadPool(nThreads);
				// SearchPool for search purpose
				ThreadPool searchPool = new ThreadPool(nThreads);
				
				// create musiclibrary (thread version)		  - [task: set content of musiclibrary and ready for build up]
				ThreadSafeMusicLibrary threadSafe_musicLibrary = new ThreadSafeMusicLibrary(musicLibraryDataBasePath);
				// create song_data_processor (thread version) - [task: addSong and build up musiclibrary]
				SongDataProcessor processSongData = new SongDataProcessor(threadSafe_musicLibrary, musicLibraryDataBasePath, threadPool, nThreads);
						
				// set attribute 
				sce.getServletContext().setAttribute("music_library",  threadSafe_musicLibrary);
				sce.getServletContext().setAttribute("song_data_processor",  processSongData);
								
			}
						
		});
		
		// add servlets		
		// search-servlet - go to a search page
		servhandler.addServlet(SearchServlet.class, "/search");
		// song-servlet for executing a search on music library and return an html page
		servhandler.addServlet(SongServlet.class, "/song");
	}

}
