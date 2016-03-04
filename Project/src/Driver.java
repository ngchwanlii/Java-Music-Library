public class Driver {
	
	public static void main(String[] args) {
		
		try {
			// if commandLineParser has exception, it will be catch under IllegalArgumentException 
			ParseCommandLineArgs commandLineParser = new ParseCommandLineArgs(args);		
//TODO: pass in specific args rather than entire map.
//TODO: move instantiation of SongDataProcessor here.
			MusicLibrary musicLibrary = new MusicLibrary(commandLineParser.getArgsMap());			
			musicLibrary.writeToTextFile();
			
		}			
		catch (IllegalArgumentException e){	
			System.out.println("Program exit.");
			System.out.println(e);			
			
		}

	}
}
