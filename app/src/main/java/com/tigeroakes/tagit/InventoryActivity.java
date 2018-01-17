package com.tigeroakes.tagit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;


public class InventoryActivity extends AppCompatActivity {
    public static final String TAG = "NfcDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private SharedPreferences pref;
    private TextView mNFCTag;
    private TextView mNameTag;
    private NfcAdapter mNfcAdapter;
    ListView listview;
    public String[] foody;
    public ArrayList<String> inventory_list = new ArrayList<>();

    public InventoryActivity instance = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (instance == null) {
            instance = this;
        }
        setContentView(R.layout.activity_inventory);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String personalNFC = pref.getString("personalNFC", "");
        String name = pref.getString("name", "");
        listview = (ListView) findViewById(R.id.listView);
        foody = new String[]{};
                final String[] foodyS = {"pizza", "burger", "chocolate", "ice-cream", "banana", "apple"};
        inventory_list = new ArrayList<>();

        RequestParams requests = new RequestParams();
        requests.put("tag", personalNFC);
        requests.put("name", name);

        HttpUtils.get("create_user/", requests, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("asd", "Hello world");
                Log.d("asd", "Response: " + response);
            }
        });

        RequestParams getInventoryParams = new RequestParams();
        getInventoryParams.put("user_id", personalNFC);

        Log.d("NFC", personalNFC);

        HttpUtils.get("get_inventory/", getInventoryParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                System.out.println("Hello world");
                Log.d("http_getInv", "Response: " + response);
                for(int i = 0; i < response.length(); i++) {
                    try {
                        inventory_list.add(response.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                inventory_list.add(" ");
                Log.d(TAG, "onSuccess: " + inventory_list);
               foody =  inventory_list.toArray(foody);
                Log.d(TAG, "onSuccess:test  " + foody[0]
                );
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(instance, R.layout.list_view_row, R.id.listText, foody);

                listview.setAdapter(adapter);
                System.out.println("Goodbye World");
//                listview.invalidate();
            }
            
            @Override
            public void onFailure(int a, Header[] b, Throwable c, JSONObject d) {
                Log.d(TAG, "onFailure: " + d);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("Hello object");
                Log.d("http_getInv", "Response: " + response);
//                inventory_list.toArray(foody);
            }
        });



//        HttpUtils.get("get_inventory/", getInventoryParams, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d("http_getInv", "Response: " + response);
//                System.out.println(response);
//            }
//        });

//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_view_row, R.id.listText, foody);
//
//        listview.setAdapter(adapter);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());
//        Log.d("Foody: ", foody.toString());

    }

//        mNFCTag = (TextView) findViewById(R.id.nfc_code);
//        mNameTag = (TextView) findViewById(R.id.name_field);
//
//        mNFCTag.setText(personalNFC);
//        mNameTag.setText(name);
//        @Override
//        public boolean onCreateOptionsMenu(Menu menu) {
//            // Inflate the menu; this adds items to the action bar if it is present.
//            getMenuInflater().inflate(R.menu.main, menu);
//            return true;
//        }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Log.d(TAG, "TESTTAG: tag" + tag);

                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);

                    break;
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }



    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                Log.d(TAG, "TESTTAG: NDEF is not supported by this Tag");

                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {

                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {

            byte[] payload = record.getPayload();

            return new String(payload);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                // TODO result is the nfc code scanned
                Log.d(TAG, "TESTTAG: KILL ME");
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("superTagNFC", result);
                editor.apply();

                Intent goToNextActivity = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(goToNextActivity);
            }
        }


    }

}
