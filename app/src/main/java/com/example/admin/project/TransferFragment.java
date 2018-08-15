package com.example.admin.project;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TransferFragment extends Fragment {
    private static final String TAG="TransferFragment";
    private final String TABLE_NAME_TRANSFER = "mbr_transfer";
    private final String TABLE_NAME_MEMBERACCOUNT = "mbr_memberaccount";
    private Button btnTab,btnDatePicker;
    private TextView txvDate,txvRemark,txvExpenseMoney,txvFee;
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    private Spinner spnSourceAccount,spnTergetAccount,spnProject;
    private String SourceAccountName;
    private SQLiteDatabase db;
    Calendar calendar=Calendar.getInstance(Locale.TAIWAN);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_fragment,container,false);
        btnTab = (Button) view.findViewById(R.id.btnTab);
        btnDatePicker=view.findViewById(R.id.btnDatePicker);
        txvDate = (TextView)view.findViewById(R.id.txvDate);
        txvRemark=(TextView)view.findViewById(R.id.remark);
        txvExpenseMoney=(TextView)view.findViewById(R.id.txvExpenseMoney);
        txvFee=(TextView)view.findViewById(R.id.txvFee);
        spnSourceAccount=(Spinner)view.findViewById(R.id.spnSourceAccount);
        spnTergetAccount=(Spinner)view.findViewById(R.id.spnTergetAccount);
        spnProject=(Spinner)view.findViewById(R.id.spnProject);
        //設定txvDate預設日期
        txvDate.setText(yyyyMMdd.format(calendar.getTime()));
        //btnDatePicker
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year,month,day);
                                txvDate.setText(yyyyMMdd.format(calendar.getTime()));
                            }
                        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        //開啟資料庫帶入spinner
        DBHelper DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        String sqlCmd;
        ArrayList<String> arrayList;
        ArrayAdapter adapter;
        //帶入來源帳務資料
        sqlCmd = "SELECT name FROM sys_account AS A " +
                "LEFT OUTER JOIN" +
                "(SELECT _id FROM mbr_memberaccount WHERE memberID=1) AS B " +
                "ON A._id=B._id";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnSourceAccount.setAdapter(adapter);

        spnSourceAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SourceAccountName=spnSourceAccount.getSelectedItem().toString();
                DBHelper DH = new DBHelper(getActivity());
                db = DH.getReadableDatabase();
                String sqlCmd = "SELECT name FROM sys_account WHERE name<>\"" + SourceAccountName + "\"";
                ArrayList<String> arrayList = Query(sqlCmd);
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
                spnTergetAccount.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //帶入目的帳務資料(預設為除了現金(新台幣)以外的項目)
        sqlCmd = "SELECT name FROM sys_account WHERE _id<>1" ;
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnTergetAccount.setAdapter(adapter);

        //帶入專案
        sqlCmd="SELECT name FROM sys_project WHERE _id IN " +
                "(SELECT projectID FROM mbr_memberproject " +
                "WHERE memberID=1)";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnProject.setAdapter(adapter);

        db.close();

        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DBHelper DH = new DBHelper(getActivity());
                    db = DH.getReadableDatabase();
                    ContentValues cv=new ContentValues();
                    cv.put("memberID","1");
                    cv.put("time",txvDate.getText().toString().replace('/','-'));
                    cv.put("sourceAccountID",Query("sys_account",spnSourceAccount.getSelectedItem().toString()));
                    cv.put("targetAccountID",Query("sys_account",spnTergetAccount.getSelectedItem().toString()));
                    cv.put("amount",txvExpenseMoney.getText().toString());
                    cv.put("fee",txvFee.getText().toString());
                    cv.put("projectID",Query("sys_project",spnProject.getSelectedItem().toString()));
                    String value=txvRemark.getText().toString();
                    if(value==""){
                        cv.putNull("comment");
                    }else
                        cv.put("comment",value);

                    long result=db.insert(TABLE_NAME_TRANSFER,null,cv);
                    if(result==-1){
                        db.close();
                        Toast.makeText(getActivity(), "新增失敗", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "新增成功", Toast.LENGTH_LONG).show();
                        //更新帳戶balance
                        String ExpenseMoney=txvExpenseMoney.getText().toString();
                        int expense=Integer.valueOf(ExpenseMoney);
                        String Fee=txvFee.getText().toString();
                        int fee;
                        if(TextUtils.isEmpty(txvFee.getText())){
                            fee=0;
                        }
                        else {
                            fee=Integer.valueOf(Fee);
                        }
                        //更新來源帳戶balance
                        int sourceExpense=expense+fee;
                        int sourceBalance=GetBalance(Query("sys_account",spnSourceAccount.getSelectedItem().toString()));
                        sourceBalance=sourceBalance-sourceExpense;
                        String SourceBalance=String.valueOf(sourceBalance);
                        ContentValues sourceValues =new ContentValues();
                        sourceValues.put("balance",SourceBalance);

                        //更新目的帳戶balance
                        int tergetExpenese=expense;
                        int tergetBalance=GetBalance(Query("sys_account",spnTergetAccount.getSelectedItem().toString()));
                        tergetExpenese=tergetBalance+tergetExpenese;
                        String TergetExpense=String.valueOf(tergetExpenese);
                        ContentValues tergetValues =new ContentValues();
                        tergetValues.put("balance",TergetExpense);

                        db.update(TABLE_NAME_MEMBERACCOUNT,sourceValues,"accountID="+Query("sys_account",spnSourceAccount.getSelectedItem().toString()),null);
                        db.update(TABLE_NAME_MEMBERACCOUNT,tergetValues,"accountID="+Query("sys_account",spnTergetAccount.getSelectedItem().toString()),null);
                        db.close();
                        Intent it=new Intent(getActivity(),NewMainActivity.class);
                        startActivity(it);
                    }


                }catch (Exception ex){
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
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
    private String Query(String tableName,String str){//輸入Table名稱與帳本名稱，得到帳本ID
        ArrayList<String> arrayList = new ArrayList<>();
        String sqlCmd="SELECT _id FROM "+tableName+" WHERE name=\""+str+"\"";
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
    //取得帳戶的balance
    private int GetBalance(String accountID){
        ArrayList<Integer> arrayList=new ArrayList<>();
        String sqlCmd="SELECT balance FROM mbr_memberaccount WHERE accountID=\""+accountID+"\"";
        try {
            Cursor c=db.rawQuery(sqlCmd,null);
            int rowsCount = c.getCount();
            if (rowsCount != 0) {
                c.moveToFirst();
                for(int i=0;i<rowsCount;i++){
                    arrayList.add(c.getInt(0));
                    c.moveToNext();
                }
            }
            c.close();
        }catch (Exception ex){
            Toast.makeText(getActivity(), "GetBalanceError", Toast.LENGTH_SHORT).show();
        }
        return Integer.valueOf(arrayList.get(0));
    }
}
