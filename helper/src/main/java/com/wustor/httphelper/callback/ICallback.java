package com.wustor.httphelper.callback;


import com.wustor.httphelper.AppException;

import java.net.HttpURLConnection;

public interface ICallback<T> {

    void onSuccess(T result);

    void onFailure(AppException e);

    /**
     * invoked on sub thread
     * @param t serialized by SubCallbacks
     * @return final result by calling onSuccess(t)
     */
    T postRequest(T t);

    /**
     *
     * invoked on sub thread
     * @return if not null, will skip the http request, call {@link #onSuccess(Object)} directly
     */
    T preRequest();

    T parse(HttpURLConnection connection, ProgressListener listener) throws AppException;

    T parse(HttpURLConnection connection) throws AppException;

    void onProgressUpdated(int state, int curLen, int totalLen);

    void cancel();
}
