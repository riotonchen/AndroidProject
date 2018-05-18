package com.example.admin.project;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private SQLiteDatabase db;
    private final String DB_NAME = "MYLOCALDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        SetUpLocalDB(DB_NAME);//建立資料庫及資料表
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment(), "支出");
        viewPager.setAdapter(adapter);
    }

    private void SetUpLocalDB(String dbName) {
        // Android 使用 SQLite 資料庫的方法
        // http://sweeteason.pixnet.net/blog/post/37364146-android-%E4%BD%BF%E7%94%A8-sqlite-%E8%B3%87%E6%96%99%E5%BA%AB%E7%9A%84%E6%96%B9%E6%B3%95

        //建資料庫
        db = openOrCreateDatabase(dbName, android.content.Context.MODE_PRIVATE, null);
        //建資料表
        final String TB_NAME;
        String[] col;
        String[] data;
        String cmd;
        Cursor cur;
        //建SortClass資料表
        TB_NAME = "SortClass";
        col = new String[]{"name", "budget", "cost"};
        data = new String[]{"進修學習", "預算:200", "$ 125",
                "食品酒水", "預算:0", "$ 120",
                "休閒娛樂", "預算:0", "$ 0",
                "人情往來", "預算:0", "$ 0",
                "其他雜項", "預算:0", "$ 0",
                "居家物業", "預算:0", "$ 0"};
        cmd = "CREATE TABLE IF NOT EXISTS " +
                TB_NAME +
                "(_id Integer Primary Key AUTOINCREMENT ," +
                "name VARCHER(32)," +
                "budget VARCHER(16)," +
                "cost VARCHER(64))";
        db.execSQL(cmd);
        cur = db.rawQuery("select * FROM " + TB_NAME, null);
        if (cur.getCount() == 0) {//沒資料才要新增
            AddData(TB_NAME, col, data);
        }
    }

    public void AddData(String tableName, String[] columnName, String[] data) {
        db = openOrCreateDatabase(DB_NAME, android.content.Context.MODE_PRIVATE, null);
        ContentValues cv = new ContentValues(columnName.length);
        for (int i = 0; i < data.length;) {
            for (int j = 0; j < columnName.length; j++) {
                cv.put(columnName[j], data[i++]);
            }
            db.insert(tableName, null, cv);
        }
    }
}