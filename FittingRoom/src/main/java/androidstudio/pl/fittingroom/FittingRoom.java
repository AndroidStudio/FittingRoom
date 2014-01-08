package androidstudio.pl.fittingroom;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class FittingRoom extends Activity {
    private final static String LOG_TAG = "FittingRoom";
    private RelativeLayout mainLayout;
    private GridView gridView;
    public int screenWidth;
    public DownloadSettingsTask downloadSettingsTask;
    public Database mDatabase;
    public ProgressBar progressBar;
    public GridViewCustomAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(LOG_TAG, "onCreate");

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        mainLayout = new RelativeLayout(this);

        final int progresBarSize = screenWidth / 3;
        final RelativeLayout.LayoutParams layoutParamsProgressBar = new RelativeLayout.LayoutParams(progresBarSize, progresBarSize);
        layoutParamsProgressBar.addRule(RelativeLayout.CENTER_IN_PARENT);

        gridView = new GridView(this);
        gridView.setVerticalSpacing(screenWidth / 20);
        gridView.setNumColumns(4);
        gridView.setSelector(new StateListDrawable());

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.INVISIBLE);

        mDatabase = new Database(this);
        mDatabase.openToWrite();

        mainLayout.addView(gridView);
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
        final String settingsUrl = "http://androidstudio.pl/fittingroom/Settings.json";
        downloadSettingsTask = new DownloadSettingsTask(this);
        downloadSettingsTask.execute(settingsUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LOG_TAG, "onPause");
        downloadSettingsTask.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(LOG_TAG, "onDestroy");
        downloadSettingsTask.close();
    }


    public boolean internetIsAvailable() {
        final ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        final String macAddress = wifiInfo.getBSSID();
        Log.w(LOG_TAG, "MacAddress: " + macAddress);
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
                && macAddress.equals("00:1a:6b:c2:2b:30");
    }

    public void updateContentView() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Cursor cursor = null;
        byte[] bytesIconIdle = null;
        byte[] bytesIcon = null;
        int imageSize = screenWidth / 5;
        try {
            cursor = mDatabase.getBackGroudImageSettings();
            if (cursor.moveToFirst()) {
                final String mode = cursor.getString(0);
                if (mode != null && mode.equals("Colour")) {
                    int red = cursor.getInt(2);
                    int green = cursor.getInt(3);
                    int blue = cursor.getInt(4);
                    mainLayout.setBackgroundColor(Color.rgb(red, green, blue));
                } else {
                    final byte[] bytes = cursor.getBlob(1);
                    final Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options));
                    mainLayout.setBackgroundDrawable(drawable);
                }
            }
            cursor = mDatabase.getIconImage();
            if (cursor.moveToFirst()) {
                bytesIcon = cursor.getBlob(0);
            }
            cursor = mDatabase.getIdleIconImage();
            if (cursor.moveToFirst()) {
                bytesIconIdle = cursor.getBlob(0);
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error updateContentView " + e);
        } finally {
            if (cursor != null) cursor.close();
        }

        gridViewAdapter = new GridViewCustomAdapter(this,
                Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytesIcon, 0, bytesIcon.length, options), imageSize, imageSize, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytesIconIdle, 0, bytesIconIdle.length, options), imageSize, imageSize, true));
        gridView.setAdapter(gridViewAdapter);
    }
}
