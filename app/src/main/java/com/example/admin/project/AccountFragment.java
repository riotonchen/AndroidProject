package com.example.admin.project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountFragment extends Fragment {
    private static final String TAG = "AccountFragment";
    private Button btnTab, btnTransfer, btnAdd;
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
        adapter = new SimpleAdapter(getActivity(), accountList, R.layout.account_item, new String[]{"accountID","account", "initialAmount", "FX", "balance"},
                new int[]{R.id.accountID,R.id.account, R.id.initialAmount, R.id.FX, R.id.balance});
        Requery();
        lv = view.findViewById(R.id.account_lv);
        lv.setAdapter(adapter);
        //設定總資產等等
        txvTotalAsset = view.findViewById(R.id.txvTotalAsset);
        txvTotalLiability = view.findViewById(R.id.txvTotalLiability);
        txvNetAsset = view.findViewById(R.id.txvNetAsset);
        SetTextView();

        DH.close();
        btnTransfer = view.findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//跳轉至轉帳畫面
                it = new Intent(getActivity(), AccountingActivity.class);
                it.putExtra("transferId", 1);
                startActivity(it);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DH=new DBHelper(getActivity());
                db=DH.getWritableDatabase();
                final String accountID=((TextView)view.findViewById(R.id.accountID)).getText().toString();

                TextView txv=new TextView(getActivity());
                txv.setText("確定要刪除帳戶?");txv.setTextSize(24);
                new AlertDialog.Builder(getActivity())
                        .setView(txv)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.delete("mbr_accounting","accountID="+accountID,null);
                                db.delete("mbr_memberaccount", "accountID="+accountID , null);
                                db.delete("sys_account","_id="+accountID,null);
                                Toast.makeText(getActivity().getApplicationContext(), "刪除成功", Toast.LENGTH_SHORT).show();
                                db.close();
                                it = new Intent(getActivity(), MainActivity.class);
                                startActivity(it);
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


                return false;
            }
        });

        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DH = new DBHelper(getActivity());
                db = DH.getReadableDatabase();
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View AccountView = inflater.inflate(R.layout.createaccount, null);

                final EditText edit_name = (EditText) AccountView.findViewById(R.id.edit_name);

                final Spinner spnAccountType = (Spinner) AccountView.findViewById(R.id.spnAccountType);
                String sqlCmd = "SELECT name FROM sys_accounttype";
                ArrayList<String> arrayList = QueryA(sqlCmd);
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
                spnAccountType.setAdapter(adapter);

                final EditText edit_initialAmount=(EditText)AccountView.findViewById(R.id.edit_initialAmount);
                //設定彈出視窗裡的選項

                new AlertDialog.Builder(getActivity())
                        .setView(AccountView)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = edit_name.getText().toString();

                                String sqlCmd = "SELECT _id FROM sys_accounttype WHERE name=\"" + spnAccountType.getSelectedItem().toString() + "\"";
                                cur = db.rawQuery(sqlCmd, null);
                                int rowsCount = cur.getCount();
                                int iaccountTypeID = 0;
                                if (rowsCount != 0) {
                                    cur.moveToFirst();
                                    for (int i = 0; i < rowsCount; i++) {
                                        iaccountTypeID = (cur.getInt(0));
                                        cur.moveToNext();
                                    }
                                    cur.close();
                                }
                                String accountTypeID=String.valueOf(iaccountTypeID);

                                String initialAmount=edit_initialAmount.getText().toString();
                                if(initialAmount==""){
                                    initialAmount="0";
                                }

                                if (TextUtils.isEmpty(name)) {
                                    Toast.makeText(getActivity().getApplicationContext(), "請輸入帳戶名稱", Toast.LENGTH_SHORT).show();
                                    db.close();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "新增成功", Toast.LENGTH_SHORT).show();
                                    //DBHelper DH = new DBHelper(getActivity());
                                    //db = DH.getReadableDatabase();

                                    String TB_NAME = "sys_account";
                                    String[] col = new String[]{"name"};
                                    String[] data = new String[]{name};
                                    AddData(TB_NAME, col, data);

                                    sqlCmd = "SELECT _id FROM sys_account ORDER BY _id DESC LIMIT 1";//找出最新一筆新增的帳戶的ID
                                    cur = db.rawQuery(sqlCmd, null);
                                    rowsCount = cur.getCount();
                                    int latestAccountID = 0;
                                    if (rowsCount != 0) {
                                        cur.moveToFirst();
                                        for (int i = 0; i < rowsCount; i++) {
                                            latestAccountID = (cur.getInt(0));
                                            cur.moveToNext();
                                        }
                                        cur.close();
                                    }
                                    final String newAccountID = String.valueOf(latestAccountID);//目前要新增的帳戶的ID


                                    TB_NAME = "mbr_memberaccount";
                                    col = new String[]{"memberID", "accountID", "accountTypeID", "initialAmount", "balance", "FX", "comment"};
                                    data = new String[]{"1", newAccountID, accountTypeID, initialAmount, initialAmount, "1:1", null,};//之前做到的地方
                                    AddData(TB_NAME, col, data);

                                    db.close();
                                    it = new Intent(getActivity(), MainActivity.class);
                                    startActivity(it);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getActivity().getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                                db.close();
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    private void Requery() {
        String sqlCmd = "SELECT accountID,name,initialAmount,FX,balance FROM sys_account,mbr_memberaccount " +
                " WHERE sys_account._id=mbr_memberaccount.accountID";
        cur = db.rawQuery(sqlCmd, null);
        int rowsCount = cur.getCount();
        if (rowsCount != 0) {
            cur.moveToFirst();
            for (int i = 0; i < rowsCount; i++) {
                Map<String, String> row = new HashMap<String, String>();
                row.put("accountID",Integer.toString(cur.getInt(0)));
                row.put("account", cur.getString(1));
                row.put("initialAmount", "初始金額:" + Integer.toString(cur.getInt(2)));
                row.put("FX", "匯率:" + cur.getString(3));
                row.put("balance", "$" + Integer.toString(cur.getInt(4)));
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

    public void AddData(String tableName, String[] columnName, String[] data) {
        //db = openOrCreateDatabase(DB_NAME, android.content.Context.MODE_PRIVATE, null);
        ContentValues cv;
        for (int i = 0; i < data.length; ) {
            cv = new ContentValues(columnName.length);
            for (int j = 0; j < columnName.length; j++) {
                cv.put(columnName[j], data[i++]);
            }
            db.insert(tableName, null, cv);
        }
    }

    private ArrayList<String> QueryA(String sqlCmd) {
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
        return arrayList;
    }
}

