package com.example.mycamera;

import android.view.TextureView;
public interface PreviewStatusListener extends TextureView.SurfaceTextureListener {
      public void onPreviewAspectRatioChangedListener(float aspectRatio);
}
