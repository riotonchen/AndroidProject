package com.example.admin.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class ExpenseSortFragment extends Fragment implements OnItemClickListener {

    private static final String TAG="ExpenseSortFragment";
    private Button btnProject,create;
    private TextView txvExpenseBudget,txvExpenseExpense,txvExpenseBalance;
    private String monthstart, monthend;
    private TextView txtExpenseSortDate;
    private ImageView imvExpenseSortArrowLeft,imvExpenseSortArrowRight;
    static final String DB_NAME = "MYLOCALDB";
    //static String [] From = new String[]{"name","budget","cost"};
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    //SimpleCursorAdapter adapter;
    SimpleAdapter adapter;
    ListView lv;
    List<Map<String,String>> sortValue=new ArrayList<Map<String,String>>();
    private Intent it;
    Calendar calendar;
    private SimpleDateFormat yyyymmdd  =  new SimpleDateFormat ("yyyy-MM-dd", Locale.TAIWAN);
    Toast tos;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expensesort_fragment, container, false);
        btnProject = view.findViewById(R.id.btnProject);
        create = view.findViewById(R.id.create);
        final View item = LayoutInflater.from(getActivity()).inflate(R.layout.createsort, null);
        monthstart = getActivity().getIntent().getExtras().getString("monthstart");
        monthend = getActivity().getIntent().getExtras().getString("monthend");
        txtExpenseSortDate=view.findViewById(R.id.txtExpenseSortDate);
        imvExpenseSortArrowLeft=view.findViewById(R.id.imvExpenseSortArrowLeft);
        imvExpenseSortArrowRight=view.findViewById(R.id.imvExpenseSortArrowRight);
        txtExpenseSortDate.setText(monthstart+"~"+monthend);
        calendar= Calendar.getInstance(Locale.TAIWAN);
        lv = view.findViewById(R.id.expenses_lv);
        txvExpenseBudget=view.findViewById(R.id.txtExpenseBudget);
        txvExpenseExpense=view.findViewById(R.id.txvExpenseExpense);
        txvExpenseBalance=view.findViewById(R.id.txvExpenseBalance);

        imvExpenseSortArrowLeft.setOnClickListener(new View.OnClickListener() {
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
                    txtExpenseSortDate.setText(monthstart.replace('-','/')+"~"+monthend.replace('-','/'));
                    UpdateSortAmount(monthstart,monthend);
                    adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost"},
                            new int[]{R.id.name, R.id.budget, R.id.cost});
                    Requery();
                    lv.setAdapter(adapter);
                    DH.close();
                }catch (Exception ex){
                    tos.setText("Error:"+ex.toString());
                }
            }
        });

        imvExpenseSortArrowRight.setOnClickListener(new View.OnClickListener() {
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
                    txtExpenseSortDate.setText(monthstart.replace('-','/')+"~"+monthend.replace('-','/'));
                    UpdateSortAmount(monthstart,monthend);
                    adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost"},
                            new int[]{R.id.name, R.id.budget, R.id.cost});
                    Requery();
                    lv.setAdapter(adapter);
                    DH.close();
                }catch (Exception ex){
                    tos.setText("Error:"+ex.toString());
                }
            }
        });

        //專案管理
        btnProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ProjectActivity.class);
                it.putExtra("monthstart", monthstart);
                it.putExtra("monthend", monthend);
                startActivity(it);
            }
        });



        //新增分類 第二次有閃退問題
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("新增分類")
                        .setView(item)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item.findViewById(R.id.edit_text);
                                String name = editText.getText().toString();
                                if (TextUtils.isEmpty(name)) {
                                    Toast.makeText(getActivity().getApplicationContext(), "123", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), name, Toast.LENGTH_SHORT).show();
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
        //之後改用Base Adapter
        //讀取分類資料
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        UpdateSortAmount(monthstart,monthend);
        //db = getActivity().openOrCreateDatabase(DB_NAME, android.content.Context.MODE_PRIVATE, null);
        /*adapter = new SimpleCursorAdapter(getActivity(), R.layout.item, cur, new String[]{"name","budget","cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost}, 0);*/
        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost});
        Requery();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        //讀取預算相關
        //txvExpenseBudget=view.findViewById(R.id.txtExpenseBudget);
        //txvExpenseExpense=view.findViewById(R.id.txvExpenseExpense);
        //txvExpenseBalance=view.findViewById(R.id.txvExpenseBalance);
        //txvExpenseBudget.setText(Query("SELECT budget FROM mbr_memberbudget WHERE month=6"));
        //....................notfinished

        DH.close();
        return view;
    }

    private void Requery() {
        String sqlCmd = "SELECT name,IFNULL(B.budget,0) AS budget,amount AS cost" +
                "        FROM (SELECT * FROM sys_sort WHERE type=0) AS A" +
                "        INNER JOIN" +
                "                (SELECT sortID,budget,amount FROM mbr_membersort WHERE memberID=1) AS B" +
                "        ON A._id=B.sortID";
        //String[][] strSort;
        cur = db.rawQuery(sqlCmd, null);
        int rowsCount = cur.getCount();
        //strSort = new String[rowsCount][4];
        String SingleExpenseBudget=""; String SingleExpenseBalance="";
        String TotalExpenseBudget=""; String TotalExpenseBalance=""; String TotalExpenseExpense="";
        int intTotalExpenseBudget=0; int intTotalExpenseBalance=0; int intTotalExpenseExpense=0;
        if (rowsCount != 0) {
            cur.moveToFirst();
            for (int i = 0; i < rowsCount; i++) {
                /*strSort[i][0] = Integer.toString(cur.getInt(0));
                strSort[i][1] = Integer.toString(cur.getInt(1));
                strSort[i][2] = Integer.toString(cur.getInt(2));
                strSort[i][3] = Integer.toString(cur.getInt(3));*/
                Map<String, String> row = new HashMap<String, String>();
                row.put("name", cur.getString(0));
                row.put("budget", "預算:" + Integer.toString(cur.getInt(1)));
                row.put("cost", "$" + Integer.toString(cur.getInt(2)));
                sortValue.add(row);

                SingleExpenseBudget=Integer.toString(cur.getInt(1));
                intTotalExpenseBudget+=Integer.parseInt(SingleExpenseBudget);
                //算出總預算
                SingleExpenseBalance=Integer.toString(cur.getInt(2));
                intTotalExpenseBalance+=Integer.parseInt(SingleExpenseBalance);
                //算出總支出
                intTotalExpenseExpense+=Integer.parseInt(SingleExpenseBudget);
                intTotalExpenseExpense-=Integer.parseInt(SingleExpenseBalance);
                //算出餘額

                cur.moveToNext();
            }
            TotalExpenseBudget=Integer.toString(intTotalExpenseBudget);
            TotalExpenseBalance=Integer.toString(intTotalExpenseBalance);
            TotalExpenseExpense=Integer.toString(intTotalExpenseExpense);
            txvExpenseBudget.setText(TotalExpenseBudget);
            txvExpenseBalance.setText(TotalExpenseBalance);
            txvExpenseExpense.setText(TotalExpenseExpense);
        }
        //adapter.changeCursor(cur);
    }

    private String Query(String sqlCmd) {
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
        return arrayList.get(0);
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
                "AND type=0 AND mbr_membersort.sortID=sortID " +
                "AND time BETWEEN '" + start + "' AND '" + end + "' GROUP BY sortID),0)";
        db.execSQL(sqlCmd);
    }

}
