package com.example.admin.project;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class AccountingActivity extends AppCompatActivity {

    private static final String TAG="AccountingActivity";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    ImageView imv;
    Uri imgUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounting);
        Log.d(TAG,"onCreate: Starting.");
        imv = (ImageView) findViewById(R.id.imageView);
        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }
    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExpendFragment(),"支出");
        adapter.addFragment(new IncomeFragment(),"收入");
        adapter.addFragment(new TransferFragment(),"轉帳");
        viewPager.setAdapter(adapter);
    }
    public void OnClick(View view){
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode ==100){
            showImg();

        }else{
            Toast.makeText(this,"沒拍到照片",Toast.LENGTH_LONG).show();
        }

    }

    public void showImg(){
        int iw,ih,vw,vh;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        try{
            BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(imgUri),null,option);

        }catch (IOException e){
            Toast.makeText(this,"讀取照片資訊時發生錯誤",Toast.LENGTH_LONG).show();
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
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri),null,null);

        }
        catch (IOException e){
            Toast.makeText(this,"無法讀取照片",Toast.LENGTH_LONG).show();
        }
        imv.setImageBitmap(bmp);
    }

    public void onGet(View v){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }else{
            savePhoto();

        }
    }
    public void savePhoto(){
        imgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
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
            Toast.makeText(this,"需要權限",Toast.LENGTH_SHORT).show();
        }

    }
}
