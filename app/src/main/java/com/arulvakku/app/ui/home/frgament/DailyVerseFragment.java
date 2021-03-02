package com.arulvakku.app.ui.home.frgament;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.arulvakku.R;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.utils.UtilSingleton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class DailyVerseFragment extends Fragment implements View.OnClickListener {

    private UtilSingleton sInstance;

    public DailyVerseFragment() {

    }

    private TextView txtVerseNo;
    private TextView txtVerse;
    private ImageView imgShare;
    private ImageView imgWhatsApp;
    private ImageView imgDownload;
    private CardView mMaterialCardView;

    private final String strWhatsApp = "com.whatsapp";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sInstance = UtilSingleton.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_verse, container, false);
        txtVerseNo = rootView.findViewById(R.id.textView6);
        txtVerse = rootView.findViewById(R.id.textView7);
        imgShare = rootView.findViewById(R.id.imageView4);
        imgWhatsApp = rootView.findViewById(R.id.image_whats_app);
        imgDownload = rootView.findViewById(R.id.image_download);
        mMaterialCardView = rootView.findViewById(R.id.cardView);
        imgShare.setOnClickListener(this);
        imgWhatsApp.setOnClickListener(this);
        imgDownload.setOnClickListener(this);
       /* rootView.findViewById(R.id.text_view_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = createBitmapFromView(getContext(), mMaterialCardView);
                if (bitmap != null) {
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                            bitmap, "Design", null);

                    Uri uri = Uri.parse(path);

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.putExtra(Intent.EXTRA_TEXT, "I found something cool!");
                    startActivity(Intent.createChooser(share, "Share Your Design!"));
                }
                *//*Intent mainIntent = new Intent(getActivity(), ViewAllDailyVerseActivity.class);
                startActivity(mainIntent);*//*
            }
        });*/

        // Hide WhatsApp icon if the WhatsApp is not found in app
        if (isPackageInstalled(strWhatsApp, getContext())) {
            imgWhatsApp.setVisibility(View.VISIBLE);
        } else {
            imgWhatsApp.setVisibility(View.GONE);
        }
        return rootView;
    }

    private Bitmap createBitmapFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTodayVerse();

    }

    private void defaultVerse() {
        txtVerseNo.setText(getString(R.string.default_chapter) + 5 + " : " + 41);
        txtVerse.setText(getString(R.string.default_verse));
    }


    public void shareDailyVerse(int app) {
        imgShare.setVisibility(View.INVISIBLE);
        imgWhatsApp.setVisibility(View.INVISIBLE);
        imgDownload.setVisibility(View.INVISIBLE);

        mMaterialCardView.setDrawingCacheEnabled(true);
        mMaterialCardView.buildDrawingCache();

        Bitmap bm = mMaterialCardView.getDrawingCache();
        imgShare.setVisibility(View.VISIBLE);
        imgDownload.setVisibility(View.VISIBLE);

        // Hide WhatsApp icon if the WhatsApp is not found in app
        if (getContext() != null)
            if (isPackageInstalled(strWhatsApp, getContext())) {
                imgWhatsApp.setVisibility(View.VISIBLE);
            } else {
                imgWhatsApp.setVisibility(View.GONE);
            }

        if (app == 3) {
            createDirectoryAndSaveFile(bm, "இன்றைய வசனம்_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".png");
        } else {
            shareBitmap(bm, app);
        }

    }


    /*
     * 1 for common share
     * 2 for whatsapp share
     * 3 for download
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView4:
                // shareDailyVerse();
                /*Intent mainIntent = new Intent(getActivity(), DailyVerseEditActivity.class);
                mainIntent.putExtra("daily_verse", txtVerse.getText().toString());
                startActivity(mainIntent);*/
                shareDailyVerse(1);
                break;
            case R.id.image_whats_app:
                // shareDailyVerse();
                /*Intent mainIntent = new Intent(getActivity(), DailyVerseEditActivity.class);
                mainIntent.putExtra("daily_verse", txtVerse.getText().toString());
                startActivity(mainIntent);*/
                shareDailyVerse(2);
                break;
            case R.id.image_download:
                // shareDailyVerse();
                /*Intent mainIntent = new Intent(getActivity(), DailyVerseEditActivity.class);
                mainIntent.putExtra("daily_verse", txtVerse.getText().toString());
                startActivity(mainIntent);*/
                getStoragePermissions();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void shareBitmap(@NonNull Bitmap bitmap, int app) {
        //---Save bitmap to external cache directory---//
        //get cache directory
        File cachePath = new File(getActivity().getExternalCacheDir(), "my_images/");
        cachePath.mkdirs();

        //create png file
        File file = new File(cachePath, "இன்றைய வசனம்.png");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //---Share File---//
        //get file uri
        Uri myImageFileUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);

        //create a intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri);
        intent.setType("image/png");

        if (app == 2) { //  share only in WhatsApp
            if (getContext() != null)
                if (isPackageInstalled(strWhatsApp, getContext())) {
                    intent.setPackage(strWhatsApp);
                    startActivity(Intent.createChooser(intent, "Share with"));
                } else {
                    showAlert();
                }
        } else { // Share image in any app
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.arulvakku&hl=en");
            startActivity(Intent.createChooser(intent, "Share with"));
        }


    }

    // Show alert if user doesn't have WhatsApp
    private void showAlert() {
       /* AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("WhatsApp is not found!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();*/

        Toast.makeText(getContext(), "WhatsApp is not found!", Toast.LENGTH_SHORT).show();
    }


    // to check whatsapp is installed or not
    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // get storage permission to store image in to local path
    private void getStoragePermissions() {

        //call back after permission granted
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                shareDailyVerse(3);

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
//                getStoragePermissions();
            }


        };

        if (getContext() != null) {
            //check all needed permissions together
            TedPermission.with(getContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission, you can not download image\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures");

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Pictures/", fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            showCustomToast();
//            Toast.makeText(getContext(), "Download successful",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Try again later!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomToast() {
        //Creating the LayoutInflater instance
        LayoutInflater li = getLayoutInflater();
        //Getting the View object as defined in the customtoast.xml file
        View layout = li.inflate(R.layout.toast,(ViewGroup) getView().findViewById(R.id.toast_layout_root));

        //Creating the Toast object
        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);//setting the view of custom toast layout
        toast.show();
    }
}
