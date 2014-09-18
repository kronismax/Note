package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.NewNoteResponse;
import note.api.ApiException;
import note.api.API.LoginResponse;
import note.model.Note;
import note.ui.login.LoginFragment.LoginRequest;
import note.ui.login.LoginFragment.MyAsyncTask;
import note.utils.UIUtils;
import android.app.Activity;
import android.content.Intent;
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

	protected API		API;
	protected EditText	textNote;
	protected EditText	titleNote;
	protected Intent	intent;
	protected Note		note	= new Note();

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_note_activity);
		API = new API();

		textNote = (EditText) findViewById(R.id.textNote);
		titleNote = (EditText) findViewById(R.id.titleNote);

		intent = getIntent();
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
					note.setDescription(NOTE);
					note.setTitle(NOTE_TITLE_NOTE);
					((MyApplication) getApplication()).getLocalData().mNotes.add(note);
					API.putNote(((MyApplication) getApplication()).getLocalData().getSessionId(), NOTE, NOTE_TITLE_NOTE);
					MyAsyncTask mt;
					mt = new MyAsyncTask();
					mt.execute(new NewNoteRequest(((MyApplication) getApplication()).getLocalData().getSessionId(), NOTE_TITLE_NOTE, NOTE));
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
	}

	public class MyAsyncTask extends AsyncTask<NewNoteRequest, Void, NewNoteResponse> {

		ApiException	exception;

		@Override
		protected NewNoteResponse doInBackground(NewNoteRequest... params){
			try {
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
