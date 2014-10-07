package note.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import note.api.ApiException.TypeOfError;
import note.model.Note;
import note.remote.DataBaseUsers;
import note.remote.RemoteUser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class API {

	public boolean setUser(String login, String password){

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

	public boolean checkUser(String login, String password){
		boolean userExists = false;
		ArrayList<RemoteUser> mUsers = DataBaseUsers.getInstance().getUsers();
		for (int i = 0; i < mUsers.size(); i++) {
			if (mUsers.get(i).login.equals(login) && mUsers.get(i).password.equals(password)) {
				userExists = true;
			}
		}
		return userExists;
	}

	// public boolean chengPassword(String login, String password, String
	// oldPassword) {
	// boolean chengPassword = false;
	// ArrayList<RemoteUser> mUsers = DataBaseUsers.getInstance().getUsers();
	// for (int i = 0; i < mUsers.size(); i++) {
	// if (mUsers.get(i).login.equals(login) &&
	// mUsers.get(i).password.equals(oldPassword)) {
	// DataBaseUsers.getInstance().setUsersChengPassword(i, new
	// RemoteUser(login, password));
	// chengPassword = true;
	// }
	// }
	// return chengPassword;
	// }

	public void putNote(String LOGIN, String NOTE, String NOTE_TITLE_NOTE){
		DataBaseUsers.getInstance().setNote(LOGIN, NOTE, NOTE_TITLE_NOTE);
	}

	public static Uri.Builder builder(String oper){

		Uri.Builder builder = new Uri.Builder();

		builder.scheme("http").encodedAuthority("notes-androidcoursesdp.rhcloud.com").appendPath("REST").appendPath(oper);

		return builder;
	}

	public static String GET(String url) throws ApiException{

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

		public int		result;
		public String	sessionId;

		public int getResult(){
			return result;
		}

		public String getSessionID(){
			return sessionId;
		}

		public LoginResponse() {
		}

		public LoginResponse(JSONObject json) throws JSONException {
			result = json.getInt("result");
			sessionId = json.getString("sessionID");
		}
	}

	public LoginResponse login(String login, String password) throws ApiException{
		try {
			String rawResponse = GET("http://notes-androidcoursesdp.rhcloud.com/REST/login?login=" + login + "&pass=" + password);
			return new LoginResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(ApiException.typeOfError.ERROR_JSON, e);
		}
	}

	public RegisterResponse register(String login, String password) throws ApiException{
		String rawResponse = GET("http://notes-androidcoursesdp.rhcloud.com/REST/register?login=" + login + "&pass=" + password);
		try {
			return new RegisterResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(ApiException.typeOfError.ERROR_JSON, e);
		}
	}

	public static class RegisterResponse {

		public int	result;

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
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		inputStream.close();
		return result;
	}

	public static class ChengPasswordResponse {

		int	userChengPassword;

		public ChengPasswordResponse(int userChengPassword) {
			this.userChengPassword = userChengPassword;
		}

		public int getChengPasswordResponse(){
			return userChengPassword;
		}
	}

	public ChengPasswordResponse chengPassword(String sessionID, String newPassword, String oldPassword) throws ApiException{
		int userChengPassword;

		Uri.Builder builder = API.builder("changePassword");
		builder.appendQueryParameter("sessionID", sessionID).appendQueryParameter("oldPass", oldPassword).appendQueryParameter("newPass", newPassword);

		Log.d("chengPassword", builder.build().toString());

		String a = GET(builder.build().toString());
		try {
			JSONObject json = new JSONObject(a);
			userChengPassword = json.getInt("result");
		} catch (Exception e) {
			throw new ApiException(TypeOfError.ERROR_JSON, e);
		}
		return new ChengPasswordResponse(userChengPassword);
	}

	public class LogOutResponse {

		int	userLogOut;

		public LogOutResponse(int userLogOut) {
			this.userLogOut = userLogOut;
		}

		public int getChengPasswordResponse(){
			return userLogOut;
		}
	}

	public LogOutResponse logOut(String ID) throws ApiException{
		int userLogOut;

		Uri.Builder builder = API.builder("logout");
		builder.appendQueryParameter("sessionID", ID);

		String a = GET(builder.build().toString());
		try {
			JSONObject json = new JSONObject(a);
			userLogOut = json.getInt("result");
		} catch (Exception e) {
			throw new ApiException(TypeOfError.ERROR_JSON, e);
		}
		return new LogOutResponse(userLogOut);
	}

	public class NewNoteResponse {

		long	ID;
		int		result;

		public NewNoteResponse(JSONObject json) throws JSONException {
			result = json.getInt("result");
			ID = json.getLong("noteID");
		}

		public long getNoteID(){
			return ID;
		}

		public int getResult(){
			return result;
		}
	}

	public NewNoteResponse newNote(String id, String title, String text) throws ApiException{
		String rawResponse = GET("http://notes-androidcoursesdp.rhcloud.com/REST/createNote?sessionID=" + id + "&title=" + title + "&content=" + text);
		try {
			return new NewNoteResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(ApiException.typeOfError.ERROR, e);
		}
	}

	public static class NoteResponse {

		public String	title;
		public String	shortContent;
		public long		noteID;

		public NoteResponse(JSONObject obj) throws JSONException {
			this.title = obj.getString("title");
			this.shortContent = obj.getString("shortContent");
			this.noteID = obj.getLong("noteID");
		}
	}

	ArrayList<NoteResponse>	notes;

	public class NoteListResponse {

		int				result;
		ArrayList<Note>	note;

		public NoteListResponse(int noteListResponse,ArrayList<Note> note) {
			this.result = noteListResponse;
			this.note = note;
		}

		public int getNoteCreate(){
			return result;
		}

		public ArrayList<Note> getNoteArray(){
			return note;
		}
	}

	public NoteListResponse getNotesList(String ID) throws ApiException{
		int noteCreate;
		Note[] note;

		Uri.Builder builder = API.builder("getNotesList");
		builder.appendQueryParameter("sessionID", ID);

		String a = GET(builder.build().toString());

		try {
			JSONObject json = new JSONObject(a);
			noteCreate = json.getInt("result");

			JSONArray jsonArray = json.getJSONArray("notes");
			note = new Note[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject item = jsonArray.getJSONObject(i);

				Log.d("putNote", item.getString("title"));
				note[i] = new Note(item.getString("title"), item.getString("shortContent"), item.getLong("noteID"));
			}
		} catch (JSONException e) {
			throw new ApiException(TypeOfError.ERROR_JSON, e);
		}

		ArrayList<Note> mNotes = new ArrayList<Note>(Arrays.asList(note));
		Log.d("GET_NOTE_LIST", mNotes.toString());
		return new NoteListResponse(noteCreate, mNotes);
	}

	public class GetNoteResponse {

		int		getNoteResponse;
		String	title;
		String	content;

		public GetNoteResponse(int getNoteResponse,String title,String content) {
			this.getNoteResponse = getNoteResponse;
			this.title = title;
			this.content = content;
		}

		public int getGetNote(){
			return getNoteResponse;
		}

		public String getTitle(){
			return title;
		}

		public String getContent(){
			return content;
		}
	}

	public GetNoteResponse getNote(String sessionID, long noteID) throws ApiException{
		int getNoteResponse;
		String title;
		String content;

		Uri.Builder builder = API.builder("getNote");
		Log.d("e", "id: " + noteID);
		builder.appendQueryParameter("sessionID", sessionID).appendQueryParameter("noteID", Long.toString(noteID));
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

	public class EditNoteResponse {

		int	editNoteResponse;

		public EditNoteResponse(int editNoteResponse) {
			this.editNoteResponse = editNoteResponse;
		}

		public int getEditNoteResponse(){
			return editNoteResponse;
		}
	}

	public EditNoteResponse getEditNote(String sessionID, long noteID, String text) throws ApiException{
		int EditNoteResponse;
		Uri.Builder builder = API.builder("editNote");
		builder.appendQueryParameter("sessionID", sessionID).appendQueryParameter("noteID", Long.toString(noteID)).appendQueryParameter("text", text);
		Log.d("EDIT_NOTE", builder.build().toString());
		try {
			JSONObject json = new JSONObject(GET(builder.build().toString()));
			EditNoteResponse = json.getInt("result");
		} catch (Exception e) {
			Log.d("GetNote", e.toString());
			throw new ApiException(TypeOfError.ERROR_JSON, e);
		}

		return new EditNoteResponse(EditNoteResponse);
	}

	public class DeleteNoteResponse {

		public int	result;

		public DeleteNoteResponse(int deleteNoteResponse) {
			this.result = deleteNoteResponse;
		}

	}

	public DeleteNoteResponse deleteNote(String sessionId, long noteId) throws ApiException{
		int deleteNoteResponse;
		Uri.Builder builder = API.builder("deleteNote");
		builder.appendQueryParameter("sessionID", sessionId).appendQueryParameter("noteID", Long.toString(noteId));

		try {
			JSONObject json = new JSONObject(GET(builder.build().toString()));
			deleteNoteResponse = json.getInt("result");

		} catch (Exception e) {
			throw new ApiException(TypeOfError.ERROR_JSON, e);
		}
		return new DeleteNoteResponse(deleteNoteResponse);
	}

}