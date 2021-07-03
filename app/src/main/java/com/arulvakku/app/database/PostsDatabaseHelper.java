package com.arulvakku.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.arulvakku.app.model.Bookmark;
import com.arulvakku.app.model.Note;
import com.arulvakku.app.model.Notes;
import com.arulvakku.app.model.Notification;

import java.util.ArrayList;
import java.util.List;


public class PostsDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "postsDatabase";
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_POSTS = "fcm_notification";
    private static final String TABLE_NOTES = "tbl_notes";


    // Note Table Columns
    private static final String TABLE_NOTE = "tbl_note";
    private static final String NOTE_ID = "id";
    private static final String NOTE_CONTENT = "note_message";
    private static final String NOTE_TITLE = "note_title";
    private static final String NOTE_BOOK_ID = "book_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String NOTE_SELECTED_VERSE = "selected_verse";

    String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NOTE + "(" +
            NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            NOTE_CONTENT + " TEXT," +
            NOTE_SELECTED_VERSE + " TEXT," +
            NOTE_BOOK_ID + " INTEGER," +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
            NOTE_TITLE + " TEXT" + ")";

    //bookmark
    private static final String TABLE_NOTE_BOOKMARK = "tbl_bookmark";
    private static final String BOOKMARK_ID = "id";
    private static final String BOOKMARK_BOOK_ID = "book_id";
    private static final String BOOKMARK_SELECTED_VERSE = "selected_verse";


    String CREATE_BOOKMARK_TABLE = "CREATE TABLE " + TABLE_NOTE_BOOKMARK + "(" +
            BOOKMARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            BOOKMARK_BOOK_ID + " INTEGER," +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
            BOOKMARK_SELECTED_VERSE + " TEXT" + ")";


    // Post Table Columns
    private static final String KEY_NOTIFICATION_ID = "id";
    private static final String KEY_NOTIFICATION_MESSAGE = "alert_message";
    private static final String KEY_NOTIFICATION_TITLE = "alert_title";
    private static final String KEY_TIME = "time";

    String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_POSTS + "(" +
            KEY_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_NOTIFICATION_TITLE + " TEXT," +
            KEY_TIME + " INTEGER," +
            KEY_NOTIFICATION_MESSAGE + " TEXT," +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private static PostsDatabaseHelper sInstance;

    public static synchronized PostsDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PostsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public PostsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTIFICATION_TABLE);
        db.execSQL(CREATE_NOTE_TABLE);
        db.execSQL(CREATE_BOOKMARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
            onCreate(db);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE_BOOKMARK);
            onCreate(db);
        }
    }


    public long insertVersesNote(int book_id, String title, String message, String selected_verse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        values.put(NOTE_BOOK_ID, book_id);
        values.put(NOTE_TITLE, title);
        values.put(NOTE_CONTENT, message);
        values.put(NOTE_SELECTED_VERSE, selected_verse);
        long id = db.insert(TABLE_NOTE, null, values);
        db.close();
        return id;
    }

    public long addBookmark(int book_id, String selected_verse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        values.put(BOOKMARK_BOOK_ID, book_id);
        values.put(BOOKMARK_SELECTED_VERSE, selected_verse);
        long id = db.insert(TABLE_NOTE_BOOKMARK, null, values);
        db.close();
        return id;
    }


    public int getNotesCount(int book_id) {
        String countQuery = "SELECT  * FROM " + TABLE_NOTE + " WHERE book_id = " + book_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getBookmarkCount(int book_id) {
        String countQuery = "SELECT  * FROM " + TABLE_NOTE_BOOKMARK + " WHERE book_id = " + book_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE, note.getNote());

        // updating row
        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }


    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?", new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public void deleteBookmark(Bookmark bookmark) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE_BOOKMARK, BOOKMARK_SELECTED_VERSE + " = ?", new String[]{String.valueOf(bookmark.getVerse())});
        db.close();
    }

    public void deleteNotes(Notes note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, NOTE_SELECTED_VERSE + " = ?", new String[]{String.valueOf(note.getVerse())});
        db.close();
    }


    public void addNotification(Notification notification) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NOTIFICATION_TITLE, notification.getmTitle());
            values.put(KEY_NOTIFICATION_MESSAGE, notification.getmMessage());
            values.put(KEY_TIME, notification.getmTime());
            db.insertOrThrow(TABLE_POSTS, null, values);
            db.setTransactionSuccessful();
            Log.d(PostsDatabaseHelper.class.getSimpleName(), "Inserted Successfully");
        } catch (Exception e) {
            Log.d(PostsDatabaseHelper.class.getSimpleName(), "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public List<Notes> getChapterNotes(int book_id) {
        List<Notes> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE + " WHERE book_id = " + book_id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Notes note = new Notes();
                note.setId(cursor.getString(cursor.getColumnIndex(NOTE_ID)));
                note.setNotes(cursor.getString(cursor.getColumnIndex(NOTE_CONTENT)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(NOTE_TITLE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                note.setVerse(cursor.getString(cursor.getColumnIndex(NOTE_SELECTED_VERSE)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return notes;
    }

    public List<Notes> getAllNotes() {
        List<Notes> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Notes note = new Notes();
                note.setId(cursor.getString(cursor.getColumnIndex(NOTE_ID)));
                note.setNotes(cursor.getString(cursor.getColumnIndex(NOTE_CONTENT)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(NOTE_TITLE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                note.setVerse(cursor.getString(cursor.getColumnIndex(NOTE_SELECTED_VERSE)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return notes;
    }


    public List<Bookmark> getChapterBookmarks(int book_id) {
        List<Bookmark> bookmarkList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE_BOOKMARK + " WHERE book_id = " + book_id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Bookmark note = new Bookmark();
                note.setVerse(cursor.getString(cursor.getColumnIndex(NOTE_SELECTED_VERSE)));
                bookmarkList.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return bookmarkList;
    }


    public List<Bookmark> getAllBookmarks() {
        List<Bookmark> bookmarkList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE_BOOKMARK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Bookmark note = new Bookmark();
                note.setVerse(cursor.getString(cursor.getColumnIndex(NOTE_SELECTED_VERSE)));
                bookmarkList.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return bookmarkList;
    }

    public List<Notification> getAllNotification() {
        List<Notification> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_POSTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Notification note = new Notification();
                note.setmId("" + cursor.getInt(cursor.getColumnIndex(KEY_NOTIFICATION_ID)));
                note.setmTitle(cursor.getString(cursor.getColumnIndex(KEY_NOTIFICATION_TITLE)));
                note.setmMessage(cursor.getString(cursor.getColumnIndex(KEY_NOTIFICATION_MESSAGE)));
                note.setmTime(cursor.getLong(cursor.getColumnIndex(KEY_TIME)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return notes;
    }

    public void deleteNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POSTS, KEY_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(notification.getmId())});
        db.close();
    }

}
