package com.example.admin.project;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TransferFragment extends Fragment {
    private static final String TAG="TransferFragment";
    private Button btnTab;
    private String today;
    private EditText date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_fragment,container,false);
        btnTab = (Button) view.findViewById(R.id.btnTab);
        date = (EditText) view.findViewById(R.id.date);
        //設定日期
        today = getActivity().getIntent().getExtras().getString("today");
        date.setText(today);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 3",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
