package com.arulvakku.app.utils;

import android.app.Activity;
import android.content.Intent;

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
