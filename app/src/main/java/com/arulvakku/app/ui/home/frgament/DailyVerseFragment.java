package com.arulvakku.app.ui.home.frgament;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.arulvakku.R;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.utils.Constants;
import com.arulvakku.app.utils.UtilSingleton;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class DailyVerseFragment extends Fragment implements View.OnClickListener {


    public DailyVerseFragment() {
    }

    private TextView txtVerseNo;
    private TextView txtVerse;
    private ImageView imgShare;
    private ImageView imgWhatsApp;
    private ImageView imgDownload;
    private CardView mMaterialCardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_verse, container, false);
        inflateXMLView(view);
        // Hide WhatsApp icon if the WhatsApp is not found in app
        checkForWhatsApp();
        return view;
    }

    private void inflateXMLView(View view) {
        txtVerseNo = view.findViewById(R.id.textView6);
        txtVerse = view.findViewById(R.id.textView7);
        imgShare = view.findViewById(R.id.imageView4);
        imgWhatsApp = view.findViewById(R.id.image_whats_app);
        imgDownload = view.findViewById(R.id.image_download);
        mMaterialCardView = view.findViewById(R.id.cardView);
        imgShare.setOnClickListener(this);
        imgWhatsApp.setOnClickListener(this);
        imgDownload.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTodayDailyVerse();
    }

    private void checkForWhatsApp() {
        if (isPackageInstalled(getContext())) {
            imgWhatsApp.setVisibility(View.VISIBLE);
        } else {
            imgWhatsApp.setVisibility(View.GONE);
        }
    }


    @SuppressLint("SimpleDateFormat")
    public void shareDailyVerse(int app) {
        if (getBitmap() != null) {
            if (app == 3) {
                createDirectoryAndSaveFile(getBitmap(), "இன்றைய வசனம்_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".png");
            } else {
                shareBitmap(getBitmap(), app);
            }
        }
    }

    private Bitmap getBitmap() {
        imgShare.setVisibility(View.INVISIBLE);
        imgWhatsApp.setVisibility(View.INVISIBLE);
        imgDownload.setVisibility(View.INVISIBLE);

        mMaterialCardView.setDrawingCacheEnabled(true);
        mMaterialCardView.buildDrawingCache();

        Bitmap bitmap = mMaterialCardView.getDrawingCache();
        imgShare.setVisibility(View.VISIBLE);
        imgDownload.setVisibility(View.VISIBLE);
        imgWhatsApp.setVisibility(View.VISIBLE);
        return bitmap;
    }


    /*
     * 1 for common share
     * 2 for whatsapp share
     * 3 for download
     * */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView4:
                shareDailyVerse(1);
                break;
            case R.id.image_whats_app:
                shareDailyVerse(2);
                break;
            case R.id.image_download:
                getStoragePermissions();
                break;
        }
    }


    private void setTodayDailyVerse() {
        String todayDate = UtilSingleton.getDate();
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
                    txtVerseNo.setText(UtilSingleton.bookName[Integer.parseInt(book_no) - 1].trim() + " " + Integer.parseInt(chapter_no) + " : " + Integer.parseInt(verse_no));
                    txtVerse.setText(day_verse);
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String loadJSONFromAsset() {
        String json;
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
                if (isPackageInstalled(getContext())) {
                    intent.setPackage(Constants.WHATSAPP_PACKAGE_NAME);
                    startActivity(Intent.createChooser(intent, "Share with"));
                } else {
                    Toast.makeText(getContext(), "WhatsApp is not found!", Toast.LENGTH_SHORT).show();
                }
        } else { // Share image in any app
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.arulvakku&hl=en");
            startActivity(Intent.createChooser(intent, "Share with"));
        }


    }

    // to check whatsapp is installed or not
    private boolean isPackageInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(Constants.WHATSAPP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
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

        File direct = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOWNLOADS);

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOWNLOADS);
            wallpaperDirectory.mkdirs();
        }

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOWNLOADS, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            if (file != null && file.exists()) {
                updateExternalStorage(file); // Vivek
            }
            notifyDownload(); // Show snack bar after download (livin)
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Try again later!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Updating the external storage to view the saved file
     * Ref: https://stackoverflow.com/questions/24072489/java-lang-securityexception-permission-denial-not-allowed-to-send-broadcast-an#24072611
     * */
    private void updateExternalStorage(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(file);
            scanIntent.setData(contentUri);
            getActivity().sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            getActivity().sendBroadcast(intent);
        }
    }

    // Show snackbar after image download successfully
    private void notifyDownload() {
        Snackbar snackbar = Snackbar
                .make(getView(), "Downloaded Successful", Snackbar.LENGTH_LONG)
                .setAction("OPEN", view -> startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)));
        snackbar.show();
    }
}
