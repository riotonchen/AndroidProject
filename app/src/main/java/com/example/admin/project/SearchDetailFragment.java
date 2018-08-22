package com.example.admin.project;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SearchDetailFragment extends Fragment {
    TextView txvDateStart,txvDateEnd;
    Button btnSearch,btnDateStart,btnDateEnd;
    String monthstart,monthend,accountID,condition;
    private int Tag;
    Intent it;
    Calendar calendar=Calendar.getInstance(Locale.TAIWAN);
    Calendar datetime=Calendar.getInstance(Locale.TAIWAN);
    private SimpleDateFormat yyyyMMdd  =  new SimpleDateFormat ("yyyy/MM/dd", Locale.TAIWAN);
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.searchdetails_fragment,container,false);

        btnSearch=view.findViewById(R.id.btnSearch);
        btnDateStart=view.findViewById(R.id.btnDateStart);
        btnDateEnd=view.findViewById(R.id.btnDateEnd);
        txvDateStart=view.findViewById(R.id.txvDateStart);
        txvDateEnd=view.findViewById(R.id.txvDateEnd);
        Tag = getActivity().getIntent().getExtras().getInt("Tag");
        accountID = getActivity().getIntent().getExtras().getString("accountID");
        condition=getActivity().getIntent().getExtras().getString("condition");

        datetime.set(Calendar.DAY_OF_MONTH,1);
        txvDateStart.setText(yyyyMMdd.format(datetime.getTime()));
        datetime.roll(Calendar.DAY_OF_MONTH,-1);
        txvDateEnd.setText(yyyyMMdd.format(datetime.getTime()));

        btnDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year,month,day);
                                txvDateStart.setText(yyyyMMdd.format(calendar.getTime()));
                            }
                        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year,month,day);
                                txvDateEnd.setText(yyyyMMdd.format(calendar.getTime()));
                            }
                        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getActivity(), ViewDetails.class);
                it.putExtra("date_start", txvDateStart.getText().toString());
                it.putExtra("date_end", txvDateEnd.getText().toString());
                it.putExtra("Tag",Tag);
                it.putExtra("accountID",accountID);
                it.putExtra("condition",condition);
                startActivity(it);
            }
        });


        return view;
    }

}
