package com.tigeroakes.tagit;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.json.*;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;

public class InfoActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private TextView mNFCTag;
    private TextView mNameTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String personalNFC = pref.getString( "personalNFC", ""); // contains the NFC info
        String name = pref.getString( "name", ""); // contains the name info

        mNFCTag = (TextView) findViewById(R.id.nfc_code);
        mNameTag = (TextView) findViewById(R.id.name_field);
        RequestParams requests = new RequestParams();

        requests.put("tag", personalNFC);
        requests.put("name", name);

        HttpUtils.get("create_user/", requests, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("asd", "Response: " + response);
            }
        });

        mNFCTag.setText(personalNFC);
        mNameTag.setText(name);

//        JSONObject obj = new JSONObject(" .... ");
//        String pageName = obj.getJSONObject("pageInfo").getString("pageName");
//
//        JSONArray arr = obj.getJSONArray("posts");
//        for (int i = 0; i < arr.length(); i++)
//        {
//            String post_id = arr.getJSONObject(i).getString("post_id");
//        }
    }




}
