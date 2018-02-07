package com.wustor.httphelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wustor.httphelper.callback.StringCallback;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = (TextView) findViewById(R.id.tv_response);
    }


    public void testGet(View view) {
        String url = "http://gank.io/api/data/Android/10/1";
        RequestManager requestManager = new RequestManager(url, RequestManager.RequestMethod.GET);
        requestManager.setCallback(new StringCallback() {
            @Override
            public void onSuccess(String result) {
                tvResult.setText(result);
            }

            @Override
            public void onFailure(AppException e) {
                tvResult.setText(e.toString());

            }
        });

        HttpHelper.getInstance().execute(this, requestManager);//发起请求
    }

    public void testPost(View view) {
        String url = "https://10.16.17.43:9002/Interface/getGuidePage";
        RequestManager requestManager = new RequestManager(url);
        requestManager.setCallback(new StringCallback() {
            @Override
            public void onSuccess(String result) {
                tvResult.setText(result);

            }

            @Override
            public void onFailure(AppException e) {
                tvResult.setText(e.toString());

            }
        });

        HttpHelper.getInstance().execute(this, requestManager);//发起请求
    }
}
