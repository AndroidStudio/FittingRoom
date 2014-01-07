package androidstudio.pl.fittingroom;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class FittingRoom extends Activity {
    private final static String LOG_TAG = "FittingRoom";
    public Database mDatabase;
    public ProgressBar progressBar;
    private DownloadSettingsTask downloadSettingsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(LOG_TAG, "onCreate");

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;

        final RelativeLayout mainLayout = new RelativeLayout(this);


        final int progresBarSize = screenWidth / 3;
        final RelativeLayout.LayoutParams layoutParamsProgressBar = new RelativeLayout.LayoutParams(progresBarSize, progresBarSize);
        layoutParamsProgressBar.addRule(RelativeLayout.CENTER_IN_PARENT);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.INVISIBLE);

        mDatabase = new Database(this);

        mainLayout.addView(progressBar, layoutParamsProgressBar);
        this.setContentView(mainLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(LOG_TAG, "onResume");
        if (downloadSettingsTask != null) {
            AsyncTask.Status diStatus = downloadSettingsTask.getStatus();
            if (diStatus != AsyncTask.Status.FINISHED) {
                return;
            }
        }
        final String settingsUrl = "";
        downloadSettingsTask = new DownloadSettingsTask(this);
        downloadSettingsTask.execute(settingsUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LOG_TAG, "onPause");
        downloadSettingsTask.cancel(true);
    }

    public boolean internetIsAvailable() {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
