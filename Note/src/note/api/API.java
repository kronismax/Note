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
		
///////////////////////           LOGIN         /////////////////////////////////////
	
	public static class LoginResponse {

		public String	sessionID	= "";
		public int		result;

		public LoginResponse(JSONObject obj) throws ApiException {
			try {
				sessionID = obj.getString("sessionID");
				result = obj.getInt("result");
			} catch (JSONException e) {
				throw new ApiException(TypeOfError.ERROR_WRONG_DATA, e);
			}
		}

		public int getUserCreate(){
			return result;
		}

		public String getSessionID(){
			return sessionID;
		}

	}

	public LoginResponse login(String login, String password) throws ApiException{
        String rawResponse = GET(createUrlBuilder().appendPath("login")
                .appendQueryParameter("login", login)
                .appendQueryParameter("pass", password)
                .toString());
        LoginResponse response = null;
        try {
            response = new LoginResponse(new JSONObject(rawResponse));
        } catch (JSONException e) {
			throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
        }
        return response;
	}

///////////////////////           REGISTRATION         /////////////////////////////////////

	public static class RegisterResponse {
		public int result;

		public RegisterResponse(JSONObject obj) throws ApiException {
			try {
				result = obj.getInt("result");
			} catch (JSONException e) {
				throw new ApiException(TypeOfError.ERROR_WRONG_DATA, e);
			}
		}
	}
	
	public RegisterResponse register(String login, String password) throws ApiException{

        String rawResponse = GET(createUrlBuilder().appendPath("register")
                .appendQueryParameter("login", login)
                .appendQueryParameter("pass", password)
                .toString());
        RegisterResponse response = null;
        try {
            response = new RegisterResponse(new JSONObject(rawResponse));
        } catch (JSONException e) {
			throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
        }
        return response;
	}

///////////////////////           CHANGE PASSWORD         /////////////////////////////////////
	
	public static class ChangePasswordResponse {
		public int result;

		public ChangePasswordResponse(JSONObject obj) throws ApiException {
			try {
				result = obj.getInt("result");
			} catch (JSONException e) {
				throw new ApiException(TypeOfError.ERROR_WRONG_DATA, e);
			}
		}
	}

	public ChangePasswordResponse getChangePassword(String sessionID, String newPassword, String oldPassword) throws ApiException{
		String rawResponse = GET(createUrlBuilder().appendPath("changePassword")
                .appendQueryParameter("sessionID", sessionID)
                .appendQueryParameter("newPass", newPassword)
                .appendQueryParameter("oldPass", oldPassword)
                .toString());
		ChangePasswordResponse response = null;
		try {
			response = new ChangePasswordResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
		}
		return response;
	}

	/////////////////////////           LOG OUT         ////////////////////////////////////////
	
	public static class LogOutResponse {
		public int	result;
		public LogOutResponse(JSONObject obj) throws ApiException {
			try {
				result = obj.getInt("result");
			} catch (JSONException e) {
				throw new ApiException(TypeOfError.ERROR_WRONG_DATA, e);
			}
		}
	}

	public LogOutResponse logOut(String ID) throws ApiException{
		String rawResponse = GET(createUrlBuilder().appendPath("logout")
				.appendQueryParameter("sessionID", ID)
				.toString());
		LogOutResponse response = null;
		try {
			response = new LogOutResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
		}
		return response;
	}

/////////////////////////           NEW NOTE         ////////////////////////////////////////
	
	// example !
	
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

	public NewNoteResponse newNote(String ID, String title, String content) throws ApiException{
        String rawResponse = GET(createUrlBuilder().appendPath("createNote")
                .appendQueryParameter("sessionID", ID)
                .appendQueryParameter("title", title)
                .appendQueryParameter("content", content)
                .toString());
        NewNoteResponse response = null;
        try {
            response = new NewNoteResponse(new JSONObject(rawResponse));
        } catch (JSONException e) {
			throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
        }
        return response;
    }

/////////////////////////           NOTE LIST         ////////////////////////////////////////
	
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
			throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
		}
		return response;

	}

/////////////////////////           GET NOTE         ////////////////////////////////////////
	
	public static class GetNoteResponse {

		public int	result;
		String		title;
		String		content;

		public GetNoteResponse(JSONObject obj) throws ApiException {

			try {
				result = obj.getInt("result");
				title = obj.getString("title");
				content = obj.getString("content");
			} catch (JSONException e) {
				throw new ApiException(TypeOfError.ERROR_WRONG_DATA, e);
			}

		}

		public int getResult(){
			return result;
		}

		public String getTitle(){
			return title;
		}

		public String getContent(){
			return content;
		}
	}

	public GetNoteResponse getNote(String sessionID, long noteID) throws ApiException{
        String rawResponse = GET(createUrlBuilder().appendPath("getNote")
                .appendQueryParameter("sessionID", sessionID)
                .appendQueryParameter("noteID", Long.toString(noteID))
                .toString());
        GetNoteResponse response = null;
        try {
            response = new GetNoteResponse(new JSONObject(rawResponse));
        } catch (JSONException e) {
        	throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
        }
        return response;
    }
	
//////////////////////////////////         EDIT NOTE       /////////////////////////
	
	public static class EditNoteResponse {

		public int	result;

		public EditNoteResponse(JSONObject obj) throws JSONException {
			result = obj.getInt("result");
		}
	}

	public EditNoteResponse getEditNote(String sessionID, long noteID, String text) throws ApiException{
        String rawResponse = GET(createUrlBuilder().appendPath("editNote")
                .appendQueryParameter("sessionID", sessionID)
                .appendQueryParameter("noteID", String.valueOf(noteID))
                .appendQueryParameter("text", text)
                .toString());
		EditNoteResponse response = null;
		try {
			response = new EditNoteResponse(new JSONObject(rawResponse));
		} catch (JSONException e) {
			throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
		}
		return response;
	}

///////////////////////           DELETE NOTE         ////////////////////////////////////////
	
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
			throw new ApiException(TypeOfError.ERROR_PARSE_RESPONSE, e);
		}
		return response;
	}
	
	

}