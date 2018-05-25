package com.example.admin.project;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class ExpendFragment extends Fragment {

    private static final String TAG="ExpendFragment";
    private Button btnTab,scan_btn,btnDialog;
    private SQLiteDatabase db;
    private final String DB_NAME = "MYLOCALDB";
    private EditText money,date,number,remark;
    private Button photoshoot,photoshoot2;
    private ImageView imv;
    private Uri imgUri;
    private String today,scanresult;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment,container,false);
        btnTab = (Button) view.findViewById(R.id.btnTab);
        scan_btn = (Button) view.findViewById(R.id.scan_btn);
        btnDialog = (Button) view.findViewById(R.id.btnDialog);
        imv = (ImageView) view.findViewById(R.id.imageView);
        photoshoot = (Button) view.findViewById(R.id.photoshoot);
        photoshoot2 = (Button) view.findViewById(R.id.button12);
        money = (EditText) view.findViewById(R.id.money);
        date = (EditText) view.findViewById(R.id.date);
        number = (EditText) view.findViewById(R.id.number);
        remark = (EditText) view.findViewById(R.id.remark);

        //設定日期
        today = getActivity().getIntent().getExtras().getString("today");
        date.setText(today);

        //接收首頁掃描資料
        scanresult = getActivity().getIntent().getExtras().getString("scanresult");
        if(scanresult != null){
            number.setText(scanresult.substring(0,10));
            date.setText(Integer.parseInt(scanresult.substring(10,13))+1911+"/"+scanresult.substring(13,15)+"/"+scanresult.substring(15,17));
            String money_temp = scanresult.substring(30,37);
            int money_int = Integer.parseInt(money_temp,16);
            money.setText(String.valueOf(money_int));
        }




        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
            }
        });


        //語音
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說話..."); //語音辨識 Dialog 上要顯示的提示文字

                startActivityForResult(intent, 1);

            }
        });
        Spinner spnSortClass=(Spinner)getActivity().findViewById(R.id.spinner);
       // db = SQLiteDatabase.openDatabase(DB_NAME, android.content.Context.MODE_PRIVATE);

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

        //拍照相機
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


        return view;


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //拍照
        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK ){
                showImg();
            }else{
                Toast.makeText(getActivity(),"沒拍到照片",Toast.LENGTH_LONG).show();
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

                String regEx="[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(all);
                String a = m.replaceAll("").trim();
                if( a != "" )
                    money.setText(m.replaceAll("").trim());
                //Toast.makeText(getActivity(), m.replaceAll("").trim(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), all,Toast.LENGTH_SHORT).show();
            }
        }

        //掃描
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null)
        {
            if (result.getContents()==null)
            {
                Toast.makeText(getActivity(), "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String all = result.getContents();
                number.setText(all.substring(0,10));
                date.setText(Integer.parseInt(all.substring(10,13))+1911+"/"+all.substring(13,15)+"/"+all.substring(15,17));
                String money_temp = all.substring(30,37);
                int money_int = Integer.parseInt(money_temp,16);
                money.setText(String.valueOf(money_int));
                //txv.setText(all+"\n"+number+"\n"+date+"\n"+money);
                //Toast.makeText(getActivity(),all+"\n"+number+"\n"+date+"\n"+money,Toast.LENGTH_SHORT).show();

            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
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




}
