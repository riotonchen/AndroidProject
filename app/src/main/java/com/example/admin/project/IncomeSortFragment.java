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
import android.widget.ImageView;
import android.widget.ListView;
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

public class IncomeSortFragment extends Fragment implements OnItemClickListener {

    private static final String TAG="IncomeSortFragment";
    private Button btnTab;
    private String monthstart, monthend;
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

        //讀取分類資料
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        UpdateSortAmount(monthstart,monthend);
        adapter = new SimpleAdapter(getActivity(), sortValue, R.layout.sort_item, new String[]{"name", "budget", "cost"},
                new int[]{R.id.name, R.id.budget, R.id.cost});
        Requery();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        DH.close();
        return view;
    }

    private void Requery() {
        String sqlCmd ="SELECT name,IFNULL(B.budget,0) AS budget,amount AS cost" +
                "        FROM (SELECT * FROM sys_sort WHERE type=1) AS A" +
                "        INNER JOIN" +
                "                (SELECT sortID,budget,amount FROM mbr_membersort WHERE memberID=1) AS B" +
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
}
