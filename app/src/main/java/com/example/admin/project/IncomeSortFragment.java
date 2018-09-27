package com.example.admin.project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class IncomeSortFragment extends Fragment implements OnItemClickListener {

    private static final String TAG="IncomeSortFragment";
    private Button btnTab,create;
    private String monthstart, monthend,sortID,sortName;
    static final String DB_NAME = "MYLOCALDB";
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    SimpleAdapter adapter;
    ListView lv;
    List<Map<String,String>> sortValue=new ArrayList<Map<String,String>>();
    private TextView txtIncomeSortDate;
    private ImageView imvIncomeSortArrowLeft,imvIncomeSortArrowRight;
    Calendar calendar;
    private SimpleDateFormat yyyymmdd  =  new SimpleDateFormat ("yyyy-MM-dd", Locale.TAIWAN);
    Toast tos;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.incomesort_fragment,container,false);
        btnTab = view.findViewById(R.id.btnTab);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
            }
        });

        monthstart = getActivity().getIntent().getExtras().getString("monthstart");
        monthend = getActivity().getIntent().getExtras().getString("monthend");
        txtIncomeSortDate=view.findViewById(R.id.txtIncomeSortDate);
        imvIncomeSortArrowLeft=view.findViewById(R.id.imvIncomeSortArrowLeft);
        imvIncomeSortArrowRight=view.findViewById(R.id.imvIncomeSortArrowRight);
        txtIncomeSortDate.setText(monthstart+"~"+monthend);
        calendar= Calendar.getInstance(Locale.TAIWAN);
        create=view.findViewById(R.id.create);
        lv = view.findViewById(R.id.revenue_lv);

        imvIncomeSortArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    DH = new DBHelper(getActivity());
                    db = DH.getReadableDatabase();
                    sortValue.clear();//先清除
                    Date date=yyyymmdd.parse(monthstart.replace('/','-'));
                    calendar.setTime(date);
                    calendar.add(calendar.MONTH,-1);
                    monthstart=yyyymmdd.format(calendar.getTime());
                    calendar.roll(Calendar.DAY_OF_MONTH,-1);
                    monthend = yyyymmdd.format(calendar.getTime());
                    txtIncomeSortDate.setText(monthstart.replace('-','/')+"~"+monthend.replace('-','/'));
                    UpdateSortAmount(monthstart,monthend);
                    adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost","MsortID"},
                            new int[]{R.id.name, R.id.budget, R.id.cost, R.id.MsortID});
                    Requery();
                    lv.setAdapter(adapter);
                    DH.close();
                }catch (Exception ex){
                    tos.setText("Error:"+ex.toString());
                }
            }
        });

        imvIncomeSortArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    DH = new DBHelper(getActivity());
                    db = DH.getReadableDatabase();
                    sortValue.clear();
                    Date date=yyyymmdd.parse(monthstart.replace('/','-'));
                    calendar.setTime(date);
                    calendar.add(calendar.MONTH,+1);
                    monthstart=yyyymmdd.format(calendar.getTime());
                    calendar.roll(Calendar.DAY_OF_MONTH,-1);
                    monthend = yyyymmdd.format(calendar.getTime());
                    txtIncomeSortDate .setText(monthstart.replace('-','/')+"~"+monthend.replace('-','/'));
                    UpdateSortAmount(monthstart,monthend);
                    adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost","MsortID"},
                            new int[]{R.id.name, R.id.budget, R.id.cost, R.id.MsortID});
                    Requery();
                    lv.setAdapter(adapter);
                    DH.close();
                }catch (Exception ex){
                    tos.setText("Error:"+ex.toString());
                }
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DH = new DBHelper(getActivity());
                db = DH.getWritableDatabase();
                LayoutInflater inflater=LayoutInflater.from(getActivity());
                View ExpenseSortView=inflater.inflate(R.layout.createsort,null);
                final EditText edit_name=(EditText)ExpenseSortView.findViewById(R.id.edit_name);
                final EditText edit_budget=(EditText)ExpenseSortView.findViewById(R.id.edit_budget);
                new AlertDialog.Builder(getActivity())
                        .setView(ExpenseSortView)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = edit_name.getText().toString();
                                String budget=edit_budget.getText().toString();

                                Pattern pattern = Pattern.compile("[0-9]*");

                                if (TextUtils.isEmpty(name)) {
                                    Toast.makeText(getActivity().getApplicationContext(), "請輸入分類名稱", Toast.LENGTH_SHORT).show();
                                } else {
                                    if(TextUtils.isEmpty(budget)){
                                        budget="0";
                                    }

                                    if(pattern.matcher(budget).matches()==true)
                                    {
                                        String TB_NAME = "sys_sort";
                                        String[] col = new String[]{"name","type","icon"};
                                        String[] data = new String[]{name,"1",null};
                                        AddData(TB_NAME, col, data);
                                        String sqlCmd = "SELECT _id FROM sys_sort ORDER BY _id DESC LIMIT 1";//找出最新一筆新增的分類的ID
                                        cur = db.rawQuery(sqlCmd, null);
                                        int rowsCount = cur.getCount();
                                        int latestSortID = 0;
                                        if (rowsCount != 0) {
                                            cur.moveToFirst();
                                            for (int i = 0; i < rowsCount; i++) {
                                                latestSortID = (cur.getInt(0));
                                                cur.moveToNext();
                                            }
                                            cur.close();
                                        }
                                        final String newSortID = String.valueOf(latestSortID);//目前要新增的分類的ID

                                        TB_NAME = "mbr_membersort";
                                        col = new String[]{"memberID","sortID","budget","amount","icon"};
                                        data = new String[]{"1",newSortID,budget,"0",null};
                                        AddData(TB_NAME, col, data);
                                        sqlCmd = "SELECT _id FROM mbr_membersort ORDER BY _id DESC LIMIT 1";//找出最新一筆新增的會員分類的ID
                                        cur = db.rawQuery(sqlCmd, null);
                                        rowsCount = cur.getCount();
                                        int latestMemberSortID = 0;
                                        if (rowsCount != 0) {
                                            cur.moveToFirst();
                                            for (int i = 0; i < rowsCount; i++) {
                                                latestMemberSortID = (cur.getInt(0));
                                                cur.moveToNext();
                                            }
                                            cur.close();
                                        }
                                        final String newMemberSortID = String.valueOf(latestMemberSortID);//目前要新增的會員分類的ID

                                        TB_NAME = "sys_subsort";
                                        col = new String[]{"name","type","icon"};
                                        data = new String[]{name,"1",null};
                                        AddData(TB_NAME, col, data);
                                        sqlCmd = "SELECT _id FROM sys_subsort ORDER BY _id DESC LIMIT 1";//找出最新一筆新增的子分類的ID
                                        cur = db.rawQuery(sqlCmd, null);
                                        rowsCount = cur.getCount();
                                        int latestSubSortID = 0;
                                        if (rowsCount != 0) {
                                            cur.moveToFirst();
                                            for (int i = 0; i < rowsCount; i++) {
                                                latestSubSortID = (cur.getInt(0));
                                                cur.moveToNext();
                                            }
                                            cur.close();
                                        }
                                        final String newSubSortID = String.valueOf(latestSubSortID);//目前要新增的會員子分類的ID

                                        TB_NAME = "mbr_membersubsort";
                                        col = new String[]{"memberID","memberSortID","subsortID","budget","amount","icon"};
                                        data = new String[]{"1",newMemberSortID,newSubSortID,budget,"0",null};
                                        AddData(TB_NAME, col, data);

                                        Toast.makeText(getActivity().getApplicationContext(), "新增成功", Toast.LENGTH_SHORT).show();


                                        sortValue.clear();

                                        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost", "MsortID"},
                                                new int[]{R.id.name, R.id.budget, R.id.cost,R.id.MsortID});
                                        Requery();
                                        lv.setAdapter(adapter);
                                        db.close();
                                        //it = new Intent(getActivity(), NewMainActivity.class);
                                        //startActivity(it);
                                    }
                                    else {
                                        Toast.makeText(getActivity().getApplicationContext(), "請勿在預算欄位輸入數字以外的文字", Toast.LENGTH_SHORT).show();
                                    }
                                    //Toast.makeText(getActivity().getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
            }
        });


        //讀取分類資料
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        UpdateSortAmount(monthstart,monthend);
        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost","MsortID"},
                new int[]{R.id.name, R.id.budget, R.id.cost, R.id.MsortID});
        Requery();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DH=new DBHelper(getActivity());
                db=DH.getWritableDatabase();
                final String MsortID=((TextView)view.findViewById(R.id.MsortID)).getText().toString();
                final String sort=((TextView)view.findViewById(R.id.name)).getText().toString();
                //initPopWindow(view);
                PopupMenu popup=new PopupMenu(getActivity(),view, Gravity.RIGHT);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.Search:
                                Intent it = new Intent(getActivity(), ViewDetails.class);
                                it.putExtra("date_start", monthstart);
                                it.putExtra("date_end", monthend);
                                it.putExtra("condition",sort);
                                it.putExtra("Tag",1);
                                it.putExtra("MsortID",MsortID);
                                startActivity(it);
                                return true;
                            case R.id.Edit:
                                LayoutInflater inflater = LayoutInflater.from(getActivity());
                                View ExpenseSortView = inflater.inflate(R.layout.createsort, null);
                                final EditText edit_name=(EditText)ExpenseSortView.findViewById(R.id.edit_name);
                                final EditText edit_budget=(EditText)ExpenseSortView.findViewById(R.id.edit_budget);

                                sortID="";
                                String budget="";
                                String sqlCmd="SELECT sortID,budget FROM mbr_membersort WHERE _id=\"" + MsortID + "\"";
                                cur=db.rawQuery(sqlCmd,null);
                                if (cur.getCount() != 0) {
                                    cur.moveToFirst();
                                    do {        // 逐筆讀出資料(只會有一筆)
                                        sortID = Integer.toString(cur.getInt(0));
                                        budget=Integer.toString(cur.getInt(1));
                                    } while (cur.moveToNext());
                                }
                                edit_budget.setText(budget);

                                sortName="";
                                sqlCmd="SELECT name FROM sys_sort WHERE _id=\""+ sortID + "\"";
                                cur=db.rawQuery(sqlCmd,null);
                                if (cur.getCount() != 0) {
                                    cur.moveToFirst();
                                    do {        // 逐筆讀出資料(只會有一筆)
                                        sortName = cur.getString(0);
                                    } while (cur.moveToNext());
                                }
                                edit_name.setText(sortName);

                                new AlertDialog.Builder(getActivity())
                                        .setView(ExpenseSortView)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String newSortName=edit_name.getText().toString();
                                                String newBudegt=edit_budget.getText().toString();
                                                if (TextUtils.isEmpty(newSortName)){
                                                    Toast.makeText(getActivity().getApplicationContext(), "請輸入分類名稱", Toast.LENGTH_SHORT).show();
                                                    db.close();
                                                }
                                                else {
                                                    if(TextUtils.isEmpty(newBudegt)){
                                                        Toast.makeText(getActivity().getApplicationContext(), "請勿在預算欄位輸入數字以外的文字", Toast.LENGTH_SHORT).show();
                                                        db.close();
                                                    }
                                                    else {
                                                        String sqlCmd = "UPDATE mbr_membersort SET budget=\"" + newBudegt + "\""+
                                                                "WHERE _id=\"" + MsortID + "\"";
                                                        db.execSQL(sqlCmd);
                                                        sqlCmd = "UPDATE sys_sort SET name=\"" + newSortName + "\""+
                                                                "WHERE _id=\"" + sortID + "\"";
                                                        db.execSQL(sqlCmd);


                                                        Toast.makeText(getActivity().getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();

                                                        sortValue.clear();

                                                        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost", "MsortID"},
                                                                new int[]{R.id.name, R.id.budget, R.id.cost,R.id.MsortID});
                                                        Requery();
                                                        lv.setAdapter(adapter);

                                                        db.close();
                                                        //it = new Intent(getActivity(), NewMainActivity.class);
                                                        //startActivity(it);
                                                    }
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
                                sortID="";
                                sqlCmd="SELECT sortID FROM mbr_membersort WHERE _id=\"" + MsortID + "\"";
                                cur=db.rawQuery(sqlCmd,null);
                                if (cur.getCount() != 0) {
                                    cur.moveToFirst();
                                    do {        // 逐筆讀出資料(只會有一筆)
                                        sortID = Integer.toString(cur.getInt(0));
                                    } while (cur.moveToNext());
                                }
                                sortName="";
                                sqlCmd="SELECT name FROM sys_sort WHERE _id=\""+ sortID + "\"";
                                cur=db.rawQuery(sqlCmd,null);
                                if (cur.getCount() != 0) {
                                    cur.moveToFirst();
                                    do {        // 逐筆讀出資料(只會有一筆)
                                        sortName = cur.getString(0);
                                    } while (cur.moveToNext());
                                }
                                TextView txv=new TextView(getActivity());
                                txv.setText("刪除此分類("+sortName+")，也會一併刪除所有此分類底下所有的收支紀錄");txv.setTextSize(15);
                                TextView txvTitle=new TextView(getActivity());
                                txvTitle.setText("刪除提示");txvTitle.setTextSize(20);

                                new AlertDialog.Builder(getActivity())//雙重確認
                                        .setView(txv)
                                        .setCustomTitle(txvTitle)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                TextView txv=new TextView(getActivity());
                                                txv.setText("最終確認，確定要刪除分類("+sortName+")?");txv.setTextSize(15);
                                                TextView txvTitle=new TextView(getActivity());
                                                txvTitle.setText("刪除提示");txvTitle.setTextSize(20);

                                                new AlertDialog.Builder(getActivity())
                                                        .setView(txv)
                                                        .setCustomTitle(txvTitle)
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                String sqlCmd="SELECT accountID FROM mbr_memberaccount";//找出帳戶的數量
                                                                Cursor aCur = db.rawQuery(sqlCmd, null);
                                                                int rowsCount=aCur.getCount();
                                                                if(rowsCount!=0) {
                                                                    aCur.moveToFirst();
                                                                    for (int k = 0; k < rowsCount; k++) {
                                                                        String accountID = Integer.toString(aCur.getInt(0));
                                                                        sqlCmd = "SELECT amount FROM mbr_accounting WHERE sortID=\"" + MsortID + "\"" +
                                                                                "AND accountID=\"" + accountID + "\"";//找出此分類每一個帳戶的所有帳務的金額
                                                                        cur = db.rawQuery(sqlCmd, null);
                                                                        int rowsCount1 = cur.getCount();
                                                                        int totalSortAmount = 0;
                                                                        if (rowsCount1 != 0) {
                                                                            cur.moveToFirst();
                                                                            for (int j = 0; j < rowsCount1; j++) {
                                                                                totalSortAmount = totalSortAmount + cur.getInt(0);
                                                                                cur.moveToNext();
                                                                            }
                                                                        }
                                                                        sqlCmd = "SELECT balance FROM mbr_memberaccount WHERE _id=\"" + accountID + "\"";
                                                                        cur = db.rawQuery(sqlCmd, null);
                                                                        int rowsCount2 = cur.getCount();
                                                                        int oldBalance = 0;
                                                                        if (rowsCount2 != 0) {
                                                                            cur.moveToFirst();
                                                                            for (int j = 0; j < rowsCount2; j++) {
                                                                                oldBalance = cur.getInt(0);
                                                                                cur.moveToNext();
                                                                            }
                                                                        }
                                                                        String newBalance = Integer.toString(oldBalance + totalSortAmount);
                                                                        sqlCmd = "UPDATE mbr_memberaccount SET balance=\"" + newBalance + "\"" +
                                                                                "WHERE _id=\"" + accountID + "\"";
                                                                        db.execSQL(sqlCmd);
                                                                        aCur.moveToNext();
                                                                    }
                                                                }

                                                                db.delete("mbr_accounting", "sortID=" + MsortID, null);
                                                                sqlCmd = "SELECT subsortID FROM mbr_membersubsort WHERE memberSortID=\"" + MsortID + "\"";
                                                                cur = db.rawQuery(sqlCmd, null);
                                                                int rowsCount3 = cur.getCount();
                                                                int isubsortID = 0;
                                                                if (rowsCount3 != 0) {
                                                                    cur.moveToFirst();
                                                                    for (int j = 0; j < rowsCount3; j++) {
                                                                        isubsortID = cur.getInt(0);
                                                                        cur.moveToNext();
                                                                    }
                                                                }
                                                                String subsortID = String.valueOf(isubsortID);
                                                                db.delete("mbr_membersubsort", "memberSortID=" + MsortID, null);
                                                                db.delete("sys_subsort", "_id=" + subsortID, null);
                                                                sqlCmd = "SELECT sortID FROM mbr_membersort WHERE _id=\"" + MsortID + "\"";
                                                                cur = db.rawQuery(sqlCmd, null);
                                                                int rowsCount4 = cur.getCount();
                                                                int isortID = 0;
                                                                if (rowsCount4 != 0) {
                                                                    cur.moveToFirst();
                                                                    for (int j = 0; j < rowsCount4; j++) {
                                                                        isortID = cur.getInt(0);
                                                                        cur.moveToNext();
                                                                    }
                                                                }
                                                                String sortID = String.valueOf(isortID);
                                                                db.delete("mbr_membersort", "_id=" + MsortID, null);
                                                                db.delete("sys_sort", "_id=" + sortID, null);


                                                                Toast.makeText(getActivity().getApplicationContext(), "刪除成功", Toast.LENGTH_SHORT).show();

                                                                sortValue.clear();

                                                                adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost", "MsortID"},
                                                                        new int[]{R.id.name, R.id.budget, R.id.cost,R.id.MsortID});
                                                                Requery();
                                                                lv.setAdapter(adapter);
                                                                db.close();
                                                                //it = new Intent(getActivity(), NewMainActivity.class);
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
                popup.inflate(R.menu.menu_sort);
                popup.show();

            }
        });


        DH.close();
        return view;
    }

    private void Requery() {
        String sqlCmd ="SELECT name,IFNULL(B.budget,0) AS budget,amount AS cost,B._id AS MsortID" +
                "        FROM (SELECT * FROM sys_sort WHERE type=1) AS A" +
                "        INNER JOIN" +
                "                (SELECT _id,sortID,budget,amount FROM mbr_membersort WHERE memberID=1) AS B" +
                "        ON A._id=B.sortID";

        cur = db.rawQuery(sqlCmd, null);
        int rowsCount = cur.getCount();
        if (rowsCount != 0) {
            cur.moveToFirst();
            for (int i = 0; i < rowsCount; i++) {
                Map<String,String> row =new HashMap<String,String>();
                row.put("name",cur.getString(0));
                row.put("budget","預算:"+Integer.toString(cur.getInt(1)));
                row.put("cost","$"+Integer.toString(cur.getInt(2)));
                row.put("MsortID",cur.getString(3));
                sortValue.add(row);
                cur.moveToNext();
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void UpdateSortAmount(String start,String end){
        start = start.replace('/', '-');
        end = end.replace('/', '-');
        //UPDATE ... FROM is not support
        String sqlCmd="UPDATE mbr_membersort SET amount= " +
                "IFNULL((SELECT SUM(amount) AS amount FROM mbr_accounting WHERE memberID=1 " +
                "AND type=1 AND mbr_membersort.sortID=sortID " +
                "AND time BETWEEN '" + start + "' AND '" + end + "' GROUP BY sortID),0)";
        db.execSQL(sqlCmd);
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
}
