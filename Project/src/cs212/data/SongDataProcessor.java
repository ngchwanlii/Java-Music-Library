package cs212.data;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import cs212.util.concurrent.ProcessingFile;
import cs212.util.concurrent.ThreadPool;


public class SongDataProcessor {
	
	// instance variable
	private MusicLibrary ml;
	private ThreadSafeMusicLibrary threadSafeML;	
	private ThreadPool threadPool;		
	private int nThreads;
	

	// SongDataProcessor constructor - single thread version
	public SongDataProcessor(MusicLibrary musicLibrary, String inputStringPath) {

		this.ml = musicLibrary;
		Path inputPath = Paths.get(inputStringPath);
		findFile(inputPath);
		
	}
	
	// SongDataProcessor constructor - multi-thread version  - for web search version / for search functionality test
	public SongDataProcessor(ThreadSafeMusicLibrary threadSafeMusicLibrary, String musiclibrary_database, int nThreads) {
		
		// initialize threadSafeMusicLibrary once		
		this.threadSafeML = threadSafeMusicLibrary;
		this.nThreads = nThreads;
		
		
		/** FIXED: encapsulate the creation of thread-pool here! **/
		// create Threadpool 						
		// ThreadPool for building up the music library concurrently
		ThreadPool threadPool = new ThreadPool(nThreads);
		this.threadPool = threadPool;
		
		Path musiclibrary_database_path = Paths.get(musiclibrary_database);		
		findFile(musiclibrary_database_path);
		
		// encapsulate threadPool shutDown & await inside SongDataProcessor's constructor 		
		// shutDown threadPool - previously submitted task will still execute
		this.threadPool.shutDown();		
		// threadPool - awaiTermination 
		// Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current thread is interrupted, 
		// whichever happens first.		
		this.threadPool.awaitTermination();	
		
		// Note: no searchPool required
		// jetty will handle multiple doGet/doPost as multi-threaded 

	}
	

	// traverse and findFiles within the File System
	public void findFile(Path path) {
		
		if (Files.isDirectory(path)) {

			try (DirectoryStream<Path> list = Files.newDirectoryStream(path)) {

				for (Path file : list) {

					findFile(file);

				}

			} 
			catch (IOException e) {
				System.out.println(e);
			}
		}

		else {

			// if checkFileFormat = true = found JSON file
			if (checkFileFormat(path)) {
				
				// when meet new JSON files, put it into queue
				// multi-thread version
				if(nThreads != 0){
					// execute new Runnable class - this class has a same logic as parseFunction which is processing the file, add song to musicLibrary
					threadPool.execute(new ProcessingFile(path, threadSafeML));
				}
				// single-thread version
				else {
					parseFunction(path);
				}
			}

		}

	}
	
	//parse and read the object in JSON object
	// add song to MusicLibrary
	public void parseFunction(Path path) {

		JSONParser jsonParser = new JSONParser();

		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			
						
			// pass in reader instead of line - FIXED
			JSONObject singleSongObject = (JSONObject) jsonParser.parse(reader);
			
			// save as a single song object
			Song song = new Song(singleSongObject);

			// use musicLibrary to add song
			ml.addSong(song);

				
			
		} 
		catch (IOException e) {
			System.out.println(e);
		} 
		catch (ParseException e) {

			System.out.println(e);
		}

	}
	
	// check if the file format is .json extension
	public boolean checkFileFormat(Path path) {

		if (path.toString().toLowerCase().trim().endsWith(".json")) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
