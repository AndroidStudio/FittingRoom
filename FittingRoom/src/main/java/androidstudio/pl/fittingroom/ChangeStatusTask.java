package androidstudio.pl.fittingroom;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ChangeStatusTask extends AsyncTask<String, Integer, Boolean> {
    private final CharSequence name;
    private final CharSequence status;
    private String LOG_TAG = "ChangeStatusTask";

    public ChangeStatusTask(CharSequence name, CharSequence status) {
        if (status.equals("Idle")) {
            this.status = "Alarm";
        } else {
            this.status = "Idle";
        }
        this.name = name;

    }

    @Override
    protected Boolean doInBackground(String... url) {
        final HttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpPost request = new HttpPost(url[0]);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("name", name.toString()));
        nameValuePairs.add(new BasicNameValuePair("status", status.toString()));
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            client.execute(request);
        } catch (Exception e) {
            request.abort();
            Log.e(LOG_TAG, "Error while sending data to: " + url[0], e);
            return false;
        } finally {
            ((AndroidHttpClient) client).close();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            Log.w(LOG_TAG, "Status changed");
        }
    }
}
