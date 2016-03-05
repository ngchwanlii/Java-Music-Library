

public class Driver {
	
	public static void main(String[] args) {
		
		try {
			// if commandLineParser has exception, it will be catch under IllegalArgumentException 
			ParseCommandLineArgs commandLineParser = new ParseCommandLineArgs(args);		
//TODO: pass in specific args rather than entire map.	FIXED
//TODO: move instantiation of SongDataProcessor here. 	FIXED	
			
			// set string path args and passed into MusicLibrary
			String inputStringPath = commandLineParser.getArgsMap().get("-input");
			String outputStringPath = commandLineParser.getArgsMap().get("-output");
			String orderStringPath = commandLineParser.getArgsMap().get("-order");
			
			// instantiate new MusicLibrary
			MusicLibrary musicLibrary = new MusicLibrary(inputStringPath, outputStringPath);
			SongDataProcessor processSongData = new SongDataProcessor(musicLibrary, inputStringPath);
			musicLibrary.writeToTextFile(orderStringPath);
			
			
		}			
		catch (IllegalArgumentException e){	
			System.out.println("Program exit.");
			System.out.println(e);			
			
		}

	}
}
