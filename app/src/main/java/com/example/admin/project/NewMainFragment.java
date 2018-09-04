package com.example.admin.project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class NewMainFragment extends Fragment {
    private static final String TAG="MainFragment";
    CarouselView customCarouselView;
    private Calendar datetime = Calendar.getInstance(Locale.TAIWAN);
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    String[] sampleTitles = {"總資產", "收支比"};
    String[] sampleTitles2 = {"$1,999", "20%"};
    private Intent it;
    public LinearLayout set,classification,account,invoice,analysis;
    private String today , weekstart , weekend , monthstart , monthend , yearstart , yearend;
    private TextView txvMainTodayExpense,txvMainMonthExpense,textView32;
    private Button btn1,btn2,btnclassification,btnInvoice,btnaccount,btnanalysis;
    private ImageView todayExpense,login;
    DBHelper DH;
    SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_main,container,false);

        set = (LinearLayout) view.findViewById(R.id.set);
        classification = (LinearLayout) view.findViewById(R.id.classification);
        account = (LinearLayout) view.findViewById(R.id.account);
        invoice = (LinearLayout) view.findViewById(R.id.invoice);
        analysis = (LinearLayout) view.findViewById(R.id.analysis);
        btn1 = (Button) view.findViewById(R.id.button21);//記一筆
        btn2 = (Button) view.findViewById(R.id.button20);//記一筆
        btnclassification = (Button) view.findViewById(R.id.btnclassification);
        btnInvoice = (Button) view.findViewById(R.id.btnInvoice);
        btnaccount = (Button) view.findViewById(R.id.btnaccount);
        btnanalysis = (Button) view.findViewById(R.id.btnanalysis);
        txvMainTodayExpense=view.findViewById(R.id.textView12);
        txvMainMonthExpense=view.findViewById(R.id.textView39);
        todayExpense = view.findViewById(R.id.imageView15);
        login = view.findViewById(R.id.login);
        textView32 = view.findViewById(R.id.textView32);
        //今日日期
        datetime.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today = yyyyMMdd.format(datetime.getTime());

        //本月日期
        datetime.set(Calendar.DAY_OF_MONTH,1);
        monthstart = yyyyMMdd.format(datetime.getTime());
        datetime.roll(Calendar.DAY_OF_MONTH,-1);
        monthend = yyyyMMdd.format(datetime.getTime());

        //登入
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), LoginActivity.class);
                startActivity(it);
            }
        });

        //帳戶管理
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), AccountActivity.class);
                startActivity(it);
            }
        });
        btnaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), AccountActivity.class);
                startActivity(it);
            }
        });

        //統計分析
        analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), MonthSortPieChartActivity.class);
                it.putExtra("monthstart", monthstart);
                it.putExtra("monthend", monthend);
                startActivity(it);
            }
        });
        btnanalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), MonthSortPieChartActivity.class);
                it.putExtra("monthstart", monthstart);
                it.putExtra("monthend", monthend);
                startActivity(it);
            }
        });

        //分類管理
        classification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), SortActivity.class);
                it.putExtra("monthstart", monthstart);
                it.putExtra("monthend", monthend);
                startActivity(it);
            }
        });
        btnclassification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), SortActivity.class);
                it.putExtra("monthstart", monthstart);
                it.putExtra("monthend", monthend);
                startActivity(it);
            }
        });

        //發票
        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it =new Intent(getActivity(),InvoiceActivity.class);
                startActivity(it);
            }
        });
        btnInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it =new Intent(getActivity(),InvoiceActivity.class);
                startActivity(it);
            }
        });

        //記一筆
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), AccountingActivity.class);
                it.putExtra("today", today);
                startActivity(it);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), AccountingActivity.class);
                it.putExtra("today", today);
                startActivity(it);
            }
        });

        //讀取範圍明細收支
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        txvMainTodayExpense.setText(SetListViewBalance(today,today,0));
        txvMainMonthExpense.setText(SetListViewBalance(monthstart,monthend,0));

        todayExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ViewDetails.class);
                it.putExtra("date_start", today);
                it.putExtra("date_end", today);
                it.putExtra("Tag",9);
                startActivity(it);
            }

        });

        textView32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ViewDetails.class);
                it.putExtra("date_start", monthstart);
                it.putExtra("date_end", monthend);
                it.putExtra("Tag",9);
                startActivity(it);
            }

        });

        txvMainMonthExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ViewDetails.class);
                it.putExtra("date_start", monthstart);
                it.putExtra("date_end", monthend);
                it.putExtra("Tag",9);
                startActivity(it);
            }

        });

        customCarouselView = (CarouselView)view.findViewById(R.id.customCarouselView);
        customCarouselView.setPageCount(2);
        customCarouselView.setSlideInterval(4000);
        customCarouselView.setViewListener(viewListener);

        return view;

    }
    // To set custom views
    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {

            View customView = getLayoutInflater().inflate(R.layout.view_custom, null);

            TextView labelTextView = (TextView) customView.findViewById(R.id.labelTextView);
            TextView labelTextView2 = (TextView) customView.findViewById(R.id.labelTextView2);
            //ImageView fruitImageView = (ImageView) customView.findViewById(R.id.fruitImageView);

            //fruitImageView.setImageResource(sampleImages[position]);
            labelTextView.setText(sampleTitles[position]);
            labelTextView2.setText(sampleTitles2[position]);

            customCarouselView.setIndicatorGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);

            return customView;
        }
    };
    private String SetListViewBalance(String start,String end,int type) {
        start = start.replace('/', '-');
        end = end.replace('/', '-');
        ArrayList<String> arrayList = new ArrayList<>();
        String sqlCmd = "SELECT amount,FX" +
                "        FROM (SELECT amount,accountID FROM mbr_accounting WHERE memberID=1" +
                "        AND type=0 AND time BETWEEN '" + start + "' AND '" + end + "') AS A " +
                "        INNER JOIN" +
                "                (SELECT _id,FX FROM mbr_memberaccount WHERE memberID=1) AS B" +
                "        ON A.accountID=B._id";
        try {
            Cursor cur = db.rawQuery(sqlCmd, null);
            int rowsCount = cur.getCount();
            float totalAmount=0;
            if (rowsCount != 0) {
                cur.moveToFirst();
                for (int i = 0; i < rowsCount; i++) {
                    int amount=cur.getInt(0);
                    float fAmount=(float)amount;
                    String FX=cur.getString(1);
                    float fFX=Float.parseFloat(FX.substring(0,FX.indexOf(":")));
                    totalAmount=totalAmount+(fAmount*fFX);
                    cur.moveToNext();
                }
                int iTotalAmount=(int)totalAmount;
                arrayList.add((type == 0 ? "$" : "$") + String.valueOf(iTotalAmount));
            }
            else {
                arrayList.add((type == 0 ? "$" : "$") + String.valueOf(0));
            }
            cur.close();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }
        return arrayList.get(0);
    }







}
