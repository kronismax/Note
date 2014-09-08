package note.api;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import note.remote.DataBaseUsers;
import note.remote.RemoteUser;
@Deprecated
public class APIold {

	public boolean setUser(String login, String password) {

		try {
			TimeUnit.SECONDS.sleep(7);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		boolean userCreate = false;
		ArrayList<RemoteUser> mUsers = DataBaseUsers.getInstance().getUsers();
		if (mUsers.isEmpty()) { DataBaseUsers.getInstance().setUsers(new RemoteUser(login, password));
			userCreate = true;
		} else {
			for (int i = 0; i < mUsers.size(); i++) {
				if (!mUsers.get(i).login.equals(login)) { DataBaseUsers.getInstance().setUsers(new RemoteUser(login, password));
					userCreate = true;
				}
			}
		}
		return userCreate;
	}

	public boolean checkUser(String login, String password) {

		try {
			TimeUnit.SECONDS.sleep(7);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		boolean userExists = false;
		ArrayList<RemoteUser> mUsers = DataBaseUsers.getInstance().getUsers();
		for (int i = 0; i < mUsers.size(); i++) {
			if (mUsers.get(i).login.equals(login)&& mUsers.get(i).password.equals(password)) {
				userExists = true;
			}
		}
		return userExists;
	}

	public boolean chengPassword(String login, String password,String oldPassword) {
		boolean chengPassword = false;
		ArrayList<RemoteUser> mUsers = DataBaseUsers.getInstance().getUsers();
		for (int i = 0; i < mUsers.size(); i++) {
			if (mUsers.get(i).login.equals(login)&& mUsers.get(i).password.equals(oldPassword)) {
				DataBaseUsers.getInstance().setUsersChengPassword(i, new RemoteUser(login, password));
				chengPassword = true;
			}
		}
		return chengPassword;
	}

	public void putNote(String LOGIN, String NOTE, String NOTE_TITLE_NOTE) {
		DataBaseUsers.getInstance().setNote(LOGIN, NOTE, NOTE_TITLE_NOTE);
	}

}
