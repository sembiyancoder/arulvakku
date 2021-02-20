package com.arulvakku.app.ui.prayer_request;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arulvakku.R;
import com.arulvakku.app.network.WebServiceHandler;
import com.arulvakku.app.utils.Constants;
import com.arulvakku.app.utils.CustomProgress;
import com.arulvakku.app.utils.NetworkErrorDialog;
import com.arulvakku.app.utils.UtilSingleton;
import com.arulvakku.app.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CreatePrayerRequestActivity extends AppCompatActivity implements View.OnClickListener {


    TextInputEditText mNameInputEditText, mPlaceEditText, mPrayerEditText;
    MaterialButton mPostMaterialButton;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private CustomProgress mCustomProgress;
    private JSONObject mJsonObject = new JSONObject();
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_prayer_request);
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("செப வேண்டுதல்");

        inflateXML();

        Intent intent = this.getIntent();
        if(intent!=null  && intent.hasExtra("request")){
            String request = intent.getStringExtra("request");
            try {
                mJsonObject = new JSONObject(request);
                mNameInputEditText.setText(mJsonObject.optString("Name"));
                mPlaceEditText.setText(mJsonObject.optString("Place"));
                mPrayerEditText.setText(mJsonObject.optString("PrayerRequest"));
                isEdit = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            isEdit = false;
        }


    }

    private void inflateXML() {
        mNameInputEditText = findViewById(R.id.edit_name);
        mPlaceEditText = findViewById(R.id.edit_place);
        mPrayerEditText = findViewById(R.id.edit_prayer_request);
        mPostMaterialButton = findViewById(R.id.btn_send);
        mPostMaterialButton.setOnClickListener(this);

        mNameInputEditText.addTextChangedListener(textWatcher);
        mPlaceEditText.addTextChangedListener(textWatcher);
        mPrayerEditText.addTextChangedListener(textWatcher);


        if(mSharedPreferences.getString("Email","")!=null && !mSharedPreferences.getString("Email","").isEmpty() ){
            mNameInputEditText.setText(mSharedPreferences.getString("Email",""));
        }
        checkFieldsForEmptyValues();
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
    }


    private JSONObject initRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", mNameInputEditText.getText().toString().trim());
            jsonObject.put("Place", mPlaceEditText.getText().toString().trim());
            jsonObject.put("DeviceId", mSharedPreferences.getString("ANDROID_ID", ""));
            jsonObject.put("PrayerRequest", mPrayerEditText.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject updateRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", mNameInputEditText.getText().toString().trim());
            jsonObject.put("Place", mPlaceEditText.getText().toString().trim());
            jsonObject.put("DeviceId", mSharedPreferences.getString("ANDROID_ID", ""));
            jsonObject.put("PrayerRequest", mPrayerEditText.getText().toString().trim());
            jsonObject.put("RequestId", mJsonObject.optString("RequestId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (!mPrayerEditText.getText().toString().isEmpty()) {
                    if (UtilSingleton.getInstance().isNetworkAvailable(this)) {
                        if(isEdit){
                            new UpdatePrayerRequest().execute();
                        }else{
                            new PostPrayersList().execute();
                        }

                    } else {
                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Prayer Request Filed Is Empty", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    private void checkFieldsForEmptyValues() {

        String name = mNameInputEditText.getText().toString();
        String place = mPlaceEditText.getText().toString();
        String prayer = mPrayerEditText.getText().toString();

        if (name.equals("") || place.equals("") || prayer.equals("")) {
            enableLoginButton(false);
        } else {
            enableLoginButton(true);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkFieldsForEmptyValues();
        }
    };


    private void enableLoginButton(boolean enable) {
        mPostMaterialButton.setEnabled(enable);
    }

    public class PostPrayersList extends AsyncTask<Void, Void, String> {

        public PostPrayersList() {

        }

        @Override
        protected void onPreExecute() {
            showLoadingDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            WebServiceHandler webService = WebServiceHandler.getInstance(getApplication());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), initRequest().toString());
            String jsonString = webService.postWebServiceCall(Constants.POST_PRAYER_REQUEST, requestBody);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null && response.length() > 0 && Utils.isValidJSON(response)) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.optBoolean("IsTransactionDone")) {
                        String result = jsonObject.optString("Result");

                        hideLoadingDialog();

                        onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    /*
     * Delete Prayer Request
     * */

    public class UpdatePrayerRequest extends AsyncTask<Void, Void, String> {

        public UpdatePrayerRequest() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            WebServiceHandler webService = WebServiceHandler.getInstance(getApplication());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), updateRequest().toString());
            String jsonString = webService.postWebServiceCall(Constants.POST_PRAYER_REQUEST, requestBody);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null && response.length() > 0 && Utils.isValidJSON(response)) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.optBoolean("IsTransactionDone")) {
                        String result = jsonObject.optString("Result");
                        onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
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


    /*
     * No internet connection error message
     * */
    private void showNetWorkErrorDialog() {
        NetworkErrorDialog networkErrorDialog = new NetworkErrorDialog(this);
        networkErrorDialog.showAlertDialog();
    }

}
