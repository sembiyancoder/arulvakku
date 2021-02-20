package com.arulvakku.app.ui.wayofcross;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.arulvakku.R;
import com.arulvakku.app.ui.wayofcross.adapter.CustomViewPagerAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class WayOfCrossActivity extends AppCompatActivity {

    private CustomViewPagerAdapter mCustomViewPagerAdapter;
    private ViewPager mViewPager;
    private JSONArray mWayOfCrossArray;
    private TextView mDescriptionTextView, mPrayerTextView;
    private TextView mTitleTextView;
    BottomSheetBehavior mBottomSheetBehavior;
    LinearLayout mLayoutBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_way_of_cross);
      /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("திருச்சிலுவைப்பாதை");*/
        inflateXMLView();
    }

    private void inflateXMLView() {
        prepareData();
        mLayoutBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mTitleTextView = findViewById(R.id.txt_title);
        mDescriptionTextView = findViewById(R.id.txt_description);
        mPrayerTextView = findViewById(R.id.txt_prayer);
        mViewPager = findViewById(R.id.viewpager);

        setViewPagerAdapter();

        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void prepareData() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            mWayOfCrossArray = obj.optJSONArray("WayOfTheCrossDetails");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("way_of_the_cross.json");
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


    private void setViewPagerAdapter() {
        mCustomViewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager(), mWayOfCrossArray, mWayOfCrossArray.length());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mCustomViewPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setDetails(0);

    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            setDetails(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

    };

    private void setDetails(int pos) {
        JSONObject jsonObject = mWayOfCrossArray.optJSONObject(pos);
        if (jsonObject != null) {
            mDescriptionTextView.setText(jsonObject.optString("Description"));
            mPrayerTextView.setText(jsonObject.optString("Prayer"));
        }

    }


}