package note.ui.note;


import java.util.ArrayList;

import note.model.Note;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.note.R;

public class NoteAdapter extends BaseAdapter {
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<Note> objects;

	public NoteAdapter(Context context, ArrayList<Note> notes) {
		ctx = context;
		objects = notes;
		lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Note getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return objects.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.list, parent, false);
		}
		Note d = getItem(position);
		((TextView) view.findViewById(R.id.noteName)).setText(d.getTitle());
		((TextView) view.findViewById(R.id.noteSubtitle)).setText(d
				.getText());
		return view;
	}
}
