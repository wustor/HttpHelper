package com.wustor.httphelper.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.URLUtil;

import com.wustor.httphelper.Request;
import com.wustor.httphelper.error.AppException;
import com.wustor.httphelper.itf.OnProgressUpdatedListener;
import com.wustor.httphelper.upload.UploadUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class HttpUrlConnectionUtil {
    public static HttpURLConnection execute(Request request, OnProgressUpdatedListener listener) throws AppException {
        if (!URLUtil.isNetworkUrl(request.url)) {
            throw new AppException(AppException.ErrorType.MANUAL, "the url :" + request.url + " is not valid");
        }
        switch (request.method) {
            case GET:
            case DELETE:
                return get(request);
            case POST:
            case PUT:
                return post(request, listener);
        }

        return null;
    }


    private static HttpURLConnection get(Request request) throws AppException {
        try {
            request.checkIfCancelled();
            HttpURLConnection connection = (HttpURLConnection) new URL(request.url).openConnection();
            connection.setRequestMethod(request.method.name());
            connection.setConnectTimeout(15 * 3000);
            connection.setReadTimeout(15 * 3000);
            addHeader(connection, request.headers, request.getContext());
            request.checkIfCancelled();
            return connection;
        } catch (InterruptedIOException e) {
            throw new AppException(AppException.ErrorType.TIMEOUT, e.getMessage());
        } catch (IOException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        }
    }


    private static HttpURLConnection post(Request request, OnProgressUpdatedListener listener) throws AppException {
        HttpURLConnection connection = null;
        OutputStream os = null;
        try {
            request.checkIfCancelled();
            // Create a trust manager that does not validate certificate chains
            // Install the all-trusting trust manager
            // 注意这部分一定要
            try {
                connection = (HttpURLConnection) new URL(request.url).openConnection();
                connection.setRequestMethod(request.method.name());
                connection.setConnectTimeout(15 * 3000);
                connection.setReadTimeout(15 * 3000);
                connection.setDoOutput(true);
                addHeader(connection, request.headers, request.getContext());
                request.checkIfCancelled();
                os = connection.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (request.filePath != null) {
                UploadUtil.upload(os, request.filePath);
            } else if (request.fileEntities != null) {
                UploadUtil.upload(os, request.content, request.fileEntities, listener);
            } else if (request.content != null) {
                if (os != null)
                    os.write(request.content.getBytes());
            } else {
                throw new AppException(AppException.ErrorType.MANUAL, "the post request has no post content");
            }

            request.checkIfCancelled();
        } catch (InterruptedIOException e) {
            throw new AppException(AppException.ErrorType.TIMEOUT, e.getMessage());
        } catch (IOException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }

            } catch (IOException e) {
                throw new AppException(AppException.ErrorType.IO, "the post outputstream can't be closed");
            }
        }

        return connection;
    }

    private static void addHeader(HttpURLConnection connection, Map<String, String> headers, Context context) {
//        if (headers == null || headers.size() == 0)
//            return;
        connection.addRequestProperty("applicationType", "ANDROID");
        String version = getString(context, "version", "");
        Log.d("version---->", version);
        connection.addRequestProperty("version", version);
//        for (Map.Entry<String, String> entry : headers.entrySet()) {
//            connection.addRequestProperty("applicationType", "android");
//        }
    }


    public static String getString(Context ctx, String key, String defaultValue) {
        if (ctx == null)
            return "";
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);

    }
}


