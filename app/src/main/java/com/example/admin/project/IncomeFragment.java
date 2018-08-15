package com.example.admin.project;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class IncomeFragment extends Fragment {
    private static final String TAG="IncomeFragment";
    private Button photoshoot,photoshoot2,btnTab,btnDatePicker;
    private ImageView imv;
    private Uri imgUri;
    //private String today;
    private SQLiteDatabase db;
    private final String DB_NAME = "MYLOCALDB";
    private final String TABLE_NAME_ACCOUNTING="mbr_accounting";
    private TextView txvDate;
    private EditText money, number, remark;
    Calendar calendar=Calendar.getInstance(Locale.TAIWAN);
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.income_fragment,container,false);
        imv =  view.findViewById(R.id.imageView);
        photoshoot =  view.findViewById(R.id.photoshoot);
        photoshoot2 = view.findViewById(R.id.button12);
        money = view.findViewById(R.id.txtIncomeMoney);
        txvDate =  view.findViewById(R.id.txvDate);
        number =  view.findViewById(R.id.number);
        remark =  view.findViewById(R.id.remark);
        //today = getActivity().getIntent().getExtras().getString("today");
        btnTab =  view.findViewById(R.id.btnTab);
        final Spinner spnSort = view.findViewById(R.id.spnSort);
        final Spinner spnSubSort = view.findViewById(R.id.spnSubSort);
        final Spinner spnAccount=view.findViewById(R.id.spnAccount);
        final Spinner spnProject=view.findViewById(R.id.spnProject);

        //設定txvDate預設日期
        txvDate.setText(yyyyMMdd.format(calendar.getTime()));
        //btnDatePicker
        btnDatePicker=view.findViewById(R.id.btnDatePicker);
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
        //Sort
        sqlCmd = "SELECT name FROM (SELECT * FROM sys_sort WHERE type=1) AS A " +
                "LEFT OUTER JOIN" +
                "(SELECT sortID FROM mbr_membersort WHERE memberID=1) AS B " +
                "ON A._id=B.sortID";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnSort.setAdapter(adapter);
        //分類選擇事件
        spnSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DBHelper DH = new DBHelper(getActivity());
                db = DH.getReadableDatabase();
                String sqlCmd = "SELECT name FROM sys_subsort AS A INNER JOIN " +
                        "   (SELECT subsortID FROM mbr_membersubsort WHERE memberSortID=" +
                        "       (SELECT _id FROM sys_sort WHERE name=\"" + parent.getSelectedItem().toString() + "\") " +
                        "       AND " +
                        "       memberID=1" +
                        "   ) AS B " +
                        "ON A._id=B.subsortID";
                ArrayList<String> arrayList = Query(sqlCmd);
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
                spnSubSort.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //SubSort
        String sortName = spnSort.getSelectedItem().toString();
        sqlCmd = "SELECT name FROM sys_subsort AS A INNER JOIN " +
                "   (SELECT subsortID FROM mbr_membersubsort WHERE memberSortID=" +
                "       (SELECT _id FROM sys_sort WHERE name=\"" + sortName + "\") " +
                "       AND " +
                "       memberID=1" +
                "   ) AS B " +
                "ON A._id=B.subsortID";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnSubSort.setAdapter(adapter);
        //spnAccount
        sqlCmd="SELECT name FROM sys_account WHERE _id IN " +
                "(SELECT accountID FROM mbr_memberaccount " +
                "WHERE memberID=1)";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnAccount.setAdapter(adapter);
        //spnProject
        sqlCmd="SELECT name FROM sys_project WHERE _id IN " +
                "(SELECT projectID FROM mbr_memberproject " +
                "WHERE memberID=1)";
        arrayList = Query(sqlCmd);
        adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        spnProject.setAdapter(adapter);
        db.close();

        //儲存
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    DBHelper DH = new DBHelper(getActivity());
                    db = DH.getReadableDatabase();
                    ContentValues cv=new ContentValues();
                    cv.put("memberID","1");
                    cv.put("time",txvDate.getText().toString().replace('/','-'));
                    cv.put("type","1");//1為支出
                    cv.put("sortID",Query("sys_sort",spnSort.getSelectedItem().toString()));//要先找出代號
                    String temp1=Query("sys_sort",spnSort.getSelectedItem().toString());
                    cv.put("subsortID",Query("sys_subsort",spnSubSort.getSelectedItem().toString()));
                    cv.put("amount",money.getText().toString());
                    cv.put("accountID",Query("sys_account",spnAccount.getSelectedItem().toString()));
                    cv.put("projectID",Query("sys_project",spnProject.getSelectedItem().toString()));
                    cv.put("invoiceNum",number.getText().toString());
                    //cv.put("picture","");
                    String value=remark.getText().toString();
                    if(value==""){
                        cv.putNull("comment");
                    }else
                        cv.put("comment",value);
                    long result=db.insert(TABLE_NAME_ACCOUNTING,null,cv);
                    if(result==-1){
                        Toast.makeText(getActivity(), "新增失敗", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getActivity(), "新增成功", Toast.LENGTH_LONG).show();
                        UpdateAccountBalance();//更新會員帳戶剩餘金額
                        db.close();
                        Intent it=new Intent(getActivity(),NewMainActivity.class);
                        startActivity(it);
                    }
                }catch (Exception ex){
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

        photoshoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
                }else{
                    savePhoto();
                    photoshoot.setVisibility(View.INVISIBLE);
                    photoshoot2.setVisibility(View.INVISIBLE);

                }
            }
        });
        photoshoot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
                }else{
                    savePhoto();
                    photoshoot.setVisibility(View.INVISIBLE);
                    photoshoot2.setVisibility(View.INVISIBLE);

                }
            }
        });
        /*imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
                }else{
                    savePhoto();

                }
            }
        });*/

        //date.setText(today);
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

    private String Query(String tableName,String str){
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode ==100){
            showImg();

        }else{
            Toast.makeText(getActivity(),"沒拍到照片",Toast.LENGTH_LONG).show();
        }
    }

    public void showImg(){
        int iw,ih,vw,vh;
        BitmapFactory.Options option = new BitmapFactory.Options();
        ContentResolver cr = getActivity().getContentResolver();
        option.inJustDecodeBounds = true;
        try{
            BitmapFactory.decodeStream(
                    cr.openInputStream(imgUri),null,option);

        }catch (IOException e){
            Toast.makeText(getActivity(),"讀取照片資訊時發生錯誤",Toast.LENGTH_LONG).show();
            return;
        }
        iw = option.outWidth;
        ih = option.outHeight;
        vw = imv.getWidth();
        vh = imv.getHeight();

        int scaleFactor = Math.min(iw/vw,ih/vh);
        option.inJustDecodeBounds = false;
        option.inSampleSize = scaleFactor;

        Bitmap bmp= null;
        try{
            bmp = BitmapFactory.decodeStream(cr.openInputStream(imgUri),null,null);
        }
        catch (IOException e){
            Toast.makeText(getActivity(),"無法讀取照片",Toast.LENGTH_LONG).show();
        }
        imv.setImageBitmap(bmp);
    }


    public void savePhoto(){
        ContentResolver cr = getActivity().getContentResolver();
        imgUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(it,100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 200){
            savePhoto();
        }
        else {
            Toast.makeText(getActivity(),"需要權限",Toast.LENGTH_SHORT).show();
        }

    }

    private void UpdateAccountBalance(){
        String sqlCmd="UPDATE mbr_memberaccount SET balance = balance + (SELECT amount FROM mbr_accounting WHERE memberID=1  " +
                "        AND type=1 ORDER BY _id DESC LIMIT 1) " +
                "  WHERE mbr_memberaccount.accountID=(SELECT accountID FROM mbr_accounting WHERE memberID=1  " +
                "        AND type=1 ORDER BY _id DESC LIMIT 1)";
        db.execSQL(sqlCmd);
    }
}
