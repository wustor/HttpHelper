package com.wustor.httphelper.callback;


public interface ProgressListener {
    void onProgressUpdated(int curLen, int totalLen);
}
