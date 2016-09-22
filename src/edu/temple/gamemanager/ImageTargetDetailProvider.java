package edu.temple.gamemanager;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import java.util.Locale;

/**
 * Sample plugin class to demonstrate how to provide extra functionality
 * to the Unity environment
 */
public class ImageTargetDetailProvider {
    protected Activity mCurrentActivity;

    /**
     * Sets the activity for the class so that the target info message
     * can be displayed
     */
    public void setActivity(Activity activity)
    {
        mCurrentActivity = activity;
    }

    /**
     * Displays a Toast message overlaid on screen with Target info
     */
    public void showTargetInfo(String targetName, float targetWidth, float targetHeight)
    {
        Log.d("MyPlugin", "Executing showTargetInfo native Android code");

        if (mCurrentActivity == null) {
            Log.e("MyPlugin", "Android Activity not set in plugin");
            return;
        }

        final String msg = String.format(Locale.ENGLISH,
                "Image Target %s - size: %5.2f x %5.2f",
                targetName, targetWidth, targetHeight);

        Log.d("MyPlugin", "Target Info: " + msg);

        mCurrentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mCurrentActivity, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}