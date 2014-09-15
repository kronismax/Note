package note.ui.note;

import note.MyApplication;
import note.ui.login.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	String LOGIN;
	NoteAdapter noteAdapter;
	Button buttonDelete;
	ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_activity);

		noteAdapter = new NoteAdapter(this,
				((MyApplication) getApplication()).getLocalData().mNotes);

		// buttonDelete = (Button) findViewById(R.id.buttonDelete);

		lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(noteAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				Intent intent = new Intent(NoteActivity.this, EditNote.class);
				intent.putExtra("NoteID", position);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		noteAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_changePassword:
			Intent intentChangePassword = new Intent(this,
					ChengPasswordActivity.class);
			startActivity(intentChangePassword);
			return true;
		case R.id.action_logOut:
			Intent intentLogOut = new Intent(this, MainActivity.class);
			startActivity(intentLogOut);
			Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show();
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

}
