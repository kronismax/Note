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
import android.net.Uri.Builder;
import android.util.Log;

public class API {
	
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

	private static Uri.Builder createUrlBuilder() {
		return new Uri.Builder().scheme("http").encodedAuthority("notes-androidcoursesdp.rhcloud.com").appendPath("REST");
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
		int userRegister;
		Uri.Builder builder = API.builder("register");
		builder.appendQueryParameter("login", login).appendQueryParameter("pass", password);
		Log.d("register", builder.build().toString());
		try {
			JSONObject json = new JSONObject(GET(builder.build().toString()));
			userRegister = json.getInt("result");
		} catch (JSONException e) {
			Log.d("regist", e.toString());
			throw new ApiException(TypeOfError.ERROR_JSON, e);
		}
		return new RegisterResponse(userRegister);
	}

	public static class RegisterResponse {

		public int	userCreate;

		public RegisterResponse() {
		}

		public RegisterResponse(int userCreate) {
			this.userCreate = userCreate;
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
		public int	result;
		public LogOutResponse(JSONObject obj) throws ApiException {
			try {
				result = obj.getInt("result");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public LogOutResponse logOut(String ID) throws ApiException{
		String rawResponse = GET(createUrlBuilder().appendPath("logout").appendQueryParameter("sessionID", ID).toString());
		LogOutResponse response = null;
		try {
			response = new LogOutResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static class NewNoteResponse {

		long	ID;
		int		result;

		public NewNoteResponse(JSONObject obj) throws JSONException {
			ID = obj.getLong("noteID");
            result = obj.getInt("result");
        }

		public long getNoteID(){
			return ID;
		}

		public int getResult(){
			return result;
		}
	}

	public NewNoteResponse newNote(String ID, String title, String content) throws ApiException, JSONException{
        String rawResponse = GET(createUrlBuilder().appendPath("createNote")
                .appendQueryParameter("sessionID", ID)
                .appendQueryParameter("title", title)
                .appendQueryParameter("content", content)
                .toString());
        NewNoteResponse response = null;
        try {
            response = new NewNoteResponse(new JSONObject(rawResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
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

	public static class NoteListResponse {

		int				result;
		ArrayList<NoteResponse>	note;

		public NoteListResponse(JSONObject obj) throws JSONException {
			result = obj.getInt("result");
			note = new ArrayList<NoteResponse>();
			JSONArray arr = obj.getJSONArray("notes");
			for (int i = 0; i < arr.length(); ++i) {
				note.add(new NoteResponse(arr.getJSONObject(i)));
			}
		}

		public ArrayList<NoteResponse> getNoteArray(){
			return note;
		}
	}

	public NoteListResponse getNotesList(String ID) throws ApiException{
		String rawResponse = GET(createUrlBuilder().appendPath("getNotesList").appendQueryParameter("sessionID", ID).toString());
		NoteListResponse response = null;
		try {
			response = new NoteListResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;

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

		public DeleteNoteResponse(JSONObject obj) throws JSONException {
            result = obj.getInt("result");
        }

	}

	public DeleteNoteResponse deleteNote(String sessionId, long noteId) throws ApiException{
		String rawResponse = GET(createUrlBuilder().appendPath("deleteNote")
				.appendQueryParameter("sessionID", sessionId)
				.appendQueryParameter("noteID", String.valueOf(noteId))
				.toString());
		DeleteNoteResponse response = null;
		try {
			response = new DeleteNoteResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	

}