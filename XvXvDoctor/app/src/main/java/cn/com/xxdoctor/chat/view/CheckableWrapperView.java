package cn.com.xxdoctor.chat.view;

import android.content.Context;
import android.widget.Checkable;

import cn.com.xxdoctor.chat.view.listview.WrapperView;

/**
 * A WrapperView that implements the checkable interface
 * 
 */
public class CheckableWrapperView extends WrapperView implements Checkable {

	public CheckableWrapperView(final Context context) {
		super(context);
	}

	@Override
	public boolean isChecked() {
		return ((Checkable) mItem).isChecked();
	}

	@Override
	public void setChecked(final boolean checked) {
		((Checkable) mItem).setChecked(checked);
	}

	@Override
	public void toggle() {
		setChecked(!isChecked());
	}
}
