package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.model.Note;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class NewNoteActivity extends Activity {
	protected API API;
	protected EditText textNote;
	protected EditText titleNote;
	protected Intent intent;
	protected Note note = new Note();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_note_activity);
		API = new API();

		textNote = (EditText) findViewById(R.id.textNote);
		titleNote = (EditText) findViewById(R.id.titleNote);

		intent = getIntent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_note_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		final String NOTE 			 = textNote.getText().toString();
		final String NOTE_TITLE_NOTE = titleNote.getText().toString();

		switch (item.getItemId()) {
		case R.id.action_save_new_note:
			if (!NOTE_TITLE_NOTE.equals("")) {
				note.setDescription(NOTE);
				note.setTitle(NOTE_TITLE_NOTE);
				((MyApplication) getApplication()).getLocalData().mNotes.add(note);
				API.putNote(((MyApplication) getApplication()).getLocalData().sessionId,NOTE, NOTE_TITLE_NOTE);
				intent = new Intent(this, NoteActivity.class);
				startActivity(intent);
				return true;
			} else {
				Toast.makeText(this, "Введите заголовок", Toast.LENGTH_SHORT).show();
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
