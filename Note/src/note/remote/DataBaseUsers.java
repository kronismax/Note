package note.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import note.remote.RemoteNote;
import note.remote.RemoteUser;
@Deprecated
public class DataBaseUsers {
	private ArrayList<RemoteUser> mUsers = new ArrayList<RemoteUser>();
	private Map<String, ArrayList<RemoteNote>> mNotes = new HashMap<String, ArrayList<RemoteNote>>();
	private ArrayList<RemoteNote> RemoteNotes = new ArrayList<RemoteNote>();
	private static volatile DataBaseUsers instance;

	public static DataBaseUsers getInstance() {
		DataBaseUsers localInstance = instance;
		if (localInstance == null) {
			synchronized (DataBaseUsers.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new DataBaseUsers();
				}
			}
		}
		return localInstance;
	}

	public ArrayList<RemoteUser> getUsers() {
		if (mUsers != null) {
			return mUsers;
		}
		return null;
	}

	public void setUsers(RemoteUser remouteUser) {
		mUsers.add(remouteUser);
	}

	public void setUsersChengPassword(int i, RemoteUser remouteUser) {
		mUsers.set(i, remouteUser);
	}

	public void setNote(String login, String text, String title) {
		RemoteNote RN = new RemoteNote(text, title);
		RemoteNotes.add(RN);
		mNotes.put(login, RemoteNotes);
	}

	public Map<String, ArrayList<RemoteNote>> getNote() {
		return mNotes;
	}

	public void init() {

	}
}
