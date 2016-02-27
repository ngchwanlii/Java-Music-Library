import java.util.Comparator;

public class TitleComparator implements Comparator<Song> {

	@Override
	public int compare(Song song1, Song song2) {
		
		
		if(song1.getTitle().equals(song2.getTitle()) && song1.getArtistName().equals(song2.getArtistName())){
			
			// if song has equal title & artist, sort by trackID
			return song1.getTrackID().compareTo(song2.getTrackID());
		}
		else if(song1.getTitle().equals(song2.getTitle())){
			
			// if song has equal title, sort by artist name
			return song1.getArtistName().compareTo(song2.getArtistName());
		}
		
		// initially, sort by title name in lexicographically
		return song1.getTitle().compareTo(song2.getTitle());
	}

		
}
