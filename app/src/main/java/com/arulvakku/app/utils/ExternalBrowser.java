package com.arulvakku.app.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class ExternalBrowser  {

    private Activity activity;
    private String url;

    private ExternalBrowser(){
    }

    public ExternalBrowser(Activity activity,String url){
        this.activity = activity;
        this.url = url;
    }

    public void openExternalBrowser(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }
}
