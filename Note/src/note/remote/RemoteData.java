package note.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class RemoteData {

	ArrayList<RemoteUser> mUsers = new ArrayList<RemoteUser>();
	Map<String, ArrayList<RemoteNote>> mNotes = new HashMap<String, ArrayList<RemoteNote>>();

	private static RemoteData instance;

	public static synchronized RemoteData getInstance() {
		if (instance == null) {
			instance = new RemoteData();
		}
		return instance;
	}

	public void setUser(String login, String password) {
		mUsers.add(new RemoteUser(login, password));
	}

	public Map<String, ArrayList<RemoteNote>> getUsetNotes() {
		return mNotes;
	}

	public ArrayList<RemoteUser> getUser() {
		return mUsers;
	}

	public void setUsersNote(String login, String text, String title) {
		//mNotes.put(login, new RemouteNote(title, text));
	}

}
