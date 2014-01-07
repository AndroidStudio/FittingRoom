package androidstudio.pl.fittingroom;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class Database {
    private static final String DATABASENAME = "FITTINGROOM";
    private static final int DATABASEVERSION = 1;
    private final Context context;
    private SQLiteHelper sqlLiteHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static final String KEY_IDLEIMAGESETTINGS = "idleimagesettings";
    private static final String KEY_IDLEIMAGESETTINGS_ID = "id";
    private static final String KEY_IDLEIMAGESETTINGS_IMAGE = "image";
    private static final String KEY_IDLEIMAGESETTINGS_IMAGE_WIDTH = "image_width";
    private static final String KEY_IDLEIMAGESETTINGS_IMAGE_HEIGHT = "image_height";
    private static final String KEY_IDLEIMAGESETTINGS_IMAGE_UPDATE_RATE= "update_rate";
    private static final String KEY_IDLEIMAGESETTINGS_SHOW_IDLE_IMAGE= "show_idle_image";

    public Database(Context context) {
        this.context = context;
    }

    public Database openToWrite() throws SQLException {
        sqlLiteHelper = new SQLiteHelper(context, DATABASENAME, null, DATABASEVERSION);
        sqLiteDatabase = sqlLiteHelper.getWritableDatabase();
        return this;
    }

    private class SQLiteHelper extends SQLiteOpenHelper {
        public SQLiteHelper(Context context, String databasename, SQLiteDatabase.CursorFactory factory, int databaseversion) {
            super(context, databasename, factory, databaseversion);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }
}
