package com.example.admin.project;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ExpendFragment extends Fragment {

    private static final String TAG="ExpendFragment";
    private Button btnTab;
    private SQLiteDatabase db;
    private final String DB_NAME = "MYLOCALDB";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment,container,false);
        btnTab = (Button) view.findViewById(R.id.btnTab);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
            }
        });
        Spinner spnSortClass=(Spinner)getActivity().findViewById(R.id.spinner);
       // db = SQLiteDatabase.openDatabase(DB_NAME, android.content.Context.MODE_PRIVATE);

        return view;
    }
}
