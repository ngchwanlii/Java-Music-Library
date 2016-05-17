package cs212.server;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs212.data.ArtistPlayCountInfo;
import cs212.data.Song;
import cs212.util.comparator.ArtistPlayCountComparator;
import database.DBConfig;
import database.DBHelper;

public class LastFMClient {
	
	private static String API_KEY = "38f0923806233ccfdf46e24ecddffddc";
	private static String GET_ARTIST_INFO_METHOD = "artist.getInfo";
	private static String GET_TOP_ARTIST_CHART_METHOD = "chart.gettopartists";
	private static String HOST = "ws.audioscrobbler.com";
	private static String FORMAT = "json";
	private static ArrayList<String> artistArray;
	
	
	
	// TODO: new added features - implement addition LASTFM API
	public static void fetchTopArtistsChart(DBConfig dbconfig) throws SQLException {
		
		// per page = 50
		// page="2" - 2 pages = Top 100 Artists

		for(int i = 1; i <= 2; i++){
			String page = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0?"
												+ "page=" + i
												+ "&api_key=" + API_KEY 
												+ "&method=" + GET_TOP_ARTIST_CHART_METHOD 
												+ "&format=" + FORMAT);
			
			// create scanner
			Scanner instream = new Scanner(page);
			boolean statusOK = false;
			
			// check status head
			if(instream.hasNext()){
				String line = instream.nextLine();
							
				statusOK = checkStatus(line);
			}
			
			// if status is not HTTP/1.1 200 OK - print error message				
			if(!statusOK){
				// means got error status code from response
				// System.out.println("error status code");
			}
			// process and read rest of line + extract JSONObject which is in String representation
			else {			
				// read line until it hit a blank line -> then extract the JSONObj in String representation
				String info = extractInfo(instream);
				
				// if we can get info for a given artist and not null
				if(info != null){
										
					// convert to JSONObject and extract [name, listeners, playcount, bio]
					// listners & playcount under "stats" object
					try {
						
						JSONParser infoParse = new JSONParser();
						// get jsonObj
						JSONObject jsonObj = (JSONObject)infoParse.parse(info);
						
						JSONObject jsonArtists = (JSONObject) jsonObj.get("artists");
											
						JSONArray artistArray = (JSONArray)jsonArtists.get("artist");
						
						
						for(int j = 0; j < artistArray.size(); j++){
							
							JSONObject obj = (JSONObject) artistArray.get(j);
							
							String artist = (String) obj.get("name");
							
					
							
							DBHelper.addTopArtistChartInfoLastFM(dbconfig, artist);
							
						}
					
					} 
					catch ( ParseException  | NumberFormatException | NullPointerException e) {
						// catch but do nothing
						// cause these API got some bugs
					}			
				}				
			}
		}		
	}
	
	
	
	
	
	
	
	
	/** FOR DEBUG USE - quickly setup server for running and test code
	 * 
	 * @param artists
	 * @param dbconfig
	 * @throws SQLException
	 */
	public static void fetchSingleArtist(String artist, DBConfig dbconfig) throws SQLException {
		
	
		String page = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0?" 
												+ "artist=" + artist 
												+ "&api_key=" + API_KEY 
												+ "&method=" + GET_ARTIST_INFO_METHOD 
												+ "&format=" + FORMAT);
		
			
			// parse header page 
			// create scanner
			Scanner instream = new Scanner(page);
			boolean statusOK = false;
			
			// check status head
			if(instream.hasNext()){
				String line = instream.nextLine();
							
				statusOK = checkStatus(line);
			}
			
			// if status is not HTTP/1.1 200 OK - print error message				
			if(!statusOK){
				// means got erro status code from response
			}
			
			// process and read rest of line + extract JSONObject which is in String representation
			else {
				
				// read line until it hit a blank line -> then extract the JSONObj in String representation
				String info = extractInfo(instream);
				
				
				// if we can get info for a given artist and not null
				if(info != null){
										
					// convert to JSONObject and extract [name, listeners, playcount, bio]
					// listners & playcount under "stats" object
					try {
						
						JSONParser infoParse = new JSONParser();
						// get jsonObj
						JSONObject jsonObj = (JSONObject)infoParse.parse(info);
						
						JSONObject jsonArtist = (JSONObject) jsonObj.get("artist");
						
						// extract artist information
				
						String artistName = (String)jsonArtist.get("name");
						

						// bio
						JSONObject bioObj = (JSONObject)jsonArtist.get("bio");
						
						String bio = (String) bioObj.get("summary");
						
						// get stats object 1st before getting listeners & playcount
						JSONObject stats = (JSONObject)jsonArtist.get("stats");
						
						// listeners
						String listeners = (String)stats.get("listeners");
														
						// playcount
						String playcount = (String)stats.get("playcount");
						
						// convert string listener -> int num
						int listenerInt = Integer.parseInt(listeners);
						// conver string playcount -> int playcount
						int playcountInt = Integer.parseInt(playcount);
						
						
						// get artist image
						JSONArray artistImageArray = (JSONArray) jsonArtist.get("image");
						
						// ready to pickup the right image with correct size
						String image = null;
						
						for(int i = 0; i < artistImageArray.size(); i++){
							
							JSONObject obj = (JSONObject) artistImageArray.get(i);
							
							// pick large image
							if(obj.get("size").equals("large")){
								
								image = (String) obj.get("#text");
								
							}
							
						}
									
						// update/insert artist info to table
						DBHelper.addArtistInfoLastFM(dbconfig, artistName, listenerInt, playcountInt, bio, image);
							
						
					} 
					catch ( ParseException  | NumberFormatException | NullPointerException e) {
						// catch but do nothing
						
					}			
				}
			}
		}
		
	
	
	
	
	
	
	
	
	
	// fetchAndStoreArtist method
	// save ArtistInfo + ArtistPlayCount table into mySQL once initialized
	public static void fetchAndStoreArtists(TreeSet<String> artists, DBConfig dbconfig) throws SQLException {
		
		
		// intialize list array
//		list = new ArrayList<ArtistPlayCountInfo>();
		
//		System.out.println(artists);
		
		// initialize 2 map
		

		for(String str : artists) {
			
			// TODO: create a HTTPFetcher class		
			String artist = str;
			
			String page = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0?" 
												+ "artist=" + artist 
												+ "&api_key=" + API_KEY 
												+ "&method=" + GET_ARTIST_INFO_METHOD 
												+ "&format=" + FORMAT);
		
			
			// parse header page 
			// create scanner
			Scanner instream = new Scanner(page);
			boolean statusOK = false;
			
			// check status head
			if(instream.hasNext()){
				String line = instream.nextLine();
				
				/**  DEBUG USE **/
//				System.out.println(line);
				
				statusOK = checkStatus(line);
			}
			
			// if status is not HTTP/1.1 200 OK - print error message				
			if(!statusOK){
				// means got erro status code from response
			}
			
			// process and read rest of line + extract JSONObject which is in String representation
			else {
				
				// read line until it hit a blank line -> then extract the JSONObj in String representation
				String info = extractInfo(instream);
				
				
				// if we can get info for a given artist and not null
				if(info != null){
										
					// convert to JSONObject and extract [name, listeners, playcount, bio]
					// listners & playcount under "stats" object
					try {
						
						JSONParser infoParse = new JSONParser();
						// get jsonObj
						JSONObject jsonObj = (JSONObject)infoParse.parse(info);
						
						JSONObject jsonArtist = (JSONObject) jsonObj.get("artist");
						
						// extract artist information
				
						String artistName = (String)jsonArtist.get("name");
						

						// bio
						JSONObject bioObj = (JSONObject)jsonArtist.get("bio");
						
						String bio = (String) bioObj.get("summary");
						
						// get stats object 1st before getting listeners & playcount
						JSONObject stats = (JSONObject)jsonArtist.get("stats");
						
						// listeners
						String listeners = (String)stats.get("listeners");
														
						// playcount
						String playcount = (String)stats.get("playcount");
						
						// convert string listener -> int num
						int listenerInt = Integer.parseInt(listeners);
						// conver string playcount -> int playcount
						int playcountInt = Integer.parseInt(playcount);
						
						
						// get artist image
						JSONArray artistImageArray = (JSONArray) jsonArtist.get("image");
						
						// ready to pickup the right image with correct size
						String image = null;
						
						for(int i = 0; i < artistImageArray.size(); i++){
							
							JSONObject obj = (JSONObject) artistImageArray.get(i);
							
							// pick large image
							if(obj.get("size").equals("large")){
								
								image = (String) obj.get("#text");
								
							}
							
						}
									
						// update/insert artist info to table
						DBHelper.addArtistInfoLastFM(dbconfig, artistName, listenerInt, playcountInt, bio, image);
							
						
					} 
					catch ( ParseException  | NumberFormatException | NullPointerException e) {
						// catch but do nothing
						
					}			
				}
			}
		}
		
	}
	
	
	
	// extract string representation of info
	public static String extractInfo(Scanner instream){
		
		String info = "";		
		String line;		
		
		while(instream.hasNext()){

			line = instream.nextLine();
			
			// detect blank line
			if(line.isEmpty()){
									
				// the next line after blank line is the info that need to be extract (contain objJSON string)	
				while(instream.hasNext()){
					
					String json = instream.nextLine();
					
					if(json != null)
						info += json;
				}				
				break;				
			}							
		}
		
		return info;
		
	}
	

	
	// check status method
	// 200 - OK = success - process rest of the file
	// if not - don't process the file
	public static boolean checkStatus(String statusHeader){
		
		
		// match HTTP status code
		String regex = "HTTP/1.1\\s+200\\s+OK";
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(statusHeader);
		
		return m.matches();
		

	}
	
	
}
