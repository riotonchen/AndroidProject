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
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeSortFragment extends Fragment implements OnItemClickListener {

    private static final String TAG="IncomeSortFragment";
    private Button btnTab;
    static final String DB_NAME = "MYLOCALDB";
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    SimpleAdapter adapter;
    ListView lv;
    List<Map<String,String>> sortValue=new ArrayList<Map<String,String>>();
<<<<<<< HEAD

=======
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80

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
<<<<<<< HEAD
		DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost});
        Requery();
=======
<<<<<<< HEAD
        db = getActivity().openOrCreateDatabase(DB_NAME,android.content.Context.MODE_PRIVATE ,null);
      /*  //建資料表
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
        }*/

        adapter = new SimpleCursorAdapter(getActivity(),R.layout.sort_item,cur,From,
                new int[] {R.id.name,R.id.budget,R.id.cost},0);

=======
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.item, new String[]{"name", "budget", "cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost});
        Requery();
>>>>>>> DBCoding
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80
        lv = (ListView) view.findViewById(R.id.revenue_lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        DH.close();
        return view;
    }
    private void Requery() {
        String sqlCmd ="SELECT name,IFNULL(B.budget,0) AS budget,0 AS cost" +
                "        FROM (SELECT * FROM sys_Sort WHERE type=1) AS A" +
                "        LEFT OUTER JOIN" +
                "                (SELECT sortID,budget FROM mbr_MemberSort WHERE memberID=1) AS B" +
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
                sortValue.add(row);
                cur.moveToNext();
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
<<<<<<< HEAD
   
=======
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80

}
