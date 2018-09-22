package com.example.admin.project;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.client.methods.HttpGet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewMainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private SQLiteDatabase db;
    private final String DB_NAME = "MYLOCALDB";
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    private Intent it;
    private String name,monthstart,monthend;
    private TextView username;
    //嚴禁 'Y'
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    Calendar datetime = Calendar.getInstance(Locale.TAIWAN);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        //username = (TextView) findViewById(R.id.username);

        SharedPreferences myPref = getPreferences(MODE_PRIVATE);
        boolean isFirst = myPref.getBoolean("isFirst", true);//第一次找不到為true
        if (isFirst) {
            SetUpLocalDB();//建立SQLite資料庫及資料表
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putBoolean("isFirst", false);
            editor.commit();
        }
        /*int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,new MainFragment())
                    .addToBackStack(null)
                    .commit();
        }*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);

        datetime.set(Calendar.DAY_OF_MONTH,1);
        monthstart = yyyyMMdd.format(datetime.getTime());
        datetime.roll(Calendar.DAY_OF_MONTH,-1);
        monthend = yyyyMMdd.format(datetime.getTime());

        // 為navigatin_view設置點擊事件
        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.Member:
                        it = new Intent(NewMainActivity.this, LoginActivity.class);
                        startActivity(it);
                        return true;
                    case R.id.SearchFriend:
                        it = new Intent(NewMainActivity.this, SearchFriendActivity.class);
                        startActivity(it);
                        return true;
                    case R.id.sort:
                        it = new Intent(NewMainActivity.this, SortActivity.class);
                        it.putExtra("monthstart", monthstart);
                        it.putExtra("monthend", monthend);
                        startActivity(it);
                        return true;
                    case R.id.sta:
                        it = new Intent(NewMainActivity.this, MonthSortPieChartActivity.class);
                        it.putExtra("monthstart", monthstart);
                        it.putExtra("monthend", monthend);
                        startActivity(it);
                        return true;
                    case R.id.account2:
                        it = new Intent(NewMainActivity.this, AccountActivity.class);
                        startActivity(it);
                        return true;
                    case R.id.Card:
                        it = new Intent(NewMainActivity.this,CardActivity.class);
                        startActivity(it);
                        return true;
                    case R.id.Logout:
                        new AlertDialog.Builder(NewMainActivity.this)
                                .setTitle("確認登出")
                                .setIcon(R.drawable.logo2)
                                .setPositiveButton("確定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SharedPreferences.Editor editor = getSharedPreferences("jwt_token", MODE_PRIVATE).edit();
                                                editor.clear(); //清除Token
                                                editor.apply();
                                                it = new Intent(NewMainActivity.this, LoginActivity.class);
                                                startActivity(it);
                                            }
                                        })
                                .setNegativeButton("取消",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                            }
                                        }).show();
                        return true;
                }
                return false;
            }
        });



        //改左側選單上方
        if(navigation_view.getHeaderCount() > 0) {
            View header = navigation_view.getHeaderView(0);

            TextView username = (TextView) header.findViewById(R.id.username);
            Intent intent = this.getIntent();
            if(intent.hasExtra("username")){
                name = this.getIntent().getExtras().getString("username");
                username.setText(name);
            }

        }




    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(NewMainActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.drawable.logo2)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).show();
        }
        return true;
    }
    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewMainFragment(), "首頁");
        adapter.addFragment(new CalenderFragment(),"月曆");
        viewPager.setAdapter(adapter);
    }

    private void SetUpLocalDB() {
        // Android 使用 SQLite 資料庫的方法
        // http://jim690701.blogspot.tw/2012/06/android-sqlite.html
        // http://sweeteason.pixnet.net/blog/post/37364146-android-%E4%BD%BF%E7%94%A8-sqlite-%E8%B3%87%E6%96%99%E5%BA%AB%E7%9A%84%E6%96%B9%E6%B3%95

        //取得資料庫
        DBHelper DH = new DBHelper(this);
        db = DH.getReadableDatabase();
        //  db = openOrCreateDatabase(dbName, android.content.Context.MODE_PRIVATE, null);
        String TB_NAME;
        String[] col;
        String[] data;
        String cmd;
        Cursor cur;
        //新增mbr_membertype資料
        //TB_NAME = "mbr_membertype";
        //col = new String[]{"membertype_id", "name", "renew_time"};
        //data = new String[]{"1", "FB帳號", datetime.getTime().toString(),
        //"2", "Google帳號", datetime.getTime().toString(),
        //"3", "本站帳號", datetime.getTime().toString(),
        //"4", "管理員", datetime.getTime().toString()};
        //AddData(TB_NAME, col, data);
        //新增mbr_member資料
        TB_NAME = "mbr_membertype";
        col = new String[]{"membertype_id", "name"};
        data = new String[]{"1", "FB帳號",
                "2", "Google帳號",
                "3", "本站帳號",
                "4", "管理員"};
        AddData(TB_NAME, col, data);
        //新增mbr_member資料
        Calendar datetime = Calendar.getInstance(Locale.TAIWAN);
        TB_NAME = "mbr_member";
        col = new String[]{"name", "nickname", "email", "password", "renew_time"};
        data = new String[]{"王小明", "小明", "A1234567@pccu.edu.tw", "pwd123", datetime.getTime().toString()};
        AddData(TB_NAME, col, data);
        //新增sys_sort資料
        TB_NAME = "sys_sort";
        col = new String[]{"name", "type", "icon"};
        data = new String[]{"食品酒水", "0", null,
                "行車交通", "0", null,
                "居家物業", "0", null,
                "交流通訊", "0", null,
                "休閒娛樂", "0", null,
                "進修學習", "0", null,
                "人情往來", "0", null,
                "醫療保健", "0", null,
                "金融保險", "0", null,
                "其他雜項", "0", null,
                "工作收入", "1", null,
                "其他收入", "1", null,
                "電子發票帶入", "0", null};
        AddData(TB_NAME, col, data);

        //新增sys_subsort資料
        TB_NAME = "sys_subsort";
        col = new String[]{"name", "type", "icon"};
        data = new String[]{"早餐", "0", null, "午餐", "0", null, "晚餐", "0", null,
                "菸酒茶飲料", "0", null, "水果零食", "0", null, "公共交通費", "0", null,
                "計程車費", "0", null, "汽機車加油費", "0", null, "火車飛機費", "0", null,
                "日常用品", "0", null, "水電瓦斯", "0", null, "房租房貸", "0", null,
                "電話費", "0", null, "手機費", "0", null, "網路費", "0", null,
                "有線電視費", "0", null, "運動健身", "0", null, "朋友聚餐", "0", null,
                "休閒玩樂", "0", null, "寵物寶貝", "0", null, "旅遊度假", "0", null,
                "奢侈敗家", "0", null, "書報雜誌", "0", null, "上課進修", "0", null,
                "網上學習", "0", null, "送禮請客", "0", null, "尊親費用", "0", null,
                "慈善捐款", "0", null, "生病醫療", "0", null, "勞健保費", "0", null,
                "保險費用", "0", null, "健康食品", "0", null, "美容養生", "0", null,
                "銀行手續", "0", null, "投資損益", "0", null, "分期付款", "0", null,
                "稅捐支出", "0", null, "賠償罰款", "0", null, "其他支出", "0", null,
                "遺失拾獲", "0", null, "呆帳損失", "0", null, "薪水收入", "1", null,
                "利息收入", "1", null, "兼職收入", "1", null, "獎金收入", "1", null,
                "禮金收入", "1", null, "意外收入", "1", null, "投資收入", "1", null,
                "電子發票", "0", null};
        AddData(TB_NAME, col, data);
        //新增mbr_membersort資料
        TB_NAME = "mbr_membersort";
        col = new String[]{"memberID", "sortID", "budget", "amount", "icon"};
        data = new String[]{"1", "1", "4000", "0", null,
                "1", "2", "300", "0", null,
                "1", "3", "0", "0", null,
                "1", "4", "499", "0", null,//休閒娛樂
                "1", "5", "500", "0", null,
                "1", "6", "0", "0", null,
                "1", "7", "0", "0", null,
                "1", "8", "0", "0", null,
                "1", "9", "0", "0", null,
                "1", "10", "0", "0", null,
                "1", "11", "0", "0", null,
                "1", "12", "0", "0", null,
                "1", "13", "0", "0", null};
        AddData(TB_NAME, col, data);
        //新增mbr_membersubsort資料
        TB_NAME = "mbr_membersubsort";
        col = new String[]{"memberID", "memberSortID", "subsortID", "budget", "amount", "icon"};
        data = new String[]{"1", "1", "1", "0", "0", null, "1", "1", "2", "0", "0", null, "1", "1", "3", "0", "0", null,
                "1", "1", "4", "0", "0", null, "1", "1", "5", "0", "0", null, "1", "2", "6", "0", "0", null,
                "1", "2", "7", "0", "0", null, "1", "2", "8", "0", "0", null, "1", "2", "9", "0", "0", null,
                "1", "3", "10", "0", "0", null, "1", "3", "11", "0", "0", null, "1", "3", "12", "0", "0", null,
                "1", "4", "13", "0", "0", null, "1", "4", "14", "0", "0", null, "1", "4", "15", "0", "0", null,
                "1", "4", "16", "0", "0", null, "1", "5", "17", "0", "0", null, "1", "5", "18", "300", "0", null,
                "1", "5", "19", "200", "0", null, "1", "5", "0", "0", "0", null, "1", "5", "21", "0", "0", null,
                "1", "5", "22", "0", "0", null, "1", "6", "23", "0", "0", null, "1", "6", "24", "0", "0", null,
                "1", "6", "25", "0", "0", null, "1", "7", "26", "0", "0", null, "1", "7", "27", "0", "0", null,
                "1", "7", "28", "0", "0", null, "1", "8", "29", "0", "0", null, "1", "8", "30", "0", "0", null,
                "1", "8", "31", "0", "0", null, "1", "8", "32", "0", "0", null, "1", "8", "33", "0", "0", null,
                "1", "9", "34", "0", "0", null, "1", "9", "35", "0", "0", null, "1", "9", "36", "0", "0", null,
                "1", "9", "37", "0", "0", null, "1", "9", "38", "0", "0", null, "1", "10", "39", "0", "0", null,
                "1", "10", "40", "0", "0", null, "1", "10", "41", "0", "0", null, "1", "11", "42", "0", "0", null,
                "1", "11", "43", "0", "0", null, "1", "11", "44", "0", "0", null, "1", "11", "45", "0", "0", null,
                "1", "12", "46", "0", "0", null, "1", "12", "47", "0", "0", null, "1", "12", "48", "0", "0", null,
                "1", "13", "49", "0", "0", null};
        AddData(TB_NAME, col, data);
        //新增sys_account資料
        TB_NAME = "sys_account";
        col = new String[]{"name"};
        data = new String[]{"現金(新台幣)", "悠遊卡", "銀行簽帳卡"};
        AddData(TB_NAME, col, data);
        //新增sys_accounttype資料
        TB_NAME = "sys_accounttype";
        col = new String[]{"name"};
        data = new String[]{"現金", "資產", "儲值卡", "負債"};
        AddData(TB_NAME, col, data);
        //新增mbr_memberaccount資料
        TB_NAME = "mbr_memberaccount";
        col = new String[]{"memberID", "accountID", "accountTypeID", "initialAmount", "balance", "FX", "comment"};
        data = new String[]{"1", "1", "1", "72500", "72500", "1:1", null,
                "1", "2", "3", "500", "500", "1:1", null,
                "1", "3", "2", "55000", "55000", "1:1", null};
        AddData(TB_NAME, col, data);
        //新增mbr_memberbudget資料
        TB_NAME = "mbr_memberbudget";
        col = new String[]{"memberID", "month", "budget"};
        data = new String[]{"1", "6", "35700"};
        AddData(TB_NAME, col, data);
        //新增sys_project資料
        TB_NAME = "sys_project";
        col = new String[]{"name"};
        data = new String[]{"無特別專案", "出差", "出國旅遊", "採購案"};
        AddData(TB_NAME, col, data);
        //新增mbr_memberproject資料
        TB_NAME = "mbr_memberproject";
        col = new String[]{"memberID", "projectID"};
        data = new String[]{"1", "1",
                "1", "2",
                "1", "3",
                "1", "4"};
        AddData(TB_NAME, col, data);
        //新增mbr_accounting資料
        TB_NAME = "mbr_accounting";
        col = new String[]{"memberID", "time", "type", "sortID", "subsortID", "amount",
                "accountID", "projectID", "invoiceNum", "picture", "comment"};
        data = new String[]{
                "1", "2018-05-12", "0", "1", "1", "65", "1", "1", "EV54838339", null, null,
                "1", "2018-05-18", "0", "1", "3", "80", "2", "1", "EV58635266", null, null,
                "1", "2018-05-23", "0", "3", "10", "250", "3", "1", null, null, null,
                "1", "2018-06-03", "1", "11", "42", "37000", "3", "1", null, null, null};
        AddData(TB_NAME, col, data);
        /*//新增mbr_income資料
        TB_NAME = "mbr_income";
        col = new String[]{"memberID", "time", "sortID", "subsortID", "amount",
                "accountID", "projectID", "invoiceNum", "picture", "comment"};
        data = new String[]{"1", datetime.getTime().toString(), "11", "42", "37000", "2", "1", null, null, null};
        AddData(TB_NAME, col, data);*/
        //轉帳資料省略
    }

    public void AddData(String tableName, String[] columnName, String[] data) {
        //db = openOrCreateDatabase(DB_NAME, android.content.Context.MODE_PRIVATE, null);
        ContentValues cv ;
        for (int i = 0; i < data.length; ) {
            cv = new ContentValues(columnName.length);
            for (int j = 0; j < columnName.length; j++) {
                cv.put(columnName[j], data[i++]);
            }
            db.insert(tableName, null, cv);
        }
    }
    public void opendraw(){
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.openDrawer(Gravity.START);
    }



}
