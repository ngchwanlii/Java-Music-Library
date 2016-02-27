import java.util.Comparator;

public class ArtistComparator implements Comparator<Song> {

	@Override
	public int compare(Song o1, Song o2) {

		if (o1.getArtistName().equals(o2.getArtistName()) && o1.getTitle().equals(o2.getTitle())) {
			return o1.getTitle().compareTo(o2.getTitle());
		} else if (o1.getArtistName().equals(o2.getArtistName())) {
			return o1.getTitle().compareTo(o2.getTitle());
		}

		return o1.getArtistName().compareTo(o2.getArtistName());
	}

}
