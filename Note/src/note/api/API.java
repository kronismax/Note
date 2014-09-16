package note.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import note.api.ApiException.TypeOfError;
import note.remote.DataBaseUsers;
import note.remote.RemoteUser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class API {

	public boolean setUser(String login, String password) {

		boolean userCreate = false;
		ArrayList<RemoteUser> mUsers = DataBaseUsers.getInstance().getUsers();
		if (mUsers.isEmpty()) {
			DataBaseUsers.getInstance().setUsers(new RemoteUser(login, password));
			userCreate = true;
		} else {
			for (int i = 0; i < mUsers.size(); i++) {
				if (!mUsers.get(i).login.equals(login)) {
					DataBaseUsers.getInstance().setUsers(new RemoteUser(login, password));
					userCreate = true;
				}
			}
		}
		return userCreate;
	}

	public boolean checkUser(String login, String password) {
		boolean userExists = false;
		ArrayList<RemoteUser> mUsers = DataBaseUsers.getInstance().getUsers();
		for (int i = 0; i < mUsers.size(); i++) {
			if (mUsers.get(i).login.equals(login) && mUsers.get(i).password.equals(password)) {
				userExists = true;
			}
		}
		return userExists;
	}

	public boolean chengPassword(String login, String password, String oldPassword) {
		boolean chengPassword = false;
		ArrayList<RemoteUser> mUsers = DataBaseUsers.getInstance().getUsers();
		for (int i = 0; i < mUsers.size(); i++) {
			if (mUsers.get(i).login.equals(login) && mUsers.get(i).password.equals(oldPassword)) {
				DataBaseUsers.getInstance().setUsersChengPassword(i, new RemoteUser(login, password));
				chengPassword = true;
			}
		}
		return chengPassword;
	}

	public void putNote(String LOGIN, String NOTE, String NOTE_TITLE_NOTE) {
		DataBaseUsers.getInstance().setNote(LOGIN, NOTE, NOTE_TITLE_NOTE);
	}

	public static Uri.Builder builder(String _oper) {

		Uri.Builder builder = new Uri.Builder();

		builder.scheme("http").encodedAuthority("notes-androidcoursesdp.rhcloud.com").appendPath("REST").appendPath(_oper);

		return builder;
	}

	public static String GET(String url) throws ApiException {
		
		Log.d("e", "request:" + url);
		
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
			throw new ApiException(ApiException.typeOfError.ERROR_CONNECTION, e);
		}
		
		Log.d("e", "response:" + result);
		
		return result;
	}

	public static class LoginResponse {
		public int result;
		public String sessionId;

		public int getResult() {
			return result;
		}

		public LoginResponse() {
		}

		public LoginResponse(JSONObject json) throws JSONException {
			result = json.getInt("result");
			sessionId = json.getString("sessionID");
		}
	}

	public LoginResponse login(String login, String password) throws ApiException {
		try {
			String rawResponse = GET("http://notes-androidcoursesdp.rhcloud.com/REST/login?login=" + login + "&pass=" + password);
			return new LoginResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(ApiException.typeOfError.ERROR_JSON, e);
		}
	}

	public RegisterResponse register(String login, String password) throws ApiException {
		String rawResponse = GET("http://notes-androidcoursesdp.rhcloud.com/REST/register?login=" + login + "&pass=" + password);
		try {
			return new RegisterResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(ApiException.typeOfError.ERROR_JSON, e);
		}
	}

	public static class RegisterResponse {

		public int result;

		public RegisterResponse() {
		}

		public RegisterResponse(JSONObject json) throws ApiException {
			try {
				result = json.getInt("result");
			} catch (JSONException e) {
				throw new ApiException(ApiException.typeOfError.ERROR_JSON, e);
			}
		}
	}

	// convert inputstream to String
	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		inputStream.close();
		return result;
	}

	public class NewNoteResponse {

		long ID;
		int result;

		public NewNoteResponse(JSONObject json) throws JSONException {
			result = json.getInt("result");
			ID = json.getLong("noteID");
		}

		public long getNoteID() {
			return ID;
		}

		public int getResult() {
			return result;
		}
	}

	public NewNoteResponse newNote(String id, String title, String text) throws ApiException {
		String rawResponse = GET("http://notes-androidcoursesdp.rhcloud.com/REST/createNote?sessionID=" + id + "&title=" + title + "&content=" + text);
		try {
			return new NewNoteResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(ApiException.typeOfError.ERROR, e);
		}
	}
	
	public static class NoteResponse {
		String title;
		String shortContent;
		long noteID;

		public NoteResponse(JSONObject obj) throws JSONException {
			this.title = obj.getString("title");
			this.shortContent = obj.getString("shortContent");
			this.noteID = obj.getLong("noteID");
		}
	}
	
	ArrayList<NoteResponse> notes;

	public class GetNoteResponse {
		int getNoteResponse;
		String title;
		String content;

		public GetNoteResponse(int getNoteResponse, String title, String content) {
			this.getNoteResponse = getNoteResponse;
			this.title = title;
			this.content = content;
		}

		public int getGetNote() {
			return getNoteResponse;
		}

		public String getTitle() {
			return title;
		}

		public String getContent() {
			return content;
		}
	}
	
	public GetNoteResponse getNote(String sessionID, String noteID) throws ApiException {
		int getNoteResponse;
		String title;
		String content;

		Uri.Builder builder = API.builder("getNote");
		builder.appendQueryParameter("sessionID", sessionID).appendQueryParameter("noteID", noteID);
		String a = GET(builder.build().toString());

		try {
			JSONObject json = new JSONObject(a);
			getNoteResponse = json.getInt("result");
			title = json.getString("title");
			content = json.getString("content");
		} catch (Exception e) {
			throw new ApiException(TypeOfError.ERROR_JSON, e);
		}
		return new GetNoteResponse(getNoteResponse, title, content);
	}

	
	
}