package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.EditNoteResponse;
import note.api.API.GetNoteResponse;
import note.api.ApiException;
import note.model.database.MyContentProvider;
import note.model.database.NoteDatabaseColumns;
import note.model.database.NoteDatabaseColumns.TableNote;
import note.utils.UIUtils;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class EditNoteActivity extends Activity {

	public static final String[]	myColumns		= { NoteDatabaseColumns.TableNote._ID, NoteDatabaseColumns.TableNote.TITLE, NoteDatabaseColumns.TableNote.CONTENT };

	protected EditText				editNote;
	protected String				title;
	private final String			LONG_EXTRA		= "ID";
	private final String			INT_EXTRA		= "POSITION";
	protected NoteAdapter			noteAdapter;
	ContentValues					contentValues	= new ContentValues();
	Cursor							c;

	public class GetNote {

		private long	noteID;
		private String	sessionID;

		public GetNote(String sessionID,long noteID) {
			this.noteID = noteID;
			this.sessionID = sessionID;
		}

		public long getNoteID(){
			return noteID;
		}

		public String getSessionID(){
			return sessionID;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.edit_note);
		editNote = (EditText) findViewById(R.id.editNote);
		//DBHelper db = new DBHelper(this);
		//noteAdapter = new NoteAdapter(this, c = (db.getReadableDatabase().query(DBHelper.Tables.TABLE_NOTE, myContent, null, null, null, null, TableNote._ID)));
		noteAdapter = new NoteAdapter(this, c = (getContentResolver().query(MyContentProvider.URI_NOTE, myColumns, null, null, TableNote._ID)));
		
		new GetNoteAsyncTask().execute(new GetNote(((MyApplication) getApplication()).getLocalData().getSessionId(), getIntent().getLongExtra(LONG_EXTRA, -1)));
		editNote.requestFocus();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_note_menu, menu);
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
			case R.id.action_edit_note:
				new EditNoteAsyncTask().execute(new EditNote(((MyApplication) getApplication()).getLocalData().getSessionId(), getIntent().getLongExtra(LONG_EXTRA, -1), editNote
						.getText().toString()));
				Intent intent = new Intent(EditNoteActivity.this, NoteActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				break;
			case android.R.id.home:
				onBackPressed();
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	};

	public class EditNote {

		private long	noteID;
		private String	sessionID;
		private String	text;

		public EditNote(String sessionID,long noteID,String text) {
			this.noteID = noteID;
			this.sessionID = sessionID;
			this.text = text;
		}

		public long getNoteID(){
			return noteID;
		}

		public String getSessionID(){
			return sessionID;
		}

		public String getText(){
			return text;
		}
	}

	public class EditNoteAsyncTask extends AsyncTask<EditNote, Void, EditNoteResponse> {

		//DBHelper		db	= new DBHelper(EditNoteActivity.this);
		ApiException	apiexception;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		API	API	= new API();

		@Override
		protected EditNoteResponse doInBackground(EditNote... params){

			try {
				contentValues.put(NoteDatabaseColumns.TableNote._ID, params[0].getNoteID());
				contentValues.put(NoteDatabaseColumns.TableNote.TITLE, params[0].text);

				//db.getWritableDatabase().replace(DBHelper.Tables.TABLE_NOTE, null, contentValues);
				//c = db.getReadableDatabase().query(DBHelper.Tables.TABLE_NOTE, myColumns, null, null, null, null, TableNote._ID);
				
				getContentResolver().update(MyContentProvider.URI_NOTE, contentValues, NoteDatabaseColumns.TableNote._ID + "=" + params[0].getNoteID(), null);
				Cursor c = getContentResolver().query(MyContentProvider.URI_NOTE, myColumns, null, null, TableNote._ID);
				noteAdapter.swapCursor(c);

				noteAdapter.swapCursor(c);
				noteAdapter.notifyDataSetChanged();
				return API.getEditNote(params[0].sessionID, params[0].noteID, params[0].text);
			} catch (ApiException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(EditNoteResponse result){
			super.onPostExecute(result);

			if (result == null) {
				UIUtils.showToastByException(EditNoteActivity.this, apiexception);
			} else {
				switch (result.getEditNoteResponse()) {
					case 0:
						noteAdapter.swapCursor(c);
						noteAdapter.notifyDataSetChanged();
						Toast toast = Toast.makeText(EditNoteActivity.this, "Красава", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.BOTTOM, 10, 50);
						toast.show();
						break;
					case 2:
						Toast toast1 = Toast.makeText(EditNoteActivity.this, "Что то не так", Toast.LENGTH_LONG);
						toast1.setGravity(Gravity.BOTTOM, 10, 50);
						toast1.show();
						break;
					default:
						Toast toast2 = Toast.makeText(EditNoteActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
						toast2.setGravity(Gravity.BOTTOM, 10, 50);
						toast2.show();
						break;
				}
			}
		}
	}

	public class GetNoteAsyncTask extends AsyncTask<GetNote, Void, GetNoteResponse> {

		ApiException	apiexception;
		GetNote			request;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected GetNoteResponse doInBackground(GetNote... params){
			API API = new API();
			try {
				request = params[0];
				return API.getNote(params[0].sessionID, params[0].noteID);
			} catch (ApiException e) {
				apiexception = e;

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(GetNoteResponse result){
			super.onPostExecute(result);

			if (result == null) {
				UIUtils.showToastByException(EditNoteActivity.this, apiexception);
			} else {
				switch (result.getGetNote()) {
					case 0:
						getActionBar().setTitle("Ред. заметки " + result.getTitle());
						editNote.setText(result.getContent());
						break;
					case 2:
						Toast toast1 = Toast.makeText(EditNoteActivity.this, "Что то не так", Toast.LENGTH_LONG);
						toast1.setGravity(Gravity.BOTTOM, 10, 50);
						toast1.show();

						break;
					default:
						Toast toast2 = Toast.makeText(EditNoteActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
						toast2.setGravity(Gravity.BOTTOM, 10, 50);
						toast2.show();
						break;
				}
			}
		}
	}
}