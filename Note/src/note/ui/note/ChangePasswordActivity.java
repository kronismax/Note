package note.ui.note;

import note.MyApplication;
import note.api.API;
import note.api.API.ChangePasswordResponse;
import note.api.ApiException;
import note.ui.login.MainActivity;
import note.ui.login.RegistrationFragment.RegisterAsyncTaskLoader;
import note.utils.UIUtils;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class ChangePasswordActivity extends Activity implements OnClickListener {
	private EditText oldPassword;
	private EditText newPassword;
	private EditText reenterNewPassword;
	private Button enterButton;

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

		Bundle changeBundle = new Bundle();
		ChangeUserPassword changeUserPassword = new ChangeUserPassword(SESSION_ID, OLD_PASSWORD, NEW_PASSWORD);
		changeBundle.putParcelable(KEY_FOR_CHANGE_PASS, changeUserPassword);
		
		if (NEW_PASSWORD.equals(REENTER_NEW_PASSWORD)) {
			 getLoaderManager().initLoader(1, changeBundle, changePasswordResponseLoaderCallbacks).forceLoad();
		} else {
			Toast toast = Toast.makeText(this, "Неверно введены данные", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 10, 50);
			toast.show();
		}
	}

	private final String KEY_FOR_CHANGE_PASS = "KEY_FOR_CHANGE_PASS";
		public LoaderManager.LoaderCallbacks<ChangePasswordResponse> changePasswordResponseLoaderCallbacks = new LoaderManager.LoaderCallbacks<ChangePasswordResponse>() {
			@Override
			public Loader<ChangePasswordResponse> onCreateLoader(int id, Bundle args) {
				return new ChangeUserPasswordLoader(ChangePasswordActivity.this, (ChangeUserPassword) args.getParcelable(KEY_FOR_CHANGE_PASS));
			}
			@Override
			public void onLoadFinished(Loader<ChangePasswordResponse> loader, ChangePasswordResponse data) {
				if (data == null) {
					final ApiException apiException = ((ChangeUserPasswordLoader) loader).e;
					if (apiException != null) {
						UIUtils.showToastByException(ChangePasswordActivity.this, apiException);
					}
				}
				Toast toast = Toast.makeText(ChangePasswordActivity.this, "Успех", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM, 10, 50);
				toast.show();
				Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			@Override
			public void onLoaderReset(Loader<ChangePasswordResponse> loader) {
			}
		};
		
	public static class ChangeUserPasswordLoader extends AsyncTaskLoader<ChangePasswordResponse> {
		ChangeUserPassword	changeUserPassword;
		public ApiException e;
		public ChangeUserPasswordLoader(Context context,ChangeUserPassword changeUserPassword) {
			super(context);
			this.changeUserPassword = changeUserPassword;
		}

		@Override
		public ChangePasswordResponse loadInBackground(){
			try {
				return new API().getChangePassword(changeUserPassword.getSessionID(), changeUserPassword.getNewPassword(), changeUserPassword.getOldPassword());
			} catch (ApiException apIexception) {
				e = apIexception;
			}
			return null;
		}
	}
		
	 public class ChangeUserPassword implements Parcelable {
		 public final Creator<ChangeUserPassword> CREATOR = new Parcelable.Creator<ChangeUserPassword>() {
			 @Override
			 public ChangeUserPassword createFromParcel(Parcel source) {
				 return new ChangeUserPassword(source);
			 }
			 @Override
			 public ChangeUserPassword[] newArray(int size) {
				 return new ChangeUserPassword[size];
			 }
		 };
		private String	sessionID;
		private String	oldPassword;
		private String	newPassword;

		ChangeUserPassword(String sessionID,String oldPassword,String newPassword) {
			Log.d("ChangeUserPassword", sessionID + "" + oldPassword + "" + newPassword);
			this.sessionID = sessionID;
			this.oldPassword = oldPassword;
			this.newPassword = newPassword;
		}

		public ChangeUserPassword(Parcel source) {
			source.writeString(sessionID);
			source.writeString(oldPassword);
			source.writeString(newPassword);
		}

		public String getSessionID(){
			return sessionID;
		}

		public String getOldPassword(){
			return oldPassword;
		}

		public String getNewPassword(){
			return newPassword;
		}

		@Override
		public int describeContents(){
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags){
			dest.writeString(sessionID);
			dest.writeString(oldPassword);
			dest.writeString(newPassword);
		}
	}
	
	
		
		
}
