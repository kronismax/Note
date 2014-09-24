package note.model;

import java.util.ArrayList;

import android.util.Log;

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

	public void setNoteID(Long noteID){
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
		Log.d("Set title", "" + title);
		mNotes.set(position, new Note(title, content, id));
	}

}
