package com.arulvakku.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.ui.adapter.PrayerRequestListAdapter;
import com.arulvakku.ui.network.WebServiceHandler;
import com.arulvakku.ui.utils.Constants;
import com.arulvakku.ui.utils.CustomProgress;
import com.arulvakku.ui.utils.NetworkErrorDialog;
import com.arulvakku.ui.utils.NetworkUtil;
import com.arulvakku.ui.utils.UtilSingleton;
import com.arulvakku.ui.utils.Utils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PrayerRequestActivity extends AppCompatActivity implements PrayerRequestListAdapter.onItemSelectedListener, View.OnClickListener {


    private PrayerRequestListAdapter mAdapter;
    private RecyclerView recyclerView;
    private JSONArray jsonArray;
    private JSONObject mJsonObject;
    private ExtendedFloatingActionButton mFloatingActionButton;
    private SharedPreferences mSharedPreferences;
    private TextView txtNoPrayer;
    private CustomProgress mCustomProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_request);
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("செப வேண்டுதல்");
        inflateXML();
    }

    private void inflateXML() {
        recyclerView = findViewById(R.id.recycler_view);
        txtNoPrayer = findViewById(R.id.txt_no_prayer);
        mFloatingActionButton = findViewById(R.id.fab);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mFloatingActionButton.setOnClickListener(this);
    }


    private void showLoadingDialog() {
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(this, "Please Wait", false);
            mCustomProgress.showProgressDialog();
        } else {
            mCustomProgress.showProgressDialog();
        }
    }

    private void hideLoadingDialog() {
        if (mCustomProgress != null) {
            mCustomProgress.hideProgressDialog();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (UtilSingleton.getInstance().isNetworkAvailable(this)) {
            new GetPrayersList().execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }


    /*
     * No internet connection error message
     * */
    private void showNetWorkErrorDialog() {
        NetworkErrorDialog networkErrorDialog = new NetworkErrorDialog(this);
        networkErrorDialog.showAlertDialog();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }


    private JSONObject initRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceId", mSharedPreferences.getString("ANDROID_ID", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject deleteRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RequestId", mJsonObject.optString("RequestId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent mainIntent = new Intent(this, CreatePrayerRequestActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDeletePrayer(JSONObject jsonObject) {
        mJsonObject = jsonObject;
        if (NetworkUtil.isOnline(this)) {
            new DeletePrayerRequest().execute();
        } else {
            showNetWorkErrorDialog();
        }
    }

    @Override
    public void onEditPrayer(JSONObject jsonObject) {
        mJsonObject = jsonObject;
        Intent mainIntent = new Intent(this, CreatePrayerRequestActivity.class);
        mainIntent.putExtra("request", jsonObject.toString());
        startActivity(mainIntent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }


    /*
     * Update Prayer Request
     * */

    private void setAdapter() {
        mAdapter = new PrayerRequestListAdapter(this, jsonArray, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        txtNoPrayer.setVisibility(View.GONE);
    }


    public class DeletePrayerRequest extends AsyncTask<Void, Void, String> {

        public DeletePrayerRequest() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            WebServiceHandler webService = WebServiceHandler.getInstance(getApplication());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), deleteRequest().toString());
            String jsonString = webService.postWebServiceCall(Constants.DELETE_PRAYER_REQUEST, requestBody);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null && response.length() > 0 && Utils.isValidJSON(response)) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (NetworkUtil.isOnline(PrayerRequestActivity.this)) {
                        new GetPrayersList().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public class GetPrayersList extends AsyncTask<Void, Void, String> {

        public GetPrayersList() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            WebServiceHandler webService = WebServiceHandler.getInstance(getApplication());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), initRequest().toString());
            String jsonString = webService.postWebServiceCall(Constants.GET_PRAYER_REQUEST_BY_DEVICE_ID, requestBody);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null && response.length() > 0 && Utils.isValidJSON(response)) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jsonArray = new JSONArray();
                    if (jsonObject.optBoolean("IsTransactionDone")) {
                        jsonArray = jsonObject.optJSONArray("Result");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            setAdapter();
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            txtNoPrayer.setVisibility(View.VISIBLE);
                        }
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        txtNoPrayer.setVisibility(View.VISIBLE);
                    }

                    hideLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
