package com.example.mycamera;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;

 public class CameraUI implements TextureView.SurfaceTextureListener{
    
	TextureView mPreviewContent;
	private SurfaceTexture mSurface;
	MycameraMainActivity mAppcontroler;
	TextureView PreviewContent;
	PreviewSurfaceControl mPreviewSurfaceControl;
	private final PreviewLayoutCalculate mPreviewLayoutCalculate;
	private RootViewLayout mRootViewLayout;
	private ControlBarWrapper mControlBarWrapper;
	private ControlBar mControlBar;
	private ShutterButton mShutterButton;
	private PhotoModule mPhotoModule;
	private static String Tag = "CameraUI";
	public CameraUI(MycameraMainActivity Appcontroler,RootViewLayout mainactivitylayout,Handler handler) {
		// TODO Auto-generated constructor stub
		mAppcontroler = Appcontroler;
		mRootViewLayout = mainactivitylayout;
		Resources res = mAppcontroler.getApplication().getBaseContext().getResources();
		mPreviewLayoutCalculate = new PreviewLayoutCalculate(res.getDimensionPixelSize(R.dimen.control_bar_height_min),
				res.getDimensionPixelSize(R.dimen.control_bar_height_max),res.getDimensionPixelSize(R.dimen.control_bar_height_optimal));
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mAppcontroler).getWindowManager().getDefaultDisplay().getMetrics(dm);
		mPreviewLayoutCalculate.setDisplayHeight(dm.heightPixels);
		mPreviewLayoutCalculate.setNavigationBarHeight(getNavigationHeight());
		mPreviewContent = (TextureView)mRootViewLayout.findViewById(R.id.preview_surfacetexture);
		mControlBarWrapper = (ControlBarWrapper)mRootViewLayout.findViewById(R.id.control_bar_wrapper);
		mControlBar = (ControlBar)mRootViewLayout.findViewById(R.id.control_bar);
		mControlBarWrapper.setPreviewLayoutCalculate(mPreviewLayoutCalculate);
		mControlBar.setPreviewLayoutCalculate(mPreviewLayoutCalculate);
		//mControlBar.setBackgroundColor(color);
		mPreviewSurfaceControl = new PreviewSurfaceControl(mPreviewContent,mPreviewLayoutCalculate);
		mRootViewLayout.setNonDecorWindowSizeChangedListener(mPreviewLayoutCalculate);
		mPreviewSurfaceControl.setSurfaceTextureListener(this);
		mShutterButton = (ShutterButton)mRootViewLayout.findViewById(R.id.shutter_button);
		mShutterButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(Tag, "onclick");
				mPhotoModule = mAppcontroler.getPhotoModule();
				mPhotoModule.onShutterButtonClicked();
			}
			
		});
        
	}

	public SurfaceTexture getsurfacetexture(){
	     return mSurface;
    }
	 
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		mSurface = surface;
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void updatePreviewAspectRatio(float aspectratio){
		Log.v(Tag, "aspectratio"+aspectratio);
		mPreviewSurfaceControl.updatePreviewAspectRatio(aspectratio);
		
	}
	
	public int getNavigationHeight() {
        Resources resources = mAppcontroler.getApplication().getBaseContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }
	
	public interface NonDecorWindowSizeChangedListener{
		public void onNonDecorWindowSizeChanged(int width,int height,int rotation);
	}

}
