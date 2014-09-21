package note.ui.note;

import note.model.LocalData;
import note.model.Note;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.note.R;

public class NoteAdapter extends BaseAdapter {

	Context			ctx;
	LayoutInflater	lInflater;
	LocalData		ld;

	public NoteAdapter(Context context,LocalData ld) {
		ctx = context;
		this.ld = ld;
		lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount(){
		return ld.getmNotes().size();
	}

	@Override
	public Note getItem(int position){
		return ld.getmNotes().get(position);
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.list, parent, false);
		}

		Note d = getItem(position);

		((TextView) view.findViewById(R.id.noteName)).setText(d.getTitle());
		((TextView) view.findViewById(R.id.noteSubtitle)).setText(d.getDescription());

		return view;

	}

	Note getNote(int position){
		return ((Note) getItem(position));
	}
}
