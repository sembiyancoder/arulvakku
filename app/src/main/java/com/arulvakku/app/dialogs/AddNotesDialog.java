package com.arulvakku.app.dialogs;

import android.app.Activity;

public class AddNotesDialog {

    private Activity activity;
    private String verses;

    private AddNotesDialog() {

    }

    public AddNotesDialog(Activity activity, String verses) {
        this.activity = activity;
        this.verses = verses;
    }


}
