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
    private static final String TABLE_NAME_MEMBERTYPE = "mbr_membertype";
    private static final String TABLE_NAME_FRIENDSHIP = "mbr_friendship";
    private static final String TABLE_NAME_BOOK = "mbr_book";
    private static final String TABLE_NAME_BOOKMEMBER = "mbr_bookmember";
    private static final String TABLE_NAME_INVOICE = "mbr_invoice";
    private static final String TABLE_NAME_INVOICEDETAIL = "mbr_invoicedetail";
    private static final String TABLE_NAME_INVOICEOWNER = "mbr_invoiceowner";
    private static final String TABLE_NAME_ENTERPRISE = "ent_enterprise";
    private static final String TABLE_NAME_PROMOTION = "ent_promotion";
    private static final String TABLE_NAME_CARD = "mbr_card";
    private static final String TABLE_NAME_THEME = "sys_theme";
    private static final String TABLE_NAME_SETUP = "mbr_setup";
    private static final String TABLE_NAME_GROUPACCOUNTING = "mbr_groupaccounting";
    private static final String TABLE_NAME_GROUPACCOUNTINGDETAIL = "mbr_groupaccountingdetail";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);//交給父類別
    }

    public DBHelper(Context context) {//自訂建構子
        this(context, DB_NAME, null, DB_VERSION);//交給預設建構子
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_mbr_membertype = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBERTYPE+
                "(membertype_id TINYINT(1) PRIMARY KEY ," +
                "name VARCHAR(10)," +
                "renew_time TIMESTAMP(4))";
        db.execSQL(CREATE_TABLE_mbr_membertype);

        final String CREATE_TABLE_mbr_member = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMBER +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name NVARCHAR(32)," +
                "nickname NVARCHAR(20)," +
                "email VARCHAR(100)," +
                "password VARCAHR(30)," +
                "renew_time DATETIME)";
        db.execSQL(CREATE_TABLE_mbr_member);

        final String CREATE_TABLE_mbr_friendship = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_FRIENDSHIP +
                "(member_id INTEGER(4)," +
                "friend_id INTEGER(4)," +
                "nickname VARCHAR(20)," +
                "renew_time TIMESTAMP(4),"+
                "PRIMARY KEY(member_id,friend_id))";
        db.execSQL(CREATE_TABLE_mbr_friendship);

        final String CREATE_TABLE_mbr_book = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BOOK +
                "(book_id INTEGER(4) PRIMARY KEY ," +
                "owner_id INTEGER(4)," +
                "name VARCHAR(20)," +
                "localpicture VARCHAR(64)," +
                "dbpicture VARCHAR(64)," +
                "balance DOUBLE(8)," +
                "renew_time TIMESTAMP(4)," +
                "FOREIGN KEY(book_id) REFERENCES mbr_bookmember(book_id),"+
                "FOREIGN KEY(book_id) REFERENCES mbr_groupaccounting(book_id))";
        db.execSQL(CREATE_TABLE_mbr_book);

        final String CREATE_TABLE_mbr_bookmember = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BOOKMEMBER +
                "(book_id INTEGER(4)," +
                "member_id INTEGER(4)," +
                "renew_time TIMESTAMP(4)," +
                "PRIMARY KEY(book_id,member_id))";
        db.execSQL(CREATE_TABLE_mbr_bookmember);

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
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "memberID INTEGER," +
                "time DATETIME," +
                "sourceAccountID INTEGER," +
                "targetAccountID INTEGER," +
                "amount INTEGER," +
                "fee INTEGER," +
                "projectID INTEGER," +
                "comment VARCHAR(32)," +
                "FOREIGN KEY(sourceAccountID) REFERENCES mbr_MemberAccount(_id)," +
                "FOREIGN KEY(targetAccountID) REFERENCES mbr_MemberAccount(_id))";
        db.execSQL(CREATE_TABLE_mbr_transfer);

        final String CREATE_TABLE_mbr_invoice = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_INVOICE +
                "(number CHAR(10) PRIMARY KEY ," +
                "date DATE," +
                "sellername VARCHAR(30)," +
                "period CHAR(5)," +
                "selleraddress VARCHAR(100)," +
                "renew_time TIMESTAMP(4),"+
                "FOREIGN KEY(number) REFERENCES mbr_invoicedetail(number),"+
                "FOREIGN KEY(number) REFERENCES mbr_invoiceowner(number),"+
                "FOREIGN KEY(number) REFERENCES mbr_groupaccounting(invoicenumber))";
        db.execSQL(CREATE_TABLE_mbr_invoice);

        final String CREATE_TABLE_mbr_invoicedetail = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_INVOICEDETAIL +
                "(number CHAR(10)," +
                "rowNum  SAMLLINT(2)," +
                "description  VARCHAR(20)," +
                "quantity  INTEGER(4)," +
                "unitPrice DOUBLE(8)," +
                "amount DOUBLE(8),"+
                "renew_time TIMESTAMP(4),"+
                "PRIMARY KEY(number,rowNum))";
        db.execSQL(CREATE_TABLE_mbr_invoicedetail);

        final String CREATE_TABLE_mbr_invoiceowner = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_INVOICEOWNER +
                "(number CHAR(10)," +
                "member_id  INTEGER(4)," +
                "renew_time TIMESTAMP(4),"+
                "PRIMARY KEY(number,member_id))";
        db.execSQL(CREATE_TABLE_mbr_invoiceowner);

        /*final String CREATE_TABLE_ent_enterprise = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ENTERPRISE +
                "(store_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name  VARCHAR(10)," +
                "email  VARCHAR(100)," +
                "password  VARCHAR(30)," +
                "uni_num CHAR(8)," +
                "mobile_num VARCHAR(30),"+
                "phone_num VARCHAR(30),"+
                "extension VARCHAR(20),"+
                "address VARCHAR(128),"+
                "renew_time TIMESTAMP(4),"+
                "FOREIGN KEY(store_id) REFERENCES ent_promotion(store_id))";
        db.execSQL(CREATE_TABLE_ent_enterprise);*/

        final String CREATE_TABLE_ent_promotion = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PROMOTION +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "store_id  INTEGER(4)," +
                "name  VARCHAR(20)," +
                "content  VARCHAR(280)," +
                "status TINYINT(1)," +
                "starttime DATE(3),"+
                "endtime DATE(3),"+
                "photo VARCHAR(100),"+
                "renew_time TIMESTAMP(4))";
        db.execSQL(CREATE_TABLE_ent_promotion);

        final String CREATE_TABLE_mbr_card = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CARD +
                "(card_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "member_id  INTEGER(4)," +
                "name  VARCHAR(20)," +
                "number  VARCHAR(100)," +
                "renew_time TIMESTAMP(4))";
        db.execSQL(CREATE_TABLE_mbr_card);

        final String CREATE_TABLE_sys_theme = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_THEME +
                "(theme_id INTEGER(4) PRIMARY KEY ," +
                "name  VARCHAR(20)," +
                "fontcolor  CHAR(12)," +
                "bgcolor  CHAR(12)," +
                "photo VARCHAR(64)," +
                "renew_time TIMESTAMP(4),"+
                "FOREIGN KEY(theme_id) REFERENCES mbr_setup(theme_id))";
        db.execSQL(CREATE_TABLE_sys_theme);

        final String CREATE_TABLE_mbr_setup = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SETUP +
                "(member_id INTEGER(4) PRIMARY KEY ," +
                "weekstartday VARCHAR(2)," +
                "monthstartday VARCHAR(2)," +
                "theme_id INTEGER(4)," +
                "notaion VARCHAR(10)," +
                "remindtime TINYINT(1),"+
                "autobackup TINYINT(1),"+
                "pwdlock CHAR(4),"+
                "renew_time TIMESTAMP(4))";
        db.execSQL(CREATE_TABLE_mbr_setup);

        final String CREATE_TABLE_mbr_groupaccounting = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_GROUPACCOUNTING +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "book_id INTEGER(4)," +
                "time DATETIME(8)," +
                "type TINYINT(1)," +
                "amount DOUBLE(8)," +
                "invoicenumber CHAR(10),"+
                "localpicture VARCHAR(64),"+
                "dbpicture VARCHAR(64),"+
                "comment VARCHAR(128),"+
                "renew_time TIMESTAMP(4),"+
                "FOREIGN KEY(id) REFERENCES mbr_groupaccountingdetail(id))";
        db.execSQL(CREATE_TABLE_mbr_groupaccounting);

        final String CREATE_TABLE_mbr_groupaccountingdetail = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_GROUPACCOUNTINGDETAIL +
                "(id INTEGER(4) ," +
                "member_id VARCHAR(20)," +
                "cost CHAR(12)," +
                "spent CHAR(12)," +
                "payable VARCHAR(64)," +
                "renew_time TIMESTAMP(4),"+
                "PRIMARY KEY(id,member_id))";
        db.execSQL(CREATE_TABLE_mbr_groupaccountingdetail);

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
