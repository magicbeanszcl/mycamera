package com.example.mycamera;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TwoStateView extends ImageView {

	private boolean mFilterEnabled = true;
	private static final int ENABLED_ALPHA = 255;
	private static final int DISABLED_ALPHA = (int)(255*0.4);
	public TwoStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		if(mFilterEnabled){
			if(enabled){
				setAlpha(ENABLED_ALPHA);
			}else{
				setAlpha(DISABLED_ALPHA);
			}
		}
	}
	
	public void setFilterEnabled(boolean filterenabled){
		mFilterEnabled = filterenabled;
	}
	
    
}
