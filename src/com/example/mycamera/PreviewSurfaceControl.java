package com.example.mycamera;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

@SuppressLint("NewApi") public class PreviewSurfaceControl implements OnLayoutChangeListener,TextureView.SurfaceTextureListener {
	private SurfaceTexture mSurfaceTexture;
    private int mWidth;
    private int mHeight;
    public static final float PREVIEW_ASPECT_RATIO = 0f;
    private float mAspectRatio = PREVIEW_ASPECT_RATIO;
    private int mOrientation = -1;
    private RectF mPreviewArea = new RectF();
    private boolean mAutoAdjustTransform = true;
    private final ArrayList<PreviewStatusListener> mPreviewAspectRatioChangedListener = new ArrayList<PreviewStatusListener>();
    private PreviewLayoutCalculate mPreviewLayoutCalculate = null;
    private TextureView mPreview;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener;
    private static final String Tag = "PreviewSurfaceControl";
    
    public PreviewSurfaceControl(TextureView preview, PreviewLayoutCalculate previewLayoutCalculate) {
		//super(context, attrs);
		// TODO Auto-generated constructor stub
    	mPreview = preview;
    	mPreviewLayoutCalculate = previewLayoutCalculate;
    	mSurfaceTexture = new SurfaceTexture(0);
        mSurfaceTexture.detachFromGLContext();
        mPreview.addOnLayoutChangeListener(this);
    	mPreview.setSurfaceTextureListener(this);
    	
	}

    //public void initPreviewSurfacetexture(PreviewLayoutCalculate previewlayoutcalculate){
    //	mPreviewLayoutCalculate = previewlayoutcalculate;
   // }
    public void updatePreviewAspectRatio(float aspectratio){
    	if(aspectratio <= 0){
    		Log.e(Tag, "aspectratio is invalid");
    		return;
    	}
    	if(aspectratio < 1f){
    		aspectratio = 1f/aspectratio;
    	}
    	Log.w(Tag, "aspectratio "+aspectratio+" mWidth "+mWidth+" mHeight "+mHeight);
    	setPreviewAspectRatio(aspectratio);
    	updateTransform();
    }
    
    public void setPreviewAspectRatio(float aspectratio){
    	if(mAspectRatio != aspectratio){
    		mAspectRatio = aspectratio;
    		onAspectRatioChanged();
    	}
    }
    
    public void addAspectRatioChangedListener(PreviewStatusListener listener){
    	if(listener != null && !mPreviewAspectRatioChangedListener.contains(listener)){
    		mPreviewAspectRatioChangedListener.add(listener);
    	}
    }
    public void onAspectRatioChanged(){
    	mPreviewLayoutCalculate.onPreviewAspectRatioChangedListener(mAspectRatio);
    	/*for(PreviewStatusListener listener : mPreviewAspectRatioChangedListener){
    		listener.onPreviewAspectRatioChangedListener(mAspectRatio);
    	}*/
    }
    
    public boolean updateTransform(){
    	
    	if (!mAutoAdjustTransform) {
            return false;
        }
    	
        if (mAspectRatio == PREVIEW_ASPECT_RATIO || mAspectRatio < 0 || mWidth == 0 || mHeight == 0) {
            return true;
        }

        Matrix matrix;
        //int cameraId = mCameraProvider.getCurrentCameraId();
        //if (cameraId >= 0) {
            //CameraDeviceInfo.Characteristics info = mCameraProvider.getCharacteristics(cameraId);
        RectF previewrect = mPreviewLayoutCalculate.getPreviewRect();
        matrix = getPreviewTransform(mOrientation, new RectF(0, 0, mWidth, mHeight),previewrect);
        
        //} else {
        Log.w(Tag, "previewrect"+previewrect);
        //    matrix = new Matrix();
        //}

        mPreview.setTransform(matrix);
        //updatePreviewArea(matrix);
        return true;
    }
    
    public Matrix getPreviewTransform(int orientation,RectF src,RectF dst){
    	if(src.equals(dst)){
    		return new Matrix();
    	}
    	Matrix transform = new Matrix();
    	transform.setRectToRect(src, dst, Matrix.ScaleToFit.FILL);
    	return transform;
    }
    
    public SurfaceTexture getsurfacetexture(){
		return mSurfaceTexture;
	}
    
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		mSurfaceTexture = surface;
		mWidth = width;
		mHeight = height;
		mSurfaceTextureListener.onSurfaceTextureAvailable(surface, mWidth, mHeight);
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

	public void clearTransform() {
        mPreview.setTransform(new Matrix());
        mPreviewArea.set(0, 0, mWidth, mHeight);
        //onPreviewAreaChanged(mPreviewArea);
        setAspectRatio(PREVIEW_ASPECT_RATIO);
    }
	
	private void setAspectRatio(float aspectRatio) {
        Log.v(Tag, "setAspectRatio: " + aspectRatio);
        if (mAspectRatio != aspectRatio) {
            Log.v(Tag, "aspect ratio changed from: " + mAspectRatio);
            mAspectRatio = aspectRatio;
            onAspectRatioChanged();
        }
    }
	
	@Override
	public void onLayoutChange(View v, int left, int top, int right,
			int bottom, int oldleft, int oldtop, int oldright, int oldbottom) {
		// TODO Auto-generated method stub
		int width = right - left;
        int height = bottom - top;
        int rotation = CameraInformation.getDisplayRotation(mPreview.getContext());
        if (mWidth != width || mHeight != height || mOrientation != rotation) {
            mWidth = width;
            mHeight = height;
            mOrientation = rotation;
            if (!updateTransform()) {
                clearTransform();
            }
        }
        Log.v(Tag, "width "+mWidth+" mHeight "+mHeight);
        //if (mOnLayoutChangeListener != null) {
        //    mOnLayoutChangeListener.onLayoutChange(v, left, top, right, bottom, oldLeft, oldTop,
        //            oldRight, oldBottom);
        //}
	}

	public void setSurfaceTextureListener(TextureView.SurfaceTextureListener listener) {
		// TODO Auto-generated method stub
		mSurfaceTextureListener = listener;
		if(mSurfaceTexture != null){
			onSurfaceTextureAvailable(mSurfaceTexture,0,0);
			mPreview.setSurfaceTexture(mSurfaceTexture);
		}
	}
	

}
