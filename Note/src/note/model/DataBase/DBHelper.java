package note.model.DataBase;

import note.model.DataBase.NoteDatabaseColumns.TableNote;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String	DATABASE_NAME		= "mydatabase.db";
	private static final int	VERSION_INITIAL		= 1;
	private static final int	DATABASE_VERSION	= VERSION_INITIAL;
	public static final String	ID					= "id";

	private static final String	DROP_TABLE			= "DROP TABLE IF EXISTS ";

	public interface NoteTable {

		String	TABLE_NOTE	= "tableNote";
	}

	 private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			   + NoteTable.TABLE_NOTE + " ("
			   + TableNote._ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			   + TableNote.TITLE      + " TEXT, "
			   + TableNote.CONTENT    + " TEXT, "
			   + TableNote.NOTE_ID    + " INTEGER)";

	

	public DBHelper(Context context,String name,CursorFactory factory,int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_NOTE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		 db.execSQL(DROP_TABLE + NoteTable.TABLE_NOTE);   
		  onCreate(db);
	}

}
