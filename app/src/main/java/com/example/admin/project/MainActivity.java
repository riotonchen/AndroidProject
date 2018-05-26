package com.example.admin.project;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.TabLayout;
import android.view.View;
import java.util.Calendar;
import java.util.Locale;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    Calendar datetime = Calendar.getInstance(Locale.TAIWAN);
    private SQLiteDatabase db;
    private final String DB_NAME = "MYLOCALDB";
    Calendar datetime = Calendar.getInstance(Locale.TAIWAN);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

<<<<<<< HEAD
=======
<<<<<<< HEAD
        //SetUpLocalDB(DB_NAME);//建立資料庫及資料表
=======
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80
        SharedPreferences myPref=getPreferences(MODE_PRIVATE);
        boolean isFirst=myPref.getBoolean("isFirst",true);//第一次找不到為true
        if(isFirst){
            SetUpLocalDB(DB_NAME);//建立SQLite資料庫及資料表
            SharedPreferences.Editor editor=getPreferences(MODE_PRIVATE).edit();
            editor.putBoolean("isFirst",false);
            editor.commit();
        }
<<<<<<< HEAD
=======

>>>>>>> DBCoding
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment(), "123");
        //adapter.addFragment(new ExpenseSortFragment(),"支出分類");
        viewPager.setAdapter(adapter);
    }
private void SetUpLocalDB(String dbName) {
        // Android 使用 SQLite 資料庫的方法
        // http://jim690701.blogspot.tw/2012/06/android-sqlite.html
        // http://sweeteason.pixnet.net/blog/post/37364146-android-%E4%BD%BF%E7%94%A8-sqlite-%E8%B3%87%E6%96%99%E5%BA%AB%E7%9A%84%E6%96%B9%E6%B3%95

        //取得資料庫
        DBHelper DH = new DBHelper(this);
<<<<<<< HEAD
        db = DH.getReadableDatabase();
        //  db = openOrCreateDatabase(dbName, android.content.Context.MODE_PRIVATE, null);
        String TB_NAME;
=======
<<<<<<< HEAD
        db=DH.getReadableDatabase();

        //建資料庫
        db = openOrCreateDatabase(dbName, android.content.Context.MODE_PRIVATE, null);
        //建資料表
        final String TB_NAME;
=======
        db = DH.getReadableDatabase();
        //  db = openOrCreateDatabase(dbName, android.content.Context.MODE_PRIVATE, null);
        String TB_NAME;
>>>>>>> DBCoding
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80
        String[] col;
        String[] data;
        String cmd;
        Cursor cur;
        //新增Member資料
        TB_NAME = "mbr_Member";
        col = new String[]{"name", "nickname", "email", "password", "renew_time"};
        data = new String[]{"王小明", "小明", "A1234567@pccu.edu.tw", "pwd123", datetime.getTime().toString()};
        AddData(TB_NAME, col, data);
        //新增SortClass資料
        TB_NAME = "sys_Sort";
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
                "其他收入", "1", null};
        AddData(TB_NAME, col, data);
<<<<<<< HEAD
=======
        /*
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
<<<<<<< HEAD
        db.execSQL(cmd);
        cur = db.rawQuery("select * FROM " + TB_NAME, null);
        if (cur.getCount() == 0) {//沒資料才要新增
            AddData(TB_NAME, col, data);
        }
=======
        db.execSQL(cmd);*/
        /*cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);
        if (cur.getCount() == 0) {//沒資料才要新增
            AddData(TB_NAME, col, data);
        }*/
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80

        //新增SubSortClass資料
        TB_NAME = "sys_SubSort";
        col = new String[]{"name", "type", "icon"};
        data = new String[]{"早餐", "0", null,"午餐", "0", null,"晚餐", "0", null,
                "菸酒茶資料", "0", null,"水果零食", "0", null,"公共交通費", "0", null,
                "計程車費", "0", null,"汽機車加油費", "0", null,"火車飛機費", "0", null,
                "日常用品", "0", null,"水電瓦斯", "0", null,"房租房貸", "0", null,
                "電話費", "0", null,"手機費", "0", null,"網路費", "0", null,
                "有線電視費", "0", null,"運動健身", "0", null,"朋友聚餐", "0", null,
                "休閒玩樂", "0", null,"寵物寶貝", "0", null,"旅遊度假", "0", null,
                "奢侈敗家", "0", null,"書報雜誌", "0", null,"上課進修", "0", null,
                "網上學習", "0", null,"送禮請客", "0", null,"尊親費用", "0", null,
                "慈善捐款", "0", null,"生病醫療", "0", null,"勞健保費", "0", null,
                "保險費用", "0", null,"健康食品", "0", null,"美容養生", "0", null,
                "銀行手續", "0", null,"投資損益", "0", null,"分期付款", "0", null,
                "稅捐支出", "0", null,"賠償罰款", "0", null,"其他支出", "0", null,
                "遺失拾獲", "0", null,"呆帳損失", "0", null,"薪水收入", "1", null,
                "利息收入", "1", null,"兼職收入", "1", null,"獎金收入", "1", null,
                "禮金收入", "1", null,"意外收入", "1", null,"投資收入", "1", null};
        AddData(TB_NAME, col, data);
        //新增MemberSort資料
        TB_NAME = "mbr_MemberSort";
        col = new String[]{"memberID", "sortID","budget" ,"icon"};
        data = new String[]{"1", "1","4000", null,
                            "1", "2","300", null,
                            "1", "4","499", null,
                            "1", "5","500", null};//休閒娛樂
        AddData(TB_NAME, col, data);
        //新增MemberSubSort資料
        TB_NAME = "mbr_MemberSubSort";
        col = new String[]{"memberID", "subsortID","budget" ,"icon"};
        data = new String[]{"1","18","300", null,//朋友聚餐
                            "1","19","200", null,};//休閒玩樂
        AddData(TB_NAME, col, data);
        //新增Account資料
        TB_NAME = "sys_Account";
        col = new String[]{"name"};
        data = new String[]{"現金(新台幣)","悠遊卡","銀行簽帳卡"};
        AddData(TB_NAME, col, data);
        //新增AccountType資料
        TB_NAME = "sys_AccountType";
        col = new String[]{"name"};
        data = new String[]{"現金","資產","儲值卡","負債"};
        AddData(TB_NAME, col, data);
        //新增MemberAccount資料
        TB_NAME = "mbr_MemberAccount";
        col = new String[]{"memberID","accountID","accountTypeID","initialAmount","FX","comment"};
        data = new String[]{"1","1","1","72500","1:1",null,
                            "1","3","2","55000","1:1",null};
        AddData(TB_NAME, col, data);
        //新增MemberBudget資料
        TB_NAME = "mbr_MemberBudget";
        col = new String[]{"memberID","month","budget"};
        data = new String[]{"1","6","35700"};
        AddData(TB_NAME, col, data);
        //新增PROJECT資料
        TB_NAME = "sys_Project";
        col = new String[]{"name"};
        data = new String[]{"無特別專案","出差","出國旅遊","採購案"};
        AddData(TB_NAME, col, data);
        //新增MemberProject資料
        TB_NAME = "mbr_MemberProject";
        col = new String[]{"memberID","projectID"};
        data = new String[]{"1","1",
                            "1","4"};
        AddData(TB_NAME, col, data);
        //新增Expend資料
        TB_NAME = "mbr_Expend";
        col = new String[]{"memberID","time","sortID","subsortID","amount",
                           "accountID","projectID","invoiceNum","picture","comment"};
        data = new String[]{"1",datetime.getTime().toString(),"1",null,"65","1","1","EV54838339",null,null,
                            "1",datetime.getTime().toString(),"3","1","80","2","1","EV58635266",null,null,};
        AddData(TB_NAME, col, data);
        //新增Income資料
        TB_NAME = "mbr_Income";
        col = new String[]{"memberID","time","sortID","subsortID","amount",
                "accountID","projectID","invoiceNum","picture","comment"};
        data = new String[]{"1",datetime.getTime().toString(),"11","42","37000","2","1",null,null,null};
        AddData(TB_NAME, col, data);
        //轉帳資料省略
<<<<<<< HEAD
=======
>>>>>>> DBCoding
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80
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