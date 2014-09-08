package note.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import note.remote.DataBaseUsers;
import note.remote.RemoteUser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class API {

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

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null) {
				result = convertInputStreamToString(inputStream); //
			} else
				result = "Did not work!";
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	private void a() {
		String result = "{result:0,sessionID:426261}";
		try {
			JSONObject obj = new JSONObject(result);
			String sessionId = obj.getString("sessionID");
			int status = obj.getInt("result");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// convert inputstream to String
	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}
}
