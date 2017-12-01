package com.wustor.httphelper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.wustor.httphelper.core.RequestTask;
import com.wustor.httphelper.entities.FileEntity;
import com.wustor.httphelper.error.AppException;
import com.wustor.httphelper.executor.SmartExecutor;
import com.wustor.httphelper.itf.ICallback;
import com.wustor.httphelper.itf.OnGlobalExceptionListener;
import com.wustor.httphelper.utils.HintUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class Request {
    public ICallback iCallback;

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private Context mContext;

    public boolean isEnableProgressUpdated() {
        return enableProgressUpdated;
    }

    public void setEnableProgressUpdated(boolean enableProgressUpdated) {
        this.enableProgressUpdated = enableProgressUpdated;
    }

    public boolean enableProgressUpdated = false;

    public boolean isDownLoad() {
        return isDownLoad;
    }

    public void setDownLoad(boolean downLoad) {
        isDownLoad = downLoad;
    }

    public boolean isDownLoad = false;
    public OnGlobalExceptionListener onGlobalExceptionListener;
    public String tag;
    private RequestTask task;
    public boolean isCompleted;
    private Future mFuture;
    public static final int STATE_UPLOAD = 1;
    public static final int STATE_DOWNLOAD = 2;
    public String filePath;
    public ArrayList<FileEntity> fileEntities;

    public void setCallback(ICallback iCallback) {
        this.iCallback = iCallback;
    }

    public void enableProgressUpdated(boolean enable) {
        this.enableProgressUpdated = enable;
    }

    public void setGlobalExceptionListener(OnGlobalExceptionListener onGlobalExceptionListener) {
        this.onGlobalExceptionListener = onGlobalExceptionListener;
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

    public void execute(Context context, SmartExecutor mExecutors) {
        if (context instanceof Activity)
            HintUtils.showDialog(context, "哈哈哈");
        task = new RequestTask(this, context);
        mFuture = mExecutors.submit(task);

    }

    public void execute(Context context, SmartExecutor mExecutors, boolean isShow) {
        task = new RequestTask(this, context);
        mFuture = mExecutors.submit(task);

    }


    public enum RequestMethod {GET, POST, PUT, DELETE}

    public enum RequestTool {OKHTTP, URLCONNECTION}

    public int maxRetryCount = 3;
    public int maxCount = 3;

    public void setUrl(String url) {
        this.url = url;
    }

    public String url;
    public String content = "";
    public Map<String, String> headers;

    public HashMap<String, String> getParaMap() {
        return paraMap;
    }

    public HashMap<String, String> paraMap;


    //设置请求参数
    public void setParamMap(HashMap<String, String> paramMap) {
        this.paraMap = paramMap;
        if (paramMap != null && !paramMap.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            content = sb.toString();
            Log.d("content---->", content);
        }
    }


    public volatile boolean isCancelled;
    public RequestMethod method;
    public RequestTool tool;

    public void setTool(RequestTool tool) {
        this.tool = tool;
    }

    public Request(String url, RequestMethod method) {
        this.url = url;
        this.method = method;
        this.tool = RequestTool.URLCONNECTION;
    }

    public Request(String url, RequestMethod method, RequestTool tool) {
        this.url = url;
        this.method = method;
        this.tool = tool;
    }

    public Request(String url) {
        this.url = url;
        this.method = RequestMethod.GET;
        this.tool = RequestTool.URLCONNECTION;
    }

    public Request(String url, RequestTool tool) {
        this.url = url;
        this.method = RequestMethod.GET;
        this.tool = tool;
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }
}
