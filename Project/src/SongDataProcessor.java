import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SongDataProcessor {
	
	// instance variable
	private MusicLibrary ml;
	private ThreadSafeMusicLibrary threadSafeML;
	private HashSet<String> checkSearchType;
	private ThreadPool threadPool;
	private ThreadPool searchPool;	
	private JSONArray artistResult;
	private JSONArray titleResult;
	private JSONArray tagResult;
	private ReentrantLock aritstLock;
	private ReentrantLock titleLock;
	private ReentrantLock tagLock;
	
	private int nThreads;
	
	// SongDataProcessor constructor - single thread version
	public SongDataProcessor(MusicLibrary musicLibrary, String inputStringPath) {

		this.ml = musicLibrary;
		Path inputPath = Paths.get(inputStringPath);
		findFile(inputPath);
		
	}
	
	// SongDataProcessor constructor - multi-thread version
	public SongDataProcessor(ThreadSafeMusicLibrary threadSafeMusicLibrary, String inputStringPath, ThreadPool threadPool, int nThreads) {
		
		// initialize threadSafeMusicLibrary once
		this.threadSafeML = threadSafeMusicLibrary;
		this.nThreads = nThreads;
		this.threadPool = threadPool;
		Path inputPath = Paths.get(inputStringPath);		
		findFile(inputPath);
		
		// encapsulate threadPool shutDown & await inside SongDataProcessor's constructor 		
		// shutDown threadPool - previously submitted task will still execute
		this.threadPool.shutDown();		
		// threadPool - awaiTermination 
		// Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current thread is interrupted, 
		// whichever happens first.		
		this.threadPool.awaitTermination();	
		
						
	
	}
	
	
	// SongDataProcessor constructor - multi-thread version - search function
	public SongDataProcessor(ThreadSafeMusicLibrary threadSafeMusicLibrary, String inputStringPath, 
							String searchInPath, ThreadPool threadPool, ThreadPool searchPool, int nThreads) {
		
		// initialize threadSafeMusicLibrary once
		this.threadSafeML = threadSafeMusicLibrary;
		this.nThreads = nThreads;
		this.threadPool = threadPool;
		this.searchPool = searchPool;
		
		Path inputPath = Paths.get(inputStringPath);		
		findFile(inputPath);
		
		// encapsulate threadPool shutDown & await inside SongDataProcessor's constructor - FIXED		
		// shutDown threadPool - previously submitted task will still execute
		this.threadPool.shutDown();
		
		// threadPool - awaiTermination 
		// Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current thread is interrupted, 
		// whichever happens first.		
		this.threadPool.awaitTermination();
		
		/** jay  **/
		this.artistResult = new JSONArray();
		this.titleResult = new JSONArray();
		this.tagResult = new JSONArray();
		this.aritstLock = new ReentrantLock();
		this.titleLock = new ReentrantLock();
		this.tagLock = new ReentrantLock();		
		this.checkSearchType = new HashSet<String>();
		Path searchIn = Paths.get(searchInPath);
		searchQueryFile(searchIn);
					
		// shut down searchPool
		this.searchPool.shutDown();
		
		// await searchPool termination - wait all searchThread joining here
		this.searchPool.awaitTermination();
		
		
		/** DEBUG artistResult/titleResult/tagResult before forming a last searchResultObj **/
//		System.out.println(artistResult); - OKAY
//		System.out.println(titleResult); - OKAY
//		System.out.println(tagResult); - OKAY
		
		// passed each searchByMethod
		
		// form a searchResultObject based on artistResult + titleResult + tagResult
		
		
		
	
	}
	
	// traverse and findFiles within the File System
	public void searchQueryFile(Path path) {
		
		if (Files.isDirectory(path)) {

			try (DirectoryStream<Path> list = Files.newDirectoryStream(path)) {

				for (Path file : list) {

					searchQueryFile(file);

				}

			} 
			catch (IOException e) {
				System.out.println(e);
			}
		}

		else {

			// if checkFileFormat = true = found JSON file
			if (checkFileFormat(path)) {
				
				// TODO: when meet new JSON files, put it into queue
				// multi-thread version
				if(nThreads != 0){
					
					JSONObject queryFileObject = parseSearch(path);
					
					/** querFileObject BELOW
					{
					    "searchByArtist": ["Queen", "Busta Rhymes"],
					    "searchByTitle": ["Wishlist", "Ode To Billie Joe  (Live @ Fillmore West)"],
					    "searchByTag": ["50s rockabilly"]
					}
					**/
					for(Object obj : queryFileObject.keySet()){
						
						// key = searchByArtist/searchByTitle/searchByTag
						String key = (String)obj;															
						
						// queryArray = an JSONArray value retrieve from key field 
						JSONArray queryArray = (JSONArray)queryFileObject.get(key);
						
						
						if(key.equals("searchByArtist")){
							
							// mark searchType + assigning task to executor +  build JSONArray that contain similarSong as JSONObject based on search type and search query  
							searchTaskExecutor(key, queryArray, artistResult, aritstLock);
							
						}
						else if(key.equals("searchByTag")){
														
							searchTaskExecutor(key, queryArray, tagResult, tagLock);
							
						}
						else if(key.equals("searchByTitle")){
							
							searchTaskExecutor(key, queryArray, titleResult, titleLock);
							
						}
						
						
					}
					
					
				}
				
			}

		}

	}
	
	// searchTaskExecutor method - this method mark searchType, assign task to threadpool
	public void searchTaskExecutor(String keyType, JSONArray queryArray, JSONArray typeResultArray, ReentrantLock lock){
		
		// mark searchType
		checkSearchType.add(keyType);
		
		for(int i = 0; i < queryArray.size(); i++){
			
			String artist = (String)queryArray.get(i);
			
			// use artistResult + artistLock
			searchPool.execute(new SearchQuery(keyType, artist, threadSafeML, typeResultArray, lock));
		}
		
	}
	
	
	// parseSearch function - return a JSONObject found from queryFile
	public JSONObject parseSearch(Path path) {

		JSONParser jsonParser = new JSONParser();
		JSONObject queryFileObj = new JSONObject();
		
		
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			
	
			queryFileObj = (JSONObject) jsonParser.parse(reader);
		
		} 
		catch (IOException e) {
			System.out.println(e);
		} 
		catch (ParseException e) {

			System.out.println(e);
		}
		return queryFileObj;

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
				
				// TODO: when meet new JSON files, put it into queue
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
			
						
			//TODO: pass in reader instead of line - FIXED
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
