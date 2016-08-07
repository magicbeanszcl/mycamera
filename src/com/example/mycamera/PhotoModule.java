package com.example.mycamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.example.mycamera.CameraAgent.CameraProxy;
import com.example.mycamera.CameraAgent.CameraShutterCallback;
import com.example.mycamera.CameraAgent.TakePictureCallback;
import com.example.mycamera.CameraInformation.CurrentCamerainfor;

public class PhotoModule {

	CameraProxy mCameraProxy;
	CameraAgent mCameraAgent;
	Handler mHandler;
	private Context mContext;
	MycameraMainActivity mMainActivity;
	CameraCapabilities mCameraCapabilities;
	CameraSettings mCameraSettings;
	public int mDisplayRotation;
	private final PostViewPictureCallback mPostViewPictureCallback = new PostViewPictureCallback();
    private final RawPictureCallback mRawPictureCallback = new RawPictureCallback();
    private final JpegPictureCallback mJpegPictureCallback = new JpegPictureCallback();
    private long mShutterCallbackTime;
    private long mCaptureStartTime;
    private long mShutterLag;
    private long mPostViewPictureCallbackTime;
    private long mRawPictureCallbackTime;
    private long mJpegPictureCallbackTime;
    public long mShutterToPictureDisplayedTime;
    public long mPictureDisplayedToJpegCallbackTime;
    private String mLatestPhotoPath;
    private static String Tag = "PhotoModule";
	public PhotoModule(MycameraMainActivity mainactivity,Handler handler) {
		// TODO Auto-generated constructor stub
		mHandler = handler;
		mMainActivity = mainactivity;
		
	}
	
	public void onCameraOpened(CameraProxy camera) {
		// TODO Auto-generated method stub
		mCameraProxy = camera;
		initializeCameraCapabilities();
		updateparameters();
		mCameraProxy.setSurfaceTexture(mMainActivity.getCameraUI().getsurfacetexture());
		SetPreviewOrientation();
		updatePicturesizeParas();
		StartPreview();
	}
	
	public void StartPreview(){
		CameraAgent.StartPreviewCallback startpreviewcallback = new CameraAgent.StartPreviewCallback(){

			@Override
			public void onPreviewStarted() {
				// TODO Auto-generated method stub
				Log.v(Tag, "preview started !");
				//Autofocus();
			}	
		};  
		mCameraProxy.startpreview(mHandler, startpreviewcallback);
	}
	
	public void initializeCameraCapabilities(){
		mCameraCapabilities = mCameraProxy.getCameraCapabilities();
		mCameraSettings = mCameraProxy.getCameraSettings();
	}
	
	public void updateparameters(){
		mCameraSettings.setFocusmode(mMainActivity.getResources().getString(R.string.camera_focusmode_default));
	}
	public void updatePicturesizeParas(){
		ArrayList<Size> SupportedPreviewSizes = mCameraCapabilities.getSupportedPreviewSize();
		ArrayList<Size> SupportedPictureSize = mCameraCapabilities.getSupportedPictureSize();
		double targetratio = (double)4/3;
		Size optimalpreviewsize = getOptimalPreviewSize(SupportedPreviewSizes,targetratio);
		Size optimalpicturesize = getOptimalPictureSize(SupportedPictureSize,targetratio);
		Log.v(Tag,"optimalsize width "+optimalpicturesize.width()+" height "+optimalpicturesize.height());
		mCameraSettings.setPreviewSize(optimalpreviewsize);
		mCameraSettings.setPictureSize(optimalpicturesize);
		mCameraProxy.applySettings(mCameraSettings);
		Log.v(Tag,"AspectRatio"+(float)optimalpreviewsize.width()/(float)optimalpreviewsize.height());
		mMainActivity.updatePreviewAspectRatio((float)optimalpreviewsize.width()/(float)optimalpreviewsize.height());
	}
	
	public  void SetPreviewOrientation(){
		mDisplayRotation = getPreviewOrientation();
		CameraInformation mCameraInformation = CameraInformation.creat();
		CurrentCamerainfor mCurrentCamerainfor  = mCameraInformation.getCurrentCameraInfors(0);
		int mSensorOrientation = mCurrentCamerainfor.getSensorOrientation();
		int result = 0;
		if(mCurrentCamerainfor.isfacingfront()){
			result = (mSensorOrientation + mDisplayRotation)%360;
		}else if(mCurrentCamerainfor.isfacingback()){
			result = (mSensorOrientation - mDisplayRotation + 360)%360;
		}else{
			Log.e(Tag, "Camera facing is not sure!");
		}
		Log.v(Tag, "display rotation is "+mDisplayRotation+" sensor rotation is "+mSensorOrientation+"result "+result);
		mCameraProxy.setDisplayOrientation(result);
	}
	
	public Size getOptimalPictureSize(ArrayList<Size> sizes,double targetratio){
		int Ratio4X3Resolution = 0;
		int Ratio16X9Resolution = 0;
		Size Max4X3Resolution = new Size(0,0);
		Size Max16X9Resolution = new Size(0,0);
		double ratio4X3 = (double)4/(double)3;
		double ratio16X9 = (double)16/(double)9;
		double Ratiodiff = 0.1;
		//Log.v(Tag,"getOptimalPictureSize "+sizes.get(1));
		for(Size size:sizes){
			double currentsizeratio = (float)size.width()/(float)size.height();
			int currentsizeresolution = size.width()*size.height();
			Log.w(Tag, "ratio "+currentsizeratio+" currentsizeresolution "+currentsizeresolution);
			double current4X3ratiodiff = Math.abs(currentsizeratio - ratio4X3);
			double current16X9ratiodiff = Math.abs(currentsizeratio - ratio16X9);
			if(current4X3ratiodiff < Ratiodiff){
				if(currentsizeresolution > Ratio4X3Resolution){
					Max4X3Resolution = size;
					Ratio4X3Resolution = currentsizeresolution;
				}
			}
			else if(current16X9ratiodiff < Ratiodiff){
				if(currentsizeresolution > Ratio16X9Resolution){
					Max16X9Resolution = size;
					Ratio16X9Resolution = currentsizeresolution;
				}
			}
		}
		
		if(Math.abs(targetratio - ratio4X3) < Ratiodiff){
		    return Max4X3Resolution;
		}
		else if(Math.abs(targetratio - ratio16X9) < Ratiodiff){
		    return Max16X9Resolution;	
		}else{
			return Max4X3Resolution;
		}
	}
	public Size getOptimalPreviewSize(ArrayList<Size> sizes,double targetratio){
		final double MATCH_TOLERANCE = 0.01;
		if(sizes == null){
			Log.e(Tag, "get supported preview sizes are null!");
		}
		double minDiff = Double.MAX_VALUE;
		int optimalSizeindex = -1;
		WindowManager windowManager = (WindowManager)mMainActivity.getSystemService(Context.WINDOW_SERVICE);
		Point res = new Point();
		windowManager.getDefaultDisplay().getSize(res);
		Size DefaultDisplaySize = new Size(res);
		
		int targetHeight = Math.min(DefaultDisplaySize.width(), DefaultDisplaySize.height());
		Log.w(Tag, "targetHeight "+targetHeight);
		for(int i = 0; i < sizes.size(); i++){
			Size size = sizes.get(i);
			double ratio = (double)size.width()/size.height();
			if(Math.abs(ratio-targetratio) > MATCH_TOLERANCE){
			   continue;
			}
			double heightDiff = Math.abs(size.height()-targetHeight);
			if(heightDiff < minDiff){
				optimalSizeindex = i;
				minDiff = heightDiff;
			}else if(heightDiff == minDiff){
				if (size.height() < targetHeight){
					optimalSizeindex = i;
					minDiff = heightDiff;
				}
			}
			
		}
		if(optimalSizeindex == -1){
			Log.w(Tag, "No preview size match the ratio!");
			return null;
		}
		return sizes.get(optimalSizeindex); 
	}
	
	public int getPreviewOrientation(){
		WindowManager windowmanager = (WindowManager)mMainActivity.getSystemService(Context.WINDOW_SERVICE);
		int rotation = windowmanager.getDefaultDisplay().getRotation();
		switch(rotation) {
		    case Surface.ROTATION_0:
		    	return 0;
		    case Surface.ROTATION_90:
		    	return 90;
		    case Surface.ROTATION_180:
		    	return 180;
		    case Surface.ROTATION_270:
		    	return 270;
		}
		return 0;
	}
	
	public void Autofocus(){
		CameraAgent.AutofocusCallback autofocuscallback = new CameraAgent.AutofocusCallback(){

			@Override
			public void onAutofocus() {
				// TODO Auto-generated method stub
				
			}
		};
		mCameraProxy.autofocus(mHandler, autofocuscallback);
	}
	
	public void onShutterButtonClicked(){
		Log.v(Tag, "onShutterButtonClicked");
		CameraAgent.TakePictureCallback jpegpicturecallback = new CameraAgent.TakePictureCallback(){
			@Override
			public void onTakePicture(byte[] data, CameraProxy camera) {
				// TODO Auto-generated method stub
				
			}
			
		};
		mCaptureStartTime = System.currentTimeMillis();
		mCameraProxy.takepicture(mHandler,new ShutterCallback(),mRawPictureCallback,mPostViewPictureCallback,mJpegPictureCallback);
	}
	
    private final class ShutterCallback implements CameraShutterCallback {

       @Override
       public void onShutter(CameraProxy camera) {
          mShutterCallbackTime = System.currentTimeMillis();
          mShutterLag = mShutterCallbackTime - mCaptureStartTime;
          Log.v(Tag, "mShutterLag = " + mShutterLag + "ms");
       }
    }

    private final class PostViewPictureCallback implements TakePictureCallback {
       @Override
       public void onTakePicture(byte[] data, CameraProxy camera) {
          mPostViewPictureCallbackTime = System.currentTimeMillis();
          Log.v(Tag, "mShutterToPostViewCallonbackTime = "
            + (mPostViewPictureCallbackTime - mShutterCallbackTime)
            + "ms");
       }
    }

    private final class RawPictureCallback implements TakePictureCallback {
       @Override
       public void onTakePicture(byte[] rawData, CameraProxy camera) {
          mRawPictureCallbackTime = System.currentTimeMillis();
          Log.v(Tag, "mShutterToRawCallbackTime = "
            + (mRawPictureCallbackTime - mShutterCallbackTime) + "ms");
       }
    }

    private final class JpegPictureCallback implements TakePictureCallback {

    	SimpleDateFormat format = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss'.jpg'");
		
		@Override
		public void onTakePicture(byte[] data, CameraProxy camera) {
			// TODO Auto-generated method stub
			mJpegPictureCallbackTime = System.currentTimeMillis();
			if (mPostViewPictureCallbackTime != 0) {
                mShutterToPictureDisplayedTime =
                        mPostViewPictureCallbackTime - mShutterCallbackTime;
                mPictureDisplayedToJpegCallbackTime =
                        mJpegPictureCallbackTime - mPostViewPictureCallbackTime;
            } else {
                mShutterToPictureDisplayedTime =
                        mRawPictureCallbackTime - mShutterCallbackTime;
                mPictureDisplayedToJpegCallbackTime =
                        mJpegPictureCallbackTime - mRawPictureCallbackTime;
            }
            Log.v(Tag, "mPictureDisplayedToJpegCallbackTime = "
                    + mPictureDisplayedToJpegCallbackTime + "ms");
            SaveImgTask mSaveImgTask = new SaveImgTask(data);
            mSaveImgTask.execute();
            StartPreview();
		   //SaveImage(data);
		}
		public Uri SaveImage(byte[] data) {
			Date date = new Date();
			String filename = format.format(date);
			File fileFolder = new File(Environment.getExternalStorageDirectory() + mMainActivity.getResources().getString(R.string.photo_folder));
			if(!fileFolder.exists()){
				fileFolder.mkdir();
			}
			File photo = new File(fileFolder, filename);
			mLatestPhotoPath = photo.getAbsolutePath();
			Log.v(Tag, "mLatestPhotoPath "+mLatestPhotoPath);
			Uri uri = null;
			FileOutputStream photoOutputStream = null;
			try {
				photoOutputStream = new FileOutputStream(photo);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				photoOutputStream.write(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				photoOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return uri;
		}
		
		private class SaveImgTask extends AsyncTask<Void,Void,Uri>{

			private final byte[] data;
			public SaveImgTask(final byte[] data){
				this.data = data;
			}
			@Override
			protected Uri doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				return SaveImage(data);
			}
			@Override
			protected void onPostExecute(Uri result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			
		}
    
    }
    
	public void onCameraPaused(){
		mCameraProxy.stoppreview();
	}
	
	public void onCameraStoped(){
		mCameraProxy.releasecamera();
		mCameraProxy = null;
	}
		
	}
	


