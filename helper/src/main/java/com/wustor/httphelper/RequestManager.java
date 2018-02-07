package com.wustor.httphelper;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.wustor.httphelper.callback.ICallback;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class RequestManager {
    String tag;//请求的tag
    public String filePath;
    private Future mFuture;
    private Context mContext;
    private RequestTask task;
    ICallback iCallback;
    volatile boolean isCompleted;
    private volatile boolean isDownLoad = false;
    static final int STATE_UPLOAD = 1;
    static final int STATE_DOWNLOAD = 2;
    public ArrayList<FileEntity> fileEntities;


    boolean enableProgressUpdated = false;

    public enum RequestMethod {GET, POST, PUT, DELETE}

    int maxCount = 3;
    public String url;
    public String content = "";
    volatile boolean isCancelled;
    public ArrayMap<String, String> headers;
    private ArrayMap<String, String> paraMap;
    public RequestMethod method = RequestMethod.POST;
    public static ArrayMap<String, String> globalHeaders=new ArrayMap<>();


    /**
     * @param url 请求的url
     */
    public RequestManager(String url) {
        this.url = url;
        this.method = RequestMethod.POST;
        initParaMap(null);
    }


    /**
     * @param url     请求url
     * @param paraMap 请求参数
     */
    public RequestManager(String url, ArrayMap<String, String> paraMap) {
        this.url = url;
        this.method = RequestMethod.POST;
        initParaMap(paraMap);
    }

    /**
     * @param url    请求url
     * @param method 请求方式
     */
    public RequestManager(String url, RequestMethod method) {
        this.url = url;
        this.method = method;
        initParaMap(paraMap);
    }

    /**
     * @param key   header的key
     * @param value header的value
     */
    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new ArrayMap<>();
        }
        headers.put(key, value);
    }


    public boolean isEnableProgressUpdated() {
        return enableProgressUpdated;
    }

    public void setEnableProgressUpdated(boolean enableProgressUpdated) {
        this.enableProgressUpdated = enableProgressUpdated;
    }

    /**
     * @param paramMap 初始化Map参数
     */
    private void initParaMap(ArrayMap<String, String> paramMap) {
        if (paramMap != null && !paramMap.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            content = sb.toString();
        }
    }


    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public ArrayMap<String, String> getParaMap() {
        return paraMap;
    }

    public boolean isDownLoad() {
        return isDownLoad;
    }

    public void setDownLoad(boolean downLoad) {
        isDownLoad = downLoad;
    }

    public void setCallback(ICallback iCallback) {
        this.iCallback = iCallback;
    }

    public void enableProgressUpdated(boolean enable) {
        this.enableProgressUpdated = enable;
    }


    public void checkIfCancelled() throws AppException {
        if (isCancelled) {
            throw new AppException(AppException.ErrorType.CANCEL, "the request has been cancelled");
        }
    }

    public void cancel(boolean force) {
        isCancelled = true;
        iCallback.cancel();
        if (force && task != null) {
            mFuture.cancel(force);
        }
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void execute(Context context, ThreadPoolExecutor executor) {
        task = new RequestTask(this, context);
        mFuture = executor.submit(task);

    }


}
