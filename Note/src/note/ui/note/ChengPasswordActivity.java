package note.ui.note;

import note.MyApplication;
import note.api.API;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note.R;

public class ChengPasswordActivity extends Activity implements OnClickListener {
	protected Intent intent;
	private EditText oldPassword;
	private EditText newPassword;
	private EditText reenterNewPassword;
	private Button enterButton;
	API API;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_activity);
		API = new API();

		oldPassword = (EditText) findViewById(R.id.oldPassword);
		newPassword = (EditText) findViewById(R.id.newPassword);
		reenterNewPassword = (EditText) findViewById(R.id.reenterPassword);
		enterButton = (Button) findViewById(R.id.buttonEnterChenge);
		enterButton.setOnClickListener(this);

		intent = getIntent();
	}

	@Override
	public void onClick(View v) {
		final String LOGIN = ((MyApplication) getApplication()).getLocalData().sessionId;
		final String OLD_PASSWORD = oldPassword.getText().toString();
		final String NEW_PASSWORD = newPassword.getText().toString();
		final String REENTER_NEW_PASSWORD = reenterNewPassword.getText().toString();

		if (API.chengPassword(LOGIN, NEW_PASSWORD, OLD_PASSWORD)&& NEW_PASSWORD.equals(REENTER_NEW_PASSWORD)) {
			Toast toast = Toast.makeText(this, "Красава", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 10, 50);
			toast.show();
			intent = new Intent(this, NoteActivity.class);
			startActivity(intent);
		} else {
			Toast toast = Toast.makeText(this, "Что то не так",Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 10, 50);
			toast.show();
		}
	}
}
