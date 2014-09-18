package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.GetNoteResponse;
import note.api.ApiException;
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

public class EditNote extends Activity {

	protected EditText	editNote;
	protected String	title;

	public class GetNote {

		private String	noteID;
		private String	sessionID;

		public GetNote(String noteID,String sessionID) {
			this.noteID = noteID;
			this.sessionID = sessionID;
		}

		public String getNoteID(){
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
		new MyAsyncTask().execute(new GetNote(((MyApplication) getApplication()).getLocalData().getNoteID(), ((MyApplication) getApplication()).getLocalData().getSessionId()));
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
				Intent intent = new Intent(this, NoteActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	};

	public class MyAsyncTask extends AsyncTask<GetNote, Void, GetNoteResponse> {

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
			if (result != null) {
				switch (result.getGetNote()) {
					case 0:
						getActionBar().setTitle(result.getTitle());

						editNote.setText(result.getContent());

						Toast toast = Toast.makeText(EditNote.this, "Успех", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.BOTTOM, 10, 50);
						toast.show();
						break;
					case 2:
						Toast toast1 = Toast.makeText(EditNote.this, "Что то не так", Toast.LENGTH_LONG);
						toast1.setGravity(Gravity.BOTTOM, 10, 50);
						toast1.show();
						break;
				}
			} else {
				Toast toast1 = Toast.makeText(EditNote.this, "Эксэпшн", Toast.LENGTH_LONG);
				toast1.setGravity(Gravity.BOTTOM, 10, 50);
				toast1.show();
			}
		}
	}
}