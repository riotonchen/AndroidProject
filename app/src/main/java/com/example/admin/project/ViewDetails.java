package com.example.admin.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ViewDetails extends AppCompatActivity {

    private static final String TAG="ViewDetails";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private ImageView imvBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewdetails);
        Log.d(TAG,"onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container3);
        setupViewPager(mViewPager);

        imvBack = (ImageView) findViewById(R.id.imvViewDetailsBack);
        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ViewdatailsFragment(),"查看明細");
        viewPager.setAdapter(adapter);

    }

    public void OnClick(View view){
        finish();
    }
}
