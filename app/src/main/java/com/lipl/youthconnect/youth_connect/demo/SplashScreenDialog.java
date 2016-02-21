package com.lipl.youthconnect.youth_connect.demo;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;

/**
 * Created by Android Luminous on 2/21/2016.
 */
public class SplashScreenDialog extends Dialog {

    protected ProgressBar splashProgressBar;
    protected TextView splashProgressMessage;

    public SplashScreenDialog(Context context) {
        super(context, R.style.SplashScreenStyle);

        setContentView(R.layout.splashscreen);
        setCancelable(false);
    }

}
