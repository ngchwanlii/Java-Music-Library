import cs212.data.MusicLibrary;
import cs212.data.SongDataProcessor;
import cs212.data.ThreadSafeMusicLibrary;
import cs212.util.search.SearchFunction;

public class Driver {
	
	public static void main(String[] args) {
		
		try {
			// if commandLineParser has exception, it will be catch under IllegalArgumentException 
			ParseCommandLineArgs commandLineParser = new ParseCommandLineArgs(args);		
	
			
			// set string path args and passed into MusicLibrary
			String inputStringPath = commandLineParser.getArgsMap().get("-input");
			String outputStringPath = commandLineParser.getArgsMap().get("-output");
			String orderStringPath = commandLineParser.getArgsMap().get("-order");
				
			int nThreads = 0;
			String searchInputPath = null;
			String searchOutputPath = null;
			boolean searchActive = false;
			
			if(commandLineParser.getArgsMap().containsKey("-searchInput") && commandLineParser.getArgsMap().containsKey("-searchOutput")){
				
				
				searchInputPath = commandLineParser.getArgsMap().get("-searchInput");
				searchOutputPath = commandLineParser.getArgsMap().get("-searchOutput");
							
				searchActive = true;
			
			}
					
			// multi-thread version
			if(commandLineParser.getArgsMap().containsKey("-threads")){
				// if the hashmap has -threads flag, get the threadNum by parsing the string representation numeric value stored inside HashMap
				nThreads = Integer.parseInt(commandLineParser.getArgsMap().get("-threads"));
				
				
				if(searchActive){
					
					
					ThreadSafeMusicLibrary threadSafeMusicLibrary = new ThreadSafeMusicLibrary(inputStringPath, outputStringPath);					
					SongDataProcessor processSongData = new SongDataProcessor(threadSafeMusicLibrary, inputStringPath, nThreads);
					SearchFunction searchFeatures = new SearchFunction(searchInputPath, searchOutputPath, threadSafeMusicLibrary, nThreads);
					
					
					threadSafeMusicLibrary.writeToTextFile(orderStringPath);
					
					// TODO: move this writeSearchResultToTextFile method to a SearchFunction class
					/** FIXED - migrate to SearchFunction class 
					 * 	use SearchFunction's writeSearchResultToTextFile method to write the searched result into a text file
					 * */
					searchFeatures.writeSearchResultToTextFile();
															
				}				
				else {
										
					ThreadSafeMusicLibrary threadSafeMusicLibrary = new ThreadSafeMusicLibrary(inputStringPath, outputStringPath);
					// threadPool will execute task 
					SongDataProcessor processSongData = new SongDataProcessor(threadSafeMusicLibrary, inputStringPath, nThreads);
					
					// write to text file
					threadSafeMusicLibrary.writeToTextFile(orderStringPath);	
				}
								
			}
			// single-thread version
			else {
				// instantiate new MusicLibrary
				
				MusicLibrary musicLibrary = new MusicLibrary(inputStringPath, outputStringPath);
				SongDataProcessor processSongData = new SongDataProcessor(musicLibrary, inputStringPath);
				musicLibrary.writeToTextFile(orderStringPath);
			}
			
			
			
		}			
		catch (IllegalArgumentException e){	
			System.out.println("Program exit.");
			System.out.println(e);			
			
		}

	}
}
