package androidstudio.pl.fittingroom;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FittingRoom extends Activity {
    private final static String urlChangeStatus = "http://androidstudio.pl/fittingroom/changestatus.php";
    private final static String settingsUrl = "http://androidstudio.pl/fittingroom/Settings.json";
    private final static String LOG_TAG = "FittingRoom";
    private RelativeLayout mainLayout;
    private GridView gridView;
    public int screenWidth;
    public DownloadSettingsTask downloadSettingsTask;
    public Database mDatabase;
    public ProgressBar progressBar;
    public GridViewCustomAdapter gridViewAdapter;
    public boolean mIsScrolling;
    public String showIdleIcon;
    public NotificationManager mNotificationManager;
    public Button reconnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(LOG_TAG, "onCreate");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        mainLayout = new RelativeLayout(this);

        final int progresBarSize = screenWidth / 3;
        final RelativeLayout.LayoutParams layoutParamsProgressBar = new RelativeLayout.LayoutParams(progresBarSize, progresBarSize);
        layoutParamsProgressBar.addRule(RelativeLayout.CENTER_IN_PARENT);

        gridView = new GridView(this);
        gridView.setVerticalSpacing(screenWidth / 20);
        gridView.setNumColumns(4);
        gridView.setSelector(new StateListDrawable());
        gridView.setOnScrollListener(onScrollListener);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.INVISIBLE);

        final RelativeLayout.LayoutParams layoutParamsRecconect = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsRecconect.addRule(RelativeLayout.CENTER_IN_PARENT);

        reconnectButton = new Button(this);
        reconnectButton.setText(getText(R.string.reconnectbutton));
        reconnectButton.setVisibility(View.INVISIBLE);
        reconnectButton.setOnClickListener(onRefreshClickListener);

        mDatabase = new Database(this);
        mDatabase.openToWrite();

        mainLayout.addView(gridView);
        mainLayout.addView(progressBar, layoutParamsProgressBar);
        mainLayout.addView(reconnectButton, layoutParamsRecconect);

        setContentView(mainLayout);
        registerReceiver(mConnectionReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private final BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
                Log.w(LOG_TAG, "Connected to Internet");
                reconnectButton.setVisibility(View.INVISIBLE);
                if (downloadSettingsTask != null) {
                    if (downloadSettingsTask.downloadAlertTask != null) {
                        downloadSettingsTask.downloadAlertTask.cancel(true);
                        downloadSettingsTask.handler.removeCallbacks(downloadSettingsTask.runnable);
                    }
                    final AsyncTask.Status diStatus = downloadSettingsTask.getStatus();
                    if (diStatus != AsyncTask.Status.FINISHED) {
                        return;
                    }
                }
                downloadSettingsTask = new DownloadSettingsTask(FittingRoom.this);
                downloadSettingsTask.execute(settingsUrl);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(LOG_TAG, "onResume");

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
        unregisterReceiver(mConnectionReceiver);
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

    private final Button.OnClickListener onRefreshClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            reconnectButton.setVisibility(View.INVISIBLE);
            if (downloadSettingsTask != null) {
                if (downloadSettingsTask.downloadAlertTask != null) {
                    downloadSettingsTask.downloadAlertTask.cancel(true);
                    downloadSettingsTask.handler.removeCallbacks(downloadSettingsTask.runnable);
                }
                final AsyncTask.Status diStatus = downloadSettingsTask.getStatus();
                if (diStatus != AsyncTask.Status.FINISHED) {
                    return;
                }
            }
            downloadSettingsTask = new DownloadSettingsTask(FittingRoom.this);
            downloadSettingsTask.execute(settingsUrl);
        }
    };

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
                showIdleIcon = cursor.getString(1);
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error updateContentView " + e);
        } finally {
            if (cursor != null) cursor.close();
        }

        if (bytesIconIdle != null && bytesIcon != null) {
            gridViewAdapter = new GridViewCustomAdapter(this, downloadSettingsTask.alertRoomNameList, downloadSettingsTask.alertRoomStatusList,
                    Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytesIcon, 0, bytesIcon.length, options), imageSize, imageSize, true),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytesIconIdle, 0, bytesIconIdle.length, options), imageSize, imageSize, true));
            gridView.setAdapter(gridViewAdapter);
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final TextView textViewName = (TextView) view.findViewById(39874528);
                    final CharSequence name = textViewName.getText();
                    final TextView textViewStatus = (TextView) view.findViewById(39874529);
                    final CharSequence status = textViewStatus.getText();
                    final ChangeStatusTask changeStatusTask = new ChangeStatusTask(name, status);
                    changeStatusTask.execute(urlChangeStatus);
                    return true;
                }
            });
        }

    }


    private final GridView.OnScrollListener onScrollListener = new GridView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            if (scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                mIsScrolling = true;
            } else {
                mIsScrolling = false;
            }

        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {

        }
    };

}
