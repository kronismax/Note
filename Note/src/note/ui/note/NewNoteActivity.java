package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.NewNoteResponse;
import note.api.ApiException;
import note.model.Note;
import note.model.database.MyContentProvider;
import note.model.database.NoteDatabaseColumns;
import note.model.database.NoteDatabaseColumns.TableNote;
import note.ui.note.NewNoteActivity.NoteCreate;
import note.utils.UIUtils;

import org.json.JSONException;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class NewNoteActivity extends Activity implements View.OnClickListener {

	protected EditText				textNote;
	protected EditText				titleNote;
	protected Button				randomNotes;
	protected Intent				intent;
	protected Note					note		= new Note();
	NoteAdapter						noteAdapter;
	// DBHelper						db;
	public Cursor c;
	public static final String[]	myColumns	= { NoteDatabaseColumns.TableNote._ID, 
													NoteDatabaseColumns.TableNote.TITLE, 
													NoteDatabaseColumns.TableNote.CONTENT };
	public String KEY_FOR_NOTE_CREATE = "KEY_FOR_NOTE_CREATE";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.new_note_activity);

		textNote = (EditText) findViewById(R.id.textNote);
		titleNote = (EditText) findViewById(R.id.titleNote);
		randomNotes = (Button) findViewById(R.id.button1);
		randomNotes.setOnClickListener(this);
		intent = getIntent();

		// DBHelper db = new DBHelper(this);

		//noteAdapter = new NoteAdapter(this, (db.getReadableDatabase().query(DBHelper.Tables.TABLE_NOTE, myContent, null, null, null, null, TableNote._ID)));
		noteAdapter = new NoteAdapter(this, c = (getContentResolver().query(MyContentProvider.URI_NOTE, myColumns, null, null, TableNote._ID)));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_note_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item){
		final String NOTE = textNote.getText().toString();
		final String TITLE = titleNote.getText().toString();

		switch (item.getItemId()) {
			case R.id.action_save_new_note:
				if (!TITLE.equals("")) {
					Bundle bundle = new Bundle();
					bundle.putParcelable(KEY_FOR_NOTE_CREATE, new NoteCreate(((MyApplication) getApplication()).getLocalData().getSessionId(), TITLE, NOTE));
	                getLoaderManager().initLoader(2, bundle, createNoteResponseLoaderCallbacks).forceLoad();
					return true;
				} else {
					Toast.makeText(this, "Введите заголовок", Toast.LENGTH_SHORT).show();
				}
			case android.R.id.home:
				onBackPressed();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public class NewNoteRequest {

		String sessionId;
		String title;
		String text;

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

	@Override
	public void onClick(View arg0){
		switch (arg0.getId()) {
			case R.id.button1:
				Bundle bundle = new Bundle();
				for (int i = 1; i < 6; i++) {
					bundle.putParcelable(KEY_FOR_NOTE_CREATE, new NoteCreate(((MyApplication) getApplication()).getLocalData().getSessionId(), "" + i, "generated"));
					getLoaderManager().initLoader(i, bundle, createNoteResponseLoaderCallbacks).forceLoad();
				}
				break;
		}
	}
	
	public LoaderManager.LoaderCallbacks<NewNoteResponse> createNoteResponseLoaderCallbacks = new LoaderManager.LoaderCallbacks<NewNoteResponse>() {
        
        @Override
        public Loader<NewNoteResponse> onCreateLoader(int id, Bundle args) {
            return new NoteCreateLoader(NewNoteActivity.this, (NoteCreate) args.getParcelable(KEY_FOR_NOTE_CREATE));
        }

        @Override
        public void onLoadFinished(Loader<NewNoteResponse> loader, NewNoteResponse data) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NoteDatabaseColumns.TableNote.TITLE, ((NoteCreateLoader)loader).noteCreate.title);
            contentValues.put(NoteDatabaseColumns.TableNote.CONTENT, ((NoteCreateLoader)loader).noteCreate.title);
            contentValues.put(NoteDatabaseColumns.TableNote._ID, data.getNoteID());
            getContentResolver().insert(MyContentProvider.URI_NOTE, contentValues);
            Intent intentLogOut = new Intent(NewNoteActivity.this, NoteActivity.class);
            intentLogOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentLogOut);
        }

        @Override
        public void onLoaderReset(Loader<NewNoteResponse> loader) {
        }
    };

    public static class NoteCreateLoader extends AsyncTaskLoader<NewNoteResponse> {

        public NoteCreate noteCreate;

        public NoteCreateLoader(Context context, NoteCreate noteCreate) {
            super(context);
            this.noteCreate = noteCreate;
        }


		@Override
		public NewNoteResponse loadInBackground() {
			try {
				return new API().newNote(noteCreate.sessionID, noteCreate.title, noteCreate.content);
			} catch (ApiException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
    
    public class NoteCreate implements Parcelable {
        public final Creator<NoteCreate> CREATOR = new Parcelable.Creator<NoteCreate>() {

            @Override
            public NoteCreate createFromParcel(Parcel source) {
                return new NoteCreate(source);
            }

            @Override
            public NoteCreate[] newArray(int size) {
                return new NoteCreate[size];
            }
        };
		private String sessionID;
		private String title;
		private String content;

        public NoteCreate(Parcel source) {
            sessionID = source.readString();
            title = source.readString();
            content = source.readString();
        }

        public NoteCreate(String sessionID, String title, String content) {
            this.sessionID = sessionID;
            this.title = title;
            this.content = content;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(sessionID);
            dest.writeString(title);
            dest.writeString(content);
        }
    }
	
}