package com.lipl.youthconnect.youth_connect.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lipl.youthconnect.youth_connect.R;

/**
 * Created by luminousinfoways on 09/02/16.
 */
public class ActivityIndicator extends ProgressDialog {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.progress_dialog_layout);

        setCancelable(true);

        RelativeLayout main = (RelativeLayout) findViewById(R.id.main);
        main.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    public ActivityIndicator(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static ActivityIndicator ctor(Context context) {
        ActivityIndicator dialog = new ActivityIndicator(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }
}
