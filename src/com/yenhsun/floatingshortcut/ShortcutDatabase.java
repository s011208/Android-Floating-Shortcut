
package com.yenhsun.floatingshortcut;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShortcutDatabase extends SQLiteOpenHelper {

    private static final String TAG = "com.yenhsun.floatingshortcut.ShortcutDatabase";

    private static final boolean DEBUG = false;

    private static final String DATABASE = "short.db";

    private static final int DB_VERSION = 1;

    private static final String TABLE = "shortcut";

    public static final String COLUMN_PACKAGENAME = "packagename";

    public static final String COLUMN_CLASSNAME = "classname";

    private SQLiteDatabase mDB;

    public ShortcutDatabase(Context context) {
        super(context, DATABASE, null, DB_VERSION);
        mDB = getWritableDatabase();
        createTable();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void createTable() {
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " (" + COLUMN_PACKAGENAME + " TEXT, "
                + COLUMN_CLASSNAME + " TEXT);");
    }

    public Cursor query() {
        if (DEBUG) {
            Cursor c = mDB.query(TABLE, null, null, null, null, null, null);
            while (c.moveToNext()) {
                Log.i("QQ", "pkg: " + c.getString(0) + ", clz: " + c.getString(1));
            }
        }
        return mDB.query(TABLE, null, null, null, null, null, null);
    }

    public void clearTable() {
        mDB.delete(TABLE, null, null);
    }

    private boolean isDuplicated(String pkg, String clz) {
        Cursor c = mDB.query(TABLE, null, null, null, null, null, null);
        while (c.moveToNext()) {
            if (pkg.equals(c.getString(0)) && clz.equals(c.getString(1))) {
                c.close();
                return true;
            }
        }
        return false;
    }

    public void insertShortcuts(ContentValues[] values) {
        for (ContentValues v : values) {
            if (!isDuplicated(v.getAsString(COLUMN_PACKAGENAME), v.getAsString(COLUMN_CLASSNAME)))
                mDB.insert(TABLE, null, v);

        }
    }

    public void insertShortcuts(ContentValues v) {
        if (!isDuplicated(v.getAsString(COLUMN_PACKAGENAME), v.getAsString(COLUMN_CLASSNAME)))
            mDB.insert(TABLE, null, v);
    }
}
