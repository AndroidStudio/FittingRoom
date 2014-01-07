package androidstudio.pl.fittingroom;

import android.app.Activity;
import android.os.Bundle;

public class FittingRoom extends Activity {
    private Database mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = new Database(this);
    }
}
