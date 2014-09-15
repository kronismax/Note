package note.ui.login;

import note.api.API;
import note.api.ApiException;
import note.api.API.RegisterResponse;
import note.utils.UIUtils;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
		return inflater.inflate(R.layout.register_frag, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveInstanceState) {
		super.onViewCreated(view, saveInstanceState);

		LogText = (EditText) view.findViewById(R.id.logText);
		PassText = (EditText) view.findViewById(R.id.passText);
		RepeatPassText = (EditText) view.findViewById(R.id.repeatPassText);
		Registration = (Button) view.findViewById(R.id.button1);
		Registration.setOnClickListener(this);

	}

	public void onClick(View arg0) {

		final String LOGIN = LogText.getText().toString();
		final String PASS = PassText.getText().toString();
		final String REPEATPASS = RepeatPassText.getText().toString();
		if ((PASS.equals(REPEATPASS) && !TextUtils.isEmpty(LOGIN) && !TextUtils.isEmpty(PASS) && !TextUtils.isEmpty(REPEATPASS))) {
			RegisterRequest request = new RegisterRequest();
			request.login = LOGIN;
			request.password = PASS;

			Registration.setEnabled(false);
			mt = new MyAsyncTask();
			mt.execute(request);
		} else {
			Toast.makeText(getActivity(), "Введите корректные данные", Toast.LENGTH_SHORT).show();
		}
	}

	public static class RegisterRequest {
		String login;
		String password;
	}

	public class MyAsyncTask extends AsyncTask<RegisterRequest, Void, RegisterResponse> {

		ApiException exception;

		@Override
		protected RegisterResponse doInBackground(RegisterRequest... params) {
			try {
				return new API().register(params[0].login, params[0].password);
			} catch (ApiException apIexception) {
				exception = apIexception;
			}
			return null;
		}

		@Override
		protected void onPostExecute(RegisterResponse result) {
			Log.d("test", "onPostExecute 1");
			super.onPostExecute(result);
			Log.d("test", "onPostExecute 2");
			Registration.setEnabled(true);
			Log.d("test", "onPostExecute 3");
			if (result == null) {
				UIUtils.showToastByException(getActivity(), exception);
			} else {
				Toast.makeText(getActivity(), "Красава", Toast.LENGTH_SHORT).show();
				getActivity().getActionBar().setSelectedNavigationItem(0);
			}
		}
	}
}