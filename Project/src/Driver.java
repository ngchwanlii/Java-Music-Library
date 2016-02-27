public class Driver {
	
	public static void main(String[] args) {
		
		ParseCommandLineArgs commandLineParser = new ParseCommandLineArgs(args);
		
		// if command-line passed in arguments has error, exit the program
		if(!commandLineParser.checkArgument()){
			
			System.out.println("Program exit.");			
		}	
		else {
			// reach this line means no problem in args, continue all the process
			SongDataProcessor songData = new SongDataProcessor(commandLineParser.getArgsMap());		
			songData.writeToTextFile();
		}

	}
}
