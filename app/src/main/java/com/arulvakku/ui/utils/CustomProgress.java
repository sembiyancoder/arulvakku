package com.arulvakku.ui.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.TextView;

import com.arulvakku.R;


/**
 * Created by Vivek E on 22-06-2019.
 */
public class CustomProgress {

    private Dialog dialog;
    private Context context;
    private String message;
    private boolean cancelable;

    public CustomProgress(Context context, String message, boolean cancelable) {
        this.context = context;
        this.message = message;
        this.cancelable = cancelable;
    }


    public void showProgressDialog() {

        if (dialog == null) {
            dialog = new Dialog(context);
        }

        dialog.setContentView(R.layout.layout_dialog_loading);
        TextView textView = dialog.findViewById(R.id.txt_loading);
        if (message != null && !message.isEmpty()) {
            textView.setText(message);
        }
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);

        if (!dialog.isShowing()) {
            dialog.show();
        }

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hideProgressDialog();
                }
                return true;
            }
        });

    }

    public void hideProgressDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

