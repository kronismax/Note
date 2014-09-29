package note.ui.note;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.note.R;

public class NoteAdapter extends CursorAdapter {

	private LayoutInflater	mLayoutInflater;
	private Context			mContext;

	public NoteAdapter(Context context,Cursor cursor) {
		super(context, cursor);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent){
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.list, parent, false);
		return retView;
	}

	@Override
	public void bindView(View view, Context context, final Cursor cursor){

		((TextView) view.findViewById(R.id.noteName)).setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
		((TextView) view.findViewById(R.id.noteSubtitle)).setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));

	}
}