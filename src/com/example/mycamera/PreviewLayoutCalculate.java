package com.example.mycamera;

import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.Log;

public class PreviewLayoutCalculate implements PreviewStatusListener,CameraUI.NonDecorWindowSizeChangedListener{
	
	private float mAspectRatio = PreviewSurfaceControl.PREVIEW_ASPECT_RATIO;
	private AppUiLayoutPosition mAppUiLayoutPosition = null;
	private int mWindowWidth = 0;
    private int mWindowHeight = 0;
    private int mRotation = 0;
    private final static float DEFAULT_RATIO=4f/3f;
    private static float mConfiguredRatio=DEFAULT_RATIO;
    private final int mControlBarMinHeight;
    private final int mControlBarMaxHeight;
    private final int mControlBarOptimalHeight;
    private int mDisplayHeight;
    private int navigationBarHeight;
    private static String Tag = "PreviewLayoutCalculate";
    //private CameraServices mService;
    
    public PreviewLayoutCalculate(/*CameraServices cameraService,*/int ControlBarMinHeight, int ControlBarMaxHeight,
            int ControlBarOptimalHeight) {
        //mService=cameraService;
        mControlBarMinHeight = ControlBarMinHeight;
        mControlBarMaxHeight = ControlBarMaxHeight;
        mControlBarOptimalHeight = ControlBarOptimalHeight;
    }
    
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
			int arg2) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void onPreviewAspectRatioChangedListener(float aspectRatio) {
		// TODO Auto-generated method stub
		if(mAspectRatio == aspectRatio){
			return;
		}
		mAspectRatio = aspectRatio;
		updatepreriewlayout();
	}
	
	@Override
	public void onNonDecorWindowSizeChanged(int width,int height,int rotation){
		mWindowWidth = width;
		mWindowHeight = height;
		mRotation = rotation;
		Log.v(Tag,"mWindowWidth "+mWindowWidth+" mWindowHeight "+mWindowHeight+" mRotation "+mRotation);
		
	}
	
	public void updatepreriewlayout(){
		if (mWindowWidth == 0 || mWindowHeight == 0) {
            return;
        }
		mAppUiLayoutPosition = getAppUiLayoutPosition(mWindowWidth, mWindowHeight, mAspectRatio,
                mRotation);
	}
	
	public static final class AppUiLayoutPosition{
		public final RectF mPreviewRect = new RectF();
		public final RectF mControlBarRect = new RectF();
		public boolean mControlBarOverlay = false;
	}
	
	public AppUiLayoutPosition getAppUiLayoutPosition(int width,int height,float previewAspectRatio,int rotation){
		boolean landscape = width > height;

        // If the aspect ratio is defined as fill the screen, then preview should
        // take the screen rect.
		AppUiLayoutPosition config = new AppUiLayoutPosition();
//        config.mPreviewRect.set(0, 0, width, height);
        int longerEdge = Math.max(width, height);
        int shorterEdge = Math.min(width, height);

        int barStartCoord=(int)(shorterEdge*mConfiguredRatio);

        if (landscape) {
            config.mControlBarRect.set(barStartCoord, 0, width, height);
        } else {
            config.mControlBarRect.set(0, barStartCoord, width, height);
        }
        Log.v(Tag, "barStartCoord "+barStartCoord+" width "+width+" height "+height);
        if (previewAspectRatio == PreviewSurfaceControl.PREVIEW_ASPECT_RATIO) {
            config.mPreviewRect.set(0, 0, width, height);
            config.mControlBarOverlay = true;
//            if (landscape) {
//                config.mBottomBarRect.set(width - mBottomBarOptimalHeight, 0, width, height);
//            } else {
//                config.mBottomBarRect.set(0, height - mBottomBarOptimalHeight, width, height);
//            }
        } else {
            if (previewAspectRatio < 1) {
                previewAspectRatio = 1 / previewAspectRatio;
            }
            // Get the bottom bar width and height.
            float barSize;
//            int longerEdge = Math.max(width, height);
//            int shorterEdge = Math.min(width, height);

            // Check the remaining space if fit short edge.
            float spaceNeededAlongLongerEdge = shorterEdge * previewAspectRatio;
            float remainingSpaceAlongLongerEdge = longerEdge - spaceNeededAlongLongerEdge;

            float previewShorterEdge;
            float previewLongerEdge;
            if (remainingSpaceAlongLongerEdge <= 0) {
                // Preview aspect ratio > screen aspect ratio: fit longer edge.
                previewLongerEdge = longerEdge;
                previewShorterEdge = longerEdge / previewAspectRatio;
                barSize = mControlBarOptimalHeight;
                config.mControlBarOverlay = true;

                if (landscape) {
                    config.mPreviewRect.set(0, height / 2 - previewShorterEdge / 2, previewLongerEdge,
                            height / 2 + previewShorterEdge / 2);
//                    config.mBottomBarRect.set(width - barSize, height / 2 - previewShorterEdge / 2,
//                            width, height / 2 + previewShorterEdge / 2);
                } else {
                    config.mPreviewRect.set(width / 2 - previewShorterEdge / 2, 0,
                            width / 2 + previewShorterEdge / 2, previewLongerEdge);
                    config.mControlBarRect.set(0, barStartCoord, width, mDisplayHeight);
//                    config.mBottomBarRect.set(width / 2 - previewShorterEdge / 2, height - barSize,
//                            width / 2 + previewShorterEdge / 2, height);
                }
            } else if (previewAspectRatio > 14f / 9f) {
                // If the preview aspect ratio is large enough, simply offset the
                // preview to the bottom/right.
                // TODO: This logic needs some refinement.
                barSize = mControlBarOptimalHeight;
                previewShorterEdge = shorterEdge;
                previewLongerEdge = shorterEdge * previewAspectRatio;
                config.mControlBarOverlay = true;
                if (landscape) {
                    float right = width;
                    float left = right - previewLongerEdge;
                    if (left < navigationBarHeight) {
                        config.mPreviewRect.set(0, 0, right - navigationBarHeight, previewShorterEdge);
                    } else {
                        config.mPreviewRect.set(0, 0, right - left, previewShorterEdge);
                    }
//                    config.mBottomBarRect.set(width - barSize, 0, width, height);
                } else {
                    float bottom = height;
                    float top = bottom - previewLongerEdge;
                    if (top < navigationBarHeight) {
                        config.mPreviewRect.set(0, 0, previewShorterEdge, bottom - navigationBarHeight);
                    } else {
                        config.mPreviewRect.set(0, 0, previewShorterEdge, bottom - top);
                    }
//                    config.mBottomBarRect.set(0, height - barSize, width, height);
                }
            } else if (remainingSpaceAlongLongerEdge <= mControlBarMinHeight) {
                // Need to scale down the preview to fit in the space excluding the bottom bar.
                previewLongerEdge = longerEdge - mControlBarMinHeight;
                previewShorterEdge = previewLongerEdge / previewAspectRatio;
                barSize = mControlBarMinHeight;
                config.mControlBarOverlay = false;
                if (landscape) {
                    config.mPreviewRect.set(0, height / 2 - previewShorterEdge / 2, previewLongerEdge,
                            height / 2 + previewShorterEdge / 2);
//                    config.mBottomBarRect.set(width - barSize, height / 2 - previewShorterEdge / 2,
//                            width, height / 2 + previewShorterEdge / 2);
                } else {
                    config.mPreviewRect.set(width / 2 - previewShorterEdge / 2, 0,
                            width / 2 + previewShorterEdge / 2, previewLongerEdge);
//                    config.mBottomBarRect.set(width / 2 - previewShorterEdge / 2, height - barSize,
//                            width / 2 + previewShorterEdge / 2, height);
                }
            } else {
                // Fit shorter edge.
                barSize = remainingSpaceAlongLongerEdge <= mControlBarMaxHeight ?
                        remainingSpaceAlongLongerEdge : mControlBarMaxHeight;
                previewShorterEdge = shorterEdge;
                previewLongerEdge = shorterEdge * previewAspectRatio;
                config.mControlBarOverlay = false;
                if (landscape) {
                    float right = width - barSize;
                    float left = right - previewLongerEdge;
                    config.mPreviewRect.set(left, 0, right, previewShorterEdge);
//                    config.mBottomBarRect.set(width - barSize, 0, width, height);
                } else {
                    barSize = config.mControlBarRect.bottom - config.mControlBarRect.top;
                    float bottom = height - barSize;
                    float top = bottom - previewLongerEdge;
                    config.mPreviewRect.set(0, top, previewShorterEdge, bottom);
//                    config.mBottomBarRect.set(0, height - barSize, width, height);
                }
            }
        }

        /*if (rotation >= 180&&!mService.isReversibleEnabled()) {
            // Rotate 180 degrees.
            Matrix rotate = new Matrix();
            rotate.setRotate(180, width / 2, height / 2);

            rotate.mapRect(config.mPreviewRect);
            rotate.mapRect(config.mBottomBarRect);
        }*/

        // Round the rect first to avoid rounding errors later on.
        round(config.mControlBarRect);
        round(config.mPreviewRect);

        return config;
	}
	
	public RectF getPreviewRect(){
		if(mAppUiLayoutPosition == null){
			updatepreriewlayout();
		}
		if(mAppUiLayoutPosition == null){
			return new RectF();
		}
		return new RectF(mAppUiLayoutPosition.mPreviewRect);
	}
	
	public RectF getControlBarRect(){
		if(mAppUiLayoutPosition == null){
			updatepreriewlayout();
		}
		if(mAppUiLayoutPosition == null){
			return new RectF();
		}
		return new RectF(mAppUiLayoutPosition.mControlBarRect);
	}
	
	public static void round(RectF rect) {
        if (rect == null) {
            return;
        }
        float left = Math.round(rect.left);
        float top = Math.round(rect.top);
        float right = Math.round(rect.right);
        float bottom = Math.round(rect.bottom);
        rect.set(left, top, right, bottom);
    }
	
	public void setDisplayHeight(int height) {
        mDisplayHeight = height;
    }
	
	public void setNavigationBarHeight(int height) {
        navigationBarHeight = height;
    }

}
