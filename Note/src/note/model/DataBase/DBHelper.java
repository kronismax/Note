package note.model.DataBase;

import note.model.DataBase.NoteDatabaseColumns.TableNote;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String	DATABASE_NAME		= "mydatabase.db";
	private static final int	VERSION_INITIAL		= 1;
	private static final int	DATABASE_VERSION	= VERSION_INITIAL;
	public static final String	ID					= "id";

	private static final String	DROP_TABLE			= "DROP TABLE IF EXISTS ";

	public interface Tables {

		String	TABLE_NOTE	= "tableNote";
	}

	 private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			   + Tables.TABLE_NOTE + " ("
			   + TableNote._ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			   + TableNote.TITLE      + " TEXT, "
			   + TableNote.CONTENT    + " TEXT)";

	

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_NOTE);
		//onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		 db.execSQL(DROP_TABLE + Tables.TABLE_NOTE);   
		  onCreate(db);
	}

}
