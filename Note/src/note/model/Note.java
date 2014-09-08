package note.model;
public class Note {

	private static long counter = -1;

	long id;
	String title;
	String description;

	public Note() {
	}

	public Note(String _title, String _description) {
		title = _title;
		description = _description;
		id = counter++;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setTitle(String _title) {
		title = _title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
