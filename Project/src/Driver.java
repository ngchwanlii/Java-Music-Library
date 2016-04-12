

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
					// create threadPool
					ThreadPool threadPool = new ThreadPool(nThreads);
					ThreadPool searchPool = new ThreadPool(nThreads);
					
					ThreadSafeMusicLibrary threadSafeMusicLibrary = new ThreadSafeMusicLibrary(inputStringPath, outputStringPath, searchInputPath, searchOutputPath);					
					SongDataProcessor processSongData = new SongDataProcessor(threadSafeMusicLibrary, inputStringPath, searchInputPath, threadPool, searchPool, nThreads);
					
					threadSafeMusicLibrary.writeToTextFile(orderStringPath);
					threadSafeMusicLibrary.writeSearchResultToTextFile(processSongData.getSearchResult());
										
					
				}				
				else {
					
					// create threadPool
					ThreadPool threadPool = new ThreadPool(nThreads);
					ThreadSafeMusicLibrary threadSafeMusicLibrary = new ThreadSafeMusicLibrary(inputStringPath, outputStringPath);
					// threadPool will execute task 
					SongDataProcessor processSongData = new SongDataProcessor(threadSafeMusicLibrary, inputStringPath, threadPool, nThreads);
					
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
