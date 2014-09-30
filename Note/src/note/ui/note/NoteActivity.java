package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.LogOutResponse;
import note.api.API.NoteListResponse;
import note.api.ApiException;
import note.model.DataBase.DBHelper;
import note.model.DataBase.NoteDatabaseColumns;
import note.ui.login.MainActivity;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.note.R;

public class NoteActivity extends Activity {

	String		LOGIN;
	NoteAdapter	noteAdapter;
	Button		buttonDelete;
	ListView	lv;
	API			API			= new API();
	DBHelper	db;
	String[]	myColumns	= { NoteDatabaseColumns.TableNote._ID, NoteDatabaseColumns.TableNote.TITLE, NoteDatabaseColumns.TableNote.CONTENT };

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_activity);

		new NotesListArrayAsyncTask().execute(new NotesList(((MyApplication) getApplication()).getLocalData().getSessionId()));

		DBHelper db = new DBHelper(this);

		noteAdapter = new NoteAdapter(this, db.getReadableDatabase().query(DBHelper.DATABASE_NAME, myColumns, null, null, null, null, DBHelper.ID));

		lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(noteAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id){
				Intent intent = new Intent(NoteActivity.this, EditNoteActivity.class);
				intent.putExtra("POSITION", position);
				intent.putExtra("ID", id);
				startActivity(intent);
			}
		});
	}

	public class NotesList {

		private String	sessionID;

		NotesList(String sessionID) {
			this.sessionID = sessionID;
		}

		public String getSessionID(){
			return sessionID;
		}
	}

	public class MyNotesListAsyncTask extends AsyncTask<NotesList, Void, NoteListResponse> {

		ApiException	apiexception;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected NoteListResponse doInBackground(NotesList... params){
			Log.d("e", "NoteListResponse doInBackground");
			try {
				return API.getNotesList(params[0].getSessionID());
			} catch (ApiException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(NoteListResponse result){
			super.onPostExecute(result);

			if (result != null) {
				switch (result.getNoteCreate()) {
					case 0:
						if (result.getNoteArray() != null) {

							Cursor c = db.getReadableDatabase().query(DBHelper.DATABASE_NAME, myColumns, null, null, null, null, DBHelper.ID);

							noteAdapter.swapCursor(c);

						}
						break;
					case 1:
						if (result.getNoteArray() == null) {
							Toast toast1 = Toast.makeText(NoteActivity.this, "Ничего нет", Toast.LENGTH_LONG);
							toast1.setGravity(Gravity.BOTTOM, 10, 50);
							toast1.show();
						}
						break;
				}
			} else {
				Toast toast1 = Toast.makeText(NoteActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
				toast1.setGravity(Gravity.BOTTOM, 10, 50);
				toast1.show();
			}
		}
	}

	@Override
	protected void onResume(){
		updateNoteAdapter();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){

		switch (item.getItemId()) {
			case R.id.action_changePassword:
				Intent intentChangePassword = new Intent(this, ChengPasswordActivity.class);
				startActivity(intentChangePassword);
				return true;
			case R.id.action_logOut:
				Intent intentLogOut = new Intent(this, MainActivity.class);
				startActivity(intentLogOut);
				return true;
			case R.id.action_add:
				Intent intentAdd = new Intent(this, NewNoteActivity.class);
				startActivity(intentAdd);
				return true;
			case R.id.action_delete:
				Toast.makeText(this, "Привет", Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public class LogOut {

		private String	sessionID;

		LogOut(String sessionID) {
			this.sessionID = sessionID;
		}

		public String getSessionID(){
			return sessionID;
		}
	}

	public class MyAsyncTask extends AsyncTask<LogOut, Void, LogOutResponse> {

		ApiException	apiexception;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected LogOutResponse doInBackground(LogOut... params){

			try {
				return API.logOut(params[0].sessionID);
			} catch (ApiException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(LogOutResponse result){
			super.onPostExecute(result);

			if (result != null) {
				switch (result.getChengPasswordResponse()) {
					case 0:
						Intent intentLogOut = new Intent(NoteActivity.this, MainActivity.class);
						startActivity(intentLogOut);

						Toast toast = Toast.makeText(NoteActivity.this, "Успех", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.BOTTOM, 10, 50);
						toast.show();
						break;
					case 1:
						Toast toast1 = Toast.makeText(NoteActivity.this, "Не вышло", Toast.LENGTH_LONG);
						toast1.setGravity(Gravity.BOTTOM, 10, 50);
						toast1.show();
						break;
				}
			} else {
				Toast toast1 = Toast.makeText(NoteActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
				toast1.setGravity(Gravity.BOTTOM, 10, 50);
				toast1.show();
			}
		}
	}

	public void updateNoteAdapter(){

		noteAdapter.notifyDataSetChanged();

	}

	public class NotesListArrayAsyncTask extends AsyncTask<NotesList, Void, NoteListResponse> {

		ApiException	apiexception;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected NoteListResponse doInBackground(NotesList... params){

			try {
				return API.getNotesList(params[0].getSessionID());
			} catch (ApiException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(NoteListResponse result){
			super.onPostExecute(result);

			if (result != null) {
				switch (result.getNoteCreate()) {
					case 0:
						if (result.getNoteArray() != null) {
							ContentValues contentValues = new ContentValues();
							
							db.getWritableDatabase().replace(DBHelper.DATABASE_NAME, null, contentValues);
							
							Cursor c = db.getReadableDatabase().query(DBHelper.DATABASE_NAME, myColumns, null, null, null, null, DBHelper.ID);
							
							noteAdapter.swapCursor(c);
							if (noteAdapter != null) {
								updateNoteAdapter();
							}
						}
						break;
					case 1:
						if (result.getNoteArray() == null) {
							Toast toast1 = Toast.makeText(NoteActivity.this, "Ну", Toast.LENGTH_LONG);
							toast1.setGravity(Gravity.BOTTOM, 10, 50);
							toast1.show();
						}
						break;
				}
			} else {
				Toast toast1 = Toast.makeText(NoteActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
				toast1.setGravity(Gravity.BOTTOM, 10, 50);
				toast1.show();
			}
		}
	}

}
