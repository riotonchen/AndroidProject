package com.example.admin.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {
    private static final String TAG="MainFragment";
    private Calendar datetime = Calendar.getInstance(Locale.TAIWAN);
    private SimpleDateFormat yyyyMMddEEEEE  =  new SimpleDateFormat ("yyyy/MM/dd EEEE", Locale.TAIWAN);
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    private Button btn1,btn2,btn3,btn4,btnScan,btnFriend,btnMember,btnInvoice,btnSet;
    private TextView txv2,txv6,txv10,txv14;
    private Intent it;
    private LinearLayout daydetails,weekdetails,monthdetails,yeardetails;
    private String today , weekstart , weekend , monthstart , monthend , yearstart , yearend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment,container,false);

        datetime.setFirstDayOfWeek(Calendar.MONDAY);
        btn1 = (Button) view.findViewById(R.id.button);//記一筆
        btn2 = (Button) view.findViewById(R.id.button2);//分類管理
        btn3 = (Button) view.findViewById(R.id.button3);//帳戶管理
        btn4 = (Button) view.findViewById(R.id.button4);//統計分析
        btnScan = (Button) view.findViewById(R.id.btnScan);//掃描
        btnFriend = (Button) view.findViewById(R.id.btnFriend);//好友
        btnMember = (Button) view.findViewById(R.id.btnMember);//會員
        btnInvoice = (Button) view.findViewById(R.id.btnInvoice);//發票
        btnSet = (Button) view.findViewById(R.id.btnSet);//設定
        txv2 = (TextView) view.findViewById(R.id.textView2);//今日日期
        txv6 = (TextView) view.findViewById(R.id.textView6);//本週日期
        txv10 = (TextView) view.findViewById(R.id.textView10);//本月日期
        txv14 = (TextView) view.findViewById(R.id.textView14);//本年日期
        daydetails = (LinearLayout)view.findViewById(R.id.daydetails);
        weekdetails = (LinearLayout)view.findViewById(R.id.weekdetails);
        monthdetails = (LinearLayout)view.findViewById(R.id.monthdetails);
        yeardetails = (LinearLayout)view.findViewById(R.id.yeardetails);

        //設置刪除線
        btnFriend.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        btnMember.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        btnInvoice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        btnSet.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);


        //今日日期
        datetime.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today = yyyyMMddEEEEE.format(datetime.getTime());
        txv2.setText(today);
        today = yyyyMMdd.format(datetime.getTime());


        //本週日期
        datetime.set(java.util.Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekstart = yyyyMMdd.format(datetime.getTime());
        datetime.set(java.util.Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        weekend = yyyyMMdd.format(datetime.getTime());
        txv6.setText(weekstart+"~"+weekend);

        //本月日期
        datetime.set(Calendar.DAY_OF_MONTH,1);
        monthstart = yyyyMMdd.format(datetime.getTime());
        datetime.roll(Calendar.DAY_OF_MONTH,-1);
        monthend = yyyyMMdd.format(datetime.getTime());
        txv10.setText(monthstart+"~"+monthend);

        //本年日期
        datetime.set(Calendar.DAY_OF_YEAR,1);
        yearstart = yyyyMMdd.format(datetime.getTime());
        datetime.roll(Calendar.DAY_OF_YEAR,-1);
        yearend = yyyyMMdd.format(datetime.getTime());
        txv14.setText(yearstart+"~"+yearend);

        //記一筆
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), AccountingActivity.class);
                it.putExtra("today", today);
                startActivity(it);
            }
        });

        //分類管理
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), SortActivity.class);
                startActivity(it);
            }
        });

        //帳戶管理
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               it = new Intent(getActivity(), AccountActivity.class);
               startActivity(it);
           }
        });

        //統計分析
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), MonthSortPieChartActivity.class);
                startActivity(it);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentIntentIntegrator integrator = new FragmentIntentIntegrator(MainFragment.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("請掃描左邊QR-Code!!");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();

            }
        });


        daydetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ViewDetails.class);
                it.putExtra("date_start", today);
                it.putExtra("date_end", today);
                startActivity(it);
            }

        });
        weekdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ViewDetails.class);
                it.putExtra("date_start", weekstart);
                it.putExtra("date_end", weekend);
                startActivity(it);
            }

        });
        monthdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ViewDetails.class);
                it.putExtra("date_start", monthstart);
                it.putExtra("date_end", monthend);
                startActivity(it);
            }

        });
        yeardetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ViewDetails.class);
                it.putExtra("date_start", yearstart);
                it.putExtra("date_end", yearend);
                startActivity(it);
            }

        });



        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //掃描
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null)
        {
            if (result.getContents()==null)
            {
                Toast.makeText(getActivity(), "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String all = result.getContents();
                it = new Intent(getActivity(), AccountingActivity.class);
                it.putExtra("scanresult", all);
                it.putExtra("today", today);
                startActivity(it);

            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

}
