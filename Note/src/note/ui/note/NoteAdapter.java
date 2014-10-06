package note.ui.note;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.note.R;

public class NoteAdapter extends CursorAdapter {

	private LayoutInflater	mLayoutInflater;
	private Context			mContext;

	public OnDeleteItemListner onDeleteItemListner;
	
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
	public void bindView(View view, Context context, final Cursor cursor) {
		((TextView) view.findViewById(R.id.noteName)).setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
		((TextView) view.findViewById(R.id.noteSubtitle)).setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));

		final long noteId = cursor.getLong(0);
		
		((Button) view.findViewById(R.id.Delete)).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						invokeOnDeleteItemListner(noteId);
					}
				});

	}

	public interface OnDeleteItemListner {
		void onItemDeleteClick(long id);

	}

	public void setOnDeleteClickListener(OnDeleteItemListner listener) {
		onDeleteItemListner = listener;

	}

	public void invokeOnDeleteItemListner(long id) {
		if (onDeleteItemListner != null) {
			onDeleteItemListner.onItemDeleteClick(id);
		}
	}
}