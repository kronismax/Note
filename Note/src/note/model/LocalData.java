package note.model;

import java.util.ArrayList;

public class LocalData {
	private String noteID;
	private String sessionID;
	public ArrayList<Note> mNotes = new ArrayList<Note>();

	public void setmNotes(ArrayList<Note> mNotes) {
		this.mNotes = mNotes;
	}

	public void setSessionId(String sessionId) {
		this.sessionID = sessionId;
	}

	public void setNoteID(String noteID) {
		this.noteID = noteID;
	}

	public String getSessionId() {
		return sessionID;
	}

	public ArrayList<Note> getmNotes() {
		return mNotes;
	}

	public String getNoteID() {
		return noteID;

	}
}
