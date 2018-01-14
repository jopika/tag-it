package com.tigeroakes.tagit;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private TextView mNFCTag;
    private TextView mNameTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String personalNFC = pref.getString( "personalNFC", "");
        String name = pref.getString( "name", "");

        mNFCTag = (TextView) findViewById(R.id.nfc_code);
        mNameTag = (TextView) findViewById(R.id.name_field);

        mNFCTag.setText(personalNFC);
        mNameTag.setText(name);
    }
}
