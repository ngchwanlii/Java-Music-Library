package cs212.util.comparator;
import java.util.Comparator;

import cs212.data.Song;

public class TitleComparator implements Comparator<Song> {

	@Override
	public int compare(Song song1, Song song2) {
		
		// if song has same title & artist, sort by trackID
		if(song1.getTitle().equals(song2.getTitle()) && song1.getArtistName().equals(song2.getArtistName())){
						
			return song1.getTrackID().compareTo(song2.getTrackID());
		}
		// if two song has same title, sort by artist name
		else if(song1.getTitle().equals(song2.getTitle())){
	
			return song1.getArtistName().compareTo(song2.getArtistName());
		}
		
		// initially, sort by title name in lexicographically
		return song1.getTitle().compareTo(song2.getTitle());
	}
		
}
