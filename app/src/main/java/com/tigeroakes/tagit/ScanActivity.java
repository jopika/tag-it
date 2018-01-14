package com.tigeroakes.tagit;

import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ScanActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private ImageView mArtifactImage;
    private TextView mArtifactName;
    private TextView mArtifactDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mArtifactImage = (ImageView) findViewById(R.id.artifactImage);
        mArtifactName = (TextView) findViewById(R.id.artifactName);
        mArtifactDescription = (TextView) findViewById(R.id.artifactDescription);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String personalNFC = pref.getString( "personalNFC", "");
        String superTagNFC = pref.getString( "superTagNFC", "");

        // TODO HTTP request to get item from super tag

       //TODO parse json array and set each field

        //mArtifactImage.set??
        mArtifactName.setText("");
        mArtifactDescription.setText("");


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
