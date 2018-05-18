package com.example.admin.project;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AccountingActivity extends AppCompatActivity {

    private static final String TAG="AccountingActivity";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounting);
        Log.d(TAG,"onCreate: Starting.");

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
}
