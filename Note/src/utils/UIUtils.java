package utils;

import note.api.ApiException;
import android.content.Context;
import android.widget.Toast;

public final class UIUtils {

	private UIUtils() {
	}

	public static void showToastByException(Context context, ApiException e) {

		switch (e.getError()) {
		case ERROR:
			Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
			break;
		case ERROR_CONNECTION:
			Toast.makeText(context, "ERROR_CONNECTION", Toast.LENGTH_SHORT).show();
			break;
		case ERROR_JSON:
			Toast.makeText(context, "ERROR_JSON", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
