package com.example.mycamera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class RootViewLayout extends FrameLayout{

	private CameraUI.NonDecorWindowSizeChangedListener mNonDecorWindowSizeChangedListener = null;
	public RootViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		if (mNonDecorWindowSizeChangedListener != null) {
            mNonDecorWindowSizeChangedListener.onNonDecorWindowSizeChanged(
                    MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec),
                    CameraInformation.getDisplayRotation(getContext()));
        }
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
	}
	
	public void setNonDecorWindowSizeChangedListener(
			CameraUI.NonDecorWindowSizeChangedListener listener) {
        mNonDecorWindowSizeChangedListener = listener;
        if (mNonDecorWindowSizeChangedListener != null) {
            mNonDecorWindowSizeChangedListener.onNonDecorWindowSizeChanged(
                    getMeasuredWidth(), getMeasuredHeight(),
                    CameraInformation.getDisplayRotation(getContext()));
        }
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	

}
