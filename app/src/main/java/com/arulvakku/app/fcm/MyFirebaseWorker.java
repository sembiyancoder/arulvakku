package com.arulvakku.app.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.arulvakku.R;
import com.arulvakku.app.network.WebServiceHandler;
import com.arulvakku.app.utils.Constants;
import com.arulvakku.app.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MyFirebaseWorker extends Worker {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public MyFirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        sharedPreferences = context.getSharedPreferences(getApplicationContext().getString(R.string.app_name), 0);
    }

    @NonNull
    @Override
    public Result doWork() {
      //  new PostPrayersList().execute();
        return null;
    }

    private JSONObject initRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FirebaseKey", sharedPreferences.getString("FCM_TOKEN", ""));
            jsonObject.put("DeviceId", sharedPreferences.getString("ANDROID_ID", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public class PostPrayersList extends AsyncTask<Void, Void, String> {

        public PostPrayersList() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            WebServiceHandler webService = WebServiceHandler.getInstance(context);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), initRequest().toString());
            String jsonString = webService.postWebServiceCall(Constants.UPDATE_FIREBASE_KEY, requestBody);
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}