package note.ui.login;

import note.MyApplication;
import note.api.API;
import note.api.API.LoginResponse;
import note.api.ApiException;
import note.model.database.MyContentProvider;
import note.ui.note.NoteActivity;
import note.utils.UIUtils;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

	private EditText			LogText;
	private EditText			PassText;
	private Button				Login;
	private Button				Demo;
	API							api				= new API();
	MyAsyncTask					mt;
	private static final String	PREF_SETTINGS	= "Settings";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
		return inflater.inflate(R.layout.log_frag, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveInstanceState){
		super.onViewCreated(view, saveInstanceState);
		LogText = (EditText) view.findViewById(R.id.logText);
		PassText = (EditText) view.findViewById(R.id.passText);
		Login = (Button) view.findViewById(R.id.button1);
		Demo = (Button) view.findViewById(R.id.ButtonDemo);
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
		Demo.setOnClickListener(this);
	}

	public void onClick(View arg0){

		switch (arg0.getId()) {
			case R.id.ButtonDemo:
				if (!TextUtils.isEmpty(LogText.getText())) {
					final String LOGIN = LogText.getText().toString();
					final String PASS = LOGIN;
					Toast toast = Toast.makeText(getActivity(), "" + LOGIN, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();

					LoginRequest request = new LoginRequest();
					request.login = LOGIN;
					request.password = PASS;
					Login.setEnabled(false);
					mt = new MyAsyncTask();
					mt.execute(request);

				} else {
					LoginRequest request = new LoginRequest();
					request.login = "q";
					request.password = "q";
					LogText.setText("q");
					Toast toast = Toast.makeText(getActivity(), "      q", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
					Login.setEnabled(false);
					mt = new MyAsyncTask();
					mt.execute(request);
				}
				break;
			case R.id.button1:
				final String LOGIN = LogText.getText().toString();
				final String PASS = PassText.getText().toString();
				Log.d("Разве логин", "?");
				if (LOGIN.isEmpty() || PASS.isEmpty()) {
					Toast toast = Toast.makeText(getActivity(), "Введите логин или пароль", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
				} else {
					LoginRequest request = new LoginRequest();
					request.login = LOGIN;
					request.password = PASS;

					Login.setEnabled(false);
					mt = new MyAsyncTask();
					mt.execute(request);
				}
				break;
		}
	}

	private void saveLastLogin(){
		final String LOGIN = LogText.getText().toString();
		SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE).edit();
		editor.putString("login", LOGIN);
		editor.commit();
	}

	public static class LoginRequest {
		String	login;
		String	password;
	}

	public class MyAsyncTask extends AsyncTask<LoginRequest, Void, LoginResponse> {

		ApiException	exception;

		@Override
		protected LoginResponse doInBackground(LoginRequest... params){
			try {
				return new API().login(params[0].login, params[0].password);
			} catch (ApiException apIexception) {
				exception = apIexception;
			}
			return null;
		}

		protected void onPostExecute(LoginResponse result){
			super.onPostExecute(result);

			if (result == null) {
				UIUtils.showToastByException(getActivity(), exception);
			} else {
				if (result.getResult() == 0) {
					SharedPreferences preferences = getActivity().getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
					String stringPreference = preferences.getString("login", "");
					if (!TextUtils.isEmpty(stringPreference)&& !(stringPreference.equals(LogText.getText().toString()))) {
						getActivity().getContentResolver().delete(MyContentProvider.URI_NOTE, null, null);
					}
					((MyApplication) getActivity().getApplication()).getLocalData().setSessionId(result.sessionId);
					saveLastLogin();
					Intent intent = new Intent(getActivity(), NoteActivity.class);
					startActivity(intent);
				} else if (result.getResult() == 1) {
					Toast toast = Toast.makeText(getActivity(), "Неправильный логин ии пароль", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
				} else {
					Toast toast = Toast.makeText(getActivity(), "Что то не так", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
				}
			}
			Login.setEnabled(true);
		}
	}
}
