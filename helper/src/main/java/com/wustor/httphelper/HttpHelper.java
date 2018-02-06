package com.wustor.httphelper;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.wustor.httphelper.util.ExecutorUtil;
import com.wustor.httphelper.util.HintUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by fatchao
 * 日期  2017-08-10.
 * 邮箱  fat_chao@163.com
 */
public class HttpHelper {
    private ThreadPoolExecutor executor;
    private volatile static HttpHelper httpHelper;
    private ArrayMap<String, ArrayList<RequestManager>> mCachedRequest;

    private HttpHelper() {
        mCachedRequest = new ArrayMap<>();
        if (executor == null) {
            executor = ExecutorUtil.createThreadPool();
        }
    }

    public static HttpHelper getInstance() {
        if (httpHelper == null) {
            synchronized (HttpHelper.class) {
                if (httpHelper == null)
                    httpHelper = new HttpHelper();
            }
        }
        return httpHelper;
    }


    public void execute(Context context, RequestManager requestManager) {
        requestManager.execute(context, executor);
        HintUtils.showDialog(context, "");
        if (!mCachedRequest.containsKey(requestManager.tag)) {
            ArrayList<RequestManager> requestManagers = new ArrayList<>();
            mCachedRequest.put(requestManager.tag, requestManagers);
        }
        mCachedRequest.get(requestManager.tag).add(requestManager);
    }

    public void execute(Context context, RequestManager requestManager, boolean isShow) {
        if (isShow)
            HintUtils.showDialog(context, "");
        requestManager.execute(context, executor);

        if (!mCachedRequest.containsKey(requestManager.tag)) {
            ArrayList<RequestManager> requestManagers = new ArrayList<>();
            mCachedRequest.put(requestManager.tag, requestManagers);
        }
        mCachedRequest.get(requestManager.tag).add(requestManager);
    }

    /**
     * @param tag 请求的tag
     */
    public void cancel(String tag) {
        if (TextUtils.isEmpty(tag))
            return;
        if (mCachedRequest.containsKey(tag)) {
            ArrayList<RequestManager> requestManagers = mCachedRequest.remove(tag);
            for (RequestManager requestManager : requestManagers) {
                if (!requestManager.isCompleted && !requestManager.isCancelled) {
                    requestManager.cancel(true);
                }
            }
        }

    }

    public void cancelAll() {
        for (Map.Entry<String, ArrayList<RequestManager>> entry : mCachedRequest.entrySet()) {
            ArrayList<RequestManager> requestManagers = entry.getValue();
            for (RequestManager requestManager : requestManagers) {
                if (!requestManager.isCompleted && !requestManager.isCancelled) {
                    requestManager.cancel(true);
                }
            }
        }
        mCachedRequest.clear();
    }


}
