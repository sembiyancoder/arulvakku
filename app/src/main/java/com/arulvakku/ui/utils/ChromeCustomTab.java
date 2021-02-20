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

public class ChromeCustomTab {

    private Activity activity;
    private String url;

    private ChromeCustomTab(){
    }

    public ChromeCustomTab(Activity activity,String url){
            this.activity = activity;
            this.url = url;
    }


    public void openInCustomTab() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();

        CustomTabsIntent anotherCustomTab = new CustomTabsIntent.Builder().build();

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_app_logo);
        int requestCode = 100;
        Intent intent = anotherCustomTab.intent;
        intent.setData(Uri.parse("https://www.arulvakku.com/calendar.php"));

        PendingIntent pendingIntent = PendingIntent.getActivity(activity,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setActionButton(bitmap, "Android", pendingIntent, true);
        builder.setShowTitle(true);


        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(url));
    }
}
