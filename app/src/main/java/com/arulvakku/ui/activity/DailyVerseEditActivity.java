package com.arulvakku.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.arulvakku.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;

public class DailyVerseEditActivity extends AppCompatActivity {

    private ColorPickerView mColorPickerView;
    private LinearLayout mShareLayout;
    private TextView mVerseTextView;
    private String mDailyVerse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_verse_edit);

        mDailyVerse = this.getIntent().getStringExtra("daily_verse");
        inflateXMLView();
    }

    private void inflateXMLView() {
        mColorPickerView = findViewById(R.id.color_picker_view);
        mShareLayout = findViewById(R.id.share_layout);
        mVerseTextView = findViewById(R.id.txt_daily_verse);
        mVerseTextView.setText(mDailyVerse);

        mColorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int selectedColor) {
                Log.d(DailyVerseEditActivity.class.getSimpleName(), Integer.toHexString(selectedColor));
                mShareLayout.setBackgroundColor(selectedColor);
            }
        });
    }
}