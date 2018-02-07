package com.wustor.httphelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wustor.httphelper.callback.StringCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRequest();
    }

    private void initRequest() {
        String url = "https://10.16.17.43:9002/Interface/getGuidePage";
        RequestManager requestManager = new RequestManager(url);
        requestManager.setCallback(new StringCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("d---->", result);
            }

            @Override
            public void onFailure(AppException e) {
                Log.d("d---->", e.toString());

            }
        });

        HttpHelper.getInstance().execute(this, requestManager);//发起请求
    }


}
