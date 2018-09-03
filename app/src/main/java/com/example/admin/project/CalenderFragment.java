package com.example.admin.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalenderFragment extends Fragment {
    private Calendar datetime = Calendar.getInstance(Locale.TAIWAN);
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    private CalendarView calendarView;
    private TextView txtTest;
    SimpleAdapter adapter;
    ListView lv;
    List<Map<String,String>> accountingValue=new ArrayList<Map<String,String>>();
    String Date;
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calender_fragment,container,false);

        calendarView=view.findViewById(R.id.calendarView);
        txtTest=view.findViewById(R.id.txtTest);
        lv=view.findViewById(R.id.accounting_lv);

        Date=yyyyMMdd.format(datetime.getTime());
        txtTest.setText(Date);

        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        adapter = new SimpleAdapter(getActivity(), accountingValue, R.layout.details_item, new String[]{"subsortName", "account", "money", "date", "txvId"},
                new int[]{R.id.subsortName, R.id.account, R.id.money, R.id.date, R.id.txvId});
        Query();
        lv.setAdapter(adapter);
        DH.close();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                DH = new DBHelper(getActivity());
                db = DH.getReadableDatabase();
                if((month+1)/10<1){//月份為個位數的情況
                    if(dayOfMonth/10<1) {//日期為個位數的情況
                        Date = String.valueOf(year) + "/0" + String.valueOf(month + 1) + "/0" + String.valueOf(dayOfMonth);
                        txtTest.setText(Date);
                    }
                    else {//日期不為個位數的情況
                        Date=String.valueOf(year)+"/0"+String.valueOf(month+1)+"/"+String.valueOf(dayOfMonth);
                        txtTest.setText(Date);
                    }
                }
                else {//月份不為個位數的情況
                    if(dayOfMonth/10<1) {//日期為個位數的情況
                        Date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/0" + String.valueOf(dayOfMonth);
                        txtTest.setText(Date);
                    }
                    else {//日期不為個位數的情況
                        Date=String.valueOf(year)+"/"+String.valueOf(month+1)+"/"+String.valueOf(dayOfMonth);
                        txtTest.setText(Date);
                    }
                }
                accountingValue.clear();
                adapter = new SimpleAdapter(getActivity(), accountingValue, R.layout.details_item, new String[]{"subsortName", "account", "money", "date", "txvId"},
                        new int[]{R.id.subsortName, R.id.account, R.id.money, R.id.date, R.id.txvId});
                Query();
                lv.setAdapter(adapter);
                DH.close();

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String txvId=((TextView) view.findViewById(R.id.txvId)).getText().toString();
                final String money=((TextView) view.findViewById(R.id.money)).getText().toString();
                final String date=((TextView)view.findViewById(R.id.date)).getText().toString();
                PopupMenu popup=new PopupMenu(getActivity(),view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.Edit:
                                if(money.indexOf("-")!=-1){//如果為支出
                                    Intent it = new Intent(getActivity(), UpdateAccountActivity.class);
                                    it.putExtra("id",txvId);
                                    it.putExtra("money",money.substring(2));
                                    it.putExtra("date" ,date);
                                    startActivity(it);
                                }
                                else{//如果為收入
                                    Intent it = new Intent(getActivity(), UpdateAccount2.class);
                                    it.putExtra("money",money.substring(2));
                                    it.putExtra("id",txvId);
                                    it.putExtra("money",money.substring(2));
                                    it.putExtra("date" ,date);
                                    startActivity(it);
                                }
                                return true;
                            case R.id.Delete:
                                DH=new DBHelper(getActivity());
                                db=DH.getWritableDatabase();
                                TextView txv=new TextView(getActivity());
                                txv.setText("確定要刪除這筆帳務?");txv.setTextSize(24);
                                new AlertDialog.Builder(getActivity())
                                        .setView(txv)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //讀取這筆帳務的帳戶
                                                String sqlCmd = "SELECT accountID FROM mbr_accounting WHERE _id=\"" + txvId + "\"";
                                                String accountID="";
                                                Cursor cur = db.rawQuery(sqlCmd, null);
                                                if (cur.getCount() > 0) {

                                                    cur.moveToFirst();    // 移到第 1 筆資料
                                                    do {        // 逐筆讀出資料(只會有一筆)
                                                        accountID = cur.getString(0);
                                                    } while (cur.moveToNext());    // 有一下筆就繼續迴圈
                                                }
                                                //讀取這筆帳務的帳戶的balance
                                                sqlCmd = "SELECT balance FROM mbr_memberaccount WHERE accountID=\"" + accountID + "\"";
                                                String balance="";
                                                cur = db.rawQuery(sqlCmd, null);
                                                if (cur.getCount() > 0) {

                                                    cur.moveToFirst();    // 移到第 1 筆資料
                                                    do {        // 逐筆讀出資料(只會有一筆)
                                                        balance = cur.getString(0);
                                                    } while (cur.moveToNext());    // 有一下筆就繼續迴圈
                                                }
                                                int iBalance=Integer.parseInt(balance);
                                                //讀取這筆帳務的金額
                                                sqlCmd = "SELECT amount FROM mbr_accounting WHERE _id=\"" + txvId + "\"";
                                                String amount="";
                                                cur = db.rawQuery(sqlCmd, null);
                                                if (cur.getCount() > 0) {

                                                    cur.moveToFirst();    // 移到第 1 筆資料
                                                    do {        // 逐筆讀出資料(只會有一筆)
                                                        amount = cur.getString(0);
                                                    } while (cur.moveToNext());    // 有一下筆就繼續迴圈
                                                }
                                                int iAmount=Integer.parseInt(amount);
                                                //修改帳戶金額
                                                if(money.indexOf("-")!=-1){//如果為支出
                                                    String newBalance = Integer.toString(iBalance+iAmount);
                                                    sqlCmd="UPDATE mbr_memberaccount SET balance =\"" + newBalance + "\""+
                                                            "  WHERE mbr_memberaccount.accountID =\"" + accountID + "\"";
                                                    db.execSQL(sqlCmd);
                                                }
                                                else {//如果為收入
                                                    String newBalance = Integer.toString(iBalance-iAmount);
                                                    sqlCmd="UPDATE mbr_memberaccount SET balance =\"" + newBalance + "\""+
                                                            "  WHERE mbr_memberaccount.accountID =\"" + accountID + "\"";
                                                    db.execSQL(sqlCmd);
                                                }

                                                //刪除帳務
                                                db.delete("mbr_accounting","_id="+txvId,null);
                                                Toast.makeText(getActivity().getApplicationContext(), "刪除成功", Toast.LENGTH_SHORT).show();
                                                db.close();
                                                Intent it = new Intent(getActivity(), NewMainActivity.class);
                                                startActivity(it);
                                                //Intent it = new Intent(getActivity(), MainActivity.class);
                                                //startActivity(it);
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(getActivity().getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                                                db.close();
                                            }
                                        })
                                        .show();
                                return true;
                        }
                        return false;
                    }
                });
                popup.inflate(R.menu.menu_accounting);
                popup.show();



            }
        });

        return view;

    }
    private void Query() {
        String sqlCmd = sqlCmd = "SELECT subsortName,account,FX,money,date,type,id FROM (SELECT _id,name AS subsortName FROM sys_subsort) AS C " +
                "INNER JOIN " +
                "(SELECT B.name AS account,A.amount AS money,B.FX,A.subsortID,A.time AS date,A.type,A._id AS id FROM " +
                "(SELECT subsortID,amount,accountID,time,type,_id FROM mbr_accounting WHERE memberID=1 " +
                "AND time BETWEEN '" + Date.replace('/', '-') + "' AND '" + Date.replace('/', '-') + "') AS A " +
                "INNER JOIN " +
                "(SELECT mbr_memberaccount._id,name,FX FROM sys_account,mbr_memberaccount " +
                "WHERE sys_account._id=mbr_memberaccount.accountID) AS B " +
                "ON A.accountID=B._id) AS D " +
                "ON C._id=D.subsortID " +
                "ORDER BY date DESC ";

        cur = db.rawQuery(sqlCmd, null);
        int rowsCount = cur.getCount();

        if (rowsCount != 0) {
            cur.moveToFirst();
            for (int i = 0; i < rowsCount; i++) {
                Map<String, String> row = new HashMap<String, String>();
                row.put("subsortName", cur.getString(0));
                row.put("account", cur.getString(1)+"("+cur.getString(2)+")");
                row.put("money", (cur.getInt(5)==0?"-$":"+$") + Integer.toString(cur.getInt(3)));
                row.put("date", cur.getString(4).replace('-','/'));
                row.put("txvId",cur.getString(6));
                accountingValue.add(row);
                cur.moveToNext();
            }
        }
    }







}
