package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.ChengPasswordResponse;
import note.api.ApiException;
import note.ui.login.MainActivity;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class ChengPasswordActivity extends Activity implements OnClickListener {
	private EditText oldPassword;
	private EditText newPassword;
	private EditText reenterNewPassword;
	private Button enterButton;
	API API;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_activity);

		oldPassword = (EditText) findViewById(R.id.oldPassword);
		newPassword = (EditText) findViewById(R.id.newPassword);
		reenterNewPassword = (EditText) findViewById(R.id.reenterPassword);

		enterButton = (Button) findViewById(R.id.buttonEnterChenge);
		enterButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		final String SESSION_ID = ((MyApplication) getApplication()).getLocalData().getSessionId();
		final String OLD_PASSWORD = oldPassword.getText().toString();
		final String NEW_PASSWORD = newPassword.getText().toString();
		final String REENTER_NEW_PASSWORD = reenterNewPassword.getText().toString();

		if (NEW_PASSWORD.equals(REENTER_NEW_PASSWORD)) {
			new MyAsyncTask().execute(new ChangeUserPassword(SESSION_ID, OLD_PASSWORD, NEW_PASSWORD));
		} else {
			Toast toast = Toast.makeText(this, "Неверно введены данные", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 10, 50);
			toast.show();
		}
	}

	public class ChangeUserPassword {
		private String sessionID;
		private String oldPassword;
		private String newPassword;

		ChangeUserPassword(String sessionID, String oldPassword, String newPassword) {
			this.sessionID = sessionID;
			this.oldPassword = oldPassword;
			this.newPassword = newPassword;
		}

		public String getSessionID() {
			return sessionID;
		}

		public String getOldPassword() {
			return oldPassword;
		}

		public String getNewPassword() {
			return newPassword;
		}
	}

	public class MyAsyncTask extends AsyncTask<ChangeUserPassword, Void, ChengPasswordResponse> {
		ApiException apiexception;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			oldPassword.setEnabled(false);
			newPassword.setEnabled(false);
			reenterNewPassword.setEnabled(false);

			enterButton.setEnabled(false);
		}

		@Override
		protected ChengPasswordResponse doInBackground(ChangeUserPassword... params) {
			try {
				return API.chengPassword(params[0].getSessionID(), params[0].getNewPassword(), params[0].getOldPassword());
			} catch (ApiException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ChengPasswordResponse result) {
			super.onPostExecute(result);

			oldPassword.setEnabled(true);
			newPassword.setEnabled(true);
			reenterNewPassword.setEnabled(true);

			enterButton.setEnabled(true);

			if (result != null) {
				switch (result.getChengPasswordResponse()) {
				case 0:
					Toast toast = Toast.makeText(ChengPasswordActivity.this, "Великолепно", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();

					Intent intent = new Intent(ChengPasswordActivity.this, MainActivity.class);
					startActivity(intent);
					break;
				case 2:
					Toast toast1 = Toast.makeText(ChengPasswordActivity.this, "Введите новый пароль", Toast.LENGTH_LONG);
					toast1.setGravity(Gravity.BOTTOM, 10, 50);
					toast1.show();
					break;
				}
			} else {
				Toast toast1 = Toast.makeText(ChengPasswordActivity.this, "Эксэпшн", Toast.LENGTH_LONG);
				toast1.setGravity(Gravity.BOTTOM, 10, 50);
				toast1.show();
			}
		}
	}
}
