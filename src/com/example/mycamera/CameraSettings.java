package com.example.mycamera;

public class CameraSettings {
	protected Size mCurrentPreviewSize;
	protected Size mCurrentPictureSize;
	protected int mPreviewFpsRangeMin;
    protected int mPreviewFpsRangeMax;
    protected int mPreviewFrameRate;
    private int mCurrentPreviewFormat;
    protected Size mCurrentVideoSize;
    protected byte mJpegCompressQuality;
    protected int mCurrentPictureFormat;
    
    
    protected CameraSettings(CameraCapabilities mCapabilities){
    
    }
    protected CameraSettings(CameraSettings src){
    	mCurrentPreviewSize =
                (src.mCurrentPreviewSize == null ? null : new Size(src.mCurrentPreviewSize));
        mCurrentPreviewFormat = src.mCurrentPreviewFormat;
        mCurrentPictureSize =
                (src.mCurrentPictureSize == null ? null : new Size(src.mCurrentPictureSize));
        mCurrentVideoSize =
                (src.mCurrentVideoSize == null ? null : new Size(src.mCurrentVideoSize));
        mJpegCompressQuality = src.mJpegCompressQuality;
        mCurrentPictureFormat = src.mCurrentPictureFormat;
    }
    
    public  CameraSettings copy(){
    	return new CameraSettings(this);
    }
    
    public Size getCurrentPreviewSize() {
        return new Size(mCurrentPreviewSize);
    }
    
    public boolean setPreviewSize(Size previewSize) {
        /*if (mSizesLocked) {
            Log.w("CameraSettings", "Attempt to change preview size while locked");
            return false;
        }*/
        mCurrentPreviewSize = new Size(previewSize);
        return true;
    }
    public Size getCurrentPictureSize() {
        return new Size(mCurrentPictureSize);
    }
    
    public Size getCurrentVideoSize() {
        return new Size(mCurrentVideoSize);
    }
}
