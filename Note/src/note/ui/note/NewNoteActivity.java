package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.NewNoteResponse;
import note.api.ApiException;
import note.model.Note;
import note.model.DataBase.DBHelper;
import note.model.DataBase.NoteDatabaseColumns;
import note.model.DataBase.NoteDatabaseColumns.TableNote;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class NewNoteActivity extends Activity {

	protected API					API;
	protected EditText				textNote;
	protected EditText				titleNote;
	protected Intent				intent;
	protected Note					note		= new Note();
	NoteAdapter						noteAdapter;
	DBHelper						db;
	public static final String[]	myContent	= { NoteDatabaseColumns.TableNote._ID, 
													NoteDatabaseColumns.TableNote.TITLE, 
													NoteDatabaseColumns.TableNote.CONTENT };

	

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_note_activity);
		API = new API();

		textNote = (EditText) findViewById(R.id.textNote);
		titleNote = (EditText) findViewById(R.id.titleNote);

		intent = getIntent();

		DBHelper db = new DBHelper(this);

		noteAdapter = new NoteAdapter(this, (db.getReadableDatabase().query(DBHelper.Tables.TABLE_NOTE, myContent, null, null, null, null, TableNote._ID)));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_note_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item){
		final String NOTE = textNote.getText().toString();
		final String NOTE_TITLE_NOTE = titleNote.getText().toString();

		switch (item.getItemId()) {
			case R.id.action_save_new_note:
				if (!NOTE_TITLE_NOTE.equals("")) {
					new MyAsyncTask().execute(new NewNoteRequest(((MyApplication) getApplication()).getLocalData().getSessionId(), NOTE_TITLE_NOTE, NOTE));
					return true;
				} else {
					Toast.makeText(this, "Введите заголовок", Toast.LENGTH_SHORT).show();
				}
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public class NewNoteRequest {

		String	sessionId;
		String	title;
		String	text;

		public NewNoteRequest(String id,String title,String text) {
			sessionId = id;
			this.title = title;
			this.text = text;
		}

		public String getTitile(){
			return title;
		}

		public String getContent(){
			return text;
		}

		public String getSessionID(){
			return sessionId;
		}

	}

	public class MyAsyncTask extends AsyncTask<NewNoteRequest, Void, NewNoteResponse> {

		ApiException	exception;
		NewNoteRequest	request;

		@Override
		protected NewNoteResponse doInBackground(NewNoteRequest... params){
			try {
				request = params[0];
				return new API().newNote(params[0].sessionId, params[0].title, params[0].text);
			} catch (ApiException apIexception) {
				exception = apIexception;
			}
			return null;
		}

		protected void onPostExecute(NewNoteResponse result){
			super.onPostExecute(result);

			if (result == null) {
				UIUtils.showToastByException(NewNoteActivity.this, exception);
			} else {
				if (result.getResult() == 0) {

					DBHelper db = new DBHelper(NewNoteActivity.this);
					ContentValues cv = new ContentValues();
					cv.put(NoteDatabaseColumns.TableNote.TITLE, request.getTitile());
					cv.put(NoteDatabaseColumns.TableNote.CONTENT, request.getContent());
					cv.put(NoteDatabaseColumns.TableNote._ID, request.getSessionID());
					db.getWritableDatabase().replace(DBHelper.Tables.TABLE_NOTE, null, cv);
					noteAdapter.notifyDataSetChanged();

					Toast.makeText(NewNoteActivity.this, "Красава", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(NewNoteActivity.this, NoteActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);

				} else if (result.getResult() == 1) {
					Toast toast = Toast.makeText(NewNoteActivity.this, "Неправильная сессия", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
				} else {
					Toast toast = Toast.makeText(NewNoteActivity.this, "Попробуйте позже", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
				}
			}
		}
	}

}
