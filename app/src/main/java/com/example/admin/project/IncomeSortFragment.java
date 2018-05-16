package com.example.admin.project;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class IncomeSortFragment extends Fragment implements OnItemClickListener {

    private static final String TAG="IncomeSortFragment";
    private Button btnTab;
    static final String DB_NAME = "HotlineDB";
    static final String TB_NAME = "hotlist";
    static final int MAX=8;
    static String [] From = new String[]{"name","budget","cost"};
    SQLiteDatabase db;
    Cursor cur;
    SimpleCursorAdapter adapter;
    ListView lv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.incomesort_fragment,container,false);
        btnTab = (Button) view.findViewById(R.id.btnTab);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
            }
        });
        db = getActivity().openOrCreateDatabase(DB_NAME,android.content.Context.MODE_PRIVATE ,null);
        //建資料表
        String createTable = "CREATE TABLE IF NOT EXISTS "+
                TB_NAME+
                "(_id Integer Primary Key AUTOINCREMENT ," +
                "name VARCHER(32),"+
                "budget VARCHER(16),"+
                "cost VARCHER(64))";
        db.execSQL(createTable);
        cur = db.rawQuery("select * FROM " +TB_NAME,null);
        if(cur.getCount()==0){
            addData("進修學習","預算:200","$ 125");
            addData("食品酒水","預算:0","$ 120");
            addData("休閒娛樂","預算:0","$ 0");
            addData("人情往來","預算:0","$ 0");
            addData("其他雜項","預算:0","$ 0");
            addData("居家物業","預算:0","$ 0");
        }

        adapter = new SimpleCursorAdapter(getActivity(),R.layout.item,cur,From,
                new int[] {R.id.name,R.id.budget,R.id.cost},0);

        lv = (ListView) view.findViewById(R.id.revenue_lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        requery();
        return view;
    }
    private void requery(){

        cur = db.rawQuery("select * FROM " +TB_NAME,null);
        adapter.changeCursor(cur);
/*
        if(cur.getCount()  == MAX){
            btInsert.setEnabled(false);

        }else{
            btInsert.setEnabled(true);
        }
        btUpdate.setEnabled(false);
        btDelete.setEnabled(false);*/
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
    private void addData(String name,String budget,String cost){
        ContentValues cv = new ContentValues(3);
        cv.put("name", name);
        cv.put("budget", budget);
        cv.put("cost", cost);
        db.insert(TB_NAME,null,cv);
    }

}
