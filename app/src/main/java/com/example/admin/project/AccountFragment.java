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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DH=new DBHelper(getActivity());
                db=DH.getWritableDatabase();
                final String accountID=((TextView)view.findViewById(R.id.accountID)).getText().toString();
                PopupMenu popup=new PopupMenu(getActivity(),view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.Search:
                                it = new Intent(getActivity(), ViewDetails.class);
                                it.putExtra("date_start", "過去");
                                it.putExtra("date_end", "現在");
                                it.putExtra("Tag","0");
                                it.putExtra("accountID",accountID);
                                startActivity(it);
                                return true;
                            case R.id.Edit:
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
                                final EditText edit_FX=(EditText)AccountView.findViewById(R.id.edit_FX);

                                int accountTypeId=0;
                                String SaccountID="";String accountName="";
                                String initialAmount="";
                                String FX="";
                                sqlCmd="SELECT * FROM mbr_memberaccount WHERE _id=\"" + accountID + "\"";
                                cur=db.rawQuery(sqlCmd,null);
                                if (cur.getCount() != 0) {
                                    cur.moveToFirst();
                                    do {        // 逐筆讀出資料(只會有一筆)
                                        accountTypeId = cur.getInt(3);
                                        SaccountID=Integer.toString(cur.getInt(2));
                                        initialAmount=Integer.toString(cur.getInt(4));
                                        FX=cur.getString(6);
                                    } while (cur.moveToNext());
                                }
                                spnAccountType.setSelection(accountTypeId-1);
                                sqlCmd="SELECT name FROM sys_account WHERE _id=\"" + SaccountID + "\"";
                                cur=db.rawQuery(sqlCmd,null);
                                if (cur.getCount() != 0) {
                                    cur.moveToFirst();
                                    do {        // 逐筆讀出資料(只會有一筆)
                                        accountName=cur.getString(0);
                                    } while (cur.moveToNext());
                                }
                                edit_initialAmount.setText(initialAmount);
                                edit_FX.setText(FX);
                                edit_name.setText(accountName);
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
                                                String FX=edit_FX.getText().toString();
                                                if(TextUtils.isEmpty(FX)){
                                                    FX="1:1";
                                                }

                                                if (TextUtils.isEmpty(name)) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "請輸入帳戶名稱", Toast.LENGTH_SHORT).show();
                                                    db.close();
                                                } else {
                                                    Toast.makeText(getActivity().getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();

                                                    sqlCmd="UPDATE sys_account SET name=\""+ name + "\""+
                                                            "WHERE _id=\""+ accountID + "\"";
                                                    db.execSQL(sqlCmd);
                                                    sqlCmd="UPDATE mbr_memberaccount SET accountTypeID=\""+ accountTypeID +"\""+
                                                            ", initialAmount=\""+ initialAmount + "\""+
                                                            ", FX=\""+ FX +"\""+
                                                            "WHERE accountID=\""+ accountID +"\"";
                                                    db.execSQL(sqlCmd);

                                                    db.close();
                                                    it = new Intent(getActivity(), NewMainActivity.class);
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
                                return true;
                            case R.id.Delete:
                                TextView txv=new TextView(getActivity());
                                txv.setText("刪除此帳戶，也會一併刪除所有此帳戶底下所有的收支紀錄");txv.setTextSize(15);
                                TextView txvTitle=new TextView(getActivity());
                                txvTitle.setText("刪除提示");txvTitle.setTextSize(20);

                                new AlertDialog.Builder(getActivity())//雙重確認
                                        .setView(txv)
                                        .setCustomTitle(txvTitle)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                TextView txv=new TextView(getActivity());
                                                txv.setText("最終確認，確定要刪除帳戶?");txv.setTextSize(15);
                                                TextView txvTitle=new TextView(getActivity());
                                                txvTitle.setText("刪除提示");txvTitle.setTextSize(20);

                                                new AlertDialog.Builder(getActivity())
                                                        .setView(txv)
                                                        .setCustomTitle(txvTitle)
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                db.delete("mbr_accounting","accountID="+accountID,null);
                                                                db.delete("mbr_memberaccount", "accountID="+accountID , null);
                                                                db.delete("sys_account","_id="+accountID,null);
                                                                Toast.makeText(getActivity().getApplicationContext(), "刪除成功", Toast.LENGTH_SHORT).show();
                                                                db.close();
                                                                it = new Intent(getActivity(), NewMainActivity.class);
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

                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.menu_account);
                popup.show();



            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DH=new DBHelper(getActivity());
                db=DH.getWritableDatabase();
                final String accountID=((TextView)view.findViewById(R.id.accountID)).getText().toString();

                TextView txv=new TextView(getActivity());
                txv.setText("刪除此帳戶，也會一併刪除所有此帳戶底下所有的收支紀錄");txv.setTextSize(15);
                TextView txvTitle=new TextView(getActivity());
                txvTitle.setText("刪除提示");txvTitle.setTextSize(20);

                new AlertDialog.Builder(getActivity())//雙重確認
                        .setView(txv)
                        .setCustomTitle(txvTitle)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextView txv=new TextView(getActivity());
                                txv.setText("最終確認，確定要刪除帳戶?");txv.setTextSize(15);
                                TextView txvTitle=new TextView(getActivity());
                                txvTitle.setText("刪除提示");txvTitle.setTextSize(20);

                                new AlertDialog.Builder(getActivity())
                                        .setView(txv)
                                        .setCustomTitle(txvTitle)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                db.delete("mbr_accounting","accountID="+accountID,null);
                                                db.delete("mbr_memberaccount", "accountID="+accountID , null);
                                                db.delete("sys_account","_id="+accountID,null);
                                                Toast.makeText(getActivity().getApplicationContext(), "刪除成功", Toast.LENGTH_SHORT).show();
                                                db.close();
                                                it = new Intent(getActivity(), NewMainActivity.class);
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
                final EditText edit_FX=(EditText)AccountView.findViewById(R.id.edit_FX);
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

                                String FX=edit_FX.getText().toString();
                                if(TextUtils.isEmpty(FX)){
                                    FX="1:1";
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
                                    data = new String[]{"1", newAccountID, accountTypeID, initialAmount, initialAmount, FX, null,};//之前做到的地方
                                    AddData(TB_NAME, col, data);

                                    db.close();
                                    it = new Intent(getActivity(), NewMainActivity.class);
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

