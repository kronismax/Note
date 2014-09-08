package note.remote;

@Deprecated
public class RemoteNote {

	private static long count = 0;
	public long id;
	String title;
	String text;

	public RemoteNote(String text1, String title1) {
		text = text1;
		title = title1;
		id = count++;
	}

}
