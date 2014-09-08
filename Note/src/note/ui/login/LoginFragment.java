package note.ui.login;

import note.MyApplication;
import note.api.API;
import note.ui.note.NoteActivity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

	private EditText LogText;
	private EditText PassText;
	private Button   Login;
	private static final String PREF_SETTINGS = "Settings";
	API api = new API();
	
	MyAsyncTask mt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstance) {
		return inflater.inflate(R.layout.log_frag, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveInstanceState) {
		super.onViewCreated(view, saveInstanceState);
		LogText = (EditText) view.findViewById(R.id.logText);
		PassText = (EditText) view.findViewById(R.id.passText);
		Login = (Button) view.findViewById(R.id.button1);
		if (saveInstanceState == null) {
			SharedPreferences preferences = getActivity().getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
			String stringPreference = preferences.getString("login", "");
			LogText.setText(stringPreference);
			if (!TextUtils.isEmpty(LogText.getText())) {
				PassText.requestFocus();
			} else {
				LogText.requestFocus();
			}
		}
		Login.setOnClickListener(this);
	}

	public void onClick(View arg0) {
		Login.setEnabled(false);
		mt = new MyAsyncTask();
		mt.execute();
	}

	private void saveLastLogin() {
		final String LOGIN = LogText.getText().toString();
		SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE).edit();
		editor.putString("login", LOGIN);
		editor.commit();
	}

	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			final String LOGIN = LogText.getText().toString();
			final String PASS = PassText.getText().toString();
			if (LOGIN.equals("") || PASS.equals("")) {
				Toast toast = Toast.makeText(getActivity(),"Введите логин или пароль", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 10, 50);
				toast.show();
			} else {
				if (api.checkUser(LOGIN, PASS)) {
					((MyApplication) getActivity().getApplication()).getLocalData().setSessionID(LOGIN);
					Toast.makeText(getActivity(), "Красава", Toast.LENGTH_SHORT).show();
					saveLastLogin();
					Intent intent = new Intent(getActivity(),NoteActivity.class);
					startActivity(intent);
				} else {
					Toast toast = Toast.makeText(getActivity(),"Неправильный логин ии пароль", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
				}
			}
			Login.setEnabled(true);
		}
	}
}
