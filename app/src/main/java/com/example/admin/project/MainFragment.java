package com.example.admin.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainFragment extends Fragment {
    private static final String TAG="MainFragment";
    private Calendar datetime = Calendar.getInstance(Locale.TAIWAN);
    private SimpleDateFormat yyyyMMddEEEEE  =  new SimpleDateFormat ("yyyy/MM/dd EEEE", Locale.TAIWAN);
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    private Button btn1,btn2,btn3,btn4;
    private TextView txv2,txv6,txv10,txv14;
    private Intent it;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment,container,false);
        datetime.setFirstDayOfWeek(Calendar.MONDAY);
        btn1 = (Button) view.findViewById(R.id.button);//記一筆
        btn2 = (Button) view.findViewById(R.id.button2);//分類管理
        btn3 = (Button) view.findViewById(R.id.button3);//帳戶管理
        btn4 = (Button) view.findViewById(R.id.button4);//統計分析
        txv2 = (TextView) view.findViewById(R.id.textView2);//今日日期
        txv6 = (TextView) view.findViewById(R.id.textView6);//本週日期
        txv10 = (TextView) view.findViewById(R.id.textView10);//本月日期
        txv14 = (TextView) view.findViewById(R.id.textView14);//本年日期

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), AccountingActivity.class);
                startActivity(it);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), SortActivity.class);
                startActivity(it);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), AccountActivity.class);
                startActivity(it);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), AccountingActivity.class);
                startActivity(it);
            }
        });

        //今日日期
        datetime.set(java.util.Calendar.HOUR_OF_DAY, 0);
        String today = yyyyMMddEEEEE.format(datetime.getTime());
        txv2.setText(today);

        //本週日期
        datetime.set(java.util.Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String weekstart = yyyyMMdd.format(datetime.getTime());
        datetime.set(java.util.Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String weekend = yyyyMMdd.format(datetime.getTime());
        txv6.setText(weekstart+"~"+weekend);

        //本月日期
        datetime.set(Calendar.DAY_OF_MONTH,1);
        String monthstart = yyyyMMdd.format(datetime.getTime());
        datetime.roll(Calendar.DAY_OF_MONTH,-1);
        String monthend = yyyyMMdd.format(datetime.getTime());
        txv10.setText(monthstart+"~"+monthend);

        //本年日期
        datetime.set(Calendar.DAY_OF_YEAR,1);
        String yearstart = yyyyMMdd.format(datetime.getTime());
        datetime.roll(Calendar.DAY_OF_YEAR,-1);
        String yearend = yyyyMMdd.format(datetime.getTime());
        txv14.setText(yearstart+"~"+yearend);


        return view;
    }

}
