package com.tigeroakes.tagit;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.net.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cz.msebera.android.httpclient.Header;

public class ScanActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private ImageView mArtifactImage;
    private TextView mArtifactName;
    private TextView mArtifactDescription;
    private TextView mPartDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mArtifactImage = (ImageView) findViewById(R.id.artifactImage);
        mArtifactName = (TextView) findViewById(R.id.artifactName);
        mArtifactDescription = (TextView) findViewById(R.id.artifactDescription);
        mPartDescription = (TextView) findViewById(R.id.partDescription);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String personalNFC = pref.getString( "personalNFC", "");
        String superTagNFC = pref.getString( "superTagNFC", "");

        // TODO HTTP request to get item from super tag

        RequestParams params = new RequestParams();
        params.put("userTag",personalNFC);
        params.put("targetTag", superTagNFC);
        Log.d("tag_request", "userTag: " + personalNFC);
        Log.d("tag_request", "targetTag: " + superTagNFC);

        HttpUtils.get("tagged/", params, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("tag_request", "Response: " + response);
                try {
                    mArtifactName.setText(response.getString("name"));
                    mArtifactDescription.setText(response.getString("description"));
//                    mArtifactImage.setImageBitmap(getImageBitmap(response.getString("photoUrl").replace("\\", "")));
                    mPartDescription.setText(response.getString("collectibleName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

       //TODO parse json array and set each field

        //mArtifactImage.set??
//        mArtifactName.setText("");
//        mArtifactDescription.setText("");



//        JSONObject obj = new JSONObject(" .... ");
//        String pageName = obj.getJSONObject("pageInfo").getString("pageName");
//
//        JSONArray arr = obj.getJSONArray("posts");
//        for (int i = 0; i < arr.length(); i++)
//        {
//            String post_id = arr.getJSONObject(i).getString("post_id");
//        }
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("Bitmap", "Error getting bitmap", e);
        }
        return bm;
    }
}
