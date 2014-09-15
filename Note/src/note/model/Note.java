package note.model;

public class Note {

	private static long counter = -1;

	long id;
	String title;
	String text;

	public Note() {
	}

	public Note(String title, String description) {
		this.title = title;
		this.text = description;
		id = counter++;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.text = description;
	}

}
