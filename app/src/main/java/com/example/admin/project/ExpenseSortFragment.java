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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
<<<<<<< HEAD
=======
import java.util.concurrent.ExecutionException;
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80

public class ExpenseSortFragment extends Fragment implements OnItemClickListener {

    private static final String TAG="ExpenseSortFragment";
<<<<<<< HEAD
    private Button btnTab,create;
<<<<<<< HEAD

=======
   // static final String DB_NAME = "HotlineDB";
=======
    private Button btnTab;
>>>>>>> DBCoding
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80
   // static final String TB_NAME = "hotlist";
    static final String DB_NAME = "MYLOCALDB";
    //static String [] From = new String[]{"name","budget","cost"};
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    //SimpleCursorAdapter adapter;
    SimpleAdapter adapter;
    ListView lv;
    List<Map<String,String>> sortValue=new ArrayList<Map<String,String>>();
<<<<<<< HEAD


=======
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.expensesort_fragment, container, false);
        btnTab = (Button) view.findViewById(R.id.btnTab);
        create = (Button) view.findViewById(R.id.create);
        final View item = LayoutInflater.from(getActivity()).inflate(R.layout.createsort, null);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1", Toast.LENGTH_SHORT).show();
            }
        });
<<<<<<< HEAD
		
=======
<<<<<<< HEAD
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80

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
                                if(TextUtils.isEmpty(name)){
                                    Toast.makeText(getActivity().getApplicationContext(), "123", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(),  name, Toast.LENGTH_SHORT).show();
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
<<<<<<< HEAD
       DH = new DBHelper(getActivity());
=======
       db = getActivity().openOrCreateDatabase(DB_NAME,android.content.Context.MODE_PRIVATE ,null);
    /*    //建資料表
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
            addData("進修學習","預算:200","$ 125");
            addData("食品酒水","預算:0","$ 120");
            addData("休閒娛樂","預算:0","$ 0");
            addData("人情往來","預算:0","$ 0");
            addData("其他雜項","預算:0","$ 0");
            addData("居家物業","預算:0","$ 0");
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
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80
        db = DH.getReadableDatabase();
        //db = getActivity().openOrCreateDatabase(DB_NAME, android.content.Context.MODE_PRIVATE, null);
        /*adapter = new SimpleCursorAdapter(getActivity(), R.layout.item, cur, new String[]{"name","budget","cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost}, 0);*/
<<<<<<< HEAD
        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost});
        Requery();
=======
        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.item, new String[]{"name", "budget", "cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost});
        Requery();
>>>>>>> DBCoding
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80
        lv = (ListView) view.findViewById(R.id.expenses_lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        //Requery();
        DH.close();
        return view;
    }
    private void Requery() {
        try{
            String sqlCmd = "SELECT name,IFNULL(B.budget,0) AS budget,0 AS cost" +
                    "        FROM (SELECT * FROM sys_Sort WHERE type=0) AS A" +
                    "        LEFT OUTER JOIN" +
                    "                (SELECT sortID,budget FROM mbr_MemberSort WHERE memberID=1) AS B" +
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
                    Map<String,String> row =new HashMap<String,String>();
                    row.put("name",cur.getString(0));
                    row.put("budget","預算:"+Integer.toString(cur.getInt(1)));
                    row.put("cost","$"+Integer.toString(cur.getInt(2)));
                    sortValue.add(row);
                    cur.moveToNext();
                }
            }
            //adapter.changeCursor(cur);
        }catch (Exception ex){

        }


    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
<<<<<<< HEAD
  
=======
<<<<<<< HEAD
   /* private void addData(String name,String budget,String cost){
        ContentValues cv = new ContentValues(3);
        cv.put("name", name);
        cv.put("budget", budget);
        cv.put("cost", cost);
        db.insert(TB_NAME,null,cv);
    }*/
>>>>>>> 4bbf247afd634dbb73be22c17a285423cc88ca80


=======
>>>>>>> DBCoding
}
