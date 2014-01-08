package androidstudio.pl.fittingroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class Database {
    private static final String DATABASENAME = "FITTINGROOM";
    private static final int DATABASEVERSION = 4;
    private final Context context;

    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;

    private static final String KEY_VERSION = "version";
    private static final String KEY_VERSION_ID = "id";
    private static final String KEY_VERSION_VALUE = "value";
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
    private static final String KEY_ICONIMAGESETTINGS_SPACING = "spacing";
    private static final String KEY_ICONIMAGESETTINGS_FADEOUT_TIME = "fadeout_time";
    private static final String KEY_ICONIMAGESETTINGS_FLASH_TIME = "flash_time";
    private static final String SCRIPT_CREATE_KEY_ICONIMAGESETTINGS = "create table "
            + KEY_ICONIMAGESETTINGS + " ("
            + KEY_ICONIMAGESETTINGS_ID + " integer primary key autoincrement, "
            + KEY_ICONIMAGESETTINGS_IMAGE + " blob not null,"
            + KEY_ICONIMAGESETTINGS_COLUMNS + " integer not null,"
            + KEY_ICONIMAGESETTINGS_ROWS + " integer not null,"
            + KEY_ICONIMAGESETTINGS_SPACING + " integer not null,"
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
        sqLiteHelper = new SQLiteHelper(context, DATABASENAME, null, DATABASEVERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqLiteHelper.close();
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
            db.execSQL("DROP TABLE IF EXISTS " + KEY_VERSION);
            db.execSQL("DROP TABLE IF EXISTS " + KEY_IDLEIMAGESETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + KEY_ALLERTFILESETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + KEY_ICONIMAGESETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + KEY_BACKGROUNDIMAGESETTINGS);
            onCreate(db);
        }
    }

    public Cursor getVersion() {
        return sqLiteDatabase.query(KEY_VERSION, new String[]{KEY_VERSION_VALUE}, null, null, null, null, null);
    }

    public void insertVersion(int version) {
        sqLiteDatabase.delete(KEY_VERSION, null, null);
        final ContentValues values = new ContentValues();
        values.put(KEY_VERSION_VALUE, version);
        sqLiteDatabase.insert(KEY_VERSION, null, values);
    }

    public void insertBackgroundImageSettings(String backgroundMode, byte[] backgroundImage, int red, int green, int blue) {
        sqLiteDatabase.delete(KEY_BACKGROUNDIMAGESETTINGS, null, null);
        final ContentValues values = new ContentValues();
        values.put(KEY_BACKGROUNDIMAGESETTINGS_MODE, backgroundMode);
        values.put(KEY_BACKGROUNDIMAGESETTINGS_IMAGE, backgroundImage);
        values.put(KEY_BACKGROUNDIMAGESETTINGS_RED, red);
        values.put(KEY_BACKGROUNDIMAGESETTINGS_GREEN, green);
        values.put(KEY_BACKGROUNDIMAGESETTINGS_BLUE, blue);
        sqLiteDatabase.insert(KEY_BACKGROUNDIMAGESETTINGS, null, values);
    }

    public Cursor getBackGroudImageSettings() {
        return sqLiteDatabase.query(KEY_BACKGROUNDIMAGESETTINGS, new String[]{
                KEY_BACKGROUNDIMAGESETTINGS_MODE, KEY_BACKGROUNDIMAGESETTINGS_IMAGE,
                KEY_BACKGROUNDIMAGESETTINGS_RED, KEY_BACKGROUNDIMAGESETTINGS_GREEN,
                KEY_BACKGROUNDIMAGESETTINGS_BLUE}, null, null, null, null, null);
    }

    public void insertIdleImageSettings(byte[] idleImageBytes, int idleImageWidth, int idleImageHeigh, int idleImageUpdateRate, String showIdleImage) {
        sqLiteDatabase.delete(KEY_IDLEIMAGESETTINGS, null, null);
        final ContentValues values = new ContentValues();
        values.put(KEY_IDLEIMAGESETTINGS_IMAGE, idleImageBytes);
        values.put(KEY_IDLEIMAGESETTINGS_IMAGE_WIDTH, idleImageWidth);
        values.put(KEY_IDLEIMAGESETTINGS_IMAGE_HEIGHT, idleImageHeigh);
        values.put(KEY_IDLEIMAGESETTINGS_IMAGE_UPDATE_RATE, idleImageUpdateRate);
        values.put(KEY_IDLEIMAGESETTINGS_SHOW_IDLE_IMAGE, showIdleImage);
        sqLiteDatabase.insert(KEY_IDLEIMAGESETTINGS, null, values);
    }

    public void insertIconImageSettings(byte[] iconImageBytes, int iconColumns, int iconRows, int iconSpacing, int iconFadeoutTime, int iconFlashTime) {
        sqLiteDatabase.delete(KEY_ICONIMAGESETTINGS, null, null);
        final ContentValues values = new ContentValues();
        values.put(KEY_ICONIMAGESETTINGS_IMAGE, iconImageBytes);
        values.put(KEY_ICONIMAGESETTINGS_COLUMNS, iconColumns);
        values.put(KEY_ICONIMAGESETTINGS_ROWS, iconRows);
        values.put(KEY_ICONIMAGESETTINGS_SPACING, iconSpacing);
        values.put(KEY_ICONIMAGESETTINGS_FADEOUT_TIME, iconFadeoutTime);
        values.put(KEY_ICONIMAGESETTINGS_FLASH_TIME, iconFlashTime);
        sqLiteDatabase.insert(KEY_ICONIMAGESETTINGS, null, values);
    }

    public void insertAlertFileUrl(String alertFile) {
        sqLiteDatabase.delete(KEY_ALLERTFILESETTINGS, null, null);
        final ContentValues values = new ContentValues();
        values.put(KEY_ALLERTFILESETTINGS_URL, alertFile);
        sqLiteDatabase.insert(KEY_ALLERTFILESETTINGS, null, values);
    }

    public Cursor getAlertUrl() {
        return sqLiteDatabase.query(KEY_ALLERTFILESETTINGS, new String[]{KEY_ALLERTFILESETTINGS_URL}, null, null, null, null, null);
    }

}
