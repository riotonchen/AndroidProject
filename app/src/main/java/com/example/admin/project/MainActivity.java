package com.example.admin.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    Calendar datetime = Calendar.getInstance(Locale.TAIWAN);
    SimpleDateFormat yyyyMMddEEEEE  =  new SimpleDateFormat ("yyyy/MM/dd EEEE", Locale.TAIWAN);
    SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    TextView txv2,txv6,txv10,txv14;

    Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        datetime.setFirstDayOfWeek(Calendar.MONDAY);
        txv2 = (TextView) findViewById(R.id.textView2);//今日日期
        txv6 = (TextView) findViewById(R.id.textView6);//本週日期
        txv10 = (TextView) findViewById(R.id.textView10);//本月日期
        txv14 = (TextView) findViewById(R.id.textView14);//本年日期

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

    }

    public void OnClick(View v){
        it=new Intent(this,Main6Activity.class);
        startActivity(it);
    }
}
