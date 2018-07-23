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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class AccountingActivity extends AppCompatActivity {

    private static final String TAG="AccountingActivity";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private ImageView imvBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounting);
        Log.d(TAG,"onCreate: Starting.");
        //imv = (ImageView) findViewById(R.id.imageView);
        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container2);
        setupViewPager(mViewPager);

        imvBack = (ImageView) findViewById(R.id.imvAccountingBack);
        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        int transferId=getIntent().getIntExtra("transferId",0);
        if(transferId==1){//目前只會跳到支出畫面
            getSupportFragmentManager().beginTransaction().replace(R.id.container2,new TransferFragment()).commit();
        }

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


}
