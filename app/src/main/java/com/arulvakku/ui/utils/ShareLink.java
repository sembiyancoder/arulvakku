package com.arulvakku.ui.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.arulvakku.R;

public class ShareLink {

    private Activity activity;
    private String url;

    private ShareLink(){
    }

    public ShareLink(Activity activity, String url){
            this.activity = activity;
            this.url = url;
    }


    public void shareLink() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        activity.startActivity(Intent.createChooser(shareIntent, "Share link using"));
    }
}
