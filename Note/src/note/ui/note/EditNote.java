package note.ui.note;

import note.MyApplication;
import note.model.Note;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.note.R;

public class EditNote extends Activity {
	public Intent intent;
	public EditText editNote;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_note);
		editNote = (EditText) findViewById(R.id.editNote);
		intent = getIntent();
		editNote.setText(((MyApplication) getApplication()).getLocalData().mNotes
				.get(intent.getIntExtra("NoteID", -1)).getText());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_note_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final String DESCRIPTION = editNote.getText().toString();
		switch (item.getItemId()) {
		case R.id.action_edit_note:
			((MyApplication) getApplication()).getLocalData().mNotes.set(intent
					.getIntExtra("NoteID", -1), new Note(
					((MyApplication) getApplication()).getLocalData().mNotes
							.get(intent.getIntExtra("NoteID", -1)).getTitle(),
					DESCRIPTION));
			intent = new Intent(this, NoteActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}