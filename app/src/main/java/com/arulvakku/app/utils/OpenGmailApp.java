package com.arulvakku.app.utils;

import android.app.Activity;
import android.content.Intent;

import com.arulvakku.R;

public class OpenGmailApp {

    private Activity activity;
    private String recipient,subject;

    private OpenGmailApp(){
    }

    public OpenGmailApp(Activity activity, String recipient, String subject){
        this.activity = activity;
        this.recipient = recipient;
        this.subject = subject;
    }

    public void openInGmail(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients={recipient,"info@arulvakku.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        activity.startActivity(Intent.createChooser(intent, "Send mail"));
        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
    }

}
