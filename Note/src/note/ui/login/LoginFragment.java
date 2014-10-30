package note.ui.login;

import note.MyApplication;
import note.api.API;
import note.api.API.LoginResponse;
import note.api.ApiException;
import note.model.database.MyContentProvider;
import note.ui.note.NoteActivity;
import note.utils.UIUtils;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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
	private static final String	PREF_SETTINGS	= "Settings";
	private final String KEY_FOR_LOGIN = "KEY_FOR_LOGIN";
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

					Login.setEnabled(false);
					Bundle loginBundle = new Bundle();
					LoginRequest loginRequest = new LoginRequest(LOGIN, PASS);
					loginBundle.putParcelable(KEY_FOR_LOGIN, loginRequest);
					getLoaderManager().initLoader(1, loginBundle, loginResponseLoaderCallbacks).forceLoad();

				} else {
					LoginRequest request = new LoginRequest("q", "q");
					LogText.setText("q");
					Toast toast = Toast.makeText(getActivity(), "      q", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
					Login.setEnabled(false);
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
					Bundle loginBundle = new Bundle();
					LoginRequest loginRequest = new LoginRequest(LOGIN, PASS);
					loginBundle.putParcelable(KEY_FOR_LOGIN, loginRequest);
					getLoaderManager().initLoader(1, loginBundle, loginResponseLoaderCallbacks).forceLoad();

					Login.setEnabled(false);
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

	 public static class LoginRequest implements Parcelable {

	        public final Creator<LoginRequest> CREATOR = new Parcelable.Creator<LoginRequest>() {

	            @Override
	            public LoginRequest createFromParcel(Parcel source) {
	                return new LoginRequest(source);
	            }

	            @Override
	            public LoginRequest[] newArray(int size) {
	                return new LoginRequest[size];
	            }
	        };
	        String login = "";
	        String pass = "";

		public LoginRequest(String l,String p) {
			login = l;
			pass = p;

		}

		public LoginRequest(Parcel source) {
			source.writeString(login);
			source.writeString(pass);
		}

		@Override
		public int describeContents(){
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags){
			dest.writeString(login);
			dest.writeString(pass);
		}
	}

	public LoaderManager.LoaderCallbacks<LoginResponse> loginResponseLoaderCallbacks = new LoaderManager.LoaderCallbacks<LoginResponse>() {
		LoginRequest request;

		@Override
		public Loader<LoginResponse> onCreateLoader(int id, Bundle args) {
			request = args.getParcelable(KEY_FOR_LOGIN);
			return new LoginAsyncTaskLoader(getActivity(), (LoginRequest) args.getParcelable(KEY_FOR_LOGIN));
		}

		@Override
		public void onLoadFinished(Loader<LoginResponse> loader, LoginResponse data) {
			if (data == null) {
				final ApiException apiException = ((LoginAsyncTaskLoader) loader).e;
				if (apiException != null) {
					UIUtils.showToastByException(getActivity(), apiException);
				}
			}
			if (data.getUserCreate() == 0) {
				((MyApplication) getActivity().getApplication()).getLocalData().setSessionId(data.sessionID);
				Toast toast = Toast.makeText(getActivity(), "Красава",Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM, 10, 50);
				toast.show();

				SharedPreferences preferences = getActivity().getSharedPreferences(PREF_SETTINGS,Context.MODE_PRIVATE);
				String stringPreference = preferences.getString("login", "");
				if (!TextUtils.isEmpty(stringPreference)&& !(stringPreference.equals(LogText.getText().toString()))) {
					getActivity().getContentResolver().delete(MyContentProvider.URI_NOTE, null, null);
				}
				saveLastLogin();
				Intent intent = new Intent(getActivity(), NoteActivity.class);
				startActivity(intent);

			}
			if (data.getUserCreate() == 1) {
				Toast toast1 = Toast.makeText(getActivity(), "Попробуй позже",Toast.LENGTH_LONG);
				toast1.setGravity(Gravity.BOTTOM, 10, 50);
				toast1.show();
			}
		}

		@Override
		public void onLoaderReset(Loader<LoginResponse> loader) {

		}
	};

	public static class LoginAsyncTaskLoader extends AsyncTaskLoader<LoginResponse> {

		public LoginRequest	loginRequest;
		public ApiException e;
		
		public LoginAsyncTaskLoader(Context context,LoginRequest loginRequest) {
			super(context);
			this.loginRequest = loginRequest;
		}

		@Override
		public LoginResponse loadInBackground(){
			try {
				return new API().login(loginRequest.login, loginRequest.pass);
			} catch (ApiException apIexception) {
				e = apIexception;
			}
			return null;
		}
	}
}