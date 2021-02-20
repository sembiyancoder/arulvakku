package com.arulvakku.app.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arulvakku.R;
import com.arulvakku.app.network.WebServiceHandler;
import com.arulvakku.app.ui.home.HomeActivity;
import com.arulvakku.app.utils.Constants;
import com.arulvakku.app.utils.UtilSingleton;
import com.arulvakku.app.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RESOLVE_HINT = 1001;
    private MaterialButton mRegisterButton, mSkipButton;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private TextInputEditText mEmailInputEditText, mPhoneTextInputEditText;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), 0);

        mRegisterButton = findViewById(R.id.btn_register);

        mEmailInputEditText = findViewById(R.id.edit_email);
        mPhoneTextInputEditText = findViewById(R.id.verse_edit_text);

        mEmailInputEditText.addTextChangedListener(textWatcher);
        mEmailInputEditText.addTextChangedListener(textWatcher);

        mSkipButton = findViewById(R.id.btn_skip);

        mRegisterButton.setOnClickListener(this);
        mSkipButton.setOnClickListener(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        mPhoneTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    requestPhoneNumber();
                }

            }
        });

        checkFieldsForEmptyValues();

    }

    private void checkFieldsForEmptyValues() {
        String username = mEmailInputEditText.getText().toString();
        String password = mPhoneTextInputEditText.getText().toString();
        if (username.equals("") || password.equals("")) {
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
        mRegisterButton.setEnabled(enable);
    }

    protected void requestPhoneNumber() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    String mobNumber = credential.getId();
                    mPhoneTextInputEditText.setText(mobNumber);
                    checkFieldsForEmptyValues();
                    //textView.setText(mobNumber);

                } else {
                    // No phone number available
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (UtilSingleton.getInstance().isNetworkAvailable(this)) {
                    new PostRegistrationAsync().execute();
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_skip:

                mEditor = mSharedPreferences.edit();
                mEditor.putBoolean("IsLoggedIn", true);
                mEditor.commit();


                Intent mainIntent = new Intent(this, HomeActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
        }
    }

    private JSONObject initRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserId", mSharedPreferences.getString("ANDROID_ID", ""));
            jsonObject.put("CountryCode", mPhoneTextInputEditText.getText().toString());
            jsonObject.put("MobileNumber", mPhoneTextInputEditText.getText().toString());
            jsonObject.put("MailId", mEmailInputEditText.getText().toString());
            jsonObject.put("FirebaseKey", mSharedPreferences.getString("FCM_TOKEN", ""));
            jsonObject.put("DeviceId", mSharedPreferences.getString("ANDROID_ID", ""));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public class PostRegistrationAsync extends AsyncTask<Void, Void, String> {

        public PostRegistrationAsync() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            WebServiceHandler webService = WebServiceHandler.getInstance(LoginActivity.this);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), initRequest().toString());
            String jsonString = webService.postWebServiceCall(Constants.INSERT_USER, requestBody);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null && response.length() > 0 && Utils.isValidJSON(response)) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.optBoolean("IsTransactionDone")) {

                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean("IsLoggedIn", true);
                        mEditor.commit();

                        mSharedPreferences.edit();
                        mEditor.putString("Email",mEmailInputEditText.getText().toString());
                        mEditor.commit();


                        Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
