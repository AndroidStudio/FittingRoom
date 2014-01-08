package androidstudio.pl.fittingroom;


import android.database.Cursor;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadSettingsTask extends AsyncTask<String, Integer, Boolean> {
    private final static String LOG_TAG = "DownloadSettingsTask";
    private final FittingRoom fittingRoom;

    public DownloadSettingsTask(FittingRoom fittingRoom) {
        this.fittingRoom = fittingRoom;
    }

    @Override
    protected void onPreExecute() {
        Log.w(LOG_TAG, "onPreExecute");
        fittingRoom.progressBar.setVisibility(View.VISIBLE);
        if (!fittingRoom.internetIsAvailable()) {
            Toast.makeText(this.fittingRoom,
                    "Proszę połączyć się z siecią WIFI o adresie: 5C:0A:5B:FC:2C:0E",
                    Toast.LENGTH_LONG).show();
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
    protected Boolean doInBackground(String... url) {
        final HttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url[0]);
        InputStream inputStream = null;
        try {
            final HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(LOG_TAG, "Error " + statusCode + " while retrieving data from " + url[0]);
                return false;
            }
            fittingRoom.mDatabase.openToWrite();
            Cursor cursor = fittingRoom.mDatabase.getVersion();
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    inputStream = entity.getContent();
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                    final StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    final JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                    final int version = jsonObject.getInt("Version");

                    if (cursor.moveToFirst()) {
                        if (version != cursor.getInt(0)) {
                            Log.w(LOG_TAG, "New version - start download data");
                            return downloadData(jsonObject);
                        } else {
                            Log.w(LOG_TAG, "Version: " + cursor.getInt(0));
                            return true;
                        }
                    } else {
                        Log.w(LOG_TAG, "No version in database");
                        return downloadData(jsonObject);
                    }
                } catch (Exception e) {
                    Log.w(LOG_TAG, "Error: " + e);
                    return false;
                } finally {
                    entity.consumeContent();
                    if (inputStream != null) inputStream.close();
                    if (cursor != null) cursor.close();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Error while retrieving data from " + url[0], e);
            return false;
        } finally {
            ((AndroidHttpClient) client).close();
        }
        return true;
    }

    private Boolean downloadData(JSONObject jsonObject) {
        try {
            final JSONArray jsonArrayBackgroundImageSettings = jsonObject.getJSONArray("BackgroundImageSettings");
            final JSONObject BackgroundImageSettings = jsonArrayBackgroundImageSettings.getJSONObject(0);
            final String BackgroundMode = BackgroundImageSettings.getString("BackgroundMode");
            final String BackgroundImageUrl = BackgroundImageSettings.getString("BackgroundImage");

            final HttpURLConnection connection = (HttpURLConnection) new URL(BackgroundImageUrl).openConnection();
            final byte[] BackgroundImageBytes = IOUtils.toByteArray(connection.getInputStream());

            final JSONArray jsonArrayBackgroundColour = BackgroundImageSettings.getJSONArray("BackgroundColour");
            final JSONObject BackgroundColour = jsonArrayBackgroundColour.getJSONObject(0);

            final int Red = BackgroundColour.getInt("Red");
            final int Green = BackgroundColour.getInt("Green");
            final int Blue = BackgroundColour.getInt("Blue");


            fittingRoom.mDatabase.insertBackgroundImageSettings(BackgroundMode, BackgroundImageBytes, Red, Green, Blue);
            fittingRoom.mDatabase.insertVersion(jsonObject.getInt("Version"));
        } catch (Exception e) {
            Log.e(LOG_TAG, "JSONException: " + e);
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.w(LOG_TAG, "onPostExecute");
        closeDatabase();
        if (result) {
            Toast.makeText(this.fittingRoom, "DONE", Toast.LENGTH_LONG).show();
            fittingRoom.updateContentView();
        } else {
            Toast.makeText(this.fittingRoom, "ERROR", Toast.LENGTH_LONG).show();
        }
        fittingRoom.progressBar.setVisibility(View.INVISIBLE);
    }

    private void closeDatabase() {
        try {
            fittingRoom.mDatabase.close();
            Log.w(LOG_TAG, "Close database");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Field closeDatabase() " + e);
        }
    }
}
