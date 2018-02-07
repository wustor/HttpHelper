package com.wustor.httphelper.util;

import android.webkit.URLUtil;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.wustor.httphelper.AppException;
import com.wustor.httphelper.NullHostNameVerifier;
import com.wustor.httphelper.RequestManager;
import com.wustor.httphelper.callback.ProgressListener;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class OKHttpUtil {
    private static OkHttpClient mClient;

    public synchronized static HttpURLConnection execute(RequestManager requestManager, ProgressListener listener) throws AppException {
        if (!URLUtil.isNetworkUrl(requestManager.url)) {
            throw new AppException(AppException.ErrorType.MANUAL, "the url :" + requestManager.url + " is not valid");
        }
        if (mClient == null) {
            initializeOkHttp();
        }
        switch (requestManager.method) {
            case GET:
            case DELETE:
                return get(requestManager);
            case POST:
            case PUT:
                return post(requestManager, listener);
        }

        return null;
    }

    private static void initializeOkHttp() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }


                }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            mClient = new OkHttpClient();
            mClient.setSslSocketFactory(sc.getSocketFactory());
            mClient.setHostnameVerifier(new NullHostNameVerifier());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static HttpURLConnection get(RequestManager requestManager) throws AppException {
        try {
            requestManager.checkIfCancelled();
            HttpURLConnection connection = new OkUrlFactory(mClient).open(new URL(requestManager.url));
            connection.setRequestMethod(requestManager.method.name());
            connection.setConnectTimeout(15 * 3000);
            connection.setReadTimeout(15 * 3000);
            addHeader(connection, requestManager.headers);
            requestManager.checkIfCancelled();
            return connection;
        } catch (MalformedURLException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        } catch (ProtocolException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        }
    }


    private static HttpURLConnection post(RequestManager requestManager, ProgressListener listener) throws AppException {
        HttpURLConnection connection = null;
        OutputStream os = null;
        try {
            requestManager.checkIfCancelled();
            connection = new OkUrlFactory(mClient).open(new URL(requestManager.url));
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
            connection.setRequestMethod(requestManager.method.name());
            connection.setConnectTimeout(15 * 3000);
            connection.setReadTimeout(15 * 3000);
            connection.setDoOutput(true);

            addHeader(connection, requestManager.headers);
            requestManager.checkIfCancelled();


            os = connection.getOutputStream();
            if (requestManager.filePath != null) {
                UploadUtil.upload(os, requestManager.filePath);
            } else if (requestManager.fileEntities != null) {
                UploadUtil.upload(os, requestManager.content, requestManager.fileEntities, listener);
            } else if (requestManager.content != null) {
                os.write(requestManager.content.getBytes());
            } else {
                throw new AppException(AppException.ErrorType.MANUAL, "the post requestManager has no post content");
            }

            requestManager.checkIfCancelled();
        } catch (InterruptedIOException e) {
            throw new AppException(AppException.ErrorType.TIMEOUT, e.getMessage());
        } catch (IOException e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                throw new AppException(AppException.ErrorType.IO, "the post outputstream can't be closed");
            }
        }

        return connection;
    }

    private static void addHeader(HttpURLConnection connection, android.support.v4.util.ArrayMap<String, String> headers) {
        if (headers == null || headers.size() == 0)
            return;

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
