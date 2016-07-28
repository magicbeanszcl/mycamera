package com.example.mycamera;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class ControlBar extends FrameLayout{
	
	private PreviewLayoutCalculate mPreviewLayoutCalculate = null;
	private ColorDrawable colordrawable;
	private static String Tag = "ControlBar";
	public ControlBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		final int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
	    final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
	    if (measureWidth == 0 || measureHeight == 0) {
	        return;
	    }
	    if(mPreviewLayoutCalculate == null){
	    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    	Log.e(Tag, "preview layout calculate need set first");
	    }
	    RectF mControlBarRect = mPreviewLayoutCalculate.getControlBarRect();
	    super.onMeasure(MeasureSpec.makeMeasureSpec(
                (int) mControlBarRect.width(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) mControlBarRect.height(), MeasureSpec.EXACTLY)
                );
		
	}
	
	public void setPreviewLayoutCalculate(PreviewLayoutCalculate calculate){
		mPreviewLayoutCalculate = calculate;
	}
	
	public void setControlBarColor(int color){
		//colordrawable.setColor(color);
		super.setBackgroundColor(color);
	}
	

}
