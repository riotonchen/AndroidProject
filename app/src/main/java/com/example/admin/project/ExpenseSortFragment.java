package com.example.admin.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseSortFragment extends Fragment implements OnItemClickListener {

    private static final String TAG="ExpenseSortFragment";
    private Button btnTab,create;
    private TextView txvExpenseBudget,txvExpenseExpense,txvExpenseBalance;
    private String monthstart, monthend;
    static final String DB_NAME = "MYLOCALDB";
    //static String [] From = new String[]{"name","budget","cost"};
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    //SimpleCursorAdapter adapter;
    SimpleAdapter adapter;
    ListView lv;
    List<Map<String,String>> sortValue=new ArrayList<Map<String,String>>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expensesort_fragment, container, false);
        btnTab = view.findViewById(R.id.btnTab);
        create = view.findViewById(R.id.create);
        final View item = LayoutInflater.from(getActivity()).inflate(R.layout.createsort, null);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1", Toast.LENGTH_SHORT).show();
            }
        });

        monthstart = getActivity().getIntent().getExtras().getString("monthstart");
        monthend = getActivity().getIntent().getExtras().getString("monthend");

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
        //讀取分類資料
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        //db = getActivity().openOrCreateDatabase(DB_NAME, android.content.Context.MODE_PRIVATE, null);
        /*adapter = new SimpleCursorAdapter(getActivity(), R.layout.item, cur, new String[]{"name","budget","cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost}, 0);*/
        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost});
        Requery();
        lv = view.findViewById(R.id.expenses_lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        //讀取預算相關
        txvExpenseBudget=view.findViewById(R.id.txtExpenseBudget);
        txvExpenseExpense=view.findViewById(R.id.txvExpenseExpense);
        txvExpenseBalance=view.findViewById(R.id.txvExpenseBalance);
        txvExpenseBudget.setText(Query("SELECT budget FROM mbr_memberbudget WHERE month=6"));
        //....................notfinished

        DH.close();
        return view;
    }

    private void Requery() {
        String sqlCmd = "SELECT name,IFNULL(B.budget,0) AS budget,0 AS cost" +
                "        FROM (SELECT * FROM sys_sort WHERE type=0) AS A" +
                "        INNER JOIN" +
                "                (SELECT sortID,budget FROM mbr_membersort WHERE memberID=1) AS B" +
                "        ON A._id=B.sortID";
        //String[][] strSort;
        cur = db.rawQuery(sqlCmd, null);
        int rowsCount = cur.getCount();
        //strSort = new String[rowsCount][4];
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
                cur.moveToNext();
            }
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
}
