package note.model;

public class Note {

	long id;
	String title;
	String description;

	public Note() {
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

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setID(long id) {
		this.id = id;
	}

	public Note(String title, String description, long id) {
		this.title = title;
		this.description = description;
		this.id = id;
	}
}
