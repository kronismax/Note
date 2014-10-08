package note.model.DataBase;

import java.util.ArrayList;

import note.model.DataBase.DBHelper.Tables;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
	 
    private DBHelper db;
 
    // All URIs share these parts
    public static final String AUTHORITY = "com.android.myapp";
    public static final String SCHEME = "content://";
 
    // URIs
    public static final Uri URI_A = Uri.parse(SCHEME + AUTHORITY + "/" + Tables.TABLE_NOTE); // FIXME use buildUpon method here

	// UriMatcher
	private enum QueryId {
		NONE,

		QUERY_Note,
	};

	static {
		addURI(Tables.TABLE_NOTE, QueryId.QUERY_Note);
	}

	private static final UriMatcher	sURIMatcher	= new UriMatcher(UriMatcher.NO_MATCH);

	private static void addURI(String uri, QueryId query){
		sURIMatcher.addURI(AUTHORITY, uri, query.ordinal());
	}

	private static QueryId matchQuery(Uri uri){
		int id = sURIMatcher.match(uri);
		return id == -1 ? QueryId.NONE : QueryId.values()[id];
	}

	// init

	@Override
	public boolean onCreate(){
		db = new DBHelper(getContext());
		return true;
	}

	// operations

	private SelectionBuilder buildSimpleSelection(Uri uri){
		final SelectionBuilder builder = new SelectionBuilder();
		// FIXME can be reworked with method in enum
		switch (matchQuery(uri)) {
			case QUERY_Note:
				return builder.table(Tables.TABLE_NOTE);

				// case QUERY_B:
				// return builder.table(Tables.TABLE_B);

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);

		}
	}

	private void notifyURI(Uri uri){
		getContext().getContentResolver().notifyChange(uri, null);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		Cursor result;
		SQLiteDatabase SQLdb = db.getReadableDatabase();

		switch (matchQuery(uri)) {
			default: {
				final SelectionBuilder builder = buildSimpleSelection(uri);
				result = builder.where(selection, selectionArgs).query(SQLdb, projection, sortOrder);
			}
		}

		result.setNotificationUri(getContext().getContentResolver(), uri);
		return result;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values){
		Uri result;
		SQLiteDatabase SQLdb = db.getWritableDatabase();
		long id = 0;
		// FIXME can be reworked with method in enum
		switch (matchQuery(uri)) {
			case QUERY_Note:
				id = db.insertOrThrow(Tables.TABLE_NOTE, null, values);
				// FIXME use buildUpon method here
				result = Uri.parse(URI_A + "/" + id);
				break;

			// case QUERY_B:
			// id = db.insertOrThrow(Tables.TABLE_B, null, values);
			// // FIXME use buildUpon method here
			// result = Uri.parse(URI_B + "/" + id);
			// break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs){
		SQLiteDatabase SQLdb = db.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int rowsDeleted = builder.where(selection, selectionArgs).delete(db);

		if (rowsDeleted > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
		final SQLiteDatabase SQLdb = db.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int rowsUpdated = builder.where(selection, selectionArgs).update(db, values);

		if (rowsUpdated > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsUpdated;
	}

	@Override
	public String getType(Uri uri){
		throw new UnsupportedOperationException("Not implemented");
	}

	// bulk insert operation

	private String simpleGetTable(Uri uri){
		// FIXME can be reworked with method in enum
		switch (matchQuery(uri)) {
			case QUERY_Note:
				return Tables.TABLE_NOTE;

				// case QUERY_B:
				// return Tables.TABLE_B;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public final int bulkInsert(Uri url, ContentValues[] values){
		int result = 0;
		String table = simpleGetTable(url);
		final SQLiteDatabase SQLdb = db.getWritableDatabase();

		SQLdb.beginTransaction();
		try {
			for (ContentValues data : values) {
				try {
					SQLdb.insertWithOnConflict(table, null, data, SQLiteDatabase.CONFLICT_REPLACE);
					result++;
				} catch (SQLiteConstraintException e) {
					throw e;
				}
			}
			SQLdb.setTransactionSuccessful();
		} finally {
			SQLdb.endTransaction();
		}

		getContext().getContentResolver().notifyChange(url, null);
		return result;
	}

	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException{
		final SQLiteDatabase SQLdb = db.getWritableDatabase();
		SQLdb.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			SQLdb.setTransactionSuccessful();
			return results;
		} finally {
			SQLdb.endTransaction();
		}
	}

}