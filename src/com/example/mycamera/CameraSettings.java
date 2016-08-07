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
    protected String mCurrentFocusmode;
    
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
        mCurrentFocusmode = src.mCurrentFocusmode;
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
    
    public boolean setPictureSize(Size picturesize){
    	mCurrentPictureSize = new Size(picturesize);
    	return true;
    }
    
    public void setFocusmode(String mfocusmode){
    	mCurrentFocusmode = mfocusmode;
    }
    
    public Size getCurrentPictureSize() {
        return new Size(mCurrentPictureSize);
    }
    
    public Size getCurrentVideoSize() {
        return new Size(mCurrentVideoSize);
    }
    
    public String getFocusmode(){
    	return mCurrentFocusmode;
    }
}
