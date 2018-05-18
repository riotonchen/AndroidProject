package com.example.admin.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "MYLOCALDB";
    private static final String TABLE_NAME_SORTCLASS="SortClass";
    private static final String TABLE_NAME_SUBSORTCLASS="SubSortClass";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);//交給父類別
    }

    public DBHelper(Context context){//自訂建構子
        this(context,DB_NAME,null,DB_VERSION);//交給預設建構子
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_SORTCLASS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SORTCLASS +
                "(_id Integer Primary Key AUTOINCREMENT ," +
                "name VARCHER(32)," +
                "budget VARCHER(16)," +
                "cost VARCHER(64))";
        db.execSQL(CREATE_SORTCLASS_TABLE);

        final String CREATE_SUBSORTCLASS_TABLE = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_SUBSORTCLASS" +
                "(_id Integer Primary Key AUTOINCREMENT ,name VARCHER(32))";
        db.execSQL(CREATE_SUBSORTCLASS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_TABLE_SORTCLASS="DROP TABLE "+TABLE_NAME_SORTCLASS;
        db.execSQL(DROP_TABLE_SORTCLASS);
        final String DROP_TABLE_SUBSORTCLASS="DROP TABLE "+TABLE_NAME_SUBSORTCLASS;
        db.execSQL(DROP_TABLE_SUBSORTCLASS);
    }
}
