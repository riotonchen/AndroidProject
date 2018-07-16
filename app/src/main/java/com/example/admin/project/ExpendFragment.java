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
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class ExpendFragment extends Fragment {

    private static final String TAG = "ExpendFragment";
    private Button btnTab, scan_btn, btnDialog,btnDatePicker;
    private SQLiteDatabase db;
    private final String DB_NAME = "MYLOCALDB";
    private final String TABLE_NAME_ACCOUNTING="mbr_accounting";
    private TextView txvDate;
    private EditText money, number, remark;
    private Button photoshoot, photoshoot2;
    private ImageView imv;
    private Uri imgUri;
    private String today, scanresult;
    Calendar calendar=Calendar.getInstance(Locale.TAIWAN);
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    private String result;
    boolean ifvoice;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment, container, false);
        btnTab =  view.findViewById(R.id.btnTab);
        scan_btn =view.findViewById(R.id.scan_btn);
        btnDialog =  view.findViewById(R.id.btnDialog);
        imv = (ImageView) view.findViewById(R.id.imageView);
        photoshoot =  view.findViewById(R.id.photoshoot);
        photoshoot2 = view.findViewById(R.id.button12);
        money = view.findViewById(R.id.txtExpenseMoney);
        txvDate =  view.findViewById(R.id.txvDate);
        number =  view.findViewById(R.id.number);
        remark =  view.findViewById(R.id.remark);
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
        sqlCmd = "SELECT name FROM (SELECT * FROM sys_sort WHERE type=0) AS A " +
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
        //SubSort，預設帶食品主分類的子分類
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


        //接收首頁掃描資料
        scanresult = getActivity().getIntent().getExtras().getString("scanresult");
        if (scanresult != null) {
            number.setText(scanresult.substring(0, 10));
            txvDate.setText(Integer.parseInt(scanresult.substring(10, 13)) + 1911 + "/" + scanresult.substring(13, 15) + "/" + scanresult.substring(15, 17));
            String money_temp = scanresult.substring(30, 37);
            int money_int = Integer.parseInt(money_temp, 16);
            money.setText(String.valueOf(money_int));
        }


        //接收首頁語音資料-分類
        result = getActivity().getIntent().getExtras().getString("product");
        if (result != null) {
            remark.setText(result);
            String Name = result.toString();
            db = DH.getWritableDatabase();
            boolean isSort=false;

            //語音結果(分類)帶入分類
            sqlCmd ="SELECT * FROM sys_sort WHERE name=\"" + Name + "\"";
            int i=0;
            Cursor C=db.rawQuery(sqlCmd,null);
            if (C.getCount()>0) {
                isSort=true;
                C.moveToFirst();    // 移到第 1 筆資料
                do {        // 逐筆讀出資料(只會有一筆)
                    i =C.getInt(0);
                } while (C.moveToNext());    // 有一下筆就繼續迴圈
            }
            else
                i=1;
            spnSort.setSelection(i-1);

            //語音結果(子分類)帶入分類
            if(!isSort) {
                sqlCmd = "SELECT * FROM sys_sort WHERE _id IN (" +
                        "SELECT memberSortID FROM mbr_membersubsort WHERE subsortID IN(" +
                        "SELECT _id FROM sys_subsort WHERE name=\"" + Name + "\"))";

                String str = "";
                i = 0;
                C = db.rawQuery(sqlCmd, null);
                if (C.getCount() > 0) {
                    C.moveToFirst();    // 移到第 1 筆資料
                    do {        // 逐筆讀出資料(只會有一筆)
                        i = C.getInt(0);
                    } while (C.moveToNext());    // 有一下筆就繼續迴圈
                } else
                    i = 1;
                spnSort.setSelection(i - 1);
            }
            ///顯示子分類
            sortName = spnSort.getSelectedItem().toString();
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
            if(Name!=null) {
                int spinnerposition = adapter.getPosition(Name);
                spnSubSort.setSelection(spinnerposition);
            }

            //語音結果帶入子分類

            /*sqlCmd ="SELECT * FROM sys_subsort WHERE name=\"" + subsortName + "\"";
            C=db.rawQuery(sqlCmd,null);
            if(C.getCount()>0) {
                C.moveToFirst();    // 移到第 1 筆資料
                do {
                    i = C.getInt(0);
                } while (C.moveToNext());
                spnSubSort.setSelection(3);//子分類ID多達48個 無法使用i-1的方法
            }*/
            db.close();
        }

        //接收首頁語音資料-金額
        String amount = getActivity().getIntent().getExtras().getString("amount");
        if (amount != null) {
            money.setText(amount);
        }
        //接收首頁語音資料-備註
        String all=getActivity().getIntent().getExtras().getString("all");
        if(all!=null){
            remark.setText(all);
        }







        //儲存
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    DBHelper DH = new DBHelper(getActivity());
                    db = DH.getReadableDatabase();
                    ContentValues cv=new ContentValues();
                    //"memberID", "time","type", "sortID", "subsortID", "amount",
                    //                "accountID", "projectID", "invoiceNum", "picture", "comment"
                    cv.put("memberID","1");
                    cv.put("time",txvDate.getText().toString().replace('/','-'));
                    cv.put("type","0");//0為支出
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
                        db.close();
                        Toast.makeText(getActivity(), "新增失敗", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(), "新增成功", Toast.LENGTH_LONG).show();
                        UpdateAccountBalance();//更新會員帳戶剩餘金額
                        db.close();
                        Intent it=new Intent(getActivity(),MainActivity.class);
                        startActivity(it);
                    }
                }catch (Exception ex){
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

        //語音
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說商品與金額"); //語音辨識 Dialog 上要顯示的提示文字

                startActivityForResult(intent, 1);

            }
        });

        //掃描
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //IntentIntegrator integrator = new IntentIntegrator(getActivity());
                FragmentIntentIntegrator integrator = new FragmentIntentIntegrator(ExpendFragment.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("請掃描左邊QR-Code!!");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        //拍照底圖
        photoshoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                } else {
                    savePhoto();
                    photoshoot.setVisibility(View.INVISIBLE);
                    photoshoot2.setVisibility(View.INVISIBLE);

                }
            }
        });

        //拍照相機
        photoshoot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                } else {
                    savePhoto();
                    photoshoot.setVisibility(View.INVISIBLE);
                    photoshoot2.setVisibility(View.INVISIBLE);

                }
            }
        });

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

        //拍照
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                showImg();
            } else {
                Toast.makeText(getActivity(), "沒拍到照片", Toast.LENGTH_LONG).show();
            }
        }

        //語音
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //把所有辨識的可能結果印出來看一看，第一筆是最 match 的。
                ArrayList result2 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String all = "";
                String r = "";

                all = all + result2.get(0);
                /*for(int i = 0; i < result2.size(); i++){
                    all = all + result2.get(0) + "\n";
                }*/
                remark.setText(all, TextView.BufferType.EDITABLE);

                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(all);
                String a = m.replaceAll("").trim();
                if (a != "")
                    money.setText(m.replaceAll("").trim());
                //Toast.makeText(getActivity(), m.replaceAll("").trim(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), all,Toast.LENGTH_SHORT).show();
            }
        }

        //掃描
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            } else {
                String all = result.getContents();
                all = all.replace(" ", "");
                if (all.length() > 40) {
                    number.setText(all.substring(0, 10));
                    txvDate.setText(Integer.parseInt(all.substring(10, 13)) + 1911 + "/" + all.substring(13, 15) + "/" + all.substring(15, 17));
                    String money_temp = all.substring(30, 37);
                    int money_int = Integer.parseInt(money_temp, 16);
                    money.setText(String.valueOf(money_int));
                    String uni = all.substring(93, 94);
                    String buy = all.substring(95, all.length());
                    int flag = uni.compareTo("1");
                    if(flag == 0 ) {
                        //buy = buy.replaceAll("\\s+", "");
                        remark.setText(buy);
                    }

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void showImg() {
        int iw, ih, vw, vh;
        BitmapFactory.Options option = new BitmapFactory.Options();
        ContentResolver cr = getActivity().getContentResolver();
        option.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(
                    cr.openInputStream(imgUri), null, option);

        } catch (IOException e) {
            Toast.makeText(getActivity(), "讀取照片資訊時發生錯誤", Toast.LENGTH_LONG).show();
            return;
        }
        iw = option.outWidth;
        ih = option.outHeight;
        vw = imv.getWidth();
        vh = imv.getHeight();

        int scaleFactor = Math.min(iw / vw, ih / vh);
        option.inJustDecodeBounds = false;
        option.inSampleSize = scaleFactor;

        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(cr.openInputStream(imgUri), null, null);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "無法讀取照片", Toast.LENGTH_LONG).show();
        }
        imv.setImageBitmap(bmp);
    }


    public void savePhoto() {
        ContentResolver cr = getActivity().getContentResolver();
        imgUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(it, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            savePhoto();
        } else {
            Toast.makeText(getActivity(), "需要權限", Toast.LENGTH_SHORT).show();
        }

    }

    private void UpdateAccountBalance(){
        String sqlCmd="UPDATE mbr_memberaccount SET balance = balance- (SELECT amount FROM mbr_accounting WHERE memberID=1  " +
                "        AND type=0 ORDER BY _id DESC LIMIT 1) " +
                "  WHERE mbr_memberaccount.accountID=(SELECT accountID FROM mbr_accounting WHERE memberID=1  " +
                "        AND type=0 ORDER BY _id DESC LIMIT 1)";
        db.execSQL(sqlCmd);
    }
}
