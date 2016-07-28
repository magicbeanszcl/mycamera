package com.example.mycamera;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.Window;

import com.example.mycamera.CameraAgent.CameraOpenCallback;
import com.example.mycamera.CameraAgent.CameraProxy;


public class MycameraMainActivity extends Activity implements CameraOpenCallback{
	
	CameraAgent mCameraAgent;
	Handler mHandler;
	CameraUI mCameraUI;
	CameraProxy mCameraProxy;
	PhotoModule mPhotoModule;
	private Context mContext;
	private boolean mPaused = false;
	private static final int LIGHTS_OUT_DELAY_MS = 4000;
	private final Runnable mFullScreenRunnable = new Runnable(){
		@Override
		@SuppressLint("InlinedApi") public void run(){
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		}
	};
	public static final int HIDE_NAVIGATION_VIEW = View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR | Window.FEATURE_NO_TITLE);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_mycamera_layout);
        mContext = getApplication().getBaseContext();
        mCameraAgent = new CameraAgent();
        mHandler = new Handler(Looper.getMainLooper());
        mCameraUI = new CameraUI(this,(RootViewLayout)findViewById(R.id.MainActivityLayout),mHandler);
        mPhotoModule = new PhotoModule(this,mHandler);
        
        if(requestcamera()){
        	Log.v("MycameraMainActivity", "open camera done!");
        }
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	/*mFullScreenRunnable.run();
    	getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                new OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                    	mHandler.removeCallbacks(mFullScreenRunnable);
                        if (getWindow().getDecorView().getSystemUiVisibility() != HIDE_NAVIGATION_VIEW) {
                        	//mHandler.postDelayed(mFullScreenRunnable, LIGHTS_OUT_DELAY_MS);
                        }
                    }
                });*/
    	if(mPaused == true){
    		requestcamera();
    	}
    	super.onResume();
		
	}

    public boolean requestcamera() {
    	if(mPaused != true){
    		mCameraAgent.OpenCamera(0, mHandler, this);
    	}else{
    		mCameraProxy.reconnectcamera(0,mHandler,this);
    		mPaused = false;
    	}
    	
    	return true;
    }
    
    public PhotoModule getPhotoModule(){
    	return mPhotoModule;
    }
    public CameraUI getCameraUI(){
    	return mCameraUI;
    }
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mPhotoModule.onCameraPaused();
		mPaused = true;
		super.onPause();
		
		//mCamera.stopPreview();
		//mCamera.release();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mPhotoModule.onCameraStoped();
		super.onStop();
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mycamera_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
	public void onCameraOpened(final CameraProxy camera) {
		// TODO Auto-generated method stub
		mCameraProxy = camera;
		mPhotoModule.onCameraOpened(mCameraProxy);
		Log.v("MycameraMainActivity", "oncamera opened!");	
	}
    
    public void updatePreviewAspectRatio(float aspectratio){
    	Log.v("MycameraMainActivity", "aspectratio!"+aspectratio);
    	mCameraUI.updatePreviewAspectRatio(aspectratio);
    	
    }

}
