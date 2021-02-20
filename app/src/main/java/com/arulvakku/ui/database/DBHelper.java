package com.arulvakku.ui.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arulvakku.ui.model.BookModel;
import com.arulvakku.ui.model.SearchWord;
import com.arulvakku.ui.model.Verses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "";
    private static String DB_NAME = "MyDataBase.db";
    private static DBHelper sInstance;
    private final Context mContext;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 10);
        DB_PATH = context.getDatabasePath(DB_NAME).getAbsolutePath();
        this.mContext = context;
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public void createDataBase() throws IOException {
        this.getReadableDatabase();
        copyDataBase();
        this.close();
    }


    public boolean checkDataBase() {
        File DbFile = new File(DB_PATH);
        boolean status = DbFile.exists();
        return DbFile.exists();
    }


    public void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String filePath = mContext.getDatabasePath(DB_NAME).getAbsolutePath();
        OutputStream mOutput = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(buffer)) > 0) {
            mOutput.write(buffer, 0, mLength);
        }
        mOutput.flush();
        mInput.close();
        mOutput.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }


    public List<Verses> getVerse(int book_id) {

        List<Verses> versesList = new ArrayList<Verses>();

        Verses verses = null;
        String query = "select substr(field1,1,2) AS book_id,cast(substr(field1,3,3) as int) AS chapter_id,cast(substr(field1,6,3) as int) AS verse_id,field2,field1,field3 from t_mybibleview where cast(substr(field1,1,2)as int)='" + book_id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String verse = cursor.getString(cursor.getColumnIndex("field2")).trim().replaceFirst("^0+(?!$*bb)", "");
                    String bookid = cursor.getString(cursor.getColumnIndex("book_id"));
                    String chapterid = cursor.getString(cursor.getColumnIndex("chapter_id"));
                    String verse_id = cursor.getString(cursor.getColumnIndex("verse_id"));
                    String full_id = cursor.getString(cursor.getColumnIndex("field1")).trim();
                    String verse_type = cursor.getString(cursor.getColumnIndex("field3")).trim();

                    String verse_replaced = verse.replace("␢", "").replace("*", "").replace("⦅", "").replace("⦆", "").replace("⁽", "").replace("⁾", "").replace("⦃", "").replace("⦄", "").replace("⒯", "").replace("* ␢", "").replace(" ␢", "").replace("⒣", "").replace("§", "");

                    if (!verse_replaced.equals("Same as above")) {
                        verses = new Verses();
                        verses.setVerse_id(verse_id);
                        verses.setVerse(verse_replaced);
                        verses.setChapter_id(chapterid);
                        verses.setBook_id(bookid);
                        verses.setFull_id(full_id);
                        verses.setVerse_type(verse_type);
                        versesList.add(verses);
                    } else {

                    }
                } while (cursor.moveToNext());
            }
        }
        return versesList;
    }

    public List<BookModel> getOldTestament() {
        List<BookModel> bookModelList = new ArrayList<BookModel>();
        String query = "select * from tbl_old_testament";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String bookName = cursor.getString(cursor.getColumnIndex("field4"));
                    String bookNo = cursor.getString(cursor.getColumnIndex("field1"));
                    String count = cursor.getString(cursor.getColumnIndex("count"));

                    BookModel model = new BookModel();
                    model.setChapter_name(bookName);
                    model.setChapter_id(bookNo);
                    model.setChapter_count(count);
                    bookModelList.add(model);
                } while (cursor.moveToNext());
            }
        }
        return bookModelList;
    }

    public List<BookModel> getNewTestament() {
        List<BookModel> bookModelList = new ArrayList<BookModel>();
        String query = "select * from tbl_new_testament";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String bookName = cursor.getString(cursor.getColumnIndex("field4"));
                    String bookNo = cursor.getString(cursor.getColumnIndex("field1"));
                    String count = cursor.getString(cursor.getColumnIndex("count"));
                    BookModel model = new BookModel();
                    model.setChapter_name(bookName);
                    model.setChapter_count(count);
                    model.setChapter_id(bookNo);
                    bookModelList.add(model);
                } while (cursor.moveToNext());
            }
        }
        return bookModelList;
    }


    public String getVerseDay(String id) {
        String today_verse = null;
        String query = "SELECT * FROM t_mybibleview  WHERE field1 = " + "'" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    today_verse = cursor.getString(cursor.getColumnIndex("field2")).replace("␢", "").replace("⦅", "").replace("⦆", "").replace("⁽", "").replace("⁾", "").replace("⦃", "").replace("⦄", "").replace("* ␢", "").replace(" ␢", "");
                } while (cursor.moveToNext());
            }
        }
        return today_verse;
    }

    public int getCount(String book_id, String chapter_id) {
        int count = 0;
        String query = "select count(*) from t_mybibleview where cast(substr(field1,1,2)as int)='" + book_id + "'" + " and cast(substr(field1,3,3)as int) < '" + chapter_id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        count = cursor.getInt(0);
        return count;
    }


    public List<SearchWord> searchWord(String name) {
        List<SearchWord> searchWordArrayList = new ArrayList<SearchWord>();
        SearchWord searchResult = null;
        String query = " SELECT * FROM t_mybibleview WHERE field2 LIKE \"%" + name.trim() + "%\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String verse = cursor.getString(cursor.getColumnIndex("field2")).replace("␢", "").replace("⦅", "").replace("⦆", "").replace("⁽", "").replace("⁾", "").replace("⦃", "").replace("⦄", "");
                    String fullId = cursor.getString(cursor.getColumnIndex("field1")).trim();
                    searchResult = new SearchWord();
                    searchResult.setId(fullId);
                    searchResult.setVerses(verse);
                    searchWordArrayList.add(searchResult);
                } while (cursor.moveToNext());
            }
        }

        return searchWordArrayList;
    }

}