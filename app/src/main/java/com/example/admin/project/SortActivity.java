package com.example.admin.project;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SortActivity extends AppCompatActivity {

    private static final String TAG="SortActivity";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private ImageView imvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort);
        Log.d(TAG,"onCreate: Starting.");
        imvBack = (ImageView) findViewById(R.id.imvSortBack);
        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }
    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExpenseSortFragment(),"支出分類");
        adapter.addFragment(new IncomeSortFragment(),"收入分類");
        viewPager.setAdapter(adapter);
    }

    public void OnClick(View view){
        finish();
    }

}
