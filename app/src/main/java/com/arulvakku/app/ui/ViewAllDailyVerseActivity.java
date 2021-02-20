package com.arulvakku.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.adapter.DailyVerseListAdapter;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.model.DailyVerse;
import com.arulvakku.app.utils.UtilSingleton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.activeandroid.Cache.getContext;

public class ViewAllDailyVerseActivity extends AppCompatActivity implements DailyVerseListAdapter.onItemSelectedListener {

    private RecyclerView mRecyclerView;
    private List<DailyVerse> mDailyVerseList = new ArrayList<>();
    private DailyVerseListAdapter mAdapter;
    private DBHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_daily_verse);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("அனைத்து தினசரி வசனங்களும்");
        mDbHelper = DBHelper.getInstance(this);
        inflateXMlView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prepareAllVerses();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new DailyVerseListAdapter(this, mDailyVerseList,this);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void inflateXMlView() {
        mRecyclerView = findViewById(R.id.recyclerview);
    }

    private void prepareAllVerses() {
        UtilSingleton singleton = UtilSingleton.getInstance();
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        List<String> stringList = getDates("01012020", "31122020");
        if (stringList != null && stringList.size() > 0) {
            for (int index = 0; index < stringList.size(); index++) {
                String date = stringList.get(index);
                String fullVerseNo = singleton.getVerse(date);

                if (date != null && !date.isEmpty() && fullVerseNo != null && !fullVerseNo.isEmpty()) {
                    String bookNo = fullVerseNo.substring(0, 2);
                    String chapterNo = fullVerseNo.substring(2, 5);
                    String verseNo = fullVerseNo.substring(5, 8);

                    if (bookNo != null && !bookNo.isEmpty() && fullVerseNo != null && !fullVerseNo.isEmpty() && chapterNo != null && !chapterNo.isEmpty() && verseNo != null && !verseNo.isEmpty()) {
                        DailyVerse dailyVerse = new DailyVerse();
                        String tempVerseInfo = singleton.bookName[Integer.parseInt(bookNo) - 1].trim() + " " + Integer.parseInt(chapterNo) + " : " + Integer.parseInt(verseNo);
                        dailyVerse.setmVerseInfo(tempVerseInfo);
                        dailyVerse.setmDate(date);
                        dailyVerse.setnVerseFullNo(fullVerseNo);
                        mDailyVerseList.add(dailyVerse);
                    }
                }
            }
        }
    }


    private static List<String> getDates(String dateString1, String dateString2) {
        ArrayList<String> dates = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("ddMMyyyy");

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            String dateTime = dateFormat.format(cal1.getTime());
            dates.add(dateTime);
            cal1.add(Calendar.DATE, 1);
        }


        return dates;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShareVerse(DailyVerse verse) {

        String share_verse = verse.getmVerseInfo() + "\n" + mDbHelper.getVerseDay(verse.getnVerseFullNo());

        if (share_verse != null && !share_verse.isEmpty()) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_verse + "\n\n" + "https://play.google.com/store/apps/details?id=com.arulvakku&hl=en");
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
        }
    }
}
