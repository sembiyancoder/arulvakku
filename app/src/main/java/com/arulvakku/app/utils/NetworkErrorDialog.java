package com.arulvakku.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.arulvakku.R;


/**
 * Created by Vivek E on 22-06-2019.
 */
public class NetworkErrorDialog {
    private Dialog dialog;
    private Context context;

    public NetworkErrorDialog(Context context) {
        this.context = context;
    }

    public void showAlertDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_no_internet_connection);
        TextView textView = dialog.findViewById(R.id.txt_open_network_connection);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(settingsIntent);
                hideAlertDialog();
            }
        });
        dialog.show();
    }

    private void hideAlertDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

