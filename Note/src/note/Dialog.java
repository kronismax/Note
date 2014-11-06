package note;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

class Dialog extends DialogFragment {

	public Dialog() {
		// Empty constructor required for DialogFragment
	}

	public static Dialog newInstance(String title){
		Dialog frag = new Dialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public android.app.Dialog onCreateDialog(Bundle savedInstanceState){
		String title = getArguments().getString("title");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage("Are you sure?");
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which){
				// on success
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
			}
		});

		return alertDialogBuilder.create();
	}
}