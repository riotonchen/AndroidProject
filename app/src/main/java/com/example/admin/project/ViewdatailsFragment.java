package com.example.admin.project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ViewdatailsFragment extends Fragment {
    private static final String TAG="ViewdatailsFragment";
    private Button btnTab;
    private TextView txv_date,txvDetailCount,txvDetialTotalAsset;
    private String date_start, date_end;
    DBHelper DH;
    SQLiteDatabase db;
    SimpleAdapter adapter;
    //14785private SimpleDateFormat yyyymmdd  =  new SimpleDateFormat ("YYYY-MM-DD", Locale.TAIWAN);
    List<Map<String,String>> detailList=new ArrayList<Map<String,String>>();
    Cursor cur;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewdetails_fragment, container, false);
        txv_date = view.findViewById(R.id.textView34);//今日日期
        date_start = getActivity().getIntent().getExtras().getString("date_start");
        date_end = getActivity().getIntent().getExtras().getString("date_end");
        txv_date.setText(date_start + "~" + date_end);
        txvDetailCount=view.findViewById(R.id.txvDetailCount);
        txvDetialTotalAsset=view.findViewById(R.id.txvDetialTotalAsset);
        //帶入明細
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        ListView lv = view.findViewById(R.id.detail_lv);
        adapter = new SimpleAdapter(getActivity(), detailList, R.layout.details_item, new String[]{"subsortName", "account", "money", "date"},
                new int[]{R.id.subsortName, R.id.account, R.id.money, R.id.date});
        Query();
        lv.setAdapter(adapter);
        //設定訊息
        //txvDetialTotalAsset=view.findViewById(R.id.txvDetialTotalAsset);
        //int totalAsset = Query("SELECT IFNUll(SUM(initialAmount),0) AS col FROM mbr_memberaccount WHERE accountTypeID <> 4");
        //nt totalLiability = Query("SELECT IFNUll(SUM(initialAmount),0) AS col FROM mbr_memberaccount WHERE accountTypeID = 4");
        //txvDetialTotalAsset.setText("淨資產：$"+Integer.valueOf(totalAsset-totalLiability));
        return view;
    }

    private void Query() {
        try{
            String sqlCmd = "SELECT subsortName,account,FX,money,date,type FROM (SELECT _id,name AS subsortName FROM sys_subsort) AS C " +
                    "INNER JOIN " +
                    "(SELECT B.name AS account,A.amount AS money,B.FX,A.subsortID,A.time AS date,A.type FROM " +
                    "(SELECT subsortID,amount,accountID,time,type FROM mbr_accounting WHERE memberID=1 " +
                    "AND time BETWEEN '"+date_start.replace('/','-')+"' AND '"+date_end.replace('/','-')+"') AS A " +
                    "INNER JOIN " +
                    "(SELECT mbr_memberaccount._id,name,FX FROM sys_account,mbr_memberaccount " +
                    "WHERE sys_account._id=mbr_memberaccount.accountID) AS B " +
                    "ON A.accountID=B._id) AS D " +
                    "ON C._id=D.subsortID " +
                    "ORDER BY date DESC ";
            String SingleDetailAsset="";
            String TotalDetailAsset="";
            int intTotalDetailAsset=0;
            cur = db.rawQuery(sqlCmd, null);
            int rowsCount = cur.getCount();
            txvDetailCount.setText("筆數:"+Integer.valueOf(rowsCount));
            if (rowsCount != 0) {
                cur.moveToFirst();
                for (int i = 0; i < rowsCount; i++) {
                    Map<String, String> row = new HashMap<String, String>();
                    row.put("subsortName", cur.getString(0));
                    row.put("account", cur.getString(1)+"("+cur.getString(2)+")");
                    row.put("money", (cur.getInt(5)==0?"-$":"+$") + Integer.toString(cur.getInt(3)));
                    row.put("date", cur.getString(4).replace('-','/'));
                    detailList.add(row);

                    SingleDetailAsset=Integer.toString(cur.getInt(3));
                    if(cur.getInt(5)==0){
                        intTotalDetailAsset-=Integer.parseInt(SingleDetailAsset);
                    }
                    else {
                        intTotalDetailAsset+=Integer.parseInt(SingleDetailAsset);
                    }
                    //算出總計

                    cur.moveToNext();
                }
                TotalDetailAsset=Integer.toString(intTotalDetailAsset);
                txvDetialTotalAsset.setText("總計：$"+TotalDetailAsset);
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(), "無資料", Toast.LENGTH_SHORT).show();
                txvDetialTotalAsset.setText("總計：$0");
            }
            cur.close();
        }catch(Exception ex){
            Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private int Query(String sqlCmd) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Cursor cur = db.rawQuery(sqlCmd, null);
            int rowsCount = cur.getCount();
            if (rowsCount != 0) {
                cur.moveToFirst();
                for (int i = 0; i < rowsCount; i++) {
                    arrayList.add(cur.getString(0));
                    cur.moveToNext();
                }
            }
            cur.close();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }
        return Integer.valueOf(arrayList.get(0));
    }


}
