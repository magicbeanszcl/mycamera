package com.example.mycamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

public class CameraInformation {
	static final int NO_DEVICE = -1;
	private final Camera.CameraInfo[] mCameraInfors;
	private final int mCameraNums;
	private final int mBackCameraId;
	private final int mFrontCameraId;
	private static final String Tag = "CameraInformation";
	
	private CameraInformation(Camera.CameraInfo[] cameraInfors,int cameraNums,int backCameraId,int frontCameraId){
		mCameraInfors = cameraInfors;
		mCameraNums = cameraNums;
		mBackCameraId = backCameraId;
	    mFrontCameraId = frontCameraId;
	}
	
	public static CameraInformation creat() {	
		int cameraNums = 0;
		Camera.CameraInfo[] cameraInfors = null;
		try{
			cameraNums = Camera.getNumberOfCameras();
			cameraInfors = new Camera.CameraInfo[cameraNums];
			for(int i = 0;i<cameraNums;i++){
				cameraInfors[i] = new Camera.CameraInfo();
				Camera.getCameraInfo(i, cameraInfors[i]);
			}
		}catch(RuntimeException ex){
			
		}
		//mCameraNums = cameranums;
		int backCameraId = NO_DEVICE;
		int frontCameraId = NO_DEVICE;
		for(int i = cameraNums-1;i>=0;i--){
			if(cameraInfors[i].facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
				frontCameraId = i;
			}else {
				if(cameraInfors[i].facing == Camera.CameraInfo.CAMERA_FACING_BACK){
					backCameraId = i;
			    }
			}
		}
		return new CameraInformation(cameraInfors,cameraNums,backCameraId,frontCameraId);
	}
	
	public static int getDisplayRotation(Context context){
		WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay()
                .getRotation();
        switch (rotation) {
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
	public CurrentCamerainfor getCurrentCameraInfors(int cameraid){
		return new CurrentCamerainfor(mCameraInfors[cameraid]);
	}
	
	public boolean OrientationIsValid(int orientation){
		if(orientation % 90 != 0){
			Log.e(Tag, "display orientation is not disvisible by 90");
			return false;
		}
		if(orientation < 0 || orientation > 270){
			Log.e(Tag, "orientation is outside expected range");
			return false;
		}
		return true;
	}
	public class CurrentCamerainfor{
		Camera.CameraInfo mInfo;
		
		public CurrentCamerainfor(Camera.CameraInfo info){
			mInfo = info; 
		}
		
		public int getSensorOrientation(){
			return mInfo.orientation;
		}
		public boolean isfacingfront(){
			return mInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
		}
		public boolean isfacingback(){
			return mInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK;
		}
	}
			

}
