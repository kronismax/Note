package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.EditNoteResponse;
import note.api.API.GetNoteResponse;
import note.api.ApiException;
import note.model.database.MyContentProvider;
import note.model.database.NoteDatabaseColumns;
import note.model.database.NoteDatabaseColumns.TableNote;
import note.ui.note.EditNoteActivity.EditNote;
import note.ui.note.EditNoteActivity.GetNote;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private final String			GET_NOTE_KEY	= "GET_NOTE_KEY";
	private final String 			EDIT_NOTE_KEY   = "EDIT_NOTE_KEY";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.edit_note);
		editNote = (EditText) findViewById(R.id.editNote);
		//DBHelper db = new DBHelper(this);
		//noteAdapter = new NoteAdapter(this, c = (db.getReadableDatabase().query(DBHelper.Tables.TABLE_NOTE, myContent, null, null, null, null, TableNote._ID)));
		noteAdapter = new NoteAdapter(this, c = (getContentResolver().query(MyContentProvider.URI_NOTE, myColumns, null, null, TableNote._ID)));
		
		Bundle getNoteBundle = new Bundle();
        GetNote getNote = new GetNote(((MyApplication) getApplication()).getLocalData().getSessionId(), getIntent().getLongExtra(LONG_EXTRA, -1));
        getNoteBundle.putParcelable(GET_NOTE_KEY, getNote);
        getLoaderManager().initLoader(1, getNoteBundle, getNoteResponseLoaderCallbacks).forceLoad();
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
				Bundle editNoteBundle = new Bundle();
                EditNote editNoteLoader = new EditNote(((MyApplication) getApplication()).getLocalData().getSessionId(), getIntent().getLongExtra(LONG_EXTRA, -1),
                        editNote.getText().toString());
                editNoteBundle.putParcelable(EDIT_NOTE_KEY, editNoteLoader);
                Log.d("actionEdit", "editNoteBundle" + editNoteBundle);
                getLoaderManager().initLoader(4, editNoteBundle, editNoteResponseLoaderCallbacks).forceLoad();
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

	public LoaderManager.LoaderCallbacks<GetNoteResponse> getNoteResponseLoaderCallbacks = new LoaderManager.LoaderCallbacks<GetNoteResponse>() {
		@Override
		public Loader<GetNoteResponse> onCreateLoader(int id, Bundle args) {
			return new GetNoteLoader(EditNoteActivity.this,(GetNote) args.getParcelable(GET_NOTE_KEY));
		}

		@Override
		public void onLoadFinished(Loader<GetNoteResponse> loader, GetNoteResponse data) {
			getActionBar().setTitle(data.getTitle());
			editNote.setText(data.getContent());
		}

		@Override
		public void onLoaderReset(Loader<GetNoteResponse> loader) {

		}
	};
	    
	    public LoaderManager.LoaderCallbacks<EditNoteResponse> editNoteResponseLoaderCallbacks = new LoaderManager.LoaderCallbacks<EditNoteResponse>() {

	        @Override
	        public Loader<EditNoteResponse> onCreateLoader(int id, Bundle args) {
	            return new EditNoteLoader(EditNoteActivity.this, (EditNote) args.getParcelable(EDIT_NOTE_KEY));
	        }

	        @Override
	        public void onLoadFinished(Loader<EditNoteResponse> loader, EditNoteResponse data) {

	            ContentValues contentValues = new ContentValues();
	            contentValues.put(NoteDatabaseColumns.TableNote._ID, ((EditNoteLoader) loader).editNote.noteID);
	            contentValues.put(NoteDatabaseColumns.TableNote.CONTENT, ((EditNoteLoader) loader).editNote.text);
	            
	            getContentResolver().update(MyContentProvider.URI_NOTE, contentValues, NoteDatabaseColumns.TableNote._ID + " = " + ((EditNoteLoader) loader).editNote.noteID, null);
	            
	            Toast toast = Toast.makeText(EditNoteActivity.this, "Успех", Toast.LENGTH_LONG);
	            toast.setGravity(Gravity.BOTTOM, 10, 50);
	            toast.show();
	        }

	        @Override
	        public void onLoaderReset(Loader<EditNoteResponse> loader) {
	        }
	    };
	    
	    public static class GetNoteLoader extends AsyncTaskLoader<API.GetNoteResponse>{
	        public GetNote getNote;

	        public GetNoteLoader(Context context, GetNote getNote) {
	            super(context);
	            this.getNote = getNote;
		}

		@Override
		public API.GetNoteResponse loadInBackground(){
			try {
				return new API().getNote(getNote.getSessionID(), getNote.getNoteID());
			} catch (ApiException apIexception) {
				apIexception.printStackTrace();
			}
			return null;
		}
	}

	public static class EditNoteLoader extends AsyncTaskLoader<EditNoteResponse> {

		public EditNote	editNote;

		public EditNoteLoader(Context context, EditNote editNote) {
			super(context);

			this.editNote = editNote;
		}

		@Override
		public EditNoteResponse loadInBackground(){
			try {
				return new API().getEditNote(editNote.sessionID, editNote.noteID, editNote.text);
			} catch (ApiException apIexception) {
				apIexception.printStackTrace();
			}
			return null;
		}
	}

    public class GetNote implements Parcelable {
        public final Creator<GetNote> CREATOR = new Parcelable.Creator<GetNote>() {

            @Override
            public GetNote createFromParcel(Parcel source) {
                return new GetNote(source);
            }

            @Override
            public GetNote[] newArray(int size) {
                return new GetNote[size];
            }
        };
        private long noteID;
        private String sessionID;

        public GetNote(String sessionID, long noteID) {
        	this.noteID = noteID;
        	this.sessionID = sessionID;
        }

        public GetNote(Parcel source) {
            source.writeLong(noteID);
            source.writeString(sessionID);
        }

        public long getNoteID() {
            return noteID;
        }

        public String getSessionID() {
            return sessionID;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(noteID);
            dest.writeString(sessionID);
        }
    }

    public class EditNote implements Parcelable {
        public final Creator<EditNote> CREATOR = new Parcelable.Creator<EditNote>() {

            @Override
            public EditNote createFromParcel(Parcel source) {
                return new EditNote(source);
            }

            @Override
            public EditNote[] newArray(int size) {
                return new EditNote[size];
            }
        };
        private long noteID;
        private String sessionID;
        private String text;

        public EditNote(String sessionID, long noteID, String text) {
            this.noteID = noteID;
            this.sessionID = sessionID;
            this.text = text;
        }

        public EditNote(Parcel source) {
            source.writeLong(noteID);
            source.writeString(sessionID);
            source.writeString(text);
        }

        public long getNoteID() {
            return noteID;
        }

        public String getSessionID() {
            return sessionID;
        }

        public String getText() {
            return text;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(noteID);
            dest.writeString(sessionID);
            dest.writeString(text);
        }
    }
	
}