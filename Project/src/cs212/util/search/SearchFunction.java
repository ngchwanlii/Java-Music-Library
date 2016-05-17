package cs212.util.search;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs212.data.ThreadSafeMusicLibrary;
import cs212.util.concurrent.ThreadPool;

/** FIXED - created new SearchFunctionc class - this class do all the search task **/
public class SearchFunction {
	
	// instance variable
	private ThreadSafeMusicLibrary threadSafeML;
	private HashSet<String> checkSearchType;
	private ThreadPool searchPool;	
	private JSONArray artistResult;
	private JSONArray titleResult;
	private JSONArray tagResult;
	private JSONObject searchResult;
	private Path searchInPath;
	private Path searchOutPath;
	private int nThreads;
	
	// constructor
	public SearchFunction(String searchIn, String searchOut, ThreadSafeMusicLibrary threadSafeMusicLibrary, int nThreads){
		
		// create a searchpool here
		ThreadPool searchPool = new ThreadPool(nThreads);
		this.searchPool = searchPool;
		this.threadSafeML = threadSafeMusicLibrary;
			
		this.nThreads = nThreads;
		this.artistResult = new JSONArray();
		this.titleResult = new JSONArray();
		this.tagResult = new JSONArray();		
		this.checkSearchType = new HashSet<String>();
		this.searchResult = new JSONObject();
		
		
		this.searchInPath = Paths.get(searchIn);
		this.searchOutPath = Paths.get(searchOut);
		
		/** DEBUG LATER  or DELETE if search Test pass**/
//		Path searchIn = Paths.get(searchInPath);
		
		// search query file 
		searchQueryFile(searchInPath);
		
		// shut down searchPool
		this.searchPool.shutDown();
		
		// await searchPool termination - wait all searchThread joining here
		this.searchPool.awaitTermination();
		
		// form a searchResult based on artistResult + titleResult + tagResult
		buildSearchResult(artistResult, titleResult, tagResult, searchResult);
		
	}
	
	// searchTaskExecutor method - this method mark searchType, assign task to threadpool
	public void searchTaskExecutor(String keyType, JSONArray queryArray, JSONArray typeResultArray){
		
		// mark searchType
		checkSearchType.add(keyType);
		
		for(int i = 0; i < queryArray.size(); i++){
			
			String query = (String)queryArray.get(i);
		
			// use typeResultArray + lock
			searchPool.execute(new SearchQuery(keyType, query, queryArray, threadSafeML, typeResultArray));
		}
		
	}
	
	// searchResult method - return a output JSONObject based on searchedType + searchQuery 
	// group all the searchTypeResult (which formed by JSONArray of each search) to a final searchResultObject  
	public void buildSearchResult(JSONArray artistResult, JSONArray titleResult, JSONArray tagResult, JSONObject searchResult){

		Iterator it = checkSearchType.iterator();
		
		while(it.hasNext()){
			
			String keyType = (String) it.next();
			
			if(keyType.equals("searchByArtist")){
				searchResult.put(keyType, artistResult);
			}
			else if(keyType.equals("searchByTag")){
				searchResult.put(keyType, tagResult);
			}
			else if(keyType.equals("searchByTitle")){
				searchResult.put(keyType, titleResult);
			}			
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
	
	// check if the file format is .json extension
	public boolean checkFileFormat(Path path) {

		if (path.toString().toLowerCase().trim().endsWith(".json")) {
			return true;
		} else {
			return false;
		}
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
				
				// when meet new JSON files, put it into queue
				// multi-thread version
				if(nThreads != 0){
					
					JSONObject queryFileObject = parseSearch(path);
	
					for(Object obj : queryFileObject.keySet()){
						
						// key = searchByArtist/searchByTitle/searchByTag
						String key = (String)obj;															
						
						// queryArray = an JSONArray value retrieve from key field 
						JSONArray queryArray = new JSONArray();
						queryArray = (JSONArray)queryFileObject.get(key);
												
						if(key.equals("searchByArtist")){
						
							// mark searchType + assigning task to executor +  build JSONArray that contain similarSong as JSONObject based on search type and search query  
							searchTaskExecutor(key, queryArray, artistResult);
							
						}
						else if(key.equals("searchByTag")){
														
							searchTaskExecutor(key, queryArray, tagResult);
							
						}
						else if(key.equals("searchByTitle")){
							
							searchTaskExecutor(key, queryArray, titleResult);
							
						}
						
						
					}
					
					
				}
				
			}

		}

	}
	
	// getSearchResult method - calling from Driver class
	public JSONObject getSearchResult(){
		return searchResult;
	}
	
	//TODO: move this functionality to a search component.
	/** FIXED - migrated to here - SearchFunction class **/
	// writeSearchResultToTextFile method - write the searchResult to searchOutputPath
	public void writeSearchResultToTextFile() throws IllegalArgumentException {
		
		if(searchInPath.toFile().exists() && searchOutPath.toFile().getParentFile().isDirectory()){
			
			try(PrintWriter writer = new PrintWriter(Files.newBufferedWriter(searchOutPath, Charset.forName("UTF-8")))){
				
				writer.println(searchResult.toJSONString());
				
			} 
			catch (IOException e) {				
				e.printStackTrace();
			}
			
		}
		else {
			throw new IllegalArgumentException("\nAttempt to write searchOutput file but the inputpath or outputpath's parent directory is not exists.");
		}
		
	}
	

}
