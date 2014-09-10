package note.ui.login;

import org.json.JSONException;

import note.api.API;
import note.api.API.RegisterResponse;
import note.ui.login.LoginFragment.LoginRequest;
import note.ui.login.LoginFragment.MyAsyncTask;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

	private EditText LogText;
	private EditText PassText;
	private EditText RepeatPassText;
	private Button Registration;

	API api = new API();
	
	MyAsyncTask mt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstance) {
		return inflater.inflate(R.layout.register_frag, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveInstanceState) {
		super.onViewCreated(view, saveInstanceState);

		LogText        = (EditText) view.findViewById(R.id.logText);
		PassText       = (EditText) view.findViewById(R.id.passText);
		RepeatPassText = (EditText) view.findViewById(R.id.repeatPassText);
		Registration   = (Button) view.findViewById(R.id.button1);
		Registration.setOnClickListener(this);
		
	}

	public void onClick(View arg0) {

		final String LOGIN = LogText.getText().toString();
		final String PASS = PassText.getText().toString();
		final String REPEATPASS = RepeatPassText.getText().toString();
		if ((PASS.equals(REPEATPASS) && !TextUtils.isEmpty(LOGIN)
				&& !TextUtils.isEmpty(PASS) && !TextUtils.isEmpty(REPEATPASS))) {

			RegisterRequest request = new RegisterRequest();
			request.login = LOGIN;
			request.password = PASS;

			Registration.setEnabled(false);
			mt = new MyAsyncTask();
			mt.execute(request);

			Toast.makeText(getActivity(), "Красава", Toast.LENGTH_SHORT).show();
			getActivity().getActionBar().setSelectedNavigationItem(0);

		} else {

			Toast.makeText(getActivity(), "Введите корректные данные",Toast.LENGTH_SHORT).show();
		}

//		Registration.setEnabled(false);
//		mt = new MyAsyncTask();
//		mt.execute();
	}

	public static class RegisterRequest {
		String login;
		String password;

	}
	
	
	
	public class MyAsyncTask extends AsyncTask<RegisterRequest, Void, RegisterResponse> {
		
		@Override
		protected RegisterResponse doInBackground(RegisterRequest... params) {
			try {
				return new API().register(params[0].login, params[0].password);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(RegisterResponse result) {
			super.onPostExecute(result);
		
		}
	}
}



/*public class MyAsyncTask extends AsyncTask<LoginRequest, Void, LoginResponse> {
	@Override
	protected LoginResponse doInBackground(LoginRequest... params) {

		try {
			return new API().login(params[0].login, params[0].password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(LoginResponse result) {
		super.onPostExecute(result);

		if (result.result == 0) {
			((MyApplication) getActivity().getApplication()).getLocalData()
					.setSessionID(result.sessionId);
			Toast.makeText(getActivity(), "Красава", Toast.LENGTH_SHORT).show();
			saveLastLogin();
			Intent intent = new Intent(getActivity(), NoteActivity.class);
			startActivity(intent);
		} else {
			Toast toast = Toast.makeText(getActivity(),
					"Неправильный логин ии пароль", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 10, 50);
			toast.show();
		}
		Login.setEnabled(true);
	}
} */
