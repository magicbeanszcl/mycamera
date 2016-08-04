package com.example.mycamera;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class CameraAgent {
CameraHandler mCameraHandler;
HandlerThread mCameraHandlerThread;
CameraCapabilities mCameraCapabilities;
private static final int CAMERA_HAL_API_VERSION_1_0 = 0X100;
private static final String Tag = "CameraAgent";
public CameraAgent(){
	mCameraHandlerThread = new HandlerThread("Camera Handler Thread");
	mCameraHandlerThread.start();
	mCameraHandler = new CameraHandler(this,mCameraHandlerThread.getLooper());
}

public void OpenCamera(final int camid,final Handler handler,final CameraOpenCallback cb) {
	try{
	mCameraHandler.obtainMessage(CameraActions.OPEN_CAMERA, camid, 0, CreateAgentCallback(handler,cb)).sendToTarget();
	Log.v("CameraAgent", "request open camera send");
	}catch(final RuntimeException ex){
		Log.e("CameraAgent", "request open camera failed");
	}
}
public CameraOpenCallbackAgent CreateAgentCallback(Handler h,final CameraOpenCallback cb) {
	return new CameraOpenCallbackAgent(h,cb);
}
public static class CameraOpenCallbackAgent implements CameraOpenCallback {
	private Handler mHandler;
	private CameraOpenCallback mCallback;
	private CameraOpenCallbackAgent(Handler h,CameraOpenCallback cb){
		mHandler = h;
		mCallback = cb;
	}
	@Override
	public void onCameraOpened(final CameraProxy camera) {
		// TODO Auto-generated method stub
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mCallback.onCameraOpened(camera);
			}
			
		});
	}
	
}

public static class StartPreviewCallbackAgent implements StartPreviewCallback {
	private Handler mHandler;
	private StartPreviewCallback mCallback;
	private StartPreviewCallbackAgent(Handler h,final StartPreviewCallback cb){
		mHandler = h;
		mCallback = cb;
	}
	@Override
	public void onPreviewStarted() {
		// TODO Auto-generated method stub
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mCallback.onPreviewStarted();
			}
			
		});
	}
	public static StartPreviewCallbackAgent CreateAgentCallback(Handler h,final StartPreviewCallback cb) {
		return new StartPreviewCallbackAgent(h,cb);
	}
}

public static class CameraShutterCallbackAgent implements ShutterCallback{

	private final Handler handler;
	private final CameraShutterCallback shuttercallback;
	private final CameraProxy mCamera;
	public CameraShutterCallbackAgent(Handler h,CameraProxy camera,CameraShutterCallback cb){
		handler = h;
		shuttercallback = cb;
		mCamera = camera;
	}
	
	@Override
	public void onShutter() {
		// TODO Auto-generated method stub
		handler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				shuttercallback.onShutter(mCamera);
			}
			
		});
	}
	
	public static CameraShutterCallbackAgent CreatAgentCallback(Handler h,CameraProxy camera,CameraShutterCallback cb){
		return new CameraShutterCallbackAgent(h,camera,cb);
	}
	
}

public static class TakePictureCallbackAgent implements PictureCallback{

	private final Handler handler;
	private final TakePictureCallback picturecallback;
	private final CameraProxy mCamera;
	public TakePictureCallbackAgent(Handler h,CameraProxy camera,TakePictureCallback cb){
		handler = h;
		picturecallback = cb;
		mCamera = camera;
	}
	@Override
	public void onPictureTaken(final byte[] data, final android.hardware.Camera camera) {
		// TODO Auto-generated method stub
		handler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				picturecallback.onTakePicture(data,mCamera);
			}
			
		});
	}
	
	public static TakePictureCallbackAgent CreatAgentCallback(Handler h,CameraProxy camera,TakePictureCallback cb){
		return new TakePictureCallbackAgent(h,camera,cb);
	}
	
}
class CameraHandler extends Handler implements Camera.ErrorCallback {
	private CameraAgent mAgent;
	private Camera mCamera;
	private int mCameraId = -1;
	private CameraParameters mCameraParameters;
	private Context mContext;
    CameraHandler(CameraAgent agent,Looper looper){
	    super(looper);
	    mAgent = agent;	
    }
    
	@Override
	public void onError(int errorCode, Camera camera) {
		// TODO Auto-generated method stub
		Log.e("CameraAgent", "camera error!");
	}	
    @Override
	public void handleMessage(final Message msg) {
		super.handleMessage(msg);
		int cameraAction = msg.what;
		try{
			switch(cameraAction) {
			    case CameraActions.OPEN_CAMERA: {
			    	final CameraOpenCallback openCallback = (CameraOpenCallback)msg.obj;
			    	final int cameraid = msg.arg1;
			    	Method OpenCamera = null;
			    	try{
			    		OpenCamera = Class.forName("android.hardware.Camera").getMethod("openLegacy",int.class,int.class);
			    		mCamera = (android.hardware.Camera)OpenCamera.invoke(null,cameraid,CAMERA_HAL_API_VERSION_1_0);
			    	}catch(NoSuchMethodException|IllegalAccessException|InvocationTargetException|ClassNotFoundException e){
			    		mCamera = android.hardware.Camera.open(cameraid);
			    	}
			    	if(mCamera != null) {
			    		mCamera.setErrorCallback(this);
			    		mCameraParameters = new CameraParameters(mCamera);
			    		mCameraCapabilities = new CameraCapabilities(mCameraParameters.getParametersSynch());
			    		CameraProxy cameraproxy = new CameraProxy(mAgent,mCamera);
			    		if(openCallback != null)
			    			openCallback.onCameraOpened(cameraproxy);
			    		Log.v("handleMessage", "onCameraOpened done!");
			    	}
			    	break;	
			    }
			    case CameraActions.SET_PREVIEW_SURFACETEXTURE:{
			    	
			    	if(msg.obj != null) {
						try {
							mCamera.setPreviewTexture((SurfaceTexture) msg.obj);
							Log.v("handleMessage", "setPreviewTexture done!");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			    	else {
						Log.e("MycameraMainActivity", "SurfaceTexture not ready!");
						return;
						}
			    	break;
			    }
			    case CameraActions.SET_DISPLAY_ORIENTATION:{
			    	mCamera.setDisplayOrientation(msg.arg1);
			    	Log.v("handleMessage", "setDisplayOrientation");
			    	break;
			    }
			    case CameraActions.APPLY_SETTINGS:{
			    	Parameters parameters = mCameraParameters.getParametersSynch();
			    	CameraSettings settings = (CameraSettings)msg.obj;
			    	applySettingsToParameters(settings,parameters);
			    	mCamera.setParameters(parameters);
			    	mCameraParameters.invalidate();
			    	break;
			    }
			    case CameraActions.START_PREVIEW:{
			    	Log.v("handleMessage", "start preview begin!");
					mCamera.startPreview();
					Log.v("handleMessage", "start preview done!");
					StartPreviewCallback previewstartedcallback = (StartPreviewCallback)msg.obj;
					previewstartedcallback.onPreviewStarted();
					break;
			    }
			    case CameraActions.AUTO_FOCUS:{
			    	mCamera.autoFocus((AutoFocusCallback)msg.obj);
			    	break;
			    }
			    case CameraActions.TAKE_PICTURE:{
			    	CaptureCallback capturecb = (CaptureCallback)msg.obj;
			    	mCamera.takePicture(capturecb.mShutter, capturecb.mRaw, capturecb.mPost, capturecb.mJpeg);
			    	break;
			    }
			    case CameraActions.STOP_PREVIEW:{
			    	mCamera.stopPreview();
			    	Log.v("handleMessage", "stop preview done!");
			    }
			    case CameraActions.RELEASE:{
			    	mCamera.release();
			    	Log.v("handleMessage", "release Camera!");
			    }
			
			}
		}catch(final RuntimeException ex){
			
		}
	}
    
    private void applySettingsToParameters(final CameraSettings settings,final Parameters parameters){
    	Size photoSize = settings.getCurrentPictureSize();
        //parameters.setPictureSize(photoSize.width(), photoSize.height());
        Size videoSize = settings.getCurrentVideoSize();
        Size previewSize = settings.getCurrentPreviewSize();
        parameters.setPreviewSize(previewSize.width(), previewSize.height());
        parameters.setPictureSize(photoSize.width(), photoSize.height());
    }
    
	public class CaptureCallback{
		public final ShutterCallback mShutter;
		public final PictureCallback mRaw;
		public final PictureCallback mPost;
		public final PictureCallback mJpeg;
		CaptureCallback(ShutterCallback shutter,PictureCallback raw,PictureCallback post,PictureCallback jpeg){
			mShutter = shutter;
			mRaw = raw;
			mPost = post;
			mJpeg = jpeg;
		}
	}
	public CaptureCallback getCaptureCallback(ShutterCallback shutter,PictureCallback raw,PictureCallback post,PictureCallback jpeg){
		return new CaptureCallback(shutter,raw,post,jpeg);
	}
}
public class CameraProxy{
	Camera mCamera;
	CameraAgent mAgent;
	
	CameraProxy(CameraAgent agent,Camera camera){
		mCamera = camera;
		mAgent = agent;
	}
	
	public void setSurfaceTexture(SurfaceTexture surfacetexture) {
		try{
			mCameraHandler.obtainMessage(CameraActions.SET_PREVIEW_SURFACETEXTURE,surfacetexture).sendToTarget();
			Log.v("CameraProxy", "start setSurfaceTexture send");
			}catch(final RuntimeException ex){
				Log.e("CameraAgent", "request open camera failed");
			}
	}
	
	public void setDisplayOrientation(int rotation){
		try{
		    mCameraHandler.obtainMessage(CameraActions.SET_DISPLAY_ORIENTATION,rotation,0).sendToTarget();
		    }catch(final RuntimeException ex){
			
		}
	}
	
	public boolean applySettings(CameraSettings settings){
		if(settings == null){
			return false;
		}
		final CameraSettings copyOfSettings = settings.copy();
		try{
			mCameraHandler.obtainMessage(CameraActions.APPLY_SETTINGS,copyOfSettings).sendToTarget();
			Log.v("CameraProxy", "applySettings");
			}catch(final RuntimeException ex){
				
			}
		return true;
	}
	
	public void startpreview(Handler h,final StartPreviewCallback cb){
		try{
		    mCameraHandler.obtainMessage(CameraActions.START_PREVIEW,StartPreviewCallbackAgent.CreateAgentCallback(h,cb)).sendToTarget();
		    Log.v("CameraProxy", "start preview send");
		}catch(final RuntimeException ex){
			
		}
	}
	
	public void autofocus(final Handler h,final AutofocusCallback cb){
		
		final AutoFocusCallback autofocuscb = new AutoFocusCallback(){
			@Override
			public void onAutoFocus(boolean arg0, Camera arg1) {
				// TODO Auto-generated method stub
				h.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						cb.onAutofocus();
					}
					
				});
			}
			
		};
		try{
			mCameraHandler.obtainMessage(CameraActions.AUTO_FOCUS,autofocuscb).sendToTarget();
		}catch(final RuntimeException ex){
			
		}
	}
	
	public void takepicture(Handler h, final CameraShutterCallback shuttercb,
			final TakePictureCallback raw,TakePictureCallback post,TakePictureCallback jpeg) {
		Log.v(Tag, "takepicture");
		final CameraHandler.CaptureCallback capturecallback = mCameraHandler.getCaptureCallback(CameraShutterCallbackAgent.CreatAgentCallback(h, this, shuttercb),
				                                                                                TakePictureCallbackAgent.CreatAgentCallback(h, this, raw), 
				                                                                                TakePictureCallbackAgent.CreatAgentCallback(h, this, post), 
				                                                                                TakePictureCallbackAgent.CreatAgentCallback(h, this, jpeg));
		try{
			mCameraHandler.obtainMessage(CameraActions.TAKE_PICTURE,capturecallback).sendToTarget();	
	    }catch(final RuntimeException ex){
	    	
	    }
	}
	public void stoppreview(){
		try{
			mCameraHandler.obtainMessage(CameraActions.STOP_PREVIEW).sendToTarget();	
	    }catch(final RuntimeException ex){
	    	
	    }
	}
	
	public void releasecamera(){
		try{
			mCameraHandler.obtainMessage(CameraActions.RELEASE).sendToTarget();
		}catch(final RuntimeException ex){
			
		}
	}
	
	public void reconnectcamera(final int camid,final Handler handler,final CameraOpenCallback cb){
		try{
			mCameraHandler.obtainMessage(CameraActions.OPEN_CAMERA, camid, 0, CreateAgentCallback(handler,cb)).sendToTarget();
			Log.v("CameraAgent", "reconnect camera send");
			}catch(final RuntimeException ex){
				Log.e("CameraAgent", "reconnect camera failed");
			}
	}
	public CameraSettings getCameraSettings(){
		return new CameraSettings(mCameraCapabilities);
	}
	
	public CameraCapabilities getCameraCapabilities(){
		return mCameraCapabilities;
	}
} 

public class CameraParameters{
	Camera mCamera;
	Parameters mParameters;
	public CameraParameters(Camera camera){
		mCamera = camera;
	}
	public synchronized Parameters getParametersSynch(){
		
		if(mParameters == null){
			mParameters = mCamera.getParameters();
			if(mParameters == null){
				Log.e("CameraParameters", "Camera return null parameters!");
				throw new IllegalStateException("Camera return null parameters");
			}
		}
	    return mParameters;
	}
	public synchronized void invalidate(){
		mParameters = null;
	}
}

public static interface CameraOpenCallback{
	public void onCameraOpened(final CameraProxy cameraproxy);
}

public static interface StartPreviewCallback{
	public void onPreviewStarted();
}

public static interface AutofocusCallback{
	public void onAutofocus();
}

public static interface CameraShutterCallback{
	public void onShutter(CameraProxy mCamera);
}
public static interface TakePictureCallback{
	public void onTakePicture(byte [] data,CameraProxy camera);
}

}
