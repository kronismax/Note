package note.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends Button {

	public MyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		MyTextView.init(this, attrs);
	}

	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		MyTextView.init(this, attrs);
	}

	public MyButton(Context context) {
		super(context);
		MyTextView.init(this, null);
	}

}
