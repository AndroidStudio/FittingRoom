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

    private static final String KEY_VERSION = "version";
    private static final String KEY_VERSION_ID = "id";
    private static final String KEY_VERSION_VALUE = "file_url";
    private static final String SCRIPT_CREATE_KEY_VERSION = "create table "
            + KEY_VERSION + " ("
            + KEY_VERSION_ID + " integer primary key autoincrement, "
            + KEY_VERSION_VALUE + " integer not null);";

    private static final String KEY_IDLEIMAGESETTINGS = "idleimagesettings";
    private static final String KEY_IDLEIMAGESETTINGS_ID = "id";
    private static final String KEY_IDLEIMAGESETTINGS_IMAGE = "image";
    private static final String KEY_IDLEIMAGESETTINGS_IMAGE_WIDTH = "image_width";
    private static final String KEY_IDLEIMAGESETTINGS_IMAGE_HEIGHT = "image_height";
    private static final String KEY_IDLEIMAGESETTINGS_IMAGE_UPDATE_RATE = "update_rate";
    private static final String KEY_IDLEIMAGESETTINGS_SHOW_IDLE_IMAGE = "show_idle_image";
    private static final String SCRIPT_CREATE_IDLEIMAGESETTINGS = "create table "
            + KEY_IDLEIMAGESETTINGS + " ("
            + KEY_IDLEIMAGESETTINGS_ID + " integer primary key autoincrement, "
            + KEY_IDLEIMAGESETTINGS_IMAGE + " blob not null,"
            + KEY_IDLEIMAGESETTINGS_IMAGE_WIDTH + " integer not null,"
            + KEY_IDLEIMAGESETTINGS_IMAGE_HEIGHT + " integer not null,"
            + KEY_IDLEIMAGESETTINGS_IMAGE_UPDATE_RATE + " integer not null,"
            + KEY_IDLEIMAGESETTINGS_SHOW_IDLE_IMAGE + " integer not null);";

    private static final String KEY_ALLERTFILESETTINGS = "alertfilesettings";
    private static final String KEY_ALLERTFILESETTINGS_ID = "id";
    private static final String KEY_ALLERTFILESETTINGS_URL = "file_url";
    private static final String SCRIPT_CREATE_KEY_ALLERTFILESETTINGS = "create table "
            + KEY_ALLERTFILESETTINGS + " ("
            + KEY_ALLERTFILESETTINGS_ID + " integer primary key autoincrement, "
            + KEY_ALLERTFILESETTINGS_URL + " text not null);";

    private static final String KEY_ICONIMAGESETTINGS = "iconimagesettings";
    private static final String KEY_ICONIMAGESETTINGS_ID = "id";
    private static final String KEY_ICONIMAGESETTINGS_IMAGE = "image";
    private static final String KEY_ICONIMAGESETTINGS_COLUMNS = "columns";
    private static final String KEY_ICONIMAGESETTINGS_ROWS = "rows";
    private static final String KEY_ICONIMAGESETTINGS_FADEOUT_TIME = "fadeout_time";
    private static final String KEY_ICONIMAGESETTINGS_FLASH_TIME = "flash_time";
    private static final String SCRIPT_CREATE_KEY_ICONIMAGESETTINGS = "create table "
            + KEY_ICONIMAGESETTINGS + " ("
            + KEY_ICONIMAGESETTINGS_ID + " integer primary key autoincrement, "
            + KEY_ICONIMAGESETTINGS_IMAGE + " blob not null,"
            + KEY_ICONIMAGESETTINGS_COLUMNS + " integer not null,"
            + KEY_ICONIMAGESETTINGS_ROWS + " integer not null,"
            + KEY_ICONIMAGESETTINGS_FADEOUT_TIME + " integer not null,"
            + KEY_ICONIMAGESETTINGS_FLASH_TIME + " integer not null);";

    private static final String KEY_BACKGROUNDIMAGESETTINGS = "backgroundimagesettings";
    private static final String KEY_BACKGROUNDIMAGESETTINGS_ID = "id";
    private static final String KEY_BACKGROUNDIMAGESETTINGS_MODE = "mode";
    private static final String KEY_BACKGROUNDIMAGESETTINGS_IMAGE = "image";
    private static final String KEY_BACKGROUNDIMAGESETTINGS_RED = "red";
    private static final String KEY_BACKGROUNDIMAGESETTINGS_GREEN = "green";
    private static final String KEY_BACKGROUNDIMAGESETTINGS_BLUE = "blue";
    private static final String SCRIPT_CREATE_KEY_BACKGROUNDIMAGESETTINGS = "create table "
            + KEY_BACKGROUNDIMAGESETTINGS + " ("
            + KEY_BACKGROUNDIMAGESETTINGS_ID + " integer primary key autoincrement, "
            + KEY_BACKGROUNDIMAGESETTINGS_MODE + " text not null,"
            + KEY_BACKGROUNDIMAGESETTINGS_IMAGE + " blob not null,"
            + KEY_BACKGROUNDIMAGESETTINGS_RED + " integer not null,"
            + KEY_BACKGROUNDIMAGESETTINGS_GREEN + " integer not null,"
            + KEY_BACKGROUNDIMAGESETTINGS_BLUE + " integer not null);";

    public Database(Context context) {
        this.context = context;
    }

    public Database openToWrite() throws SQLException {
        sqlLiteHelper = new SQLiteHelper(context, DATABASENAME, null, DATABASEVERSION);
        sqLiteDatabase = sqlLiteHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqlLiteHelper.close();
    }


    private class SQLiteHelper extends SQLiteOpenHelper {
        public SQLiteHelper(Context context, String databasename, SQLiteDatabase.CursorFactory factory, int databaseversion) {
            super(context, databasename, factory, databaseversion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SCRIPT_CREATE_KEY_VERSION);
            db.execSQL(SCRIPT_CREATE_IDLEIMAGESETTINGS);
            db.execSQL(SCRIPT_CREATE_KEY_ALLERTFILESETTINGS);
            db.execSQL(SCRIPT_CREATE_KEY_ICONIMAGESETTINGS);
            db.execSQL(SCRIPT_CREATE_KEY_BACKGROUNDIMAGESETTINGS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            db.execSQL("DROP TABLE IF EXISTS " + SCRIPT_CREATE_KEY_VERSION);
            db.execSQL("DROP TABLE IF EXISTS " + SCRIPT_CREATE_IDLEIMAGESETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + SCRIPT_CREATE_KEY_ALLERTFILESETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + SCRIPT_CREATE_KEY_ICONIMAGESETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + SCRIPT_CREATE_KEY_BACKGROUNDIMAGESETTINGS);
            onCreate(db);
        }
    }
}
