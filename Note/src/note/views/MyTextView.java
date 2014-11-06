package note.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.note.R;

public class MyTextView extends TextView {

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(this, attrs);
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(this, attrs);
	}

	public MyTextView(Context context) {
		super(context);
		init(this, null);
	}

	static void init(TextView tv, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = tv.getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);
			String fontName = a.getString(R.styleable.MyTextView_fontName);
			if (fontName != null) {
				Typeface myTypeface = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/" + fontName);
				tv.setTypeface(myTypeface);
			}
			a.recycle();
		}
	}
	
}