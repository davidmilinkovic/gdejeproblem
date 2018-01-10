package com.shabaton.gdejeproblem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by david on 10.01.2018.
 */

public class SQLiteObavestenja extends SQLiteOpenHelper {


    private  static final String kreiranje = "CREATE TABLE obavestenja (id INTEGER PRIMARY KEY, obrisano INTEGER, notif INTEGER)";
    private  static final String brisanje = "DROP TABLE IF EXISTS obavestenja";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GDP.db";


    public SQLiteObavestenja(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(kreiranje);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(brisanje);
        onCreate(db);

    }
}
