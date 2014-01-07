package androidstudio.pl.fittingroom;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.sql.SQLException;

public class FittingRoom extends Activity {
    private final static String LOG_TAG = "FittingRoom";
    private Database mDatabase;
    private ProgressBar progressbar;
    private DownloadSettingsTask downloadSettingsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;

        final RelativeLayout mainLayout = new RelativeLayout(this);


        final int progresBarSize = screenWidth / 3;
        final RelativeLayout.LayoutParams layoutParamsProgressBar = new RelativeLayout.LayoutParams(progresBarSize, progresBarSize);
        layoutParamsProgressBar.addRule(RelativeLayout.CENTER_IN_PARENT);

        progressbar = new ProgressBar(this);
        progressbar.setVisibility(View.INVISIBLE);

        mDatabase = new Database(this);

        mainLayout.addView(progressbar, layoutParamsProgressBar);
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
        Log.d(LOG_TAG, "onResume");
        if (downloadSettingsTask != null) {
            AsyncTask.Status diStatus = downloadSettingsTask.getStatus();
            if (diStatus != AsyncTask.Status.FINISHED) {
                return;
            }
        }

        if (mDatabase != null) {
            try {
                mDatabase.openToWrite();
            } catch (SQLException e) {
                Log.e(LOG_TAG, "Field openToWrite() " + e);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        if (mDatabase != null) {
            try {
                mDatabase.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Field closeDatabase() " + e);
            }
        }
    }
}
