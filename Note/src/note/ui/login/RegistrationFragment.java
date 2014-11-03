package note.ui.login;

import note.api.API;
import note.api.ApiException;
import note.api.API.RegisterResponse;
import note.ui.login.LoginFragment.LoginAsyncTaskLoader;
import note.utils.UIUtils;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

	private EditText	LogText;
	private EditText	PassText;
	private EditText	RepeatPassText;
	private Button		Registration;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
		return inflater.inflate(R.layout.register_frag, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveInstanceState){
		super.onViewCreated(view, saveInstanceState);

		LogText = (EditText) view.findViewById(R.id.logText);
		PassText = (EditText) view.findViewById(R.id.passText);
		RepeatPassText = (EditText) view.findViewById(R.id.repeatPassText);
		Registration = (Button) view.findViewById(R.id.button_login);
		Registration.setOnClickListener(this);

	}

	public void onClick(View arg0){

		final String LOGIN = LogText.getText().toString();
		final String PASS = PassText.getText().toString();
		final String REPEATPASS = RepeatPassText.getText().toString();
		if ((PASS.equals(REPEATPASS) && !TextUtils.isEmpty(LOGIN) && !TextUtils.isEmpty(PASS) && !TextUtils.isEmpty(REPEATPASS))) {
			RegisterRequest request = new RegisterRequest(LOGIN, PASS);

			Registration.setEnabled(false);
			Bundle loginBundle = new Bundle();
			RegisterRequest registerRequest = new RegisterRequest(LOGIN, PASS);
			loginBundle.putParcelable(KEY_FOR_REGISTER, registerRequest);
			getLoaderManager().initLoader(1, loginBundle, registerResponseLoaderCallbacks).forceLoad();
		} else {
			Toast.makeText(getActivity(), "Введите корректные данные", Toast.LENGTH_SHORT).show();
		}
	}

	public static class RegisterRequest implements Parcelable {

        public final Creator<RegisterRequest> CREATOR = new Parcelable.Creator<RegisterRequest>() {

            @Override
            public RegisterRequest createFromParcel(Parcel source) {
                return new RegisterRequest(source);
            }

            @Override
            public RegisterRequest[] newArray(int size) {
                return new RegisterRequest[size];
            }
        };
        String login = "";
        String pass = "";

	public RegisterRequest(String login,String pass) {
		this.login = login;
		this.pass = pass;

	}

	public RegisterRequest(Parcel source) {
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

	
	private final String KEY_FOR_REGISTER = "KEY_FOR_REGISTER";
	public LoaderManager.LoaderCallbacks<RegisterResponse> registerResponseLoaderCallbacks = new LoaderManager.LoaderCallbacks<RegisterResponse>() {
		RegisterRequest request;

		@Override
		public Loader<RegisterResponse> onCreateLoader(int id, Bundle args) {
			request = args.getParcelable(KEY_FOR_REGISTER);
			return new RegisterAsyncTaskLoader(getActivity(),(RegisterRequest) args.getParcelable(KEY_FOR_REGISTER));
		}

		@Override
		public void onLoadFinished(Loader<RegisterResponse> loader,RegisterResponse data) {
			if (data == null) {
				final ApiException apiException = ((RegisterAsyncTaskLoader) loader).e;
				if (apiException != null) {
					UIUtils.showToastByException(getActivity(), apiException);
				}
			}
			if (data.result == 0) {
				Toast toast = Toast.makeText(getActivity(), "Красава",Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM, 10, 50);
				toast.show();
				getActivity().getActionBar().setSelectedNavigationItem(0);
			}
			if (data.result == 1) {
				Toast toast1 = Toast.makeText(getActivity(), "Попробуй позже",Toast.LENGTH_LONG);
				toast1.setGravity(Gravity.BOTTOM, 10, 50);
				toast1.show();
			}
		}

		@Override
		public void onLoaderReset(Loader<RegisterResponse> loader) {

		}
	};
	
	public static class RegisterAsyncTaskLoader extends AsyncTaskLoader<RegisterResponse> {

		public RegisterRequest	registerRequest;
		public ApiException e;
		
		public RegisterAsyncTaskLoader(Context context,RegisterRequest registerRequest) {
			super(context);
			this.registerRequest = registerRequest;
		}

		@Override
		public RegisterResponse loadInBackground(){
			try {
				return new API().register(registerRequest.login, registerRequest.pass);
			} catch (ApiException apIexception) {
				e = apIexception;
			}
			return null;
		}
	}
}