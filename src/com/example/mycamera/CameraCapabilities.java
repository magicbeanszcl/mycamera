package com.example.mycamera;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class CameraCapabilities {
protected final ArrayList<int[]> mSupportedPreviewFpsRange = new ArrayList<int[]>();
protected final ArrayList<Size> mSupportedPreviewSize = new ArrayList<Size>();
protected final TreeSet<Integer> mSupportedPreviewFormats = new TreeSet<Integer>();
protected final ArrayList<Size> mSupportedPictureSize = new ArrayList<Size>();
protected final TreeSet<Integer> mSupportedPictureFormats = new TreeSet<Integer>();

public CameraCapabilities(Parameters cameraParameters){	
	mSupportedPreviewFormats.addAll(cameraParameters.getSupportedPreviewFormats());
	mSupportedPictureFormats.addAll(cameraParameters.getSupportedPictureFormats());
	Createpreviewsize(cameraParameters);
	Createpicturesize(cameraParameters);
	//mSupportedPreviewSize = cameraParameters.getSupportedPreviewSizes();
}

private void Createpreviewsize(Parameters p){
	List<Camera.Size> SupportedPreviewSize = p.getSupportedPreviewSizes();
	if(SupportedPreviewSize != null){
		for(Camera.Size s : SupportedPreviewSize){
			mSupportedPreviewSize.add(new Size(s.width,s.height));
		}
	}
}

private void Createpicturesize(Parameters p){
	List<Camera.Size> Supportedpicturesize = p.getSupportedPictureSizes();
	if(Supportedpicturesize != null){
		for(Camera.Size s : Supportedpicturesize){
			mSupportedPictureSize.add(new Size(s.width,s.height));
		}
	}
}

public ArrayList<Size> getSupportedPreviewSize(){
	return mSupportedPreviewSize;
}

public ArrayList<Size> getSupportedPictureSize(){
	return mSupportedPictureSize;
}
}
