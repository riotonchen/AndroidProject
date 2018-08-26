package com.example.admin.project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
    private Button btnAdd;
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
        btnAdd =view.findViewById(R.id.btnAdd);

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
                    adapter = new SimpleAdapter(getActivity(), projectValue, R.layout.project_item, new String[]{"name", "expense", "income","MprojectID"},
                            new int[]{R.id.name, R.id.expense, R.id.income, R.id.MprojectID});
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
                    adapter = new SimpleAdapter(getActivity(), projectValue, R.layout.project_item, new String[]{"name", "expense", "income","MprojectID"},
                            new int[]{R.id.name, R.id.expense, R.id.income ,R.id.MprojectID});
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
        adapter = new SimpleAdapter(getActivity(), projectValue, R.layout.project_item, new String[]{"name", "expense", "income", "MprojectID"},
                new int[]{R.id.name, R.id.expense, R.id.income, R.id.MprojectID});
        Query(monthstart,monthend);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DH=new DBHelper(getActivity());
                db=DH.getWritableDatabase();
                final String MprojectID=((TextView)view.findViewById(R.id.MprojectID)).getText().toString();
                final String project=((TextView)view.findViewById(R.id.name)).getText().toString();
                PopupMenu popup=new PopupMenu(getActivity(),view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.Search:
                                Intent it = new Intent(getActivity(), ViewDetails.class);
                                it.putExtra("date_start", monthstart);
                                it.putExtra("date_end", monthend);
                                it.putExtra("condition",project);
                                it.putExtra("Tag",2);
                                it.putExtra("MprojectID",MprojectID);
                                startActivity(it);
                                return true;
                            case R.id.Edit:
                                LayoutInflater inflater = LayoutInflater.from(getActivity());
                                View ExpenseSortView = inflater.inflate(R.layout.createproject, null);
                                final EditText edit_name=(EditText)ExpenseSortView.findViewById(R.id.edit_name);

                                String projectName="";
                                String sqlCmd="SELECT name FROM sys_project WHERE _id=\""+ MprojectID + "\"";
                                cur=db.rawQuery(sqlCmd,null);
                                if (cur.getCount() != 0) {
                                    cur.moveToFirst();
                                    do {        // 逐筆讀出資料(只會有一筆)
                                        projectName = cur.getString(0);
                                    } while (cur.moveToNext());
                                }
                                edit_name.setText(projectName);
                                new AlertDialog.Builder(getActivity())
                                        .setView(ExpenseSortView)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String newProjectName=edit_name.getText().toString();
                                                if (TextUtils.isEmpty(newProjectName)){
                                                    Toast.makeText(getActivity().getApplicationContext(), "請輸入專案名稱", Toast.LENGTH_SHORT).show();
                                                    db.close();
                                                }
                                                else {
                                                    String sqlCmd = "UPDATE sys_project SET name=\"" + newProjectName + "\""+
                                                            "WHERE _id=\"" + MprojectID + "\"";
                                                    db.execSQL(sqlCmd);


                                                    db.close();
                                                    Toast.makeText(getActivity().getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                                    Intent it = new Intent(getActivity(), NewMainActivity.class);
                                                    startActivity(it);
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
                                TextView txv=new TextView(getActivity());
                                txv.setText("刪除此專案，也會一併刪除所有此專案底下所有的收支紀錄");txv.setTextSize(15);
                                TextView txvTitle=new TextView(getActivity());
                                txvTitle.setText("刪除提示");txvTitle.setTextSize(20);

                                new AlertDialog.Builder(getActivity())//雙重確認
                                        .setView(txv)
                                        .setCustomTitle(txvTitle)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                TextView txv=new TextView(getActivity());
                                                txv.setText("最終確認，確定要刪除專案?");txv.setTextSize(15);
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
                                                                        sqlCmd = "SELECT amount FROM mbr_accounting WHERE projectID=\"" + MprojectID + "\"" +
                                                                                "AND accountID=\"" + accountID + "\"";//找出此專案每一個帳戶的所有帳務的金額
                                                                        cur = db.rawQuery(sqlCmd, null);
                                                                        int rowsCount1 = cur.getCount();
                                                                        int totalProjectAmount = 0;
                                                                        if (rowsCount1 != 0) {
                                                                            cur.moveToFirst();
                                                                            for (int j = 0; j < rowsCount1; j++) {
                                                                                totalProjectAmount = totalProjectAmount + cur.getInt(0);
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
                                                                        String newBalance = Integer.toString(oldBalance + totalProjectAmount);
                                                                        sqlCmd = "UPDATE mbr_memberaccount SET balance=\"" + newBalance + "\"" +
                                                                                "WHERE _id=\"" + accountID + "\"";
                                                                        db.execSQL(sqlCmd);
                                                                        aCur.moveToNext();
                                                                    }
                                                                }

                                                                db.delete("mbr_accounting", "projectID=" + MprojectID, null);


                                                                sqlCmd = "SELECT _id FROM mbr_memberproject WHERE projectID=\"" + MprojectID + "\"";
                                                                cur = db.rawQuery(sqlCmd, null);
                                                                int rowsCount4 = cur.getCount();
                                                                int iprojectID = 0;
                                                                if (rowsCount4 != 0) {
                                                                    cur.moveToFirst();
                                                                    for (int j = 0; j < rowsCount4; j++) {
                                                                        iprojectID = cur.getInt(0);
                                                                        cur.moveToNext();
                                                                    }
                                                                }
                                                                String projectID = String.valueOf(iprojectID);
                                                                db.delete("mbr_memberproject", "_id=" + projectID, null);
                                                                db.delete("sys_project", "_id=" + MprojectID, null);


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
                popup.inflate(R.menu.menu_project);
                popup.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DH = new DBHelper(getActivity());
                db = DH.getWritableDatabase();
                LayoutInflater inflater=LayoutInflater.from(getActivity());
                View ProjectView=inflater.inflate(R.layout.createproject,null);
                final EditText edit_name=(EditText)ProjectView.findViewById(R.id.edit_name);
                new AlertDialog.Builder(getActivity())
                        .setView(ProjectView)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                String name = edit_name.getText().toString();
                                if (TextUtils.isEmpty(name)) {
                                    Toast.makeText(getActivity().getApplicationContext(), "請輸入分類名稱", Toast.LENGTH_SHORT).show();
                                }else {
                                    String TB_NAME = "sys_project";
                                    String[] col = new String[]{"name"};
                                    String[] data = new String[]{name};
                                    AddData(TB_NAME, col, data);
                                    String sqlCmd = "SELECT _id FROM sys_project ORDER BY _id DESC LIMIT 1";//找出最新一筆新增的專案的ID
                                    cur = db.rawQuery(sqlCmd, null);
                                    int rowsCount = cur.getCount();
                                    int latestProjectID = 0;
                                    if (rowsCount != 0) {
                                        cur.moveToFirst();
                                        for (int i = 0; i < rowsCount; i++) {
                                            latestProjectID = (cur.getInt(0));
                                            cur.moveToNext();
                                        }
                                        cur.close();
                                    }
                                    final String newtProjectID = String.valueOf(latestProjectID);//目前要新增的專案的ID
                                    TB_NAME = "mbr_memberproject";
                                    col = new String[]{"memberID","projectID"};
                                    data = new String[]{"1",newtProjectID};
                                    AddData(TB_NAME, col, data);

                                    Toast.makeText(getActivity().getApplicationContext(), "新增成功", Toast.LENGTH_SHORT).show();
                                    db.close();
                                    Intent it = new Intent(getActivity(), NewMainActivity.class);
                                    startActivity(it);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });

        return view;
    }
    private void Query(String start,String end) {
        start = start.replace('/', '-');
        end = end.replace('/', '-');
        String sqlCmd = "SELECT i.name,IFNULL(expense,0) AS expense,IFNULL(income,0) AS income,C.projectID AS MprojectID FROM " +
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
                row.put("MprojectID",Integer.toString(cur.getInt(3)));
                projectValue.add(row);
                cur.moveToNext();
            }
        }
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
