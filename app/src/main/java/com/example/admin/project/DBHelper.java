package com.example.admin.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "MYLOCALDB";
    private static final String TABLE_NAME_MEMBER = "mbr_member";
    private static final String TABLE_NAME_SORT = "sys_sort";
    private static final String TABLE_NAME_SUBSORT = "sys_subsort";
    private static final String TABLE_NAME_MEMBERSORT = "mbr_membersort";
    private static final String TABLE_NAME_MEMBESUBRSORT = "mbr_membersubsort";
    private static final String TABLE_NAME_ACCOUNT = "sys_account";
    private static final String TABLE_NAME_ACCOUNTTYPE = "sys_accounttype";
    private static final String TABLE_NAME_MEMBERACCOUNT = "mbr_memberaccount";
    private static final String TABLE_NAME_MEMBERBUDGET = "mbr_memberbudget";
    private static final String TABLE_NAME_PROJECT = "sys_project";
    private static final String TABLE_NAME_MEMBERPROJECT = "mbr_memberproject";
    //private static final String TABLE_NAME_EXPEND="mbr_expend";
    //private static final String TABLE_NAME_INCOME="mbr_income";
    private static final String TABLE_NAME_ACCOUNTING = "mbr_accounting";
    private static final String TABLE_NAME_TRANSFER = "mbr_transfer";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);//交給父類別
    }

    public DBHelper(Context context) {//自訂建構子
        this(context, DB_NAME, null, DB_VERSION);//交給預設建構子
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_mbr_member = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBER +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name NVARCHAR(32)," +
                "nickname NVARCHAR(20)," +
                "email VARCHAR(100)," +
                "password VARCAHR(30)," +
                "renew_time DATETIME)";
        db.execSQL(CREATE_TABLE_mbr_member);

        final String CREATE_TABLE_sys_sort = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SORT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32)," +
                "type BOOLEAN," +
                "icon VARCHAR(64))";
        db.execSQL(CREATE_TABLE_sys_sort);

        final String CREATE_TABLE_sys_subsort = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SUBSORT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32)," +
                "type BOOLEAN," +
                "icon VARCHAR(64))";
        db.execSQL(CREATE_TABLE_sys_subsort);

        final String CREATE_TABLE_mbr_membersort = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBERSORT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER ," +
                "sortID INTEGER," +
                "budget INTEGER," +
                "amount INTEGER," +
                "icon VARCHAR(64)," +
                "FOREIGN KEY(sortID) REFERENCES sys_Sort(_id))";
        db.execSQL(CREATE_TABLE_mbr_membersort);

        final String CREATE_TABLE_mbr_membersubsort = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBESUBRSORT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER," +
                "memberSortID INTEGER," +
                "subsortID INTEGER," +
                "budget INTEGER," +
                "amount INTEGER," +
                "icon VARCHAR(64)," +
                "FOREIGN KEY(subsortID) REFERENCES sys_SubSort(_id)," +
                "FOREIGN KEY(memberSortID) REFERENCES mbr_MemberSort(_id))";
        db.execSQL(CREATE_TABLE_mbr_membersubsort);

        final String CREATE_TABLE_mbr_sys_account = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ACCOUNT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32))";
        db.execSQL(CREATE_TABLE_mbr_sys_account);


        final String CREATE_TABLE_sys_accounttype = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ACCOUNTTYPE +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32))";
        db.execSQL(CREATE_TABLE_sys_accounttype);

        final String CREATE_TABLE_mbr_memberaccount = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBERACCOUNT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER," +
                "accountID INTEGER," +
                "accountTypeID INTEGER," +
                "initialAmount INTEGER," +
                "balance INTEGER," +
                "FX VARCHAR(10)," +
                "comment VARCHAR(32)," +
                "FOREIGN KEY(accountID) REFERENCES sys_Account(_id)," +
                "FOREIGN KEY(accountTypeID) REFERENCES sys_AccountType(_id))";
        db.execSQL(CREATE_TABLE_mbr_memberaccount);

        final String CREATE_TABLE_mbr_memberbudget = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBERBUDGET +
                "(memberID INTEGER," +
                "month DATETIME," +
                "budget INTEGER," +
                "PRIMARY KEY(memberID,month))";
        db.execSQL(CREATE_TABLE_mbr_memberbudget);

        final String CREATE_TABLE_sys_project = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PROJECT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32))";
        db.execSQL(CREATE_TABLE_sys_project);

        final String CREATE_TABLE_mbr_memberproject = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBERPROJECT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER," +
                "projectID INTEGER," +
                "FOREIGN KEY(projectID) REFERENCES sys_Project(_id))";
        db.execSQL(CREATE_TABLE_mbr_memberproject);

        final String CREATE_TABLE_mbr_accounting = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ACCOUNTING +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER," +
                "time DATETIME," +
                "type BOOLEAN," +
                "sortID INTEGER," +
                "subsortID INTEGER," +
                "amount INTEGER," +
                "accountID INTEGER," +
                "projectID INTEGER," +
                "invoiceNum CHAR(10)," +
                "picture VARCHAR(64)," +
                "comment VARCHAR(32)," +
                "FOREIGN KEY(sortID) REFERENCES mbr_MemberSort(_id)," +
                "FOREIGN KEY(subsortID) REFERENCES mbr_MemberSubSort(_id)," +
                "FOREIGN KEY(accountID) REFERENCES mbr_MemberAccount(_id)," +
                "FOREIGN KEY(projectID) REFERENCES mbr_MemberProject(_id))";
        db.execSQL(CREATE_TABLE_mbr_accounting);

        final String CREATE_TABLE_mbr_transfer = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TRANSFER +
                "(memberID INTEGER," +
                "time DATETIME," +
                "sourceAccountID INTEGER," +
                "targetAccountID INTEGER," +
                "amount INTEGER," +
                "fee INTEGER," +
                "projectID INTEGER," +
                "comment VARCHAR(32)," +
                "PRIMARY KEY(memberID,time)," +
                "FOREIGN KEY(sourceAccountID) REFERENCES mbr_MemberAccount(_id)," +
                "FOREIGN KEY(targetAccountID) REFERENCES mbr_MemberAccount(_id))";
        db.execSQL(CREATE_TABLE_mbr_transfer);

        /*final String CREATE_TRIGGER_UpdateAccount="CREATE TRIGGER UpdateAccount AFTER INSERT ON mbr_accounting " +
                " BEGIN " +
                "  UPDATE mbr_memberaccount SET balance=balance- " +
                "  NEW.amount " +
                "  WHERE mbr_memberaccount.accountID=NEW.accountID;   " +
                " END ";
        db.execSQL(CREATE_TRIGGER_UpdateAccount);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* final String DROP_TABLE_sys_sort="DROP TABLE "+TABLE_NAME_SORT;
        db.execSQL(DROP_TABLE_sys_sort);
        final String DROP_TABLE_sys_subsort="DROP TABLE "+TABLE_NAME_SUBSORT;
        db.execSQL(DROP_TABLE_sys_subsort);*/
    }
}
