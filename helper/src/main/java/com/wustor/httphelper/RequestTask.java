package com.wustor.httphelper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.wustor.httphelper.callback.ProgressListener;
import com.wustor.httphelper.util.HintUtils;
import com.wustor.httphelper.util.OKHttpUtil;

import java.io.IOException;
import java.net.HttpURLConnection;


public class RequestTask implements Runnable {
    //切换线程的handler
    private Handler handler = new Handler(Looper.getMainLooper());
    private RequestManager requestManager;


    public RequestTask(RequestManager requestManager, Context context) {
        this.requestManager = requestManager;
        requestManager.setContext(context);
    }

    //上传或者下载
    public Object request(int retry) {
        try {
            HttpURLConnection connection = null;
            connection = OKHttpUtil.execute(requestManager, !requestManager.enableProgressUpdated ? null : new ProgressListener() {
                @Override
                public void onProgressUpdated(int curLen, int totalLen) {
                    onProgressUpdate(RequestManager.STATE_UPLOAD, curLen, totalLen);
                }
            });
            if (connection != null) {
                try {
                    connection.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestManager.isDownLoad()) {
                return requestManager.iCallback.parse(connection, new ProgressListener() {
                    @Override
                    public void onProgressUpdated(int curLen, int totalLen) {
                        onProgressUpdate(RequestManager.STATE_DOWNLOAD, curLen, totalLen);
                    }
                });
            } else {
                return requestManager.iCallback.parse(connection);
            }
        } catch (AppException e) {
            Log.d("error--->", e.getMessage());
            if (e.type == AppException.ErrorType.TIMEOUT) {
                if (retry < requestManager.maxCount) {
                    retry++;
                    return request(retry);
                }
            }
            return e;
        }
    }


    //上传或者下载的进度回调
    private void onProgressUpdate(Integer... values) {
        requestManager.iCallback.onProgressUpdated(values[0], values[1], values[2]);
    }


    @Override
    public void run() {
        if (requestManager.iCallback != null) {
            final Object o = requestManager.iCallback.preRequest();
            if (o != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HintUtils.closeDialog();
                        if (o instanceof AppException)
                            requestManager.iCallback.onFailure((AppException) o);
                        else
                            requestManager.iCallback.onSuccess(o);
                    }
                });
            } else {
                final Object o1 = request(0);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HintUtils.closeDialog();
                        if (o1 instanceof AppException)
                            requestManager.iCallback.onFailure((AppException) o1);
                        else
                            requestManager.iCallback.onSuccess(o1);

                    }
                });

            }
        }

    }
}
