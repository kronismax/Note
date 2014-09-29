package note;

import note.model.LocalData;
import note.remote.DataBaseUsers;
import android.app.Application;
import android.database.Cursor;

public class MyApplication extends Application {
	LocalData ld = new LocalData();

	@Override
	public void onCreate() {
		super.onCreate();
		DataBaseUsers.getInstance().init();
	}

	public LocalData getLocalData() {
		return ld;
	}

}
