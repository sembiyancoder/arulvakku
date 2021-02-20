package com.arulvakku.app.ui.home.frgament;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.arulvakku.R;
import com.arulvakku.app.ui.DailyVerseEditActivity;
import com.arulvakku.app.ui.ViewAllDailyVerseActivity;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.utils.UtilSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DailyVerseFragment extends Fragment implements View.OnClickListener {

    private UtilSingleton sInstance;

    public DailyVerseFragment() {

    }

    private TextView txtCurrentDate;
    private TextView txtVerseNo;
    private TextView txtVerse;
    private ImageView imgShare;
    private LinearLayout cardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sInstance = UtilSingleton.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_verse, container, false);
        txtCurrentDate = rootView.findViewById(R.id.txt_current_date);
        txtVerseNo = rootView.findViewById(R.id.txt_verse_no);
        txtVerse = rootView.findViewById(R.id.txt_verse);
        imgShare = rootView.findViewById(R.id.img_share);
        cardView = rootView.findViewById(R.id.daily_verse_layout);

        rootView.findViewById(R.id.text_view_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity(), ViewAllDailyVerseActivity.class);
                startActivity(mainIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (sInstance.getTodayDate() != null) {
            txtCurrentDate.setText(sInstance.getTodayDate());
        }

        setTodayVerse();
        imgShare.setOnClickListener(this);

    }

    private void defaultVerse() {
        txtVerseNo.setText(getString(R.string.default_chapter) + 5 + " : " + 41);
        txtVerse.setText(getString(R.string.default_verse));
    }


    public void shareDailyVerse() {
        imgShare.setVisibility(View.INVISIBLE);
        txtCurrentDate.setVisibility(View.VISIBLE);
        cardView.setDrawingCacheEnabled(true);
        cardView.buildDrawingCache();

        Bitmap bm = cardView.getDrawingCache();
        imgShare.setVisibility(View.VISIBLE);
        txtCurrentDate.setVisibility(View.GONE);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.arulvakku&hl=en");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "இன்றைய வசனம்.png");
        try {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.getAbsolutePath()));
        startActivity(Intent.createChooser(share, "Share Image"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_share:
                // shareDailyVerse();
                Intent mainIntent = new Intent(getActivity(), DailyVerseEditActivity.class);
                mainIntent.putExtra("daily_verse", txtVerse.getText().toString());
                startActivity(mainIntent);
                break;
        }
    }


    private void setTodayVerse() {
        String todayDate = sInstance.getDate();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = obj.optJSONArray("Result");
            DBHelper dbHelper = DBHelper.getInstance(getContext());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.optString("sVersesDate");
                if (date.equalsIgnoreCase(todayDate)) {
                    String verse_id = jsonObject.optString("sVerses");
                    String book_no = verse_id.substring(0, 2);
                    String chapter_no = verse_id.substring(2, 5);
                    String verse_no = verse_id.substring(5, 8);
                    String day_verse = dbHelper.getVerseDay(verse_id);
                    txtVerseNo.setText(sInstance.bookName[Integer.parseInt(book_no) - 1].trim() + " " + Integer.parseInt(chapter_no) + " : " + Integer.parseInt(verse_no));
                    txtVerse.setText(day_verse);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("daily_verses.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
