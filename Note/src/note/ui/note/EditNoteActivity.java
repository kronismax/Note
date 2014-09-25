package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.EditNoteResponse;
import note.api.API.GetNoteResponse;
import note.api.ApiException;
import note.utils.UIUtils;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class EditNoteActivity extends Activity {

	protected EditText		editNote;
	protected String		title;

	private final String	LONG_EXTRA	= "ID";
	private final String	INT_EXTRA	= "POSITION";

	public class GetNote {

		private long	noteID;
		private String	sessionID;

		public GetNote(String _sessionID,long _noteID) {
			noteID = _noteID;
			sessionID = _sessionID;
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
		setContentView(R.layout.edit_note);
		editNote = (EditText) findViewById(R.id.editNote);

		new GetNoteAsyncTask().execute(new GetNote(((MyApplication) getApplication()).getLocalData().getSessionId(), getIntent().getLongExtra(LONG_EXTRA, -1)));

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
				Log.d("LONG", Long.toString(getIntent().getLongExtra(LONG_EXTRA, -1)));
				new EditNoteAsyncTask().execute(new EditNote(((MyApplication) getApplication()).getLocalData().getSessionId(), getIntent().getLongExtra(LONG_EXTRA, -1), editNote
						.getText().toString()));
				finish();
				break;

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

		ApiException	apiexception;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		API	API	= new API();

		@Override
		protected EditNoteResponse doInBackground(EditNote... params){

			try {
				((MyApplication) getApplication()).getLocalData().addLocalNoteForIndex(getActionBar().getTitle().toString(), params[0].text,
						getIntent().getLongExtra(LONG_EXTRA, -1), getIntent().getIntExtra(INT_EXTRA, -1));
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

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected GetNoteResponse doInBackground(GetNote... params){
			API API = new API();
			try {
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

						getActionBar().setTitle(result.getTitle());

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