package com.example.admin.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private ListView listView;
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
        listView=view.findViewById(R.id.detail_lv);
        //帶入明細
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        ListView lv = view.findViewById(R.id.detail_lv);
        adapter = new SimpleAdapter(getActivity(), detailList, R.layout.details_item, new String[]{"subsortName", "account", "money", "date", "txvId"},
                new int[]{R.id.subsortName, R.id.account, R.id.money, R.id.date, R.id.txvId});
        Query();
        lv.setAdapter(adapter);
        //設定訊息
        //txvDetialTotalAsset=view.findViewById(R.id.txvDetialTotalAsset);
        //int totalAsset = Query("SELECT IFNUll(SUM(initialAmount),0) AS col FROM mbr_memberaccount WHERE accountTypeID <> 4");
        //nt totalLiability = Query("SELECT IFNUll(SUM(initialAmount),0) AS col FROM mbr_memberaccount WHERE accountTypeID = 4");
        //txvDetialTotalAsset.setText("淨資產：$"+Integer.valueOf(totalAsset-totalLiability));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String id=((TextView) view.findViewById(R.id.txvId)).getText().toString();
                String money=((TextView) view.findViewById(R.id.money)).getText().toString();
                String date=((TextView)view.findViewById(R.id.date)).getText().toString();
                String sp=Integer.toString(position);//測試用
                if(money.indexOf("-")!=-1){//如果為支出
                    Intent it = new Intent(getActivity(), UpdateAccountActivity.class);
                    it.putExtra("id",id);
                    it.putExtra("money",money.substring(2));
                    it.putExtra("date" ,date);
                    startActivity(it);
                }
                else{//如果為收入
                    Intent it = new Intent(getActivity(), UpdateAccount2.class);
                    it.putExtra("money",money.substring(2));
                    it.putExtra("id",id);
                    it.putExtra("money",money.substring(2));
                    it.putExtra("date" ,date);
                    startActivity(it);
                }
                //Toast.makeText(getActivity(),"你點擊了第"+sp+"項"+"id為"+id+"金額為"+money.substring(2),Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DH=new DBHelper(getActivity());
                db=DH.getWritableDatabase();
                final String txvId=((TextView)view.findViewById(R.id.txvId)).getText().toString();
                final String money=((TextView) view.findViewById(R.id.money)).getText().toString();

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
        });

        return view;
    }

    private void Query() {
        try{
            String sqlCmd = "SELECT subsortName,account,FX,money,date,type,id FROM (SELECT _id,name AS subsortName FROM sys_subsort) AS C " +
                    "INNER JOIN " +
                    "(SELECT B.name AS account,A.amount AS money,B.FX,A.subsortID,A.time AS date,A.type,A._id AS id FROM " +
                    "(SELECT subsortID,amount,accountID,time,type,_id FROM mbr_accounting WHERE memberID=1 " +
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
                    row.put("txvId",cur.getString(6));
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
