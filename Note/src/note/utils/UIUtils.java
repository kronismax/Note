package note.utils;

import note.api.ApiException;
import android.content.Context;
import android.widget.Toast;

public final class UIUtils {

	private UIUtils() {
	}

	public static void showToastByException(Context context, ApiException e) {
		e.printStackTrace();
		switch (e.getError()) {
		case ERROR_CONNECTION:
			Toast.makeText(context, "ERROR_CONNECTION", Toast.LENGTH_SHORT).show();
			break;
		case ERROR_PARSE_RESPONSE:
			Toast.makeText(context, "ERROR_PARSE_RESPONSE", Toast.LENGTH_SHORT).show();
			break;
		case ERROR_WRONG_DATA:
			Toast.makeText(context, "ERROR_WRONG_DATA", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
