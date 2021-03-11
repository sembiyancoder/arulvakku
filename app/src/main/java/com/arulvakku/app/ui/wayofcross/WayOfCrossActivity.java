package com.arulvakku.app.ui.wayofcross;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.arulvakku.R;
import com.arulvakku.app.ui.wayofcross.adapter.CustomViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class WayOfCrossActivity extends AppCompatActivity {

    private CustomViewPagerAdapter mCustomViewPagerAdapter;
    private ViewPager mViewPager;
    private JSONArray mWayOfCrossArray;
    private ImageView mLeftArrowImageView, mRightArrowImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_way_of_cross);
        inflateXMLView();
    }

    private void inflateXMLView() {
        prepareData();
        mViewPager = findViewById(R.id.viewpager);
        mLeftArrowImageView = findViewById(R.id.image_left_arrow);
        mRightArrowImageView = findViewById(R.id.image_right_arrow);
        setViewPagerAdapter();

        mRightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });

        mLeftArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
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
    }


}