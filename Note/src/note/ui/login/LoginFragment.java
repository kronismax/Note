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
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.note.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

	private EditText			eLogin;
	private EditText			ePassword;
	private Button				Login;
	private Button				Demo;
	private TextView textView1;
	private static final String	PREF_SETTINGS	= "Settings";
	private final String KEY_FOR_LOGIN = "KEY_FOR_LOGIN";
	ProgressDialog mProgressDialog;
	Bundle loginBundle = new Bundle();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
		return inflater.inflate(R.layout.log_frag, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveInstanceState){
		super.onViewCreated(view, saveInstanceState);
		eLogin = (EditText) view.findViewById(R.id.logText);
		ePassword = (EditText) view.findViewById(R.id.passText);
		Login = (Button) view.findViewById(R.id.button_login);
		Demo = (Button) view.findViewById(R.id.ButtonDemo);
		textView1= (TextView) view.findViewById(R.id.textView1);
		
		String fontShortcuts = "fonts/CANON.ttf";
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), fontShortcuts);
		Demo.setTypeface(font);
		
		if (saveInstanceState == null) {
			SharedPreferences preferences = getActivity().getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
			String stringPreference = preferences.getString("login", "");
			eLogin.setText(stringPreference);
			if (!TextUtils.isEmpty(eLogin.getText())) {
				ePassword.requestFocus();
			} else {
				eLogin.requestFocus();
			}
		}
		Login.setOnClickListener(this);
		Demo.setOnClickListener(this);
	}

	public void onClick(View arg0){

		switch (arg0.getId()) {
			case R.id.ButtonDemo:
				if (!TextUtils.isEmpty(eLogin.getText())) {
					final String LOGIN = eLogin.getText().toString();
					final String PASS = LOGIN;
					login(LOGIN, LOGIN, PASS, arg0);					
				} else {
					String loginQ = "q";
					login(loginQ, loginQ, loginQ, arg0);
				}
				break;
			case R.id.button_login:
				final String LOGIN = eLogin.getText().toString();
				final String PASS = ePassword.getText().toString();
				Log.d("Разве логин", "?");
				if (LOGIN.isEmpty() || PASS.isEmpty()) {
					Toast toast = Toast.makeText(getActivity(), "Введите логин или пароль", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 10, 50);
					toast.show();
				} else {
					login("Красава", LOGIN, PASS, arg0);
			}
		}
	}
	
	public void login(String toastText, String login, String pass, View arg0) {
		Toast toast = Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 10, 50);
		toast.show();
		
		LoginRequest loginRequest = new LoginRequest(login, pass);
		loginBundle.putParcelable(KEY_FOR_LOGIN, loginRequest);
		
		Login.setEnabled(false);
		
		launchRingDialog(arg0);
	}
	
	public void launchRingDialog(View view){
		final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Downloading ...", true);
		ringProgressDialog.setCancelable(true);
		new Thread(new Runnable() {

			@Override
			public void run(){
				try {
					// Here you should write your time consuming task... let the progress ring for 10 seconds...
					Thread.sleep(500);
					getLoaderManager().initLoader(1, loginBundle, loginResponseLoaderCallbacks).forceLoad();
				} catch (Exception e) {
				}
				ringProgressDialog.dismiss();
			}
		}).start();
	}
	
	private void saveLastLogin(){
		final String LOGIN = eLogin.getText().toString();
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
				if (!TextUtils.isEmpty(stringPreference)&& !(stringPreference.equals(eLogin.getText().toString()))) {
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
		public ApiException	e;

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