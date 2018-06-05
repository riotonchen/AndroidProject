package com.example.admin.project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProjectFragment extends Fragment {
    private static final String TAG="ProjectFragment";
    private String monthstart, monthend;
    private TextView txtProject;
    private ImageView imvProjectArrowLeft,imvProjectArrowRight;
    Calendar calendar;
    private SimpleDateFormat yyyymmdd  =  new SimpleDateFormat ("yyyy-MM-dd", Locale.TAIWAN);
    Toast tos;
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    SimpleAdapter adapter;
    ListView lv;
    List<Map<String,String>> projectValue=new ArrayList<Map<String,String>>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_fragment,container,false);

        monthstart = getActivity().getIntent().getExtras().getString("monthstart");
        monthend = getActivity().getIntent().getExtras().getString("monthend");

        txtProject=view.findViewById(R.id.txtProject);
        imvProjectArrowLeft=view.findViewById(R.id.imvProjectArrowLeft);
        imvProjectArrowRight=view.findViewById(R.id.imvProjectArrowRight);
        txtProject.setText(monthstart+"~"+monthend);
        calendar=Calendar.getInstance(Locale.TAIWAN);
        lv = view.findViewById(R.id.project_lv);

        imvProjectArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    projectValue.clear();
                    Date date=yyyymmdd.parse(monthstart.replace('/','-'));
                    calendar.setTime(date);
                    calendar.add(calendar.MONTH,-1);
                    monthstart=yyyymmdd.format(calendar.getTime());
                    calendar.roll(Calendar.DAY_OF_MONTH,-1);
                    monthend = yyyymmdd.format(calendar.getTime());
                    txtProject.setText(monthstart.replace('-','/')+"~"+monthend.replace('-','/'));
                    adapter = new SimpleAdapter(getActivity(), projectValue, R.layout.project_item, new String[]{"name", "expense", "income"},
                            new int[]{R.id.name, R.id.expense, R.id.income});
                    Query(monthstart,monthend);
                    lv.setAdapter(adapter);

                }catch (Exception ex){
                    tos.setText("Error:"+ex.toString());
                }
            }
        });

        imvProjectArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    projectValue.clear();
                    Date date=yyyymmdd.parse(monthstart.replace('/','-'));
                    calendar.setTime(date);
                    calendar.add(calendar.MONTH,+1);
                    monthstart=yyyymmdd.format(calendar.getTime());
                    calendar.roll(Calendar.DAY_OF_MONTH,-1);
                    monthend = yyyymmdd.format(calendar.getTime());
                    txtProject.setText(monthstart.replace('-','/')+"~"+monthend.replace('-','/'));
                    adapter = new SimpleAdapter(getActivity(), projectValue, R.layout.project_item, new String[]{"name", "expense", "income"},
                            new int[]{R.id.name, R.id.expense, R.id.income});
                    Query(monthstart,monthend);
                    lv.setAdapter(adapter);
                }catch (Exception ex){
                    tos.setText("Error:"+ex.toString());
                }
            }
        });

        //讀取專案資料
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        adapter = new SimpleAdapter(getActivity(), projectValue, R.layout.project_item, new String[]{"name", "expense", "income"},
                new int[]{R.id.name, R.id.expense, R.id.income});
        Query(monthstart,monthend);
        lv.setAdapter(adapter);

        return view;
    }
    private void Query(String start,String end) {
        start = start.replace('/', '-');
        end = end.replace('/', '-');
        String sqlCmd = "SELECT i.name,IFNULL(expense,0) AS expense,IFNULL(income,0) AS income FROM " +
                "((SELECT projectID,name FROM (SELECT * FROM sys_project) AS A " +
                "      INNER JOIN " +
                "       (SELECT projectID FROM mbr_memberproject WHERE memberID=1) AS B " +
                "       ON A._id=B.projectID) AS C " +
                "   LEFT OUTER JOIN " +
                "  (SELECT projectID,SUM(amount) AS expense FROM mbr_accounting WHERE memberID=1 " +
                "AND type=0 AND time BETWEEN '" + start + "' AND '" + end + "' GROUP BY projectID) AS D " +
                "  ON C.projectID=D.projectID) AS I " +
                "INNER JOIN " +
                "((SELECT projectID,name FROM (SELECT * FROM sys_project) AS E " +
                "      INNER JOIN " +
                "       (SELECT projectID FROM mbr_memberproject WHERE memberID=1) AS F " +
                "       ON E._id=F.projectID) AS G " +
                "   LEFT OUTER JOIN " +
                "  (SELECT projectID,SUM(amount) AS income FROM mbr_accounting WHERE memberID=1 " +
                "AND type=1 AND time BETWEEN '" + start + "' AND '" + end + "' GROUP BY projectID) AS H " +
                "  ON G.projectID=H.projectID) AS J " +
                "ON I.projectID=J.projectID ";
        cur = db.rawQuery(sqlCmd, null);
        int rowsCount = cur.getCount();
        if (rowsCount != 0) {
            cur.moveToFirst();
            for (int i = 0; i < rowsCount; i++) {

                Map<String, String> row = new HashMap<String, String>();
                row.put("name", cur.getString(0));
                row.put("expense", "-$" + Integer.toString(cur.getInt(1)));
                row.put("income", "+$" + Integer.toString(cur.getInt(2)));
                projectValue.add(row);
                cur.moveToNext();
            }
        }
    }
}
