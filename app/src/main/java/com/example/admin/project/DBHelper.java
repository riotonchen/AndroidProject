package com.example.admin.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "MYLOCALDB";
    private static final String TABLE_NAME_MEMBER="mbr_Member";
    private static final String TABLE_NAME_SORT="sys_Sort";
    private static final String TABLE_NAME_SUBSORT="sys_SubSort";
    private static final String TABLE_NAME_MEMBERSORT="mbr_MemberSort";
    private static final String TABLE_NAME_MEMBESUBRSORT="mbr_MemberSubSort";
    private static final String TABLE_NAME_ACCOUNT="sys_Account";
    private static final String TABLE_NAME_ACCOUNTTYPE="sys_AccountType";
    private static final String TABLE_NAME_MEMBERACCOUNT="mbr_MemberAccount";
    private static final String TABLE_NAME_MEMBERBUDGET="mbr_MemberBudget";
    private static final String TABLE_NAME_PROJECT="sys_Project";
    private static final String TABLE_NAME_MEMBERPROJECT="mbr_MemberProject";
    private static final String TABLE_NAME_EXPEND="mbr_Expend";
    private static final String TABLE_NAME_INCOME="mbr_Income";
    private static final String TABLE_NAME_TRANSFER="mbr_Transfer";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);//交給父類別
    }

    public DBHelper(Context context){//自訂建構子
        this(context,DB_NAME,null,DB_VERSION);//交給預設建構子
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_mbr_Member = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBER +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name NVARCHAR(32)," +
                "nickname NVARCHAR(20)," +
                "email VARCHAR(100)," +
                "password VARCAHR(30)," +
                "renew_time DATETIME)";
        db.execSQL(CREATE_TABLE_mbr_Member);

        final String CREATE_TABLE_sys_Sort = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SORT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32)," +
                "type BOOLEAN," +
                "icon VARCHAR(64))";
        db.execSQL(CREATE_TABLE_sys_Sort);

        final String CREATE_TABLE_sys_SubSort = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_SUBSORT" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32)," +
                "type BOOLEAN," +
                "icon VARCHAR(64))";
        db.execSQL(CREATE_TABLE_sys_SubSort);

        final String CREATE_TABLE_mbr_MemberSort = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBERSORT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER ," +
                "sortID INTEGER," +
                "budget INTEGER," +
                "icon VARCHAR(64)," +
                "FOREIGN KEY(sortID) REFERENCES sys_Sort(_id))";
        db.execSQL(CREATE_TABLE_mbr_MemberSort);

        final String CREATE_TABLE_mbr_MemberSubSort = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_MEMBESUBRSORT" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER," +
                "subsortID INTEGER," +
                "budget INTEGER," +
                "icon VARCHAR(64)," +
                "FOREIGN KEY(subsortID) REFERENCES sys_SubSort(_id))";
        db.execSQL(CREATE_TABLE_mbr_MemberSubSort);

        final String CREATE_TABLE_mbr_sys_Account = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_ACCOUNT" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32))";
        db.execSQL(CREATE_TABLE_mbr_sys_Account);


        final String CREATE_TABLE_sys_AccountType = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_ACCOUNTTYPE" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32))";
        db.execSQL(CREATE_TABLE_sys_AccountType);

        final String CREATE_TABLE_mbr_MemberAccount = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_MEMBERACCOUNT" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER," +
                "accountID INTEGER," +
                "accountTypeID INTEGER," +
                "initialAmount INTEGER," +
                "FX VARCHAR(10)," +
                "comment VARCHAR(32)," +
                "FOREIGN KEY(accountID) REFERENCES sys_Account(_id)," +
                "FOREIGN KEY(accountTypeID) REFERENCES sys_AccountType(_id))";
        db.execSQL(CREATE_TABLE_mbr_MemberAccount);

        final String CREATE_TABLE_mbr_MemberBudget = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_MEMBERBUDGET" +
                "(memberID INTEGER," +
                "month DATETIME," +
                "budget INTEGER," +
                "PRIMARY KEY(memberID,month))";
        db.execSQL(CREATE_TABLE_mbr_MemberBudget);

        final String CREATE_TABLE_sys_Project = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_PROJECT" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(32))";
        db.execSQL(CREATE_TABLE_sys_Project);

        final String CREATE_TABLE_mbr_MemberProject = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_MEMBERPROJECT" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER," +
                "projectID INTEGER," +
                "FOREIGN KEY(projectID) REFERENCES sys_Project(_id))";
        db.execSQL(CREATE_TABLE_mbr_MemberProject);

        final String CREATE_TABLE_mbr_Expend = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_EXPEND" +
                "(memberID INTEGER," +
                "time DATETIME," +
                "sortID INTEGER," +
                "subsortID INTEGER," +
                "amount INTEGER," +
                "accountID INTEGER," +
                "projectID INTEGER," +
                "invoiceNum CHAR(10)," +
                "picture VARCHAR(64)," +
                "comment VARCHAR(32)," +
                "PRIMARY KEY(memberID,time)," +
                "FOREIGN KEY(sortID) REFERENCES mbr_MemberSort(_id)," +
                "FOREIGN KEY(subsortID) REFERENCES mbr_MemberSubSort(_id)," +
                "FOREIGN KEY(accountID) REFERENCES mbr_MemberAccount(_id)," +
                "FOREIGN KEY(projectID) REFERENCES mbr_MemberProject(_id))";
        db.execSQL(CREATE_TABLE_mbr_Expend);

        final String CREATE_TABLE_mbr_Income = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_INCOME" +
                "(memberID INTEGER," +
                "time DATETIME," +
                "sortID INTEGER," +
                "subsortID INTEGER," +
                "amount INTEGER," +
                "accountID INTEGER," +
                "projectID INTEGER," +
                "invoiceNum CHAR(10)," +
                "picture VARCHAR(64)," +
                "comment VARCHAR(32)," +
                "PRIMARY KEY(memberID,time)," +
                "FOREIGN KEY(sortID) REFERENCES mbr_MemberSort(_id)," +
                "FOREIGN KEY(subsortID) REFERENCES mbr_MemberSubSort(_id)," +
                "FOREIGN KEY(accountID) REFERENCES mbr_MemberAccount(_id)," +
                "FOREIGN KEY(projectID) REFERENCES mbr_MemberProject(_id))";
        db.execSQL(CREATE_TABLE_mbr_Income);

        final String CREATE_TABLE_mbr_Transfer = "CREATE TABLE IF NOT EXISTS " + "TABLE_NAME_TRANSFER" +
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
        db.execSQL(CREATE_TABLE_mbr_Transfer);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* final String DROP_TABLE_sys_Sort="DROP TABLE "+TABLE_NAME_SORT;
        db.execSQL(DROP_TABLE_sys_Sort);
        final String DROP_TABLE_sys_SubSort="DROP TABLE "+TABLE_NAME_SUBSORT;
        db.execSQL(DROP_TABLE_sys_SubSort);*/
    }
}
