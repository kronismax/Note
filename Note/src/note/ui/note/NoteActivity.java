package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.DeleteNoteResponse;
import note.api.API.LogOutResponse;
import note.api.API.NoteListResponse;
import note.api.ApiException;
import note.model.database.MyContentProvider;
import note.model.database.NoteDatabaseColumns;
import note.model.database.NoteDatabaseColumns.TableNote;
import note.ui.login.MainActivity;
import note.utils.UIUtils;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.note.R;

public class NoteActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

	String		LOGIN;
	NoteAdapter	noteAdapter;
	Button		buttonDelete;
	ListView	lv;
	static API			API			= new API();
	// DBHelper	db;
	String[]	myColumns	= { NoteDatabaseColumns.TableNote._ID, 
								NoteDatabaseColumns.TableNote.TITLE, 
								NoteDatabaseColumns.TableNote.CONTENT };
	AlertDialog.Builder		ad;
	Context					context;
	private final String	KEY_FOR_BUNDLE	= "KEY_FOR_BUNDLE";

    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_activity);
		Bundle bundle = new Bundle();
		bundle.putString(KEY_FOR_BUNDLE, (((MyApplication) getApplication()).getLocalData().getSessionId()));
		//new NotesListArrayAsyncTask().execute(new NotesList(((MyApplication) getApplication()).getLocalData().getSessionId()));
		getLoaderManager().initLoader(1, bundle, this);
		getLoaderManager().initLoader(2, bundle, notesListResponseLoaderCallbacks).forceLoad();
		//db = new DBHelper(this);
		noteAdapter = new NoteAdapter(this, getContentResolver().query(MyContentProvider.URI_NOTE, myColumns, null, null, TableNote._ID));
		//adapter = new SimpleCursorAdapter(this, R.id.list, null, myColumns, to);
		//getLoaderManager().initLoader(1, null, this);
		
		lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(noteAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id){
				Intent intent = new Intent(NoteActivity.this, EditNoteActivity.class);
				intent.putExtra("POSITION", position);
				intent.putExtra("ID", id);
				startActivity(intent);
			}
		});

		noteAdapter.setOnDeleteClickListener(new NoteAdapter.OnDeleteItemListner() {

			@Override
			public void onItemDeleteClick(final long id){
				context = NoteActivity.this;
				String title = "                    Уверен";
				String button1String = "Да";
				String button2String = "Отмена";

				ad = new AlertDialog.Builder(context);
				ad.setTitle(title);
				ad.setNegativeButton(button1String, new OnClickListener() {

					public void onClick(DialogInterface dialog, int arg1){
						if (true) {
							new DeleteNoteAsyncTask().execute(new DeleteNoteRequest(((MyApplication) getApplication()).getLocalData().getSessionId(), id));
							//new NotesListArrayAsyncTask().execute(new NotesList(((MyApplication) getApplication()).getLocalData().getSessionId()));
						}
					}
				});
				ad.setPositiveButton(button2String, new OnClickListener() {

					public void onClick(DialogInterface dialog, int arg1){
					}
				});
				ad.setCancelable(false);
				ad.show();
			}
		});
	}

	public class NotesList {

		private String	sessionID;

		NotesList(String sessionID) {
			this.sessionID = sessionID;
		}

		public String getSessionID(){
			return sessionID;
		}
	}

	@Override
	protected void onResume(){
		// updateNoteAdapter();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){

		switch (item.getItemId()) {
			case R.id.action_changePassword:
				Intent intentChangePassword = new Intent(this, ChengPasswordActivity.class);
				startActivity(intentChangePassword);
				return true;
			case R.id.action_logOut:
				API = new API();
				new MyAsyncTask().execute(new LogOut(((MyApplication) getApplication()).getLocalData().getSessionId()));
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

	public class LogOut {

		private String	sessionID;

		LogOut(String sessionID) {
			this.sessionID = sessionID;
		}

		public String getSessionID(){
			return sessionID;
		}
	}

	public class MyAsyncTask extends AsyncTask<LogOut, Void, LogOutResponse> {

		ApiException	apiexception;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected LogOutResponse doInBackground(LogOut... params){

			try {
				return API.logOut(params[0].sessionID);
			} catch (ApiException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(LogOutResponse result){
			super.onPostExecute(result);

			if (result != null) {
				switch (result.getChengPasswordResponse()) {
					case 0:
						Intent intentLogOut = new Intent(NoteActivity.this, MainActivity.class);
						startActivity(intentLogOut);

						Toast toast = Toast.makeText(NoteActivity.this, "  Успех", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.BOTTOM, 10, 50);
						toast.show();
						break;
					case 1:
						Toast toast1 = Toast.makeText(NoteActivity.this, "Не вышло", Toast.LENGTH_LONG);
						toast1.setGravity(Gravity.BOTTOM, 10, 50);
						toast1.show();
						break;
				}
			} else {
				Toast toast1 = Toast.makeText(NoteActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
				toast1.setGravity(Gravity.BOTTOM, 10, 50);
				toast1.show();
			}
		}
	}

	public void updateNoteAdapter(){
		if (noteAdapter != null) {
			//DBHelper db = new DBHelper(this);
			//Cursor c = getContentResolver().query(MyContentProvider.URI_NOTE, myColumns, null, null, TableNote._ID);
			//noteAdapter.swapCursor(c);
		}
	}

//	public class NotesListArrayAsyncTask extends AsyncTask<NotesList, Void, NoteListResponse> {
//
//		ApiException	apiexception;
//
//		@Override
//		protected void onPreExecute(){
//			super.onPreExecute();
//		}
//
//		@Override
//		protected NoteListResponse doInBackground(NotesList... params){
//
//			try {
//				return API.getNotesList(params[0].getSessionID());
//			} catch (ApiException e) {
//				apiexception = e;
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(NoteListResponse result){
//			super.onPostExecute(result);
//
//			if (result != null) {
//				switch (result.getNoteCreate()) {
//					case 0:
//						if (result.getNoteArray() != null) {
//							//ContentValues contentValues = new ContentValues();
//							//for (Note item : result.getNoteArray()) {
//							//contentValues.put(NoteDatabaseColumns.TableNote.TITLE, item.getTitle());
//							//contentValues.put(NoteDatabaseColumns.TableNote.CONTENT, item.getDescription());
//							//contentValues.put(NoteDatabaseColumns.TableNote._ID, item.getId());
//							//db.getWritableDatabase().replace(DBHelper.Tables.TABLE_NOTE, null, contentValues);
//							//getContentResolver().insert(MyContentProvider.URI_NOTE, contentValues);
//							//Cursor c = db.getReadableDatabase().query(DBHelper.Tables.TABLE_NOTE, myColumns, null, null, null, null, TableNote._ID);
//							ContentValues[] contentValues = new ContentValues[result.getNoteArray().size()];
//							for (int i = 0; i < contentValues.length; i++) {
//								contentValues[i] = new ContentValues();
//								contentValues[i].put(NoteDatabaseColumns.TableNote._ID, result.getNoteArray().get(i).getId());
//								contentValues[i].put(NoteDatabaseColumns.TableNote.TITLE, result.getNoteArray().get(i).getTitle());
//								contentValues[i].put(NoteDatabaseColumns.TableNote.CONTENT, result.getNoteArray().get(i).getDescription());
//
//							}
//							getContentResolver().bulkInsert(MyContentProvider.URI_NOTE, contentValues);
//							//Cursor c = getContentResolver().query(MyContentProvider.URI_NOTE, myColumns, null, null, TableNote._ID);
//							//noteAdapter.swapCursor(c);
//							//updateNoteAdapter();
//						}
//						break;
//					case 1:
//						if (result.getNoteArray() == null) {
//							Toast toast1 = Toast.makeText(NoteActivity.this, "Ну", Toast.LENGTH_LONG);
//							toast1.setGravity(Gravity.BOTTOM, 10, 50);
//							toast1.show();
//						}
//						break;
//				}
//			} else {
//				Toast toast1 = Toast.makeText(NoteActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
//				toast1.setGravity(Gravity.BOTTOM, 10, 50);
//				toast1.show();
//			}
//		}
//	}

	public class DeleteNoteRequest {

		private String	sessionId;
		private long	noteId;

		DeleteNoteRequest(String sessionId,long noteId) {
			this.sessionId = sessionId;
			this.noteId = noteId;
		}

		public String getSessionId(){
			return sessionId;
		}

		public long getNoteId(){
			return noteId;
		}

	}

	public class DeleteNoteAsyncTask extends AsyncTask<DeleteNoteRequest, Void, DeleteNoteResponse> {

		ApiException		apiexception;
		DeleteNoteRequest	deleteNoteId;

		// DBHelper db = new DBHelper(NoteActivity.this);

		@Override
		protected DeleteNoteResponse doInBackground(DeleteNoteRequest... params){
			try {
				deleteNoteId = params[0];
				Log.d("Note id doIn", ":" + deleteNoteId);
				return API.deleteNote(params[0].sessionId, params[0].noteId);
			} catch (ApiException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(DeleteNoteResponse result){
			super.onPostExecute(result);

			if (result != null) {
				switch (result.result) {
					case 0:
						// DBHelper db = new DBHelper(NoteActivity.this);
						// db.getWritableDatabase().delete(Tables.TABLE_NOTE,
						// NoteDatabaseColumns.TableNote._ID + " = ?", new
						// String[] { String.valueOf(deleteNoteId) });
						// db.close();
						getContentResolver().delete(MyContentProvider.URI_NOTE, NoteDatabaseColumns.TableNote._ID + " = " + deleteNoteId.getNoteId(), null);
						updateNoteAdapter();
						break;
					case 2:
						Toast toast = Toast.makeText(NoteActivity.this, "Что то не так", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.BOTTOM, 10, 50);
						toast.show();
						break;
					default:
						Toast toast1 = Toast.makeText(NoteActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
						toast1.setGravity(Gravity.BOTTOM, 10, 50);
						toast1.show();
						break;
				}
			} else {
				UIUtils.showToastByException(NoteActivity.this, apiexception);
			}
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
		CursorLoader cursorLoader = new CursorLoader(this, MyContentProvider.URI_NOTE, myColumns, null, null, null);
		Log.d("onCreateLoader", "WORK");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor){
		Log.d("onLoadFinished", "WORK");
		noteAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0){
		noteAdapter.swapCursor(null);
	}

	public static class NoteListAsyncTaskLoader extends AsyncTaskLoader<API.NoteListResponse> {

		  // We hold a reference to the Loader’s data here.
		private API.NoteListResponse	mData;
		private String				sessionID;
		
		public NoteListAsyncTaskLoader(Context ctx, String ID) {
		    // Loaders may be used across multiple Activitys (assuming they aren't
		    // bound to the LoaderManager), so NEVER hold a reference to the context
		    // directly. Doing so will cause you to leak an entire Activity's context.
		    // The superclass constructor will store a reference to the Application
		    // Context instead, and can be retrieved with a call to getContext().
			super(ctx);
			sessionID = ID;
		}

		  /****************************************************/
		  /** (1) A task that performs the asynchronous load **/
		  /****************************************************/

		@Override
		public API.NoteListResponse loadInBackground(){
		    // This method is called on a background thread and should generate a
		    // new set of data to be delivered back to the client.
			getContext().getContentResolver().delete(MyContentProvider.URI_NOTE, null, null);
			try {
				
				return API.getNotesList(sessionID);
			} catch (ApiException apIexception) {
				apIexception.printStackTrace();
			}
			return null;
		}
	}
	
	public LoaderManager.LoaderCallbacks<NoteListResponse>	notesListResponseLoaderCallbacks	= new LoaderManager.LoaderCallbacks<NoteListResponse>() {

    	@Override
    	public Loader<NoteListResponse> onCreateLoader(int id, Bundle args) {
    		Log.d("Loader", "0000");
    		return new NoteListAsyncTaskLoader(NoteActivity.this, args.getString(KEY_FOR_BUNDLE));
    	}
    	@Override
    	public void onLoadFinished(Loader<NoteListResponse> loader, NoteListResponse data) {
    		if (data.getNoteArray() != null) {
    			ContentValues[] contentValues = new ContentValues[data.getNoteArray().size()];
				for (int i = 0; i < contentValues.length; i++) {
					contentValues[i] = new ContentValues();
					contentValues[i].put(NoteDatabaseColumns.TableNote._ID, data.getNoteArray().get(i).noteID);
					contentValues[i].put(NoteDatabaseColumns.TableNote.TITLE, data.getNoteArray().get(i).title);
					contentValues[i].put(NoteDatabaseColumns.TableNote.CONTENT, data.getNoteArray().get(i).shortContent);

				}
				getContentResolver().bulkInsert(MyContentProvider.URI_NOTE, contentValues);
    		}
    		getLoaderManager().destroyLoader(2);
    	}
    	@Override
    	public void onLoaderReset(Loader<NoteListResponse> loader) {
    	}
    };
	
}