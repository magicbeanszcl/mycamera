package com.example.mycamera;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class ControlBarWrapper extends FrameLayout {

	private PreviewLayoutCalculate mPreviewLayoutCalculate = null;
	ControlBar mControlBar;
	private static String Tag = "ControlBarWrapper";
	public ControlBarWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		//super.onFinishInflate();
		mControlBar = (ControlBar)findViewById(R.id.control_bar);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		//super.onLayout(changed, left, top, right, bottom);
		if(mPreviewLayoutCalculate == null){
			Log.e(Tag, "preview layout calculate need set first");
			return;
		}
		RectF mControlBarRect = mPreviewLayoutCalculate.getControlBarRect();
		Log.e(Tag, "mControlBarRect Width "+mControlBarRect.width()+" mControlBarRect Height "+mControlBarRect.height());
		mControlBar.layout((int)mControlBarRect.left, (int)mControlBarRect.top, (int)mControlBarRect.right, (int)mControlBarRect.bottom);
		mControlBar.setControlBarColor(getResources().getColor(R.color.control_bar_default_background));
	}
	
	public void setPreviewLayoutCalculate(PreviewLayoutCalculate calculate){
		mPreviewLayoutCalculate = calculate;
	}
	
}
