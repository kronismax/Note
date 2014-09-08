package note.model;

import java.util.ArrayList;

public class LocalData {

	public String sessionId;
	public ArrayList<Note> mNotes = new ArrayList<Note>();

	public void setSessionID(String id) {
		sessionId = id;
	}

	public ArrayList<Note> getNote() {
		return mNotes;
	}

}
