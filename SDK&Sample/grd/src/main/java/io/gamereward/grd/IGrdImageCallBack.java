package io.gamereward.grd;

import android.graphics.Bitmap;

public interface IGrdImageCallBack {
    void OnFinished(int error, String data, Bitmap bitmap);
}
