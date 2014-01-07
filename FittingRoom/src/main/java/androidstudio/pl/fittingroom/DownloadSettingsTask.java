package androidstudio.pl.fittingroom;


import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.SQLException;

public class DownloadSettingsTask extends AsyncTask<String, Integer, Boolean> {
    private final static String LOG_TAG = "DownloadSettingsTask";
    private final FittingRoom fittingRoom;

    public DownloadSettingsTask(FittingRoom fittingRoom) {
        this.fittingRoom = fittingRoom;
    }

    @Override
    protected void onPreExecute() {
        Log.w(LOG_TAG, "onPreExecute");
        if (fittingRoom.internetIsAvailable()) {
            fittingRoom.progressBar.setVisibility(View.VISIBLE);
        } else {
            Log.e(LOG_TAG, "No internet connection");
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.w(LOG_TAG, "onCancelled");
        closeDatabase();
        fittingRoom.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        if (fittingRoom.mDatabase != null) {
            try {
                fittingRoom.mDatabase.openToWrite();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                Log.e(LOG_TAG, "Field openToWrite() " + e);
                return false;
            }
        } else {
            Log.e(LOG_TAG, "Database == null");
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.w(LOG_TAG, "onPostExecute");
        if (result) {
            Toast.makeText(fittingRoom, "DONE", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(fittingRoom, "ERROR", Toast.LENGTH_LONG).show();
        }
        closeDatabase();
        fittingRoom.progressBar.setVisibility(View.INVISIBLE);
    }

    private void closeDatabase() {
        if (fittingRoom.mDatabase != null) {
            try {
                fittingRoom.mDatabase.close();
                Log.w(LOG_TAG, "Database closed!!!");
            } catch (Exception e) {
                Log.e(LOG_TAG, "Field closeDatabase() " + e);
            }
        }
    }
}
