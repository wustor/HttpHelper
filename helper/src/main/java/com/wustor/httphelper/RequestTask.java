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
    private Request request;



    public RequestTask(Request request, Context context) {
        this.request = request;
        request.setContext(context);
    }

    //上传或者下载
    public Object request(int retry) {
        try {
//                FIXME: for HttpUrlConnection
            HttpURLConnection connection = null;
            connection = OKHttpUtil.execute(request, !request.enableProgressUpdated ? null : new ProgressListener() {
                @Override
                public void onProgressUpdated(int curLen, int totalLen) {
                    onProgressUpdate(Request.STATE_UPLOAD, curLen, totalLen);
                }
            });
            if (connection != null) {
                try {
                    int responseCode = connection.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (request.isDownLoad()) {
                return request.iCallback.parse(connection, new ProgressListener() {
                    @Override
                    public void onProgressUpdated(int curLen, int totalLen) {
                        onProgressUpdate(Request.STATE_DOWNLOAD, curLen, totalLen);
                    }
                });
            } else {
                return request.iCallback.parse(connection);
            }
        } catch (AppException e) {
            Log.d("error--->", e.getMessage());
            if (e.type == AppException.ErrorType.TIMEOUT) {
                if (retry < request.maxCount) {
                    retry++;
                    return request(retry);
                }
            }
            return e;
        }
    }


    //上传或者下载的进度回调
    private void onProgressUpdate(Integer... values) {
        request.iCallback.onProgressUpdated(values[0], values[1], values[2]);
    }


    @Override
    public void run() {
        if (request.iCallback != null) {
            final Object o = request.iCallback.preRequest();
            if (o != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HintUtils.closeDialog();
                        if (o instanceof AppException)
                            request.iCallback.onFailure((AppException) o);
                        else
                            request.iCallback.onSuccess(o);
                    }
                });
            } else {
                final Object o1 = request(0);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HintUtils.closeDialog();
                        if (o1 instanceof AppException)
                            request.iCallback.onFailure((AppException) o1);
                        else
                            request.iCallback.onSuccess(o1);

                    }
                });

            }
        }

    }
}