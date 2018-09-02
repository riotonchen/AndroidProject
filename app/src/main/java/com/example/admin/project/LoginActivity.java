package com.example.admin.project;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private Button button15,button13,button14;
    private EditText editText3;
    boolean check = true;
    View view3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button15 = (Button) findViewById(R.id.button15);
        button13 = (Button) findViewById(R.id.button13);
        button14 = (Button) findViewById(R.id.button14);
        view3 = (View) findViewById(R.id.view3);
        editText3 = (EditText) findViewById(R.id.editText3);

    }
    public void onClick(View view){
        if(check == true){
            editText3.setVisibility(View.VISIBLE);
            button14.setVisibility(View.INVISIBLE);
            button13.setText("註冊");
            button15.setText("已經有帳號?");
            view3.setVisibility(View.INVISIBLE);
            check = false;
        }else{
            editText3.setVisibility(View.INVISIBLE);
            button14.setVisibility(View.VISIBLE);
            view3.setVisibility(View.VISIBLE);
            button13.setText("登入");
            button15.setText("還沒有帳號");
            check = true;
        }
    }





   
}
