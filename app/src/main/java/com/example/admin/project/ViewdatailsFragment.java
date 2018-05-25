package com.example.admin.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ViewdatailsFragment extends Fragment {
    private static final String TAG="ViewdatailsFragment";
    private Button btnTab;
    private TextView txv_date;
    private String date_start, date_end;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewdetails_fragment,container,false);
        txv_date = (TextView) view.findViewById(R.id.textView33);//今日日期

        date_start = getActivity().getIntent().getExtras().getString("date_start");
        date_end = getActivity().getIntent().getExtras().getString("date_end");

        txv_date.setText(date_start +"~"+ date_end);

        return view;
    }
}
