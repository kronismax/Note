package note.model;

public class LocalData {

	private String	sessionID;

	public String getSessionId(){
		return sessionID;
	}

	public void setSessionId(String sessionId){
		this.sessionID = sessionId;
	}
	
	/*private long			noteID;
	
	public ArrayList<Note>	mNotes	= new ArrayList<Note>();*/

/*
	public void setmNotes(ArrayList<Note> mNotes) {
		this.mNotes = mNotes;
	}

	public void setNoteID(long noteID) {
		this.noteID = noteID;
	}

	public ArrayList<Note> getmNotes(){
		return mNotes;
	}

	public long getNoteID(){
		return noteID;

	}

	public void addLocalNoteForIndex(String title, String content, long id, int position){
		mNotes.set(position, new Note(title, content, id));
	}*/

}
