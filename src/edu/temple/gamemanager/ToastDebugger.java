package edu.temple.gamemanager;

import android.app.Activity;
import android.widget.Toast;

public class ToastDebugger {
    protected Activity mCurrentActivity;

    public void setActivity(Activity activity)
    {
        mCurrentActivity = activity;
    }

    public void showMessage(final String message)
    {
        if (mCurrentActivity != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mCurrentActivity, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}