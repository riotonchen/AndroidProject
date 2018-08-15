package com.example.admin.project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class UpdateAccountFragment extends Fragment {
    private String ExpenseMoney, ID, DATE, AccountID;
    private EditText txvExpenseMoney;
    private TextView txvRemark, txvDate, txvNumber;
    private Button btnDatePicker, btnTab,btnDelete;
    Calendar calendar = Calendar.getInstance(Locale.TAIWAN);
    private SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
    private SQLiteDatabase db;
    private boolean ifFirst = false;
    private Spinner spnAccount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_account_fragment, container, false);

        txvExpenseMoney = view.findViewById(R.id.txvExpenseMoney);
        txvDate = view.findViewById(R.id.txvDate);
        txvRemark = view.findViewById(R.id.remark);
        txvNumber = view.findViewById(R.id.number);
        btnDatePicker = view.findViewById(R.id.btnDatePicker);
        btnTab = view.findViewById(R.id.btnTab);
        btnDelete=view.findViewById(R.id.btnDelete);
        final Spinner spnSort = view.findViewById(R.id.spnSort);
        final Spinner spnSubSort = view.findViewById(R.id.spnSubSort);
        spnAccount = view.findViewById(R.id.spnAccount);
        final Spinner spnProject = view.findViewById(R.id.spnProject);


        ExpenseMoney = getActivity().getIntent().getExtras().getString("money");
        txvExpenseMoney.setText(ExpenseMoney);
        ID = getActivity().getIntent().getExtras().getString("id");
        //txvRemark.setText(ID);
        DATE = getActivity().getIntent().getExtras().getString("date");
        txvDate.setText(DATE);


        btnDatePicker.setOnClickListener(new View.OnClickListener() {//按下按鈕可調整日期
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                txvDate.setText(yyyyMMdd.format(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        DBHelper DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        String sqlCmd = "Select invoiceNum FROM mbr_accounting WHERE _id=\"" + ID + "\"";
        String incoice = "";
        Cursor cur = db.rawQuery(sqlCmd, null);
        if (cur.getCount() > 0) {

            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                incoice = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        txvNumber.setText(incoice);
        sqlCmd = "Select comment FROM mbr_accounting WHERE _id=\"" + ID + "\"";
        String remark = "";
        cur = db.rawQuery(sqlCmd, null);
        if (cur.getCount() > 0) {

            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                remark = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        txvRemark.setText(remark);


        ArrayList<String> arrayList;
        ArrayAdapter adapter;
        //Sort
        sqlCmd = "SELECT name FROM (SELECT * FROM sys_sort WHERE type=0) AS A " +
                "LEFT OUTER JOIN" +
                "(SELECT sortID FROM mbr_membersort WHERE memberID=1) AS B " +
                "ON A._id=B.sortID";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnSort.setAdapter(adapter);
        //分類選擇事件
        spnSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ifFirst) {
                    DBHelper DH = new DBHelper(getActivity());
                    db = DH.getReadableDatabase();
                    String sqlCmd = "SELECT name FROM sys_subsort AS A INNER JOIN " +
                            "   (SELECT subsortID FROM mbr_membersubsort WHERE memberSortID=" +
                            "       (SELECT _id FROM sys_sort WHERE name=\"" + parent.getSelectedItem().toString() + "\") " +
                            "       AND " +
                            "       memberID=1" +
                            "   ) AS B " +
                            "ON A._id=B.subsortID";
                    ArrayList<String> arrayList = Query(sqlCmd);
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
                    spnSubSort.setAdapter(adapter);
                } else
                    ifFirst = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //找出這筆帳務儲存的分類的ID
        sqlCmd = "SELECT sortID FROM mbr_accounting WHERE _id=\"" + ID + "\"";
        String sortID = "";
        cur = db.rawQuery(sqlCmd, null);
        if (cur.getCount() > 0) {

            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                sortID = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        //帶入這筆帳務儲存的分類
        sqlCmd = "SELECT name FROM sys_sort WHERE _id=\"" + sortID + "\"";
        cur = db.rawQuery(sqlCmd, null);
        String saveSortName = "";
        if (cur.getCount() > 0) {
            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                saveSortName = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        if (saveSortName != null) {
            int spinnerposition = adapter.getPosition(saveSortName);
            spnSort.setSelection(spinnerposition);
        }


        //SubSort
        String sortName = spnSort.getSelectedItem().toString();
        sqlCmd = "SELECT name FROM sys_subsort AS A INNER JOIN " +
                "   (SELECT subsortID FROM mbr_membersubsort WHERE memberSortID=" +
                "       (SELECT _id FROM sys_sort WHERE name=\"" + sortName + "\") " +
                "       AND " +
                "       memberID=1" +
                "   ) AS B " +
                "ON A._id=B.subsortID";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnSubSort.setAdapter(adapter);

        //找出這筆帳務儲存的子分類的ID
        sqlCmd = "SELECT subsortID FROM mbr_accounting WHERE _id=\"" + ID + "\"";
        String subSortID = "";
        cur = db.rawQuery(sqlCmd, null);
        if (cur.getCount() > 0) {

            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                subSortID = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        //帶入這筆帳務儲存的子分類
        sqlCmd = "SELECT name FROM sys_subsort WHERE _id=\"" + subSortID + "\"";
        cur = db.rawQuery(sqlCmd, null);
        String saveSubSortName = "";
        if (cur.getCount() > 0) {
            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                saveSubSortName = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        if (saveSubSortName != null) {
            int spinnerposition = adapter.getPosition(saveSubSortName);
            spnSubSort.setSelection(spinnerposition);
        }


        //spnAccount
        sqlCmd = "SELECT name FROM sys_account WHERE _id IN " +
                "(SELECT accountID FROM mbr_memberaccount " +
                "WHERE memberID=1)";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnAccount.setAdapter(adapter);

        //找出這筆帳務儲存的帳戶的ID
        sqlCmd = "SELECT accountID FROM mbr_accounting WHERE _id=\"" + ID + "\"";
        AccountID = "";
        cur = db.rawQuery(sqlCmd, null);
        if (cur.getCount() > 0) {

            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                AccountID = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        //帶入這筆帳務儲存的帳戶
        sqlCmd = "SELECT name FROM sys_account WHERE _id=\"" + AccountID + "\"";
        cur = db.rawQuery(sqlCmd, null);
        String saveAccountID = "";
        if (cur.getCount() > 0) {
            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                saveAccountID = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        if (saveAccountID != null) {
            int spinnerposition = adapter.getPosition(saveAccountID);
            spnAccount.setSelection(spinnerposition);
        }


        //spnProject
        sqlCmd = "SELECT name FROM sys_project WHERE _id IN " +
                "(SELECT projectID FROM mbr_memberproject " +
                "WHERE memberID=1)";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnProject.setAdapter(adapter);

        sqlCmd = "SELECT projectID FROM mbr_accounting WHERE _id=\"" + ID + "\"";
        String projectID = "";
        cur = db.rawQuery(sqlCmd, null);
        if (cur.getCount() > 0) {

            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                projectID = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        //帶入這筆帳務儲存的帳戶
        sqlCmd = "SELECT name FROM sys_project WHERE _id=\"" + projectID + "\"";
        cur = db.rawQuery(sqlCmd, null);
        String saveProjectID = "";
        if (cur.getCount() > 0) {
            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                saveProjectID = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        if (saveProjectID != null) {
            int spinnerposition = adapter.getPosition(saveProjectID);
            spnProject.setSelection(spinnerposition);
        }



        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DBHelper DH = new DBHelper(getActivity());
                    db = DH.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("memberID", "1");
                    cv.put("time", txvDate.getText().toString().replace('/', '-'));
                    cv.put("type", "0");//0為支出
                    cv.put("sortID", Query("sys_sort", spnSort.getSelectedItem().toString()));//要先找出代號
                    cv.put("subsortID", Query("sys_subsort", spnSubSort.getSelectedItem().toString()));
                    cv.put("amount", txvExpenseMoney.getText().toString());
                    cv.put("accountID", Query("sys_account", spnAccount.getSelectedItem().toString()));
                    cv.put("projectID", Query("sys_project", spnProject.getSelectedItem().toString()));
                    String invoiceNum = txvNumber.getText().toString();
                    if (invoiceNum == "") {
                        cv.putNull("invoiceNum");
                    } else
                        cv.put("invoiceNum", invoiceNum);
                    String value = txvRemark.getText().toString();
                    if (value == "") {
                        cv.putNull("comment");
                    } else
                        cv.put("comment", value);
                    long result = db.update("mbr_accounting", cv, "_id" + "=" + ID, null);
                    if (result == -1) {
                        db.close();
                        Toast.makeText(getActivity(), "修改失敗", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_LONG).show();

                        UpdateAccountBalance();//更新會員帳戶剩餘金額
                        db.close();
                        Intent it = new Intent(getActivity(), NewMainActivity.class);
                        startActivity(it);
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper DH=new DBHelper(getActivity());
                db=DH.getWritableDatabase();

                TextView txv=new TextView(getActivity());
                txv.setText("確定要刪除帳戶?");txv.setTextSize(24);
                new AlertDialog.Builder(getActivity())
                        .setView(txv)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.delete("mbr_accounting","_id="+ID,null);
                                Toast.makeText(getActivity().getApplicationContext(), "刪除成功", Toast.LENGTH_SHORT).show();
                                db.close();
                                Intent it = new Intent(getActivity(), NewMainActivity.class);
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
        });

        return view;
    }

    private ArrayList<String> Query(String sqlCmd) {
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

    private String Query(String tableName, String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        String sqlCmd = "SELECT _id FROM " + tableName + " WHERE name=\"" + str + "\"";
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
        return arrayList.get(0);

    }

    private void UpdateAccountBalance(){
        //比對帳戶是否有更動
        String updateExpenseMoney=txvExpenseMoney.getText().toString();
        int IupdateExpenseMoney=Integer.parseInt(updateExpenseMoney);//要更新的金額
        int IExpenseMoney=Integer.parseInt(ExpenseMoney);//原本的金額
        DBHelper DH = new DBHelper(getActivity());
        db = DH.getWritableDatabase();


        //原儲存帳戶的Balance
        String sqlCmd = "SELECT balance FROM mbr_memberaccount WHERE accountID=\"" + AccountID + "\"";
        String balance="";
        Cursor cur = db.rawQuery(sqlCmd, null);
        if (cur.getCount() > 0) {

            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                balance = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        int iBalance=Integer.parseInt(balance);
        int iCalculatedExpenseMoney=IupdateExpenseMoney-IExpenseMoney;
        String calculatedExpenseMoney=Integer.toString(iBalance-iCalculatedExpenseMoney);

        String updateAccountID= Query("sys_account", spnAccount.getSelectedItem().toString());
        //新選擇的帳戶的Balance
        sqlCmd = "SELECT balance FROM mbr_memberaccount WHERE accountID=\"" + updateAccountID + "\"";
        String balance2="";
        cur = db.rawQuery(sqlCmd, null);
        if (cur.getCount() > 0) {

            cur.moveToFirst();    // 移到第 1 筆資料
            do {        // 逐筆讀出資料(只會有一筆)
                balance2 = cur.getString(0);
            } while (cur.moveToNext());    // 有一下筆就繼續迴圈
        }
        int iBalance2=Integer.parseInt(balance2);//新帳戶balance
        String NewBalance=Integer.toString(iBalance2-IupdateExpenseMoney);
        String OldBalance=Integer.toString(iBalance+IExpenseMoney);

        //如果'帳戶沒有更動
        if(AccountID.equals(updateAccountID)){
            Log.i("Change1","No");
            sqlCmd="UPDATE mbr_memberaccount SET balance =\"" + calculatedExpenseMoney + "\""+
                    "  WHERE mbr_memberaccount.accountID=(SELECT accountID FROM mbr_accounting WHERE memberID=1  " +
                    "        AND type=0 AND _id=\"" + ID + "\")";
            db.execSQL(sqlCmd);
        }
        else{//如果帳戶有更動
            //更動新帳戶
            Log.i("Change1","Yes");
            sqlCmd="UPDATE mbr_memberaccount SET balance =\"" + NewBalance + "\""+
                    "  WHERE mbr_memberaccount.accountID=(SELECT accountID FROM mbr_accounting WHERE memberID=1  " +
                    "        AND type=0 AND _id=\"" + ID + "\")";
            db.execSQL(sqlCmd);
            //更動舊帳戶
            sqlCmd="UPDATE mbr_memberaccount SET balance =\"" + OldBalance + "\""+
                    "  WHERE mbr_memberaccount.accountID =\"" + AccountID + "\"";
            db.execSQL(sqlCmd);
        }





    }

}
