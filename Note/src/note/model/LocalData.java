package note.model;

import java.util.ArrayList;

public class LocalData {

	private long			noteID;
	private String			sessionID;
	public ArrayList<Note>	mNotes	= new ArrayList<Note>();

	public void setmNotes(ArrayList<Note> mNotes){
		this.mNotes = mNotes;
	}

	public void setSessionId(String sessionId){
		this.sessionID = sessionId;
	}

	public void setNoteID(long noteID){
		this.noteID = noteID;
	}

	public String getSessionId(){
		return sessionID;
	}

	public ArrayList<Note> getmNotes(){
		return mNotes;
	}

	public long getNoteID(){
		return noteID;

	}

	public void addLocalNoteForIndex(String title, String content, long id, int position){
		mNotes.set(position, new Note(title, content, id));
	}

}
