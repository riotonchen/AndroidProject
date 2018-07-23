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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountFragment extends Fragment {
    private static final String TAG = "AccountFragment";
    private Button btnTab,btnTransfer;
    static final String DB_NAME = "MYLOCALDB";
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    SimpleAdapter adapter;
    ListView lv;
    List<Map<String, String>> accountList = new ArrayList<Map<String, String>>();
    TextView txvTotalAsset, txvTotalLiability, txvNetAsset;
    Intent it;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);
        //讀取帳戶資料
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        adapter = new SimpleAdapter(getActivity(), accountList, R.layout.account_item, new String[]{"account", "initialAmount", "FX", "balance"},
                new int[]{R.id.account, R.id.initialAmount, R.id.FX, R.id.balance});
        Requery();
        lv = view.findViewById(R.id.account_lv);
        lv.setAdapter(adapter);
        //設定總資產等等
        txvTotalAsset = view.findViewById(R.id.txvTotalAsset);
        txvTotalLiability = view.findViewById(R.id.txvTotalLiability);
        txvNetAsset = view.findViewById(R.id.txvNetAsset);
        SetTextView();

        DH.close();
        btnTransfer=view.findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//跳轉至轉帳畫面
                it =new Intent(getActivity(), AccountingActivity.class);
                it.putExtra("transferId",1);
                startActivity(it);
            }
        });

        return view;
    }

    private void Requery() {
        String sqlCmd = "SELECT name,initialAmount,FX,balance FROM sys_account,mbr_memberaccount " +
                " WHERE sys_account._id=mbr_memberaccount.accountID";
        cur = db.rawQuery(sqlCmd, null);
        int rowsCount = cur.getCount();
        if (rowsCount != 0) {
            cur.moveToFirst();
            for (int i = 0; i < rowsCount; i++) {
                Map<String, String> row = new HashMap<String, String>();
                row.put("account", cur.getString(0));
                row.put("initialAmount", "初始金額:" + Integer.toString(cur.getInt(1)));
                row.put("FX", "匯率:" + cur.getString(2));
                row.put("balance", "$" + Integer.toString(cur.getInt(3)));
                accountList.add(row);
                cur.moveToNext();
            }
        }
    }

    private void SetTextView() {
        int totalAsset = Query("SELECT IFNUll(SUM(balance),0) AS col FROM mbr_memberaccount WHERE accountTypeID <> 4");
        int totalLiability = Query("SELECT IFNUll(SUM(balance),0) AS col FROM mbr_memberaccount WHERE accountTypeID = 4");
        txvTotalAsset.setText(String.valueOf(totalAsset));
        txvTotalLiability.setText(String.valueOf(totalLiability));
        txvNetAsset.setText(String.valueOf(totalAsset - totalLiability));
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

