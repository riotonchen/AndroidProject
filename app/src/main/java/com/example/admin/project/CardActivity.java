package com.example.admin.project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopeer.cardstack.CardStackView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CardActivity extends AppCompatActivity implements CardStackView.ItemExpendListener {

    private CardStackView cardStackView;
    private TestAdapter testStackAdapter;
    private SQLiteDatabase db;
    private Button btnCreate,btnModify,btnDelete,btncode;
    private EditText edit_code;
    private ArrayList<String> arrayList;
    private ArrayAdapter adapter;

    //private static Integer[] TEST_DATAS = new Integer[]{1,2,3,4,5,66};
    List<Integer> list = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        btnCreate=(Button)findViewById(R.id.button);
        btnDelete=(Button)findViewById(R.id.button13);


        DBHelper DH = new DBHelper(this);
        db = DH.getReadableDatabase();


        /*SharedPreferences myPref = getPreferences(MODE_PRIVATE);
        boolean isFirst = myPref.getBoolean("isFirst", true);//第一次找不到為true
        if (isFirst) {
            SetUpLocalDB();//建立SQLite資料庫及資料表
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putBoolean("isFirst", false);
            editor.commit();
        }*/


        cardStackView = (CardStackView) findViewById(R.id.stackview);
        cardStackView.setItemExpendListener(this);
        testStackAdapter = new TestAdapter(this);
        cardStackView.setAdapter(testStackAdapter);

        Cursor cur = db.rawQuery("SELECT card_id FROM mbr_card", null);
        int rowsCount = cur.getCount();
        if (rowsCount != 0) {
            cur.moveToFirst();
            for (int i = 0; i < rowsCount; i++) {
                list.add(cur.getInt(0));
                cur.moveToNext();
            }
        }
        /*for(int i=5;i<=10;i++) {
            list.add(i);
        }*/

        testStackAdapter.updateData(list);

        /*new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        testStackAdapter.updateData(list);
                    }
                }
                , 200
        );*/



        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper DH=new DBHelper(CardActivity.this);
                db=DH.getWritableDatabase();
                LayoutInflater inflater = LayoutInflater.from(CardActivity.this);
                final View CardView = inflater.inflate(R.layout.createcard, null);
                final EditText edit_name=(EditText)CardView.findViewById(R.id.edit_name);
                edit_code=(EditText)CardView.findViewById(R.id.edit_code);
                btncode=(Button)CardView.findViewById(R.id.btnCode);

                btncode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IntentIntegrator integrator = new IntentIntegrator(CardActivity.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        integrator.setPrompt("請掃描!!");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.setOrientationLocked(false);
                        integrator.initiateScan();
                    }
                });
                new AlertDialog.Builder(CardActivity.this)
                        .setView(CardView)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newCardName=edit_name.getText().toString();
                                String newCode=edit_code.getText().toString();
                                if (TextUtils.isEmpty(newCardName)){
                                    Toast.makeText(CardActivity.this.getApplicationContext(), "請輸入分類名稱", Toast.LENGTH_SHORT).show();
                                    db.close();
                                }
                                else {
                                    if(TextUtils.isEmpty(newCode)){
                                        Toast.makeText(CardActivity.this.getApplicationContext(), "請掃描條碼", Toast.LENGTH_SHORT).show();
                                        db.close();
                                    }
                                    else {
                                        String[] col;
                                        String[] data;
                                        String TB_NAME = "mbr_card";
                                        col = new String[]{"member_id","name","number"};
                                        data = new String[]{"0",edit_name.getText().toString(),edit_code.getText().toString()};
                                        ContentValues cv ;
                                        for (int i = 0; i < data.length; ) {
                                            cv = new ContentValues(col.length);
                                            for (int j = 0; j < col.length; j++) {
                                                cv.put(col[j], data[i++]);
                                            }
                                            db.insert(TB_NAME, null, cv);
                                        }


                                        db.close();
                                        Intent it = new Intent(CardActivity.this, NewMainActivity.class);
                                        Toast.makeText(CardActivity.this.getApplicationContext(), "新增成功", Toast.LENGTH_SHORT).show();
                                        startActivity(it);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(CardActivity.this.getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                                db.close();
                            }
                        })
                        .show();



                //Intent it = new Intent(CardActivity.this, Main2Activity.class);
                //startActivity(it);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper DH=new DBHelper(CardActivity.this);
                db=DH.getWritableDatabase();
                LayoutInflater inflater = LayoutInflater.from(CardActivity.this);
                final View CardView = inflater.inflate(R.layout.deletecard, null);
                final Spinner spnCard=(Spinner)CardView.findViewById(R.id.spinner);
                String sqlCmd="SELECT card_id,name FROM mbr_card WHERE member_id=0";
                arrayList = Query(sqlCmd);
                adapter = new ArrayAdapter(CardActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayList);
                spnCard.setAdapter(adapter);
                new AlertDialog.Builder(CardActivity.this)
                        .setView(CardView)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String dName=spnCard.getSelectedItem().toString();
                                db.delete("mbr_card","card_id="+dName.substring(0, dName.indexOf(".")),null);
                                Toast.makeText(CardActivity.this, String.valueOf("刪除成功"), Toast.LENGTH_SHORT).show();
                                db.close();
                                Intent it = new Intent(CardActivity.this, NewMainActivity.class);
                                startActivity(it);
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

        /*LayoutInflater inflater = LayoutInflater.from(CardActivity.this);
        final View CardView = inflater.inflate(R.layout.card_item, null);
        final TextView txvid=(TextView)CardView.findViewById(R.id.txvId);
        Button btnDelete=(Button)CardView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //db.delete("mbr_card","card_id="+txvid.getText().toString(),null);
                Toast.makeText(CardActivity.this, String.valueOf("刪除成功"), Toast.LENGTH_SHORT).show();
                Intent it = new Intent(CardActivity.this, NewMainActivity.class);
                startActivity(it);
            }
        });*/

    }



    @Override
    public void onItemExpend(boolean expend) {

    }

    public void AddData(String tableName, String[] columnName, String[] data) {
        //db = openOrCreateDatabase(DB_NAME, android.content.Context.MODE_PRIVATE, null);
        ContentValues cv ;
        for (int i = 0; i < data.length; ) {
            cv = new ContentValues(columnName.length);
            for (int j = 0; j < columnName.length; j++) {
                cv.put(columnName[j], data[i++]);
            }
            db.insert(tableName, null, cv);
        }
    }

    private void SetUpLocalDB() {
        DBHelper DH = new DBHelper(this);
        db = DH.getReadableDatabase();
        String TB_NAME;
        String[] col;
        String[] data;
        TB_NAME = "mbr_card";
        col = new String[]{"name"};
        data = new String[]{"FB帳號卡",
                "帳號卡",
                "本站帳號卡",
                "管理員卡"};
        AddData(TB_NAME, col, data);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,result.getContents() , Toast.LENGTH_SHORT).show();
                edit_code.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ArrayList<String> Query(String sqlCmd) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Cursor cur = db.rawQuery(sqlCmd, null);
            int rowsCount = cur.getCount();
            if (rowsCount != 0) {
                cur.moveToFirst();
                for (int i = 0; i < rowsCount; i++) {
                    arrayList.add(String.valueOf(cur.getInt(0))+". "+cur.getString(1));
                    cur.moveToNext();
                }
            }
            cur.close();
        } catch (Exception ex) {
            Toast.makeText(CardActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
        return arrayList;
    }

}
